/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
            SQLQuery tableQuery = sess.createSQLQuery("SELECT * FROM Tables ORDER BY ID");
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
        SQLQuery filterQuery = sess.createSQLQuery("SELECT * from Filters WHERE TableID=" + tableId + " ORDER BY ID");
        filterQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> filterObjectList = filterQuery.list();
        List<Filter> filterList = new ArrayList<>();
        filterObjectList.forEach(filterMapObject -> filterList.add(mapFilter(filterMapObject)));
        table.setFilterList(filterList);
        SQLQuery metaDataQuery = sess.createSQLQuery("SELECT * from MetaData WHERE TableID=" + tableId + " ORDER BY ID");
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
        SQLQuery getColumnNames = sess.createSQLQuery("SELECT column_name from information_schema.columns where table_schema='PAPolicy_Copy' and table_name='" + table.getTableName() + "'");
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
