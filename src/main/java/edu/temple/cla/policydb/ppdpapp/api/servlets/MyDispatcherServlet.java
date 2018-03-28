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
package edu.temple.cla.policydb.ppdpapp.api.servlets;

import edu.temple.cla.policydb.ppdpapp.api.models.User;
import edu.temple.cla.policydb.ppdpapp.api.services.Account;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import javax.security.sasl.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 *
 * @author Paul
 */
@Component
public class MyDispatcherServlet extends DispatcherServlet {

    private final ApplicationContext servletContext;
    
    private Account accountSvc;
    
    private static final Logger LOGGER = Logger.getLogger(MyDispatcherServlet.class);
    
    
    public MyDispatcherServlet(WebApplicationContext servletContext) {
        super(servletContext);
        this.servletContext = servletContext;
    }
    
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        if (parameterMap.containsKey("token")) {
            processToken(parameterMap, req, res);
        } else {
            super.service(req, res);
        }
    }
    
    private void processToken(Map<String, String[]> parameterMap, ServletRequest req, ServletResponse res) {
        if (accountSvc == null) {
            accountSvc = (Account)servletContext.getBean("account");
        }
        String token = parameterMap.get("token")[0];
        try {
            User user = accountSvc.doAuthentication(token);
            Map<String, String[]> newParameterMap = new HashMap<>(parameterMap);
            newParameterMap.put("user", new String[]{user.toJson()});
            HttpServletRequest newReq = new ModifiedRequest(req, newParameterMap);
            super.service(newReq, res);
        } catch (AuthenticationException ex) {
            LOGGER.info(ex.getMessage());
            HttpServletResponse httpResponse = (HttpServletResponse) res;
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);            
        } catch (Exception e) {
            LOGGER.error("Error processing token", e);
            HttpServletResponse httpResponse = (HttpServletResponse) res;
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
    }
    
    private String mapToString(Map<String, String[]> map) {
        StringBuilder stb = new StringBuilder();
        map.forEach((k, v) -> {
            stb.append(k)
                    .append(" -> ")
                    .append(arrayToString(v))
                    .append("\n");
        });
        return stb.toString();
    }
    
    StringJoiner arrayToString(String[] v) {
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        for (String s:v) {
            sj.add(s);
        }
        return sj;
    }
    
    private class ModifiedRequest extends HttpServletRequestWrapper {
        
        private final Map<String, String[]> newParameterMap;
        
        public ModifiedRequest(ServletRequest req, Map<String, String[]> newParameterMap) {
            super((HttpServletRequest)req);
            this.newParameterMap = Collections.unmodifiableMap(newParameterMap);
        }
        
        @Override
        public Map<String, String[]> getParameterMap() {
            return newParameterMap;
        }
        
        @Override
        public String getParameter(String name) {
            String[] value = newParameterMap.get(name);
            if (name != null) {
                return value[0];
            } else {
                return null;
            }
        }
        
        @Override
        public String[] getParameterValues(String name) {
            return newParameterMap.get(name);
        }
        
        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(newParameterMap.keySet());
        }
    }
    
}
