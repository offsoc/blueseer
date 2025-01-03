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
package com.blueseer.inv;


import bsmf.MainFrame;
import static bsmf.MainFrame.tags;
import com.blueseer.fgl.fglData;
import com.blueseer.fgl.fglData.gl_pair;
import static com.blueseer.fgl.fglData.setGLRecNbr;
import static com.blueseer.inv.invData.getItemMstr;
import com.blueseer.inv.invData.in_mstr;
import static com.blueseer.inv.invData.inventoryAdjustmentTransaction;
import com.blueseer.inv.invData.item_mstr;
import com.blueseer.inv.invData.tran_mstr;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import com.blueseer.utl.DTData;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.getSysMetaValue;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

/**
 *
 * @author vaughnte
 */
public class InventoryMaint extends javax.swing.JPanel {

    // global variable declarations
    boolean isLoad = false;
    public static javax.swing.table.DefaultTableModel lookUpModel = null;
    public static JTable lookUpTable = new JTable();
    public static MouseListener mllu = null;
    public static JDialog dialog = new JDialog();

    public static ButtonGroup bg = null;
    public static JRadioButton rb1 = null;
    public static JRadioButton rb2 = null;
    
    /**
     * Creates new form InventoryMiscPanel
     */
    public InventoryMaint() {
        initComponents();
        setLanguageTags(this);
    }

    public void executeTask(BlueSeerUtils.dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(BlueSeerUtils.dbaction type, String[] key) { 
              this.type = type.name();
              this.key = key;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            
             switch(this.type) {
                case "run":
                    message = postProd();    
                    break;      
                default:
                    message = new String[]{"1", "unknown action"};
            }
            
            return message;
        }
 
        
       public void done() {
            try {
            String[] message = get();
           
            BlueSeerUtils.endTask(message);
            initvars(null);  
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
      
       BlueSeerUtils.startTask(new String[]{"","Running..."});
       Task z = new Task(x, y); 
       z.execute(); 
       
    }
    
    public void setPanelComponentState(Object myobj, boolean b) {
        JPanel panel = null;
        JTabbedPane tabpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else {
            return;
        }
        
        if (panel != null) {
        panel.setEnabled(b);
        Component[] components = panel.getComponents();
        
            for (Component component : components) {
                if (component instanceof JLabel || component instanceof JTable ) {
                    continue;
                }
                if (component instanceof JPanel) {
                    setPanelComponentState((JPanel) component, b);
                }
                if (component instanceof JTabbedPane) {
                    setPanelComponentState((JTabbedPane) component, b);
                }
                
                component.setEnabled(b);
            }
        }
            if (tabpane != null) {
                tabpane.setEnabled(b);
                Component[] componentspane = tabpane.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
                    }
                    if (component instanceof JPanel) {
                        setPanelComponentState((JPanel) component, b);
                    }
                    component.setEnabled(b);
                }
            }
    } 
    
    public void setComponentDefaultValues() {
        isLoad = true;
        java.util.Date now = new java.util.Date();
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        dcdate.setDate(now);
        dcexpire.setDate(null);
        tbitem.setText("");
        tbqty.setText("");
        tbref.setText("");
        tbrmks.setText("");
        tblotserial.setText("");
        tbitem.setBackground(Color.white);
        tbqty.setBackground(Color.white);
        
        ArrayList<String[]> initDataSets = invData.getInvMaintInit();
        String defaultsite = "";
        
        ddtype.requestFocus();
        
        ddsite.removeAllItems();
        ddwh.removeAllItems();
        ddloc.removeAllItems();
        ddacct.removeAllItems();
        ddcc.removeAllItems();
        
        ArrayList<String> wh = OVData.getWareHouseList(); 
        for (String[] s : initDataSets) {
            if (s[0].equals("accounts")) {
              ddacct.addItem(s[1]); 
            }
            
            if (s[0].equals("depts")) {
              ddcc.addItem(s[1]); 
            }
            
            if (s[0].equals("warehouses")) {
              ddwh.addItem(s[1]); 
            }
            
            if (s[0].equals("locations")) {
              ddloc.addItem(s[1]); 
            }
          
            if (s[0].equals("site")) {
              defaultsite = s[1]; 
            }
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]); 
            }
            
        }
        
         ddsite.setSelectedItem(defaultsite);
         
         if (ddacct.getItemCount() > 0) {
          ddacct.setSelectedIndex(0);
         }
         
         if (ddcc.getItemCount() > 0) {
          ddcc.setSelectedIndex(0);
         }
        
         ddwh.insertItemAt("", 0);
         ddloc.insertItemAt("", 0);
        
        isLoad = false;
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
    
    
    public void getiteminfo(String item) {
        
        item_mstr im = getItemMstr(new String[]{item});
        ddsite.setSelectedItem(im.it_site());
        ddloc.setSelectedItem(im.it_loc());
        ddwh.setSelectedItem(im.it_wh());
             
    }
    
    
    
    public void initvars(String[] arg) {
        
        
       setPanelComponentState(this, true); 
       setComponentDefaultValues();
        
    }
    
    public void lookUpFrameItemDesc() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getItemDescBrowseBySite(luinput.getText(), "it_item", ddsite.getSelectedItem().toString());
        } else {
         luModel = DTData.getItemDescBrowseBySite(luinput.getText(), "it_desc", ddsite.getSelectedItem().toString());   
        }
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (luModel.getRowCount() < 1) {
            ludialog.setTitle(getMessageTag(1001));
        } else {
            ludialog.setTitle(getMessageTag(1002, String.valueOf(luModel.getRowCount())));
        }
        }
        };
        luinput.addActionListener(lual);
        
        luTable.removeMouseListener(luml);
        luml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0) {
                ludialog.dispose();
                tbitem.setText(target.getValueAt(row,1).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblitem", this.getClass().getSimpleName()), getGlobalColumnTag("description")); 
        
        
        
    }

    public void lookUpFrameAcctDesc() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getAcctBrowseUtil(luinput.getText(),0, "ac_id");
        } else {
         luModel = DTData.getAcctBrowseUtil(luinput.getText(),0, "ac_desc");   
        }
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (luModel.getRowCount() < 1) {
            ludialog.setTitle(getMessageTag(1001));
        } else {
            ludialog.setTitle(getMessageTag(1002, String.valueOf(luModel.getRowCount())));
        }
        }
        };
        luinput.addActionListener(lual);
        
        luTable.removeMouseListener(luml);
        luml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0) {
                ludialog.dispose();
                ddacct.setSelectedItem(target.getValueAt(row,1).toString());
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblacct", this.getClass().getSimpleName()), 
                getGlobalColumnTag("description")); 
        
        
    }

    public String[] postProd() {
        String[] m = null;
        boolean isError = false;
        String type = "";
        String op = "";
        double qty = 0;
        double totalcost = 0.00;
        
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        String loc = "";
        String wh = "";
        String acct = "";
        String cc = "";
        String site = "";
        
        if (ddloc.getSelectedItem() != null)
        loc = ddloc.getSelectedItem().toString();
        
        if (ddwh.getSelectedItem() != null)
        wh = ddwh.getSelectedItem().toString();
        
        if (ddacct.getSelectedItem() != null)
        acct = ddacct.getSelectedItem().toString();
        
        if (ddcc.getSelectedItem() != null)
        cc = ddcc.getSelectedItem().toString();
        
        if (ddsite.getSelectedItem() != null)
        site = ddsite.getSelectedItem().toString();
        
        if (! tbqty.getText().isEmpty()) {
            qty = bsParseDouble(tbqty.getText());
        }
        
        if (ddtype.getSelectedItem().toString().equals("issue")) {
            type = "ISS-MISC";
            qty = (-1 * qty);
        } else {
            type = "RCT-MISC";
            qty = qty;
        }
        
        String gldoc = setGLRecNbr("AJ");
        
        // all inventory transactions performed in base currency
        String basecurr = OVData.getDefaultCurrency();
        
        // get cost
        String itemtype = invData.getItemCode(tbitem.getText());
        double cost = invData.getItemCost(tbitem.getText(), "standard", site);
        if (cost == 0 && itemtype.equals("A")) {
            cost = invData.getItemPurchPrice(tbitem.getText());
        }
        
        // lets get the productline of the part being adjusted
        String prodline = OVData.getProdLineFromItem(tbitem.getText());
        
        if ( prodline == null || prodline.isEmpty() ) {
            return new String[]{"1", getMessageTag(1066)};
        }
        
        String invacct = OVData.getProdLineInvAcct(prodline);
        
        if (invacct.isEmpty()) {
            return new String[]{"1", getMessageTag(1067)};
        }
        
        if (cost == 0.00) {
            return new String[]{"1", getMessageTag(1068)};
        }
        
        
        if ( OVData.isGLPeriodClosed(dfdate.format(dcdate.getDate()))) {
          return new String[]{"1", getMessageTag(1035)};
        }
        
        String expire = null;
        if (dcexpire.getDate() != null) {
            expire = dfdate.format(dcexpire.getDate());
        }
        
        tran_mstr tm = new tran_mstr(null,
                "", // id
                site, // site
                tbitem.getText(),
                qty,
                setDateDB(today), //entdate
                setDateDB(dcdate.getDate()), //effdate
                bsmf.MainFrame.userid, //userid
                tbref.getText(), //ref
                "", //addrcode
                type, //type
                null, //datetime
                tbrmks.getText(), //remarks
                "", //tr_nbr
                "", //misc1
                tblotserial.getText(), //lot
                tblotserial.getText(), //serial
                "InventoryMaint", //program
                0, //amt
                0, //mtl
                0, //lbr
                0, //bdn
                0, //ovh
                0, //out
                "", //batch
                op, //op
                loc, //loc
                wh, //wh
                expire, //expire
                cc, //cc
                "", //wc
                "", //wf
                "", //prodline
                "0", //timestamp ; db assigned
                "", //cell
                0, //export
                "", //order
                0, //line
                "", //po
                0.00, //price
                cost, //cost
                acct, //acct
                "", //terms
                "", //pack
                "", //curr
                null, //packdate
                null, //assydate
                "", //uom
                qty, //baseqty
                "" //bom
        );
        
        
        in_mstr in = new in_mstr(null,
                tbitem.getText(),
                qty,
                null, // date
                loc,
                wh,
                site,
                tblotserial.getText(),
                expire,
                bsmf.MainFrame.userid,
                "InventoryMaint"
        );
        
        String acct_cr = "";
        String cc_cr = "";
        String acct_dr = "";
        String cc_dr = "";
        if (ddtype.getSelectedItem().toString().equals("issue")) {
            acct_cr = invacct;
            cc_cr = prodline;
            acct_dr = acct;
            cc_dr = cc;
        } else {
            acct_cr = ddacct.getSelectedItem().toString();
            cc_cr = ddcc.getSelectedItem().toString();
            acct_dr = invacct;
            cc_dr = prodline;
        }
            gl_pair gv = new gl_pair(null,
                    acct_cr, 
                    cc_cr, 
                    acct_dr, 
                    cc_dr,  
                    setDateDB(dcdate.getDate()), 
                    (cost * bsParseDouble(tbqty.getText())),  
                    (cost * bsParseDouble(tbqty.getText())),  
                    basecurr, 
                    basecurr, 
                    tbref.getText() , 
                    type,
                    site, 
                    tbrmks.getText(), 
                    gldoc
            );
                
        m = inventoryAdjustmentTransaction(tm, in, gv); 
      
        return m;
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
        tbrmks = new javax.swing.JTextField();
        tbitem = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        lblcc = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tblotserial = new javax.swing.JTextField();
        tbref = new javax.swing.JTextField();
        tbqty = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        dcdate = new com.toedter.calendar.JDateChooser();
        ddtype = new javax.swing.JComboBox();
        ddloc = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        ddcc = new javax.swing.JComboBox();
        btadd = new javax.swing.JButton();
        ddacct = new javax.swing.JComboBox();
        lblacct = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        ddwh = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        btLookUpItemDesc = new javax.swing.JButton();
        btLookUpAcctDesc = new javax.swing.JButton();
        dcexpire = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        btgenerate = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Inventory Adjustment (Issue / Receipt)"));
        jPanel1.setName("panelmain"); // NOI18N

        tbitem.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbitemFocusLost(evt);
            }
        });

        jLabel3.setText("Site:");
        jLabel3.setName("lblsite"); // NOI18N

        lblcc.setText("cc:");
        lblcc.setName("lblcc"); // NOI18N

        jLabel2.setText("Item:");
        jLabel2.setName("lblitem"); // NOI18N

        jLabel6.setText("EffDate");
        jLabel6.setName("lbleffdate"); // NOI18N

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel7.setText("Serial/Lot:");
        jLabel7.setName("lblserial"); // NOI18N

        jLabel5.setText("Qty:");
        jLabel5.setName("lblqty"); // NOI18N

        dcdate.setDateFormatString("yyyy-MM-dd");

        ddtype.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "receipt", "issue" }));
        ddtype.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ddtypeItemStateChanged(evt);
            }
        });

        jLabel1.setText("Type:");
        jLabel1.setName("lbltype"); // NOI18N

        jLabel4.setText("Location:");
        jLabel4.setName("lblloc"); // NOI18N

        btadd.setText("Commit");
        btadd.setName("btcommit"); // NOI18N
        btadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddActionPerformed(evt);
            }
        });

        lblacct.setText("Acct:");
        lblacct.setName("lblacct"); // NOI18N

        jLabel9.setText("Remarks:");
        jLabel9.setName("lblremarks"); // NOI18N

        jLabel8.setText("Reference:");
        jLabel8.setName("lblref"); // NOI18N

        ddwh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddwhActionPerformed(evt);
            }
        });

        jLabel10.setText("Warehouse:");
        jLabel10.setName("lblwh"); // NOI18N

        btLookUpItemDesc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpItemDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpItemDescActionPerformed(evt);
            }
        });

        btLookUpAcctDesc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        btLookUpAcctDesc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btLookUpAcctDescActionPerformed(evt);
            }
        });

        dcexpire.setDateFormatString("yyyy-MM-dd");

        jLabel11.setText("Expire");
        jLabel11.setName("lblexpiredate"); // NOI18N

        btgenerate.setText("generate");
        btgenerate.setName("btgenerate"); // NOI18N
        btgenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btgenerateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(lblacct)
                    .addComponent(lblcc)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btadd)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(ddwh, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddcc, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddacct, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddtype, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbitem, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddsite, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddloc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tbqty, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcdate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tblotserial, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbref, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                            .addComponent(dcexpire, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btLookUpItemDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btLookUpAcctDesc, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btgenerate))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tbitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2))
                            .addComponent(btLookUpItemDesc))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddwh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblacct))
                            .addComponent(btLookUpAcctDesc))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblcc))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dcdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dcexpire, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tblotserial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(btgenerate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbrmks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btadd)
                .addContainerGap())
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddActionPerformed
        
        boolean requireWHLoc = BlueSeerUtils.ConvertStringToBool(getSysMetaValue("system", "inventorycontrol", "operation_whloc_required"));
        
        if (tbitem.getText().isEmpty()) {
            tbitem.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1024, tbitem.getName()));
            tbitem.requestFocus();
            return;
        }
        
        if (tbqty.getText().isEmpty()) {
            tbqty.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1036));
            tbqty.requestFocus();
            return;
        }
        
        if (requireWHLoc && ddwh.getSelectedItem().toString().isEmpty()) {
           // ddwh.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1190));
            ddwh.requestFocus();
            return;
        }
        
        if (requireWHLoc && ddloc.getSelectedItem().toString().isEmpty()) {
           // ddwh.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1190));
            ddloc.requestFocus();
            return;
        }
        
        
        // check if item exists
        if (! OVData.isValidItem(tbitem.getText())) {
            bsmf.MainFrame.show(getMessageTag(1026, tbitem.getText()));
            return;
        }
        
        setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.run, new String[]{""});
        
    }//GEN-LAST:event_btaddActionPerformed

    private void tbitemFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbitemFocusLost
        if (! tbitem.getText().isEmpty()) {
            if (! OVData.isValidItem(tbitem.getText())) {
                bsmf.MainFrame.show(getMessageTag(1021, tbitem.getText()));
                tbitem.setBackground(Color.yellow);
                tbitem.requestFocus();
            } else {
              tbitem.setBackground(Color.white);
              getiteminfo(tbitem.getText());   
             }
        }
    }//GEN-LAST:event_tbitemFocusLost

    private void ddtypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ddtypeItemStateChanged
        if (ddtype.getSelectedItem().toString().equals("issue")) {
            lblacct.setText("Debit Acct:");
            lblcc.setText("Debit CC:");
        } else {
            lblacct.setText("Credit Acct:");
            lblcc.setText("Credit CC:");
        }
    }//GEN-LAST:event_ddtypeItemStateChanged

    private void ddwhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddwhActionPerformed
      if (ddwh.getSelectedItem() != null) {
             ddloc.removeAllItems();
             ArrayList<String> loc = OVData.getLocationListByWarehouse(ddwh.getSelectedItem().toString());
             for (String lc : loc) {
                ddloc.addItem(lc);
             }
             ddloc.insertItemAt("", 0);
        }
    }//GEN-LAST:event_ddwhActionPerformed

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
        String x = BlueSeerUtils.bsformat("", tbqty.getText(), "0");
        if (x.equals("error")) {
            tbqty.setText("");
            tbqty.setBackground(Color.yellow);
            bsmf.MainFrame.show(getMessageTag(1000));
            tbqty.requestFocus();
        } else {
            tbqty.setText(x);
            tbqty.setBackground(Color.white);
        }
    }//GEN-LAST:event_tbqtyFocusLost

    private void btLookUpItemDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpItemDescActionPerformed
        lookUpFrameItemDesc();
    }//GEN-LAST:event_btLookUpItemDescActionPerformed

    private void btLookUpAcctDescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btLookUpAcctDescActionPerformed
        lookUpFrameAcctDesc();
    }//GEN-LAST:event_btLookUpAcctDescActionPerformed

    private void btgenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btgenerateActionPerformed
       tblotserial.setText(String.valueOf(OVData.getNextNbr("serial")));
    }//GEN-LAST:event_btgenerateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btLookUpAcctDesc;
    private javax.swing.JButton btLookUpItemDesc;
    private javax.swing.JButton btadd;
    private javax.swing.JButton btgenerate;
    private com.toedter.calendar.JDateChooser dcdate;
    private com.toedter.calendar.JDateChooser dcexpire;
    private static javax.swing.JComboBox ddacct;
    private javax.swing.JComboBox ddcc;
    private javax.swing.JComboBox ddloc;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddtype;
    private javax.swing.JComboBox<String> ddwh;
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
    private javax.swing.JLabel lblacct;
    private javax.swing.JLabel lblcc;
    private static javax.swing.JTextField tbitem;
    private javax.swing.JTextField tblotserial;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbrmks;
    // End of variables declaration//GEN-END:variables
}
