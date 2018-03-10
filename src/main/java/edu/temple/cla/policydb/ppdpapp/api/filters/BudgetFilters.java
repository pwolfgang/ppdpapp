/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.temple.cla.policydb.ppdpapp.api.filters;

/**
 * Class to generate the filter form input for the budget related data.
 * @author Paul Wolfgang
 */
public class BudgetFilters extends Filter {

    private final String dispParameterName = "disp";
    private final String adjustParameterName = "adjust";
    private final String baseYearParameterName = "baseYear";
    private String dispParameterValue;
    private String adjustParameterValue;
    private String baseYearParameterValue;

    /**
     * Construct a BudgetFilter object
     * @param id Unique id of this filter
     * @param tableId ID of the referencing table
     * @param description NULL, hard coded in the class
     * @param columnName NULL, hard coded in the class
     * @param tableReference NULL, not used
     * @param additionalParam Reference to the Deflator table.
     */
    public BudgetFilters(int id, int tableId, String description,
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
