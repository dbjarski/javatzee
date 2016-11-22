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
package com.davidjarski.javatzee.dice;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.Timer;

public class JAnimatedDie extends JGraphicalDie
{
    public static final String PROP_ROLLING_DIE = "ROLLING_DIE";
    public static final int DEFAULT_DELAY = 90;
    public static final int DEFAULT_ROLLS = 5;
    
    private PropertyChangeSupport propertySupport;

    private static int numRolls;
    private static int delay = 90;
    private Timer timer;
    
    public JAnimatedDie()
    {
        propertySupport = new PropertyChangeSupport(this);
        timer = new Timer(delay, new ActionListener()
        {
            int rollCount;

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (++rollCount < numRolls) {
                    rollForAnimation();
                } else if (rollCount == numRolls) {
                    JAnimatedDie.super.roll();
                } else {
                    rollCount = 0;
                    setEnabled(true);
                    // let any listeners know that the die has stopped rolling
                    propertySupport.firePropertyChange(
                            PROP_ROLLING_DIE, Boolean.TRUE, Boolean.FALSE);
                    timer.stop();
                }
            }
        });
    }
    
    public void roll()
    {
        if (isEnabled() && !isLocked()) {
            if (numRolls > 1) {
                // let any listeners know that the die is now rolling
                propertySupport.firePropertyChange(
                        PROP_ROLLING_DIE, Boolean.FALSE, Boolean.TRUE);
                setEnabled(false);
                startAnimation();
            } else {
                super.roll();
            }
        }
    }
    
    public static int getNumberOfRolls() {
        return numRolls;
    }
    
    public static void setNumberOfRolls(int numRolls) {
        JAnimatedDie.numRolls = numRolls;
    }
    
    public int getDelay(int delay) {
        return timer.getDelay();
    }
    
    public static void setDefaultDelay(int delay) {
        JAnimatedDie.delay = delay;
    }

    public void setDelay(int delay)
    {
        timer.setDelay(delay);
    }

    private void startAnimation()
    {
        setEnabled(false);  // don't let user mess with die while it's rolling!
        timer.start();
    }
    
    private void rollForAnimation() {
        int differentValue = (int)(Math.random() * 5) + 1;
        if (differentValue == getValue()) {
            ++differentValue;
        }
        setValue(differentValue);
    }

    @Override
    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener)
    {
        propertySupport.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener)
    {
        propertySupport.removePropertyChangeListener(propertyName, listener);
    }
}
