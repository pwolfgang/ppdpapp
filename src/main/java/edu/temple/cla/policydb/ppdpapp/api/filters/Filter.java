/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.temple.cla.policydb.ppdpapp.api.filters;

import java.util.Objects;

/**
 * A Filter is responsible for displaying the html fragment that is included
 * in the form, and for generating the appropriate query fragment based
 * on the form input.
 * @author Paul Wolfgang
 */
public abstract class Filter {

    private final int id;
    private final int tableId;
    private final String description;
    private final String columnName;
    private final String tableReference;
    private final String additionalParam;

    /**
     * Construct a Filter
     * @param id The unique ID of this filter from the database
     * @param tableId The unique ID of the referencing table
     * @param description The description displayed on the form
     * @param columnName The column of the database which this filter is based on.
     * @param tableReference Table that provides additional information for the filter
     * (for example, drop down items).
     * @param additionalParam An additional paramter for this filter.
     */
    public Filter(int id, int tableId, String description, String columnName,
            String tableReference, String additionalParam) {
        this.id = id;
        this.tableId = tableId;
        this.description = description;
        this.columnName = columnName;
        this.tableReference = tableReference;
        this.additionalParam = additionalParam;
    }

    /**
     * Return the id
     * @return the id
     */
    public final int getId() {
        return id;
    }

    /**
     * Return the tableId
     * @return the tableId
     */
    public final int getTableId() {
        return tableId;
    }

    /**
     * Return the description
     * @return the description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Return the columnName
     * @return the columnName
     */
    public final String getColumnName() {
        return columnName;
    }

    /**
     * Return the table reference
     * @return the tableReference
     */
    public final String getTableReference() {
        return tableReference;
    }

    /**
     * Return the additional parameter
     * @return the additionalParam
     */
    public final String getAdditionalParam() {
        return additionalParam;
    }
    
    public abstract String getFilter();
    
    public abstract String getConvertToBoolInt(int i);
    
    public abstract String getSetFilterValue(int i);
       
    /**
     * Determine if two filter objects are equal.
     * @param o The other object
     * @return True if this and o are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() == o.getClass()) {
            Filter other = (Filter) o;
            return (getId() == other.getId() &&
                    getTableId() == other.getTableId() &&
                    getDescription().equals(other.getDescription()) &&
                    getTableReference() == null ? other.getTableReference() == null : getTableReference().equals(other.getTableReference()) &&
                    getAdditionalParam() == null ? other.getAdditionalParam() == null : getAdditionalParam().equals(other.getAdditionalParam()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.id;
        hash = 67 * hash + this.tableId;
        hash = 67 * hash + Objects.hashCode(this.columnName);
        hash = 67 * hash + Objects.hashCode(this.tableReference);
        hash = 67 * hash + Objects.hashCode(this.additionalParam);
        return hash;
    }

}
