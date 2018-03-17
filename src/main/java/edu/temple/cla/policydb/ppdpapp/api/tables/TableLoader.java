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
import edu.temple.cla.policydb.ppdpapp.api.filters.MultiValuedFilter;
import edu.temple.cla.policydb.ppdpapp.api.models.MetaData;
import edu.temple.cla.policydb.ppdpapp.api.models.TypeAheadData;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Paul Wolfgang
 */
public class TableLoader {

    private static final Logger LOGGER = Logger.getLogger(TableLoader.class);
    @Autowired
    private final SessionFactory sessionFactory;

    private List<Table> tableList;
    private Map<String, Table> documentNameMap;
    private Map<String, Table> tableNameMap;

    public TableLoader(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Load all table objects and associated filters.
     *
     * @return The tables as a list
     */
    @Transactional
    public List<Table> getTables() {
        if (tableList == null) {
            final Session sess = sessionFactory.getCurrentSession();
            try {
            NativeQuery tableQuery = sess.createNativeQuery("SELECT * FROM Tables ORDER BY ID");
            tableQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> tableObjectMapList = tableQuery.list();
            List<Table> tempTableList = new ArrayList<>();
            documentNameMap = new HashMap<>();
            tableNameMap = new HashMap<>();
            tableObjectMapList.forEach(tableObjectMap -> {
                Table table = loadTable(tableObjectMap, sess);
                tempTableList.add(table);
                documentNameMap.put(table.getDocumentName(), table);
                tableNameMap.put(table.getTableName(), table);
            });
            Table[] tableArray = tempTableList.toArray(new Table[tempTableList.size()]);
            tableList = Arrays.asList(tableArray);
            } catch (Exception ex) {
                LOGGER.error("Failure to load tables", ex);
                throw ex;
            }
        }
        return tableList;
    }

    /**
     * Load the selected table object and associated filters.
     *
     * @param tableId The table id
     * @return The requested table object.
     */
    public Table getTable(String tableId) {
        int id = Integer.parseInt(tableId);
        return getTableById(id);
    }
    
    public Table getTableById(int id) {
        getTables();
        return tableList.get(id - 1);
    }

    /**
     * Load the selected table object by DocumentName.
     *
     * @param documentName
     * @return The requested table object.
     */
    public Table getTableByDocumentName(String documentName) {
        getTables();
        return documentNameMap.get(documentName);
    }
    
    public Table getTableByTableName(String tableName) {
        getTables();
        return tableNameMap.get(tableName);
    }

    private Table loadTable(Map<String, Object> tableObjectMap, Session sess) {
        Table table = mapTable(tableObjectMap);
        int tableId = table.getId();
        NativeQuery filterQuery = sess.createNativeQuery("SELECT * from Filters WHERE TableID=" 
                + tableId + " ORDER BY ID");
        filterQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> filterObjectList = filterQuery.list();
        List<Filter> filterList = new ArrayList<>();
        filterObjectList.forEach(filterMapObject -> filterList.add(mapFilter(filterMapObject)));
        table.setFilterList(filterList);
        NativeQuery metaDataQuery = sess.createNativeQuery("SELECT * from MetaData WHERE TableID=" 
                + tableId + " ORDER BY ID");
        metaDataQuery.addEntity(MetaData.class);
        @SuppressWarnings("unchecked")
        List<MetaData> metaDataList = metaDataQuery.list();
        table.setMetaDataList(metaDataList);
        metaDataList.forEach(metaData -> {
            if (metaData.getTypeAheadRef() != null) {
                TypeAheadData typeAheadData = new TypeAheadData(metaData.getColumnName(), metaData.getTypeAheadRef());
                typeAheadData.loadTypeAheadData(sess);
                metaData.setTypeAheadData(typeAheadData);
            }
        });
        NativeQuery getColumnNames = sess.createNativeQuery("SELECT column_name "
                + "from information_schema.columns where table_schema='PAPolicy_Copy' "
                + "and table_name='" + table.getTableName() + "'");
        @SuppressWarnings("unchecked")
        List<String> columns = getColumnNames.list();
        table.setColumns(columns);
        table.setSessionFactory(sessionFactory);
        return table;
    }
    

    private Table mapTable(Map<String, Object> tableObjectMap) {
        String className = (String) tableObjectMap.get("Class");
        String packageName = "edu.temple.cla.policydb.ppdpapp.api.tables";
        Table item = null;
        try {
            item
                    = (Table) Class.forName(packageName + "."
                            + className).newInstance();
            item.setId((Integer) tableObjectMap.get("ID"));
            item.setTableName((String) tableObjectMap.get("TableName"));
            item.setTableTitle((String) tableObjectMap.get("TableTitle"));
            item.setMajorOnly((Boolean) tableObjectMap.get("MajorOnly"));
            item.setCodeColumn((String) tableObjectMap.get("CodeColumn"));
            item.setTextColumn((String) tableObjectMap.get("TextColumn"));
            item.setDateColumn((String) tableObjectMap.get("DateColumn"));
            item.setDateFormat((String) tableObjectMap.get("DateFormat"));
            item.setYearColumn((String) tableObjectMap.get("YearColumn"));
            item.setMinYear((Integer) tableObjectMap.get("MinYear"));
            item.setMaxYear((Integer) tableObjectMap.get("MaxYear"));
            item.setLinkColumn((String) tableObjectMap.get("LinkColumn"));
            String drillDownColumnList = (String) tableObjectMap.get("DrillDownFields");
            if (drillDownColumnList != null) {
                String[] drillDownColumnListArray = drillDownColumnList.split(",\\s*");
                item.setDrillDownColumns(drillDownColumnListArray);
            }
            String codingColumnsList = (String) tableObjectMap.get("CodingColumns");
            if (codingColumnsList != null) {
                String[] codingColumnsListArray = codingColumnsList.split(",\\s*");
                item.setCodingColumns(codingColumnsListArray);
            }
            item.setNoteColumn((String) tableObjectMap.get("Note"));
            item.setDataEntry((Boolean) tableObjectMap.get("IsDataEntry"));
            item.setEditable((Boolean) tableObjectMap.get("IsEditable"));
            item.setDocumentName((String) tableObjectMap.get("DocumentName"));
            item.setNumCodesRequired((Byte) tableObjectMap.get("NumCodesRequired"));
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            LOGGER.error(ex);
        }
        return item;
    }

    private Filter mapFilter(Map<String, Object> filterObjectMap) {
        String packageName = "edu.temple.cla.policydb.ppdpapp.api.filters";
        String className = (String) filterObjectMap.get("FilterClass");
        try {
            Class<?> itemClass = Class.forName(packageName + "." + className);
            @SuppressWarnings("unchecked")
            Constructor<Filter> constructor
                    = (Constructor<Filter>) itemClass.getDeclaredConstructor(
                            int.class, int.class, String.class, String.class,
                            String.class, String.class);
            Integer id = (Integer) filterObjectMap.get("ID");
            Integer tableId = (Integer) filterObjectMap.get("TableID");
            String columnName = (String) filterObjectMap.get("ColumnName");
            String description = (String) filterObjectMap.get("Description");
            String tableReference = (String) filterObjectMap.get("TableReference");
            String additionalParam = (String) filterObjectMap.get("AdditionalParam");
            Filter item = constructor.newInstance(id, tableId, description,
                    columnName, tableReference, additionalParam);
            if (item instanceof MultiValuedFilter) {
                MultiValuedFilter multiValuedFilter = (MultiValuedFilter)item;
                multiValuedFilter.setSessionFactory(sessionFactory);
                multiValuedFilter.readFilterChoices();
            }
            return item;
        } catch (ClassNotFoundException 
                | NoSuchMethodException 
                | SecurityException 
                | InstantiationException 
                | IllegalAccessException 
                | IllegalArgumentException 
                | InvocationTargetException ex) {
            LOGGER.error(ex);
        }
        return null;
    }

}
