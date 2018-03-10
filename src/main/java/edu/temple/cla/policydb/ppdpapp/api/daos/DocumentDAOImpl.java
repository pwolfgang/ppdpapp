package edu.temple.cla.policydb.ppdpapp.api.daos;

import edu.temple.cia.policydb.ppdpapp.util.BillsUtil;
import edu.temple.cla.policydb.ppdpapp.api.tables.Table;
import edu.temple.cla.policydb.ppdpapp.api.tables.TableLoader;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import static java.util.Collections.emptyMap;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.SQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class DocumentDAOImpl implements DocumentDAO {

    @Autowired
    private final SessionFactory sessionFactory;
    @Autowired
    private TableLoader tableLoader;

    public DocumentDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public Object find(String docType, String id) {
        Object docObj = sessionFactory.getCurrentSession().get(Object.class, id);
        return docObj;
    }

    @Override
    @Transactional
    public List<Object> findDocuments(String docType) {
        Session sess = sessionFactory.getCurrentSession();
        Integer tableID = tablesIDByName(docType);
        Map<String, Integer> statMap = getStatMap(sess, tableID);
        SQLQuery query = sess.createSQLQuery("select * from " + docType + " order by ID desc");
        try {
            List<Map<String, Object>> queryList = applyStatusToQueryResult(query, statMap);
            return (List) queryList;
        } catch (Exception ex) {
            throw new RuntimeException("Error in query " + query, ex);
        }
    }

    @Override
    @Transactional
    public List<Object> findDocumentsPage(String docType, int page) {
        Session sess = sessionFactory.getCurrentSession();
        Integer tableID = tablesIDByName(docType);
        int startRow = page * 25;
        Map<String, Integer> statMap = getStatMap(sess, tableID);
        SQLQuery query = sess.createSQLQuery("select * from " + docType + " order by ID desc"
                + " LIMIT " + startRow + ", 25");
        try {
            List<Map<String, Object>> queryList = applyStatusToQueryResult(query, statMap);
            return (List) queryList;
        } catch (Exception ex) {
            throw new RuntimeException("Error in query " + query, ex);
        }
    }

    private List<Map<String, Object>> applyStatusToQueryResult(SQLQuery query, Map<String, Integer> statMap) {
        query.setResultTransformer(SpecialAliasToEntityMapResultTransformer.INSTANCE);
        List<Map<String, Object>> queryList = query.list();
        queryList.forEach(entry -> {
            String id = (String) entry.get("ID");
            Integer stat = statMap.getOrDefault(id, 0);
            entry.put("stat", stat);
        });
        return queryList;
    }

    private List<Map<String, Object>> applyStatusToQueryResult(SQLQuery query, Map<String, Integer> statMap, int desiredStat) {
        query.setResultTransformer(SpecialAliasToEntityMapResultTransformer.INSTANCE);
        List<Map<String, Object>> queryList = query.list();
        List<Map<String, Object>> result = new ArrayList<>();
        queryList.forEach(entry -> {
            String id = (String) entry.get("ID");
            Integer stat = statMap.getOrDefault(id, 0);
            if (stat == desiredStat) {
                entry.put("stat", stat);
                result.add(entry);
            }
        });
        return result;
    }

    private Map<String, Integer> getStatMap(Session sess, int tableID) {
        Table table = tableLoader.getTableById(tableID);
        int maxNumberOfCodes = table.getNumCodesRequired();
        SQLQuery statusQuery = sess.createSQLQuery("select DocumentID, "
                + "count(DocumentID) as stat from UserPolicyCode where "
                + "TablesID=" + tableID + " group by DocumentID");
        List<Object[]> statList = statusQuery.list();
        Map<String, Integer> statMap = new HashMap<>();
        statList.forEach(row -> {
            String ID = (String) row[0];
            int stat = ((BigInteger) row[1]).intValue();
            if (stat > 0 && stat < maxNumberOfCodes) {
                stat = 1;
            } else if (stat == maxNumberOfCodes) {
                stat = 2;
            }
            statMap.put(ID, stat);
        });
        return statMap;
    }

    @Override
    @Transactional
    public List<Object> findDocumentsNoBatch(String tableName, int assignmentType, int batch_id) {
        Session sess = sessionFactory.getCurrentSession();
        Table table = tableLoader.getTableByTableName(tableName);
        int tableID = table.getId();
        String codeColumn = table.getCodeColumn();
        Integer desiredStat = null;
        switch (assignmentType) {
            case 2:
                desiredStat = 0;
                break;
            case 3:
                desiredStat = 1;
                break;
            case 4:
                desiredStat = 2;
                break;
        }
        Map<String, Integer> statMap = getStatMap(sess, tableID);
        SQLQuery query = sess.createSQLQuery("SELECT * FROM " + tableName + " ns "
                + "WHERE isNull(ns." + codeColumn + ") AND ns.ID NOT IN "
                + "(select DocumentID from UserPolicyCode where TablesID="
                + tableID + " and Email in (SELECT Email from BatchUser where "
                + "BatchID=" + batch_id + ")) AND ns.ID NOT IN "
                + "(SELECT bd.DocumentID FROM BatchDocument bd "
                + "JOIN Batches on bd.BatchID=Batches.BatchID WHERE "
                + "AssignmentTypeID=" + assignmentType + " "
                + "AND bd.TablesID = " + tableID + ") Order By ID Desc");
        try {
            List<Map<String, Object>> queryList = applyStatusToQueryResult(query, statMap, desiredStat);
            List<Map<String, Object>> filteredQueryList = new ArrayList<>();
            for (Map<String, Object> entry : queryList) {
                if (entry.get("stat").equals(desiredStat)) {
                    filteredQueryList.add(entry);
                }
            }
            return (List) filteredQueryList;
        } catch (Exception ex) {
            throw new RuntimeException("Error in query " + query, ex);
        }
    }

    @Override
    @Transactional
    public String getDocumentCount(String tableName) {
        Session sess = sessionFactory.getCurrentSession();
        SQLQuery query = sess.createSQLQuery("select count(ID) from " + tableName);
        try {
            return query.uniqueResult().toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error in query " + query, ex);
        }
    }

    @Override
    @Transactional
    public Object findDocument(String tableName, String id) {
        if (tableName.equals("Transcript")) {
            return findHearing(id);
        }
        Session sess = sessionFactory.getCurrentSession();
        SQLQuery query = sess.createSQLQuery("SELECT * FROM " + tableName + " WHERE ID = '" + id + "'");
        query.setResultTransformer(SpecialAliasToEntityMapResultTransformer.INSTANCE);
        return query.uniqueResult();
    }

    @Transactional
    private Object findHearing(String id) {
        Session sess = sessionFactory.getCurrentSession();
        SQLQuery query = sess.createSQLQuery("SELECT * FROM Transcript WHERE ID = '" + id + "'");
        query.setResultTransformer(SpecialAliasToEntityMapResultTransformer.INSTANCE);
        Map<String, Object> queryResult = (Map<String, Object>) query.uniqueResult();
        SQLQuery getCommittees = sess.createSQLQuery("select AlternateName from Transcript_Committee join CommitteeAliases on committeeId=ID where transcriptId= '" + id + "'");
        String committees = formatCommitteesList(getCommittees.list());
        queryResult.put("Committees", committees);
        SQLQuery getBills = sess.createSQLQuery("SELECT BillID from Transcript_BillID where TranscriptID = '" + id + "'");
        String bills = BillsUtil.createBillLinks(getBills.list());
        queryResult.put("Bills", bills);
        SQLQuery getWitnesses = sess.createSQLQuery("select testimonyURL from Witness where TranscriptID='" + id + "'");
        String witnesses = formatWitnesses(getWitnesses.list());
        queryResult.put("WitnessTranscriptURLs", witnesses);
        return queryResult;
    }

    private String formatCommitteesList(List<String> list) {
        StringJoiner sj = new StringJoiner("<br/>\n");
        list.forEach(s -> sj.add(s));
        return sj.toString();
    }

    private String formatWitnesses(List<String> witnesses) {
        StringJoiner sj = new StringJoiner("<br/>\n");
        witnesses.forEach(w -> {
            if (w != null) {
                sj.add("<a href=\"" + w + "\">" + w + "</a>");
            }
        });
        return sj.toString();
    }

    @Override
    @Transactional
    public List<Map<String, String>> findDocumentCodes(String docType, String id) {
        Session sess = sessionFactory.getCurrentSession();
        SQLQuery query = sess.createSQLQuery("SELECT * FROM UserPolicyCode WHERE documentID = '" + id + "'");
        query.setResultTransformer(SpecialAliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }

    @Override
    @Transactional
    public Object findDocumentCode(String docType, String id, String email) {
        Session sess = sessionFactory.getCurrentSession();
        SQLQuery query = sess.createSQLQuery("SELECT * FROM UserPolicyCode WHERE documentID = '" + id + "' and Email = '" + email + "'");
        query.setResultTransformer(SpecialAliasToEntityMapResultTransformer.INSTANCE);
        return query.uniqueResult();
    }

    @Override
    @Transactional
    public void addDocumentCode(String email, String tableName, String docid, String batchid, int codeid) {
        //set some initial variables to make the logic below more straightforward
        Table table = tableLoader.getTableByTableName(tableName);
        int tableID = table.getId();
        int maxNumOfCodes = table.getNumCodesRequired();
        Session sess = sessionFactory.getCurrentSession();

        //take the batchID
        //count how many UserPolicyCodes there currently are for that document/table.
        //if the maxCodes = numberOfCodes
        //set the final code
        //insert into UserPolicyCode
        //endIf
        //if there are too many codes
        //throw exception
        SQLQuery query = sess.createSQLQuery("SELECT Code FROM UserPolicyCode WHERE DocumentID = '"
                + docid + "' AND TablesID = " + tableID + " AND Email <> '" + email + "'");
        List<Integer> userPolicyCodes = query.list();
        Integer matches = 1; //because the codeid always matches itsself
        if (userPolicyCodes.size() == maxNumOfCodes) { //if there is already the max value of userPolicyCodes in the database, this must be a tiebreak.
            //insert into UserPolicyCode
            insertUserPolicyCode(email, tableName, docid, batchid, codeid);
            //set the final code
            updateDocumentFinalCode(tableName, docid, batchid, codeid);
        } else if (userPolicyCodes.size() < maxNumOfCodes) { // enter this logic-block if the size of less than our maxNumOfCodes
            for (int i = 0; i <= userPolicyCodes.size() - 1; i++) { // keep looping while i is less than the size minus 1 (to avoid nullpointer error)
                if (userPolicyCodes.get(i) == codeid) { // we are comparing to the codeid submitted by the user
                    matches++;
                    // if we have reached the number of matches needed, exit the loop.
                    if (matches == maxNumOfCodes) {
                        break;
                    }
                }
            }
            // check again and do what needs to be done.
            if (matches == maxNumOfCodes) {
                //insert into UserPolicyCode
                insertUserPolicyCode(email, tableName, docid, batchid, codeid);
                //set final code
                updateDocumentFinalCode(tableName, docid, batchid, codeid);
            } else {
                //insert into UserPolicyCode
                insertUserPolicyCode(email, tableName, docid, batchid, codeid);
            }
        }
    }

    @Override
    @Transactional
    public List<Object> findDocumentsNoCodes(String tableName, int batchid, String email) {
        Session sess = sessionFactory.getCurrentSession();
        Table table = tableLoader.getTableByTableName(tableName);
        String codingColumnsList = table.getCodingColumnsList();
        SQLQuery query = sess.createSQLQuery("select " + codingColumnsList
                + ", UserPolicyCode.Code as UserCode from BatchDocument "
                + "left join UserPolicyCode on "
                + "BatchDocument.BatchID=UserPolicyCode.BatchID "
                + "and BatchDocument.DocumentID=UserPolicyCode.DocumentID "
                + "join " + tableName + " on "
                + "BatchDocument.DocumentID=" + tableName + ".ID "
                + "where (BatchDocument.BatchId=" + batchid + ") and (Email='" + email + "' or isNull(Email))");
        query.setResultTransformer(SpecialAliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }

    @Override
    @Transactional
    public List<Object> findDocumentsTieBreak(String tableName, int batchid, String email) {
        Session sess = sessionFactory.getCurrentSession();
        Table table = tableLoader.getTableByTableName(tableName);
        int tableId = table.getId();
        String codingColumnsList = table.getCodingColumnsList();
        String codeColumn = codeColumnByName(tableName);
        SQLQuery query = sess.createSQLQuery("select " + codingColumnsList + ", "
                + codeColumn + " as UserCode from BatchDocument "
                + "join " + tableName + " on "
                + "BatchDocument.DocumentID=" + tableName + ".ID "
                + "where (BatchDocument.BatchId=" + batchid + ") order by BatchDocument.DocumentID");
        query.setResultTransformer(SpecialAliasToEntityMapResultTransformer.INSTANCE);
        List<Map<String, Object>> documentsList = query.list();
        SQLQuery query2 = sess.createSQLQuery("select BatchDocument.DocumentID, "
                + "Code from BatchDocument join UserPolicyCode on "
                + "BatchDocument.TablesID=UserPolicyCode.TablesID and "
                + "BatchDocument.DocumentID=UserPolicyCode.DocumentID where "
                + "BatchDocument.TablesID=" + tableId + " and "
                + "BatchDocument.BatchId=" + batchid + " and "
                + "UserPolicyCode.Email<>'" + email + "'");
        query2.setResultTransformer(SpecialAliasToEntityMapResultTransformer.INSTANCE);
        List<Map<String, Object>> codeList = query2.list();
        Map<String, Map<String, Object>> resultMap = new HashMap<>();
        documentsList.forEach(row -> {
            resultMap.put((String) row.get("ID"), row);
        });
        codeList.forEach(codeRow -> {
            Map<String, Object> row = resultMap.getOrDefault(codeRow.get("DocumentID"), emptyMap());
            List<Integer> codes = (List<Integer>) row.getOrDefault("Codes", new ArrayList<>());
            Integer code = (Integer) codeRow.get("Code");
            if (code != null) {
                codes.add(code);
            }
            row.put("Codes", (Object) codes);
        });
        List<Object> result = new ArrayList<>();
        resultMap.forEach((k, v) -> {
            List<Integer> codes = (List<Integer>) v.get("Codes");
            for (int i = 0; i < codes.size(); i++) {
                String key = String.format("Code%d", i + 1);
                v.put(key, (Object) codes.get(i));
            }
            result.add(v);
        });
        return result;
    }

    @Override
    @Transactional
    public void updateDocument(String tableName, Object docObj) {
        Session sess = sessionFactory.getCurrentSession();
        Map<String, String> map = (Map) docObj;
        Object[] keyArray = map.keySet().toArray();
        Object[] valueArray = map.values().toArray();
        Table table = tableLoader.getTableByTableName(tableName);
        Set<String> columns = table.getColumns();
        Object docID = "";
        int mapSize = map.size();
        String sql = "UPDATE " + tableName + " SET ";
        for (int i = 0; i < mapSize; i++) {
            if (columns.contains(keyArray[i])) {
                String sqlAppend;
                if (valueArray[i] != null) {
                    String valueString = valueArray[i].toString();
                    if (valueString.startsWith("1969-12")) { // Ignore dates in December 1969
                        sqlAppend = keyArray[i] + " = NULL, ";
                    } else {
                        sqlAppend = keyArray[i] + " = '" + valueString.replace("'", "''") + "', ";
                    }
                } else {
                    sqlAppend = keyArray[i] + " = NULL, ";
                }
                sqlAppend = sqlAppend.replace(":", "\\:");
                if (keyArray[i] == "ID") {
                    docID = valueArray[i];
                } else {
                    sql = sql + sqlAppend;
                }
            }
        }
        sql = sql.substring(0, sql.length() - 2) + " WHERE ID = '" + docID + "'";
        try {
            SQLQuery query = sess.createSQLQuery(sql);
            query.executeUpdate();
        } catch (Exception ex) {
            throw new RuntimeException("Error in SQL query " + sql, ex);
        }
    }

    @Override
    @Transactional
    public int insertDocument(String tableName, Object docObj) {
        Session sess = sessionFactory.getCurrentSession();
        Map<String, Object> map = (Map) docObj;
        StringJoiner columns = new StringJoiner(", ", "(", ")");
        StringJoiner values = new StringJoiner(", ", "(", ")");
        SQLQuery findLastID = sess.createSQLQuery("SELECT max(ID) from " + tableName + ";");
        Integer lastID = (Integer) findLastID.uniqueResult();
        int docID = lastID + 1;
        columns.add("ID");
        values.add(String.valueOf(docID));
        map.forEach((k, v) -> {
            if (!k.equals("ID")) {
                columns.add(k);
                values.add("'" + v.toString().replace("'", "''") + "'");
            }
        });
        String sql = "INSERT INTO " + tableName + " " + columns + " VALUES " + values + ";";
        SQLQuery query = sess.createSQLQuery(sql);
        try {
            query.executeUpdate();
        } catch (Exception ex) {
            throw new RuntimeException("Error executing Hibernate query " + query.getQueryString(), ex);
        }
        return docID;
    }

    public void insertUserPolicyCode(String email, String tableName, String docid, String batchid, int codeid) {
        Session sess = sessionFactory.getCurrentSession();
        Integer tableID = tablesIDByName(tableName);
        String codeName = codeColumnByName(tableName);
        try {
            int batchIdInt = Integer.parseInt(batchid);
        } catch (NumberFormatException ex) {
            batchid = "NULL";
        }
        SQLQuery query = sess.createSQLQuery("INSERT INTO UserPolicyCode (Email, DocumentID ,TablesID, BatchID, Code"
                + ") VALUES ('" + email + "','" + docid + "'," + tableID + "," + batchid + "," + codeid + ");");
        try {
            if (query.executeUpdate() != 1) {
                throw new RuntimeException("Insert to UserPolicyCode Failed " + query);
            }
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause == null || !cause.getMessage().startsWith("Duplicate entry")) {
                throw new RuntimeException("Insert to UserPolicyCode Failed", e);
            }
            query = sess.createSQLQuery("UPDATE UserPolicyCode SET Code = " + codeid
                    + " WHERE (Email = '" + email + "' and DocumentID = '" + docid + "' and TablesID = " + tableID + " AND BatchID = " + batchid + ");");
            if (query.executeUpdate() != 1) {
                SQLQuery query2 = sess.createSQLQuery("SELECT BatchID from UserPolicyCode"
                        + " WHERE (Email = '" + email + "' and DocumentID = '" + docid + "' and TablesID = " + tableID + ");");
                List<Integer> resultList = query2.list();
                if (!resultList.isEmpty()) {
                    throw new RuntimeException(email + " attempt to add a code to batch " + batchid + " but aleady coded in batch " + resultList.get(0));
                } else {
                    throw new RuntimeException("Update to UserPolicyCode Failed " + query);
                }
            }

        }
    }

    public void updateDocumentFinalCode(String tableName, String docid, String batchid, int codeid) {
        Session sess = sessionFactory.getCurrentSession();
        Integer tableID = tablesIDByName(tableName);
        String codeName = codeColumnByName(tableName);
        //sets the document as complete.
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        try {
            int batchIdInt = Integer.parseInt(batchid);
        } catch (NumberFormatException ex) {
            batchid = "NULL";
        }
        SQLQuery query;
        if ("NULL".equals(batchid)) {
            query = sess.createSQLQuery("UPDATE BatchDocument SET DateCompleted = '" + dateString + "' WHERE DocumentID = '" + docid + "' AND TablesID = " + tableID + " AND isNull(BatchID)");
        } else {
            query = sess.createSQLQuery("UPDATE BatchDocument SET DateCompleted = '" + dateString + "' WHERE DocumentID = '" + docid + "' AND TablesID = " + tableID + " AND BatchID = " + batchid);
        }
        query.executeUpdate();
        query = sess.createSQLQuery("UPDATE " + tableName + " SET " + codeName + " = " + codeid
                + " WHERE ID = '" + docid + "'");
        query.executeUpdate();

    }

    //helper functions
    public int tablesIDByName(String tableName) {
        Table table = tableLoader.getTableByTableName(tableName);
        return table.getId();
    }

    public String[] codingColumsByName(String tableName) {
        Table table = tableLoader.getTableByTableName(tableName);
        return table.getCodingColumns();
    }

    public String codeColumnByName(String tableName) {
        Table table = tableLoader.getTableByTableName(tableName);
        return table.getCodeColumn();
    }

    /**
     * Method to verify that a batch does not contain documents already coded by
     * a user.
     *
     * @param batchid The id of the batch to be verified
     * @param email The email of the user
     * @return true if the batch does not contain any documents coded by this
     * user
     */
    @Override
    @Transactional
    public List<String> verifyUser(int batchid, String email) {
        Session sess = sessionFactory.getCurrentSession();
        SQLQuery query = sess.createSQLQuery("select UserPolicyCode.documentID from BatchDocument "
                + "join UserPolicyCode on BatchDocument.DocumentID=UserPolicyCode.DocumentID "
                + "and BatchDocument.TablesID=UserPolicyCode.TablesID "
                + "where Email='" + email + "' and BatchDocument.BatchID=" + batchid + ";");
        return query.list();
    }
}
