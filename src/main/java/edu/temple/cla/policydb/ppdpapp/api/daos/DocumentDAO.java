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
package edu.temple.cla.policydb.ppdpapp.api.daos;


import java.util.List;
import java.util.Map;

public interface DocumentDAO {

    public Object find(String docType, String id);

    public List<Map<String, Object>> findDocuments(String tableName);
    
    public List<Map<String, Object>> findDocumentsPage(String tableName, int page);
    
    public String getDocumentCount(String docType);

    /**
     * Finds all documents that are currently not assigned to a batch of the
     * assignment type and do not have a code assigned by a user of the 
     * target batch.
     * @param tableName   The table name (e.g. NewsClips)
     * @param assignmentType The assignment type (e.g. second code)
     * @param batch_id The id of the target batch.
     * @return 
     */
    public List<Map<String, Object>> findDocumentsNoBatch(String tableName, int assignmentType, int batch_id);
        
    public Object findDocument(String docType, String id);

    public List<Map<String, Object>> findDocumentCodes(String tableName, String id);
    
    public Object findDocumentCode(String tableName, String id, String email);

    public void addDocumentCode(String email, String tableName, String docid, String batchid, int codeid);

    public List<Map<String,Object>> findDocumentsNoCodes(String tableName, int batchid, String email);

    public List<Map<String,Object>> findDocumentsTieBreak(String tableName, int batchid, String email);
    
    public List<Map<String, Object>> findDocumentsClusters(String tableName, int batchid, String email);

    public void updateDocument(String tableName, Map<String, Object> docObj);

    public int insertDocument(String tableName, Map<String, Object> docObj);
    
    public List<String> verifyUser(int batchId, String email);
}
