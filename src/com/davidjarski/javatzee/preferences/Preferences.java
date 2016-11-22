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
package com.davidjarski.javatzee.preferences;

import com.davidjarski.javatzee.IO.PreferencesIO;
import com.davidjarski.javatzee.dice.JAnimatedDie;
import com.davidjarski.javatzee.dice.JGraphicalDie;
import com.davidjarski.javatzee.dice.JGraphicalDie.DiceStyle;
import com.davidjarski.javatzee.dice.JGraphicalDie.LockStyle;

public class Preferences
{
    private static Preferences savedPreferences;

    private boolean coloredLock = false;
    private DiceStyle mainStyle = DiceStyle.getDefault();
    private DiceStyle highlightStyle = DiceStyle.getDefault();
    private LockStyle lockStyle = LockStyle.getDefault();
    private int delay = JAnimatedDie.DEFAULT_DELAY;
    private int numRolls = JAnimatedDie.DEFAULT_ROLLS;


    public static Preferences getCopy(Preferences original) {
        Preferences copy = new Preferences();
        copy.coloredLock = original.coloredLock;
        copy.delay = original.delay;
        copy.highlightStyle = original.highlightStyle;
        copy.lockStyle = original.lockStyle;
        copy.mainStyle = original.mainStyle;
        copy.numRolls = original.numRolls;
        return copy;
    }

    public static Preferences getDefaultPreferences() {
        return new Preferences();
    }

    public static Preferences getSavedPreferences() {
        if (savedPreferences == null) {
            savedPreferences = new Preferences();
            PreferencesIO.read(savedPreferences);
        }
        return savedPreferences;
    }

    public static void savePreferences(Preferences preferences) {
        savedPreferences = preferences;
        PreferencesIO.write(preferences);
    }

    private Preferences() {
    }

    public void load() {
        JGraphicalDie.setMainStyle(mainStyle);
        JGraphicalDie.setHighlightStyle(highlightStyle);
        JGraphicalDie.setLockStyle(lockStyle);
        JGraphicalDie.setColoredLock(coloredLock);
        JAnimatedDie.setNumberOfRolls(numRolls);
        JAnimatedDie.setDefaultDelay(delay);
    }

    public boolean isColoredLock() {
        return coloredLock;
    }

    public void setColoredLock(boolean coloredLock) {
        this.coloredLock = coloredLock;
    }

    public DiceStyle getMainStyle() {
        return mainStyle;
    }

    public void setMainStyle(DiceStyle mainStyle) {
        this.mainStyle = mainStyle;
    }

    public DiceStyle getHighlightStyle() {
        return highlightStyle;
    }

    public void setHighlightStyle(DiceStyle highlightStyle) {
        this.highlightStyle = highlightStyle;
    }

    public LockStyle getLockStyle() {
        return lockStyle;
    }

    public void setLockStyle(LockStyle lockStyle) {
        this.lockStyle = lockStyle;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getNumRolls() {
        return numRolls;
    }

    public void setNumRolls(int numRolls) {
        this.numRolls = numRolls;
    }

    @Override
    public String toString() {
        String result = "Dice style = " + mainStyle;
        result += "\nHighlight style = " + highlightStyle;
        result += "\nLock style = " + lockStyle;
        result += "\nColored lock = " + coloredLock;
        result += "\nAnimation delay = " + delay;
        result += "\nNumber of rolls = " + numRolls;
        return result;
    }
}
