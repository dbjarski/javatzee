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

import com.davidjarski.javatzee.IO.HighScoresIO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class HighScores
{
    public static final int HIGH_SCORE_COUNT = 10;
    
    private final ArrayList<GameRecord> list;
    private final Comparator comparator;
    private int size;
    
    
    public HighScores() {
        list = new ArrayList<>();
        loadHighScores();
        comparator = GameRecord.getComparator(GameRecord.SortOption.SCORE);
    }
    
    public GameRecord get(int index) {
        return list.get(index);
    }
    
    public int getSize() {
        return size;
    }
    
    public boolean addConditionally(GameRecord record) {
        if (size < HIGH_SCORE_COUNT) {
            saveHighScore(record);
            ++size;
            return true;
        } else if (comparator.compare(record, list.get(size - 1)) > 0) {
            list.remove(size - 1);
            saveHighScore(record);
            return true;
        }
        return false;
    }
    
    public void loadHighScores() {
        HighScoresIO.read(list);
        size = list.size();
    }
    
    void addTestRecord(GameRecord record) {
        list.add(record);
        ++size;
        Collections.sort(list, Collections.reverseOrder(comparator));
    }
    
    private void saveHighScore(GameRecord record) {
        list.add(record);
        Collections.sort(list, Collections.reverseOrder(comparator));
        HighScoresIO.write(list);
    }
        
}
