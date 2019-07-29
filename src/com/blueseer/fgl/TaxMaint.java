
package com.blueseer.fgl;

import bsmf.MainFrame;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.backgroundcolor;
import static bsmf.MainFrame.backgroundpanel;
import static bsmf.MainFrame.reinitpanels;
import java.awt.Color;
import java.awt.GradientPaint;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author vaughnte
 */


public class TaxMaint extends javax.swing.JPanel {

    
     javax.swing.table.DefaultTableModel taxmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                "Element", "Percent", "Type", "Enabled"
            });
    
    /**
     * Creates new form ClockControl
     */
    public TaxMaint() {
        initComponents();
    }

    
     public void getTax(String code) {
         try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                
                int i = 0;
                    res = st.executeQuery("SELECT * FROM  tax_mstr where tax_code = " + "'" + code + "'" + ";");
                    while (res.next()) {
                        i++;
                        tbdesc.setText(res.getString("tax_desc"));
                        tbtaxcode.setText(res.getString("tax_code"));
                    }
                    res = st.executeQuery("SELECT * FROM  taxd_mstr where " +
                            " taxd_parentcode = " + "'" + code + "'" + ";");
                    while (res.next()) {
                     taxmodel.addRow(new Object[]{res.getString("taxd_desc"), res.getString("taxd_percent"), res.getString("taxd_type"), res.getBoolean("taxd_enabled")});   
                    }
           
                    if (i > 0) {
                        enableAll();
                        btbrowse.setEnabled(false);
                        btnew.setEnabled(false);
                        btadd.setEnabled(false);
                        tbtaxcode.setEnabled(false);
                    }
                    
            }
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve task master record");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
     }
    
     public void clearAll() {
           taxmodel.setRowCount(0);
           tabletax.setModel(taxmodel);
           tbtaxcode.setText("");
           tbdesc.setText("");
           tbtaxelement.setText("");
           tbtaxpercent.setText("");
           cbenabled.setSelected(false);
           
     }
     
     public void enableAll() {
         tabletax.setEnabled(true);
         tbtaxcode.setEnabled(true);
         tbdesc.setEnabled(true);
         tbtaxelement.setEnabled(true);
         tbtaxpercent.setEnabled(true);
         cbenabled.setEnabled(true);
         btbrowse.setEnabled(true);
         btedit.setEnabled(true);
         btnew.setEnabled(true);
         btadd.setEnabled(true);
         btdelete.setEnabled(true);
         btaddelement.setEnabled(true);
         btdeleteelement.setEnabled(true);
         ddtype.setEnabled(true);
     }
    
        public void disableAll() {
         tabletax.setEnabled(false);
         tbtaxcode.setEnabled(false);
         tbdesc.setEnabled(false);
         tbtaxelement.setEnabled(false);
         cbenabled.setEnabled(false);
         btbrowse.setEnabled(false);
         btedit.setEnabled(false);
         btnew.setEnabled(false);
         btadd.setEnabled(false);
         tbtaxpercent.setEnabled(false);
         btaddelement.setEnabled(false);
         btdeleteelement.setEnabled(false);
         btdelete.setEnabled(false);
         ddtype.setEnabled(false);
     }
     
    public void initvars(String arg) {
          
           clearAll();
           disableAll();
           
          
           
           if (! arg.isEmpty()) {
             getTax(arg);
           } else {
               disableAll();
               btbrowse.setEnabled(true);
               btnew.setEnabled(true);
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
        btedit = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabletax = new javax.swing.JTable();
        cbenabled = new javax.swing.JCheckBox();
        btdeleteelement = new javax.swing.JButton();
        tbtaxelement = new javax.swing.JTextField();
        btaddelement = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        tbtaxpercent = new javax.swing.JTextField();
        ddtype = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnew = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tbtaxcode = new javax.swing.JTextField();
        tbdesc = new javax.swing.JTextField();
        btbrowse = new javax.swing.JButton();
        btadd = new javax.swing.JButton();
        btdelete = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Tax Maintenance"));

        btedit.setText("Edit");
        btedit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bteditActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Tax Elements Add/Delete"));

        jLabel3.setText("Tax Element");

        tabletax.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tabletax);

        cbenabled.setText("Enabled?");

        btdeleteelement.setText("Delete");
        btdeleteelement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteelementActionPerformed(evt);
            }
        });

        btaddelement.setText("Add");
        btaddelement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddelementActionPerformed(evt);
            }
        });

        jLabel4.setText("Percent");

        ddtype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "OTHER", "FEDERAL", "STATE", "LOCAL" }));

        jLabel7.setText("Type");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbtaxelement)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(tbtaxpercent, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbenabled)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btaddelement)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btdeleteelement)
                        .addGap(21, 21, 21))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtaxelement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbenabled)
                    .addComponent(jLabel4)
                    .addComponent(tbtaxpercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btaddelement)
                        .addComponent(btdeleteelement))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Master Tax Code"));

        btnew.setText("New");
        btnew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnewActionPerformed(evt);
            }
        });

        jLabel5.setText("Tax Code");

        jLabel6.setText("Desc");

        btbrowse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btbrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btbrowseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(tbtaxcode, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btbrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnew)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(tbdesc))
                .addGap(38, 38, 38))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(tbtaxcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btbrowse)
                    .addComponent(btnew))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btadd.setText("Add");
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        btdelete.setText("Delete");
        btdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btdelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btedit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btadd)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btedit)
                    .addComponent(btadd)
                    .addComponent(btdelete))
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void bteditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bteditActionPerformed
        
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                
                int i = 0;
                    
                       st.executeUpdate("update tax_mstr set " + 
                           "tax_desc = " + "'" + tbdesc.getText() + "'" + 
                           " where tax_code = " + "'" + tbtaxcode.getText() + "'" +
                             ";");     
                
               //  now lets delete all stored actions of this master task...then add back from table
                st.executeUpdate("delete from taxd_mstr where taxd_parentcode = " + "'" + tbtaxcode.getText() + "'" + ";");
                       
                 for (int j = 0; j < tabletax.getRowCount(); j++) {
                st.executeUpdate("insert into taxd_mstr (taxd_parentcode, taxd_desc, taxd_percent, taxd_type, taxd_enabled ) values ( " 
                        + "'" + tbtaxcode.getText() + "'" + ","
                        + "'" + tabletax.getValueAt(j, 0).toString() + "'" + ","
                        + "'" + tabletax.getValueAt(j, 1).toString() + "'" + ","
                        + "'" + tabletax.getValueAt(j, 2).toString() + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(Boolean.valueOf(tabletax.getValueAt(j, 3).toString())) + "'" 
                        + " );" );
                 }
              
                 bsmf.MainFrame.show("Updated Tax Master");
                 initvars("");
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem updating tax master");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       
    }//GEN-LAST:event_bteditActionPerformed

    private void btnewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnewActionPerformed
      
      tbtaxcode.setText("");
      tbtaxcode.requestFocus();
      enableAll();
      btbrowse.setEnabled(false);
      btedit.setEnabled(false);
      btnew.setEnabled(false);
     
      
    }//GEN-LAST:event_btnewActionPerformed

    private void btaddelementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddelementActionPerformed
        
        Pattern p = Pattern.compile("^[0-9]\\d*(\\.\\d+)?$");
        Matcher m = p.matcher(tbtaxpercent.getText());
        if (!m.find() || tbtaxpercent.getText() == null) {
            bsmf.MainFrame.show("Invalid Percent Format");
            return;
        }
        if (Double.valueOf(tbtaxpercent.getText()) == 0) {
            bsmf.MainFrame.show("Value cannot be zero");
            return;
        }
        
        taxmodel.addRow(new Object[]{ tbtaxelement.getText(), tbtaxpercent.getText(), ddtype.getSelectedItem().toString(), String.valueOf(BlueSeerUtils.boolToInt(cbenabled.isSelected())) });
    }//GEN-LAST:event_btaddelementActionPerformed

    private void btdeleteelementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteelementActionPerformed
       int[] rows = tabletax.getSelectedRows();
        for (int i : rows) {
            bsmf.MainFrame.show("Removing row " + i);
            ((javax.swing.table.DefaultTableModel) tabletax.getModel()).removeRow(i);
            
        }
    }//GEN-LAST:event_btdeleteelementActionPerformed

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
            
        if (tbtaxcode.getText().isEmpty()) {
            bsmf.MainFrame.show("Must enter a tax code");
            return;
        }
        
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
          
                int i = 0;
                    
                    st.executeUpdate("insert into tax_mstr (tax_code, tax_desc) values (" + 
                            "'" + tbtaxcode.getText() + "'" + "," +
                            "'" + tbdesc.getText() + "'"  + 
                            ")" + ";");     
                
              
                 for (int j = 0; j < tabletax.getRowCount(); j++) {
                st.executeUpdate("insert into taxd_mstr (taxd_parentcode, taxd_desc, taxd_percent, taxd_type, taxd_enabled ) values ( " 
                        + "'" + tbtaxcode.getText() + "'" + ","
                        + "'" + tabletax.getValueAt(j, 0).toString() + "'" + ","
                        + "'" + tabletax.getValueAt(j, 1).toString() + "'" + ","
                        + "'" + tabletax.getValueAt(j, 2).toString() + "'" + ","
                        + "'" + BlueSeerUtils.boolToInt(Boolean.valueOf(tabletax.getValueAt(j, 3).toString())) + "'" 
                        + " );" );
                 }
              
                bsmf.MainFrame.show("Added Master Tax Record");
                 initvars("");
                 
            } catch (SQLException s) {
                MainFrame.bslog(s);                  
                bsmf.MainFrame.show("Problem adding master tax");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btaddActionPerformed

    private void btbrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btbrowseActionPerformed
        reinitpanels("BrowseUtil", true, "taxmaint,tax_code");
    }//GEN-LAST:event_btbrowseActionPerformed

    private void btdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteActionPerformed
        
        
        
        boolean proceed = bsmf.MainFrame.warn("Are you sure?");
        if (proceed) {
            try {

                Class.forName(bsmf.MainFrame.driver).newInstance();
                bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
                ResultSet res = null;
                try {
                    Statement st = bsmf.MainFrame.con.createStatement();
                    int k = 0;
                    res = st.executeQuery("SELECT cm_code FROM  cm_mstr where cm_tax_code = " + "'" + tbtaxcode.getText() + "'" + ";");
                    while (res.next()) {
                        k++;
                    }
                    if (k > 0) {
                        bsmf.MainFrame.show("Cannot delete tax code until removed from all customers");
                        return;
                    }
                    
                    int i = st.executeUpdate("delete from tax_mstr where tax_code = " + "'" + tbtaxcode.getText() + "'" + ";");
                    int j = st.executeUpdate("delete from taxd_mstr where taxd_parentcode = " + "'" + tbtaxcode.getText() + "'" + ";");
                    if (i > 0 && j > 0) {
                        bsmf.MainFrame.show("deleted tax code " + tbtaxcode.getText());
                        initvars("");
                    }
                } catch (SQLException s) {
                    MainFrame.bslog(s);
                    bsmf.MainFrame.show("Unable to Delete Tax Code Record");
                }
                bsmf.MainFrame.con.close();
            } catch (Exception e) {
                MainFrame.bslog(e);
            }
        }
    }//GEN-LAST:event_btdeleteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btadd;
    private javax.swing.JButton btaddelement;
    private javax.swing.JButton btbrowse;
    private javax.swing.JButton btdelete;
    private javax.swing.JButton btdeleteelement;
    private javax.swing.JButton btedit;
    private javax.swing.JButton btnew;
    private javax.swing.JCheckBox cbenabled;
    private javax.swing.JComboBox<String> ddtype;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabletax;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbtaxcode;
    private javax.swing.JTextField tbtaxelement;
    private javax.swing.JTextField tbtaxpercent;
    // End of variables declaration//GEN-END:variables
}
