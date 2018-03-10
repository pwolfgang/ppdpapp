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
