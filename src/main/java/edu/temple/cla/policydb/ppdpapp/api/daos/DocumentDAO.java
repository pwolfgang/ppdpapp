package edu.temple.cla.policydb.ppdpapp.api.daos;


import java.util.List;
import java.util.Map;

public interface DocumentDAO {

    public Object find(String docType, String id);

    public List<Object> findDocuments(String docType);
    
    public List<Object> findDocumentsPage(String docType, int page);
    
    public String getDocumentCount(String docType);

    /**
     * Finds all documents that are currently not assigned to a batch of the
     * assignment type and do not have a code assigned by a user of the 
     * target batch.
     * @param docType   The document type (e.g. NewsClips)
     * @param assignmentType The assignment type (e.g. second code)
     * @param batch_id The id of the target batch.
     * @return 
     */
    public List<Object> findDocumentsNoBatch(String docType, int assignmentType, int batch_id);
        
    public Object findDocument(String docType, String id);

    public List<Map<String, String>> findDocumentCodes(String docType, String id);
    
    public Object findDocumentCode(String docType, String id, String email);

    public void addDocumentCode(String email, String tableName, String docid, String batchid, int codeid);

    public List<Object> findDocumentsNoCodes(String tableName, int batchid, String email);

    public List<Object> findDocumentsTieBreak(String tableName, int batchid, String email);

    public void updateDocument(String tableName, Object docObj);

    public int insertDocument(String tableName, Object docObj);
    
    public List<String> verifyUser(int batchId, String email);
}
