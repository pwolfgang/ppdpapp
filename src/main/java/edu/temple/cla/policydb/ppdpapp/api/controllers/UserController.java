package edu.temple.cla.policydb.ppdpapp.api.controllers;


import edu.temple.cla.policydb.ppdpapp.api.daos.UserDAO;
import edu.temple.cla.policydb.ppdpapp.api.models.Batch;
import edu.temple.cla.policydb.ppdpapp.api.models.User;
import edu.temple.cla.policydb.ppdpapp.api.services.Account;
import java.util.List;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private Account accountSvc;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getUsers(@RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(userDAO.list(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{email:.+}")
    public ResponseEntity<?> getUser(@PathVariable String email, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(userDAO.find(email), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> postUser(@RequestBody User userObj, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(userDAO.save(userObj), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{email:.+}/batches")
    public ResponseEntity<?> getUserBatches(@PathVariable String email, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(userDAO.findBatches(email), HttpStatus.OK);
    }
}
