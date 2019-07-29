
package com.blueseer.inv;

import bsmf.MainFrame;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.backgroundcolor;
import static bsmf.MainFrame.backgroundpanel;
import java.awt.Color;
import java.awt.GradientPaint;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 *
 * @author vaughnte
 */


public class InventoryCtrl extends javax.swing.JPanel {

    /**
     * Creates new form ClockControl
     */
    public InventoryCtrl() {
        initComponents();
    }

    
     public void getcontrol() {
          
         try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                
                int i = 0;
                    res = st.executeQuery("SELECT * FROM  inv_ctrl;");
                    while (res.next()) {
                        i++;
                        cbmultiplan.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("planmultiscan")));
                        cbdemdtoplan.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("demdtoplan")));   
                        cbprintsubticket.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("printsubticket"))); 
                        cbautoitem.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("autoitem")));
                    }
           
            }
            catch (SQLException s) {
                bsmf.MainFrame.show("Unable to retrieve ov_ctrl");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
     }
    
    
    public void initvars(String arg) {
               getcontrol();
        
           
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
        btupdate = new javax.swing.JButton();
        cbdemdtoplan = new javax.swing.JCheckBox();
        cbmultiplan = new javax.swing.JCheckBox();
        cbprintsubticket = new javax.swing.JCheckBox();
        cbautoitem = new javax.swing.JCheckBox();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Inventory Control"));

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        cbdemdtoplan.setText("Demand To Plan");

        cbmultiplan.setText("MultiScan Plan Ticket?");

        cbprintsubticket.setText("Print Sub Ticket From Scan?");

        cbautoitem.setText("Auto Item Number Assignment?");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbdemdtoplan)
                            .addComponent(cbmultiplan)
                            .addComponent(cbprintsubticket)
                            .addComponent(cbautoitem)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(189, 189, 189)
                        .addComponent(btupdate)))
                .addGap(113, 113, 113))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(cbdemdtoplan)
                .addGap(18, 18, 18)
                .addComponent(cbmultiplan)
                .addGap(18, 18, 18)
                .addComponent(cbprintsubticket)
                .addGap(18, 18, 18)
                .addComponent(cbautoitem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(btupdate)
                .addGap(34, 34, 34))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                boolean proceed = true;
                int i = 0;
                String login = "";
                
             
                
                
                res = st.executeQuery("SELECT *  FROM  inv_ctrl ;");
                    while (res.next()) {
                        i++;
                    }
                if (i == 0) {
                    
                    st.executeUpdate("insert into inv_ctrl values (" + 
                            "'" + BlueSeerUtils.boolToInt(cbmultiplan.isSelected()) + "'" +  "," +
                            "'" + BlueSeerUtils.boolToInt(cbdemdtoplan.isSelected()) + "'" + "," +
                            "'" + BlueSeerUtils.boolToInt(cbprintsubticket.isSelected()) + "'" + "," +
                            "'" + BlueSeerUtils.boolToInt(cbautoitem.isSelected()) + "'" +
                            ")"  + ";");              
                          bsmf.MainFrame.show("Inserting Defaults");
                } else {
                    st.executeUpdate("update inv_ctrl set " +
                            " planmultiscan = " + "'" + BlueSeerUtils.boolToInt(cbmultiplan.isSelected()) + "'" + "," +
                            " printsubticket = " + "'" + BlueSeerUtils.boolToInt(cbprintsubticket.isSelected()) + "'" + "," +
                            " demdtoplan = " + "'" + BlueSeerUtils.boolToInt(cbdemdtoplan.isSelected()) + "'"  + "," + 
                            " autoitem = " + "'" + BlueSeerUtils.boolToInt(cbautoitem.isSelected()) + "'"  +
                            ";");   
                   
                    bsmf.MainFrame.show("Updated Defaults");
                   
                }
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem updating inv_ctrl");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbautoitem;
    private javax.swing.JCheckBox cbdemdtoplan;
    private javax.swing.JCheckBox cbmultiplan;
    private javax.swing.JCheckBox cbprintsubticket;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
