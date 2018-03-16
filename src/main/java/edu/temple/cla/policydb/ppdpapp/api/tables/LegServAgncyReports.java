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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Paul
 */
public class LegServAgncyReports extends AbstractTable {

    @Override
    @Transactional
    public ResponseEntity<?> uploadFile(String docObjJson, MultipartFile file) {
        Session sess = getSessionFactory().openSession();
        try {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> report = mapper.readValue(docObjJson, Map.class);
            String agency = (String) report.get("Organization");
            SQLQuery getAgencyID = sess.createSQLQuery("select ID from LegServiceAgencies where Agency=\'" + agency + "\'");
            String agencyID = (String) getAgencyID.uniqueResult();
            String date = (String) report.get("Date");
            String[] dateTokens = date.split("-");
            String year = dateTokens[0];
            String title = (String) report.get("Title");
            String lastIdQuery = String.format("select max(ID) from LSAReportsText where ID like(\"%s_%s_%%\")", agencyID, year);
            SQLQuery getLastId = sess.createSQLQuery(lastIdQuery);
            String lastId = (String) getLastId.uniqueResult();
            int lastIdNum = 0;
            if (lastId != null) {
                lastIdNum = Integer.parseInt(lastId.split("_")[2]);
            }
            String newId = String.format("%s_%s_%03d", agencyID, year, lastIdNum + 1);
            String fileName = file.getOriginalFilename();
            java.io.File baseDir = new java.io.File("/var/ppdp/files/LegServiceAgencyReports/pdfs");
            java.io.File agencyDir = new java.io.File(baseDir, agency);
            java.io.File javaFile = new java.io.File(agencyDir, fileName);
            if (!file.isEmpty()) {
                try {
                    byte[] bytes = file.getBytes();
                    try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(javaFile))) {
                        stream.write(bytes);
                        stream.flush();
                    } 
                    SQLQuery updateLSAReports = sess.createSQLQuery(
                            "insert into LSAReportsText (ID, Year, Agency, "
                                    + "Title, FileName) values "
                                    + "(?, ?, ?, ?, ?)");
                    updateLSAReports.setString(0, newId);
                    updateLSAReports.setString(1, year);
                    updateLSAReports.setString(2, agency);
                    updateLSAReports.setString(3, title);
                    updateLSAReports.setString(4, fileName);
                    updateLSAReports.executeUpdate();
                    String url = "#lsar.spg?ID=" + newId + "#";
                    report.put("Hyperlink", url);
                    return new ResponseEntity<>(report, HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("file NOT upload No DATA", HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            sess.close();
        }
    }

}
