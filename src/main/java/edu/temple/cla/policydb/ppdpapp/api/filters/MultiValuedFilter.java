/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.temple.cla.policydb.ppdpapp.api.filters;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.transaction.annotation.Transactional;

/**
 * A multivalued filter consists of a set of radio buttons
 * labeled No Filter, Include, Exclude and a series of check boxes giving
 * the choices to either include or exclude. The choices are given in a
 * table specified by the tableReference attribute.
 * @author Paul Wolfgang
 */
public class MultiValuedFilter extends Filter {

    private static final Logger logger = Logger.getLogger(MultiValuedFilter.class);

    private SessionFactory sessionFactory;

    private String filterChoices;
    
    private String firstFilterChoice;
    
    /**
     * Construct a MultiValuedFilter object
     * @param id unique ID
     * @param tableId Table containing the dataset
     * @param description Description of the filter
     * @param columnName Column containing the data to be filtered
     * @param tableReference Table containing the filter choices
     * @param additionalParam not used.
     */
    public MultiValuedFilter(int id, int tableId, String description,
            String columnName, String tableReference, String additionalParam) {
        super(id, tableId, description, columnName, tableReference,
                additionalParam);
    }
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Transactional
    public void readFilterChoices() {
        Session sess = sessionFactory.getCurrentSession();
        SQLQuery query = sess.createSQLQuery("select * from " + getTableReference() + " order by ID");
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        List<Map<String, Object>> choiceObject = query.list();
        StringJoiner stj = new StringJoiner(",\n", "{", "}");
        choiceObject.forEach(map -> {
                StringJoiner sj2 = new StringJoiner(":");
                map.forEach((k, v) -> {
                    StringBuilder stb = new StringBuilder();
                    if (v instanceof Number) {
                        stb.append(v);
                    } else {
                        stb.append("\"").append(v).append("\"");
                    }
                    sj2.add(stb);
                });
                String choice = sj2.toString();
                stj.add(choice);
                if (firstFilterChoice == null) {
                    StringJoiner sj3 = new StringJoiner(",", "{", "}");
                    map.forEach((k, v) -> {
                        StringBuilder stb = new StringBuilder();
                        stb.append("'")
                                .append(k.toLowerCase())
                        .append("':");
                        if (v instanceof Number) {
                            stb.append(v);
                        } else {
                            stb.append("\"").append(v).append("\"");
                        }
                    sj3.add(stb);
                    });
                    firstFilterChoice = sj3.toString();
                }       
        });
        filterChoices = stj.toString();
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
    
    public String getFilterChoices() {
        return filterChoices;
    }
    
    public String getFirstFilterChoice() {
        return firstFilterChoice;
    }

    public StringBuilder getFilterHtml() {
        StringBuilder sb2 = new StringBuilder();
        String filterName = this.getColumnName().toLowerCase();
        String nc_type_dd_status = "nc_" + filterName + "_dd_status";
        String nc_type = "nc_" + filterName;
        String nc_type_dd_items = "nc_" + filterName + "_dd_items";
        String setType = "set" + getColumnName();
        sb2.append("<div class=\"btn-group\" dropdown is-open=\"").append(nc_type_dd_status).append("\">\n");
        sb2.append("<button type=\"button\" class=\"btn btn-primary dropdown-toggle\" dropdown-toggle\n");
        sb2.append("ng-click='").append(nc_type_dd_status).append(" = !").append(nc_type_dd_status).append("'>\n");
        sb2.append("{{").append(nc_type).append(".description | isUndefined:'Select ").append(filterName).append("'}}<span class=\"caret\"></span>\n");
        sb2.append("</button>\n");
        sb2.append("<ul class=\"dropdown-menu\" role=\"menu\">\n");
        sb2.append("<li ng-repeat=\"(key, value) in ").append(nc_type_dd_items).append("\">\n");
        sb2.append("<a href ng-click=\"").append(setType).append("(key)\">{{value}}</a>\n");
        sb2.append("</li>\n");
        sb2.append("</ul>\n");
        sb2.append("</div>\n");
        return sb2;
    }

    public StringBuilder getFilterJs() {
        StringBuilder stb2 = new StringBuilder();
        String filterName = this.getColumnName().toLowerCase();
        String nc_type_dd_status = "nc_" + filterName + "_dd_status";
        String nc_type = "nc_" + filterName;
        String nc_type_dd_items = "nc_" + filterName + "_dd_items";
        String setType = "set" + filterName;
        stb2.append("        $scope.").append(nc_type_dd_status).append(" = false;\n")
                .append("        $scope.").append(nc_type).append(" = null;\n")
                .append("        $scope.loaded_").append(nc_type).append(" = true;\n")
                .append("        $scope.").append(nc_type_dd_items).append(" = ").append(this.getFilterChoices()).append("\n")
                .append("        $scope.set").append(getColumnName()).append(" = function (id) {\n")
                .append("            key = id;\n")
                .append("            value = $scope.").append(nc_type_dd_items).append("[id];\n")
                .append("            obj = {'id':key, 'description':value};\n")
                .append("            $scope.").append(nc_type).append(" = obj;\n")
                .append("        };\n");
        return stb2;
    }
    
    public StringBuilder getFilterValueJs() {
        StringBuilder stb2 = new StringBuilder();
        String filterName = this.getColumnName().toLowerCase();
        String nc_type_dd_status = "nc_" + filterName + "_dd_status";
        String nc_type = "nc_" + filterName;
        String nc_type_dd_items = "nc_" + filterName + "_dd_items";
        String setType = "set" + filterName;
        String selectedKey = "res." + getColumnName();
        String selectedValue = "$scope." + nc_type_dd_items + "[" + selectedKey + ']';
        stb2.append("        $scope.").append(nc_type_dd_status).append(" = false;\n")
                .append("        $scope.loaded_").append(nc_type).append(" = true;\n")
                .append("        $scope.").append(nc_type_dd_items).append(" = ").append(this.getFilterChoices()).append("\n")
                .append("        $scope.set").append(getColumnName()).append(" = function (id) {\n")
                .append("            key = id;\n")
                .append("            value = $scope.").append(nc_type_dd_items).append("[id];\n")
                .append("            obj = {'id':key, 'description':value};\n")
                .append("            $scope.").append(nc_type).append(" = obj;\n")
                .append("        };\n")
                .append("        $scope.selectedKey = ").append(selectedKey).append(";\n")
                .append("        $scope.selectedValue = ").append(selectedValue).append(";\n")
                .append("        $scope.").append(nc_type).append(" = {'id':$scope.selectedKey, 'description':$scope.selectedValue};\n");
       return stb2;        
    }
    
    public StringBuilder getFilterSetDefaultJs() {
        StringBuilder stb2 = new StringBuilder();
        String filterName = this.getColumnName().toLowerCase();
        String nc_type_dd_status = "nc_" + filterName + "_dd_status";
        String nc_type = "nc_" + filterName;
        String nc_type_dd_items = "nc_" + filterName + "_dd_items";
        String setType = "set" + filterName;
        stb2.append("                if ($scope.")
                .append(nc_type)
                .append(" === null) {\n                    $scope.")
                .append(nc_type).append(" = ")
                .append(this.getFirstFilterChoice())
                .append(";\n                }\n");
        return stb2;
    }
    
    public String getField() {
        String filterName = getColumnName().toLowerCase();
        String nc_type = "nc_" + filterName;
        return getColumnName() + "ID: $scope." + nc_type + ".id";
    }
    
    public String getFieldWOID() {
        String filterName = getColumnName().toLowerCase();
        String nc_type = "nc_" + filterName;
        return getColumnName() + ": $scope." + nc_type + ".id";        
    }
}
