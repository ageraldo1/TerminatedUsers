package com.itk.infor.users;

import com.itk.util.ParameterParser;
import com.itk.infor.s3.Employee;
import com.itk.infor.s3.Identities;
import com.itk.landmark.LMIdentities;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TerminatedUsers {

   private enum operation {
       list,
       generate
   }
   public static void main(String[] args) {
       ParameterParser p = new ParameterParser();
       
       if ( p.validateArgs(args) == false) {
           System.exit(160);
       }
       
       if ( args[0].equals("list")) {
           list(Integer.parseInt(args[1]));
           
       } else if (args[0].equals("generate")) {
           generate(Integer.parseInt(args[1]), args[2]);
       }      
       
       System.out.println ("Process completed without issues");
   }
   
   private static void list (int nr_days) {
       Employee e = new Employee();
       Identities i = new Identities();
       LMIdentities l = new LMIdentities();
       
       ArrayList<String> lmList = new ArrayList<>();
       ArrayList<String> lmServices = new ArrayList<>();
       String lmIdentity = null;
           
        for (HashMap record : e.getEmployeeList(nr_days)) {
            System.out.println ("Company.......................................: " + record.get("COMPANY"));
            System.out.println ("Employee number...............................: " + record.get("EMPLOYEE"));               
            System.out.print   ("S3 actor(s) associated to employee............: [" );               

            boolean firstTime = true;

            for (String employeeRMID : i.getEmployeeRMID((int) (record.get("COMPANY")), (int) record.get("EMPLOYEE"))) {
                if ( firstTime == true ) {
                    firstTime = false;    

                } else {
                    System.out.print (",");                                              
            }

            System.out.print (employeeRMID);

            lmIdentity = l.getActor(employeeRMID);

            if ( lmIdentity != null  ) {
                lmList.add (lmIdentity);
                lmServices = l.getServiceAssociatedToActor(lmIdentity);
            }
         }

         System.out.print ("] \n");
         System.out.println ("Landmark actor(s) associated to employee......: " + lmList);
         System.out.println ("Landmark services(s) associated to employee...: " + lmServices);
         System.out.println ("");

         lmList.clear();
         lmServices.clear();
        }
   }
   
   private static void generate (int nr_days, String generateOption) {
       
       Employee e = new Employee();
       Identities i = new Identities();
       LMIdentities l = new LMIdentities();
       
       BufferedWriter loadUsersFS = null;
       BufferedWriter secadmFS = null;
       
       String lmIdentity = null;
       
       try {
           String loadUsersTemplate = System.getProperty("itk.work.dir") + System.getProperty("file.separator") + "delete_users.xml";
           String secadmTemplate = System.getProperty("itk.work.dir") + System.getProperty("file.separator") + "delete_users.secadm";
           
           loadUsersFS = new BufferedWriter(new FileWriter(loadUsersTemplate));           
           
           if ( !generateOption.equals("s3") ) secadmFS = new BufferedWriter(new FileWriter(secadmTemplate));           

           loadUsersFS.write ("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
           loadUsersFS.newLine();

           loadUsersFS.write ("<XML>");
           loadUsersFS.newLine();
           
           loadUsersFS.write ("<USERDATA>");
           loadUsersFS.newLine();
           
           for (HashMap record : e.getEmployeeList(nr_days)) {
               for (String employeeRMID : i.getEmployeeRMID((int) (record.get("COMPANY")), (int) record.get("EMPLOYEE"))) {
                   
                   System.out.println ("Generating S3 " + employeeRMID + " record for employee " + record.get("EMPLOYEE") + " and company " + record.get("COMPANY") ); 
                   
                   loadUsersFS.write ("\t <USER ID=\"" + employeeRMID + "\"/>");
                   loadUsersFS.newLine();
                   
                   if ( !generateOption.equals("s3") ) {
                       lmIdentity = l.getActor(employeeRMID);

                       if ( lmIdentity != null  ) {
                           for (String service : l.getServiceAssociatedToActor(lmIdentity)) {
                               System.out.println ("Generating Landmark record " + lmIdentity + " for the service " + service); 
                               
                               secadmFS.write ("identity delete " + service + " " + lmIdentity);
                               secadmFS.newLine();                           
                           }

                           secadmFS.write ("actor delete " + lmIdentity);
                           secadmFS.newLine();                           
                       }
                   }
               }
           }

           loadUsersFS.write ("</USERDATA>");
           loadUsersFS.newLine();

           loadUsersFS.write ("</XML>");
           loadUsersFS.newLine();
           
           
       } catch ( IOException ex) {
           System.out.println (ex);
           
       }  finally {
           
           try {
               if ( loadUsersFS != null ) {
                   loadUsersFS.flush();
                   loadUsersFS.close();
               }
               
               if ( secadmFS != null ) {
                   secadmFS.flush();
                   secadmFS.close();
               }
               
           } catch ( IOException ie ) {}
       }

       
   }
   
}
