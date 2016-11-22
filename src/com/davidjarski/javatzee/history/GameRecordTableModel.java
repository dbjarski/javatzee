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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import javax.swing.table.AbstractTableModel;

public class GameRecordTableModel extends AbstractTableModel
{
    public enum SortOption { DATE_TIME, TIME }
    
    public static final int RANK_COLUMN = 0;
    public static final int NAME_COLUMN = 1;
    public static final int SCORE_COLUMN = 2;
    public static final int KIND5_COLUMN = 3;
    public static final int DATE_COLUMN = 4;
    public static final int TIME_COLUMN = 5;
       
    private final ArrayList<GameRecord> records;
    private final HashMap<Integer, Integer> ranks;
    private final Comparator scoreComparator;
    protected final String[] columnNames = { "Rank", "Name", "Score", "Javatzees", "Date", "Time" };
    
    public GameRecordTableModel() {
        records = null;
        ranks = null;
        scoreComparator = null;
    }
    
    public GameRecordTableModel(ArrayList<GameRecord> records) {
        this.records = records;
        ranks = new HashMap<>();
        scoreComparator = GameRecord.getComparator(GameRecord.SortOption.SCORE);
        putRanks();
    }
    
    public void addGameRecords(ArrayList<GameRecord> newRecords) {
        for (GameRecord record : newRecords) {
            records.add(record);
        }
        putRanks();
        fireTableDataChanged();
    }
    
    public void putRanks() {
        Collections.sort(records, Collections.reverseOrder(scoreComparator));
        int rank = 0;
        for (GameRecord record : records) {
            ranks.put(record.getId(), ++rank);
        }
    }
    
    public GameRecord getGameRecordAt(int row) {
        if (row >= 0 && row < records.size()) {
            return records.get(row);
        }
        return null;
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
        return getValueAt(0, column).getClass();
    }
    
    @Override
    public int getRowCount() {
        return records.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex) {
        case RANK_COLUMN:
            return ranks.get(records.get(rowIndex).getId());
        case NAME_COLUMN:
            return records.get(rowIndex).getName();
        case SCORE_COLUMN:
            return records.get(rowIndex).getScore();
        case KIND5_COLUMN:
            return records.get(rowIndex).getKind5Count();
        case DATE_COLUMN:
        case TIME_COLUMN:
            return records.get(rowIndex).getMillis();
        } 
        return null;
    }
    
    public static Comparator getComparator(SortOption option) {
        switch (option) {
        case DATE_TIME:
            return new DateTimeComparator();
        case TIME:
            return new TimeComparator();
        }
        return null;
    }
    
    private static class DateTimeComparator implements Comparator<Long> {
        
        @Override
        public int compare(Long left, Long right) {
                        long comparison = left - right;
            return comparison > 0 ? 1 
                   : comparison < 0 ? -1 
                   : 0;
        }
    }
    
    private static class TimeComparator implements Comparator<Long> {
        private final GregorianCalendar leftCalendar = new GregorianCalendar();
        private final GregorianCalendar rightCalendar = new GregorianCalendar();
        
        @Override
        public int compare(Long left, Long right) {
            leftCalendar.setTimeInMillis(left);
            rightCalendar.setTimeInMillis(right);
            
            int comparison = leftCalendar.get(Calendar.HOUR_OF_DAY)
                    - rightCalendar.get(Calendar.HOUR_OF_DAY);
            if (comparison > 0) { return 1; }
            if (comparison < 0) { return -1; }
            
            // same hour, so we need to check minutes
            comparison = leftCalendar.get(Calendar.MINUTE)
                    - rightCalendar.get(Calendar.MINUTE);
            if (comparison > 0) { return 1; }
            if (comparison < 0) { return -1; }
            
            // same hour and minute, so we consider the two times to be equal
            return 0;
        }
    }
    
}
