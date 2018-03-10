package edu.temple.cla.policydb.ppdpapp.api.filters;

import javax.servlet.http.HttpServletRequest;

/**
 * A binary filter presents the choice of either including or excluding a
 * filter. It also has the option to ignore, which is the default.
 *
 * @author Paul Wolfgang
 */
public class BinaryFilter extends Filter {

    private final String parameterName;
    private String parameterValue;
    private static final String BOTH = "587";
    private String filterQualifier;

    /**
     * Construct a BinaryFilter object
     *
     * @param id The unique id
     * @param tableId The id of the referencing table
     * @param description The description of this filter
     * @param columnName The column name to base the filter on.
     * @param tableReference Not used, NULL
     * @param additionalParam Not used, NULL
     */
    public BinaryFilter(int id, int tableId, String description,
            String columnName, String tableReference, String additionalParam) {
        super(id, tableId, description, columnName, tableReference,
                additionalParam);
        parameterName = "F" + id;
    }
    
    @Override
    public String getFilter() {
        return "{name: '" + getColumnName() + ";, value:0}";
    }
    
    @Override
    public String getConvertToBoolInt(int i) {
        return getColumnName() + ": $scope.convertBoolToInt($scope.filters[" + i + "].value)";
    }
    
    @Override
    public String getSetFilterValue(int i) {
        return "$scope.filters[" + i + "].value = res." + getColumnName();
    }


}
