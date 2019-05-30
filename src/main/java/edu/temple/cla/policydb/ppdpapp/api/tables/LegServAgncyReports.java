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
import edu.temple.cla.policydb.ppdpapp.api.daos.BatchDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.DocumentDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.FileDAO;
import edu.temple.cla.policydb.ppdpapp.api.models.Batch;
import edu.temple.cla.policydb.ppdpapp.api.models.File;
import edu.temple.cla.policydb.ppdpapp.api.models.MetaData;
import edu.temple.cla.policydb.ppdpapp.util.ZipUtil;
import static edu.temple.cla.policydb.ppdpapp.util.ZipUtil.isZipFile;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import static java.util.stream.Collectors.toList;
import javax.persistence.Tuple;
import org.hibernate.HibernateException;
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

    /**
     * Method to respond to the POST file/upload where the tableId references
     * the Legislative Service Agency Reports.
     *
     * @param fileDAO The FileDAO to be inserted into the File table.
     * @param file The file being uploaded.
     * @return ResponseEntity indicating success or an error. If success, then
     * the fileObj is returned to the client.
     */
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
                byte[] bytes = file.getBytes();
                try (BufferedOutputStream stream
                        = new BufferedOutputStream(new FileOutputStream(javaFile))) {
                    stream.write(bytes);
                }
                fileObj = fileDAO.save(fileObj);
                return new ResponseEntity<>(fileObj, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("file NOT upload No DATA", HttpStatus.NOT_FOUND);
        }

    }

    /**
     * Method to pre-process Legislative Service Agency Report document. This
     * method checks the value of the Hyperlink field. If it references a local
     * file on the server, the file is moved to the directory where the
     * Legislative Service Agency Reports are stored and a new Hyperlink is
     * created.
     *
     * @param docObj
     */
    @Override
    public void preProcessDocument(Map<String, Object> docObj) {
        String hyperlink = (String)docObj.get("Hyperlink");
        if (hyperlink.startsWith("file:")) {
            try {
                URL url = new URL(hyperlink);
                String fullPathName = url.toURI().getPath();
                java.io.File sourceFile = new java.io.File(fullPathName);
                if (sourceFile.exists() && !isZipFile(sourceFile.getName())) {
                    String fileName = sourceFile.getName();
                    java.io.File javaFile = enterFileIntoDatabase(docObj, fileName);
                    sourceFile.renameTo(javaFile);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error entering LSAR file into database", e);
            }
        }    
    }

    /**
     * Method to respond to the POST file/upload where the tableId references
     * the Legislative Service Agency Reports.
     *
     * @param docObjJson
     * @param file
     * @return
     */
    @Override
    public ResponseEntity<?> uploadFile(String docObjJson, MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("file NOT upload No DATA", HttpStatus.NOT_FOUND);
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            Map<String, Object> docObj = mapper.readValue(docObjJson, Map.class);
            java.io.File javaFile = enterFileIntoDatabase(docObj, file.getOriginalFilename());
            file.transferTo(javaFile);
            return new ResponseEntity<>(docObj, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error("Error uploading file", e);
            return new ResponseEntity<>("Error uploading file, see log for details",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public java.io.File enterFileIntoDatabase(Map<String, Object> docObj, String fileName) 
            throws NumberFormatException, HibernateException, IOException {
        try (Session sess = getSessionFactory().openSession()) {
            String agency = (String) docObj.get("Organization");
            NativeQuery<Tuple> getAgencyID
                    = sess.createNativeQuery("select ID from LegServiceAgencies where Agency=\'" + agency + "\'",
                            Tuple.class);
            String agencyID = getAgencyID.stream()
                    .map(tuple -> (String) tuple.get("ID"))
                    .findFirst()
                    .get();
            String date = (String) docObj.get("Date");
            String[] dateTokens = date.split("-");
            String year = dateTokens[0];
            String title = (String) docObj.get("Title");
            String lastIdQuery = String.format("select max(ID) maxId from "
                    + "LSAReportsText where ID like(\"%s_%s_%%\")", agencyID, year);
            NativeQuery<Tuple> getLastId = sess.createNativeQuery(lastIdQuery, Tuple.class);
            String lastId = getLastId.stream()
                    .map(tuple -> (String) tuple.get("maxId"))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse("x_x_0");
            int lastIdNum = Integer.parseInt(lastId.split("_")[2]);
            String newId = String.format("%s_%s_%03d", agencyID, year, lastIdNum + 1);
            java.io.File baseDir = new java.io.File("/var/ppdp/files/LegServiceAgencyReports/pdfs");
            java.io.File agencyDir = new java.io.File(baseDir, agency);
            java.io.File javaFile = new java.io.File(agencyDir, fileName);
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
            String url = "lsar.spg?ID=" + newId;
            docObj.put("Hyperlink", url);
            return javaFile;
        }
    }

    /**
     * Method to publish the dataset. This method will determine the new entries
     * in the PAPolicy_Copy database and load them into the PAPolicy database.
     * It will also copy the corresponding Legislative Service Agency reports.
     *
     * @return ResponseEntity indicating success or failure.
     */
    @Override
    public ResponseEntity<?> publishDataset() {
        try (Session sess = getSessionFactory().openSession()) {
            String dropNewEntriesTable = "drop table if exists NewLSAReports";
            String createNewEntriesTable = "create table NewLSAReports "
                    + "select PAPolicy_Copy.LegServiceAgencyReports.ID,"
                    + " PAPolicy_Copy.LegServiceAgencyReports.Hyperlink, "
                    + "PAPolicy_Copy.LegServiceAgencyReports.FinalCode "
                    + "from PAPolicy_Copy.LegServiceAgencyReports left join "
                    + "PAPolicy.LegServiceAgencyReports on "
                    + "PAPolicy_Copy.LegServiceAgencyReports.ID=PAPolicy.LegServiceAgencyReports.ID "
                    + "where isNull(PAPolicy.LegServiceAgencyReports.ID) "
                    + "and not isNull(PAPolicy_Copy.LegServiceAgencyReports.FinalCode)";
            Transaction tx = sess.beginTransaction();
            // Find the new dataset entries and create a table
            sess.createNativeQuery(dropNewEntriesTable).executeUpdate();
            sess.createNativeQuery(createNewEntriesTable).executeUpdate();
            // Find those new entries that reference a document
            String findNewDocIDs = "select HyperLink from NewLSAReports where not isNull(Hyperlink)";
            List<String> hyperLinkList = sess.createNativeQuery(findNewDocIDs, Tuple.class)
                    .stream()
                    .map(t -> {
                        String[] parts = ((String) t.get("HyperLink")).split("=");
                        return parts[1].substring(0, (parts[1].length()) - 1);
                    })
                    .collect(toList());
            // List the new document's ids in a table
            sess.createNativeQuery("drop table if exists NewDocIds").executeUpdate();
            String createNewDocIds = "create table NewDocIds (docId varchar(50))";
            sess.createNativeQuery(createNewDocIds).executeUpdate();
            StringJoiner sj = new StringJoiner(", ");
            hyperLinkList.forEach(s -> sj.add(String.format("('%s')", s)));
            sess.createNativeQuery("insert into NewDocIds values " + sj.toString()).executeUpdate();
            // Copy the new documents from ppdp to PAPolicy
            String PAPolicy_Copy_BaseDir = "/var/ppdp/files/LegServiceAgencyReports/pdfs";
            String PAPolicy_BaseDir = "/var/www/html/PAPolicy/LegServiceAgencyReports/pdfs";
            String findNewDocuments = "select ID, Agency, FileName from NewDocIds "
                    + "left join LSAReportsText on docID=ID";
            sess.createNativeQuery(findNewDocuments, Tuple.class)
                    .stream()
                    .forEach(tuple -> {
                        String agency = (String) tuple.get("Agency");
                        String fileName = (String) tuple.get("FileName");
                        Path fromPath = FileSystems.getDefault().getPath(PAPolicy_Copy_BaseDir, agency, fileName);
                        Path toPath = FileSystems.getDefault().getPath(PAPolicy_BaseDir, agency, fileName);
                        try {
                            Files.copy(fromPath, toPath, REPLACE_EXISTING, COPY_ATTRIBUTES);
                        } catch (IOException ioex) {
                            throw new RuntimeException("Error copying " + fromPath + " to " + toPath, ioex);
                        }
                    });
            // Insert the new documents into PAPolicy.LSAReportsText
            sess.createNativeQuery("insert into PAPolicy.LSAReportsText "
                    + "select ID, Year, Agency, Title, FileName from NewDocIds "
                    + "left Join LSAReportsText on docID=ID")
                    .executeUpdate();
            sess.createNativeQuery("insert into PAPolicy.LegServiceAgencyReports "
                    + "select LegServiceAgencyReports.ID, Title, Organization, "
                    + "Date, LegServiceAgencyReports.Hyperlink, Abstract, "
                    + "LegRequest, Recomendation, Tax, Elderly, "
                    + "LegServiceAgencyReports.FinalCode from NewLSAReports "
                    + "left join LegServiceAgencyReports on "
                    + "NewLSAReports.ID = LegServiceAgencyReports.ID")
                    .executeUpdate();
            tx.commit();
        }
        return new ResponseEntity<>("Dataset published", HttpStatus.OK);
    }

    public String getFileUploadHtml() {
        boolean fileUpload = false;
        for (MetaData metaData : metaDataList) {
            if (metaData.getDataType().equals("fileUpload")) {
                fileUpload = true;
            }
        }
        if (fileUpload) {
            return "        <div class=\"row margin-top-large ng-binding ng-hide\" \n"
                    + "             ng-show=\"fileNameNotDefined\">\n"
                    + "            <div class=\"col-md-6\">\n"
                    + "                <progressbar value=\"progress\"></progressbar>\n"
                    + "            </div>\n"
                    + "        </div>\n"
                    + "        <div class=\"form-group row\">\n"
                    + "            <!-- <div class=\"ng-binding ng-hide\" ng-show=\"fileNameNotDefined\"> -->\n"
                    + "                <div class=\"col-md-12\">\n"
                    + "                    <span class=\"btn btn-success btn-file btn-lg\" \n"
                    + "                          ng-disabled=\"form.$invalid || processing\">\n"
                    + "                        <span class=\"glyphicon glyphicon-plus\"></span> \n"
                    + "                        Select file and Upload\n"
                    + "                        <input type=\"file\" ng-file-select=\"onFileSelect($files)\" />\n"
                    + "                        <i ng-show=\"processing\" class=\"fa fa-spinner fa-spin\"></i>\n"
                    + "                    </span>\n"
                    + "                </div>\n"
                    + "            <!-- </div> -->\n"
                    + "        </div>\n";
        } else {
            return null;
        }
    }

    public String getFileUploadJavaScript() {
        return "var batch_id = $routeParams.batch_id;\n"
                + "if (batch_id !== 'none') {\n"
                + "    batchesAPI.find(authInfo.token, batch_id)\n"
                + "        .success(function (batchObj) {\n"
                + "            if (batchObj.fileID) {\n"
                + "                filesAPI.find(authInfo.token, batchObj.fileID)\n"
                + "                    .success(function (fileObj) {\n"
                + "                        $scope.Hyperlink = fileObj.fileURL;\n"
                + "                        $scope.fileNameDefined = Boolean(fileObj.fileURL);\n"
                + "                        $scope.fileNameNotDefined = !Boolean(fileObj.fileURL);\n"
                + "                    })\n"
                + "                    .error(function (err) {\n"
                + "                        $scope.errMsg = err;\n"
                + "                        $scope.requestFailed = true;\n"
                + "                    });\n"
                + "            } else {\n"
                + "                $scope.fileNameDefined = false;\n"
                + "                $scope.fileNameNotDefined = true;\n"
                + "            }\n"
                + "        })\n"
                + "        .error(function (err) {\n"
                + "            $scope.errMsg = err;\n"
                + "            $scope.requestFailed = true;\n"
                + "        });\n"
                + "} else {\n"
                + "      $scope.fileNameDefined = false;\n"
                + "      $scope.fileNameNotDefined = true;\n"
                + "}\n"
                + "\n";
    }

    /**
     * Method to decompress zip file and add the content documents to a batch.
     * This method is currently only applicable to LegServiceAgencyReports.
     * @param documentDAO The Document DAO
     * @param fileDAO The File DAO
     * @param batchDAO The Batch DAO
     * @param batchObj The Batch to which documents may be added
     * @return Updated Batch object, or error indication.
     */
    @Override
    public ResponseEntity<?> checkZip(DocumentDAO documentDAO, FileDAO fileDAO, 
            BatchDAO batchDAO, Batch batchObj) {
        if (batchObj.getFileID() != null) {
            int fileId;
            try {
                fileId = Integer.parseInt(batchObj.getFileID());
            } catch (NumberFormatException ex) {
                return new ResponseEntity<>(batchObj, HttpStatus.OK);
            }
            File file = fileDAO.find(fileId);
            String contentType = file.getContentType();
            if (contentType.contains("zip")) {
                java.io.File unzippedDirectory = ZipUtil.unzipFiles(file.getFileURL());
                java.io.File[] files = unzippedDirectory.listFiles();
                for (java.io.File javaFile : files) {
                    Map<String, Object> lsaReportObject = createLSAReport(javaFile);
                    int docID = documentDAO.insertDocument(getTableName(), lsaReportObject);
                    batchDAO.addDocument(batchObj.getBatchID(), Integer.toString(docID));
                }
            }
        }
        return new ResponseEntity<>(batchObj, HttpStatus.OK);
    }
    
    private Map<String, Object> createLSAReport(java.io.File javaFile) {
        String name = javaFile.getName();
        URL url;
        try {
            url = javaFile.toURI().toURL();
        } catch (MalformedURLException e) {
            // Cannot happen
            throw new RuntimeException(e);
        }
        Map<String, Object> lsaReport = new HashMap<>();
        lsaReport.put("Title", name);
        lsaReport.put("Hyperlink", url.toString());
        return lsaReport;
    }
}
