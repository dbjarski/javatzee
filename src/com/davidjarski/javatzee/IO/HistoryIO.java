/* 
 * The MIT License
 *
 * Copyright 2014 David Jarski.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.davidjarski.javatzee.IO;

import static com.davidjarski.javatzee.IO.IOUtility.CHARSET;
import static com.davidjarski.javatzee.IO.IOUtility.LINE_SEPARATOR;
import static com.davidjarski.javatzee.IO.IOUtility.getHistoryFile;
import static com.davidjarski.javatzee.IO.IOUtility.getNameFile;
import com.davidjarski.javatzee.history.GameRecord;
import com.davidjarski.javatzee.players.Players;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HistoryIO
{
    public static final int CURRENT_VERSION = 1;
    
    private static final int COUNT_POSITION = 2;
    private static final int BUFFER_SIZE = 1024;
    private static final int PACKED_INT_COUNT = 5;
    /**
     * The number of bytes needed to store a single record. Used to determine
     * when to compact the ByteBuffer and read in more bytes.
     */
    private static final int RECORD_BYTE_COUNT = 33;
        
    private static HashMap<String, Integer> nameMap;
    private static HashMap<Integer, String> indexMap;
    private static int nextId;
    private static boolean initialized;
    private static int historyCount;
    
    public static ArrayList<GameRecord> read() {
        if (!initialized) {
            initialize();
        }
        ArrayList<GameRecord> records = null;
        try (FileInputStream fis = new FileInputStream(getHistoryFile())) {
            FileChannel channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            buffer.limit(6);
            channel.read(buffer);
            buffer.rewind();
            if (buffer.getShort() != CURRENT_VERSION) {
                // handle any update code here
            }
            int count = buffer.getInt();
            records = new ArrayList<>(count);
            buffer.clear();
            channel.read(buffer);
            buffer.rewind();
            GameRecord record;
            for (int i = 0; i < count; ++i) {
                record = new GameRecord(
                    indexMap.get((int)buffer.getShort()),   // name
                    buffer.getShort(),                      // score
                    buffer.get(),                           // fiveOfAKindCount
                    buffer.getLong());                     // millis
                int[] ints = new int[PACKED_INT_COUNT];
                for (int j = 0; j < PACKED_INT_COUNT; ++j) {
                    ints[j] = buffer.getInt();
                }
                record.setInts(ints);
                if (buffer.remaining() < RECORD_BYTE_COUNT) {
                    buffer.compact();
                    channel.read(buffer);
                    buffer.rewind();
                }
                records.add(record);
            }
        } catch (IOException ex) {
            Logger.getLogger(HistoryIO.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return records;
    }
    
    public static void write(ArrayList<GameRecord> records) {
        if (!initialized) {
            initialize();
        }
        ArrayList<String> newNames = new ArrayList<>();
        int[] ids = new int[records.size()];
        int index = 0;
        for (GameRecord record : records) {
            Integer id = nameMap.get(record.getName());
            if (id == null) {
                ids[index++] = nextId;
                String newName = record.getName();
                nameMap.put(newName, nextId);
                indexMap.put(nextId, newName);
                newNames.add(record.getName());
                nextId++;
            } else {
                ids[index++] = id;
            }
        }
        if (!newNames.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(getNameFile(), true), CHARSET))) {
                for (String newName : newNames) {
                    writer.write(newName + LINE_SEPARATOR);
                }
            } catch (IOException ex) {
                Logger.getLogger(HistoryIO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try (RandomAccessFile raf = new RandomAccessFile(getHistoryFile(), "rw")) {
            FileChannel channel = raf.getChannel();
            channel.position(raf.length());
            ByteBuffer buffer = ByteBuffer.allocate(RECORD_BYTE_COUNT * Players.MAX_PLAYER_COUNT);
            index = 0;
            
            for (GameRecord record : records) {
                buffer.putShort((short)ids[index++]);
                buffer.putShort((short)record.getScore());
                buffer.put((byte)record.getKind5Count());
                buffer.putLong(record.getMillis());
                int[] ints = record.getInts();
                for (int i : ints) {
                    buffer.putInt(i);
                }
            }
            
            buffer.flip();
            channel.write(buffer);
            
            // write the count to the file
            raf.seek(COUNT_POSITION);
            raf.writeInt( (historyCount += records.size()) );
        } catch (IOException ex) {
            Logger.getLogger(HistoryIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private static void initialize() {
        initialized = true;
        nameMap = new HashMap<>();
        indexMap = new HashMap<>();

        File nameFile = getNameFile();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(nameFile), CHARSET) )) {
            
            if (Integer.parseInt(reader.readLine()) != CURRENT_VERSION) {
                // handle any updates here
            }
            
            String name;
            while ( (name = reader.readLine()) != null ) {
                nameMap.put(name, nextId);
                indexMap.put(nextId++, name);
            }

        } catch (IOException | NumberFormatException ex) {
            Logger.getLogger(HistoryIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try (DataInputStream dis = new DataInputStream(
                new FileInputStream(getHistoryFile()))) {
            if (dis.readShort() != CURRENT_VERSION) {
                // handle any updates here
            }
            historyCount = dis.readInt();
        } catch (IOException ex) {
            Logger.getLogger(HistoryIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args) {
        ArrayList<GameRecord> records = read();
        for (GameRecord record : records) {
            System.out.println(record);
        }
    }
}
