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
import edu.temple.cla.policydb.ppdpapp.api.daos.BatchDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.DocumentDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.FileDAO;
import edu.temple.cla.policydb.ppdpapp.api.models.Batch;
import edu.temple.cla.policydb.ppdpapp.api.models.File;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Class to manage the House Hearing Transcripts.
 *
 * @author Paul Wolfgang
 */
public class TranscriptTable extends AbstractTable {

    private static final Logger LOGGER = Logger.getLogger(TranscriptTable.class);

    private DataSource dataSource;

    @Override
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Create a session factory to be used to populate the Transcript table. The
     * file uploading processing apparently cannot use the global
     * SessionFactory.
     *
     * @param dataSource The JDBC DataSource to access the database.
     * @return A SessionFactory to access the database.
     */
    private SessionFactory buildSessionFactory(DataSource dataSource) {
        BasicDataSource basicDataSource = (BasicDataSource) dataSource;
        Properties properties = new Properties();
        properties.put("jdbc.driver", basicDataSource.getDriverClassName());
        properties.put("jdbc.url", basicDataSource.getUrl());
        properties.put("jdbc.username", basicDataSource.getUsername());
        properties.put("jdbc.password", basicDataSource.getPassword());
        return Main.configureSessionFactory(properties);
    }

    /**
     * Method to upload the Transcript file and populate the database. This
     * method invokes the stand-alone program that will parse the transcript XML
     * file and populate the database tables.
     *
     * @param fileDAO Data access object for the File table. NOT USED.
     * @param file The MultipartFile from the HTML POST request
     * @return A ResponseEntity indicating success or an error.
     */
    @Override
    public ResponseEntity<?> uploadFile(FileDAO fileDAO, MultipartFile file) {
        File fileObj = new File();
        if (!file.isEmpty()) {
            try {
                fileObj = fileDAO.save(fileObj);
                byte[] bytes = file.getBytes();
                {
                    InputStream input = new ByteArrayInputStream(bytes);
                    // Create special session factory to access Transcript tables
                    try (SessionFactory sessionFactory = buildSessionFactory(dataSource)) {
                        TranscriptDAO transcriptDAO
                                = new TranscriptDAO(sessionFactory, fileObj.getFileID(), getId());
                        transcriptDAO.loadDocument(input);
                    }
                }
                return new ResponseEntity<>(fileObj, HttpStatus.OK);
            } catch (Exception e) {
                LOGGER.error("Error uploading file", e);
                return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("file NOT upload No DATA", HttpStatus.NOT_FOUND);
        }

    }

    /**
     * Method to publish the new documents. This method finds table entries in
     * the PAPolicy_Copy version of a table that do not have a corresponding row
     * in the PAPolicy version, and whose code is not null. These rows are then
     * inserted into the PAPolicy version. This method is specific to the
     * Transcript table since it also uploads the records from the other tables
     * that are linked to this one.
     *
     * @return HttpStatus.OK if successful, otherwise an error status.
     */
    @Override
    public ResponseEntity<?> publishDataset() {
        String dropNewIDs = "drop table if exists NewTranscripts";
        String findNewIDs = "create table NewTranscripts "
                + "select PAPolicy_Copy.Transcript.ID "
                + "from PAPolicy_Copy.Transcript left join PAPolicy.Transcript "
                + "on PAPolicy_Copy.Transcript.ID=PAPolicy.Transcript.ID "
                + "where isNull(PAPolicy.Transcript.ID) "
                + "and not isNull(PAPolicy_Copy.Transcript.FinalCode)";
        String insertWitness = "insert into PAPolicy.Witness "
                + "select * from PAPolicy_Copy.Witness "
                + "where PAPolicy_Copy.Witness.TranscriptID "
                + "in (select ID from NewTranscripts)";
        String insertTranscriptCommittee = "insert into PAPolicy.Transcript_Committee "
                + "select * from PAPolicy_Copy.Transcript_Committee "
                + "where PAPolicy_Copy.Transcript_Committee.TranscriptID "
                + "in (select ID from NewTranscripts)";
        String insertTranscriptBillID = "insert into PAPolicy.Transcript_BillID "
                + "select * from PAPolicy_Copy.Transcript_BillID "
                + "where PAPolicy_Copy.Transcript_BillID.TranscriptID "
                + "in (select ID from NewTranscripts)";
        String insertTranscript = "insert into PAPolicy.Transcript "
                + "select * from PAPolicy_Copy.Transcript "
                + "where PAPolicy_Copy.Transcript.ID "
                + "in (select ID from NewTranscripts)";

        try (Session sess = getSessionFactory().openSession()) {
            Transaction tx = sess.beginTransaction();
            sess.createNativeQuery(dropNewIDs).executeUpdate();
            sess.createNativeQuery(findNewIDs).executeUpdate();
            sess.createNativeQuery(insertWitness).executeUpdate();
            sess.createNativeQuery(insertTranscriptCommittee).executeUpdate();
            sess.createNativeQuery(insertTranscriptBillID).executeUpdate();
            sess.createNativeQuery(insertTranscript).executeUpdate();
            tx.commit();
        }
        return new ResponseEntity<>(getDocumentName() + " has been published", HttpStatus.OK);
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

    /**
     * Method to add documents to a batch after file upload. This method adds
     * the documents that were uploaded to one or more batches. The maximum
     * batch size is 100.
     *
     * @param documentDAO The Document DAO
     * @param fileDAO The File DAO
     * @param batchDAO The Batch DAO
     * @param batchObj The Batch to which documents may be added
     * @return Updated Batch object, or error indication.
     */
    @Override
    public ResponseEntity<?> addToBatch(DocumentDAO documentDAO, FileDAO fileDAO,
            BatchDAO batchDAO, Batch batchObj) {
        if (batchObj.getFileID() != null) {
            int fileId;
            try {
                fileId = Integer.parseInt(batchObj.getFileID());
            } catch (NumberFormatException ex) {
                // Batch is not associated with a file
                return new ResponseEntity<>(batchObj, HttpStatus.OK);
            }
            List<String> documentIds;
            try (SessionFactory dbSessionFactory = buildSessionFactory(dataSource);
                    Session session = dbSessionFactory.openSession()) {
                @SuppressWarnings("unchecked")
                Query<String> getDocumentIDs = session.createNativeQuery(""
                        + "select DocumentID from FileDocument "
                        + "where TablesID=" + getId() + " and FileID=" + fileId);
                documentIds = getDocumentIDs.list();
            }
            int numDocuments = documentIds.size();
            int numBatches = (numDocuments + 99) / 100;
            batchObj.setAssignmentTypeID(2);
            batchObj.setAssignmentDescription("First Code");
            String firstID = documentIds.get(0);
            String lastID = documentIds.get(numDocuments > 99 ? 99 : numDocuments-1);
            String batchOriginalName = batchObj.getName();
            batchObj.setName(batchOriginalName + " " + firstID + " to " + lastID);
            batchDAO.save(batchObj);
            List<Batch> batches = new ArrayList<>();
            batches.add(batchObj);
            for (int kk = 1; kk < numBatches; kk++) {
                firstID = documentIds.get(kk*100);
                lastID = documentIds.get(numDocuments > (kk*100 + 99) ? kk*100 + 99 : numDocuments-1);
                Batch newBatch = new Batch();
                newBatch.setAssignmentDescription("First Code");
                newBatch.setAssignmentTypeID(2);
                newBatch.setTablesID(batchObj.getTablesID());
                newBatch.setFileID(batchObj.getFileID());
                newBatch.setCreator(batchObj.getCreator());
                newBatch.setDateAdded(batchObj.getDateAdded());
                newBatch.setDateDue(batchObj.getDateDue());
                newBatch.setName(batchOriginalName + " " + firstID + " to " + lastID);
                batchDAO.save(newBatch);
                batches.add(newBatch);
            }
            for (int kk = 0; kk < numBatches; kk++) {
                Batch currentBatch = batches.get(kk);
                int start = kk*100;
                int end = numDocuments > start + 100 ? start + 100 : numDocuments;
                for (int i = start; i < end; i++) {
                    batchDAO.addDocument(currentBatch.getBatchID(), documentIds.get(i));
                }
            }
        } else {
            // Batch is not associated with a file
            return new ResponseEntity<>(batchObj, HttpStatus.OK);
        }
        return null;
    }

}
