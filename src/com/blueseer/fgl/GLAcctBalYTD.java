/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.blueseer.fgl;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;

/**
 *
 * @author vaughnte
 */
public class GLAcctBalYTD extends javax.swing.JPanel {
 
    /**
     * Creates new form ScrapReportPanel
     */
    
    
        class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
            if (col == 2)       
                return Double.class;  
            else return String.class;  //other columns accept String values  
        }  
      
        @Override  
          public boolean isCellEditable(int row, int col) {  
            if (col == 0)       //first column will be uneditable  
                return false;  
            else return true;  
          }  
        }    


    
    
    public GLAcctBalYTD() {
        initComponents();
    }

    public void initvars(String arg) {
         
         
         
          if (ddacctfrom.getItemCount() == 0) {
        ArrayList myacct = OVData.getGLAcctList();
        for (int i = 0; i < myacct.size(); i++) {
            ddacctfrom.addItem(myacct.get(i));
            ddacctto.addItem(myacct.get(i));
        }
        } else {
            ddacctfrom.setSelectedIndex(0);
            ddacctto.setSelectedIndex(ddacctto.getItemCount());
        }
         
       
          
          
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btRun = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablescrap = new javax.swing.JTable();
        labelcount = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        labeldollar = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ddacctfrom = new javax.swing.JComboBox();
        ddacctto = new javax.swing.JComboBox();
        cbzero = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Account Balance YTD"));

        btRun.setText("Run");
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel1.setText("From Acct");

        jLabel4.setText("To Acct");

        tablescrap.setAutoCreateRowSorter(true);
        tablescrap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tablescrap);

        labelcount.setText("0");

        jLabel7.setText("Count");

        labeldollar.setBackground(new java.awt.Color(195, 129, 129));
        labeldollar.setText("0");

        jLabel9.setText("$");

        cbzero.setText("Include Zero Balances?");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 965, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ddacctfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddacctto, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cbzero)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labeldollar, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btRun)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(ddacctfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddacctto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cbzero))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(labeldollar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(26, 26, 26)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRunActionPerformed
   
    
try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                ResultSet res = null;

                int qty = 0;
                double dol = 0;
                DecimalFormat df = new DecimalFormat("#0.00");
                int i = 0;
               
               
                   
                MyTableModel mymodel = new MyTableModel(new Object[][]{},
                        new String[]{"Acct", "Description", "Amt"});
                tablescrap.setModel(mymodel);
                TableColumnModel tcm = tablescrap.getColumnModel();
                tcm.getColumn(2).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());
               
                 DateFormat dfdate = new SimpleDateFormat("yyyy");
                 java.util.Date now = new java.util.Date();
                 String year = dfdate.format(now);
                 
                double amt = 0;
                String acct = "";
                String acctdesc = "";
                
                
                 
               res = st.executeQuery("select acb_acct, ac_desc, sum(acb_amt) as sum from acb_mstr " +
                        " inner join ac_mstr on ac_id = acb_acct " +
                        "where acb_year = " +
                        "'" + year + "'" +
                        " and acb_acct >= " + "'" + ddacctfrom.getSelectedItem().toString() + "'" +
                        " and acb_acct <= " + "'" + ddacctto.getSelectedItem().toString() + "'" +
                        " group by acb_acct " +
                        ";");
                while (res.next()) {
                   
                   amt = res.getDouble("sum");
                   acct = res.getString("acb_acct");
                   acctdesc = (res.getString("ac_desc") == null) ? "" : res.getString("ac_desc");
                   dol += amt;
                 if (! cbzero.isSelected() && amt == 0)
                     continue;
                 
                  i++;
                 mymodel.addRow(new Object[]{acct, acctdesc, amt });
                               
                }
                
                labeldollar.setText(String.valueOf(df.format(dol)));
                labelcount.setText(String.valueOf(i));
                
            } catch (SQLException s) {
                JOptionPane.showMessageDialog(mydialog, "Cannot execute sql query for Acct Balance Summary Report");
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       
    }//GEN-LAST:event_btRunActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JCheckBox cbzero;
    private javax.swing.JComboBox ddacctfrom;
    private javax.swing.JComboBox ddacctto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelcount;
    private javax.swing.JLabel labeldollar;
    private javax.swing.JTable tablescrap;
    // End of variables declaration//GEN-END:variables
}
