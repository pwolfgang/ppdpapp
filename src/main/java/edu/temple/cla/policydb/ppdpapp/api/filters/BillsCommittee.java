/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.temple.cla.policydb.ppdpapp.api.filters;

/**
 * Filter to select the committee(s) that held hearings on a bill.
 * @author Paul Wolfgang
 */
public class BillsCommittee extends Filter {

    private final String parameterName;
    private final String primaryName;
    private String parameterValue;
    private String primaryValue;
    private String filterQualifier;
    private final String chamberNumber;

    /**
     * Construct a BillsCommittee object
     * @param id The unique id
     * @param tableId The table ID
     * @param description The description
     * @param columnName Null -- not used by this class
     * @param tableReference Reference to the CommitteeAliases table
     * @param additionalParam House or Senate
     */
    public BillsCommittee(int id, int tableId, String description, 
            String columnName, String tableReference, String additionalParam) {
        super(id, tableId, description, columnName, tableReference,
                additionalParam);
        parameterName = "F" + getId();
        if (getAdditionalParam().equals("House")) {
            chamberNumber = "1";
            primaryName = "hprimary";
        } else {
            chamberNumber = "2";
            primaryName = "sprimary";
        }
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
