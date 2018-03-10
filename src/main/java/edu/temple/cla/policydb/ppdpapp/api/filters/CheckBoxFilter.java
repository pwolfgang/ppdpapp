/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.temple.cla.policydb.ppdpapp.api.filters;

/**
 * A checkbox filter allows the selection of an option.  Actually two radio
 * buttons are displayed. The name checkbox is historical. The change to
 * radio buttons was made to support accessibility. 
 * @author Paul Wolfgang
 */
public class CheckBoxFilter extends BinaryFilter {

    private final String parameterName;
    private String parameterValue;

    private String filterQualifier;

    public CheckBoxFilter(int id, int tableId, String description,
            String columnName, String tableReference, String additionalParam) {
        super(id, tableId, description, columnName, tableReference,
                additionalParam);
        parameterName = "F" + id;
    }

}
