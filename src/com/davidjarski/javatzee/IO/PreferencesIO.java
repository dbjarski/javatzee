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
import static com.davidjarski.javatzee.IO.IOUtility.getBoolean;
import static com.davidjarski.javatzee.IO.IOUtility.getString;
import static com.davidjarski.javatzee.IO.IOUtility.getUserFile;
import static com.davidjarski.javatzee.IO.IOUtility.putBoolean;
import static com.davidjarski.javatzee.IO.IOUtility.putString;
import com.davidjarski.javatzee.dice.JGraphicalDie;
import com.davidjarski.javatzee.preferences.Preferences;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PreferencesIO
{
    public static final int CURRENT_VERSION = 1;
    
    public static void write(Preferences prefs) {
        write(prefs, getUserFile());
    }
    
    public static void read(Preferences prefs) {
        File file = getUserFile();
        try (FileInputStream fis = new FileInputStream(file)) {
            FileChannel channel = fis.getChannel();
            channel.position(Block.PREFERENCES.position);
            ByteBuffer buffer = ByteBuffer.allocate(Block.PREFERENCES.size);
            channel.read(buffer);
            buffer.rewind();

            int fileVersion = buffer.getShort();
            if (fileVersion == CURRENT_VERSION) {
                prefs.setMainStyle(JGraphicalDie.DiceStyle.create(getString(buffer)));
                prefs.setHighlightStyle(JGraphicalDie.DiceStyle.create(getString(buffer)));
                prefs.setLockStyle(JGraphicalDie.LockStyle.create(getString(buffer)));
                prefs.setColoredLock(getBoolean(buffer));
                prefs.setDelay(buffer.getShort());
                prefs.setNumRolls(buffer.get());    
            } else {
                // add update code here when/if necessary
            }
            
        } catch (IOException ex) {
            Logger.getLogger(PreferencesIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    static void write(Preferences prefs, File file) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            FileChannel channel = raf.getChannel();
            channel.position(Block.PREFERENCES.position);
            ByteBuffer buffer = ByteBuffer.allocate(Block.PREFERENCES.size);
            
            buffer.putShort((short)CURRENT_VERSION);
            putString(buffer, prefs.getMainStyle().name());
            putString(buffer, prefs.getHighlightStyle().name());
            putString(buffer, prefs.getLockStyle().name());
            putBoolean(buffer, prefs.isColoredLock());
            buffer.putShort((short)prefs.getDelay());
            buffer.put((byte)prefs.getNumRolls());
            
            buffer.flip();
            channel.write(buffer);
            
        } catch (IOException ex) {
            Logger.getLogger(PreferencesIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
