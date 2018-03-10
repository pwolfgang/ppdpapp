/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.temple.cla.policydb.ppdpapp.api.filters;

/**
 * Filter that consists of several options displayed in a dropdown form.
 * @author Paul Wolfgang
 */
public class DropDown extends MultiValuedFilter {

    public DropDown(int id, int tableId, String description,
            String columnName, String tableReference, String additionalParam) {
        super(id, tableId, description, columnName, tableReference,
                additionalParam);
    }

}
