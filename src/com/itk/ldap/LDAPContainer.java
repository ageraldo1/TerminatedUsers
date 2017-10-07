package com.itk.ldap;

import javax.naming.ldap.*;
import javax.naming.directory.*;
import javax.naming.*;
import java.util.Hashtable;

public final class LDAPContainer {
    
    private String username;
    private String password;
    private String url;
    private String basedn;
    private DirContext ctxldap;
    private String lastErrMessage;
    
    public LDAPContainer () {
        setUsername (System.getProperty("s3.ldap.user"));
        setPassword (System.getProperty("s3.ldap.password"));
        setURL (System.getProperty("s3.ldap.url"));
        setBaseDN (System.getProperty("s3.ldap.basedn"));        
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String u) {
        this.username = u;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String p) {
        this.password = p;
    }
    

    public String getURL() {
        return this.url;
    }
    
    public void setURL(String u) {
        this.url = u;
    }


    public String getBaseDN() {
        return this.basedn;
    }
    
    public void setBaseDN(String b) {
        this.basedn = b;
    }
    
    private void setLastErrorMessage(String msg) {
        this.lastErrMessage = msg;
    }
    
    public String getLastErrorMessage() {
        return this.lastErrMessage;
    }
    
    public boolean openConnection () {
        boolean state = true;
        
        if ( this.ctxldap == null ) {
            Hashtable env  = new Hashtable();        

            env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION,"simple");
            env.put(Context.SECURITY_PRINCIPAL, getUsername());
            env.put(Context.SECURITY_CREDENTIALS,getPassword());
            env.put(Context.PROVIDER_URL,getURL());

            try {
                this.ctxldap = new InitialLdapContext(env, null);
                
            } catch (AuthenticationException ae) {
                setLastErrorMessage ("Invalid username / password for autentication " + ae.toString());
                state = false;
                
            } catch (NamingException e) {
                setLastErrorMessage("Error connecting to ldap server " + e.toString());
                state = false;
            }
        }
        
        return state;
    }
    
    public void closeConnection() {
        if ( this.ctxldap != null ) {
            try {
                ctxldap.close();                
            } catch ( NamingException e) {
                setLastErrorMessage ("Error closing LDAP DB connection " + e.toString());
                
            } finally {
                this.ctxldap = null;
            }
        }
    }
    
    public DirContext getConnection() {
        return this.ctxldap;
    }
            
    
}
