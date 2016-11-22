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
package com.davidjarski.javatzee.scorepad;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

public class ScoreBox extends JLabel {
   
    public enum HandType {
        ONES, TWOS, THREES, FOURS, FIVES, SIXES,
        THREE_OF_A_KIND, FOUR_OF_A_KIND, FIVE_OF_A_KIND,
        FULL_HOUSE, SHORT_STRAIGHT, LONG_STRAIGHT, CHANCE
    }
    
    private int score;

    private final int column;
    private final boolean upper;
    private final HandType handType;
   
    public ScoreBox() {
        this(HandType.CHANCE, 0, true);
    }
    
    public ScoreBox(HandType handType, int column, boolean upper) {
        this.handType = handType;
        this.column = column;
        this.upper = upper;
        
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        setText(null);
        setBackground(Color.WHITE);
        setFont(getFont().deriveFont(Font.BOLD));
        setPreferredSize(new Dimension(40, 26));
        setMinimumSize(new Dimension(40, 26));
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    }
    
    public void setScore(int score) {
        this.score = score;
        setText(Integer.toString(score));
    }
    
    public int getScore() {
        return score;
    }
    
    public void eraseScore() {
        setText(null);
        score = 0;
    }
    
    public HandType getHandType() {
        return handType;
    }

    public boolean isUpper()
    {
        return upper;
    }

    public int getColumn()
    {
        return column;
    }
}
