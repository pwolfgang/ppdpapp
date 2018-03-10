/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.cla.policydb.ppdpapp.api.filters;

/**
 * The WhereHeld filter is a special case of the BinaryFilter. It displays a set
 * of radio buttons BOTH, &lt;city&gt;, Outside &lt;city&gt. The &lt;city&gt; is
 * set to "Harrisburg" by the additionalParameter from the Filters database row.
 * This allows this to be used in other states.
 *
 * @author Paul Wolfgang
 */
public class WhereHeld extends BinaryFilter {

    private static final String BOTH = "587";
    private final String parameterName;
    private String parameterValue;
    private String filterQualifier;

    /**
     * Construct a Filter
     *
     * @param id The unique ID of this filter from the database
     * @param tableId The unique ID of the referencing table
     * @param description The description displayed on the form
     * @param columnName The column of the database which this filter is based
     * on.
     * @param tableReference not used.
     * @param additionalParam Specifies the capital city.
     */
    public WhereHeld(int id, int tableId, String description,
            String columnName, String tableReference, String additionalParam) {
        super(id, tableId, description, columnName, tableReference,
                additionalParam);
        parameterName = "F" + getId();
    }

}
