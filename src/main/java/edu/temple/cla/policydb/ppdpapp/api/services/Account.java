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
package edu.temple.cla.policydb.ppdpapp.api.services;


import edu.temple.cla.policydb.ppdpapp.api.daos.UserDAO;
import edu.temple.cla.policydb.ppdpapp.api.models.User;
import java.nio.charset.Charset;
import java.util.Date;
import javax.security.sasl.AuthenticationException;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Account {

    @Autowired
    private UserDAO userDAO;

    public String[] parseAuthHeader(String header) {
        String base64Credentials = header.substring("Basic".length()).trim();
        String credentials = new String(Base64.decodeBase64(base64Credentials), Charset.forName("UTF-8"));
        return credentials.split(":", 2);
    }

    public boolean isAccessTokenExpired(String token) {
        // simply extracting the timestamp from the token string and passing it as a date to the overloaded method.
        try {
            String[] values = token.split(":", 2);
            long tokenTime = Long.parseLong(values[1]);
            Date dt = new Date(tokenTime);
            Date today = new Date();
            return today.after(dt);
            //return false;
        } catch (Exception e) {
            return true; // change back to true after done debugging...
        }
    }

    public User doAuthentication(String token) throws AuthenticationException {
        if (isAccessTokenExpired(token)) {
            throw new AuthenticationException("Token Expired");
        }
        User user = userDAO.findByToken(token);
        if (user == null) {
            throw new AuthenticationException("User not logged in");
        }
        return user;
    }
}
