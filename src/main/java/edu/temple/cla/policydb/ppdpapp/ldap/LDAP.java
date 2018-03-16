/* 
 * Copyright (c) 2018, Temple University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * All advertising materials features or use of this software must display 
 *   the following  acknowledgement
 *   This product includes software developed by Temple University
 * * Neither the name of the copyright holder nor the names of its 
 *   contributors may be used to endorse or promote products derived 
 *   from this software without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
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
    
    private Hashtable<String, String> masterEnv;
    
    public LDAP(Hashtable<String, String> env) {
        masterEnv = env;
        masterEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        masterEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
    }
    
    public Object[] authorize(String uid, String pw) {
        try {
            Hashtable<String, String> env = new Hashtable<>(masterEnv);
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
