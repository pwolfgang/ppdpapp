package edu.temple.cla.policydb.ppdpapp.api.controllers;


import edu.temple.cla.policydb.ppdpapp.api.daos.NewspaperDAO;
import edu.temple.cla.policydb.ppdpapp.api.models.Newspaper;
import edu.temple.cla.policydb.ppdpapp.api.models.User;
import edu.temple.cla.policydb.ppdpapp.api.services.Account;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Newspapers")
public class NewspaperController {

    @Autowired
    private NewspaperDAO NewspaperDAO;
    @Autowired
    private Account accountSvc;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getNewspapers(@RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<List<Newspaper>>(NewspaperDAO.list(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/add/{name}")
    public ResponseEntity postNewspapers(@PathVariable String name, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        NewspaperDAO.add(name);
        return new ResponseEntity<String>("newspaper added, m8 (pronounced 'mate')", HttpStatus.OK);
    }
}
