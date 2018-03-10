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

import edu.temple.cla.policydb.ppdpapp.api.filters.Filter;
import edu.temple.cla.policydb.ppdpapp.api.models.MetaData;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * The Table interface defines the methods for each of the datasets.
 * For each of the datasets methods are required to generate the javascript
 * and html files for the app that will provide the necessary controllers
 * and views to enter, edit, code, and tiebreak the data.
 * Table objects are created and initialized from the Tables table in the
 * database. 
 * @author Paul Wolfgang
 */
public interface Table {

    /**
     * The unique identifier of this table as defined in the Tables table
     * @return the id
     */
    int getId();

    /**
     * The table identifier is set when the class is loaded, either for
     * initial display of the analysis form, or when the request is processed.
     * @param id the id to set
     */
    void setId(int id);

    /**
     * Return the table name as defined in the Tables table
     * @return the tableName
     */
    String getTableName();

    /**
     * The table name is set when the class is loaded.
     * @param tableName the tableName to set
     */
    void setTableName(String tableName);
    
       /**
     * Determine if three codes are required for tiebreak.
     * @return true if three codes are required for tiebreak.
     */
    boolean isCode3();

    /**
     * The table title as defined in the Tables table
     * @return the tableTitle
     */
    String getTableTitle();

    /**
     * The table title is set when the class is loaded.
     * @param tableTitle the tableTitle to set
     */
    void setTableTitle(String tableTitle);

    /**
     * This data set is only searchable by major topic.
     * @return the majorOnly
     */
    boolean isMajorOnly();

    /**
     * Set when the class is loaded.
     * @param majorOnly the majorOnly to set
     */
    void setMajorOnly(boolean majorOnly);
    
    /**
     * The minimum year for which data is available
     * @return the minYear
     */
    int getMinYear();

    /**
     * Set when the class is loaded
     * @param minYear the minYear to set
     */
    void setMinYear(int minYear);

    /**
     * The maximum year for which data is available
     * @return the maxYear
     */
    int getMaxYear();

    /**
     * Set when the class is loaded
     * @param maxYear the maxYear to set
     */
    void setMaxYear(int maxYear);

    /**
     * The list of filters is defined in the Filters table
     * @return the filterList
     */
    List<Filter> getFilterList();

    /**
     * Return the number of filters
     * @return filterList.size()
     */
    int getFilterListSize();

    /**
     * The filter list is set when the class is loaded.
     * @param filterList the filterList to set
     */
    void setFilterList(List<Filter> filterList);
    
    /**
     * The meta data is defined in the MetaData table
     * @return the metaData
     */
    List<MetaData> getMetaDataList();
    
    /**
     * The meta data list is set when the class is loaded.
     * @param metaDataList the metaDataList to set
     */
    void setMetaDataList(List<MetaData> metaDataList);

    /**
     * Get the qualifier
     * @return the qualifier
     */
    char getQualifier(); 

    /**
     * Set the qualifier
     * @param qualifier the qualifier to set
     */
    void setQualifier(char qualifier);

    /**
     * Get the textColumn
     * @return the textColumn
     */
    public String getTextColumn();
    
    /**
     * Set the textColumn
     * @param textColumn the textColumn to set
     */
    void setTextColumn(String textColumn);

    /**
     * Get the linkColumn.
     * @return the linkColumn
     */
    String getLinkColumn();

    /**
     * Set the linkColumn. Set when the table is loaded.
     * @param linkColumn the linkColumn to set
     */
    void setLinkColumn(String linkColumn);

    /**
     * Method to get the Code column name
     *
     * @return the Code column name
     */
    String getCodeColumn();

    /**
     * Method to set the Code column name. Called when table is loaded.
     * @param codeColumn value to be set
     */
    void setCodeColumn(String codeColumn);
    
    String getDateColumn();
    void setDateColumn(String dateColumn);
    String getDateFormat();
    void setDateFormat(String dateFormat);
    
    /**
     * Get the column that contains the year
     * @return year column
     */
    String getYearColumn();
    
    /**
     * Set the yearColumn
     * @param yearColumn the yearColumn to set
     */
    void setYearColumn(String yearColumn);

    /**
     * Get the drillDown columns
     * @return the drill-down columns
     */
    String[] getDrillDownColumns();

    /**
     * Set the drillDown columns
     * @param drillDownColumns the array of columns to display in the
     * drill-down page
     */
    void setDrillDownColumns(String[] drillDownColumns);
    
    String[] getCodingColumns();
    
    String getCodingColumnsList();
    
    void setCodingColumns(String[] codingColumns);
    
    /**
     * Set the noteColumn.
     * @param noteColumn the noteColumn value to be set.
     */
    void setNoteColumn(String noteColumn); 

    /**
     * Get the noteColumn
     * @return the noteColumn
     */
    String getNoteColumn();

    /**
     * @return the isDataEntry
     */
    boolean isDataEntry();

    /**
     * @param isDataEntry the isDataEntry to set
     */
    void setDataEntry(boolean isDataEntry);

    /**
     * @return the idEditable
     */
    public boolean isEditable();

    /**
     * @param isEditable
     */
    public void setEditable(boolean isEditable);
    
    public boolean isRequired();
    
    public void setRequired(boolean isRequired);
    
    public Set<String> getColumns();
    
    public void setColumns(Collection<String> columns);

    /**
     * @return the documentName
     */
    String getDocumentName();

    /**
     * @param documentName the documentName to set
     */
    void setDocumentName(String documentName);

    /**
     * @return the numCodesRequired
     */
    int getNumCodesRequired();

    /**
     * @param numCodesRequired the numCodesRequired to set
     */
    void setNumCodesRequired(int numCodesRequired);
    
    /**
     * Method to populate the template parameters.
     * The following template parameters are defined:
     * <ul>binaryFilters The binary filter entries in the filters object</ul>
     * <ul>binaryFiltersSetValue A sequence of statements to set the filter values from the res object </ul>
     * <ul>columnDefs The columns to be displayed as a columnDefs object fields</ul>
     * <ul>columnTH A list of &lt;th&gt; containing the column names</ul>
     * <ul>columnTD A list of &lt;td&gt; containing the column as entries in the row object</ul>
     * <ul>columnFields A list of columns as object entries mapped to $scope</ul>
     * <ul>defineBinaryFilterFields A list of the filters as object entries mapped to $scope</ul>
     * <ul>dateColumn The date formatted as a grid info entry</ul>
     * <ul>document The document name</ul>
     * <ul>documentUC The document name, first char UC</ul>
     * <ul>tableName The database table name</ul>
     * @return The template parameter as a Map&lt;String, String&gt;
     */
    Map<String, String> getTemplateParameters();
    
    ResponseEntity<?> uploadFile(String docObjJson, MultipartFile file);
    
    void setSessionFactory(SessionFactory sessionFactory);
    
    /**
     *
     * @return
     */
    SessionFactory getSessionFactory();

}
