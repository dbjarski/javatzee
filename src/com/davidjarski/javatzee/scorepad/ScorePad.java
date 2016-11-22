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

import com.davidjarski.javatzee.hand.Hand;
import com.davidjarski.javatzee.history.GameRecord;
import com.davidjarski.javatzee.players.Players;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class ScorePad extends javax.swing.JPanel
        implements MouseListener, PropertyChangeListener
{
    
    private int numPlayers;  // the number of player, 1-4
    private int currentPlayer;  // a number, 0-3
    private ScorePanel currentPanel;
    private ArrayList<ScorePanel> scorePanels;
    private int gameOverCount;
    private final PropertyChangeSupport propertySupport;
    private Hand hand;
    private final Font smallFont;
    private final Font largeFont;

    public enum Cycle {

        LEFT, RIGHT, HOME
    }
    
    public ScorePad(Hand hand) {
        this.hand = hand;
        initComponents();
        propertySupport = new PropertyChangeSupport(this);
        scorePanels = new ArrayList<>();
        addPanel();
        numPlayers = 1;
        currentPanel = scorePanels.get(0);
        cycleScorePanel(Cycle.HOME);
        largeFont = currentPanel.getPlayerFont();
        smallFont = largeFont.deriveFont(12f);
    }
    
    public ScorePad() {
        this(null);
    }
    
    public ArrayList<GameRecord> getGameRecords() {
        ArrayList<GameRecord> records = new ArrayList<>();
        for (ScorePanel panel : scorePanels) {
            records.add(panel.getGameRecord());
        }
        return records;
    }

    public boolean isGameOver() {
        return gameOverCount == numPlayers;
    }
    
    public boolean isGameInProgress() {
        return scorePanels.get(0).isGameInProgress();
    }
    
    public void cycleScorePanel(Cycle direction) {
        if (numPlayers > 1) {
            currentPanel.setActive(false);
            switch (direction) {
            case HOME:
                currentPlayer = 0;
                currentPanel = scorePanels.get(0);
                break;
            case RIGHT:
                currentPlayer = (currentPlayer + 1) % numPlayers;
                currentPanel = scorePanels.get(currentPlayer);
                break;
            case LEFT:
                if (--currentPlayer < 0) {
                    currentPlayer = numPlayers - 1;
                }
                currentPanel = scorePanels.get(currentPlayer);
                break;
            }
        }
        currentPanel.setActive(true);
    }
    
    public void setPlayers(Players players) {
        while (players.getNumberOfPlayers() < numPlayers) {
            removePanel();
        }
        while (players.getNumberOfPlayers() > numPlayers) {
            addPanel();
        }
        Font font = (players.getLongestLength() > 12 ? smallFont : largeFont);
        for (int i = 0; i < scorePanels.size(); ++i) {
            scorePanels.get(i).setPlayerName(players.getName(i));
            scorePanels.get(i).setPlayerFont(font);
        }
    }

    private void addPanel() {
        ScorePanel panel = new ScorePanel(hand);
        scorePanels.add(numPlayers++, panel);
        add(panel);
        panel.addPropertyChangeListener(ScorePanel.PROP_SCORE_ENTERED, this);
        panel.addPropertyChangeListener(ScorePanel.PROP_GAME_OVER, this);
        
        if (numPlayers > 1) {
            for (ScorePanel scorePanel : scorePanels) {
                scorePanel.setUsingHighlights(true);
            }
            cycleScorePanel(Cycle.HOME);
        }
    }
    
    private void removePanel() {
        ScorePanel panel = scorePanels.get(numPlayers - 1);
        remove(panel);        
        scorePanels.remove(--numPlayers);
        if (numPlayers == 1) {
            for (ScorePanel scorePanel : scorePanels) {
                scorePanel.setUsingHighlights(false);
            }
            cycleScorePanel(Cycle.HOME);
        }
    }

    public void reset() {
        gameOverCount = 0;
        for (ScorePanel panel : scorePanels) {
            panel.reset();
            panel.setActive(false);
        }
        cycleScorePanel(Cycle.HOME);
    }

    public void disableScorePanels() {
        for (ScorePanel panel : scorePanels) {
            if (panel != null) {
                panel.setEnabled(false);
            }
        }
    }

    public void enablescorePanels() {
        for (ScorePanel panel : scorePanels) {
            if (panel != null) {
                panel.setEnabled(true);
            }
        }
    }

    public void undoLastScoreEntered() {
        cycleScorePanel(Cycle.LEFT);
        currentPanel.undoScoreEntered();
        currentPanel.setEnabled(true);
    }
    
    public void handleNewTurn() {
        currentPanel.handleNewTurn();
    }
    
    public void doDummyGame() {
        reset();
        for (int i = 0; i < numPlayers; ++i) {
            currentPanel.doDummyGame();
            cycleScorePanel(Cycle.RIGHT);
        }
    }
        
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        switch(evt.getPropertyName()) {
        case ScorePanel.PROP_SCORE_ENTERED:
            if ((Integer)evt.getNewValue() > (Integer)(evt.getOldValue())) {
                cycleScorePanel(Cycle.RIGHT);
            } else {
            }
            propertySupport.firePropertyChange(evt);
            break;
        case ScorePanel.PROP_GAME_OVER:
            if (++gameOverCount == numPlayers) {
                propertySupport.firePropertyChange(evt);
            }
            break;
        }
    }

    public int getCurrentPlayerNumber() {
        return currentPlayer;
    }

    public void highlightPlayerName() {
        currentPanel.highlightPlayerName();
    }
    
    public void unHighlightPlayerName() {
        currentPanel.unHightlightPlayerName();
    }

    public String getPlayerName() {
        return currentPanel.getPlayerName();
    }

    public int getNumPlayers() {
        return numPlayers;
    }
    
    public int getScore() {
        return currentPanel.getScore();
    }

    public int getFiveOfAKindCount() {
        return currentPanel.getKind5Count();
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

    public void addPropertyChangeListenerToScorePanels(String propertyName,
            PropertyChangeListener listener) {
        for (ScorePanel panel : scorePanels) {
            panel.addPropertyChangeListener(propertyName, listener);
        }
    }

    public void removePropertyChangeListenerFromScorePanels(
            String propertyName, PropertyChangeListener listener) {
        for (ScorePanel panel : scorePanels) {
            panel.removePropertyChangeListener(propertyName, listener);
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        currentPanel.mousePressed(e);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        scorePanelLabels1 = new com.davidjarski.javatzee.scorepad.ScorePanelLabels();

        setLayout(new java.awt.GridLayout(1, 0));
        add(scorePanelLabels1);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.davidjarski.javatzee.scorepad.ScorePanelLabels scorePanelLabels1;
    // End of variables declaration//GEN-END:variables
}
