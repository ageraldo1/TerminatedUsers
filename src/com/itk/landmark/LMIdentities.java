package com.itk.landmark;

import com.itk.db.Database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;


public class LMIdentities {
    
    public String getActor (String rmid) {

        Database db = new Database(Database.dbType.Landmark);        
        Statement stmt;
        String result = null;
        
        String sql = "SELECT ACTOR FROM ACTOR WHERE ACTOR='" + rmid + "'";
        
        if ( db.openDBConnection() == true ) {
            try {
                stmt = db.getConnection().createStatement();
                
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    
                    while ( rs.next()) {
                        result = rs.getString("ACTOR").trim();
                    }
                }
                stmt.close();
                
            } catch (SQLException e) {
                System.out.println ("Error retrieving getActor : " + e.toString()); 
                
            } finally {
                db.closeDBConnection();
            }                     
            
        }
        
        return result;
    }
    
    public ArrayList<String> getServiceAssociatedToActor(String rmid) {
        
        Database db = new Database(Database.dbType.Landmark);        
        Statement stmt;
        
        ArrayList<String> identityList = new ArrayList<>();
        
        String sql = "SELECT ACTOR, SERVICE FROM IDENTITYACTOR WHERE ACTOR='" + rmid + "'";
        
        if ( db.openDBConnection() == true ) {
            try {
                stmt = db.getConnection().createStatement();
                
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    
                    while ( rs.next()) {
                        identityList.add(rs.getString("SERVICE").trim());
                    }
                }
                stmt.close();
                
            } catch (SQLException e) {
                System.out.println ("Error retrieving getActor : " + e.toString()); 
                
            } finally {
                db.closeDBConnection();
            }                     
            
        }
        
        return identityList;
        
    }

}
