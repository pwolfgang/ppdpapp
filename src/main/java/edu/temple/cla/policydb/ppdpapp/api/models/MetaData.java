/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.cla.policydb.ppdpapp.api.models;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Paul
 */
@Entity
@Table(name = "MetaData")
public class MetaData implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "TableID")
    private Integer tableID;
    @Basic(optional = false)
    @Column(name = "TABLE_NAME")
    private String tableName;
    @Basic(optional = false)
    @Column(name = "COLUMN_NAME")
    private String columnName;
    @Basic(optional = false)
    @Column(name = "DATA_TYPE")
    private String dataType;
    @Column(name = "MAX_LENGTH")
    private Short maxLength;
    @Column(name = "Editable")
    private Boolean editable;
    @Column(name = "Required")
    private Boolean required;
    @Column(name = "url")
    private Boolean url;
    @Column(name = "TypeAheadRef")
    private String typeAheadRef;
    @Transient
    private TypeAheadData typeAheadData;
    

    public MetaData() {
    }

    public MetaData(Integer id) {
        this.id = id;
    }

    public MetaData(Integer id, String tableName, String columnName, String dataType) {
        this.id = id;
        this.tableName = tableName;
        this.columnName = columnName;
        this.dataType = dataType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTableID() {
        return tableID;
    }

    public void setTableID(Integer tableID) {
        this.tableID = tableID;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Short getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Short maxLength) {
        this.maxLength = maxLength;
    }

    public Boolean isEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }
    
    public Boolean isRequired() {
        return required;
    }
    
    public void setRequired(Boolean required) {
        this.required = required;
    }
    
    public Boolean isUrl() {
        return url;
    }
    
    public void setUrl(Boolean url) {
        this.url = url;
    }

    public String getTypeAheadRef() {
        return typeAheadRef;
    }

    public void setTypeAheadRef(String typeAheadRef) {
        this.typeAheadRef = typeAheadRef;
    }
    
    public boolean isTypeAhead() {
        return typeAheadRef != null;
    }
       
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MetaData)) {
            return false;
        }
        MetaData other = (MetaData) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "edu.temple.cla.policydb.ppdpapp.api.models.MetaData[ id=" + id + " ]";
    }
    
    public TypeAheadData getTypeAheadData() {
        return typeAheadData;
    }
    
    public void setTypeAheadData(TypeAheadData typeAheadData) {
        this.typeAheadData = typeAheadData;
    }
    
}
