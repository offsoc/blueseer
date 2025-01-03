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
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.EDData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.EDData.getEDIStds;
import static com.blueseer.utl.EDData.updateEDIFileLogStatusManual;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableCellRenderer;
import jcifs.smb.SmbException;

/**
 *
 * @author vaughnte
 */
public class EDITransactionBrowse extends javax.swing.JPanel {
 
    
    javax.swing.table.DefaultTableModel docmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"Select", "IdxNbr", "ComKey", "SenderID", "ReceiverID", "TimeStamp", "InFileType", "InDocType", "InBatch", "Reference", "OutFileType", "OutDocType", "OutBatch", "InView", "OutView",  "Status"})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0 || col == 13 || col == 14 || col == 15)  {     
                            return ImageIcon.class; 
                        } else if (col == 1 || col == 2) {
                            return Integer.class;
                        } else {
                            return String.class;
                        }  //other columns accept String values  
                      }
                      
                    @Override
                    public boolean isCellEditable(int row, int column)
                    {
                       // make read only fields except column 0,13,14
                        if (column == 0 || column == 13 || column == 14 || column == 15) {                            
                           return false;
                        } else {
                           return true; 
                        }
                    }
                        };
                
    javax.swing.table.DefaultTableModel filemodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"Select", "LogID", "ComKey", "Partner", "FileType", "DocType", "TimeStamp", "File", "Dir", "View", "Status"})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0 || col == 9 || col == 10)  {     
                            return ImageIcon.class; 
                        } else if (col == 1 || col == 2) {
                            return Integer.class;
                        } else {
                            return String.class;
                        }  //other columns accept String values  
                      }  
                      @Override
                    public boolean isCellEditable(int row, int column)
                    {
                        // make read only fields except column 0,13,14
                        if (column == 0 || column == 9 || column == 10) {                            
                           return false;
                        } else {
                           return true; 
                        }
                    }
                        };
    
    javax.swing.table.DefaultTableModel modeldetail = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"LogID", "ComKey", "Severity", "Desc", "TimeStamp"});
    
   
    
    
     class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(Color.blue);
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
     
    class DocViewRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

       
            if (column == 8 || column == 12)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       
        return c;
    }
    }
        
    class FileViewRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

       
            if (column == 7)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       
        return c;
    }
    }
    
    
    public void getDocLogView() {
     
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
       HashMap<String, String> hm = getEDIStds();
       
        docmodel.setNumRows(0);
        tafile.setText("");
        try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            Statement st2 = con.createStatement();
            ResultSet res = null;
            ResultSet resdetail = null;
            try {

                int i = 0;

                String dir = "0";
                String indoctype = "";
                String outdoctype = "";
                
                tablereport.setModel(docmodel);
                tablereport.getColumnModel().getColumn(13).setMaxWidth(50);
                tablereport.getColumnModel().getColumn(14).setMaxWidth(50);
                tablereport.getColumnModel().getColumn(15).setMaxWidth(50);
             
              
                    if (! ddtradeid.getSelectedItem().toString().isEmpty() && dddoc.getSelectedItem().toString().isEmpty() ) {
                    res = st.executeQuery("SELECT edx_id, edx_comkey, edx_indoctype, edx_outdoctype, " +
                    " edx_sender, edx_receiver, edx_infiletype, edx_inbatch, edx_outbatch, edx_ref, edx_ts, edx_ack, edx_status, edx_outfiletype,  " +
                    " coalesce(elg_severity,'success') as detstatus " +
                    " FROM edi_idx  " +
                    " left outer join edi_log on elg_comkey = edx_comkey and elg_severity = 'error' " +
                    " where edx_sender >= " + "'" + ddtradeid.getSelectedItem().toString() + "'" +
                    " AND edx_sender <= " + "'" + ddtradeid.getSelectedItem().toString() + "'" +
                    " AND edx_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +        
                    " AND edx_ts >= " + "'" + dfdate.format(dcfrom.getDate()) + " 00:00:00" + "'" +
                    " AND edx_ts <= " + "'" + dfdate.format(dcto.getDate())  + " 23:59:59" + "'" + " order by edx_id desc ;" ) ;
                    }
                    
                    if (! dddoc.getSelectedItem().toString().isEmpty() && ddtradeid.getSelectedItem().toString().isEmpty()) {
                    res = st.executeQuery("SELECT edx_id, edx_comkey, edx_indoctype, edx_outdoctype, " +
                    " edx_sender, edx_receiver, edx_infiletype, edx_inbatch, edx_outbatch, edx_ref, edx_ts, edx_ack, edx_status, edx_outfiletype,  " +
                    " coalesce(elg_severity,'success') as detstatus " +
                    " FROM edi_idx  " +
                    " left outer join edi_log on elg_comkey = edx_comkey and elg_severity = 'error' " +
                    " where " +
                    " edx_indoctype >= " + "'" + dddoc.getSelectedItem().toString() + "'" +
                    " AND edx_indoctype <= " + "'" + dddoc.getSelectedItem().toString() + "'" +    
                    " AND edx_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +         
                    " AND edx_ts >= " + "'" + dfdate.format(dcfrom.getDate()) + " 00:00:00" + "'" +
                    " AND edx_ts <= " + "'" + dfdate.format(dcto.getDate())  + " 23:59:59" + "'" + " order by edx_id desc ;" ) ;
                    }
                    
                    if (! dddoc.getSelectedItem().toString().isEmpty() && ! ddtradeid.getSelectedItem().toString().isEmpty()) {
                    res = st.executeQuery("SELECT edx_id, edx_comkey, edx_indoctype, edx_outdoctype, " +
                    " edx_sender, edx_receiver, edx_infiletype, edx_inbatch, edx_outbatch, edx_ref, edx_ts, edx_ack, edx_status, edx_outfiletype,  " +
                    " coalesce(elg_severity,'success') as detstatus " +
                    " FROM edi_idx  " +
                    " left outer join edi_log on elg_comkey = edx_comkey and elg_severity = 'error' " +
                    " where edx_sender >= " + "'" + ddtradeid.getSelectedItem().toString() + "'" +
                    " AND edx_sender <= " + "'" + ddtradeid.getSelectedItem().toString() + "'" +
                    " AND edx_indoctype >= " + "'" + dddoc.getSelectedItem().toString() + "'" +
                    " AND edx_indoctype <= " + "'" + dddoc.getSelectedItem().toString() + "'" +    
                    " AND edx_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +         
                    " AND edx_ts >= " + "'" + dfdate.format(dcfrom.getDate()) + " 00:00:00" + "'" +
                    " AND edx_ts <= " + "'" + dfdate.format(dcto.getDate())  + " 23:59:59" + "'" + " order by edx_id desc ;" ) ;
                    }
                    
                    
                    if (ddtradeid.getSelectedItem().toString().isEmpty() && dddoc.getSelectedItem().toString().isEmpty()) {
                    res = st.executeQuery("SELECT edx_id, edx_comkey, edx_indoctype, edx_outdoctype, " +
                    " edx_sender, edx_receiver, edx_infiletype, edx_inbatch, edx_outbatch, edx_ref, edx_ts, edx_ack, edx_status, edx_outfiletype,  " +
                    " (select elg_severity from edi_log where elg_idxnbr = edx_id and elg_comkey = edx_comkey order by elg_id desc limit 1) as detstatus " +
                    " FROM edi_idx  " +
                   // " left outer join edi_log on elg_comkey = edx_comkey and elg_severity = 'error' " +
                    " where edx_ts >= " + "'" + dfdate.format(dcfrom.getDate()) + " 00:00:00" + "'" +
                    " AND edx_ts <= " + "'" + dfdate.format(dcto.getDate())  + " 23:59:59" + "'" + 
                    " AND edx_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + 
                    " order by edx_id desc ;" ) ;
                    }
                    
                    if (! tbref.getText().isEmpty()) {
                    res = st.executeQuery("SELECT edx_id, edx_comkey, edx_indoctype, edx_outdoctype, " +
                    " edx_sender, edx_receiver, edx_infiletype, edx_inbatch, edx_outbatch, edx_ref, edx_ts, edx_ack, edx_status, edx_outfiletype,  " +
                    " coalesce(elg_severity,'success') as detstatus " +
                    " FROM edi_idx  " +
                    " left outer join edi_log on elg_comkey = edx_comkey and elg_severity = 'error' " +
                    " where edx_ref like " + "'%" + tbref.getText() + "%'" +
                    " AND edx_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +         
                    " order by edx_id desc ;" ) ;
                    }
                    
                
                
                ImageIcon statusImage = null;
                while (res.next()) {
                    
                    if (! ddoutdoctype.getSelectedItem().toString().isBlank() && ! res.getString("edx_outdoctype").equals(ddoutdoctype.getSelectedItem().toString())) {
                        continue;
                    }
                    
                    if (res.getString("detstatus").equals("success")) {
                      statusImage = BlueSeerUtils.clickcheck;
                    }  else {
                      statusImage = BlueSeerUtils.clicknocheck;
                    }
                    if (res.getString("edx_ack").equals("1")) {
                      statusImage = BlueSeerUtils.clickcheckblue; 
                    }
                
                    i++;
                    
                  if (hm.containsKey(res.getString("edx_indoctype"))) {
                    indoctype = hm.get(res.getString("edx_indoctype"));
                  } else {
                    indoctype = res.getString("edx_indoctype");
                  }
                  
                  if (hm.containsKey(res.getString("edx_outdoctype"))) {
                    outdoctype = hm.get(res.getString("edx_outdoctype"));
                  } else {
                    outdoctype = res.getString("edx_outdoctype");
                  }
                    
                    
                 //   "Select", "IdxNbr", "ComKey", "SenderID", "ReceiverID", "TimeStamp", "InFileType", "InDocType", "InBatch", "OutFileType", "OutDocType", "OutBatch",  "Status"                     
                    docmodel.addRow(new Object[]{BlueSeerUtils.clickbasket,
                        res.getInt("edx_id"),
                        res.getInt("edx_comkey"),
                        res.getString("edx_sender"),
                        res.getString("edx_receiver"),
                        res.getString("edx_ts"),
                        res.getString("edx_infiletype"),
                        indoctype,
                        res.getString("edx_inbatch"),
                        res.getString("edx_ref"), 
                        res.getString("edx_outfiletype"),
                        outdoctype,
                        res.getString("edx_outbatch"),
                        BlueSeerUtils.clickleftdoc,
                        BlueSeerUtils.clickrightdoc,
                        statusImage
                    });
                }
                
                tbtot.setText(String.valueOf(i));

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (resdetail != null) {
                    resdetail.close();
                }
                if (st != null) {
                    st.close();
                }
                if (st2 != null) {
                    st2.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
   }
    
    public void getFileLogView() {
     
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
             
        filemodel.setNumRows(0);
        tafile.setText("");
        
        HashMap<String, String> hm = getEDIStds();
        
        try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {

                int i = 0;
                String doctype = "";
               
                tablereport.setModel(filemodel);
               //  tablereport.getColumnModel().getColumn(8).setCellRenderer(new EDITransactionBrowse.SomeRenderer()); 
              //   tablereport.getColumnModel().getColumn(7).setCellRenderer(new EDITransactionBrowse.FileViewRenderer()); 
                 tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
                 tablereport.getColumnModel().getColumn(1).setMaxWidth(100);
                 tablereport.getColumnModel().getColumn(2).setMaxWidth(100);
                 tablereport.getColumnModel().getColumn(9).setMaxWidth(50);
                 tablereport.getColumnModel().getColumn(10).setMaxWidth(50);
                 
              
                    if (! ddtradeid.getSelectedItem().toString().isEmpty() && dddoc.getSelectedItem().toString().isEmpty() ) {
                    res = st.executeQuery("SELECT * FROM edi_file  " +
                    " where edf_partner >= " + "'" + ddtradeid.getSelectedItem().toString() + "'" +
                    " AND edf_partner <= " + "'" + ddtradeid.getSelectedItem().toString() + "'" +        
                    " AND edf_ts >= " + "'" + dfdate.format(dcfrom.getDate()) + " 00:00:00" + "'" +
                    " AND edf_ts <= " + "'" + dfdate.format(dcto.getDate())  + " 23:59:59" + "'" + 
                    " AND edf_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +         
                    " order by edf_id desc ;" ) ;
                    }
                    
                    if (! dddoc.getSelectedItem().toString().isEmpty() && ddtradeid.getSelectedItem().toString().isEmpty()) {
                    res = st.executeQuery("SELECT * FROM edi_file  " +
                    " where " +
                    " edf_doctype >= " + "'" + dddoc.getSelectedItem().toString() + "'" +
                    " AND edf_doctype <= " + "'" + dddoc.getSelectedItem().toString() + "'" +        
                    " AND edf_ts >= " + "'" + dfdate.format(dcfrom.getDate()) + " 00:00:00" + "'" +
                    " AND edf_ts <= " + "'" + dfdate.format(dcto.getDate())  + " 23:59:59" + "'" +
                    " AND edf_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +         
                    " order by edf_id desc ;" ) ;
                    }
                    
                    if (! dddoc.getSelectedItem().toString().isEmpty() && ! ddtradeid.getSelectedItem().toString().isEmpty()) {
                    res = st.executeQuery("SELECT * FROM edi_file  " +
                     " where edf_partner >= " + "'" + ddtradeid.getSelectedItem().toString() + "'" +
                    " AND edf_partner <= " + "'" + ddtradeid.getSelectedItem().toString() + "'" +
                    " AND edf_doctype >= " + "'" + dddoc.getSelectedItem().toString() + "'" +
                    " AND edf_doctype <= " + "'" + dddoc.getSelectedItem().toString() + "'" +        
                    " AND edf_ts >= " + "'" + dfdate.format(dcfrom.getDate()) + " 00:00:00" + "'" +
                    " AND edf_ts <= " + "'" + dfdate.format(dcto.getDate())  + " 23:59:59" + "'" + 
                    " AND edf_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +         
                    " order by edf_id desc ;" ) ;
                    }
                    
                    if (ddtradeid.getSelectedItem().toString().isEmpty() && dddoc.getSelectedItem().toString().isEmpty()) {
                    res = st.executeQuery("SELECT * FROM edi_file  " +
                    " where edf_ts >= " + "'" + dfdate.format(dcfrom.getDate()) + " 00:00:00" + "'" +
                    " AND edf_ts <= " + "'" + dfdate.format(dcto.getDate())  + " 23:59:59" + "'" + 
                    " AND edf_site = " + "'" + ddsite.getSelectedItem().toString() + "'" +         
                    " order by edf_id desc ;" ) ;
                    }
                    
                
                
                ImageIcon statusImage = null;
                while (res.next()) {
                    i++;
                  if (res.getString("edf_status").equals("success")) {
                      statusImage = BlueSeerUtils.clickcheck;
                  }  else {
                      statusImage = BlueSeerUtils.clicknocheck;
                  }
                  
                  if (hm.containsKey(res.getString("edf_doctype"))) {
                    doctype = hm.get(res.getString("edf_doctype"));
                  } else {
                    doctype = res.getString("edf_doctype");
                  }
                  
                  
                 //   "Select", "IdxNbr", "ComKey", "SenderID", "ReceiverID", "TimeStamp", "InFileType", "InDocType", "InBatch", "OutFileType", "OutDocType", "OutBatch",  "Status"                     
                    filemodel.addRow(new Object[]{BlueSeerUtils.clickbasket,
                        res.getInt("edf_id"),
                        res.getInt("edf_comkey"),
                        res.getString("edf_partner"),
                        res.getString("edf_filetype"),
                        doctype,
                        res.getString("edf_ts"),
                        res.getString("edf_file"),
                        res.getString("edf_dir"),
                        BlueSeerUtils.clickfind, 
                        statusImage
                    });
                }
                
                hm.clear();
                
                tbtot.setText(String.valueOf(i));

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
    
    
     
   
    /**
     * Creates new form ScrapReportPanel
     */
    

    public EDITransactionBrowse() {
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
    
    public void getdetail(String comkey, String idxkey) {
      
         modeldetail.setNumRows(0);
        
         DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
        
        try {

            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                int i = 0;
                String blanket = "";
                if (idxkey.equals("0")) {
                 res = st.executeQuery("select elg_id, elg_comkey, elg_idxnbr, elg_severity, elg_desc, elg_ts from edi_log " +
                        " where elg_comkey = " + "'" + comkey + "'" +
                        ";");   
                } else {
                 res = st.executeQuery("select elg_id, elg_comkey, elg_idxnbr, elg_severity, elg_desc, elg_ts from edi_log " +
                        " where elg_comkey = " + "'" + comkey + "'" +
                        " and elg_idxnbr = " + "'" + idxkey + "'" +
                        ";");   
                }
                
                while (res.next()) {
                   modeldetail.addRow(new Object[]{ 
                      res.getString("elg_id"), 
                      res.getString("elg_comkey"),
                      res.getString("elg_severity"),
                      res.getString("elg_desc"),
                      res.getString("elg_ts")
                      });
                }
               
               
                this.repaint();

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
    
    public void initvars(String[] arg) {
       
        buttonGroup1.add(rbDocLog);
        buttonGroup1.add(rbFileLog);
        rbFileLog.setSelected(true);
        
        dddoc.removeAllItems();
        ddoutdoctype.removeAllItems();
        ArrayList<String> mylist = OVData.getCodeMstrKeyList("edidoctype");
        for (int i = 0; i < mylist.size(); i++) {
            dddoc.addItem(mylist.get(i));
            ddoutdoctype.addItem(mylist.get(i));
        }
        dddoc.insertItemAt("", 0);
        dddoc.setSelectedIndex(0);
        ddoutdoctype.insertItemAt("", 0);
        ddoutdoctype.setSelectedIndex(0);
        
        ddtradeid.removeAllItems();
        ArrayList<String> tradeids = EDData.getEDITradeIDs();
        for (int i = 0; i < tradeids.size(); i++) {
            ddtradeid.addItem(tradeids.get(i));
        }
        ddtradeid.insertItemAt("", 0);
        ddtradeid.setSelectedIndex(0);
        
        tbtoterrors.setText("0");
        tbtot.setText("0");
       
        
        java.util.Date now = new java.util.Date();
       
        
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        java.util.Date firstday = cal.getTime();
        
       // dcfrom.setDate(firstday);
       dcfrom.setDate(now);
        dcto.setDate(now);
               
        docmodel.setNumRows(0);
        modeldetail.setNumRows(0);
        tablereport.setModel(filemodel);
        tabledetail.setModel(modeldetail);
        
        tablereport.getTableHeader().setReorderingAllowed(false);
        tabledetail.getTableHeader().setReorderingAllowed(false);
        
        // tablereport.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());
         tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
         tabledetail.getColumnModel().getColumn(0).setMaxWidth(100);
         tabledetail.getColumnModel().getColumn(1).setMaxWidth(100);
         tabledetail.getColumnModel().getColumn(2).setMaxWidth(100);
         tabledetail.getColumnModel().getColumn(4).setMaxWidth(200);
       
        
        btdetail.setEnabled(false);
        bthidetext.setEnabled(false);
        detailpanel.setVisible(false);
        textpanel.setVisible(false);
        btreprocess.setEnabled(false);
        btclearstatus.setEnabled(false);
        tafile.setText("");
        tafile.setFont(new Font("monospaced", Font.PLAIN, 12));
        
        ddsite.removeAllItems();
        OVData.getSiteList(bsmf.MainFrame.userid).stream().forEach((s) -> ddsite.addItem(s));  
        ddsite.setSelectedItem(OVData.getDefaultSite());
        
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        tablepanel = new javax.swing.JPanel();
        summarypanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        detailpanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();
        textpanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tafile = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        btdetail = new javax.swing.JButton();
        btRun = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        dcfrom = new com.toedter.calendar.JDateChooser();
        dcto = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        bthidetext = new javax.swing.JButton();
        rbFileLog = new javax.swing.JRadioButton();
        rbDocLog = new javax.swing.JRadioButton();
        tbsegdelim = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        lbsegdelim = new javax.swing.JLabel();
        tbref = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btreprocess = new javax.swing.JButton();
        btclearstatus = new javax.swing.JButton();
        ddtradeid = new javax.swing.JComboBox<>();
        dddoc = new javax.swing.JComboBox<>();
        ddoutdoctype = new javax.swing.JComboBox<>();
        ddsite = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        tbtoterrors = new javax.swing.JLabel();
        tbtot = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();

        jLabel2.setText("jLabel2");

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("EDITranBrowse"));
        jPanel1.setName("panelmain"); // NOI18N

        tablepanel.setLayout(new javax.swing.BoxLayout(tablepanel, javax.swing.BoxLayout.LINE_AXIS));

        summarypanel.setLayout(new java.awt.BorderLayout());

        tablereport.setAutoCreateRowSorter(true);
        tablereport.setModel(new javax.swing.table.DefaultTableModel(
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
        tablereport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablereportMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablereport);

        summarypanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        tablepanel.add(summarypanel);

        detailpanel.setLayout(new java.awt.BorderLayout());

        tabledetail.setModel(new javax.swing.table.DefaultTableModel(
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
        tabledetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabledetailMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabledetail);

        detailpanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        tablepanel.add(detailpanel);

        textpanel.setMinimumSize(new java.awt.Dimension(23, 23));
        textpanel.setName(""); // NOI18N
        textpanel.setPreferredSize(new java.awt.Dimension(452, 402));
        textpanel.setLayout(new java.awt.BorderLayout());

        tafile.setColumns(20);
        tafile.setRows(5);
        jScrollPane3.setViewportView(tafile);

        textpanel.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        tablepanel.add(textpanel);

        btdetail.setText("Hide Detail");
        btdetail.setName("bthidedetail"); // NOI18N
        btdetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdetailActionPerformed(evt);
            }
        });

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel5.setText("From Date:");
        jLabel5.setName("lblfromdate"); // NOI18N

        jLabel6.setText("To Date:");
        jLabel6.setName("lbltodate"); // NOI18N

        dcfrom.setDateFormatString("yyyy-MM-dd");

        dcto.setDateFormatString("yyyy-MM-dd");

        jLabel3.setText("SenderID");
        jLabel3.setName("lbltpid"); // NOI18N

        jLabel4.setText("DocType");
        jLabel4.setName("lbldoctype"); // NOI18N

        bthidetext.setText("Hide Text");
        bthidetext.setName("bthidetext"); // NOI18N
        bthidetext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bthidetextActionPerformed(evt);
            }
        });

        rbFileLog.setText("FileLogView");
        rbFileLog.setName("cbfilelog"); // NOI18N
        rbFileLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbFileLogActionPerformed(evt);
            }
        });

        rbDocLog.setText("DocLogView");
        rbDocLog.setName("cbdoclog"); // NOI18N
        rbDocLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbDocLogActionPerformed(evt);
            }
        });

        tbsegdelim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbsegdelimFocusLost(evt);
            }
        });

        jLabel1.setText("SegDelim (int)");
        jLabel1.setName("lblsegdelim"); // NOI18N

        jLabel7.setText("Reference");
        jLabel7.setName("lblref"); // NOI18N

        btreprocess.setText("Reprocess");
        btreprocess.setName("btreprocess"); // NOI18N
        btreprocess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btreprocessActionPerformed(evt);
            }
        });

        btclearstatus.setText("Clear Status");
        btclearstatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearstatusActionPerformed(evt);
            }
        });

        jLabel10.setText("Out DocType");

        jLabel9.setText("Site:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel6))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dcfrom, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                            .addComponent(dcto, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
                        .addGap(31, 31, 31)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(rbFileLog)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rbDocLog))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btRun)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdetail)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bthidetext)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btreprocess)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclearstatus)
                                .addGap(69, 69, 69)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbsegdelim, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbsegdelim, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddtradeid, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ddoutdoctype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dddoc, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btRun)
                        .addComponent(btdetail)
                        .addComponent(bthidetext)
                        .addComponent(tbsegdelim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(lbsegdelim, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btreprocess)
                        .addComponent(btclearstatus))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel5)
                        .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rbFileLog)
                        .addComponent(rbDocLog))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel6)
                        .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(dddoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(ddtradeid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddoutdoctype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel8.setText("Total Errors:");
        jLabel8.setName("lbltotalerrors"); // NOI18N

        tbtoterrors.setText("0");

        tbtot.setText("0");

        jLabel11.setText("Total Transactions:");
        jLabel11.setName("lbltotaltrans"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(tbtoterrors, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                    .addComponent(tbtot, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtot, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtoterrors, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(66, 66, 66))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(tablepanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1279, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRunActionPerformed
      if (rbDocLog.isSelected()) {
        getDocLogView();
      } else {
        getFileLogView();  
      }
    }//GEN-LAST:event_btRunActionPerformed

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
       detailpanel.setVisible(false);
       btdetail.setEnabled(false);
    }//GEN-LAST:event_btdetailActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 0 && rbDocLog.isSelected()) {
                getdetail(tablereport.getValueAt(row, 2).toString(), tablereport.getValueAt(row, 1).toString());
                btdetail.setEnabled(true);
                detailpanel.setVisible(true);
        }
        if ( col == 0 && rbFileLog.isSelected()) {
                getdetail(tablereport.getValueAt(row, 2).toString(), "0");
                btdetail.setEnabled(true);
                detailpanel.setVisible(true);
        }
        if ( col == 15) {
                String ackfile = EDData.getEDIAckFileFromEDIIDX(tablereport.getValueAt(row, 1).toString());
                if (! ackfile.isEmpty()) {
                int k = 10;
                      if (BlueSeerUtils.isParsableToInt(tbsegdelim.getText())) {
                      k = Integer.valueOf(tbsegdelim.getText());
                      }
                     try {
                         tafile.setText("");
                         if (! tablereport.getValueAt(row, 7).toString().isEmpty()) {
                         ArrayList<String> segments = EDData.readEDIRawFileByDoc(ackfile, 
                                 cleanDirString(EDData.getEDIBatchDir()),
                                 true,
                                 "0",
                                 "0",
                                 String.valueOf(k)
                                 );  
                            tafile.append(ackfile + ":" + "\n");
                            for (String segment : segments ) {
                                tafile.append(segment);
                                tafile.append("\n");
                            }
                         }
                     } catch (MalformedURLException ex) {
                         MainFrame.bslog(ex);
                     } catch (SmbException ex) {
                         MainFrame.bslog(ex);
                     } catch (IOException ex) {
                         MainFrame.bslog(ex);
                     }
                     tafile.setCaretPosition(0);
                     textpanel.setVisible(true);
                     bthidetext.setEnabled(true);
                }
        }
        
          if ( col == 9 && rbFileLog.isSelected()) {
              int k = 10;
              // try to get delimiter from edi_idx (edx_comkey)
              String[] p = EDData.getEDIDocPositionEDIIDXcomkey(tablereport.getValueAt(row, 2).toString());
              if (! p[4].isEmpty()) {
               k = Integer.valueOf(p[4]);
              }
              
              // if textbox segment is occupied...override lookup
              if (! tbsegdelim.getText().isEmpty() && BlueSeerUtils.isParsableToInt(tbsegdelim.getText())) {
              k = Integer.valueOf(tbsegdelim.getText());
              }
             try {
                 tafile.setText("");
                 if (! tablereport.getValueAt(row, 7).toString().isEmpty()) {
                 ArrayList<String> segments = EDData.readEDIRawFileByDoc(tablereport.getValueAt(row, 7).toString(), 
                         cleanDirString(EDData.getEDIInArch()),
                         true,
                         "0",
                         "0",
                         String.valueOf(k)
                         );  
                    for (String segment : segments ) {
                        tafile.append(segment);
                        tafile.append("\n");
                    }
                 }
             } catch (MalformedURLException ex) {
                 MainFrame.bslog(ex);
             } catch (SmbException ex) {
                 MainFrame.bslog(ex);
             } catch (IOException ex) {
                 MainFrame.bslog(ex);
             }
             tafile.setCaretPosition(0);
             textpanel.setVisible(true);
             bthidetext.setEnabled(true);
             
        }
          if ( (col == 13) && rbDocLog.isSelected()) {
              int k = 10;
              String[] p = EDData.getEDIDocPositionEDIIDX(tablereport.getValueAt(row, 1).toString());
              String end = "0";  // doc end ...revisit...just show everything for now
             try {
                 tafile.setText("");
                 if (! tablereport.getValueAt(row, 8).toString().isEmpty()) {
                 ArrayList<String> segments = EDData.readEDIRawFileByDoc(tablereport.getValueAt(row, 8).toString(), 
                         cleanDirString(EDData.getEDIBatchDir()),
                         true,
                         p[2],
                         end,
                         p[4]
                         );  
                    for (String segment : segments ) {
                        tafile.append(segment);
                        tafile.append("\n");
                    }
                 }
             } catch (MalformedURLException ex) {
                 MainFrame.bslog(ex);
             } catch (SmbException ex) {
                 MainFrame.bslog(ex);
             } catch (IOException ex) {
                 MainFrame.bslog(ex);
             }
             tafile.setCaretPosition(0);
             textpanel.setVisible(true);
             bthidetext.setEnabled(true);
             
        }
          
         if ( (col == 14) && rbDocLog.isSelected()) {
              int k = 10;
              String[] p = EDData.getEDIDocPositionEDIIDX(tablereport.getValueAt(row, 1).toString());
             
            //  if (BlueSeerUtils.isParsableToInt(tbsegdelim.getText())) {
            //  k = Integer.valueOf(tbsegdelim.getText());
            //  }
             try {
                 tafile.setText("");
                 if (! tablereport.getValueAt(row, 12).toString().isEmpty()) {
                 ArrayList<String> segments = EDData.readEDIRawFileByDoc(tablereport.getValueAt(row, 12).toString(), 
                         cleanDirString(EDData.getEDIBatchDir()),
                         true,
                         p[0],
                         p[1],
                         p[4]
                         );  
                    for (String segment : segments ) {
                        tafile.append(segment);
                        tafile.append("\n");
                    }
                 }
             } catch (MalformedURLException ex) {
                 MainFrame.bslog(ex);
             } catch (SmbException ex) {
                 MainFrame.bslog(ex);
             } catch (IOException ex) {
                 MainFrame.bslog(ex);
             }
             tafile.setCaretPosition(0);
             textpanel.setVisible(true);
             bthidetext.setEnabled(true);
             
        }  
      
    }//GEN-LAST:event_tablereportMouseClicked

    private void bthidetextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bthidetextActionPerformed
        textpanel.setVisible(false);
       bthidetext.setEnabled(false);
    }//GEN-LAST:event_bthidetextActionPerformed

    private void tbsegdelimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsegdelimFocusLost
        if (BlueSeerUtils.isParsableToInt(tbsegdelim.getText())) {
        int x = Integer.valueOf(tbsegdelim.getText());
        lbsegdelim.setText(String.valueOf((char) x));
        } else {
            tbsegdelim.setText("10");
        }
    }//GEN-LAST:event_tbsegdelimFocusLost

    private void rbDocLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbDocLogActionPerformed
        detailpanel.setVisible(false);
        btdetail.setEnabled(false);
        if (rbDocLog.isSelected()) {
            tbref.setEnabled(true);
            btreprocess.setEnabled(false);
            btclearstatus.setEnabled(false);
            ddoutdoctype.setEnabled(true);
           // getDocLogView();
        } else {
            tbref.setEnabled(false);
            btreprocess.setEnabled(true);
            btclearstatus.setEnabled(true);
            ddoutdoctype.setEnabled(false);
           // getFileLogView();
        }
    }//GEN-LAST:event_rbDocLogActionPerformed

    private void rbFileLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbFileLogActionPerformed
        detailpanel.setVisible(false);
        btdetail.setEnabled(false);
        if (rbDocLog.isSelected()) {
            tbref.setEnabled(true);
            btreprocess.setEnabled(false);
            btclearstatus.setEnabled(false);
            ddoutdoctype.setEnabled(true);
           // getDocLogView();
        } else {
            tbref.setEnabled(false);
            btreprocess.setEnabled(true);
            btclearstatus.setEnabled(true);
            ddoutdoctype.setEnabled(false);
         //   getFileLogView();
        }
    }//GEN-LAST:event_rbFileLogActionPerformed

    private void btreprocessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btreprocessActionPerformed
        //bsmf.MainFrame.show("This functionality is not available yet");
       // return;
        
        int[] rows = tablereport.getSelectedRows();
        if (rows.length > 1) {
            bsmf.MainFrame.show(getMessageTag(1162));
            return;
        }
        for (int i : rows) {
            if (tablereport.getValueAt(i, 1) != null && tablereport.getValueAt(i, 2) != null ) {
                String batch = EDData.getEDIBatchFromedi_file(tablereport.getValueAt(i,2).toString());
                if (! batch.isEmpty())
                    try {
                        batch = cleanDirString(EDData.getEDIBatchDir()) + batch; 
                       String[] m = EDI.processFile(batch, "", "", "", false, true, Integer.valueOf(tablereport.getValueAt(i, 2).toString()), Integer.valueOf(tablereport.getValueAt(i, 1).toString()), "");
                       String result = m[0] + " of " + m[1];
                       bsmf.MainFrame.show(getMessageTag(1163,result));
                    } catch (IOException ex) {
                        MainFrame.bslog(ex);
                    } catch (ClassNotFoundException ex) {
                        MainFrame.bslog(ex);
                    }
            }
        }
        
    }//GEN-LAST:event_btreprocessActionPerformed

    private void btclearstatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearstatusActionPerformed
         int[] rows = tablereport.getSelectedRows();
        for (int i : rows) {
            updateEDIFileLogStatusManual(tablereport.getValueAt(i,2).toString());
        }
        if (rows.length >= 1) {
        getFileLogView();
        }
    }//GEN-LAST:event_btclearstatusActionPerformed

    private void tabledetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabledetailMouseClicked
        int row = tabledetail.rowAtPoint(evt.getPoint());
        int col = tabledetail.columnAtPoint(evt.getPoint());
        if (col == 3) {
            tafile.setText(tabledetail.getValueAt(row, 3).toString());
            tafile.setCaretPosition(0);
            textpanel.setVisible(true);
            bthidetext.setEnabled(true);
        }
    }//GEN-LAST:event_tabledetailMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btclearstatus;
    private javax.swing.JButton btdetail;
    private javax.swing.JButton bthidetext;
    private javax.swing.JButton btreprocess;
    private javax.swing.ButtonGroup buttonGroup1;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JComboBox<String> dddoc;
    private javax.swing.JComboBox<String> ddoutdoctype;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JComboBox<String> ddtradeid;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lbsegdelim;
    private javax.swing.JRadioButton rbDocLog;
    private javax.swing.JRadioButton rbFileLog;
    private javax.swing.JPanel summarypanel;
    private javax.swing.JTable tabledetail;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablereport;
    private javax.swing.JTextArea tafile;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbsegdelim;
    private javax.swing.JLabel tbtot;
    private javax.swing.JLabel tbtoterrors;
    private javax.swing.JPanel textpanel;
    // End of variables declaration//GEN-END:variables
}
