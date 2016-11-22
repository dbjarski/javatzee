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

public class HighScoresTableModel extends GameRecordTableModel
{
   
    private final HighScores highScores;  
    
    public HighScoresTableModel(HighScores highScores) {
        this.highScores = highScores;
    }
    
    @Override
    public GameRecord getGameRecordAt(int row) {
        if (row >= 0 && row < highScores.getSize()) {
            return highScores.get(row);
        }
        return null;
    }
    
    @Override
    public int getRowCount() {
        return highScores.getSize();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex) {
        case RANK_COLUMN:
            return ++rowIndex;
        case NAME_COLUMN:
            return highScores.get(rowIndex).getName();
        case SCORE_COLUMN:
            return highScores.get(rowIndex).getScore();
        case KIND5_COLUMN:
            return highScores.get(rowIndex).getKind5Count();
        case DATE_COLUMN:
        case TIME_COLUMN:
            return highScores.get(rowIndex).getMillis();
        } 
        return null;
    }
    
    
    
}
