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
package com.davidjarski.javatzee.history;

import com.davidjarski.javatzee.scorepad.ScorePanel;
import java.util.Comparator;

public class GameRecord
{
    private static int idCounter = 0;
    
    public enum SortOption { SCORE, DATE_TIME }
    private final String name;
    private final long millis;
    private final int score;
    private final int kind5Count;
    private final int id;
    
    private int[] ints;
    
    public GameRecord(ScorePanel panel) {
        name = panel.getPlayerName();
        score = panel.getScore();
        kind5Count = panel.getKind5Count();
        millis = System.currentTimeMillis();
        ints = panel.getInts();
        id = idCounter++;

    }
    
    public GameRecord(String name, int score, int kind5Count, long millis) {
        this.name = name;
        this.score = score;
        this.kind5Count = kind5Count;
        this.millis = millis;
        id = idCounter++;
    }
    
    public GameRecord(String name, int score, int kind5Count, long millis,
            int[] ints) {
        this(name, score, kind5Count, millis);
        this.ints = ints;
    }

    public String getName() {
        return name;
    }

    public long getMillis() {
        return millis;
    }

    public int getScore() {
        return score;
    }

    public int getKind5Count() {
        return kind5Count;
    }
    
    public int getId() {
        return id;
    }
    
    public int[] getInts() {
        return ints;
    }
    
    public void setInts(int[] ints) {
        this.ints = ints;
    }
    
    @Override
    public String toString() {
        return String.format("%-20s %4d  %d %d [%d, %d, %d, %d, %d]",
                name, score, kind5Count, millis, ints[0], ints[1], ints[2], ints[3], ints[4]);
    }
    
    public static Comparator getComparator(SortOption option) {
        switch (option) {
        case SCORE:
            return new ScoreComparator();
        case DATE_TIME:
            return new DateTimeComparator();
        }
        return null;
    }
    
    private static class ScoreComparator implements Comparator<GameRecord> {

        @Override
        public int compare(GameRecord left, GameRecord right) {
            return left.score - right.score; 
        }
    }
    
    private static class DateTimeComparator implements Comparator<GameRecord> {

        @Override
        public int compare(GameRecord left, GameRecord right) {
            long comparison = left.millis - right.millis;
            return comparison > 0 ? 1 
                   : comparison < 0 ? -1 
                   : 0;
        }
    }
}
 