/* 
 * Copyright (c) 2018, Temple University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * All advertising materials features or use of this software must display 
 *   the following  acknowledgement
 *   This product includes software developed by Temple University
 * * Neither the name of the copyright holder nor the names of its 
 *   contributors may be used to endorse or promote products derived 
 *   from this software without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package edu.temple.cla.policydb.ppdpapp.api.tables;

import edu.temple.cla.policydb.capcodeassignment.AssignCAPCode;
import edu.temple.cla.policydb.ppdpapp.api.daos.FileDAO;
import edu.temple.cla.policydb.ppdpapp.api.filters.BinaryFilter;
import java.util.List;
import edu.temple.cla.policydb.ppdpapp.api.filters.Filter;
import edu.temple.cla.policydb.ppdpapp.api.filters.MultiValuedFilter;
import edu.temple.cla.policydb.ppdpapp.api.models.MetaData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.emptyMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import javax.persistence.Tuple;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * The AbstractTable provides default implementation of the Table interface. The
 * only method not implemented is getTitleBox.
 *
 * @author Paul Wolfgang
 */
public abstract class AbstractTable implements Table {

    private int id;
    private String tableName;
    private String tableTitle;
    private boolean majorOnly;
    private String codeColumn;
    private String textColumn;
    private String dateColumn;
    private String dateFormat;
    private String yearColumn;
    private String[] drillDownColumns;
    private String[] codingColumns;
    private String linkColumn;
    private int minYear;
    private int maxYear;
    private char qualifier;
    private String noteColumn;
    private boolean dataEntry;
    private boolean editable;
    private boolean required;
    private String documentName;
    private int numCodesRequired;
    private List<Filter> filterList;
    private List<MultiValuedFilter> multiValuedFilterList;
    private List<BinaryFilter> binaryFilterList;
    protected List<MetaData> metaDataList;
    private List<MetaData> textFieldMetaData;
    private List<MetaData> typeAheadFiledsMetaData;
    private List<MetaData> dateFieldMetaData;
    private Set<String> columns;
    private SessionFactory sessionFactory;
    private DataSource datasource;

    protected final Logger LOGGER = Logger.getLogger(getClass());

    /**
     * Get the ID
     *
     * @return the id
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Set the ID
     *
     * @param id the id to set
     */
    @Override
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Return the table name
     *
     * @return the tableName
     */
    @Override
    public String getTableName() {
        return tableName;
    }

    /**
     * Set the table name
     *
     * @param tableName the tableName to set
     */
    @Override
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Get the table title
     *
     * @return the tableTitle
     */
    @Override
    public String getTableTitle() {
        return tableTitle;
    }

    /**
     * Set the table title
     *
     * @param tableTitle the tableTitle to set
     */
    @Override
    public void setTableTitle(String tableTitle) {
        this.tableTitle = tableTitle;
    }

    /**
     * Get majorOnly
     *
     * @return majorOnly
     */
    @Override
    public boolean isMajorOnly() {
        return majorOnly;
    }

    /**
     * Set majorOnly
     *
     * @param majorOnly the value majorOnly to set
     */
    @Override
    public void setMajorOnly(boolean majorOnly) {
        this.majorOnly = majorOnly;
    }

    /**
     * Get the minYear
     *
     * @return the minYear
     */
    @Override
    public int getMinYear() {
        return minYear;
    }

    /**
     * Set the minYear
     *
     * @param minYear the minYear to set
     */
    @Override
    public void setMinYear(int minYear) {
        this.minYear = minYear;
    }

    /**
     * Get the maxYear
     *
     * @return the maxYear
     */
    @Override
    public int getMaxYear() {
        return maxYear;
    }

    /**
     * Set the maxYear
     *
     * @param maxYear the maxYear to set
     */
    @Override
    public void setMaxYear(int maxYear) {
        this.maxYear = maxYear;
    }

    /**
     * Get the filterList
     *
     * @return the filterList
     */
    @Override
    public List<Filter> getFilterList() {
        return filterList;
    }

    /**
     * Get the filterList size
     *
     * @return filterList.size()
     */
    @Override
    public int getFilterListSize() {
        return getFilterList().size();
    }

    /**
     * Set the filterList
     *
     * @param filterList the filterList to set
     */
    @Override
    public void setFilterList(List<Filter> filterList) {
        this.filterList = filterList;
    }

    /**
     * Get the qualifier
     *
     * @return the qualifier
     */
    @Override
    public char getQualifier() {
        return qualifier;
    }

    /**
     * Set the qualifier
     *
     * @param qualifier the qualifier to set
     */
    @Override
    public void setQualifier(char qualifier) {
        this.qualifier = qualifier;
    }

    /**
     * Get the textColumn
     *
     * @return the textColumn
     */
    @Override
    public String getTextColumn() {
        return textColumn;
    }

    /**
     * Set the textColumn
     *
     * @param textColumn the textColumn to set
     */
    @Override
    public void setTextColumn(String textColumn) {
        this.textColumn = textColumn;
    }

    /**
     * Return a String representation of the table
     *
     * @return The tableTitle followed by the filterQualifierString
     */
    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder(getTableTitle());
        getFilterList().forEach(filter -> stb.append("<br/>").append(filter));
        return stb.toString();
    }

    @Override
    public String getDateColumn() {
        return dateColumn;
    }

    @Override
    public void setDateColumn(String dateColumn) {
        this.dateColumn = dateColumn;
    }

    @Override
    public String getDateFormat() {
        return dateFormat;
    }

    @Override
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * Get the column that contains the year
     *
     * @return year column
     */
    @Override
    public String getYearColumn() {
        return yearColumn;
    }

    /**
     * Set the yearColumn
     *
     * @param yearColumn the yearColumn to set
     */
    @Override
    public void setYearColumn(String yearColumn) {
        this.yearColumn = yearColumn;
    }

    /**
     * Get the drillDown columns
     *
     * @return the drill-down columns
     */
    @Override
    public String[] getDrillDownColumns() {
        return drillDownColumns;
    }

    /**
     * Set the drillDown columns
     *
     * @param drillDownColumns the array of columns to display in the drill-down
     * page
     */
    @Override
    public void setDrillDownColumns(String[] drillDownColumns) {
        if (drillDownColumns != null) {
            this.drillDownColumns = drillDownColumns.clone();
        } else {
            this.drillDownColumns = null;
        }
    }

    @Override
    public String[] getCodingColumns() {
        return codingColumns;
    }

    @Override
    public String getCodingColumnsList() {
        StringJoiner sj = new StringJoiner(", ");
        for (String column : codingColumns) {
            sj.add(column);
        }
        if (linkColumn != null) {
            if (linkColumn.contains("#")) {
                sj.add("substring(substring_index(" + linkColumn + ", '#', 2), 2) as Hyperlink");
            } else {
                sj.add(linkColumn + " as Hyperlink");
            }
        }
        return sj.toString();
    }

    @Override
    public void setCodingColumns(String[] codingColumns) {
        this.codingColumns = codingColumns;
    }

    /**
     * Get the linkColumn.
     *
     * @return the linkColumn
     */
    @Override
    public String getLinkColumn() {
        return linkColumn;
    }

    /**
     * Set the linkColumn.
     *
     * @param linkColumn the linkColumn to set
     */
    @Override
    public void setLinkColumn(String linkColumn) {
        this.linkColumn = linkColumn;
    }

    /**
     * Set the noteColumn.
     *
     * @param noteColumn the noteColumn value to be set.
     */
    @Override
    public void setNoteColumn(String noteColumn) {
        this.noteColumn = noteColumn;
    }

    /**
     * Get the noteColumn
     *
     * @return the noteColumn
     */
    @Override
    public String getNoteColumn() {
        return noteColumn != null ? noteColumn : "";
    }

    /**
     * Method to get the Code column name
     *
     * @return the Code column name
     */
    @Override
    public String getCodeColumn() {
        return codeColumn;
    }

    /**
     * Method to set the Code column name
     *
     * @param codeColumn the codeColumn to be set
     */
    @Override
    public void setCodeColumn(String codeColumn) {
        this.codeColumn = codeColumn;
    }

    /**
     * @return the isDataEntry
     */
    @Override
    public boolean isDataEntry() {
        return dataEntry;
    }

    /**
     * @param isDataEntry the isDataEntry to set
     */
    @Override
    public void setDataEntry(boolean isDataEntry) {
        this.dataEntry = isDataEntry;
    }

    /**
     * @return the idEditable
     */
    @Override
    public boolean isEditable() {
        return editable;
    }

    /**
     * @param isEditable
     */
    @Override
    public void setEditable(boolean isEditable) {
        this.editable = isEditable;
    }

    /**
     * @return the documentName
     */
    @Override
    public String getDocumentName() {
        return documentName;
    }

    /**
     * @param documentName the documentName to set
     */
    @Override
    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    /**
     * @return the numCodesRequired
     */
    @Override
    public int getNumCodesRequired() {
        return numCodesRequired;
    }

    /**
     * @param numCodesRequired the numCodesRequired to set
     */
    @Override
    public void setNumCodesRequired(int numCodesRequired) {
        this.numCodesRequired = numCodesRequired;
    }

    @Override
    public boolean isCode3() {
        return numCodesRequired == 3;
    }

    /**
     * Method to populate the template parameters. The following template
     * parameters are defined:
     * <ul>binaryFilters The binary filter entries in the filters object</ul>
     * <ul>binaryFiltersSetValue A sequence of statements to set the filter
     * values from the res object </ul>
     * <ul>columnDefs The columns to be displayed as a columnDefs object
     * fields</ul>
     * <ul>columnTH A list of &lt;th&gt; containing the column names</ul>
     * <ul>columnTD A list of &lt;td&gt; containing the column as entries in the
     * row object</ul>
     * <ul>textFileds A list of columns as object entries mapped to $scope</ul>
     * <ul>defineBinaryFilterFields A list of the filters as object entries
     * mapped to $scope</ul>
     * <ul>dateColumn The date formatted as a grid info entry</ul>
     * <ul>document The document name</ul>
     * <ul>documentUC The document name, first char UC</ul>
     * <ul>tableName The database table name</ul>
     *
     * @return The template parameter as a Map&lt;String, String&gt;
     */
    @Override
    public Map<String, String> getTemplateParameters() {
        Map<String, String> templateParameters = new HashMap<>();
        templateParameters.put("tableName", getTableName());
        templateParameters.put("documentName", getDocumentName());
        templateParameters.put("binaryFilters", getBinaryFilters());
        templateParameters.put("binaryFiltersSetValue", getBinaryFiltersSetValue());
        templateParameters.put("textFieldDefs", getTextFieldDefs());
        templateParameters.put("columnTH", getColumnTH());
        templateParameters.put("columnTD", getColumnTD());
        templateParameters.put("Code3", isCode3() ? "Code3" : null);
        templateParameters.put("hyperLink", (getLinkColumn() != null ? "Hyperlink" : null));
        templateParameters.put("codeColumn", getCodeColumn());
        templateParameters.put("codeColumnTH", getCodeColumnTH());
        templateParameters.put("codeColumnTD", getCodeColumnTD());
        templateParameters.put("textFields", getTextFields());
        templateParameters.put("textFieldsSetValues", getTextFieldsSetValues());
        templateParameters.put("textFieldsHtml", getTextFieldsHtml());
        templateParameters.put("defineBinaryFilterFields", getDefineBinaryFilterFields());
        templateParameters.put("dateColumnDefs", getDateColumnDefs());
        templateParameters.put("document", getDocument());
        templateParameters.put("documentUC", getDocumentUC());
        templateParameters.put("documentNameSignular", getDocumentNameSingular());
        templateParameters.put("tableName", getTableName());
        templateParameters.put("multiValuedFiltersHtml", getMultiValuedFiltersHtml());
        templateParameters.put("multiValuedFiltersJs", getMultiValuedFiltersJs());
        templateParameters.put("multiValuedFiltersSetDefaultJs", getMultiValuedFiltersSetDefaultJs());
        templateParameters.put("multiValuedFiltersFields", getMultiValuedFiltersFields());
        templateParameters.put("multiValuedFiltersValueJs", getMultiValuedFiltersValueJs());
        templateParameters.put("typeAheadFieldsJs", getTypeAheadFieldsJs());
        templateParameters.put("typeAheadFields", getTypeAheadFields());
        templateParameters.put("typeAheadFieldsSetValues", getTypeAheadFieldsSetValues());
        templateParameters.put("typeAheadFieldsHtml", getTypeAheadFieldsHtml());
        templateParameters.put("initializeDateFields", getInitializeDateFields());
        templateParameters.put("setLastDate", getSetLastDate());
        templateParameters.put("dateFields", getDateFields());
        templateParameters.put("dateFieldFunctions", getDateFieldFunctions());
        templateParameters.put("setDateFieldsFromRes", getSetDateFieldsFromRes());
        templateParameters.put("dateFieldsHtml", getDateFieldsHtml());
        templateParameters.put("fileUploadHtml", getFileUploadHtml());
        templateParameters.put("fileUploadJavaScript", getFileUploadJavaScript());
        return templateParameters;
    }

    private List<BinaryFilter> getBinaryFilterList() {
        if (binaryFilterList == null) {
            binaryFilterList = new ArrayList<>();
            filterList.forEach(filter -> {
                if (filter instanceof BinaryFilter) {
                    binaryFilterList.add((BinaryFilter) filter);
                }
            });
        }
        return binaryFilterList;
    }

    public String getBinaryFilters() {
        StringJoiner sj = new StringJoiner(",\n");
        getBinaryFilterList().forEach(filter -> {
            sj.add("{name: '" + filter.getColumnName() + "', value: 0}");
        });
        return sj.toString();
    }

    public String getBinaryFiltersSetValue() {
        StringJoiner sj = new StringJoiner("\n");
        int i = 0;
        for (Filter filter : getBinaryFilterList()) {
            sj.add("$scope.filters[" + (i++) + "].value = res."
                    + filter.getColumnName() + ";");
        }
        return sj.toString();
    }

    public String getTextFieldDefs() {
        StringJoiner sj = new StringJoiner(",\n");
        getTextFieldMetaData().forEach((metaData) -> {
            sj.add("{field: '" + metaData.getColumnName() + "'}");
        });
        return sj.toString();
    }

    public String getColumnTH() {
        StringJoiner sj = new StringJoiner("\n");
        for (String column : getCodingColumns()) {
            sj.add("<th>" + column + "</th>");
        }
        return sj.toString();
    }

    public String getCodeColumnTH() {
        return "<th>" + getCodeColumn() + "</th>";
    }

    public String getColumnTD() {
        StringJoiner sj = new StringJoiner("\n");
        for (String column : getCodingColumns()) {
            sj.add("<td>{{row." + column + "}}</td>");
        }
        return sj.toString();
    }

    public String getCodeColumnTD() {
        return "<td><input type=\"text\" ng-model=\"row.UserCode\" \n"
                + "               ng-model-options=\"{updateOn: 'blur'}\" \n"
                + "               ng-blur=\"codeDoc(row)\"/></td>";
    }

    /**
     * Method to generate the Javascript code to define the column fields in the
     * document object and set their values from the corresponding field in the
     * $scope object.
     *
     * @return Javascript code.
     */
    public String getTextFields() {
        StringJoiner sj = new StringJoiner(",\n");
        getTextFieldMetaData().forEach(metaData -> {
            String column = metaData.getColumnName();
            sj.add(column + ": $scope." + column);
        });
        return sj.toString();
    }

    /**
     * Method to generate the Javascript code to set the column fields from the
     * query result into the $scope object.
     *
     * @return Javascript code
     */
    public String getTextFieldsSetValues() {
        StringJoiner sj = new StringJoiner("\n");
        getTextFieldMetaData().forEach(metaData -> {
            String column = metaData.getColumnName();
            sj.add("$scope." + column + " = res." + column + ";");
        });
        return sj.toString();
    }

    /**
     * Method to generate the html code to display/edit the text fields in the
     * create/edit pages.
     *
     * @return html code
     */
    public String getTextFieldsHtml() {
        StringBuilder stb = new StringBuilder();
        getTextFieldMetaData().forEach(metaData -> {
            String columnName = metaData.getColumnName();
            stb.append("<div class=\"form-group row\"><div class=\"col-md-12\">\n");
            if (metaData.isTypeAhead()) {
                stb.append(metaData.getTypeAheadData().getTypeAheadFieldHtml());
            } else {
                stb.append("<p>").append(columnName).append("</p>\n");
                if (metaData.isEditable()) {
                    if (metaData.getDataType().equals("text")) {
                        stb.append("<textarea class=\"form-control\" placeholder=\"")
                                .append(columnName)
                                .append("\" ng-model=\"")
                                .append(columnName)
                                .append("\"");
                        if (metaData.isRequired()) {
                            stb.append(" required");
                        }
                        stb.append("></textarea>\n");
                    } else {
                        stb.append("<input type=\"text\" class=\"form-control\" placeholder=\"")
                                .append(columnName)
                                .append("\" ng-model=\"")
                                .append(columnName)
                                .append("\"");
                        if (metaData.isRequired()) {
                            stb.append(" required");
                        }
                        stb.append(" />\n");
                    }
                } else if (metaData.isUrl()) {
                    stb.append("<a href=\"{{")
                            .append(columnName)
                            .append("}}\">{{")
                            .append(columnName)
                            .append("}}</a>");
                } else {
                    stb.append("<p ng-bind-html=\"")
                            .append(columnName)
                            .append("\"></p>");
                }
            }
            stb.append("</div></div>\n");
        });
        return stb.toString();
    }

    /**
     * Method to generate the Javascript code to initialize the typeahead fields
     * model.
     *
     * @return Javascript code.
     */
    public String getTypeAheadFieldsJs() {
        StringBuilder stb = new StringBuilder();
        getTypeAheadFieldsMetaData().forEach(metaData -> {
            stb.append(metaData.getTypeAheadData().getTypeAheadFieldJs());
        });
        return stb.toString();
    }

    /**
     * Method to generate the Javascript code to define the column fields in the
     * document object and set their values from the corresponding field in the
     * $scope object.
     *
     * @return Javascript code.
     */
    public String getTypeAheadFields() {
        if (getTypeAheadFieldsMetaData().isEmpty()) {
            return null;
        }
        StringJoiner sj = new StringJoiner(",\n");
        getTypeAheadFieldsMetaData().forEach(metaData -> {
            String column = metaData.getColumnName();
            sj.add(column + ": $scope." + column);
        });
        return sj.toString();
    }

    /**
     * Method to generate the Javascript code to set the column fields from the
     * query result into the $scope object.
     *
     * @return Javascript code
     */
    public String getTypeAheadFieldsSetValues() {
        if (getTypeAheadFieldsMetaData().isEmpty()) {
            return null;
        }
        StringJoiner sj = new StringJoiner("\n");
        getTypeAheadFieldsMetaData().forEach(metaData -> {
            String column = metaData.getColumnName();
            sj.add("$scope." + column + " = res." + column + ";");
        });
        return sj.toString();
    }

    /**
     * Method to generate the html code to display/edit the text fields in the
     * create/edit pages.
     *
     * @return html code
     */
    public String getTypeAheadFieldsHtml() {
        StringBuilder stb = new StringBuilder();
        getTypeAheadFieldsMetaData().forEach(metaData -> {
            stb.append(metaData.getTypeAheadData().getTypeAheadFieldHtml());
        });
        return stb.toString();
    }

    public String getDefineBinaryFilterFields() {
        StringJoiner sj = new StringJoiner(",\n");
        int i = 0;
        for (Filter filter : getBinaryFilterList()) {
            sj.add(filter.getColumnName()
                    + ": $scope.convertBoolToInt($scope.filters["
                    + (i++) + "].value)");
        }
        return sj.toString();
    }

    public String getDocumentNameSingular() {
        String documentUC = getDocumentUC();
        if (documentUC.endsWith("s")) {
            return documentUC.substring(0, documentName.length() - 1);
        } else {
            return documentUC;
        }
    }

    public String getDateColumnDefs() {
        StringJoiner stj = new StringJoiner(",\n");
        getDateFieldMetaData().forEach(metaData -> {
            stj.add(getDateColumnDef(metaData.getColumnName()));
        });
        return stj.toString();
    }

    public String getDateColumnDef(String columnName) {
        return "{name: '" + columnName + "', field: '"
                + columnName + "', cellFilter: 'date:\\'" + getDateFormat() + "\\''}";
    }

    public String getDocument() {
        return documentName;
    }

    public String getDocumentUC() {
        char[] chars = documentName.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    private List<MultiValuedFilter> getMultiValuedFilterList() {
        if (multiValuedFilterList == null) {
            multiValuedFilterList = new ArrayList<>();
            filterList.forEach(filter -> {
                if (filter instanceof MultiValuedFilter) {
                    multiValuedFilterList.add((MultiValuedFilter) filter);
                }
            });
        }
        return multiValuedFilterList;
    }

    /**
     * Method to generate the html to display and enter the multiValued filter
     * on the create/view page.
     *
     * @return html code.
     */
    public String getMultiValuedFiltersHtml() {
        if (getMultiValuedFilterList().isEmpty()) {
            return "";
        }
        StringBuilder stb = new StringBuilder();
        stb.append("<div class=\"form-group row\"><div class=\"col-md-12\">\n");
        getMultiValuedFilterList().forEach(filter -> {
            stb.append(filter.getFilterHtml());
        });
        stb.append("</div>\n");
        return stb.toString();
    }

    /**
     * Method to generate the Javascript code to display the choices and set to
     * selected value when selected from the drop-down.
     *
     * @return JavaScript Code.
     */
    public String getMultiValuedFiltersJs() {
        StringBuilder stb = new StringBuilder();
        getMultiValuedFilterList().forEach(filter -> {
            stb.append(filter.getFilterJs());
        });
        return stb.toString();
    }

    /**
     * Method to generate the Javascript code to display the choices, the
     * currently selected choice and set to selected value when selected from
     * the drop-down.
     *
     * @return JavaScript Code.
     */
    public String getMultiValuedFiltersValueJs() {
        StringBuilder stb = new StringBuilder();
        getMultiValuedFilterList().forEach(filter -> {
            stb.append(filter.getFilterValueJs());
        });
        return stb.toString();
    }

    /**
     * Method to generate Javascript code to set the default value, if no value
     * selected from the dropdown.
     *
     * @return Javascript code.
     */
    public String getMultiValuedFiltersSetDefaultJs() {
        StringBuilder stb = new StringBuilder();
        getMultiValuedFilterList().forEach(filter -> {
            stb.append(filter.getFilterSetDefaultJs());
        });
        return stb.toString();
    }

    /**
     * Method to generate Javascript code to define the fields in the document
     * object.
     *
     * @return Javascript code.
     */
    public String getMultiValuedFiltersFields() {
        List<MultiValuedFilter> theFilterList = getMultiValuedFilterList();
        if (theFilterList.isEmpty()) {
            return "";
        } else {
            StringJoiner stj = new StringJoiner(",\n");
            theFilterList.forEach(filter -> {
                stj.add(filter.getFieldWOID());
            });
            return stj.toString() + "\n";
        }
    }

    /**
     * Return a copy of the MetaDataList
     *
     * @return a copy of the MetaDataList
     */
    @Override
    public List<MetaData> getMetaDataList() {
        return new ArrayList<>(metaDataList);
    }

    /**
     * Set the MetaDataList
     *
     * @param metaDataList The MetaDataList to be set
     */
    @Override
    public void setMetaDataList(List<MetaData> metaDataList) {
        this.metaDataList = new ArrayList<>(metaDataList);
    }

    /**
     * Return a list of the MetaData for the text fields
     *
     * @return a list of the MetaData for the text fields
     */
    public List<MetaData> getTextFieldMetaData() {
        if (textFieldMetaData == null) {
            textFieldMetaData = metaDataList.stream()
                    .filter(item -> (item.getDataType().equals("varchar") || item.getDataType().equals("text")))
                    .collect(Collectors.toList());
        }
        return textFieldMetaData;
    }

    /**
     * Return a list of the MetaData for the type-ahead fields
     *
     * @return a list of the MetaData for the type-ahead fields
     */
    public List<MetaData> getTypeAheadFieldsMetaData() {
        if (typeAheadFiledsMetaData == null) {
            typeAheadFiledsMetaData = metaDataList.stream()
                    .filter(item -> item.getTypeAheadRef() != null)
                    .collect(Collectors.toList());
        }
        return typeAheadFiledsMetaData;
    }

    /**
     * Return a list of the MetaData for the date fields
     *
     * @return a list of the MetaData for the date fields
     */
    public List<MetaData> getDateFieldMetaData() {
        if (dateFieldMetaData == null) {
            dateFieldMetaData = metaDataList.stream()
                    .filter(item -> item.getDataType().startsWith("date"))
                    .collect(Collectors.toList());
        }
        return dateFieldMetaData;
    }

    public String getInitializeDateFields() {
        StringJoiner stj = new StringJoiner("\n");
        for (int i = 0; i < getDateFieldMetaData().size(); i++) {
            stj.add(getInitializeDateField(i));
        }
        return stj.toString();
    }

    private String getInitializeDateField(int index) {
        String s = getIndexSubScript(index);
        return "            var lastDate" + s + " = localStorage.getItem('lastDate" + s + "');\n"
                + "            if (lastDate" + s + " === null || lastDate" + s + "=== 'null') {\n"
                + "                $scope.dt" + s + " = new Date();\n"
                + "            } else {\n"
                + "                $scope.dt" + s + " = new Date(lastDate);\n"
                + "            }\n";
    }

    public String getDateFieldFunctions() {
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < getDateFieldMetaData().size(); i++) {
            String s = getIndexSubScript(i);
            stb.append("        $scope.today")
                    .append(s)
                    .append(" = function () {\n            $scope.dt")
                    .append(s)
                    .append(" = new Date();\n        };\n")
                    .append("        $scope.clear")
                    .append(s).append(" = function () {\n")
                    .append("            $scope.dt").append(s)
                    .append(" = null;\n")
                    .append("        };\n");
        }
        return stb.toString();
    }

    public String getSetLastDate() {
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < getDateFieldMetaData().size(); i++) {
            String s = getIndexSubScript(i);
            stb.append("            localStorage.setItem('lastDate")
                    .append(s)
                    .append("', $scope.dt")
                    .append(s)
                    .append(");\n");
        }
        return stb.toString();
    }

    public String getDateFields() {
        StringJoiner sj = new StringJoiner(",\n");
        for (int i = 0; i < getDateFieldMetaData().size(); i++) {
            String dc = getDateFieldMetaData().get(i).getColumnName();
            sj.add(getDateField(i, dc));
        }
        return sj.toString();
    }

    public String getDateField(int index, String dateColumn) {
        String s = getIndexSubScript(index);
        return "                " + dateColumn + ": ($scope.dt" + s + ") ? "
                + " $scope.dt" + s + ".getFullYear() + '-' + ($scope.dt" + s
                + ".getMonth() + 1) + '-' + $scope.dt" + s + ".getDate()"
                + ": null";
    }

    public String getSetDateFieldsFromRes() {
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < getDateFieldMetaData().size(); i++) {
            String s = getIndexSubScript(i);
            String dc = getDateFieldMetaData().get(i).getColumnName();
            stb.append("$scope.dt")
                    .append(s)
                    .append(" = new Date(res.")
                    .append(dc)
                    .append(");\n");
        }
        return stb.toString();
    }

    public String getDateFieldsHtml() {
        StringBuilder stb = new StringBuilder();
        for (int i = 0; i < getDateFieldMetaData().size(); i++) {
            stb.append(getDateFieldHtml(i, getDateFieldMetaData().get(i)));
        }
        return stb.toString();
    }

    public String getDateFieldHtml(int index, MetaData metadata) {
        String columnName = metadata.getColumnName();
        String req = metadata.isRequired() ? "true" : "false";
        String s = getIndexSubScript(index);
        return "                <p class=\"input-group\">\n"
                + "                " + columnName + "</br>\n"
                + "                <datepicker ng-model=\"dt" + s
                + "\" show-weeks=\"false\" class=\"well well-sm margin-bottom-xsmall\" "
                + "style=\"display:inline-block\"></datepicker><br />\n"
                + "                <input type=\"text\" class=\"form-control\" "
                + "datepicker-popup=\"MM/dd/yyyy\" ng-model=\"dt" + s + "\" \n"
                + "                       datepicker-options=\"dateOptions\" "
                + "ng-required=\"" + req + "\" close-text=\"Close\" /><br />\n"
                + "                <button type=\"button\" class=\"btn btn-sm btn-info\" "
                + "ng-click=\"today" + s + "()\">Today</button>\n"
                + "                <button type=\"button\" class=\"btn btn-sm btn-danger\" "
                + "ng-click=\"clear" + s + "()\">Clear</button>\n"
                + "                </p>\n"
                + "";
    }

    private String getIndexSubScript(int index) {
        if (index == 0) {
            return "";
        } else {
            return String.format("_%d", index);
        }
    }

    /**
     * @return the required
     */
    @Override
    public boolean isRequired() {
        return required;
    }

    /**
     * @param required the required to set
     */
    @Override
    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public Set<String> getColumns() {
        return columns;
    }

    @Override
    public void setColumns(Collection<String> columns) {
        this.columns = Collections.unmodifiableSet(new LinkedHashSet<>(columns));
    }

    @Override
    public String getFileUploadHtml() {
        return null;
    }

    @Override
    public String getFileUploadJavaScript() {
        return null;
    }

    @Override
    public ResponseEntity<?> uploadFile(String docObjJson, MultipartFile file) {
        return new ResponseEntity<>("File Upload not Supported", HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<?> uploadFile(FileDAO fileDAO, MultipartFile file) {
        return new ResponseEntity<>("File Upload not Supported", HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Method to publish the new documents. This method finds table entries in
     * the PAPolicy_Copy version of a table that do not have a corresponding row
     * in the PAPolicy version, and whose code is not null. These rows are then
     * inserted into the PAPolicy version. This method is used only for those
     * tables which other tables are not linked to it.
     *
     * @return HttpStatus.OK if successful, otherwise an error status.
     */
    @Override
    public ResponseEntity<?> publishDataset() {
        int numberChanged;
        try (Session sess = sessionFactory.openSession()) {
            String metaDataQuery = "select COLUMN_NAME from INFORMATION_SCHEMA.COLUMNS "
                    + "where table_name='" + tableName + "' and table_schema='PAPolicy'";
            @SuppressWarnings("unchecked")
            List<String> columnNames = sess.createNativeQuery(metaDataQuery).list();
            String selectedColumns = columnNames.stream().collect(Collectors.joining(", "));
            String query = "insert into PAPolicy." + tableName + " "
                    + "(select " + selectedColumns + " from PAPolicy_Copy." + tableName + " where "
                    + "PAPolicy_Copy." + tableName + ".ID in (select PAPolicy_Copy." + tableName + ".ID "
                    + "from PAPolicy_Copy." + tableName + " left join PAPolicy." + tableName + " on "
                    + "PAPolicy_Copy." + tableName + ".ID=PAPolicy." + tableName + ".ID "
                    + "where isNull (PAPolicy." + tableName + ".ID)) and "
                    + "not isNull(PAPolicy_Copy." + tableName + "." + codeColumn + "))";
            Transaction tx = sess.beginTransaction();
            numberChanged = sess.createNativeQuery(query)
                    .executeUpdate();
            tx.commit();
        }
        return new ResponseEntity<>(documentName + " has been published "
                + numberChanged + " rows matched", HttpStatus.OK);
    }

    /**
     * Method to update the codes. This method updates the PAPolicy copy of the
     * table so that the Code and CAPCode are equal to the corresponding Code
     * and CAPCode in the PAPolicy_Copy version.
     *
     * @return HttpStatus.OK if successful, otherwise an error status.
     */
    @Override
    public ResponseEntity<?> updateCodes() {
        String query = String.format("update PAPolicy.%s left join "
                + "PAPolicy_Copy.%s on PAPolicy.%s.ID=PAPolicy_Copy.%s.ID  "
                + "set PAPolicy.%s.%s=PAPolicy_Copy.%s.%s,"
                + "PAPolicy.%s.CAPCode=PAPolicy_Copy.%s.CAPCode "
                + "where not isNull(PAPolicy_Copy.%s.%s)",
                tableName, tableName, tableName, tableName,
                tableName, codeColumn, tableName, codeColumn,
                tableName, tableName, tableName, codeColumn);
        try (Session sess = sessionFactory.openSession()) {
            Transaction tx = sess.beginTransaction();
            sess.createNativeQuery(query)
                    .executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            throw new RuntimeException("Error Excecuting Query " + query, ex);
        }
        return new ResponseEntity<>(documentName + " has been updated ", HttpStatus.OK);
    }

    /**
     * Method to update all fields. This method updates the PAPolicy copy of the
     * table so that all fields are equal to the values in the PAPolicy_Copy
     * version.
     *
     * @return HttpStatus.OK if successful, otherwise an error status.
     */
    @Override
    public ResponseEntity<?> updateAll() {
        try (Session sess = sessionFactory.openSession()) {
            String metaDataQuery = "select COLUMN_NAME from INFORMATION_SCHEMA.COLUMNS "
                    + "where table_name='" + tableName + "' and table_schema='PAPolicy'";
            @SuppressWarnings("unchecked")
            List<String> columnNames = sess.createNativeQuery(metaDataQuery).list();
            String assignment = columnNames.stream()
                    .filter(s -> !s.equals("ID"))
                    .map(s -> "PAPolicy." + tableName + "." + s + "=" + "PAPolicy_Copy." + tableName + "." + s)
                    .collect(Collectors.joining(", "));
            String updateTemplate = "UPDATE PAPolicy.%s left join PAPolicy_Copy.%s "
                    + "ON PAPolicy.%s.ID=PAPolicy_Copy.%s.ID SET %s";
            String updateQuery = String.format(updateTemplate, tableName,
                    tableName, tableName, tableName, assignment);
            LOGGER.info(updateQuery);
            Transaction tx = sess.beginTransaction();
            sess.createNativeQuery(updateQuery)
                    .executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            throw new RuntimeException("Error updating all fields", ex);
        }
        return new ResponseEntity<>(documentName + " has been updated ", HttpStatus.OK);
    }

    /**
     * Method to assign CAP codes. For editable tables that are not major only
     * this method assigns the CAP code based on a crosswalk. Since codes can be
     * changed after the original coding phase a scan is then made to flag those
     * records which the CAP code does not match the crosswalk for manual
     * review. For major only tables, CAP Code assignment is done by the
     * CAPCodeAssignment class.
     *
     * @return
     */
    @Override
    public ResponseEntity<?> assignCAPCode() {
        if (isMajorOnly()) {
            AssignCAPCode assignCAPCode = new AssignCAPCode();
            assignCAPCode.setDataSource(datasource);
            assignCAPCode.setSessionFactory(sessionFactory);
            return assignCAPCode.doAssignment(this);
        }
        String assignCAPCodeTemplate = "update %s left join PPAtoCAP on "
                + "%s.%s=PPAtoCAP.PPACode set %s.CAPCode=PPAtoCAP.CAPCode "
                + "where isNull(%s.CAPCode)";
        String setCAPOkTemplate = "update %s left join PPAtoCAP on "
                + "%s.%s=PPAtoCAP.PPACode set CAPOk=1 "
                + "where %s.CAPCode=PPAtoCAP.CAPCode;";
        String assignCAPCodeQuery = String.format(assignCAPCodeTemplate,
                tableName, tableName, getCodeColumn(), tableName, tableName);
        String setCAPOkQuery = String.format(setCAPOkTemplate,
                tableName, tableName, getCodeColumn(), tableName);
        try (Session sess = sessionFactory.openSession()) {
            Transaction tx = sess.beginTransaction();
            sess.createNativeQuery(assignCAPCodeQuery)
                    .executeUpdate();
            sess.createNativeQuery(setCAPOkQuery)
                    .executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            throw new RuntimeException("Error Excecuting Query \n"
                    + assignCAPCodeQuery
                    + "\nor\n" + setCAPOkQuery, ex);
        }
        return new ResponseEntity<>(documentName + " has been updated ", HttpStatus.OK);
    }

    @Override
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public void setDataSource(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public DataSource getDataSource() {
        return datasource;
    }

}
