/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn 

All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.blueseer.edi;

import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.EDData.getEDIPartnerDocIDs;
import static com.blueseer.utl.EDData.getEDIPartnerDocSet;
import static com.blueseer.utl.EDData.getEDIPartners;
import com.blueseer.utl.OVData;
import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 *
 * @author vaughnte
 */
public class EDIControl extends javax.swing.JPanel {

    /**
     * Creates new form EDIControlPanel
     */
    boolean isLoad = false;
    
    public EDIControl() {
        initComponents();
        setLanguageTags(this);
    }

    public void setLanguageTags(Object myobj) {
       JPanel panel = null;
        JTabbedPane tabpane = null;
        JScrollPane scrollpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else if (myobj instanceof JScrollPane) {
           scrollpane = (JScrollPane) myobj;    
        } else {
            return;
        }
       Component[] components = panel.getComponents();
       for (Component component : components) {
           if (component instanceof JPanel) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".panel." + component.getName())) {
                       ((JPanel) component).setBorder(BorderFactory.createTitledBorder(tags.getString(this.getClass().getSimpleName() +".panel." + component.getName())));
                    } 
                    setLanguageTags((JPanel) component);
                }
                if (component instanceof JLabel ) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JLabel) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    }
                }
                if (component instanceof JButton ) {
                    if (tags.containsKey("global.button." + component.getName())) {
                       ((JButton) component).setText(tags.getString("global.button." + component.getName()));
                    }
                }
                if (component instanceof JCheckBox) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JCheckBox) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    } 
                }
                if (component instanceof JRadioButton) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JRadioButton) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    } 
                }
       }
    }
    
    
    public void copyPartnerDoc(String fromid, String fromdoc, String fromsndgs, String fromrcvgs, String toid, String tosndgs, String torcvgs) {
         try {
            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                int i = 0;
                res = st.executeQuery("SELECT edi_id, edi_doc FROM  edi_mstr where " +
                                          " edi_id = " + "'" + toid + "'" +
                                          " AND edi_doc = " + "'" + fromdoc + "'" + 
                                          " AND edi_sndgs = " + "'" + tosndgs + "'" +
                                          " AND edi_rcvgs = " + "'" + torcvgs + "'" +        
                                          ";");
                    while (res.next()) {
                        i++;
                    }
                
                    if (i == 0) {
                    st.executeUpdate("insert into edi_mstr (edi_id, edi_doc, edi_sndisa, edi_sndq, edi_sndgs, edi_map, edi_eledelim, edi_segdelim, edi_subdelim, edi_fileprefix, edi_filesuffix, edi_filepath, edi_version, edi_rcvisa, edi_rcvgs, edi_rcvq, edi_supcode, edi_doctypeout, edi_filetypeout, edi_ifs, edi_ofs, edi_fa_required ) " + 
                            " select  " + "'" + toid + "'" + ", em.edi_doc, em.edi_sndisa, em.edi_sndq, " + "'" + tosndgs + "'" + ", em.edi_map, em.edi_eledelim, em.edi_segdelim, em.edi_subdelim, em.edi_fileprefix, em.edi_filesuffix, em.edi_filepath, em.edi_version, em.edi_rcvisa, " + "'" + torcvgs + "'" + ", em.edi_rcvq, em.edi_supcode, em.edi_doctypeout, em.edi_filetypeout, em.edi_ifs, em.edi_ofs, em.edi_fa_required from edi_mstr em " +
                            " where em.edi_id = " + "'" + fromid + "'" +
                            " and em.edi_doc = " + "'" + fromdoc + "'" +
                            " and em.edi_sndgs = " + "'" + fromsndgs + "'" +
                            " and em.edi_rcvgs = " + "'" + fromrcvgs + "'");
                    bsmf.MainFrame.show(getMessageTag(1125));
                    } else {
                    bsmf.MainFrame.show(getMessageTag(1014));    
                    }
                    
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }
    
    public void getdefault() {
        
        try {

            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                int i = 0;
                res = st.executeQuery("select * from edi_ctrl;");
                while (res.next()) {
                    i++;
                    tboutdir.setText(res.getString("edic_outdir"));
                    tbindir.setText(res.getString("edic_indir"));
                    tboutscript.setText(res.getString("edic_outftp"));
                    tbinarch.setText(res.getString("edic_inarch"));
                    tboutarch.setText(res.getString("edic_outarch"));
                    tbbatch.setText(res.getString("edic_batch"));
                    tbstructure.setText(res.getString("edic_structure"));
                    tberrordir.setText(res.getString("edic_errordir")); 
                    cbarchive.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("edic_archyesno")));
                    cbdelete.setSelected(BlueSeerUtils.ConvertStringToBool(res.getString("edic_delete")));
                }
               
                if (i == 0)
                    bsmf.MainFrame.show(getMessageTag(1001));

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }
    
    public void initvars(String[] key) {
        getdefault();
        
        isLoad = true;
        ddcode.removeAllItems();
        ArrayList<String> mylist = getEDIPartnerDocIDs(); 
        for (int i = 0; i < mylist.size(); i++) {
            ddcode.addItem(mylist.get(i));
        }
        
        ddtocode.removeAllItems();
        ArrayList<String> partners = getEDIPartners();
        for (int i = 0; i < partners.size(); i++) {
            ddtocode.addItem(partners.get(i));
        }
        
        isLoad = false;
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        tboutscript = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tboutdir = new javax.swing.JTextField();
        tbindir = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btupdate = new javax.swing.JButton();
        tbinarch = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tboutarch = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cbarchive = new javax.swing.JCheckBox();
        tbbatch = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cbdelete = new javax.swing.JCheckBox();
        tberrordir = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        tbstructure = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btcopy = new javax.swing.JButton();
        ddcode = new javax.swing.JComboBox<>();
        tbrcvgs = new javax.swing.JTextField();
        ddset = new javax.swing.JComboBox<>();
        tbsndgs = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        ddtocode = new javax.swing.JComboBox<>();
        btexport = new javax.swing.JButton();

        jLabel6.setText("jLabel6");

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("EDI Control Maintenance"));
        jPanel1.setName("panelmain"); // NOI18N

        jLabel2.setText("Default In Dir");
        jLabel2.setName("lblindir"); // NOI18N

        jLabel1.setText("Default Out Dir");
        jLabel1.setName("lbloutdir"); // NOI18N

        jLabel3.setText("Default Out Script");
        jLabel3.setName("lblscriptdir"); // NOI18N

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel4.setText("Inbound Archive");
        jLabel4.setName("lblarchindir"); // NOI18N

        jLabel5.setText("Outbound Archive");
        jLabel5.setName("lblarchoutdir"); // NOI18N

        cbarchive.setText("Archive?");
        cbarchive.setName("cbarchive"); // NOI18N

        jLabel7.setText("Batch Directory");
        jLabel7.setName("lblbatchdir"); // NOI18N

        cbdelete.setText("Delete (no archive)");
        cbdelete.setName("cbdelete"); // NOI18N

        jLabel8.setText("Error Directory");
        jLabel8.setName("lblerrordir"); // NOI18N

        jLabel12.setText("IFS / OFS Dir");
        jLabel12.setName("lblfsdir"); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Copy Partner/Doc Function"));

        btcopy.setText("copy");
        btcopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcopyActionPerformed(evt);
            }
        });

        ddcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcodeActionPerformed(evt);
            }
        });

        jLabel9.setText("Code");

        jLabel10.setText("code+doc+sndgs+rcvgs");

        jLabel14.setText("To: GS Send");

        jLabel15.setText("To: GS Recv");

        jLabel11.setText("To: code");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddset, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tbrcvgs, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                        .addGap(24, 24, 24)
                        .addComponent(btcopy)
                        .addGap(78, 78, 78))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(tbsndgs, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(ddtocode, javax.swing.GroupLayout.Alignment.LEADING, 0, 141, Short.MAX_VALUE))
                            .addComponent(ddcode, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(ddtocode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsndgs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbrcvgs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btcopy)
                    .addComponent(jLabel15))
                .addContainerGap())
        );

        btexport.setText("Export Master Data");
        btexport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tboutscript, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbindir, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tboutdir, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbinarch, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btexport)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(tbstructure)
                                .addComponent(tberrordir)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(cbdelete)
                                        .addComponent(cbarchive))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(btupdate))
                                .addComponent(tboutarch)
                                .addComponent(tbbatch)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(12, 12, 12))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tboutdir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbindir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tboutscript, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbinarch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tboutarch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbbatch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbstructure, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tberrordir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbarchive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbdelete)
                    .addComponent(btupdate))
                .addGap(43, 43, 43)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btexport)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
        try {

            Connection con = DriverManager.getConnection(url + db, user, pass);
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                boolean proceed = true;
                int i = 0;
                
                res = st.executeQuery("SELECT *  FROM  edi_ctrl ;");
                    while (res.next()) {
                        i++;
                    }
                if (i == 0) {
                    
                    st.executeUpdate("insert into edi_ctrl values (" + "'" + tboutdir.getText().replace("\\","\\\\") + "'" + ","
                            + "'" + tbindir.getText().replace("\\","\\\\") + "'" + "," 
                            + "'" + tboutscript.getText().replace("\\","\\\\") + "'" + "," 
                            + "'" + tbinarch.getText().replace("\\","\\\\") + "'" + "," 
                            + "'" + tboutarch.getText().replace("\\","\\\\") + "'" + ","
                            + "'" + tbbatch.getText().replace("\\","\\\\") + "'" + "," 
                            + "'" + tbstructure.getText().replace("\\","\\\\") + "'" + ","          
                            + "'" + tberrordir.getText().replace("\\","\\\\") + "'" + ","         
                            + "'" + BlueSeerUtils.boolToInt(cbarchive.isSelected()) + "'" + ","      
                            + "'" + BlueSeerUtils.boolToInt(cbdelete.isSelected()) + "'"        
                            + ") ;");              
                          bsmf.MainFrame.show(getMessageTag(1007));
                } else {
                    st.executeUpdate("update edi_ctrl set " 
                            + "edic_outdir = " + "'" + tboutdir.getText().replace("\\","\\\\") + "'" + ","
                            + "edic_indir = " + "'" + tbindir.getText().replace("\\","\\\\") + "'" + "," 
                            + "edic_inarch = " + "'" + tbinarch.getText().replace("\\","\\\\") + "'" + "," 
                            + "edic_outarch = " + "'" + tboutarch.getText().replace("\\","\\\\") + "'" + "," 
                            + "edic_batch = " + "'" + tbbatch.getText().replace("\\","\\\\") + "'" + ","   
                            + "edic_structure = " + "'" + tbstructure.getText().replace("\\","\\\\") + "'" + ","           
                            + "edic_errordir = " + "'" + tberrordir.getText().replace("\\","\\\\") + "'" + ","            
                            + "edic_delete = " + "'" + BlueSeerUtils.boolToInt(cbdelete.isSelected()) + "'" + ","         
                            + "edic_archyesno = " + "'" + BlueSeerUtils.boolToInt(cbarchive.isSelected()) + "'" + "," 
                            + "edic_outftp = " + "'" + tboutscript.getText() + "'" + ";");   
                    bsmf.MainFrame.show(getMessageTag(1008));
                }
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void ddcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcodeActionPerformed
         if (ddcode.getSelectedItem() != null && ! isLoad) {
            ddset.removeAllItems();
            ArrayList<String[]> mylist = getEDIPartnerDocSet(ddcode.getSelectedItem().toString());
            for (int i = 0; i < mylist.size(); i++) {
                String x = String.join("+", mylist.get(i));
                ddset.addItem(x);
            }
         }
    }//GEN-LAST:event_ddcodeActionPerformed

    private void btcopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcopyActionPerformed
        String[] x = ddset.getSelectedItem().toString().split("\\+");
        copyPartnerDoc(x[0], x[1], x[2], x[3], ddtocode.getSelectedItem().toString(), tbsndgs.getText(), tbrcvgs.getText());
    }//GEN-LAST:event_btcopyActionPerformed

    private void btexportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btexportActionPerformed
        
        OVData.exportTable("edi_mstr", "edimstr.txt");
        OVData.exportTable("edi_doc", "edidoc.txt");
        OVData.exportTable("edi_docdet", "edidocdet.txt");
        OVData.exportTable("edi_stds", "edistds.txt");
        OVData.exportTable("edp_partner", "edppartner.txt");
        OVData.exportTable("edpd_partner", "edpdpartner.txt");
        bsmf.MainFrame.show("Files exported to temp dir");
        
    }//GEN-LAST:event_btexportActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btcopy;
    private javax.swing.JButton btexport;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbarchive;
    private javax.swing.JCheckBox cbdelete;
    private javax.swing.JComboBox<String> ddcode;
    private javax.swing.JComboBox<String> ddset;
    private javax.swing.JComboBox<String> ddtocode;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField tbbatch;
    private javax.swing.JTextField tberrordir;
    private javax.swing.JTextField tbinarch;
    private javax.swing.JTextField tbindir;
    private javax.swing.JTextField tboutarch;
    private javax.swing.JTextField tboutdir;
    private javax.swing.JTextField tboutscript;
    private javax.swing.JTextField tbrcvgs;
    private javax.swing.JTextField tbsndgs;
    private javax.swing.JTextField tbstructure;
    // End of variables declaration//GEN-END:variables
}
