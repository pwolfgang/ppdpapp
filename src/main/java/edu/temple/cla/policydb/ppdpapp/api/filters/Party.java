/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.temple.cla.policydb.ppdpapp.api.filters;

/**
 * The Party filter is a special case of the binary filter. It displays three
 * radio buttons: NO FILTER, Republican, Democrat. 
 * This filter is only used by the Bills/Acts dataset.
 * @author Paul Wolfgang
 */
public class Party extends BinaryFilter {

    private final String parameterName;
    private String parameterValue;

    private String filterQualifier;

    /**
     * Construct a Party object
     * @param id unique ID
     * @param tableId Table containing the dataset
     * @param description Description of the filter
     * @param columnName Column containing the data to be filtered
     * @param tableReference not used.
     * @param additionalParam not used.
     */
    public Party(int id, int tableId, String description,
            String columnName, String tableReference, String additionalParam) {
        super(id, tableId, description, columnName, tableReference,
                additionalParam);
        parameterName = "F" + getId();
    }

}
