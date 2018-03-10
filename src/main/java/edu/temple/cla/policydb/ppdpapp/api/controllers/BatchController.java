package edu.temple.cla.policydb.ppdpapp.api.controllers;


import edu.temple.cla.policydb.ppdpapp.api.daos.AssignmentTypeDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.BatchDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.DocumentDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.UserDAO;
import edu.temple.cla.policydb.ppdpapp.api.models.AssignmentType;
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
    public ResponseEntity getBatches(@RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        if (user.getRole().getRoleID() > 1) {
            return new ResponseEntity<List<Batch>>(batchDAO.list(), HttpStatus.OK);
        } else {
            return new ResponseEntity<List<Batch>>(userDAO.findBatches(user.getEmail()), HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id:\\d+}")
    public ResponseEntity getBatch(@PathVariable int id, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Object>(batchDAO.find(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/users")
    public ResponseEntity getBatchUsers(@PathVariable int id, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<List<User>>(batchDAO.findUsers(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/documents")
    public ResponseEntity getDocuments(@PathVariable int id, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<List<Object>>(batchDAO.findDocuments(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity postBatch(@RequestBody Batch batchObj, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Batch>(batchDAO.save(batchObj), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity deleteBatch(@PathVariable int id, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        batchDAO.delete(id);
        return new ResponseEntity<String>("batch deleted, amigo.", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{id}/add/user")
    public ResponseEntity postAddUser(@PathVariable int id, @RequestBody User userObj, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        List<String> previouslyCodedDocuments = documentDAO.verifyUser(id, userObj.getEmail());
        if (previouslyCodedDocuments.isEmpty()) {
            Batch batchObj = batchDAO.find(id);
            List<User> userList = batchObj.getUsers();
            userList.add(userObj);
            batchDAO.save(batchObj);
            return new ResponseEntity<String>("user added, friend", HttpStatus.OK);
        } else {
            previouslyCodedDocuments.sort(String::compareTo);
            String firstDocument = previouslyCodedDocuments.get(0);
            String lastDocument = previouslyCodedDocuments.get(previouslyCodedDocuments.size()-1);
            return new ResponseEntity<String>("Batch contains documents " 
                    + firstDocument + " through " + lastDocument 
                    + " that were coded by " + userObj.getEmail(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}/delete/user/{email:.+}")
    public ResponseEntity deleteUser(@PathVariable int id, @PathVariable String email, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        Batch batchObj = batchDAO.find(id);
        batchDAO.deleteUser(id, email);
        return new ResponseEntity<String>("user deleted, comrade", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{batchid}/add/document/{docid}")
    public ResponseEntity postAddDocument(@PathVariable int batchid, @PathVariable String docid, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        batchDAO.addDocument(batchid, docid);
        return new ResponseEntity<String>("document added, pal", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{batchid}/delete/document/{docid}")
    public ResponseEntity deleteDocument(@PathVariable int batchid, @PathVariable String docid, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        batchDAO.deleteDocument(batchid, docid);
        return new ResponseEntity<String>("document deleted, chum", HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/assignment_types")
    public ResponseEntity getAssignmentTypes(@RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<List<AssignmentType>>(assignmentTypeDAO.list(), HttpStatus.OK);
    }
    
}
