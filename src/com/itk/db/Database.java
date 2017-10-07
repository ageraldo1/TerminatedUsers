/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itk.db;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSetMetaData;

public class Database {
    
    private String jdbcDriver;
    private String jdbcURL;
    private String jdbcUserName;
    private String jdbcPassword; 
    private String lastErrMessage;
    
    private Connection dbConnection = null;
    
    public enum dbType {
        S3, Landmark
    }
    
    public Database(dbType db) {
        
        switch (db) {
            case S3:
                setJdbcUserName (System.getProperty("s3.db.user"));
                setJdbcPassword (System.getProperty("s3.db.pwd"));
                setJdbcURL (System.getProperty("s3.db.url"));
                setJdbcDriver (System.getProperty("s3.db.driver"));    
                break;
                
            case Landmark:
                setJdbcUserName (System.getProperty("lmk.db.user"));
                setJdbcPassword (System.getProperty("lmk.db.pwd"));
                setJdbcURL (System.getProperty("lmk.db.url"));
                setJdbcDriver (System.getProperty("lmk.db.driver"));    
                break;
                
        }
            
    }
    
    private void setJdbcDriver(String driver) {
        this.jdbcDriver = driver;
    }
    
    private String getJdbcDriver() {
        return this.jdbcDriver;
    }

    private void setJdbcURL(String url) {
        this.jdbcURL = url;
    }
    
    private String getJdbcURL() {
        return this.jdbcURL;
    }
        
    private void setJdbcUserName(String user) {
        this.jdbcUserName = user;
    }
    
    private String getJdbcUserName() {
        return this.jdbcUserName;
    }
            
    private void setJdbcPassword(String password) {
        this.jdbcPassword = password;
    }
    
    private String getJdbcPassword() {
        return this.jdbcPassword;
    }
    
    public boolean openDBConnection () {
        boolean state = true;
        
        if ( this.dbConnection == null ) {
            try {
                Class.forName ( getJdbcDriver());
                
            } catch (ClassNotFoundException e) {
                state = false;
                setLastError(e.toString());
            }
            
            try {
                this.dbConnection = DriverManager.getConnection(getJdbcURL(), getJdbcUserName(), getJdbcPassword() );                
                
            } catch (SQLException e) {
                state = false;
                setLastError(e.toString());
            }
        }
        
        return state;
    }
    
    public void closeDBConnection() {
        if ( this.dbConnection != null ) {
            try {
                this.dbConnection.close();                
                
            } catch (SQLException e) {
                setLastError (e.toString());
                
            } finally {
                this.dbConnection = null;                
            }
        }        
    }
    
    public Connection getConnection() {
        return this.dbConnection;
    }

    private void setLastError(String err) {
        this.lastErrMessage = err; 
    }
    
    public String getLastError() {
        return this.lastErrMessage;
    }
    
    public void info()
    {
        System.out.println ("User.........: " + getJdbcUserName());
        System.out.println ("Password.....: " + getJdbcPassword());
        System.out.println ("URL..........: " + getJdbcURL());
        System.out.println ("Driver.......: " + getJdbcDriver());
        
        if ( this.dbConnection == null ) {
            System.out.println ("State........: closed");
            
        } else {
            System.out.println ("State........: opened");
        }
        
        System.out.println ("Last error...: " + getLastError());
    
    }
    
   
    public String processResultSet(ResultSet rs) throws SQLException
    {
        StringBuilder sb = new StringBuilder();

        ResultSetMetaData rsmd = rs.getMetaData();
        int totalCols = rsmd.getColumnCount();
        int[] colCounts = new int[totalCols];
        String[] colLabels = new String[totalCols];
        for(int i = 0; i < totalCols; i++)
        {
            colCounts[i] = rsmd.getColumnDisplaySize(i+1);
            colLabels[i] = rsmd.getColumnLabel(i+1);
            if(colLabels[i].length() > colCounts[i])
            {
                colLabels[i] = colLabels[i].substring(0, colCounts[i]);
            }
            sb.append(String.format("| %"+ colCounts[i] +"s ", colLabels[i]));
        }
        sb.append("|\n");

        String horizontalLine = getHorizontalLine(colCounts);
        while(rs.next())
        {
            sb.append(horizontalLine);
            for(int i = 0; i < totalCols; i++)
            {
                sb.append(String.format("| %"+ colCounts[i] +"s ",rs.getString(i+1)));
            }
            sb.append("|\n");

        }


        return (getHorizontalLine(colCounts)+sb.toString());
    }

    private String getHorizontalLine(int[] colCounts)
    {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < colCounts.length; i++)
        {
            sb.append("+");
            for(int j = 0; j < colCounts[i] + 2; j++)
            {
                sb.append("-");
            }
        }
        sb.append("+\n");

        return sb.toString();
    }    
}
