package com.itk.util;

import java.util.Arrays;
import java.io.File;

public class ParameterParser {
    
    
    public boolean validateArgs(String args[])
    {
        if ( System.getProperty("s3.db.user") == null || 
             System.getProperty("s3.db.pwd") == null  || 
             System.getProperty("s3.db.url") == null  || 
             System.getProperty("s3.db.driver") == null ) {            

            showUsage ("Missing S3 variables");
            return false;
            
        } else if ( System.getProperty("s3.ldap.url") == null || 
                    System.getProperty("s3.ldap.user") == null || 
                    System.getProperty("s3.ldap.password") == null || 
                    System.getProperty("s3.ldap.basedn") == null ) {
            
            showUsage ("Missing LDAP variables");
            return false;
            
        } else if (System.getProperty("lmk.db.user") == null || 
                   System.getProperty("lmk.db.pwd") == null ||
                   System.getProperty("lmk.db.url") == null ||
                   System.getProperty("lmk.db.driver") == null ) {
            
            showUsage ("Missing Landmark variables");
            return false;
            
        } else if (System.getProperty("itk.work.dir") == null) {
            showUsage ("Working directory not defined");
            return false;
        }
        
        File workDirectory = new File(System.getProperty("itk.work.dir"));
        
        if ( ! workDirectory.exists() ) {
            showUsage ("Working directory " + System.getProperty("itk.work.dir") + " does not exists");
            return false;            
            
        } else if ( !workDirectory.canWrite() && !workDirectory.canRead()) {
            showUsage ("User running the application does not have read/write permission under working directory " + System.getProperty("itk.work.dir"));
            return false;            
            
        }
        
        if ( args.length != 3 ) {
            showUsage ("Invalid number of operation(s) specified  " + Arrays.toString(args));
            return false;
            
        } else {
            
            if ( !args[0].equals("list") && !args[0].equals("generate")) {
                showUsage ("Invalid operation specified " + Arrays.toString(args));
                return false;                
            }
            
            if ( !args[1].matches("-?\\d+(\\.\\d+)?")) {
                showUsage ("Invalid interval " + args[1]);
                return false;
            }
            
            if ( !args[2].equals("s3") && !args[2].equals("landmark") && !args[2].equals("both")) {
                showUsage ("Invalid environment specified " + Arrays.toString(args));
                return false;                                
            }
        }
        
        
        return true;        
    }
    
    private void showUsage(String msg)            
    {
        final String nr = System.getProperty("line.separator");
        
        String errorMsg = "Invalid parameters: " + msg + nr + nr + 
                     "Description: Delete terminated users process " + nr + nr +                       
                     "Sintax : java [params] com.itk.infor.users.TerminatedUsers <Operation> <Interval> <Environment>" + nr + nr +
                     "Where  : " + nr +
                     "    System variables : " + nr +
                     "        -Ds3.db.user       = database user name with read only access on S3 product line" + nr + 
                     "        -Ds3.db.pwd        = database user password for S3 product line" + nr + 
                     "        -Ds3.db.url        = JDBC url for S3 product line" + nr +
                     "        -Ds3.db.driver     = JDBC driver for S3 product line" + nr + nr +
                     "        -Ds3.ldap.url      = URL for S3 LDAP container " + nr +
                     "        -Ds3.ldap.user     = Username to connect to LDAP container " + nr +
                     "        -Ds3.ldap.password = Password to connect to LDAP container " + nr +
                     "        -Ds3.ldap.basedn   = Base DN of LDAP container " + nr +
                     "        -Dlmk.db.user      = database user name with read only access on Landmark GEN product line" + nr + 
                     "        -Dlmk.db.pwd       = database user password for Landmark GEN product line" + nr + 
                     "        -Dlmk.db.url       = JDBC url for Landmrk GEN product line" + nr +
                     "        -Dlmk.db.driver    = JDBC driver for Landmark GEN product line" + nr + nr +
                     "   Operation   : " + nr +
                     "        list     = Only list the terminated employees " + nr +
                     "        generate = Generate loadusers and secadm files " + nr + nr + 
                     "   Interval    : The number of days backward of the termination date. Specify -1 in order to get no days restriction." + nr + nr +
                     "   Environment : Only used when [Operation] == generate. Target environment for output files." + nr + 
                     "       s3       - generate only delete_users.xml file." + nr + 
                     "       landmark - generate only delete_users.secadm file." + nr + 
                     "       both     - generate both files";
        
        System.out.println (errorMsg);    
    }
    
    
}
