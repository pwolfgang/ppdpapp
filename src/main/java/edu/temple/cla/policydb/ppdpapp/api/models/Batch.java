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

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 * Batch entity.
 * 
* See:
 * http://docs.jboss.org/hibernate/annotations/3.5/reference/en/html/entity.html
 */
@Entity
@Table(name = "Batches")
public class Batch implements Serializable {

    /**
     * Annotated properties/fields.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BatchID", nullable = false)
    private int batchID;

    @Column(name = "FileID", nullable = true)
    private String fileID = null;

    @Column(name = "TablesID", nullable = false)
    private int tablesID;
    
    @Column(name = "AssignmentTypeID", nullable = true)
    private int assignmentTypeID;
    
    @Column(name = "AssignmentDescription", nullable = true)
    private String assignmentDescription;

    @Column(name = "Name", nullable = false)
    private String name;

    @Temporal(TemporalType.DATE)
    @Column(name = "DateAdded", nullable = false)
    private Date dateAdded;

    @Column(name = "Creator", nullable = false)
    private String creator;

    @Temporal(TemporalType.DATE)
    @Column(name = "DateDue", nullable = false)
    private Date dateDue;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "BatchUser", joinColumns = {
        @JoinColumn(name = "BatchID")}, inverseJoinColumns = {
        @JoinColumn(name = "Email")})
    private List<User> users;

    /**
     * Getters.
     */
    public int getBatchID() {
        return this.batchID;
    }

    public String getFileID() {
        return this.fileID;
    }

    public int getTablesID() {
        return this.tablesID;
    }
    
    public int getAssignmentTypeID() {
        return this.assignmentTypeID;
    }
    
    public String getAssignmentDescription() {
        return this.assignmentDescription;
    }

    public String getName() {
        return this.name;
    }

    public Date getDateAdded() {
        return this.dateAdded;
    }

    public String getCreator() {
        return this.creator;
    }

    public Date getDateDue() {
        return this.dateDue;
    }

    public List<User> getUsers() {
        return this.users;
    }

    /**
     * Setters.
     */
    public void setBatchID(int batchID) {
        this.batchID = batchID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public void setTablesID(int tablesID) {
        this.tablesID = tablesID;
    }
    
    public void setAssignmentTypeID(int assignmentTypeID) {
        this.assignmentTypeID = assignmentTypeID;
    }
    
    public void setAssignmentDescription(String assignmentDescription) {
        this.assignmentDescription = assignmentDescription;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setDateDue(Date dateDue) {
        this.dateDue = dateDue;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
