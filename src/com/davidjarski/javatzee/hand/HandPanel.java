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
import com.davidjarski.javatzee.dice.JAnimatedDie;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JPanel;

public class HandPanel extends JPanel implements PropertyChangeListener
{
    public static final String PROP_NEW_ROLL = "NEW_ROLL";
    public static final String PROP_HAND_ROLLING = "HAND_ROLLING";
    
    private JAnimatedDie[] dice;
    private final Hand hand;
    private int rollingDiceCount;
    private int rollCount = Integer.MIN_VALUE;
    private boolean disabledDice;
    private boolean rolling;
    private final PropertyChangeSupport propertySupport;
    private final Dimension size;
    private final boolean[] lockedDice;
    private int lastRollCount;
    private boolean newGame;
    private boolean gameOver;
    
    private int animationDelay = 90;
    
    private static final int MAX_ROLL_COUNT = 3;
    private static final int DISABLED = Integer.MIN_VALUE;


    /**
     * Creates new form RollPanel
     */
    public HandPanel()
    {
        initComponents();
        propertySupport = new PropertyChangeSupport(this);
        lockedDice = new boolean[5];
        rollButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                if (rollingDiceCount == 0 && rollCount < MAX_ROLL_COUNT) {

                    /* Make sure that at least one die is unlocked. Otherwise
                     * we could increment the roll count even though no dice
                     * were rolled.
                     */
                    boolean unlockedDieExists = false;
                    for (JAnimatedDie die : dice) {
                        if (!die.isLocked()) {
                            unlockedDieExists = true;
                            break;
                        }
                    }

                    if (unlockedDieExists) {
                        if (disabledDice) {
                            enableDice();
                        }
                        incrementRollCount();
                        rollDice();
                    }
                }
            }
        });
        dice = new JAnimatedDie[]{jAnimatedDie1, jAnimatedDie2, jAnimatedDie3,
            jAnimatedDie4, jAnimatedDie5};
        hand = new Hand(dice);
        for (JAnimatedDie die : dice) {
            die.addPropertyChangeListener(JAnimatedDie.PROP_ROLLING_DIE, this);
        }
        updatePanelState();
        rollButton.setText("Begin Game");
        Dimension min = rollButton.getMinimumSize();
        size = new Dimension(min.width, min.height + 10);
        rollButton.setPreferredSize(size);
        rollButton.setText("Test Dice");
    }
    
    public HandPanel(boolean testing) {
        this();
        dice[0].setLocked(true);
    }

    public Hand getHand()
    {
        return hand;
    }
    
    public boolean isRollInProgress() {
        return rollCount > 0;
    }
    
    public void handleGameOver() {
        rollCount = DISABLED;
        gameOver = true;
        updatePanelState();
    }

    public void handleNewTurn()
    {
        rollCount = 0;
        updatePanelState();
        rollButton.setEnabled(true);
    }
    
    public void handleUndo() {
        for (int i = 0; i < lockedDice.length; ++i) {
            if (lockedDice[i]) {
                dice[i].setLocked(true);
            }
        }
        enableDice();
        rollCount = lastRollCount;
        updatePanelState();
    }
    
    public void rollDice() {
        for (int i = 0; i < dice.length; ++i) {
            lockedDice[i] = dice[i].isLocked();
        }
        for (Die die : dice) {
            die.roll();
        }
        /* handle the roll here if we aren't animating the roll. otherwise the
           the roll is handled by the property change listener */
        if (JAnimatedDie.getNumberOfRolls() == 1) {
            hand.handleRoll();
            updatePanelState();
        }
    }
    
    public void testRoll() {
        for (Die die : dice) {
            die.roll();
        }
    }
    
    public void reset() {
        rollCount = DISABLED;
        newGame = true;
        updatePanelState();
    }

    private void updatePanelState()
    {
        switch (rollCount) {
        case 0:
            rollButton.setText("New Roll");
            for (JAnimatedDie die : dice) {
                die.setLocked(false);
            }
            disableDice();
            rollButton.setEnabled(true);
            break;
        case 1:
            rollButton.setText("Roll Two");
            break;
        case (MAX_ROLL_COUNT - 1):
            rollButton.setText("Last Roll");
            break;
        case MAX_ROLL_COUNT:
            rollButton.setText("Score Roll");
            disableDice();
            rollButton.setEnabled(false);
            break;
        case DISABLED:
            if (newGame) {
                rollButton.setText("Begin Game");
                rollCount = 0;
                disableDice();
                for (JAnimatedDie die : dice) {
                    die.setLocked(false);
                }
                rollButton.setEnabled(true);
                newGame = false;
            } else if (gameOver) {
                rollButton.setText("Game Over");
                rollButton.setEnabled(false);
                disableDice();
            } else {
                
            }
        }
    }
    
    public void setAnimationDelay(int delay) {
        animationDelay = delay;
        for (JAnimatedDie die : dice) {
            die.setDelay(delay);
        }
    }
    
    public int getAnimationDelay() {
        return animationDelay;
    }

    private void enableDice()
    {
        for (JAnimatedDie die : dice) {
            die.setEnabled(true);
        }
        disabledDice = false;
    }

    private void disableDice()
    {
        for (JAnimatedDie die : dice) {
            die.setEnabled(false);
        }
        disabledDice = true;
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        switch(evt.getPropertyName()) {
        case JAnimatedDie.PROP_ROLLING_DIE:
            rollingDiceCount += evt.getNewValue() == Boolean.TRUE ? 1 : -1;
            if (rolling && rollingDiceCount == 0) {
                hand.handleRoll();
                updatePanelState();
                setRolling(false);
            } else if (!rolling && rollingDiceCount == 1) {
                setRolling(true);
            }
            break;
        }
    }
    
    private void setRolling(boolean rolling) {
        boolean old = this.rolling;
        this.rolling = rolling;
        propertySupport.firePropertyChange(PROP_HAND_ROLLING, old, rolling);
    }
    
    private void incrementRollCount()
    {
        lastRollCount = ++rollCount;
        if (rollCount == 1) {
            propertySupport.firePropertyChange(PROP_NEW_ROLL, 0, 1);
        }
    }
    
    @Override
    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener)
    {
        this.propertySupport.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener)
    {
        this.propertySupport.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        dicePanel = new javax.swing.JPanel();
        jAnimatedDie1 = new com.davidjarski.javatzee.dice.JAnimatedDie();
        jAnimatedDie2 = new com.davidjarski.javatzee.dice.JAnimatedDie();
        jAnimatedDie3 = new com.davidjarski.javatzee.dice.JAnimatedDie();
        jAnimatedDie4 = new com.davidjarski.javatzee.dice.JAnimatedDie();
        jAnimatedDie5 = new com.davidjarski.javatzee.dice.JAnimatedDie();
        rollButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        dicePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        dicePanel.setLayout(new java.awt.GridBagLayout());

        jAnimatedDie1.setText("jAnimatedDie1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        dicePanel.add(jAnimatedDie1, gridBagConstraints);

        jAnimatedDie2.setText("jAnimatedDie2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        dicePanel.add(jAnimatedDie2, gridBagConstraints);

        jAnimatedDie3.setText("jAnimatedDie3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        dicePanel.add(jAnimatedDie3, gridBagConstraints);

        jAnimatedDie4.setText("jAnimatedDie4");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        dicePanel.add(jAnimatedDie4, gridBagConstraints);

        jAnimatedDie5.setText("jAnimatedDie5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        dicePanel.add(jAnimatedDie5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        add(dicePanel, gridBagConstraints);

        rollButton.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        rollButton.setText("Score Roll");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 0, 0);
        add(rollButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel dicePanel;
    private com.davidjarski.javatzee.dice.JAnimatedDie jAnimatedDie1;
    private com.davidjarski.javatzee.dice.JAnimatedDie jAnimatedDie2;
    private com.davidjarski.javatzee.dice.JAnimatedDie jAnimatedDie3;
    private com.davidjarski.javatzee.dice.JAnimatedDie jAnimatedDie4;
    private com.davidjarski.javatzee.dice.JAnimatedDie jAnimatedDie5;
    private javax.swing.JButton rollButton;
    // End of variables declaration//GEN-END:variables


}
