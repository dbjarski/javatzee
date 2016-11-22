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
package com.davidjarski.javatzee.players;

import com.davidjarski.javatzee.main.DialogListener;
import com.davidjarski.javatzee.main.Utility;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;

public class PlayerNamesDialog extends javax.swing.JDialog
{
    public static final String NEW_NAMES = "NEW_NAMES";
    
    private static final int LABEL_MAX_CHARS =
            (Players.MAX_CHARACTERS > 18 ? 18 : Players.MAX_CHARACTERS);
    private static final String LONG_NAME_MESSAGE = "Player names may not exceed "
            + LABEL_MAX_CHARS + " characters.";
    
    private DialogListener dialogListener;
    private JTextField[] textFields;
    private Players players = Players.getPlayers();
    private int usedFields;
    
    /**
     * Creates new form PlayerNamesDialog
     */
    public PlayerNamesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        Utility.lockComponentSize(lblErrorMessage);
        lblErrorMessage.setText(null);
        initTextFields();
        
        Point p = parent.getLocation();
        int xOffset = (parent.getWidth() - getWidth()) / 2;
        setLocation(p.x + xOffset, p.y + 50);
    }
    
    public PlayerNamesDialog(java.awt.Frame parent, boolean modal,
            DialogListener listener) {
        this(parent, modal);
        dialogListener = listener;
    }
    
    private boolean validateName(JTextField textField) {
        textField.setText(textField.getText().trim());
        if (textField.getText().length() > LABEL_MAX_CHARS) {
            lblErrorMessage.setText(LONG_NAME_MESSAGE);
            return false;
        } 
        return true;
    }
    
    private boolean validateNames() {
        usedFields = 0;
        for (JTextField textField : textFields) {
            if (!validateName(textField)) {
                return false;
            }
        }
        lblErrorMessage.setText("");
        return true;
    }
    
    private void initTextFields() {
        textFields = new JTextField[]{
            txtPlayer1, txtPlayer2, txtPlayer3, txtPlayer4};
        for (int i = 0; i < textFields.length; ++i) {
            if (i < players.getNumberOfPlayers()) {
                textFields[i].setText(players.getName(i));
            }
            
            final int index = i;
            textFields[i].addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    validateNames();
                    textFields[index].selectAll();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    validateNames();
                }   
            });
        }
        txtPlayer1.requestFocus();
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

        pnlNames = new javax.swing.JPanel();
        lblPlayer1 = new javax.swing.JLabel();
        lblPlayer2 = new javax.swing.JLabel();
        lblPlayer3 = new javax.swing.JLabel();
        lblPlayer4 = new javax.swing.JLabel();
        txtPlayer1 = new javax.swing.JTextField();
        txtPlayer2 = new javax.swing.JTextField();
        txtPlayer3 = new javax.swing.JTextField();
        txtPlayer4 = new javax.swing.JTextField();
        lblErrorMessage = new javax.swing.JLabel();
        pnlSaveSettings = new javax.swing.JPanel();
        chkSaveSettings = new javax.swing.JCheckBox();
        pnlButtons = new javax.swing.JPanel();
        btnOk = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlNames.setLayout(new java.awt.GridBagLayout());

        lblPlayer1.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        lblPlayer1.setText("Enter Player 1 Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        pnlNames.add(lblPlayer1, gridBagConstraints);

        lblPlayer2.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        lblPlayer2.setText("Enter Player 2 Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        pnlNames.add(lblPlayer2, gridBagConstraints);

        lblPlayer3.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        lblPlayer3.setText("Enter Player 3 Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        pnlNames.add(lblPlayer3, gridBagConstraints);

        lblPlayer4.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        lblPlayer4.setText("Enter Player 4 Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        pnlNames.add(lblPlayer4, gridBagConstraints);

        txtPlayer1.setColumns(20);
        txtPlayer1.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        pnlNames.add(txtPlayer1, new java.awt.GridBagConstraints());

        txtPlayer2.setColumns(20);
        txtPlayer2.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlNames.add(txtPlayer2, gridBagConstraints);

        txtPlayer3.setColumns(20);
        txtPlayer3.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlNames.add(txtPlayer3, gridBagConstraints);

        txtPlayer4.setColumns(20);
        txtPlayer4.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlNames.add(txtPlayer4, gridBagConstraints);

        lblErrorMessage.setFont(new java.awt.Font("Dialog", 1, 13)); // NOI18N
        lblErrorMessage.setForeground(new java.awt.Color(153, 0, 0));
        lblErrorMessage.setText(LONG_NAME_MESSAGE);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 0, 0);
        pnlNames.add(lblErrorMessage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        getContentPane().add(pnlNames, gridBagConstraints);

        pnlSaveSettings.setLayout(new java.awt.GridBagLayout());

        chkSaveSettings.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        chkSaveSettings.setSelected(true);
        chkSaveSettings.setText("Make these the default players");
        chkSaveSettings.setIconTextGap(8);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        pnlSaveSettings.add(chkSaveSettings, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(8, 16, 0, 12);
        getContentPane().add(pnlSaveSettings, gridBagConstraints);

        pnlButtons.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        btnOk.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        btnOk.setText("OK");
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });
        pnlButtons.add(btnOk);

        btnClear.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        btnClear.setText("Clear All");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });
        pnlButtons.add(btnClear);

        btnCancel.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        pnlButtons.add(btnCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(30, 12, 12, 12);
        getContentPane().add(pnlButtons, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        for (JTextField textField : textFields) {
            textField.setText("");
        }
        txtPlayer1.requestFocus();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        int nameCount = 0;
        for (JTextField textField : textFields) {
            if (!validateName(textField)) {
                return;  // abort - user must fix errors
            } else if (!textField.getText().isEmpty()) {
                ++nameCount;
            }
        }
        if (nameCount > 0) {
            String[] names = new String[nameCount];
            int i = 0;
            for (JTextField textField : textFields) {
                if (!textField.getText().isEmpty()) {
                    names[i++] = textField.getText();
                }
            }
            if (players.set(names) && chkSaveSettings.isSelected()) {
                Players.savePlayers(players);
            }

            dialogListener.onDialogChangesAccepted(NEW_NAMES, players);
        }
        dispose();
    }//GEN-LAST:event_btnOkActionPerformed

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
            java.util.logging.Logger.getLogger(PlayerNamesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PlayerNamesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PlayerNamesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PlayerNamesDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run() {
                PlayerNamesDialog dialog = new PlayerNamesDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnOk;
    private javax.swing.JCheckBox chkSaveSettings;
    private javax.swing.JLabel lblErrorMessage;
    private javax.swing.JLabel lblPlayer1;
    private javax.swing.JLabel lblPlayer2;
    private javax.swing.JLabel lblPlayer3;
    private javax.swing.JLabel lblPlayer4;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlNames;
    private javax.swing.JPanel pnlSaveSettings;
    private javax.swing.JTextField txtPlayer1;
    private javax.swing.JTextField txtPlayer2;
    private javax.swing.JTextField txtPlayer3;
    private javax.swing.JTextField txtPlayer4;
    // End of variables declaration//GEN-END:variables
}
