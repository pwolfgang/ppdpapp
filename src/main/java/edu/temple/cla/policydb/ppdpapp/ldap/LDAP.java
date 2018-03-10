package edu.temple.cla.policydb.ppdpapp.ldap;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;

public class LDAP {
    
    private Hashtable<String, String> env;
    
    public LDAP(Hashtable<String, String> env) {
        this.env = env;
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
    }
    
    public Object[] authorize(String uid, String pw) {
        try {
            DirContext ctx = new InitialDirContext(env);
            Attributes matchAttributes = new BasicAttributes(true);
            matchAttributes.put(new BasicAttribute("uid", uid));
            NamingEnumeration<SearchResult> answer
                    = ctx.search("ou=people,dc=temple,dc=edu", matchAttributes);
            if (!answer.hasMore()) {
                return new Object[]{false, "User does not exist in Temple system"};
            }
            SearchResult r = answer.next();
            String templeEduTUNIC = (String) r.getAttributes().get("templeEduTUNIC").get();
            String dn = "templeEduTUNIC=" + templeEduTUNIC + ",ou=people,dc=temple,dc=edu";
            env.put(Context.SECURITY_PRINCIPAL, dn);
            env.put(Context.SECURITY_CREDENTIALS, pw);
            DirContext ctx2 = new InitialDirContext(env);
            return new Object[]{true, "OK"};
        } catch (Throwable ex1) {
            return new Object[]{false, ex1.toString()};
        }
    }

}
