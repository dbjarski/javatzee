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
import static com.davidjarski.javatzee.IO.IOUtility.getString;
import static com.davidjarski.javatzee.IO.IOUtility.getUserFile;
import static com.davidjarski.javatzee.IO.IOUtility.putString;
import com.davidjarski.javatzee.players.Players;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayersIO
{
    public static final int CURRENT_VERSION = 1;
    
    /* this class simply calls the overloaded version (avoids code duplication
       in the Utility.createUserFile() method, which stores default values).
    */
    public static void write(String[] names) {
        write(names, getUserFile());
    }
    
    public static String[] read() {
        File file = getUserFile();
        String[] names = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            FileChannel channel = fis.getChannel();
            channel.position(Block.PLAYERS.position);
            ByteBuffer buffer = ByteBuffer.allocate(Block.PLAYERS.size);
            channel.read(buffer);
            buffer.rewind();

            int fileVersion = buffer.getShort();
            if (fileVersion == CURRENT_VERSION) {
                names = new String[buffer.get()];
                for (int i = 0; i < names.length; ++i) {
                    names[i] = getString(buffer);
                }
            } else {
                // add update code here when/if necessary
            }
            
        } catch (IOException ex) {
            Logger.getLogger(PlayersIO.class.getName()).log(Level.SEVERE, null, ex);
            names = new String[]{Players.DEFAULT_PLAYER_NAME};
        } 
        return names;
    }
    
    static void write(String[] names, File file) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            FileChannel channel = raf.getChannel();
            channel.position(Block.PLAYERS.position);
            ByteBuffer buffer = ByteBuffer.allocate(Block.PLAYERS.size);
            
            buffer.putShort((short)CURRENT_VERSION);
            buffer.put((byte)names.length);
            for (String name : names) {
                putString(buffer, name);
            }
            
            buffer.flip();
            channel.write(buffer);
            
        } catch (IOException ex) {
            Logger.getLogger(PlayersIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
