/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.temple.cla.policydb.ppdpapp.api.filters;

/**
 * The mention filter is similar to the binary filter.  Originally three options
 * were available: no mention, mention, and significant mention, but the
 * significant mention option has been eliminated. However, some data is still
 * coded for significant mention. Therefore, the separate filter class is
 * still needed.
 * @author Paul Wolfgang
 */
public class Mention extends BinaryFilter {

    /**
     * Construct a BinaryFilter object
     * @param id The unique id
     * @param tableId The id of the referencing table
     * @param description The description of this filter
     * @param columnName The column name to base the filter on.
     * @param tableReference Not used, NULL
     * @param additionalParam Not used, NULL
     */
    public Mention(int id, int tableId, String description,
            String columnName, String tableReference, String additionalParam) {
        super(id, tableId, description, columnName, tableReference,
                additionalParam);
    }

}
