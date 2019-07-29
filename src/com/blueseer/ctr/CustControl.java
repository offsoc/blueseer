
package com.blueseer.ctr;

import com.blueseer.ord.*;
import com.blueseer.utl.BlueSeerUtils;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import bsmf.MainFrame;
/**
 *
 * @author vaughnte
 */


public class CustControl extends javax.swing.JPanel {

    /**
     * Creates new form ClockControl
     */
    public CustControl() {
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
                    res = st.executeQuery("SELECT * FROM  cm_ctrl ;");
                    while (res.next()) {
                        i++;
                        cbautocust.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("cmc_autocust")));
                    }
           
            }
            catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to retrieve cm_ctrl");
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
        cbautocust = new javax.swing.JCheckBox();
        btupdate = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Customer Control"));

        cbautocust.setText("Auto Customer ID?");
        cbautocust.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbautocustActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btupdate)
                    .addComponent(cbautocust))
                .addGap(25, 25, 25))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(cbautocust, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btupdate)
                .addContainerGap(26, Short.MAX_VALUE))
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
                String autocust = "";
             
                
                if ( cbautocust.isSelected() ) {
                autocust = "1";    
                } else {
                    autocust = "0";
                }
                
               
                
                res = st.executeQuery("SELECT *  FROM  cm_ctrl ;");
                    while (res.next()) {
                        i++;
                    }
                if (i == 0) {
                    
                    st.executeUpdate("insert into cm_ctrl (cmc_autocust ) values (" + "'" + autocust + "'" + 
                                    ")" + ";");              
                          bsmf.MainFrame.show("Inserting Defaults");
                } else {
                    st.executeUpdate("update cm_ctrl set " 
                            + " cmc_autocust = " + "'" + autocust + "'" +        
                            ";");   
                    bsmf.MainFrame.show("Updated Defaults");
                }
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Problem updating cm_ctrl");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void cbautocustActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbautocustActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbautocustActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbautocust;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
