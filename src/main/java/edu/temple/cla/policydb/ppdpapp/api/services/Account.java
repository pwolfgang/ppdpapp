package edu.temple.cla.policydb.ppdpapp.api.services;


import edu.temple.cla.policydb.ppdpapp.api.daos.UserDAO;
import edu.temple.cla.policydb.ppdpapp.api.models.User;
import java.nio.charset.Charset;
import java.util.Date;
import javax.security.sasl.AuthenticationException;

import org.apache.tomcat.util.codec.binary.Base64;
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
            throw new AuthenticationException();
        }
        User user = userDAO.findByToken(token);
        if (user == null) {
            throw new AuthenticationException();
        }
        return user;
    }
}
