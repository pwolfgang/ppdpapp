/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.temple.cla.policydb.ppdpapp.api.filters;

import javax.servlet.http.HttpServletRequest;

/**
 * The PublicOpinionFilters are not actual filters.
 * They display choices for the display of the public opinion data.
 * @author Paul Wolfgang
 */
public class PublicOpinionFilters extends Filter {

    private String mipdisp;

    /**
     * Construct a Filter
     * @param id The unique ID of this filter from the database
     * @param tableId The unique ID of the referencing table
     * @param description not used
     * @param columnName not used
     * @param tableReference not used
     * @param additionalParam not used
     */
    public PublicOpinionFilters(int id, int tableId, String description,
            String columnName, String tableReference, String additionalParam) {
        super(id, tableId, description, columnName, tableReference,
                additionalParam);
    }

    @Override
    public String getFilter() {
        return null;
    }
    
    @Override
    public String getConvertToBoolInt(int i) {
        return null;
    }
    
    @Override
    public String getSetFilterValue(int i) {
        return null;
    }
}
