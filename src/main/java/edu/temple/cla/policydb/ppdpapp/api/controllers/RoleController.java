package edu.temple.cla.policydb.ppdpapp.api.controllers;


import edu.temple.cla.policydb.ppdpapp.api.daos.RoleDAO;
import edu.temple.cla.policydb.ppdpapp.api.models.Role;
import edu.temple.cla.policydb.ppdpapp.api.models.User;
import edu.temple.cla.policydb.ppdpapp.api.services.Account;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleDAO roleDAO;
    @Autowired
    private Account accountSvc;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getRoles(@RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<List<Role>>(roleDAO.list(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity getRole(@PathVariable int id, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Role>(roleDAO.find(id), HttpStatus.OK);
    }
}
