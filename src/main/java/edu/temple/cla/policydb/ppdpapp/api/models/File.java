package edu.temple.cla.policydb.ppdpapp.api.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * File entity.
 *
 * See:
 * http://docs.jboss.org/hibernate/annotations/3.5/reference/en/html/entity.html
 */
@Entity
@Table(name = "Files")
public class File {

    /**
     * Annotated properties/fields.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FileID", nullable = false)
    private Integer fileID;

    @Column(name = "Name", nullable = true)
    private String name;

    @Column(name = "FileURL", nullable = true)
    private String fileURL;

    @Column(name = "DateAdded", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dateAdded;

    @Column(name = "Creator", nullable = true)
    private String creator;
    
    @Column(name = "ContentType", nullable = true)
    private String contentType;

    /**
     * Getters and setters
     */
    public Integer getFileID() {
        return fileID;
    }

    public void setFileID(Integer fileID) {
        this.fileID = fileID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
}
