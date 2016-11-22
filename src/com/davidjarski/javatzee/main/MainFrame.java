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
package com.davidjarski.javatzee.main;

import com.davidjarski.javatzee.IO.HistoryIO;
import com.davidjarski.javatzee.IO.Resources;
import com.davidjarski.javatzee.hand.HandPanel;
import com.davidjarski.javatzee.help.AboutDialog;
import com.davidjarski.javatzee.history.GameRecord;
import com.davidjarski.javatzee.history.GameRecordTableModel;
import com.davidjarski.javatzee.history.HighScores;
import com.davidjarski.javatzee.history.HistoryDialog;
import com.davidjarski.javatzee.history.HighScoresTableModel;
import com.davidjarski.javatzee.players.PlayerNamesDialog;
import com.davidjarski.javatzee.players.Players;
import com.davidjarski.javatzee.preferences.Preferences;
import com.davidjarski.javatzee.preferences.PreferencesDialog;
import com.davidjarski.javatzee.scorepad.ScorePanel;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class MainFrame extends javax.swing.JFrame
            implements ActionListener, PropertyChangeListener, DialogListener
{
    
    private static final String HIGH_SCORES_ACTION = "HIGH_SCORES";
    private static final String NEW_GAME_ACTION = "NEW_GAME";
    private static final String PREFERENCES_ACTION = "PREFERENCES";
    private static final String UNDO_ACTION = "UNDO";
    private static final String ENTER_USERS_ACTION = "ENTER_USERS";
    private static final String HELP_ACTION = "HELP";
    private static final String HISTORY_ACTION = "HISTORY";
    private static final String RECORD_ACTION = "RECORD";
    
    private static final String APPLICATION_NAME = "Triple Javatzee";
    private static final String HIGH_SCORES_TITLE = "All-Time High Scores";
    private static final String HISTORY_TITLE = "Game History";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    private static final int MESSAGE_DELAY = 500;
    
    private JButton highScoresButton;
    private JButton newGameButton;
    private JButton preferencesButton;
    private JButton undoButton;
    private JButton enterUsersbutton;
    private JButton helpButton;
    private JButton historyButton;
    private JButton recordButton;
    private Players players;
    private HistoryDialog highScoresDialog;
    private HistoryDialog historyDialog;
    private final HighScores highScores;
    private boolean orderedWindows;

    /**
     * Creates new form MainFrame
     */
    public MainFrame()
    {
        Preferences preferences = Preferences.getSavedPreferences();
        preferences.load();
        players = Players.loadSavedPlayers();
        highScores = new HighScores();
        initComponents();
        initMenu();
        handPanel.setAnimationDelay(preferences.getDelay());
        
        setTitle(APPLICATION_NAME);
        setIconImage(new ImageIcon(getClass().getResource(Resources.FAVICON_PATH)).getImage());
        
        scorePad.setPlayers(players);
        handPanel.addPropertyChangeListener(HandPanel.PROP_HAND_ROLLING, this);
        handPanel.addPropertyChangeListener(HandPanel.PROP_NEW_ROLL, this);
        scorePad.addPropertyChangeListener(ScorePanel.PROP_GAME_OVER, this);
        scorePad.addPropertyChangeListener(ScorePanel.PROP_SCORE_ENTERED, this);
        
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            @Override
            public void windowActivated(java.awt.event.WindowEvent evt) {
                if (!orderedWindows) {
                    if (highScoresDialog != null) {
                        highScoresDialog.toFront();
                    }
                    if (historyDialog != null) {
                        historyDialog.toFront();
                    }
                    evt.getWindow().toFront();
                } else {
                }
                orderedWindows = !orderedWindows;
            }
            
        });
        
        pack();
        setLocationRelativeTo(null);
        
        startNewGame();
    }
    
    public void startNewGame() {
        handPanel.reset();
        scorePad.reset();
        hintPanel.reset();
    }

    private void initMenu()
    {
        int iconSize = 32;
        int currentPos = 0;
        
        try {
            BufferedImage image = ImageIO.read(getClass().getResource(Resources.TOOLBAR_ICONS_PATH));
            
            BufferedImage icon = image.getSubimage(currentPos, 0, iconSize, iconSize);
            highScoresButton = makeToolBarButton(icon,
                    "High Scores", HIGH_SCORES_ACTION);
            if (highScores.getSize() == 0) {
                icon = image.getSubimage(currentPos, iconSize, iconSize, iconSize);
                highScoresButton.setDisabledIcon(new ImageIcon(icon));
                highScoresButton.setEnabled(false);
            }

            icon = image.getSubimage((currentPos += iconSize), 0, iconSize, iconSize);
            newGameButton = makeToolBarButton(icon,
                    "New Game", NEW_GAME_ACTION);

            icon = image.getSubimage((currentPos += iconSize), 0, iconSize, iconSize);
            preferencesButton = makeToolBarButton(icon,
                    "Preferences", PREFERENCES_ACTION);

            icon = image.getSubimage((currentPos += iconSize), 0, iconSize, iconSize);
            undoButton = makeToolBarButton(icon,
                    "Undo", UNDO_ACTION);
            icon = image.getSubimage(currentPos, iconSize, iconSize, iconSize);
            undoButton.setDisabledIcon(new ImageIcon(icon));
            undoButton.setEnabled(false);

            icon = image.getSubimage((currentPos += iconSize), 0, iconSize, iconSize);
            enterUsersbutton = makeToolBarButton(icon,
                    "Enter Player Names", ENTER_USERS_ACTION);

            // help button, tooltip of "about" until I make an actual help section
            icon = image.getSubimage((currentPos += iconSize), 0, iconSize, iconSize);
            helpButton = makeToolBarButton(icon,
                    "About", HELP_ACTION);  

            icon = image.getSubimage((currentPos += iconSize), 0, iconSize, iconSize);
            historyButton = makeToolBarButton(icon,
                    "History", HISTORY_ACTION);          
            if (highScores.getSize() == 0) {
                icon = image.getSubimage(currentPos, iconSize, iconSize, iconSize);
                historyButton.setDisabledIcon(new ImageIcon(icon));
                historyButton.setEnabled(false);
            }

            icon = image.getSubimage((currentPos += iconSize), 0, iconSize, iconSize);
            recordButton = makeToolBarButton(icon,
                    "Record New Game", RECORD_ACTION);

            toolBar.add(Box.createHorizontalStrut(10));
            toolBar.add(newGameButton);
            toolBar.addSeparator(new Dimension(25,0));
            toolBar.add(undoButton);
            toolBar.addSeparator(new Dimension(25,0));
            toolBar.add(enterUsersbutton);
            toolBar.add(highScoresButton);
            toolBar.add(historyButton);  //not currently implemented
            toolBar.add(preferencesButton);
            toolBar.add(helpButton);
            toolBar.add(recordButton);
            toolBar.add(Box.createHorizontalGlue());

            toolBar.setFloatable(false);
            pack();
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    private JButton makeToolBarButton(Image image, String toolTip,
                String actionCommand) {
        JButton button = new JButton();
        button.setIcon(new ImageIcon(image));
        button.setToolTipText(toolTip);
        button.setActionCommand(actionCommand);
        button.addActionListener(this);
        return button;
    }
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
        switch(evt.getActionCommand()) {
        case UNDO_ACTION:
            if (scorePad.isGameOver()) {
                undoButton.setEnabled(false);
            } else {
                scorePad.undoLastScoreEntered();
                undoButton.setEnabled(false);
                handPanel.handleUndo();
            }
            break;
        case NEW_GAME_ACTION:
            if ((scorePad.isGameInProgress() || handPanel.isRollInProgress())
                    && JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to abort the current game"
                            + LINE_SEPARATOR + "and start a new one?",
                    "Abort Game?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION)
            {
                return; // //user chose not to start a new game
            }
            startNewGame();
            break;
        case ENTER_USERS_ACTION:
            new PlayerNamesDialog(this, true, this).setVisible(true);
            break;
        case HIGH_SCORES_ACTION:
            if (highScoresDialog == null) {
                highScoresDialog = new HistoryDialog(
                        null, false, new HighScoresTableModel(highScores));
                highScoresDialog.setLocationRelativeTo(this);
                highScoresDialog.setTitle(HIGH_SCORES_TITLE);
            }
            highScoresDialog.setVisible(true);
            break;
        case HISTORY_ACTION:
            if (historyDialog == null) {
                historyDialog = new HistoryDialog(null, false, new GameRecordTableModel(HistoryIO.read()));
                historyDialog.setLocationRelativeTo(this);
                historyDialog.setTitle(HISTORY_TITLE);
                historyDialog.setSortingEnabled(true);
            }
            historyDialog.setVisible(true);
            break;
        case PREFERENCES_ACTION:
            new PreferencesDialog(this, true, this).setVisible(true);
            break;
        case HELP_ACTION:
            new AboutDialog(this, true).setVisible(true);
            break;
        case RECORD_ACTION:
            scorePad.doDummyGame();
            break;
        }
    }
    
    
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        switch(evt.getPropertyName()) {
        case HandPanel.PROP_NEW_ROLL:
            undoButton.setEnabled(false);
            scorePad.handleNewTurn();
            break;
        case HandPanel.PROP_HAND_ROLLING:
            boolean rolling = (Boolean) evt.getNewValue();
            if (rolling) {
                hintPanel.reset();
            } else {
                hintPanel.updateDisplay();
            }
            break;
        case ScorePanel.PROP_SCORE_ENTERED:
            handPanel.handleNewTurn();
            undoButton.setEnabled(true);
            break;
        case ScorePanel.PROP_GAME_OVER:
            handPanel.handleGameOver();
            undoButton.setEnabled(false);
            historyButton.setEnabled(true);
            highScoresButton.setEnabled(true);
            ArrayList<GameRecord> records = scorePad.getGameRecords();
            HistoryIO.write(records);
            if (historyDialog != null) {
                historyDialog.updateTable(records);
            }
            ArrayList<String> names = null;
            /*
                Try addding the new records to the list of high scores. If any
                record is found to be a new high score, display a message.
            */
            for (GameRecord record : records) {
                if (highScores.addConditionally(record)) {
                    if (highScoresDialog != null) {
                        highScoresDialog.updateTable();
                    }
                    if (names == null) {
                        names = new ArrayList<>();
                    }
                    names.add(record.getName());
                } else {
                }
            }
            if (names != null) {
                displayHighScoreMessage(names);
            }
            break;
        }
    }
    
    @Override
    public void onDialogChangesAccepted(String key, Object newValue) {
        switch(key) {
        case PreferencesDialog.ANIMATION_DELAY:
            try {
                handPanel.setAnimationDelay((Integer)newValue);
                handPanel.repaint();
            } catch (ClassCastException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            break;
        case PlayerNamesDialog.NEW_NAMES:
            // TODO new names confirmation dialog a bigger font
            if (scorePad.isGameInProgress() && JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to abort the current game?",
                    "Abort Game?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
                return; // //user chose not to start a new game
            }
            try {
                players = (Players)newValue;
                scorePad.setPlayers(players);
                pack();
            } catch (ClassCastException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            break;
        }
    }
    
    private void displayHighScoreMessage(ArrayList<String> names) {
        // TODO give high score message a bigger font
        final String nameString;
        if (names.size() == 1) {
            nameString = names.get(0);
        } else if (names.size() == 2) {
            nameString = names.get(0) + " and " + names.get(1);
        } else {
            String s = "";
            for (int i = 0; i < names.size() - 1; ++i) {
                s += names.get(i) + ", ";
            }
            s += "and " + names.get(names.size() - 1);
            nameString = s;
        }
        
        Timer timer = new Timer(MESSAGE_DELAY, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon icon = new ImageIcon(
                        getClass().getResource(Resources.MINIME_ICON_PATH));
                String message = "Congratulations, " + nameString + "!" + LINE_SEPARATOR
                        + "You've earned a spot in the high scores.";
                if (highScoresDialog == null) {
                    message += LINE_SEPARATOR 
                            + "View the high score list by selecting the star in the toolbar.";
                }
                JOptionPane.showMessageDialog(mainPanel, message,
                        "New High Score!", JOptionPane.PLAIN_MESSAGE, icon);
            }
        });
        timer.setRepeats(false);
        timer.start();
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

        toolBar = new javax.swing.JToolBar();
        mainPanel = new javax.swing.JPanel();
        leftPanel = new javax.swing.JPanel();
        handPanel = new com.davidjarski.javatzee.hand.HandPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 20), new java.awt.Dimension(0, 20), new java.awt.Dimension(32767, 20));
        hintPanel = new com.davidjarski.javatzee.hints.HintPanel(handPanel.getHand());
        rightPanel = new javax.swing.JPanel();
        scorePad = new com.davidjarski.javatzee.scorepad.ScorePad(handPanel.getHand());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("MainFrame"); // NOI18N
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        toolBar.setRollover(true);
        getContentPane().add(toolBar);

        mainPanel.setLayout(new java.awt.GridBagLayout());

        leftPanel.setLayout(new javax.swing.BoxLayout(leftPanel, javax.swing.BoxLayout.Y_AXIS));
        leftPanel.add(handPanel);
        leftPanel.add(filler1);
        leftPanel.add(hintPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 15, 0);
        mainPanel.add(leftPanel, gridBagConstraints);

        rightPanel.setLayout(new java.awt.GridLayout(1, 0));
        rightPanel.add(scorePad);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 15, 15);
        mainPanel.add(rightPanel, gridBagConstraints);

        getContentPane().add(mainPanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private com.davidjarski.javatzee.hand.HandPanel handPanel;
    private com.davidjarski.javatzee.hints.HintPanel hintPanel;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel rightPanel;
    private com.davidjarski.javatzee.scorepad.ScorePad scorePad;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables

}
