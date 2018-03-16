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


import edu.temple.cla.policydb.ppdpapp.api.daos.AssignmentTypeDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.BatchDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.DocumentDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.UserDAO;
import edu.temple.cla.policydb.ppdpapp.api.models.Batch;
import edu.temple.cla.policydb.ppdpapp.api.models.User;
import edu.temple.cla.policydb.ppdpapp.api.services.Account;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/batches")
public class BatchController {

    @Autowired
    private BatchDAO batchDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private DocumentDAO documentDAO;
    @Autowired
    private AssignmentTypeDAO assignmentTypeDAO;
    @Autowired
    private Account accountSvc;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getBatches(@RequestParam(value = "user") User user) {

        if (user.getRole().getRoleID() > 1) {
            return new ResponseEntity<>(batchDAO.list(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(userDAO.findBatches(user.getEmail()), HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id:\\d+}")
    public ResponseEntity<?> getBatch(@PathVariable int id, @RequestParam(value = "user") User user) {
        return new ResponseEntity<>(batchDAO.find(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/users")
    public ResponseEntity<?> getBatchUsers(@PathVariable int id, @RequestParam(value = "user") User user) {
        return new ResponseEntity<>(batchDAO.findUsers(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/documents")
    public ResponseEntity<?> getDocuments(@PathVariable int id, @RequestParam(value = "user") User user) {
        return new ResponseEntity<>(batchDAO.findDocuments(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> postBatch(@RequestBody Batch batchObj, @RequestParam(value = "user") User user) {
        return new ResponseEntity<>(batchDAO.save(batchObj), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<?> deleteBatch(@PathVariable int id, @RequestParam(value = "user") User user) {
        batchDAO.delete(id);
        return new ResponseEntity<>("batch deleted, amigo.", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{id}/add/user")
    public ResponseEntity<?> postAddUser(@PathVariable int id, @RequestBody User userObj, @RequestParam(value = "user") User user) {
        List<String> previouslyCodedDocuments = documentDAO.verifyUser(id, userObj.getEmail());
        if (previouslyCodedDocuments.isEmpty()) {
            Batch batchObj = batchDAO.find(id);
            List<User> userList = batchObj.getUsers();
            userList.add(userObj);
            batchDAO.save(batchObj);
            return new ResponseEntity<>("user added, friend", HttpStatus.OK);
        } else {
            previouslyCodedDocuments.sort(String::compareTo);
            String firstDocument = previouslyCodedDocuments.get(0);
            String lastDocument = previouslyCodedDocuments.get(previouslyCodedDocuments.size()-1);
            return new ResponseEntity<>("Batch contains documents " 
                    + firstDocument + " through " + lastDocument 
                    + " that were coded by " + userObj.getEmail(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}/delete/user/{email:.+}")
    public ResponseEntity<?> deleteUser(@PathVariable int id, @PathVariable String email, @RequestParam(value = "user") User user) {
        Batch batchObj = batchDAO.find(id);
        batchDAO.deleteUser(id, email);
        return new ResponseEntity<>("user deleted, comrade", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{batchid}/add/document/{docid}")
    public ResponseEntity<?> postAddDocument(@PathVariable int batchid, @PathVariable String docid, @RequestParam(value = "user") User user) {
        batchDAO.addDocument(batchid, docid);
        return new ResponseEntity<>("document added, pal", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{batchid}/delete/document/{docid}")
    public ResponseEntity<?> deleteDocument(@PathVariable int batchid, @PathVariable String docid, @RequestParam(value = "user") User user) {
        batchDAO.deleteDocument(batchid, docid);
        return new ResponseEntity<>("document deleted, chum", HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/assignment_types")
    public ResponseEntity<?> getAssignmentTypes(@RequestParam(value = "user") User user) {
        return new ResponseEntity<>(assignmentTypeDAO.list(), HttpStatus.OK);
    }
    
}
