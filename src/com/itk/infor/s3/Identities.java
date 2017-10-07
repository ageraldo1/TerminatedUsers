package com.itk.infor.s3;

import com.itk.ldap.LDAPContainer;
import javax.naming.directory.*;
import javax.naming.*;
import java.util.ArrayList;


public class Identities {
    
    private ArrayList<String> services;
    
    public void addService(String serviceName) {
        if ( this.services == null) {
            this.services = new ArrayList<>();
        }
        
        this.services.add(serviceName);
    }
    
    public ArrayList<String> getServiceList() {
        
        LDAPContainer ldap = new LDAPContainer();
        
        SearchResult result = null;
        Attributes attrs = null;
        
        String serviceTree = "OU=resources,O=lwsnrmdata,".concat(ldap.getBaseDN());
        String serviceObjectClass = "(objectClass=zzlwsnobjService)";
        
        if ( ldap.openConnection() == true ) {
            try {
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

                NamingEnumeration<SearchResult> results = ldap.getConnection().search(serviceTree, serviceObjectClass, searchControls);
                
                while ( results.hasMoreElements() ) {    
                    result = results.next();
                    attrs = result.getAttributes();

                    for (NamingEnumeration ne = attrs.getAll(); ne.hasMore();) {
                        Attribute attr = (Attribute) ne.next();
                        
                        if ( attr.getID().equals("cn")) {
                            addService (attr.get().toString());
                        }
                    }                    
                
                }
                
            } catch (NamingException e) {
                System.out.println (e.toString());
            
            } finally {        
                ldap.closeConnection();
            }
        }
        
        return this.services;
    }
    
    public ArrayList<String> getEmployeeRMID(int company, int employeeNumber) {
        
        final String objectClass = "(objectClass=lwsnssoLsIdentity)";
        
        LDAPContainer ldap = new LDAPContainer();
        
        SearchResult result = null;
        Attributes attrs = null;
        String serviceTree = null;        
        
        
        String ssoLoginIds = null;
        String ssoRMIds = null;
        ArrayList<String> RMID = new ArrayList<>();
        
        for (String serviceName : getServiceList()) {
            if ( serviceName.contains("EMPLOYEE") == true) {                
                serviceTree = "cn=" + serviceName + ",ou=svcxref,o=lwsnSecData,".concat(ldap.getBaseDN());
                
                if ( ldap.openConnection() == true ) {
                    try {
                        SearchControls searchControls = new SearchControls();
                        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

                        NamingEnumeration<SearchResult> results = ldap.getConnection().search(serviceTree, objectClass, searchControls);

                        while ( results.hasMoreElements() ) {    
                            result = results.next();
                            attrs = result.getAttributes();
                            
                            ssoLoginIds = attrs.get("lwsnssoLoginIds").get().toString();
                            ssoRMIds = attrs.get("lwsnssoListOfIDs").get().toString();
                            
                            if ( ssoLoginIds.split("::").length == 2 ) {
                                try {
                                     int w_company = Integer.parseInt(ssoLoginIds.split("::")[0].split(":")[1]);
                                     int w_employee = Integer.parseInt(ssoLoginIds.split("::")[1].split(":")[1]);
                                
                                     if ( company == w_company && employeeNumber == w_employee) {
                                         if ( RMID.contains(ssoRMIds) == false ) {
                                             RMID.add(ssoRMIds);
                                         }
                                     }
                                } catch (NumberFormatException e) {
                                    // invalid value for employee number or company 
                                }
                            }
                        }
                    } catch (NamingException e) {
                        System.out.println (e.toString());

                    } finally {        
                        ldap.closeConnection();
                    }
                }
            }
        }
                
        return RMID;
    }
    
    public ArrayList<String> getRMIDServices(String rmid) {
        ArrayList<String> RMIDServices = new ArrayList<>();

        LDAPContainer ldap = new LDAPContainer();
        SearchResult result = null;
        Attributes attrs = null;
        
        String serviceTree = "lwsnssoRMId=" + rmid + ",ou=idxref,o=lwsnSecData,".concat(ldap.getBaseDN());
        final String objectClass = "(objectClass=lwsnssoLsIdenRef)";

        if ( ldap.openConnection() == true ) {
            try {
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

                NamingEnumeration<SearchResult> results = ldap.getConnection().search(serviceTree, objectClass, searchControls);
                
                while ( results.hasMoreElements() ) {    
                    result = results.next();
                    attrs = result.getAttributes();
                    
                    //System.out.println (attrs.size());
                    //System.out.println (attrs.getAll().toString());
                    
                    
                    System.out.println ("cn=" + attrs.get("cn").get().toString());
                    //System.out.println ("attrs.get("lwsnssoLoginIds").get().toString());
                
                }
                
            } catch (NamingException e) {
                System.out.println (e.toString());
            
            } finally {        
                ldap.closeConnection();
            }
        }
        
        
        
        
        
        
        
        
        
        
        
        
        
        return RMIDServices;    
    }
    
    
}
