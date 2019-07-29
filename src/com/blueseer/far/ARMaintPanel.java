/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.blueseer.far;

import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;

/**
 *
 * @author vaughnte
 */
public class ARMaintPanel extends javax.swing.JPanel {

    /**
     * Creates new form ARMaintPanel
     */
    public ARMaintPanel() {
       initComponents();
       this.setBorder(javax.swing.BorderFactory.
      createTitledBorder(null, "AR Maintenance", javax.swing.border.
      TitledBorder.CENTER, javax.swing.border.
      TitledBorder.DEFAULT_POSITION, null, java.awt.Color.white));;
       repaint();
    }

    
   public void initvars(String arg)
   {
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select cm_code from cm_mstr ;");
                while (res.next()) {
                    cbARMcust.addItem(res.getString("cm_code"));
                }
                res = st.executeQuery("select ac_id, ac_desc from ac_mstr order by ac_id;");
                while (res.next()) {
                    cbARMaccount.addItem(res.getString("ac_id") + " " + res.getString("ac_desc"));
                }

                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyyMMdd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
                tbARMdate.setText(clockdate);
            } catch (SQLException s) {
                JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "Sql code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
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
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        tbARMid = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        btARMNew = new javax.swing.JButton();
        cbARMtype = new javax.swing.JComboBox();
        jLabel59 = new javax.swing.JLabel();
        cbARMaccount = new javax.swing.JComboBox();
        tbARMref = new javax.swing.JTextField();
        btARMAdd = new javax.swing.JButton();
        btARMGet = new javax.swing.JButton();
        btARMUpdate = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        tbARMnbr = new javax.swing.JTextField();
        tbARMamt = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        cbARMcust = new javax.swing.JComboBox();
        jLabel58 = new javax.swing.JLabel();
        tbARMdate = new javax.swing.JTextField();

        setBackground(new java.awt.Color(0, 102, 204));
        setForeground(java.awt.Color.white);
        setName(""); // NOI18N

        jLabel54.setText("Inv/Chk");

        jLabel55.setText("Ref");

        jLabel57.setText("Type");

        jLabel56.setText("Date");

        btARMNew.setText("New");
        btARMNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btARMNewActionPerformed(evt);
            }
        });

        cbARMtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "I", "P", "M" }));
        cbARMtype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbARMtypeActionPerformed(evt);
            }
        });

        jLabel59.setText("Account");

        btARMAdd.setText("Add");
        btARMAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btARMAddActionPerformed(evt);
            }
        });

        btARMGet.setText("Get");
        btARMGet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btARMGetActionPerformed(evt);
            }
        });

        btARMUpdate.setText("Update");
        btARMUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btARMUpdateActionPerformed(evt);
            }
        });

        jLabel29.setText("ARMaint ID");

        jLabel53.setText("Cust ID");

        cbARMcust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbARMcustActionPerformed(evt);
            }
        });

        jLabel58.setText("Amt");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel54)
                    .addComponent(jLabel53)
                    .addComponent(jLabel55)
                    .addComponent(jLabel56)
                    .addComponent(jLabel57)
                    .addComponent(jLabel58)
                    .addComponent(jLabel29)
                    .addComponent(jLabel59))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbARMcust, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbARMnbr)
                            .addComponent(cbARMtype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbARMdate)
                            .addComponent(tbARMref)
                            .addComponent(tbARMid)
                            .addComponent(tbARMamt, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(72, 72, 72)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btARMNew)
                            .addComponent(btARMGet)
                            .addComponent(btARMUpdate)
                            .addComponent(btARMAdd)))
                    .addComponent(cbARMaccount, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(tbARMid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btARMNew))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(cbARMcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btARMGet))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbARMaccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel59))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(tbARMnbr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(tbARMdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel55)
                    .addComponent(tbARMref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel57)
                    .addComponent(cbARMtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btARMUpdate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58)
                    .addComponent(tbARMamt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btARMAdd))
                .addContainerGap())
        );

        add(jPanel1);

        getAccessibleContext().setAccessibleName("");
    }// </editor-fold>//GEN-END:initComponents

    private void cbARMcustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbARMcustActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbARMcustActionPerformed

    private void cbARMtypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbARMtypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbARMtypeActionPerformed

    private void btARMAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btARMAddActionPerformed
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                String arstatus = "O";
                String arid = null;
                double totamt = 0.00;
                int i = 0;

                if (proceed) {

                    if (cbARMtype.getSelectedItem().toString().equals("P")) {
                        res = st.executeQuery("SELECT ar_id, ar_nbr, ar_amt FROM  ar_mstr where ar_nbr = " + "'" + tbARMnbr.getText() + "'" + ";");
                        while (res.next()) {
                            i++;
                            arid = res.getString("ar_id");
                            totamt = totamt + Double.parseDouble(res.getString("ar_amt"));
                        }

                        if (totamt == Double.parseDouble(tbARMamt.getText())) {
                            arstatus = "C";
                        }

                        if (i > 0) {
                            st.executeUpdate("update ar_mstr set ar_status = " + "'" + arstatus.toString() + "'"
                                + " where ar_id = " + "'" + arid.toString() + "'"
                                + ";");
                        }
                        arstatus = "";
                    }

                    st.executeUpdate("insert into ar_mstr "
                        + "(ar_id, ar_cust, ar_nbr,"
                        + "ar_amt, ar_date, ar_type, ar_rmks, ar_ref, ar_acct, ar_status )"
                        + " values ( " + "'" + tbARMid.getText().toString() + "'" + ","
                        + "'" + cbARMcust.getSelectedItem().toString() + "'" + ","
                        + "'" + tbARMnbr.getText().toString() + "'" + ","
                        + "'" + tbARMamt.getText() + "'" + ","
                        + "'" + tbARMdate.getText().toString() + "'" + ","
                        + "'" + cbARMtype.getSelectedItem() + "'" + ","
                        + null + ","
                        + "'" + tbARMref.getText().toString() + "'" + ","
                        + "'" + cbARMaccount.getSelectedItem().toString().substring(0, 8) + "'" + ","
                        + "'" + arstatus.toString() + "'"
                        + ")"
                        + ";");

                    JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "Added AR Record");
                    //reinitapmvariables();
                    // btQualProbAdd.setEnabled(false);
                } // if proceed
            } catch (SQLException s) {
                JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "Sql code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btARMAddActionPerformed

    private void btARMUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btARMUpdateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btARMUpdateActionPerformed

    private void btARMGetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btARMGetActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btARMGetActionPerformed

    private void btARMNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btARMNewActionPerformed
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                res = st.executeQuery("select max(counter_id) as 'num' from counter where counter_name = 'ar';");
                while (res.next()) {

                    int nextnumber = Integer.valueOf(res.getString("num"));
                    nextnumber++;
                    OVData.updatecounter("ar", nextnumber);
                    tbARMid.setText(String.format("%d", nextnumber).toString());
                    tbARMid.setEnabled(false);
                    // jcbsupervisor_empmast.setSelectedIndex(0);
                }

                java.util.Date now = new java.util.Date();
                DateFormat dfdate = new SimpleDateFormat("yyyyMMdd");
                DateFormat dftime = new SimpleDateFormat("HH:mm:ss");
                String clockdate = dfdate.format(now);
                String clocktime = dftime.format(now);
                tbARMdate.setText(clockdate);
               
            } catch (SQLException s) {
                JOptionPane.showMessageDialog(bsmf.MainFrame.mydialog, "Sql code does not execute");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btARMNewActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btARMAdd;
    private javax.swing.JButton btARMGet;
    private javax.swing.JButton btARMNew;
    private javax.swing.JButton btARMUpdate;
    private javax.swing.JComboBox cbARMaccount;
    private javax.swing.JComboBox cbARMcust;
    private javax.swing.JComboBox cbARMtype;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tbARMamt;
    private javax.swing.JTextField tbARMdate;
    private javax.swing.JTextField tbARMid;
    private javax.swing.JTextField tbARMnbr;
    private javax.swing.JTextField tbARMref;
    // End of variables declaration//GEN-END:variables
}
