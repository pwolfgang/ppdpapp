/*
 * The MIT License
 *
 * Copyright 2016 Temple University.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package edu.temple.cla.policydb.ppdpapp.api.servlets;

import edu.temple.cla.papolicy.ppdpapp.template.Template;
import edu.temple.cla.policydb.ppdpapp.api.controllers.IndexController;
import edu.temple.cla.policydb.ppdpapp.api.tables.Table;
import edu.temple.cla.policydb.ppdpapp.api.tables.TableLoader;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Class to generate the app.js and document js files from templates. All other
 * js files in the app directory are copied as is. Template prototypes contain
 * variable references of the form ${<i>variable</i>}.
 * @author Paul Wolfgang
 */
@Controller
public class AppGenerator {
    
    @Autowired
    private TableLoader tableLoader;
    
    @Autowired
    private ServletContext servletContext;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @RequestMapping("app/**")
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        String requestURL = request.getRequestURI();
        int indexOfApp = requestURL.indexOf("/app/");
        String fileName = requestURL.substring(indexOfApp+5);
        int posLastDot = fileName.lastIndexOf(".");
        String suffix = "";
        if (posLastDot != -1) {
            suffix = fileName.substring(posLastDot + 1);
        }
        switch (suffix) {
            case "js":
                response.setContentType("text/javascript;charset=UTF-8");
                break;
            case "html":
                response.setContentType("text/html");
                break;
            default:
                response.setContentType("text");
        }
        if (fileName.equals("app.js")) {
            expandAppJS(response);
        } else if (fileName.equals("batches/batches.js")) {
            expandBatchesJS(response);
        } else if (fileName.equals("assignments/assignments.js")) {
            expandAssignmentsJS(response);
        } else if (fileName.startsWith("documents/")) {
            expandTemplate(fileName, response);
        } else {
            copyFile(fileName, response);
        }
    }

    private void copyFile(String fileName, HttpServletResponse response) {
        try {
            String contextPath = servletContext.getRealPath("/");
            File contextRoot = new File(contextPath);
            File root = new File(contextRoot, "app");
            File file = new File(root, fileName);
            if (file.exists()) {
                try (
                        InputStream in = new BufferedInputStream(new FileInputStream(file));
                        OutputStream out = new BufferedOutputStream(response.getOutputStream())) {
                    long fileLength = file.length();
                    response.setContentLength((int)fileLength);
                    int c;
                    while ((c = in.read()) != -1) {
                        out.write(c);
                    }
                    out.flush();
                } catch (IOException ioex) {
                    throw new RuntimeException(ioex);
                }

            }
        } catch (Throwable tx) {
            String txMessage = tx.getMessage();
            throw tx;
        }
    }
    
    private void expandAppJS(HttpServletResponse response) {
        List<Table> tables = IndexController.getEditableTables(tableLoader);
        StringJoiner sj = new StringJoiner(", ");
        tables.forEach(table -> sj.add("'" + table.getDocumentName() + "'"));
        Template template = new Template(Collections.singletonMap("tables", sj.toString()));
        expandFile(template, "app", "app.js", response);
    }
    
    private void expandBatchesJS(HttpServletResponse response) {
        expandFile(new Template(Collections.emptyMap()), "app/batches", "batches.js", response);
    }
    
    private void expandAssignmentsJS(HttpServletResponse response) {
        expandFile(new Template(Collections.emptyMap()), "app/assignments", "assignments.js", response);
    }
    
    private void expandTemplate(String fileName, HttpServletResponse response) {
        String documentFileName = fileName.substring(10);
        int posSlash = documentFileName.indexOf("/");
        String documentName = documentFileName.substring(0, posSlash);
        documentFileName = documentFileName.substring(posSlash+1);
        String prototypeFileName = documentFileName.replaceAll(documentName, "document");
        String prototypeRoot = "app/documents/prototypes";
        Table table = tableLoader.getTableByDocumentName(documentName);
        Template template = new Template(table.getTemplateParameters());
        expandFile(template, prototypeRoot, prototypeFileName, response); 
    }

    private void expandFile(Template template, String prototypeRoot, 
            String prototypeFileName, HttpServletResponse response) {
        try {
            String contextPath = servletContext.getRealPath("/");
            File contextRoot = new File(contextPath);
            File root = new File(contextRoot, prototypeRoot);
            File file = new File(root, prototypeFileName);
            String absPath2 = file.getAbsolutePath();
            try (
                    BufferedReader  in = new BufferedReader(new FileReader(file));
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream()));
                    ){
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.trim().equals("$forEachTable")){
                        expandForEachTable(in, out);
                    }else {
                        String expandedLine = template.evaluate(line);
                        if (!expandedLine.isEmpty()) {
                            out.println(expandedLine);
                        }
                    }
                }
                out.flush();
            } catch (IOException ioex) {
                throw new RuntimeException(ioex);
            }      
        } catch (Throwable tx) {
            String txMessage = tx.getMessage();
            throw tx;
        }
    }
    
    private void expandForEachTable(BufferedReader in, PrintWriter out) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while (!((line = in.readLine()).contains("$endFor"))) {
            lines.add(line);
        }
        List<Table> tables = IndexController.getEditableTables(tableLoader);
        tables.forEach(table -> {
            Template template = new Template(table.getTemplateParameters());
            lines.forEach(l -> {
                out.println(template.evaluate(l));
            });
        });
    }

}
