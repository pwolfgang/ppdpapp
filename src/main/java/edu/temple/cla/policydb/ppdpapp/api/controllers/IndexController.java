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
package edu.temple.cla.policydb.ppdpapp.api.controllers;

import edu.temple.cla.policydb.ppdpapp.api.tables.Table;
import edu.temple.cla.policydb.ppdpapp.api.tables.TableLoader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import static java.util.stream.Collectors.toList;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Paul Wolfgang
 */
@Controller
public class IndexController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(IndexController.class);

    @Autowired
    private TableLoader tableLoader;

    @RequestMapping(value = "/", method = GET)
    public ModelAndView index(HttpServletRequest request,
            HttpServletResponse response) {
        List<Table> tables = getEditableTables(tableLoader);
        Map<String, Object> model = new HashMap<>();
        model.put("tables", tables);
        String version = getVersion();
        LOGGER.info("Version: " + version);
        model.put("version", version);
        return new ModelAndView("index", model);
    }

    public static List<Table> getEditableTables(TableLoader tableLoader) {
        return tableLoader.getTables()
                .stream()
                .filter(t->t.isEditable())
                .collect(toList());
    }
    
    private synchronized String getVersion() {
        String version = null;
        
        try {
            Properties p = new Properties();
            InputStream is = getClass().getResourceAsStream("/META-INF/maven"
                    + "/edu.temple.cla.papolicy.wolfgang/newppdpapp");
            if (is != null) {
                p.load(is);
                version = p.getProperty("version", "");
                is.close();
            }
        } catch (Exception ex) {
            //ignore
        }
        
        if (version == null) {
            Package thePackage = getClass().getPackage();
            version = thePackage.getImplementationVersion();
            if (version == null) {
                version = thePackage.getSpecificationVersion();
            }
        }
        
        if (version == null) {
            version = "";
        }
        return version;
    }

}
