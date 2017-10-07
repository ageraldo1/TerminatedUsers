/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itk.infor.s3;

import com.itk.db.Database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;


public class Employee {
    
    private ArrayList employeeList;
    
    public ArrayList<HashMap> getEmployeeList(int nr_days) {
        
        Database db = new Database(Database.dbType.S3);        
        Statement stmt;
        String sql;
        
        ArrayList<HashMap> empList = new ArrayList<>();
        
        
        if ( nr_days == -1 ) {
            sql = "select COMPANY, EMPLOYEE, LAST_NAME, FIRST_NAME, TRUNC(TERM_DATE) TERM_DATE " + 
                  "from EMPLOYEE " + 
                  "where EMP_STATUS='T' ";            
        } else {            
            sql = "select COMPANY, EMPLOYEE, LAST_NAME, FIRST_NAME, TRUNC(TERM_DATE) TERM_DATE " + 
                  "from EMPLOYEE " + 
                  "where EMP_STATUS='T' " + 
                  "AND TRUNC(TERM_DATE) >= TRUNC(SYSDATE - " + nr_days + ")";
        }
        
        if ( db.openDBConnection() == true ) {            
            try {
                stmt = db.getConnection().createStatement();
                
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    
                    if (rs.next()) {
                        //initEmployeeList();
                        
                        do {
                            HashMap record = new HashMap();
                        
                            record.put("COMPANY", rs.getInt("COMPANY"));
                            record.put("EMPLOYEE", rs.getInt("EMPLOYEE"));
                            record.put("FIRST_NAME", rs.getString("FIRST_NAME"));
                            record.put("LAST_NAME", rs.getString("LAST_NAME"));
                            record.put("TERM_DATE", rs.getDate("TERM_DATE"));
                        
                            //addRecord(record);                        
                            empList.add(record);
                            
                            
                        } while (rs.next());
                    }
                }
                stmt.close();
                
            } catch (SQLException e) {
                System.out.println ("Error retrieving employee list : " + e.toString()); 
                
            } finally {
                db.closeDBConnection();
            }                     
        }
        
        return empList;
    }
    
    public void addRecord(HashMap record) {
        this.employeeList.add(record);
    }
    
    private void initEmployeeList() {
        if ( this.employeeList == null ) {
            this.employeeList = new ArrayList();
        }
    }
    
    public int getEmployeeListSize() {
        if ( this.employeeList != null ) {
            return this.employeeList.size();
            
        } else { 
            return -1;
        }
    }
    
    public void printEmployeeList() {
        Object record;
        
        //System.out.println ()
        for ( int i =0; i < this.employeeList.size(); i++) {
            record = this.employeeList.get(i);
            
            
        }
    }
           
    
}
