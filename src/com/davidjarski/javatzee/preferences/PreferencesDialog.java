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

import com.davidjarski.javatzee.dice.JAnimatedDie;
import com.davidjarski.javatzee.dice.JGraphicalDie;
import com.davidjarski.javatzee.dice.JGraphicalDie.DiceStyle;
import com.davidjarski.javatzee.dice.JGraphicalDie.LockStyle;
import com.davidjarski.javatzee.hand.HandPanel;
import com.davidjarski.javatzee.main.DialogListener;
import java.awt.Frame;

public class PreferencesDialog extends javax.swing.JDialog
{

    public static final String ANIMATION_DELAY = "ANIMATION_DELAY";

    private int originalDelay;
    private int delay;
    private boolean userClosedWithButton;

    private Preferences preferences;
    private Preferences originalPreferences;
    private DialogListener dialogListener;

    /**
     * Creates new form PreferencesDialog
     */
    public PreferencesDialog(Frame parent, boolean modal) {
        this(parent, modal, null);
    }

    public PreferencesDialog(Frame parent, boolean modal,
            DialogListener listener) {
        super(parent, modal);
        // the preferences need to be loaded before the call to initComponents()
        originalPreferences = Preferences.getSavedPreferences();
        preferences = Preferences.getCopy(originalPreferences);    
        initComponents();

        dialogListener = listener;
        originalDelay = handPanel.getAnimationDelay();

        setComponentValues();
        setLocationRelativeTo(parent);
    }

    private void setComponentValues() {
        lblDelaySlider.setText("Animation Delay: " + preferences.getDelay());
        spnNumRolls.setValue(preferences.getNumRolls());

        switch (preferences.getMainStyle()) {
        case WHITE:
            rdbMainWhite.setSelected(true);
            break;
        case BLACK:
            rdbMainBlack.setSelected(true);
            break;
        case SINGLE_HUE:
            rdbMainSingle.setSelected(true);
            break;
        case SOFT_MULTI_HUE:
            rdbMainSoftMulti.setSelected(true);
            break;
        case BRIGHT_MULTI_HUE:
            rdbMainBrightMulti.setSelected(true);
            break;
        }

        switch (preferences.getHighlightStyle()) {
        case WHITE:
            rdbHighlightWhite.setSelected(true);
            break;
        case BLACK:
            rdbHighlightBlack.setSelected(true);
            break;
        case SINGLE_HUE:
            rdbHighlightSingle.setSelected(true);
            break;
        case SOFT_MULTI_HUE:
            rdbHighlightSoftMulti.setSelected(true);
            break;
        case BRIGHT_MULTI_HUE:
            rdbHighlightBrightMulti.setSelected(true);
            break;
        }

        switch (preferences.getLockStyle()) {
        case NONE:
            rdbLockNone.setSelected(true);
            break;
        case SMALL:
            rdbLockSmall.setSelected(true);
            break;
        case MEDIUM:
            rdbLockMedium.setSelected(true);
            break;
        case LARGE:
            rdbLockLarge.setSelected(true);
            break;
        }
        
        chkColoredLock.setSelected(preferences.isColoredLock());
    }

    private void setNumRolls(int numRolls) {
        preferences.setNumRolls(numRolls);
        JAnimatedDie.setNumberOfRolls(numRolls);
    }

    private void setDelay(int delay) {
        preferences.setDelay(delay);
        this.delay = delay;
        handPanel.setAnimationDelay(delay);
    }

    private void setMainDiceStyle(DiceStyle style) {
        preferences.setMainStyle(style);
        JGraphicalDie.setMainStyle(style);
        handPanel.repaint();
    }

    private void setHighlightDiceStyle(DiceStyle style) {
        preferences.setHighlightStyle(style);
        JGraphicalDie.setHighlightStyle(style);
        handPanel.repaint();
    }

    private void setLockStyle(LockStyle style) {
        preferences.setLockStyle(style);
        JGraphicalDie.setLockStyle(style);
        handPanel.repaint();
    }

    private void setColoredLock(boolean coloredLock) {
        preferences.setColoredLock(coloredLock);
        JGraphicalDie.setColoredLock(coloredLock);
        handPanel.repaint();
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

        mainDiceStyleGroup = new javax.swing.ButtonGroup();
        highlightStyleGroup = new javax.swing.ButtonGroup();
        lockStyleGroup = new javax.swing.ButtonGroup();
        pnlLeftColumn = new javax.swing.JPanel();
        handPanel = new HandPanel(true);
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 30), new java.awt.Dimension(0, 30), new java.awt.Dimension(32767, 30));
        pnlAnimation = new javax.swing.JPanel();
        lblNumRollsSpinner = new javax.swing.JLabel();
        spnNumRolls = new javax.swing.JSpinner();
        lblDelaySlider = new javax.swing.JLabel();
        sldDelay = new javax.swing.JSlider();
        pnlRightColumn = new javax.swing.JPanel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        pnlMainDiceStyle = new javax.swing.JPanel();
        innerPanel1 = new javax.swing.JPanel();
        lblMainStylePanel = new javax.swing.JLabel();
        rdbMainWhite = new javax.swing.JRadioButton();
        rdbMainBlack = new javax.swing.JRadioButton();
        rdbMainSingle = new javax.swing.JRadioButton();
        rdbMainSoftMulti = new javax.swing.JRadioButton();
        rdbMainBrightMulti = new javax.swing.JRadioButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        pnlHighlightDiceStyle = new javax.swing.JPanel();
        innerPanel2 = new javax.swing.JPanel();
        lblHighlightStylePanel = new javax.swing.JLabel();
        rdbHighlightWhite = new javax.swing.JRadioButton();
        rdbHighlightBlack = new javax.swing.JRadioButton();
        rdbHighlightSingle = new javax.swing.JRadioButton();
        rdbHighlightSoftMulti = new javax.swing.JRadioButton();
        rdbHighlightBrightMulti = new javax.swing.JRadioButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        pnlLockStyle = new javax.swing.JPanel();
        innerPanel3 = new javax.swing.JPanel();
        lblLockStylePanel = new javax.swing.JLabel();
        rdbLockNone = new javax.swing.JRadioButton();
        rdbLockSmall = new javax.swing.JRadioButton();
        rdbLockMedium = new javax.swing.JRadioButton();
        rdbLockLarge = new javax.swing.JRadioButton();
        chkColoredLock = new javax.swing.JCheckBox();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        pnlButtons = new javax.swing.JPanel();
        innerPanel4 = new javax.swing.JPanel();
        btnOk = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Preferences");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlLeftColumn.setLayout(new javax.swing.BoxLayout(pnlLeftColumn, javax.swing.BoxLayout.Y_AXIS));
        pnlLeftColumn.add(handPanel);
        pnlLeftColumn.add(filler1);

        pnlAnimation.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlAnimation.setLayout(new java.awt.GridBagLayout());

        lblNumRollsSpinner.setText("Rolls per Animation:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        pnlAnimation.add(lblNumRollsSpinner, gridBagConstraints);

        spnNumRolls.setModel(new javax.swing.SpinnerNumberModel(5, 1, 99, 1));
        spnNumRolls.setToolTipText("");
        spnNumRolls.setRequestFocusEnabled(false);
        spnNumRolls.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnNumRollsStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        pnlAnimation.add(spnNumRolls, gridBagConstraints);

        lblDelaySlider.setText("Animation Delay: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        pnlAnimation.add(lblDelaySlider, gridBagConstraints);

        sldDelay.setMajorTickSpacing(10);
        sldDelay.setMaximum(200);
        sldDelay.setMinimum(1);
        sldDelay.setPaintTicks(true);
        sldDelay.setToolTipText("Milliseconds between rolls");
        sldDelay.setValue(preferences.getDelay());
        sldDelay.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldDelayStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 10, 10);
        pnlAnimation.add(sldDelay, gridBagConstraints);

        pnlLeftColumn.add(pnlAnimation);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(pnlLeftColumn, gridBagConstraints);

        pnlRightColumn.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weighty = 0.5;
        pnlRightColumn.add(filler5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        gridBagConstraints.weighty = 0.5;
        pnlRightColumn.add(filler6, gridBagConstraints);

        pnlMainDiceStyle.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlMainDiceStyle.setLayout(new java.awt.GridBagLayout());

        innerPanel1.setLayout(new java.awt.GridBagLayout());

        lblMainStylePanel.setText("Select a primary dice set");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        innerPanel1.add(lblMainStylePanel, gridBagConstraints);

        mainDiceStyleGroup.add(rdbMainWhite);
        rdbMainWhite.setText("White");
        rdbMainWhite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbMainWhiteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        innerPanel1.add(rdbMainWhite, gridBagConstraints);

        mainDiceStyleGroup.add(rdbMainBlack);
        rdbMainBlack.setText("Black");
        rdbMainBlack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbMainBlackActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 0, 8);
        innerPanel1.add(rdbMainBlack, gridBagConstraints);

        mainDiceStyleGroup.add(rdbMainSingle);
        rdbMainSingle.setText("Single Color");
        rdbMainSingle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbMainSingleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 0, 8);
        innerPanel1.add(rdbMainSingle, gridBagConstraints);

        mainDiceStyleGroup.add(rdbMainSoftMulti);
        rdbMainSoftMulti.setText("Soft Multi-Color");
        rdbMainSoftMulti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbMainSoftMultiActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 0, 8);
        innerPanel1.add(rdbMainSoftMulti, gridBagConstraints);

        mainDiceStyleGroup.add(rdbMainBrightMulti);
        rdbMainBrightMulti.setText("Bright Multi-Color");
        rdbMainBrightMulti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbMainBrightMultiActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 8, 8);
        innerPanel1.add(rdbMainBrightMulti, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlMainDiceStyle.add(innerPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        pnlMainDiceStyle.add(filler4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlRightColumn.add(pnlMainDiceStyle, gridBagConstraints);

        pnlHighlightDiceStyle.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlHighlightDiceStyle.setLayout(new java.awt.GridBagLayout());

        innerPanel2.setLayout(new java.awt.GridBagLayout());

        lblHighlightStylePanel.setText("Select a locked dice set");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        innerPanel2.add(lblHighlightStylePanel, gridBagConstraints);

        highlightStyleGroup.add(rdbHighlightWhite);
        rdbHighlightWhite.setText("White");
        rdbHighlightWhite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbHighlightWhiteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        innerPanel2.add(rdbHighlightWhite, gridBagConstraints);

        highlightStyleGroup.add(rdbHighlightBlack);
        rdbHighlightBlack.setText("Black");
        rdbHighlightBlack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbHighlightBlackActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 0, 8);
        innerPanel2.add(rdbHighlightBlack, gridBagConstraints);

        highlightStyleGroup.add(rdbHighlightSingle);
        rdbHighlightSingle.setText("Single Color");
        rdbHighlightSingle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbHighlightSingleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 0, 8);
        innerPanel2.add(rdbHighlightSingle, gridBagConstraints);

        highlightStyleGroup.add(rdbHighlightSoftMulti);
        rdbHighlightSoftMulti.setText("Soft Multi-Color");
        rdbHighlightSoftMulti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbHighlightSoftMultiActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 0, 8);
        innerPanel2.add(rdbHighlightSoftMulti, gridBagConstraints);

        highlightStyleGroup.add(rdbHighlightBrightMulti);
        rdbHighlightBrightMulti.setText("Bright Multi-Color");
        rdbHighlightBrightMulti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbHighlightBrightMultiActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 8, 8);
        innerPanel2.add(rdbHighlightBrightMulti, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 6);
        pnlHighlightDiceStyle.add(innerPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        pnlHighlightDiceStyle.add(filler2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlRightColumn.add(pnlHighlightDiceStyle, gridBagConstraints);

        pnlLockStyle.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlLockStyle.setLayout(new java.awt.GridBagLayout());

        innerPanel3.setLayout(new java.awt.GridBagLayout());

        lblLockStylePanel.setText("Select a lock style");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        innerPanel3.add(lblLockStylePanel, gridBagConstraints);

        lockStyleGroup.add(rdbLockNone);
        rdbLockNone.setText("None");
        rdbLockNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbLockNoneActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        innerPanel3.add(rdbLockNone, gridBagConstraints);

        lockStyleGroup.add(rdbLockSmall);
        rdbLockSmall.setText("Small");
        rdbLockSmall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbLockSmallActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 0, 8);
        innerPanel3.add(rdbLockSmall, gridBagConstraints);

        lockStyleGroup.add(rdbLockMedium);
        rdbLockMedium.setText("Medium");
        rdbLockMedium.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbLockMediumActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 0, 8);
        innerPanel3.add(rdbLockMedium, gridBagConstraints);

        lockStyleGroup.add(rdbLockLarge);
        rdbLockLarge.setText("Large");
        rdbLockLarge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbLockLargeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 0, 8);
        innerPanel3.add(rdbLockLarge, gridBagConstraints);

        chkColoredLock.setText("Colored Lock");
        chkColoredLock.setIconTextGap(8);
        chkColoredLock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkColoredLockActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(12, 8, 8, 8);
        innerPanel3.add(chkColoredLock, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlLockStyle.add(innerPanel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        pnlLockStyle.add(filler3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlRightColumn.add(pnlLockStyle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(pnlRightColumn, gridBagConstraints);

        pnlButtons.setLayout(new java.awt.GridLayout(1, 0));

        innerPanel4.setLayout(new java.awt.GridLayout(1, 0, 30, 0));

        btnOk.setText("Save Setttings");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });
        innerPanel4.add(btnOk);

        btnReset.setText("Default Settings");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });
        innerPanel4.add(btnReset);

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        innerPanel4.add(btnCancel);

        pnlButtons.add(innerPanel4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(20, 5, 12, 5);
        getContentPane().add(pnlButtons, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void spnNumRollsStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spnNumRollsStateChanged
    {//GEN-HEADEREND:event_spnNumRollsStateChanged
        setNumRolls((Integer) spnNumRolls.getValue());
    }//GEN-LAST:event_spnNumRollsStateChanged

    private void sldDelayStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_sldDelayStateChanged
    {//GEN-HEADEREND:event_sldDelayStateChanged
        setDelay(sldDelay.getValue());
        lblDelaySlider.setText("Animation Delay: " + delay);
    }//GEN-LAST:event_sldDelayStateChanged

    private void rdbMainWhiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbMainWhiteActionPerformed
        setMainDiceStyle(DiceStyle.WHITE);
    }//GEN-LAST:event_rdbMainWhiteActionPerformed

    private void rdbMainBlackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbMainBlackActionPerformed
        setMainDiceStyle(DiceStyle.BLACK);
    }//GEN-LAST:event_rdbMainBlackActionPerformed

    private void rdbMainSingleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbMainSingleActionPerformed
        setMainDiceStyle(DiceStyle.SINGLE_HUE);
    }//GEN-LAST:event_rdbMainSingleActionPerformed

    private void rdbMainSoftMultiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbMainSoftMultiActionPerformed
        setMainDiceStyle(DiceStyle.SOFT_MULTI_HUE);
    }//GEN-LAST:event_rdbMainSoftMultiActionPerformed

    private void rdbMainBrightMultiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbMainBrightMultiActionPerformed
        setMainDiceStyle(DiceStyle.BRIGHT_MULTI_HUE);
    }//GEN-LAST:event_rdbMainBrightMultiActionPerformed

    private void rdbHighlightWhiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbHighlightWhiteActionPerformed
        setHighlightDiceStyle(DiceStyle.WHITE);
    }//GEN-LAST:event_rdbHighlightWhiteActionPerformed

    private void rdbHighlightBlackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbHighlightBlackActionPerformed
        setHighlightDiceStyle(DiceStyle.BLACK);
    }//GEN-LAST:event_rdbHighlightBlackActionPerformed

    private void rdbHighlightSingleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbHighlightSingleActionPerformed
        setHighlightDiceStyle(DiceStyle.SINGLE_HUE);
    }//GEN-LAST:event_rdbHighlightSingleActionPerformed

    private void rdbHighlightSoftMultiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbHighlightSoftMultiActionPerformed
        setHighlightDiceStyle(DiceStyle.SOFT_MULTI_HUE);
    }//GEN-LAST:event_rdbHighlightSoftMultiActionPerformed

    private void rdbHighlightBrightMultiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbHighlightBrightMultiActionPerformed
        setHighlightDiceStyle(DiceStyle.BRIGHT_MULTI_HUE);
    }//GEN-LAST:event_rdbHighlightBrightMultiActionPerformed

    private void rdbLockNoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbLockNoneActionPerformed
        setLockStyle(LockStyle.NONE);
    }//GEN-LAST:event_rdbLockNoneActionPerformed

    private void rdbLockSmallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbLockSmallActionPerformed
        setLockStyle(LockStyle.SMALL);
    }//GEN-LAST:event_rdbLockSmallActionPerformed

    private void rdbLockMediumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbLockMediumActionPerformed
        setLockStyle(LockStyle.MEDIUM);
    }//GEN-LAST:event_rdbLockMediumActionPerformed

    private void rdbLockLargeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbLockLargeActionPerformed
        setLockStyle(LockStyle.LARGE);
    }//GEN-LAST:event_rdbLockLargeActionPerformed

    private void chkColoredLockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkColoredLockActionPerformed
        setColoredLock(chkColoredLock.isSelected());
    }//GEN-LAST:event_chkColoredLockActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        Preferences.savePreferences(preferences);
        if (delay != originalPreferences.getDelay()) {
            dialogListener.onDialogChangesAccepted(ANIMATION_DELAY, delay);
        }
        preferences.load();
        userClosedWithButton = true;
        this.dispose();
    }//GEN-LAST:event_btnOkActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        preferences = Preferences.getDefaultPreferences();
        preferences.load();
        handPanel.setAnimationDelay(JAnimatedDie.DEFAULT_DELAY);
        handPanel.repaint();
        setComponentValues();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        originalPreferences.load();
        userClosedWithButton = true;
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (!userClosedWithButton) {
            Preferences.getSavedPreferences().load();
        }
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(PreferencesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PreferencesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PreferencesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PreferencesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run() {
                PreferencesDialog dialog = new PreferencesDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter()
                {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnReset;
    private javax.swing.JCheckBox chkColoredLock;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private com.davidjarski.javatzee.hand.HandPanel handPanel;
    private javax.swing.ButtonGroup highlightStyleGroup;
    private javax.swing.JPanel innerPanel1;
    private javax.swing.JPanel innerPanel2;
    private javax.swing.JPanel innerPanel3;
    private javax.swing.JPanel innerPanel4;
    private javax.swing.JLabel lblDelaySlider;
    private javax.swing.JLabel lblHighlightStylePanel;
    private javax.swing.JLabel lblLockStylePanel;
    private javax.swing.JLabel lblMainStylePanel;
    private javax.swing.JLabel lblNumRollsSpinner;
    private javax.swing.ButtonGroup lockStyleGroup;
    private javax.swing.ButtonGroup mainDiceStyleGroup;
    private javax.swing.JPanel pnlAnimation;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlHighlightDiceStyle;
    private javax.swing.JPanel pnlLeftColumn;
    private javax.swing.JPanel pnlLockStyle;
    private javax.swing.JPanel pnlMainDiceStyle;
    private javax.swing.JPanel pnlRightColumn;
    private javax.swing.JRadioButton rdbHighlightBlack;
    private javax.swing.JRadioButton rdbHighlightBrightMulti;
    private javax.swing.JRadioButton rdbHighlightSingle;
    private javax.swing.JRadioButton rdbHighlightSoftMulti;
    private javax.swing.JRadioButton rdbHighlightWhite;
    private javax.swing.JRadioButton rdbLockLarge;
    private javax.swing.JRadioButton rdbLockMedium;
    private javax.swing.JRadioButton rdbLockNone;
    private javax.swing.JRadioButton rdbLockSmall;
    private javax.swing.JRadioButton rdbMainBlack;
    private javax.swing.JRadioButton rdbMainBrightMulti;
    private javax.swing.JRadioButton rdbMainSingle;
    private javax.swing.JRadioButton rdbMainSoftMulti;
    private javax.swing.JRadioButton rdbMainWhite;
    private javax.swing.JSlider sldDelay;
    private javax.swing.JSpinner spnNumRolls;
    // End of variables declaration//GEN-END:variables
}
