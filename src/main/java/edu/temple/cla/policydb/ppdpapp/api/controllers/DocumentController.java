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

import edu.temple.cla.policydb.ppdpapp.api.daos.DocumentDAO;
import edu.temple.cla.policydb.ppdpapp.api.models.User;
import edu.temple.cla.policydb.ppdpapp.api.tables.Table;
import edu.temple.cla.policydb.ppdpapp.api.tables.TableLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    DocumentDAO documentDAO;
    @Autowired
    private TableLoader tableLoader;

    @RequestMapping(method = RequestMethod.GET, value = "/{tableName}")
    public ResponseEntity<?> getDocuments(@PathVariable String tableName,
            @RequestParam(value = "user") User user) {
        return new ResponseEntity<>(documentDAO.findDocuments(tableName),
                HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
            value = "/{tableName}/page/{page}")

    public ResponseEntity<?> getDocumentsPaged(@PathVariable String tableName,
            @PathVariable int page, @RequestParam(value = "user") User user) {
        return new ResponseEntity<>(documentDAO.findDocumentsPage(tableName, page), 
                HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{tableName}/{id}")
    public ResponseEntity<?> getDocument(@PathVariable String tableName,
            @PathVariable String id, @RequestParam(value = "user") User user) {
        // The id parameter may be a command
        switch (id) {
            case "count":
                return new ResponseEntity<>(documentDAO.getDocumentCount(tableName), 
                        HttpStatus.OK);
            default:
                return new ResponseEntity<>(documentDAO.findDocument(tableName, id), 
                        HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.GET, 
            value = "/{tableName}/nobatch/{assignmentType}/{batch_id}")
    public ResponseEntity<?> getDocument(@PathVariable String tableName,
            @PathVariable int assignmentType, @PathVariable int batch_id, 
            @RequestParam(value = "user") User user) {
        return new ResponseEntity<>(documentDAO.findDocumentsNoBatch(tableName, 
                assignmentType, batch_id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{tableName}/{id}/codes")
    public ResponseEntity<?> getDocumentCodes(@PathVariable String tableName, 
            @PathVariable String id, @RequestParam(value = "user") User user) {
        return new ResponseEntity<>(documentDAO.findDocumentCodes(tableName, id), 
                HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{tableName}/{id}/code")
    public ResponseEntity<?> getDocumentCode(@PathVariable String tableName, 
            @PathVariable String id, @RequestParam(value = "user") User user) {
        return new ResponseEntity<>(documentDAO.findDocumentCode(tableName, id, 
                user.getEmail()), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, 
            value = "/{tableName}/{docid}/batch/{batchid}/add/code/{codeid}")
    public ResponseEntity<?> addDocumentCodes(@PathVariable String tableName, 
            @PathVariable String docid, 
            @PathVariable String batchid, 
            @PathVariable String codeid, 
            @RequestParam(value = "user") User user) {
        int code;
        try {
            code = Integer.parseInt(codeid);
            documentDAO.addDocumentCode(user.getEmail(), tableName, docid, batchid, code);
        } catch (NumberFormatException nfex) {
            if (!"null".equals(codeid)) {
                return new ResponseEntity<>("Bad code value " + codeid, 
                        HttpStatus.BAD_REQUEST);
            }
        }
        // Either way, this is OK.
        return new ResponseEntity<>("document code added, bud", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{tableName}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> updateDocument(@RequestBody Map<String, Object> docObj, 
            @PathVariable String tableName, @RequestParam(value = "user") User user) {
        documentDAO.updateDocument(tableName, docObj);
        return new ResponseEntity<>("document updated, lad", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{tableName}")
    public ResponseEntity<?> insertDocument(@RequestBody Map<String, Object> docObj, 
            @PathVariable String tableName, @RequestParam(value = "user") User user) {
        int docID = documentDAO.insertDocument(tableName, docObj);
        return new ResponseEntity<>(docID, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{tableName}/batch/{batchid}/nocodes")
    public ResponseEntity<?> getDocumentNoCodes(@PathVariable String tableName, 
            @PathVariable int batchid, @RequestParam(value = "user") User user) {
        return new ResponseEntity<>(documentDAO.findDocumentsNoCodes(tableName, 
                batchid, user.getEmail()), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, 
            value = "/{tableName}/batch/{batchid}/tiebreak")
    public ResponseEntity<?> getDocumentTieBreak(@PathVariable String tableName, 
            @PathVariable int batchid, @RequestParam(value = "user") User user) {
        return new ResponseEntity<>(documentDAO.findDocumentsTieBreak(tableName, 
                batchid, user.getEmail()), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{tableName}/upload")
    public ResponseEntity<?> uploadFile(
            @PathVariable String tableName,
            @RequestParam(value = "user") User user,
            @RequestParam(value = "docObj") String docObjJson,
            @RequestBody MultipartFile file) throws Exception {

        Table table = tableLoader.getTableByTableName(tableName);
        return table.uploadFile(docObjJson, file);
    }
}
