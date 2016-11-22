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
package com.davidjarski.javatzee.hand;

import com.davidjarski.javatzee.dice.Die;
import com.davidjarski.javatzee.dice.StandardDie;

public class Hand
{

    public static final int ONES = 0;
    public static final int TWOS = 1;
    public static final int THREES = 2;
    public static final int FOURS = 3;
    public static final int FIVES = 4;
    public static final int SIXES = 5;

    private Die[] hand;
    private int[] counts;
    private boolean longStraight;
    private boolean shortStraight;
    private boolean fullHouse;
    private boolean threeOfAKind;
    private boolean fourOfAKind;
    private boolean fiveOfAKind;

    public Hand()
    {
        hand = new StandardDie[5];
        counts = new int[6];
        for (int i = 0; i < hand.length; ++i) {
            hand[i] = new StandardDie();
        }
    }

    public Hand(Die[] hand) throws IllegalArgumentException
    {
        if (hand.length != 5) {
            throw new IllegalArgumentException("hand must have exactly 5 elements.");
        }
        this.hand = hand;
        counts = new int[6];
    }

    public void roll()
    {

        // set all counts to 0
        for (int i = 0; i < counts.length; ++i) {
            counts[i] = 0;
        }
        // roll all the dice
        for (Die die : hand) {
            die.roll();
        }
        // populate counts array
        for (int i = 0; i < hand.length; ++i) {
            ++counts[hand[i].getValue() - 1];
        }
        setHandTypes();
    }
    
    public void handleRoll() {
        // set all counts to 0
        for (int i = 0; i < counts.length; ++i) {
            counts[i] = 0;
        }
        // populate counts array
        for (int i = 0; i < hand.length; ++i) {
            ++counts[hand[i].getValue() - 1];
        }
        setHandTypes();
    }

    private void setHandTypes()
    {
        int consecutive = 0; // keeps track of straights
        int maxConsecutive = 0; // the most numbers in a row
        boolean hasCountOfTwo = false; // used for testing for fullhouse

        clearHandTypes();
        for (int i = 0; i < counts.length; ++i) {
            if (counts[i] == 0) {
                consecutive = 0;
            } else {
                switch (counts[i]) {
                    case 2:
                        hasCountOfTwo = true;
                        break;
                    case 5:
                        fiveOfAKind = true;
                    case 4:
                        fourOfAKind = true;
                    case 3:
                        threeOfAKind = true;
                        break;
                }
                if (++consecutive > maxConsecutive) {
                    maxConsecutive = consecutive;
                }
            }
        }

        // check for straights
        if (maxConsecutive >= 4) {
            shortStraight = true;
            if (maxConsecutive == 5) {
                longStraight = true;
            }
        }

        // check for a full house
        if (fiveOfAKind || (threeOfAKind && hasCountOfTwo)) {
            fullHouse = true;
        }
    }

    private void clearHandTypes()
    {
        shortStraight = false;
        longStraight = false;
        threeOfAKind = false;
        fourOfAKind = false;
        fiveOfAKind = false;
        fullHouse = false;
    }

    public int getCount(int index)
    {
        return counts[index];
    }

    public int getSum()
    {
        int sum = 0;
        for (int i = 0; i < hand.length; ++i) {
            sum += hand[i].getValue();
        }
        return sum;
    }

    public boolean isLongStraight()
    {
        return longStraight;
    }

    public boolean isShortStraight()
    {
        return shortStraight;
    }

    public boolean isFullHouse()
    {
        return fullHouse;
    }

    public boolean isThreeOfAKind()
    {
        return threeOfAKind;
    }

    public boolean isFourOfAKind()
    {
        return fourOfAKind;
    }

    public boolean isFiveOfAKind()
    {
        return fiveOfAKind;
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < hand.length; ++i) {
            if (i != 0) {
                builder.append(',');
            }
            builder.append(hand[i].getValue());
        }
        builder.append("] (");

        for (int i = 0; i < counts.length; ++i) {
            if (i != 0) {
                builder.append(',');
            }
            builder.append(counts[i]);
        }
        builder.append(')');

        return builder.toString();
    }

    public static void main(String[] args)
    {
        Hand hand = new Hand();
        System.out.println(hand);
        System.out.println("Sum: " + hand.getSum());
        System.out.println("Short Straight: " + hand.shortStraight);
        System.out.println("Long Straight: " + hand.longStraight);
        System.out.println("Three of a Kind: " + hand.threeOfAKind);
        System.out.println("Four of a Kind: " + hand.fourOfAKind);
        System.out.println("Five of a Kind: " + hand.fiveOfAKind);
        System.out.println("Full House: " + hand.fullHouse);
    }
}
