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
package edu.temple.cla.policydb.ppdpapp.api.models;

import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;
import javax.persistence.*;

//import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * User entity.
 * 
* See:
 * http://docs.jboss.org/hibernate/annotations/3.5/reference/en/html/entity.html
 */
@Entity
@Table(name = "Users")
public class User {

    /**
     * Annotated properties/fields.
     */
    @Id
    @Column(name = "Email", nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "RoleID")
    private Role role;

    @Column(name = "FirstName", nullable = false)
    private String firstName;

    @Column(name = "LastName", nullable = false)
    private String lastName;

    @Column(name = "IsActive", nullable = true)
    private boolean isActive;

    @Column(name = "DateAdded", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateAdded;

    @Column(name = "AccessToken", nullable = true)
    private String accessToken;

    /**
     * Getters.
     */
    public String getEmail() {
        return this.email;
    }

    public Role getRole() {
        return this.role;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public Date getDateAdded() {
        return this.dateAdded;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    /**
     * Setters.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(Role roleObj) {
        this.role = roleObj;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String toJson() {
        StringJoiner sj = new StringJoiner(",", "{", "}");
        sj.add("\"email\":\"" + email + "\"");
        sj.add("\"role\":" + role.toJson());
        sj.add("\"firstName\":\"" + firstName + "\"");
        sj.add("\"lastName\":\"" + lastName + "\"");
        sj.add("\"isActive\":" + isActive );
        sj.add("\"dateAdded\":\"" + dateAdded + "\"");
        sj.add("\"accessToken\":\"" + accessToken + "\"");
        return sj.toString();
    }
    
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this.getClass() == o.getClass()) {
            User other = (User) o;
            if (!Objects.equals(email, other.email)) return false;
            if (!Objects.equals(role, other.role)) return false;
            if (!Objects.equals(firstName, other.firstName)) return false;
            if (!Objects.equals(lastName, other.lastName)) return false;
            if (isActive != other.isActive) return false;
            return Objects.equals(dateAdded, other.dateAdded);
        } else {
            return false;
        }
    }
}
