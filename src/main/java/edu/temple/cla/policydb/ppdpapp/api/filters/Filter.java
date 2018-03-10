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
