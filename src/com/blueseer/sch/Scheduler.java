/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn "VCSCode"

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
package com.blueseer.sch;

import bsmf.MainFrame;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import com.toedter.calendar.DateUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import static bsmf.MainFrame.con;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import javax.swing.JPanel;

/**
 *
 * @author vaughnte
 */
public class Scheduler extends javax.swing.JPanel {

    // NOTE:  if you change this...you must also adjust APCheckRun...my advise....dont change it
       Scheduler.MyTableModel mymodel = new Scheduler.MyTableModel(new Object[][]{},
                        new String[]{"PlanNbr", "Part", "DueDate", "Type", "isSched", "Cell", "QtySched", "QtyRequired", "QtyComp", "SchedDate", "Order", "line", "Status", "Print", "Update", "Void"})
               {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 13 || col == 14 || col == 15  ) {      
                            return ImageIcon.class;  
                        } else if (col == 6 || col == 7 || col == 8) {
                            return Integer.class;
                        } else if (col == 4) {
                            return Boolean.class;    
                        } else {
                            return String.class;
                        }  //other columns accept String values  
                      }  
                        };
      
      
    MyTableModelDetail modeldetail = new MyTableModelDetail(new Object[][]{},
           new String[]{"PlanNbr", "Part", "Type", "Cell", "QtySched", "Status"});
     
    
    boolean isLoad = false;
    String[] cells = null;
    String startdate = "";
    String enddate = "";
    String cumstartdate = "";
    String cumenddate = "";
    double schtot = 0;
    double reqtot = 0;
    
    class ComboBoxRenderer extends JComboBox implements TableCellRenderer {
  public ComboBoxRenderer(String[] items) {
    super(items);
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {
    if (isSelected) {
      setForeground(table.getSelectionForeground());
      super.setBackground(table.getSelectionBackground());
    } else {
      setForeground(table.getForeground());
      setBackground(table.getBackground());
    }
    setSelectedItem(value);
    return this;
  }
}

  class ComboBoxEditor extends DefaultCellEditor {
  public ComboBoxEditor(String[] items) {
    super(new JComboBox(items));
  }
} 
      
  class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {

          CheckBoxRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            
          }

          public Component getTableCellRendererComponent(JTable table, Object value,
              boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
              setForeground(table.getSelectionForeground());
              //super.setBackground(table.getSelectionBackground());
              setBackground(table.getSelectionBackground());
            } else {
              setForeground(table.getForeground());
              setBackground(table.getBackground());
            }
            setSelected((value != null && ((Boolean) value).booleanValue()));
            return this;
          }
} 
      
  class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
       boolean[] canEdit = new boolean[]{
                false, false, false, false, false, true, true, false, false, true, false, false, false, false, false, false
        };

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            // plan is closed
            if (mytable.getModel().getValueAt(rowIndex, 12).equals("closed")) {   // 1
               canEdit = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};  
            } else if (mytable.getModel().getValueAt(rowIndex, 12).equals("voided")) {   // -1
               canEdit = new boolean[]{false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};  
            } else {
               canEdit = new boolean[]{false, false, false, false, false, true, true, false, false, true, false, false, false, false, false, false};  
            }
            
            return canEdit[columnIndex];
        }
     //     public Class getColumnClass(int col) {  
     //       if (col == 6)       
     //           return Double.class;  
     //       else return String.class;  //other columns accept String values  
    //    }  
        
        public Class getColumnClass(int column) {
            
            
               if (column == 6)       
                return Double.class; 
               else if (column == 4) 
                   return Boolean.class;
            else return String.class;  //other columns accept String values 
            
       /*     
      if (column >= 0 && column < getColumnCount()) {
          
          
           if (getRowCount() > 0) {
             // you need to check 
             Object value = getValueAt(0, column);
             // a line for robustness (in real code you probably would loop all rows until
             // finding a not-null value 
             if (value != null) {
                return value.getClass();
             }

        }
          
          
          
      }  
              
        return Object.class;
*/
               }
       
        
        
   }    
    
  class MyTableModelDetail extends DefaultTableModel {  
      
        public MyTableModelDetail(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
            if (col == 3 )       
                return Integer.class;  
            else return String.class;  //other columns accept String values  
        }  
      @Override  
      public boolean isCellEditable(int row, int col) {  
        if (col == 0)       //first column will be uneditable  
            return false;  
        else return true;  
      }  
       
      
      List<Color> rowColours = Arrays.asList(
        Color.RED,
        Color.GREEN,
        Color.CYAN
    );

    public void setRowColour(int row, Color c) {
        rowColours.set(row, c);
        fireTableRowsUpdated(row, row);
    }

    public Color getRowColour(int row) {
        return rowColours.get(row);
    }

      
        }    
    
  
  class SomeRenderer extends DefaultTableCellRenderer {
         
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
     
        
        
        /*
        String status = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 7);  // 7 = status column
        if ("error".equals(status)) {
            c.setBackground(Color.red);
            c.setForeground(Color.WHITE);
        } else if ("close".equals(status)) {
            c.setBackground(Color.blue);
            c.setForeground(Color.WHITE);
        } else if ("backorder".equals(status)) {
            c.setBackground(Color.yellow);
            c.setForeground(Color.BLACK);
        }
        else {
            c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
        }   
        */
        
        //c.setBackground(row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
      // c.setBackground(row % 2 == 0 ? Color.GREEN : Color.LIGHT_GRAY);
      // c.setBackground(row % 3 == 0 ? new Color(245,245,220) : Color.LIGHT_GRAY);
       
        if (isSelected)
        {
            setBackground(table.getSelectionBackground());
            setForeground(Color.BLACK);
           
        }
        else
        {
              String status = (String) mytable.getModel().getValueAt(table.convertRowIndexToModel(row), 12);  // 7 = status column
             
              if (status.equals("closed")) {
              setForeground(Color.blue);
             } else if (status.equals("voided")) {
              setForeground(Color.red);   
             } else {
              setBackground(table.getBackground());
              setForeground(table.getForeground());
              }
        }
        
            boolean issched = (Boolean) mytable.getModel().getValueAt(table.convertRowIndexToModel(row), 4);
            if (( column == 5 || column == 6) && ! issched ) {
            c.setBackground(Color.green);
            c.setForeground(Color.BLACK);
            }
            else {
                c.setBackground(table.getBackground());
            }
        
           
            
            return c;
    }
    }
    
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
                setForeground(table.getForeground());
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            if (mytable.getModel().getValueAt(row, 2).toString().compareTo("approved") == 0) {
            setBackground(Color.green);
            //setEnabled(false);
            setText("complete");
            }
            return this;
        }
    }
    
  class ButtonEditor extends DefaultCellEditor {

        protected JButton button;
        private String label;
        private String columnname;
        private int myrow;
        private int mycol;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            label = (value == null) ? "" : value.toString();
            columnname = String.valueOf(column);
            button.setText(label);
            //button.setText("approve");
            
            isPushed = true;
           
            return button;
        }
/*
        public Object getCellEditorValue() {
           
        }
*/
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
    
    
    
    /**
     * Creates new form MRPBrowse1
     */
    public Scheduler() {
        initComponents();
    }

     public void printtickets(String fromjob, String tojob ) {
        
       try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;

                HashMap hm = new HashMap();
                hm.put("REPORT_TITLE", "CUT TICKET");
                 hm.put("SUBREPORT_DIR", "jasper/");
                hm.put("fromjob",  fromjob);
                hm.put("tojob", tojob);
                //hm.put("imagepath", "images/avmlogo.png");
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_part, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/jobticketmulti.jasper");
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, bsmf.MainFrame.con );
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, bsmf.MainFrame.con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/jobticketmulti.pdf");
         
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);
                
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("sql problem printing ticket");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
    }
    
    public void printticket(String jobid, String bustitle) {
        
       try {
            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                String jasperfile = "jobticket.jasper";  // need to make this changeable via site_mstr

                HashMap hm = new HashMap();
                hm.put("BUSINESSTITLE", bustitle);
                hm.put("REPORT_TITLE", jasperfile);
                 hm.put("SUBREPORT_DIR", "jasper/");
                hm.put("myid",  jobid);
                //hm.put("imagepath", "images/avmlogo.png");
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_part, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/" + jasperfile);
              //  JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, hm, bsmf.MainFrame.con );
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, bsmf.MainFrame.con );
                JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/jobticket.pdf");
         
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);
                
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("sql problem printing single ticket");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
        
    }
    
    public void postcommit() {
        java.util.Date now = new java.util.Date();
         
         Calendar calfrom = Calendar.getInstance();
         calfrom.add(Calendar.DATE, -7);
         dcfrom.setDate(calfrom.getTime());
         
         
         Calendar calto = Calendar.getInstance();
         calto.add(Calendar.DATE, 14);
         dcto.setDate(calto.getTime());
         
        
         frompart.setText("");
         topart.setText("");
         
         
         mymodel.setRowCount(0);
         mytable.setModel(mymodel);
          
          
          
             
         
         
    }
    
    public void initvars(String[] arg) {
        
        isLoad = true;
        
        PanelDetail.setVisible(false);
        
        java.util.Date now = new java.util.Date();
         
         Calendar calfrom = Calendar.getInstance();
         // from previous Sunday
         calfrom.add(Calendar.DAY_OF_WEEK, -(calfrom.get(Calendar.DAY_OF_WEEK)-1));
         dcfrom.setDate(calfrom.getTime());
         
         
         Calendar calto = calfrom;
         calto.add(Calendar.DATE, 21);
         // to following Saturday
        // while (calto.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
       //  calto.add(Calendar.DATE, 1);
       //  }
         dcto.setDate(calto.getTime());
         
        
         frompart.setText("");
         topart.setText("");
         
         mymodel.setRowCount(0);
         mytable.setModel(mymodel);
         
         modeldetail.setRowCount(0);
         tabledetail.setModel(modeldetail);
        
        ddsite.removeAllItems();
        ArrayList<String>  mylist = OVData.getSiteList();
        for (String code : mylist) {
          ddsite.addItem(code);
        } 
         
        ddcellchoice.removeAllItems();
        ArrayList<String>  cells_list = OVData.getCodeMstrKeyList("CELL");
        cells = cells_list.toArray(new String[cells_list.size()]);
        for (String code : cells_list) {
          ddcellchoice.addItem(code);
        } 
        ddcellchoice.insertItemAt("ALL", 0);
        ddcellchoice.setSelectedIndex(0);
        
        isLoad = false;
       
             
         
         
    }
    
    public void getDetail() {
         modeldetail.setRowCount(0);
         DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));
         DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
      
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                int i = 0;  
                
                if (ddcellchoice.getSelectedItem().toString().equals("ALL")) {
                res = st.executeQuery("SELECT plan_nbr, plan_type, plan_part, plan_qty_req, plan_qty_comp, "
                        + " plan_qty_sched, plan_date_due, plan_date_sched, plan_status, ifnull(plan_is_sched,0) plan_is_sched, plan_cell, plan_order, plan_line " +
                        " FROM  plan_mstr " +
                        " where plan_date_sched = " + "'" + dfdate.format(jc.getDate()) + "'" +
                        " AND plan_is_sched = " + "'1' "  +
                        " order by plan_part, plan_cell;");
                } else {
                res = st.executeQuery("SELECT plan_nbr, plan_type, plan_part, plan_qty_req, plan_qty_comp, "
                        + " plan_qty_sched, plan_date_due, plan_date_sched, plan_status, ifnull(plan_is_sched,0) plan_is_sched, plan_cell, plan_order, plan_line " +
                        " FROM  plan_mstr " +
                        " where plan_date_sched = " + "'" + dfdate.format(jc.getDate()) + "'" +
                        " AND plan_cell = " + "'" + ddcellchoice.getSelectedItem().toString() + "'" +
                        " AND plan_is_sched = " + "'1' "  +
                        " order by plan_part, plan_cell;");    
                }
                while (res.next()) {
                   modeldetail.addRow(new Object[]{ 
                      res.getString("plan_nbr"), 
                       res.getString("plan_part"),
                       res.getString("plan_type"),
                       res.getString("plan_cell"),
                       res.getString("plan_qty_sched"),
                      res.getString("plan_status")});
                }
               
                this.repaint();

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("Unable to get Detail");
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }    
    
    public ArrayList<String[]> getSummaryByDate(String fromdate, String todate) {
        
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<String[]> arr = new ArrayList<String[]>();
        try {

            Class.forName(bsmf.MainFrame.driver).newInstance();
            bsmf.MainFrame.con = DriverManager.getConnection(bsmf.MainFrame.url + bsmf.MainFrame.db, bsmf.MainFrame.user, bsmf.MainFrame.pass);
            try {
                Statement st = bsmf.MainFrame.con.createStatement();
                ResultSet res = null;
                
                if (ddcellchoice.getSelectedItem().toString().equals("ALL")) {
                res = st.executeQuery("SELECT sum(plan_qty_sched) as 'sum', plan_date_sched, plan_cell " +
                        " FROM  plan_mstr " +
                        " where plan_date_sched >= " + "'" + fromdate + "'" +
                        " AND plan_date_sched <= " + "'" + todate + "'"  +
                        " AND plan_is_sched = " + "'1' "  +
                        " group by plan_date_sched, plan_cell order by plan_date_sched;");
                } 
                while (res.next()) {
                    String[] s = new String[]{res.getString("plan_date_sched"), res.getString("plan_cell"),res.getString("sum") };
                    arr.add(s);
                }

            } catch (SQLException s) {
                s.printStackTrace();
            }
            bsmf.MainFrame.con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

        return arr;
    }    
    
    public void adjustCalendar() {
        
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        
        JPanel jPanel = jc.getDayChooser().getDayPanel();
        Component component[] = jPanel.getComponents();
        Calendar cal = Calendar.getInstance();
        cal.setTime(jc.getDate());
        
        // first day of month
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String firstday = dfdate.format(cal.getTime());
        int offset = cal.get(Calendar.DAY_OF_WEEK);
        // last day of month
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastday = dfdate.format(cal.getTime());
        
        // reset calendar to current date clicked
        cal.setTime(jc.getDate());
        String x = "";
        int sum = 0;
        ArrayList<String[]> list = getSummaryByDate(firstday,lastday);
        
        for (int z = 1; z <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); z++) {
            x = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + String.format("%02d", z) ;
            sum = 0;
            for (String[] k : list) {
                if (k[0].equals(x)) {
                    sum += Integer.valueOf(k[2]);
                }
            }
            if (sum > 100) {
                
                 cal.set(Calendar.DAY_OF_MONTH,z);
                 component[z + 6 + (offset - 1)].setBackground(Color.green);
                 // bsmf.MainFrame.show(x + "/" + z + "/" + offset);
            }
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        bthide = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jc = new com.toedter.calendar.JCalendar();
        dcfrom = new com.toedter.calendar.JDateChooser();
        dcto = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        frompart = new javax.swing.JTextField();
        topart = new javax.swing.JTextField();
        cbsched = new javax.swing.JCheckBox();
        cbclosed = new javax.swing.JCheckBox();
        btRun = new javax.swing.JButton();
        btcommit = new javax.swing.JButton();
        labelcount = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        labelqtysched = new javax.swing.JLabel();
        labelqtyreqd = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ddcellchoice = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        btdetail = new javax.swing.JButton();
        PanelReport = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mytable = new javax.swing.JTable();
        PanelDetail = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();

        setBackground(new java.awt.Color(0, 102, 204));
        setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("MRP Browse"));

        bthide.setText("Hide Detail");
        bthide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bthideActionPerformed(evt);
            }
        });

        jLabel3.setText("Site");

        jc.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jc.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jcPropertyChange(evt);
            }
        });

        dcfrom.setDateFormatString("yyyy-MM-dd");

        dcto.setDateFormatString("yyyy-MM-dd");

        jLabel7.setText("From:");

        jLabel8.setText("To:");

        cbsched.setText("Unscheduled Only?");

        cbclosed.setText("OpenOnly?");
        cbclosed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbclosedActionPerformed(evt);
            }
        });

        btRun.setText("Run");
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        btcommit.setText("Commit");
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        labelcount.setText("0");

        jLabel9.setText("Rows");

        labelqtysched.setText("0");

        labelqtyreqd.setText("0");

        jLabel1.setText("Qty Sched");

        jLabel2.setText("Qty Reqd");

        jLabel4.setText("Cell:");

        btdetail.setText("Detail");
        btdetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdetailActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel8)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(frompart, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                                .addComponent(topart))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(dcto, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                                    .addComponent(dcfrom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addGap(3, 3, 3)
                                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cbsched)
                                .addGap(5, 5, 5)
                                .addComponent(cbclosed, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btRun)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btcommit))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(labelqtysched, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelqtyreqd, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))))
            .addComponent(jc, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddcellchoice, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdetail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bthide))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)))
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(frompart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(topart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbsched)
                    .addComponent(cbclosed))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btRun)
                    .addComponent(btcommit))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelqtyreqd, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(labelqtysched, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jc, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddcellchoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(btdetail)
                    .addComponent(bthide))
                .addContainerGap())
        );

        mytable.setModel(new javax.swing.table.DefaultTableModel(
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
        mytable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mytableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(mytable);

        javax.swing.GroupLayout PanelReportLayout = new javax.swing.GroupLayout(PanelReport);
        PanelReport.setLayout(PanelReportLayout);
        PanelReportLayout.setHorizontalGroup(
            PanelReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
        );
        PanelReportLayout.setVerticalGroup(
            PanelReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
        );

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
        jScrollPane2.setViewportView(tabledetail);

        javax.swing.GroupLayout PanelDetailLayout = new javax.swing.GroupLayout(PanelDetail);
        PanelDetail.setLayout(PanelDetailLayout);
        PanelDetailLayout.setHorizontalGroup(
            PanelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDetailLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        PanelDetailLayout.setVerticalGroup(
            PanelDetailLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelDetail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(PanelReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(PanelDetail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void bthideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bthideActionPerformed
       PanelDetail.setVisible(false);
       // tabledetail.setVisible(false);
       // btdetail.setEnabled(false);
    }//GEN-LAST:event_bthideActionPerformed

    private void mytableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mytableMouseClicked
          int row = mytable.rowAtPoint(evt.getPoint());
        int col = mytable.columnAtPoint(evt.getPoint());
        if ( col == 13) {
              printticket(mytable.getValueAt(row, 0).toString(), "Work Order");
        }
        
        if (col == 14)   {
                    if ( mytable.getValueAt(row, 12).equals("open")) {
                        boolean isGood = OVData.updatePlanOrder(mytable.getValueAt(row, 0).toString(), 
                        mytable.getValueAt(row, 6).toString(),
                        mytable.getValueAt(row, 5).toString(),
                        mytable.getValueAt(row, 9).toString(),
                        mytable.getValueAt(row,12).toString() 
                         );  
                           if (! isGood) {
                               bsmf.MainFrame.show("Unable to update");
                           } else {
                               bsmf.MainFrame.show("Record Updated");
                           }
                   } 
        }
        if (col == 15)   {
                    if ( mytable.getValueAt(row, 12).equals("open")) {
                        OVData.updatePlanStatus(mytable.getValueAt(row, 0).toString(), "-1");
                        bsmf.MainFrame.show("JobNbr Voided");
                        mytable.setValueAt("voided", row, 12);
                   } 
        }
        
         
    }//GEN-LAST:event_mytableMouseClicked

    private void btRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRunActionPerformed
        schtot = 0;
        reqtot = 0;

        labelqtysched.setText("0");
        labelqtyreqd.setText("0");
        labelcount.setText("0");

        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);
            try {
                Statement st = con.createStatement();
                ResultSet res = null;

                double amt = 0;

                DecimalFormat df = new DecimalFormat("###,###,###.##", new DecimalFormatSymbols(Locale.US));
                int i = 0;
                String fpart = "";
                String tpart = "";
                String fcell = bsmf.MainFrame.lowchar;
                String tcell = bsmf.MainFrame.hichar;
                String status = "";

                if (frompart.getText().isEmpty()) {
                    fpart = bsmf.MainFrame.lowchar;
                } else {
                    fpart = frompart.getText();
                }
                if (topart.getText().isEmpty()) {
                    tpart = bsmf.MainFrame.hichar;
                } else {
                    tpart = topart.getText();
                }
               

                //  ScrapReportPanel.MyTableModel mymodel = new ScrapReportPanel.MyTableModel(new Object[][]{},
                    //         new String[]{"Acct", "Description", "Amt"});
                // tablescrap.setModel(mymodel);

                //  mytable.getColumnModel().getColumn(0).setCellRenderer(new ProdSchedPanel.SomeRenderer());

                CheckBoxRenderer checkBoxRenderer = new CheckBoxRenderer();
                mytable.getColumnModel().getColumn(4).setCellRenderer(checkBoxRenderer);

                //  ComboBoxRenderer comboBoxRenderer = new ComboBoxRenderer(new String[]{"1","2"});
                //  mytable.getColumnModel().getColumn(5).setCellRenderer(comboBoxRenderer);
                
                TableColumn col = mytable.getColumnModel().getColumn(5);
                col.setCellEditor(new ComboBoxEditor(cells));
                col.setCellRenderer(new ComboBoxRenderer(cells));

                Enumeration<TableColumn> en = mytable.getColumnModel().getColumns();
                while (en.hasMoreElements()) {
                    TableColumn tc = en.nextElement();
                    if (tc.getIdentifier().toString().equals("isSched") ||
                        tc.getIdentifier().toString().equals("Cell") ||
                        tc.getIdentifier().toString().equals("Print") ||
                        tc.getIdentifier().toString().equals("Update") ||
                        tc.getIdentifier().toString().equals("Void")) {
                        continue;
                    }
                    tc.setCellRenderer(new Scheduler.SomeRenderer());
                }

                //       mytable.getColumn("Update").setCellRenderer(new ProdSchedPanel.ButtonRenderer());
                //        mytable.getColumn("Print").setCellRenderer(new ProdSchedPanel.ButtonRenderer());
                //        mytable.getColumn("Void").setCellRenderer(new ProdSchedPanel.ButtonRenderer());

                DefaultCellEditor singleClick = (DefaultCellEditor) mytable.getDefaultEditor(mytable.getColumnClass(6));
                singleClick.setClickCountToStart(1);
                mytable.setDefaultEditor(mytable.getColumnClass(6), singleClick);
                //   DefaultCellEditor singleClick2 = (DefaultCellEditor) mytable.getDefaultEditor(mytable.getColumnClass(5));
                //  singleClick2.setClickCountToStart(1);
                //   mytable.setDefaultEditor(mytable.getColumnClass(5), singleClick2);

                //  mytable.setDefaultEditor(mytable.getColumnClass(5), singleClick);

                //    mytable.getColumn("Update").setCellEditor(
                    //             new ProdSchedPanel.ButtonEditor(new JCheckBox()));
                //     mytable.getColumn("Print").setCellEditor(
                    //             new ProdSchedPanel.ButtonEditor(new JCheckBox()));
                //      mytable.getColumn("Void").setCellEditor(
                    //              new ProdSchedPanel.ButtonEditor(new JCheckBox()));
                mytable.getColumn("Update").setMaxWidth(100);
                mytable.getColumn("Print").setMaxWidth(100);
                mytable.getColumn("Void").setMaxWidth(100);

                mymodel.setRowCount(0);

                // ReportPanel.TableReport.getColumn("CallID").setCellRenderer(new ButtonRenderer());
                //          ReportPanel.TableReport.getColumn("CallID").setCellEditor(
                    //       new ButtonEditor(new JCheckBox()));

                // tcm.getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer());

                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

                if (cbsched.isSelected()) {
                    res = st.executeQuery("SELECT plan_nbr, plan_type, plan_part, plan_qty_req, plan_qty_comp, "
                        + " plan_qty_sched, plan_date_due, plan_date_sched, plan_status, ifnull(plan_is_sched,0) plan_is_sched, plan_cell, plan_order, plan_line " +
                        " FROM  plan_mstr " +
                        " where plan_date_due >= " + "'" + dfdate.format(dcfrom.getDate()) + "'" +
                        " AND plan_date_due <= " + "'" + dfdate.format(dcto.getDate()) + "'" +
                        " AND plan_part >= " + "'" + fpart + "'" +
                        " AND plan_part <= " + "'" + tpart + "'" +
                        " AND plan_cell >= " + "'" + fcell + "'" +
                        " AND plan_cell <= " + "'" + tcell + "'" +
                        " AND plan_is_sched = " + "'0' "  +
                        " order by plan_part, plan_date_due;");
                } else {
                    res = st.executeQuery("SELECT plan_nbr, plan_part, plan_type, plan_qty_req, plan_qty_comp, "
                        + " plan_qty_sched, plan_date_due, plan_date_sched, plan_status, ifnull(plan_is_sched,0) plan_is_sched, plan_cell, plan_order, plan_line " +
                        " FROM  plan_mstr " +
                        " where plan_date_due >= " + "'" + dfdate.format(dcfrom.getDate()) + "'" +
                        " AND plan_date_due <= " + "'" + dfdate.format(dcto.getDate()) + "'" +
                        " AND plan_part >= " + "'" + fpart + "'" +
                        " AND plan_part <= " + "'" + tpart + "'" +
                        " AND plan_cell >= " + "'" + fcell + "'" +
                        " AND plan_cell <= " + "'" + tcell + "'" +
                        " order by plan_part, plan_date_due ;");
                }
                while (res.next()) {

                    if (cbclosed.isSelected() && res.getInt("plan_status") == 1) {
                        continue;
                    }

                    // plan can be voided by setting to -1
                    if (cbclosed.isSelected() && res.getInt("plan_status") == -1) {
                        continue;
                    }

                    i++;
                    reqtot = reqtot + res.getInt("plan_qty_req");
                    schtot = schtot + res.getInt("plan_qty_sched");
                    if (res.getString("plan_status").equals("0")) { status = "open"; }
                    if (res.getString("plan_status").equals("1")) { status = "closed"; }
                    if (res.getString("plan_status").equals("-1")) { status = "voided"; }

                    mymodel.addRow(new Object[]{
                        res.getString("plan_nbr"),
                        res.getString("plan_part"),
                        res.getString("plan_date_due"),
                        res.getString("plan_type"),
                        res.getBoolean("plan_is_sched"),
                        res.getString("plan_cell"),
                        res.getInt("plan_qty_sched"),
                        res.getInt("plan_qty_req"),
                        res.getInt("plan_qty_comp"),
                        res.getString("plan_date_sched"),
                        res.getString("plan_order"),
                        res.getString("plan_line"),
                        status,
                        BlueSeerUtils.clickprint,
                        BlueSeerUtils.clickrefresh,
                        BlueSeerUtils.clicktrash
                    });
                }
                labelqtysched.setText(String.valueOf(schtot));
                labelqtyreqd.setText(String.valueOf(reqtot));
                labelcount.setText(String.valueOf(i));

        adjustCalendar();
                
                
                //    RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(mymodel);
                //     mytable.setRowSorter(sorter);

            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show("sql problem running view");
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }

    }//GEN-LAST:event_btRunActionPerformed

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
        boolean commit = true;
        int count = 0;
        java.util.Date now = new java.util.Date();
        // clean out the unchecked rows

        for (int i = 0 ; i < mymodel.getRowCount(); i++) {

            if (! mymodel.getValueAt(i, 12).equals("open") && ! mymodel.getValueAt(i, 12).equals("closed") && ! mymodel.getValueAt(i, 12).equals("voided")) {
                bsmf.MainFrame.show("Invalid entry in status field...must be 'closed', 'open', 'voided'");
                commit = false;
                break;
            }
        }

        if (commit) {
            // remove rows that are already scheduled 'true'
            for (int i = 0 ; i < mymodel.getRowCount(); i++) {
                if ( (boolean) mymodel.getValueAt(i, 4) == true ) {
                    //    bsmf.MainFrame.show("yep1:" + mymodel.getValueAt(i, 0).toString());
                    mymodel.removeRow(i);
                    i--;

                }
            }

            // now remove rows that have blank cell and QtySched
            for (int i = 0 ; i < mymodel.getRowCount(); i++) {
                if ( mymodel.getValueAt(i, 5).toString().isEmpty() || mymodel.getValueAt(i, 5).toString() == null || mymodel.getValueAt(i, 6).toString().isEmpty() || mymodel.getValueAt(i, 6).toString() == null  ) {
                    //    bsmf.MainFrame.show("yep2:" + mymodel.getValueAt(i, 0).toString());
                    mymodel.removeRow(i);
                    i--;

                }
            }

            // all that should be left are lines to be scheduled
            if (mytable.getRowCount() > 0) {
                count = OVData.CommitSchedules(mytable);
            }
            postcommit();
            bsmf.MainFrame.show(String.valueOf(count) + " " + "Schedules Committed ");
        }
    }//GEN-LAST:event_btcommitActionPerformed

    private void cbclosedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbclosedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbclosedActionPerformed

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
        getDetail();
        PanelDetail.setVisible(true);
    }//GEN-LAST:event_btdetailActionPerformed

    private void jcPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jcPropertyChange
       if (! evt.getPropertyName().toLowerCase().equals("ancestor")) {
           String[] o = evt.getOldValue().toString().split(",");
           String[] n = evt.getNewValue().toString().split(",");
           
           if (! o[30].equals(n[30])) {
               adjustCalendar();
           }
          
       
        
       }
    }//GEN-LAST:event_jcPropertyChange


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelDetail;
    private javax.swing.JPanel PanelReport;
    private javax.swing.JButton btRun;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btdetail;
    private javax.swing.JButton bthide;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbclosed;
    private javax.swing.JCheckBox cbsched;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JComboBox<String> ddcellchoice;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JTextField frompart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private com.toedter.calendar.JCalendar jc;
    private javax.swing.JLabel labelcount;
    private javax.swing.JLabel labelqtyreqd;
    private javax.swing.JLabel labelqtysched;
    private javax.swing.JTable mytable;
    private javax.swing.JTable tabledetail;
    private javax.swing.JTextField topart;
    // End of variables declaration//GEN-END:variables
}
