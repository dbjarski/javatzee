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
package com.davidjarski.javatzee.players;

import com.davidjarski.javatzee.IO.PlayersIO;

public class Players
{
    /* Any changes to MAX_PLAYER_COUNT or MAX_PLAYER_NAME_CHARACTERS will require
     corresponding changes to the storage structure defined in IO.IOUtility 
     and the read() and write() methods of IO.PlayersIO
     */

    public static final int MAX_PLAYER_COUNT = 4;
    public static final int MAX_CHARACTERS = 32;
    public static final String DEFAULT_PLAYER_NAME = "Player 1";

    // the list of player names that were saved to disk
    private static Players savedPlayers;
    // the current list of player names
    private static Players players;

    private String[] names;

    public static Players getCopy(Players original) {
        Players copy = new Players();
        copy.names = new String[original.names.length];
        for (int i = 0; i < copy.names.length; ++i) {
            copy.names[i] = original.names[i];
        }
        return copy;
    }

    public static Players getDefaultPlayers() {
        Players players = new Players();
        players.names = new String[]{DEFAULT_PLAYER_NAME};
        return players;
    }

    public static Players getPlayers() {
        return players;
    }

    public static Players loadSavedPlayers() {
        if (savedPlayers == null) {
            savedPlayers = new Players();
            savedPlayers.names = PlayersIO.read();
        }
        players = savedPlayers;
        return players;
    }

    public static void savePlayers(Players players) {
        savedPlayers = getCopy(players);
        PlayersIO.write(players.names);
    }

    private Players() { }

    public String getName(int index) {
        return names[index];
    }
    
    public int getNumberOfPlayers() {
        return names.length;
    }

    public boolean set(String[] names) {
        if (names.length <= MAX_PLAYER_COUNT
                && getLongestLength(names) <= MAX_CHARACTERS) {
            // passed validation, now make sure these names are different
            // from the names we already have
            if (names.length == this.names.length) {
                for (int i = 0; i < names.length; ++i) {
                    if (!names[i].equals(this.names.length)) {
                        this.names = names;
                        return true;  // at least one new name
                    }
                }
            } else {
                // different length array, so definitely a change in names
                this.names = names;
                return true;
            }
                   
        }
        // the new names failed to validate or were the same as current names
        return false;
    }
    
    public int getLongestLength() {
        return getLongestLength(names);
    }
    
    private int getLongestLength(String[] strings) {
        int max = 0;
        int current;
        for (String s : strings) {
            current = s.length();
            if (current > max) {
                max = current;
            }
        }
        return max;
    }
}
