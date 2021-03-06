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
import edu.temple.cla.policydb.ppdpapp.ldap.LDAP;
import edu.temple.cla.policydb.ppdpapp.api.models.User;
import edu.temple.cla.policydb.ppdpapp.api.services.Account;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {
    
    private static final Logger LOGGER = Logger.getLogger(AccountController.class);

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private Account accountSvc;
    @Autowired LDAP ldap;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> postAccount(@RequestHeader(value = "Authorization") String authorization) {
        // Take Base64 encoded string from authorization header and extract username and password.
        String[] values = accountSvc.parseAuthHeader(authorization);

        // Query database and ensure the email exists.
        // On success, generate an access token with expiry date of 24 hours.
        try {
            Date dt;
            Timestamp ts;
            User user = userDAO.find(values[0]);
            if (user == null) {
                LOGGER.error("Unrecognized user " + values[0] + "Attempted login");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .header("WWW-authenticate", "Basic")
                        .body("User not recognized");
            }
            String uid = values[0].split("@")[0];
            Object[] authorized = authorizeUser(uid, values[1]);
            if (!(boolean)(authorized[0])){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .header("WWW-authenticate", "Basic")
                        .body("Incorrect credentials provided " + authorized[1]);
            }
            Calendar c = Calendar.getInstance();

            c.setTime(new Date());
            c.add(Calendar.DATE, 1);
            dt = c.getTime();
            ts = new Timestamp(dt.getTime());

            String randString = RandomStringUtils.randomAlphanumeric(32);
            String token = randString + ":" + ts.getTime();

            // Update user and store in database.
            user.setAccessToken(token);
            userDAO.update(user);
            LOGGER.info(user.getEmail() + "Logged in");
            return new ResponseEntity<>("\"" + token + "\"", HttpStatus.OK);
        } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .header("WWW-authenticate", "Basic")
                        .body("Error processing login " + e);
        }
    }
    
    private Object[] authorizeUser(String uid, String pw) {
        // Check for dummy accounts
        // Note that dummy accounts must be in the Users table, so these
        // accounts are not available on the production system.
        switch (uid) {
            case "admin":
            case "researcher":
            case "sr.researcher":
            case "paul": return new Object[]{true, "dummy account"};
        }
        return ldap.authorize(uid, pw);
    }
    
    @RequestMapping(value = "/logout/{email:.+}", method = RequestMethod.POST)
    public ResponseEntity<?> logout(@PathVariable String email) {
        User user = userDAO.find(email);
        if (user != null) {
            user.setAccessToken(null);
            userDAO.update(user);
            LOGGER.info(user.getEmail() + "Logged out");
            return new ResponseEntity<>(email + " logged out", HttpStatus.OK);
        } else {
            LOGGER.error("Unrecognized email: " + email + "attempted logout");
            return new ResponseEntity<>(email + " not found", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
