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
package edu.temple.cla.policydb.ppdpapp.api.tables;

import edu.temple.cla.papolicy.transcriptdata.TranscriptDAO;
import edu.temple.cla.papolicy.uploadtranscriptdata.Main;
import edu.temple.cla.policydb.ppdpapp.api.daos.FileDAO;
import edu.temple.cla.policydb.ppdpapp.api.models.File;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Paul Wolfgang
 */
public class TranscriptTable extends AbstractTable {
    private static final Logger LOGGER = Logger.getLogger(BillsTable.class);
    
    private DataSource dataSource;
    
    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    private SessionFactory buildSessionFactory(DataSource dataSource) {
        BasicDataSource basicDataSource = (BasicDataSource)dataSource;
        Properties properties = new Properties();
        properties.put("jdbc.driver", basicDataSource.getDriverClassName());
        properties.put("jdbc.url", basicDataSource.getUrl());
        properties.put("jdbc.username", basicDataSource.getUsername());
        properties.put("jdbc.password", basicDataSource.getPassword());
        return Main.configureSessionFactory(properties);
    }
    
    @Override
    public ResponseEntity<?> uploadFile(FileDAO fileDAO, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        java.io.File baseDir = new java.io.File("/var/ppdp/files");
        java.io.File javaFile = new java.io.File(baseDir, fileName);
        File fileObj = new File();
        try {
            URL fileURL = javaFile.toURI().toURL();
            fileObj.setFileURL(fileURL.toString());
        } catch (MalformedURLException ex) {
            // cannot happen
        }
        fileObj.setContentType(file.getContentType());

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();{
                    InputStream input = new ByteArrayInputStream(bytes);
                    // Create special session factory to access Transcript tables
                    try (SessionFactory sessionFactory = buildSessionFactory(dataSource)) {
                        TranscriptDAO transcriptDAO = new TranscriptDAO(sessionFactory);
                        transcriptDAO.loadDocument(input);
                    }
                }
                fileObj = fileDAO.save(fileObj);
                return new ResponseEntity<>(fileObj, HttpStatus.OK);
            } catch (Exception e) {
                LOGGER.error("Error uploading file", e);
                return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("file NOT upload No DATA", HttpStatus.NOT_FOUND);
        }

    }
    
    @Override
    public ResponseEntity<?> publishDataset() {
        return new ResponseEntity<>("Not implemented for this document type", HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public String getTextFieldsHtml() {
        StringBuilder stb = new StringBuilder(super.getTextFieldsHtml());
        stb.append("<div class=\"row\">\n");
        stb.append("<div class=\"col-md-4\">\n");
        stb.append("<p>Committees</p>");
        stb.append("<p ng-bind-html=\"Committees\"></p>");
        stb.append("</div>");
        stb.append("<div class=\"col-md-4\">\n");
        stb.append("<p>Bills</p>");
        stb.append("<p ng-bind-html=\"Bills\"</p>");
        stb.append("</div>");
        stb.append("<div class=\"col-md-4\">\n");
        stb.append("<p>WitnessTranscriptURLs</p>");
        stb.append("<p ng-bind-html=\"WitnessTranscriptURLs\"></p>");
        stb.append("</div>");
        stb.append("</div>\n");
        return stb.toString();
    }
    
    @Override
    public String getTextFields() {
        StringBuilder stb = new StringBuilder(super.getTextFields());
        stb.append(",\n");
        stb.append("Committees: $scope.Committees,\n");
        stb.append("Bills: $scope.Bills,\n");
        stb.append("WitnessTranscriptURLs: $scope.WitnessTranscriptURLs");
        return stb.toString();
    }
    
    @Override
    public String getTextFieldsSetValues() {
        StringBuilder stb = new StringBuilder(super.getTextFieldsSetValues());
        stb.append("\n");
        stb.append("$scope.Committees = res.Committees;\n");
        stb.append("$scope.Bills = res.Bills;\n");
        stb.append("$scope.WitnessTranscriptURLs = res.WitnessTranscriptURLs;");
        return stb.toString();
    }
    
    
}
