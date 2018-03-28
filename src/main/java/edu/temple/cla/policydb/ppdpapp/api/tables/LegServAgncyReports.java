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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Tuple;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Paul
 */
public class LegServAgncyReports extends AbstractTable {
    
    public static final Logger LOGGER = Logger.getLogger(LegServAgncyReports.class);

    @Override
    public ResponseEntity<?> uploadFile(String docObjJson, MultipartFile file) {
        try (Session sess = getSessionFactory().openSession()) {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> report = mapper.readValue(docObjJson, Map.class);
            String agency = (String) report.get("Organization");
            NativeQuery<Tuple> getAgencyID = 
                    sess.createNativeQuery("select ID from LegServiceAgencies where Agency=\'" + agency + "\'",
                            Tuple.class);
            String agencyID = getAgencyID.stream()
                    .map(tuple -> (String)tuple.get("ID"))
                    .findFirst()
                    .get();
            String date = (String) report.get("Date");
            String[] dateTokens = date.split("-");
            String year = dateTokens[0];
            String title = (String) report.get("Title");
            String lastIdQuery = String.format("select max(ID) maxId from "
                    + "LSAReportsText where ID like(\"%s_%s_%%\")", agencyID, year);
            NativeQuery<Tuple> getLastId = sess.createNativeQuery(lastIdQuery, Tuple.class);
            String lastId = getLastId.stream()
                    .map(tuple -> (String)tuple.get("maxId"))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse("x_x_0");
            int lastIdNum = Integer.parseInt(lastId.split("_")[2]);
            String newId = String.format("%s_%s_%03d", agencyID, year, lastIdNum + 1);
            String fileName = file.getOriginalFilename();
            java.io.File baseDir = new java.io.File("/var/ppdp/files/LegServiceAgencyReports/pdfs");
            java.io.File agencyDir = new java.io.File(baseDir, agency);
            java.io.File javaFile = new java.io.File(agencyDir, fileName);
            if (!file.isEmpty()) {
                try {
                    file.transferTo(javaFile);
                    Transaction tx = sess.beginTransaction();
                    NativeQuery<?> updateLSAReports = sess.createNativeQuery(
                            "insert into LSAReportsText (ID, Year, Agency, "
                                    + "Title, FileName) values "
                                    + "(?, ?, ?, ?, ?)");
                    updateLSAReports.setParameter(1, newId);
                    updateLSAReports.setParameter(2, year);
                    updateLSAReports.setParameter(3, agency);
                    updateLSAReports.setParameter(4, title);
                    updateLSAReports.setParameter(5, fileName);
                    updateLSAReports.executeUpdate();
                    tx.commit();
                    String url = "#lsar.spg?ID=" + newId + "#";
                    report.put("Hyperlink", url);
                    return new ResponseEntity<>(report, HttpStatus.OK);
                } catch (Exception e) {
                    LOGGER.error("Error uploading file", e);
                    return new ResponseEntity<>("Error uploading file, see log for details", 
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ResponseEntity<>("file NOT upload No DATA", HttpStatus.NOT_FOUND);
            }
        } catch (Exception ex) {
            LOGGER.error("Error Uploading File", ex);
            return new ResponseEntity<>(ex.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
