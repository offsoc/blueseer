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

/**
 *
 * @author vaughnte
 */
import static bsmf.MainFrame.tags;
import static com.blueseer.edi.EDI.packageEnvelopes;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import com.blueseer.utl.EDData;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class EDILoad {
 public static boolean isDebug = false;   
 public static String  now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));   
    
public static void main(String[] args) {
 
    bsmf.MainFrame.setConfig();
    tags = ResourceBundle.getBundle("resources.bs", Locale.getDefault());
    String[] vs = checkargs(args);  
    String prog = vs[0];
    String[] doctypes = null;
    String site = vs[2];
    if (vs[1] != null && ! vs[1].isEmpty()) {
     doctypes = vs[1].split(",");
    }
    String myargs = String.join(",", args);
    switch (prog) {
        
        case "translation" :
            runTranslation(args);
            break;
        case "exportFromDB" :
            runExport(doctypes, site);
            break;
        default:
            System.out.println("Unable to process arguments " + myargs);
        
    }
    
    System.exit(0);
}

public static String[] checkargs(String[] args) {
        List<String> legitargs = Arrays.asList("-i", "-o", "-debug", "-site");
     
        String[] vals = new String[9]; // last element is the program type (single or mulitiple)
        Arrays.fill(vals, "");
        
        String myargs = String.join(",", args);
        
       
         
         
        // now process the qualifiers
        for (int i = 0; i < args.length ;i++) {
            if (args[i].substring(0,1).equals("-")) {
              
              // first make sure -xx argument qualifier is legit  
              if (! legitargs.contains(args[i])) {
                  System.out.println("Bad Qualifier");
                  System.exit(1);
              }
              
               if ( (args.length > i+1 && args[i+1] != null) || (args[i].equals("-i")) ) {
                
                 switch (args[i].toLowerCase()) {                    
                    case "-i" :
                        vals[1] = ""; // no parameters for translation
                        vals[0] = "translation"; 
                        break;      
                    case "-o" :
                        vals[1] = args[i+1];
                        vals[0] = "exportFromDB"; 
                        break;  
                    case "-site" :
                        vals[2] = args[i+1];
                        break;     
                    case "-debug" :
                        isDebug = true; 
                        break; 
                    default:
                        System.out.println("Unable to process arguments " + myargs);
                        System.exit(1);
                 }
                                    
               } else {
                  System.out.println("missing value for qualifier " + myargs);
                  System.exit(1);
               }
            }
             
           
         }
        return vals;
    }


public static void runTranslation(String[] args) {
    try {
     
            boolean isDebug = false;
            if (args != null && args.length > 0) {
                if (args[0].equals("-debug")) {
                    isDebug = true;
                }
            }
           
            String inDir = cleanDirString(EDData.getEDIInDir());
            String inArch = cleanDirString(EDData.getEDIInArch()); 
            String ErrorDir = cleanDirString(EDData.getEDIErrorDir()); 
               String archpath = inArch;
               File folder = new File(inDir);
               File[] listOfFiles = folder.listFiles();
               if (listOfFiles == null) {
                   System.out.println("EDILoad ERROR:  Unable to list inDir...possible DB read issue ");
                   return;
               }

               if (listOfFiles.length == 0) {
                   System.out.println("EDILoad:  No files to process");
               }
              for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                System.out.println("EDILoad:  processing file " + listOfFiles[i].getName());
                  if(listOfFiles[i].length() == 0) { 
                  listOfFiles[i].delete();
                  } else { 
                 String[] m = EDI.processFile(inDir + listOfFiles[i].getName(),"","","", isDebug, false, 0, 0, "");
                 
                 // show error if exists...usually malformed envelopes
                    if (m[0].equals("1")) {
                        System.out.println(m[1]);
                        // now move to error folder
                        Path movefrom = FileSystems.getDefault().getPath(inDir + listOfFiles[i].getName());
                        Path errortarget = FileSystems.getDefault().getPath(ErrorDir + listOfFiles[i].getName());
                       
                         Files.move(movefrom, errortarget, StandardCopyOption.REPLACE_EXISTING);
                         continue;  // bale from here
                    }
                    
                    // if delete set in control panel...remove file and continue;
                         if (EDData.isEDIDeleteFlag()) {
                          Path filetodelete = FileSystems.getDefault().getPath(inDir + listOfFiles[i].getName());
                          Files.delete(filetodelete);
                         }
                    
                    // now archive file
                         if (! inArch.isEmpty() && ! EDData.isEDIDeleteFlag() && EDData.isEDIArchFlag() ) {
                         Path movefrom = FileSystems.getDefault().getPath(inDir + listOfFiles[i].getName());
                         Path target = FileSystems.getDefault().getPath(inArch + listOfFiles[i].getName());
                        // bsmf.MainFrame.show(movefrom.toString() + "  /  " + target.toString());
                         Files.move(movefrom, target, StandardCopyOption.REPLACE_EXISTING);
                          // now remove from list
                         }
                 
                  
                  }
                }
              }
       } catch (IOException ex) {
          ex.printStackTrace();
       } catch (ClassNotFoundException ex) {
          ex.printStackTrace();
       }
}

public static String[] runTranslationSingleFile(Path targetfilepath) {
    String[] m = new String[]{"",""};
    try {
            String inArch = cleanDirString(EDData.getEDIInArch()); 
            String ErrorDir = cleanDirString(EDData.getEDIErrorDir());
             
                if (targetfilepath.toFile().exists() && targetfilepath.toFile().isFile()) {
                System.out.println("EDILoad:  processing file " + targetfilepath.toString());
                  if(targetfilepath.toFile().length() == 0) { 
                  targetfilepath.toFile().delete();
                  } else { 
                 m = EDI.processFile(targetfilepath.toString(),"","","", isDebug, false, 0, 0, "");
                 
                 // show error if exists...usually malformed envelopes
                    if (m[0].equals("1")) {
                        System.out.println(m[1]);
                        // now move to error folder
                        Path movefrom = FileSystems.getDefault().getPath(targetfilepath.toFile().getName());
                        Path errortarget = FileSystems.getDefault().getPath(ErrorDir + "/" + targetfilepath.toFile().getName());
                        // bsmf.MainFrame.show(movefrom.toString() + "  /  " + target.toString());
                         Files.move(movefrom, errortarget, StandardCopyOption.REPLACE_EXISTING);
                    }
                    
                    // if delete set in control panel...remove file and continue;
                         if (EDData.isEDIDeleteFlag()) {
                          Files.delete(targetfilepath);
                         }
                    
                    // now archive file
                         if (! inArch.isEmpty() && ! EDData.isEDIDeleteFlag() && EDData.isEDIArchFlag() ) {
                         Path archtarget = FileSystems.getDefault().getPath(inArch + "/" + targetfilepath.toFile().getName());
                        // bsmf.MainFrame.show(movefrom.toString() + "  /  " + target.toString());
                         Files.move(targetfilepath, archtarget, StandardCopyOption.REPLACE_EXISTING);
                          // now remove from list
                         }
                 
                  
                  }
                }
            
       } catch (IOException ex) {
          ex.printStackTrace();
       } catch (ClassNotFoundException ex) {
          ex.printStackTrace();
       }
    
    return m;
}

public static void runExport(String[] docs, String site) {
        if (docs == null) {
            System.out.println("no doc list for -o exports");
            return;
        }
        System.out.println("exporting documents...");
        List<String> allowed = Arrays.asList(docs);
        // invoices
        if (allowed.contains("810")) {
          ArrayList<String> list = EDData.getEDIInvoices(bsmf.MainFrame.lowchar, bsmf.MainFrame.hichar, bsmf.MainFrame.lowdate, bsmf.MainFrame.hidate, false);
          ArrayList<String> updateList = new ArrayList<String>();  
            for (String x : list) {
              if (EDI.Create810(x) == 0) {
                updateList.add(x);
                System.out.println(now + " exporting 810: " + x + " Success");
              } else {
                System.out.println(now + " exporting 810: " + x + " Failed");  
              }
            }
            EDData.updateEDIInvoiceStatus(updateList);             
        }
        // acknowledgements
        if (allowed.contains("855")) {
          ArrayList<String> list = EDData.getEDIACKs(bsmf.MainFrame.lowchar, bsmf.MainFrame.hichar, bsmf.MainFrame.lowdate, bsmf.MainFrame.hidate, false);
          ArrayList<String> updateList = new ArrayList<String>();  
            for (String x : list) {
              if (EDI.Create855(x) == 0) {
                updateList.add(x);
                 System.out.println(now + " exporting 855: " + x + " Success");
              } else {
                System.out.println(now + " exporting 855: " + x + " Failed");   
              }
            }
            EDData.updateEDIOrderStatus(updateList);             
        }
        // ASNs
        if (allowed.contains("856")) {
          ArrayList<String> list = EDData.getEDIASNs(bsmf.MainFrame.lowchar, bsmf.MainFrame.hichar, bsmf.MainFrame.lowdate, bsmf.MainFrame.hidate, false);
          ArrayList<String> updateList = new ArrayList<String>();  
            for (String x : list) {
              if (EDI.Create856(x) == 0) {
                updateList.add(x);
                System.out.println(now + " exporting 856: " + x + " Success");
              } else {
                System.out.println(now + " exporting 856: " + x + " Failed");  
              }
            }
            EDData.updateEDIASNStatus(updateList);             
        }
        // Purchase Orders
        if (allowed.contains("850")) {
          ArrayList<String> list = EDData.getEDIPOs(bsmf.MainFrame.lowchar, bsmf.MainFrame.hichar, bsmf.MainFrame.lowdate, bsmf.MainFrame.hidate, false);
          ArrayList<String> updateList = new ArrayList<String>();  
            for (String x : list) {
              if (EDI.Create850(x) == 0) {
                updateList.add(x);
                System.out.println(now + " exporting 850: " + x + " Success");
              } else {
                System.out.println(now + " exporting 850: " + x + " Failed");   
              } 
            }
            EDData.updateEDIPOStatus(updateList);             
        }
        
        // if hanoi is not null
        packageEnvelopes();
}


} // class

