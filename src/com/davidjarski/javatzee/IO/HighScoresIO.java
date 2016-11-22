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

import com.davidjarski.javatzee.IO.IOUtility.Block;
import static com.davidjarski.javatzee.IO.IOUtility.getUserFile;
import static com.davidjarski.javatzee.IO.IOUtility.putString;
import static com.davidjarski.javatzee.IO.IOUtility.getString;
import com.davidjarski.javatzee.history.GameRecord;
import com.davidjarski.javatzee.scorepad.ScorePanel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HighScoresIO
{
    public static final int CURRENT_VERSION = 1;
    

    public static void read(ArrayList<GameRecord> records) {
        records.clear();
        File file = getUserFile();
        try (FileInputStream fis = new FileInputStream(file)) {
            FileChannel channel = fis.getChannel();
            channel.position(Block.HIGH_SCORES.position);
            ByteBuffer buffer = ByteBuffer.allocate(Block.HIGH_SCORES.size);
            channel.read(buffer);
            buffer.rewind();

            if (buffer.getShort() != CURRENT_VERSION) {
                // add any update code here 
            }
            int count = buffer.get();
            for (int i = 0; i < count; ++i) {
                int[] ints = new int[ScorePanel.INT_COUNT];
                GameRecord record = new GameRecord(getString(buffer), buffer.getShort(),
                        buffer.get(), buffer.getLong());
                for (int j = 0; j < ints.length; ++j) {
                    ints[j] = buffer.getInt();
                }
                record.setInts(ints);
                records.add(record);
            }
        } catch (IOException ex) {
            Logger.getLogger(HighScoresIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void write(ArrayList<GameRecord> records) {
        File file = getUserFile();
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            FileChannel channel = raf.getChannel();
            channel.position(Block.HIGH_SCORES.position + 2);
            ByteBuffer buffer = ByteBuffer.allocate(Block.HIGH_SCORES.size);
            
            buffer.put((byte)records.size());
            for (GameRecord record : records) {
                putString(buffer, record.getName());
                buffer.putShort((short)record.getScore());
                buffer.put((byte)record.getKind5Count());
                buffer.putLong(record.getMillis());
                for (int i : record.getInts()) {
                    buffer.putInt(i);
                }
            }
            
            buffer.flip();
            channel.write(buffer);
            
        } catch (IOException ex) {
            Logger.getLogger(HighScoresIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
