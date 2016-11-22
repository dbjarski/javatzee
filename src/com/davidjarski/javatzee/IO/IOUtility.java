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

import com.davidjarski.javatzee.players.Players;
import com.davidjarski.javatzee.preferences.Preferences;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

class IOUtility
{
    public static final int CURRENT_VERSION = 1;
    public static final Charset CHARSET = Charset.forName("UTF-8");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    public enum Block {
        METADATA(0, 63),
        PREFERENCES(64, 319),
        PLAYERS(320, 1023),
        HIGH_SCORES(1024, 3071);
        
        public final int position;
        public final int size;
        Block(int lowerBound, int upperBound) {
            this.position = lowerBound;
            this.size = upperBound - lowerBound + 1;
        }
    }
    
    public static final byte TRUE = 1;
    public static final byte FALSE = 0;
    

    private static boolean userFileInitialized;
    private static boolean historyFileInitialized;
    private static boolean nameFileInitialized;
    private static File appDataPath;
    private static File userFile;
    private static File historyFile;
    private static File nameFile;
    
    public static final int putString(ByteBuffer buffer, String string) {
        byte[] bytes = string.getBytes(CHARSET);
        buffer.putShort((short)bytes.length);
        buffer.put(bytes);
        return bytes.length;
    }
    
    public static final String getString(ByteBuffer buffer) {
        int length = buffer.getShort();
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return new String(bytes, CHARSET);
    }
    
    public static final void putBoolean(ByteBuffer buffer, boolean bool) {
        buffer.put( (bool ? TRUE : FALSE) );
    }
    
    public static final boolean getBoolean(ByteBuffer buffer) {
        return buffer.get() != FALSE;
    }
    
    public static final File getUserFile() {
        if ( !userFileInitialized ) {
            userFileInitialized = true;
            
            File file = new File(getJavatzeeDataPath(), Resources.USER_FILENAME);
            if (!file.exists()) {
                createUserFile(file);
            }
            if (file.exists()) {                
                userFile = file;
            }
        } 
        
        return userFile;
    }
    
    private static File getJavatzeeDataPath() {
        if (appDataPath == null) {
            String userHome = System.getProperty("user.home") + File.separator;
            String appData = getAppDataLocation();
            String pathString = userHome + appData + Resources.APP_DIRECTORY_NAME
                    + File.separator;
            File path = new File(pathString);
            path.mkdirs();
            if (path.exists()) {
                appDataPath = path;
            }
        }
        return appDataPath;
    }
    
    private static String getAppDataLocation() {
        String appData = "";
            switch(System.getProperty("os.name")) {
            case "Windows 8":
            case "Windows 7":
            case "Windows Vista":
                appData = "AppData" + File.separator + "Local" + File.separator;
                break;
            case "Windows XP":
                appData = "Local Settings" + File.separator + "Application Data"
                        + File.separator;
                break;
            case "Linux":
                appData = ".config" + File.separator;
                break;
            case "Mac OS X":
                appData = "Library" + File.separator + "Application Support"
                        + File.separator;
                break;
            }
            return appData;
    }
    
    private static void createUserFile(File file) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            file.createNewFile(); 
            raf.writeShort((short)CURRENT_VERSION);
            for (Block block : Block.values()) {
                raf.writeShort((short)block.position);
            }
            raf.seek(Block.HIGH_SCORES.position);
            raf.writeShort((short)HighScoresIO.CURRENT_VERSION);
            raf.writeByte(0);
        } catch (IOException ex) {
            Logger.getLogger(IOUtility.class.getName()).log(Level.SEVERE, null, ex);
        }

        PreferencesIO.write(Preferences.getDefaultPreferences(), file);
        PlayersIO.write(new String[]{Players.DEFAULT_PLAYER_NAME}, file);
        
    }
    
    public static File getHistoryFile() {
        if ( !historyFileInitialized ) {
            historyFileInitialized = true;
            
            File file = new File(getJavatzeeDataPath(), Resources.HISTORY_FILENAME);
            if (!file.exists()) {
                createHistoryFile(file);
            }
            if (file.exists()) {                
                historyFile = file;
            }
        }
        return historyFile;
    }
    
    private static void createHistoryFile(File file) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            file.createNewFile(); 
            raf.writeShort((short)HistoryIO.CURRENT_VERSION);
            raf.writeInt(0);
        } catch (IOException ex) {
            Logger.getLogger(IOUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static File getNameFile() {
        if (!nameFileInitialized) {
            nameFileInitialized = true;
            
            File file = new File(getJavatzeeDataPath(), Resources.NAME_FILENAME);
            if (!file.exists()) {
                createNameFile(file);
            }
            if (file.exists()) {                
                nameFile = file;
            }
        }
        return nameFile;
    }

    private static void createNameFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(file), CHARSET))) {
            
            file.createNewFile(); 
            writer.write(HistoryIO.CURRENT_VERSION + LINE_SEPARATOR);
        } catch (IOException ex) {
            Logger.getLogger(IOUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
