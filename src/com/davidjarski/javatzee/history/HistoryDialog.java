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

import com.davidjarski.javatzee.IO.HistoryIO;
import com.davidjarski.javatzee.IO.Resources;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

public class HistoryDialog extends javax.swing.JDialog
{

    private static final int CELL_PADDING = 8;

    private GameRecordTableModel tableModel;
    private boolean headerWidthsSet;
    private boolean atMaxHeight;
    private int maxHeight;

    
    public HistoryDialog(java.awt.Frame parent, boolean modal, final GameRecordTableModel tableModel) {
        super(parent, modal);
        this.tableModel = tableModel;
        initComponents();

        setIconImage(new ImageIcon(getClass().getResource(Resources.FAVICON_PATH)).getImage());
        setRenderers();
        setColumnWidths();
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        maxHeight = gameHistoryPanel.getScorePanelHeight();
        setScrollPaneSize();
        
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            int changeCount = 0;
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                int row = table.getSelectedRow();
                if (row >= 0 && row < table.getRowCount()) {
                    gameHistoryPanel.setGameRecord(
                            tableModel.getGameRecordAt(table.convertRowIndexToModel(row)));
                }
            }
        });
        table.setRowSelectionInterval(0, 0);
                   
        setResizable(false);
        pack();
    }

    public void updateTable() {
        tableModel.fireTableDataChanged();
        setColumnWidths();
        if (!atMaxHeight) {
            setScrollPaneSize();
        }
        table.setRowSelectionInterval(0, 0);
        pack();
    }
    
    public void updateTable(ArrayList<GameRecord> records) {
        tableModel.addGameRecords(records);
        updateTable();
    }
    
    @Override
    public void setLocationRelativeTo(java.awt.Component component) {
        super.setLocationRelativeTo(component);
        Point p = component.getLocation();
        int xOffset = (component.getWidth() - getWidth()) / 2;
        setLocation(p.x + xOffset, p.y + 50);
    }
    
        
    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        lblTableHeader.setText(title);
        
    }
    
    public void setSortingEnabled(boolean enabled) {
        if (enabled) {
            TableRowSorter sorter = new TableRowSorter<GameRecordTableModel>(tableModel);
            sorter.setComparator(GameRecordTableModel.TIME_COLUMN,
                    GameRecordTableModel.getComparator(GameRecordTableModel.SortOption.TIME));
            sorter.setComparator(GameRecordTableModel.DATE_COLUMN,
                    GameRecordTableModel.getComparator(GameRecordTableModel.SortOption.DATE_TIME));
            sorter.toggleSortOrder(GameRecordTableModel.DATE_COLUMN);
            sorter.toggleSortOrder(GameRecordTableModel.DATE_COLUMN);
            table.setRowSorter(sorter);
            table.setRowSelectionInterval(0, 0);
        } else {
            table.setRowSorter(null);
        }
    }
    
    private void setScrollPaneSize() {
        if (table.getPreferredSize().height < maxHeight) {
            table.setPreferredScrollableViewportSize(table.getPreferredSize());
        } else {
            atMaxHeight = true;
            table.setPreferredScrollableViewportSize(table.getPreferredSize());
            jScrollPane.setPreferredSize(new Dimension(jScrollPane.getViewport().getPreferredSize().width + 20, maxHeight));
        }
    }

    private void setRenderers() {

        // set the default cell renderer to display the cell with internal padding
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());

        table.getColumnModel().getColumn(GameRecordTableModel.DATE_COLUMN)
                .setCellRenderer(new DateRenderer());
        table.getColumnModel().getColumn(GameRecordTableModel.TIME_COLUMN)
                .setCellRenderer(new TimeRenderer());
    }

    private void setColumnWidths() {
        TableColumnModel columnModel = table.getColumnModel();
        
        for (int col = 0; col < columnModel.getColumnCount(); ++col) {
            int width = 0;
            // get the header width
            if (!headerWidthsSet) {
                Object value = table.getColumnModel().getColumn(col).getHeaderValue();
                width += table.getTableHeader().getDefaultRenderer()
                        .getTableCellRendererComponent(
                                table, value, false, false, 0, col)
                        .getPreferredSize().width;
            }
            // iterate through the rows and update the width if necessary
            for (int row = 0; row < table.getRowCount(); ++row) {
                TableCellRenderer renderer = table.getCellRenderer(row, col);
                Component component = table.prepareRenderer(renderer, row, col);
                width = Math.max(component.getPreferredSize().width, width);
            }
            
            width += CELL_PADDING * 2;
            if (!headerWidthsSet 
                    || columnModel.getColumn(col).getPreferredWidth() < width) {
                columnModel.getColumn(col).setPreferredWidth(width);
                
            }
            
        }
        headerWidthsSet = true;
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

        pnlHeader = new javax.swing.JPanel();
        lblTableHeader = new javax.swing.JLabel();
        lblSubHeader = new javax.swing.JLabel();
        pnlTable = new javax.swing.JPanel();
        jScrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        gameHistoryPanel = new com.davidjarski.javatzee.history.GameHistoryPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlHeader.setBackground(new java.awt.Color(255, 203, 20));
        pnlHeader.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlHeader.setLayout(new java.awt.GridBagLayout());

        lblTableHeader.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblTableHeader.setText("Game History");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        pnlHeader.add(lblTableHeader, gridBagConstraints);

        lblSubHeader.setText(" ");
        lblSubHeader.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        pnlHeader.add(lblSubHeader, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(pnlHeader, gridBagConstraints);

        pnlTable.setLayout(new javax.swing.BoxLayout(pnlTable, javax.swing.BoxLayout.Y_AXIS));

        jScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        table.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        table.setModel(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        jScrollPane.setViewportView(table);

        pnlTable.add(jScrollPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        getContentPane().add(pnlTable, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        getContentPane().add(gameHistoryPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(HistoryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HistoryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HistoryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HistoryDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run() {
                HistoryDialog dialog = new HistoryDialog(new javax.swing.JFrame(), true);
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
    
    private HistoryDialog(java.awt.Frame parent, boolean modal) {
        this(parent, modal, new GameRecordTableModel(HistoryIO.read()));
        this.setSortingEnabled(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.davidjarski.javatzee.history.GameHistoryPanel gameHistoryPanel;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JLabel lblSubHeader;
    private javax.swing.JLabel lblTableHeader;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlTable;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables

    private static class CustomTableCellRenderer extends DefaultTableCellRenderer
    {
        private final Border border = BorderFactory.createEmptyBorder(0, CELL_PADDING, 0, CELL_PADDING);
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setBorder(border);
            return this;
        }
    }
    
    static class DateRenderer extends DefaultTableCellRenderer {
        private final DateFormat formatter;

        public DateRenderer() {
            formatter = DateFormat.getDateInstance(DateFormat.MEDIUM);
            // use leading zeros for month and day, if those values are
            // expressed as a numbers
            try {
                SimpleDateFormat simpleFormatter = (SimpleDateFormat)formatter;
                String pattern = simpleFormatter.toPattern();
                if (!pattern.contains("dd")) {
                        pattern = pattern.replace("d", "dd");
                }
                if (!pattern.contains("MM")) {
                        pattern = pattern.replace("M", "MM");
                }
                simpleFormatter.applyPattern(pattern);
            } catch (ClassCastException ex) {
                // do nothing - the default pattern remains unchanged
            }
        }

        @Override
        public void setValue(Object value) {
            setText((value == null) ? "" : formatter.format(value));
        }
    }
    
    static class TimeRenderer extends DefaultTableCellRenderer {
        private final DateFormat formatter;
        private final Border border;
        
        public TimeRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
            border = BorderFactory.createEmptyBorder(0, 0, 0, CELL_PADDING * 2);
            formatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        }
        
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(border);
            return this;
        }
        
        @Override
        public void setValue(Object value) {
            setText((value == null) ? "" : formatter.format(value));
        }
    }

}
