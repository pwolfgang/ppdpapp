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

import edu.temple.cla.policydb.ppdpapp.api.daos.FileDAO;
import edu.temple.cla.policydb.ppdpapp.api.models.File;
import edu.temple.cla.policydb.uploadbillsdata.ProcessSessionData;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.Set;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Paul Wolfgang
 */
public class BillsTable extends AbstractTable {
    
    private static final Logger LOGGER = Logger.getLogger(BillsTable.class);
    
    private DataSource datasource;
    
    @Override
    public void setDataSource (DataSource datasource) {
        this.datasource = datasource;
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
                    Connection conn = datasource.getConnection();
                    ProcessSessionData processSessionData = new ProcessSessionData(conn, "Bills_Data");
                    Set<String> unknownCommittees = processSessionData.processStream(input);
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
    
}
