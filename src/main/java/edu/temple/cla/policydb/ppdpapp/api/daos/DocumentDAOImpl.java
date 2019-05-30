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
package edu.temple.cla.policydb.ppdpapp.api.daos;

import edu.temple.cia.policydb.ppdpapp.util.BillsUtil;
import edu.temple.cla.policydb.ppdpapp.api.tables.Table;
import edu.temple.cla.policydb.ppdpapp.api.tables.TableLoader;
import edu.temple.cla.papolicy.wolfgang.resolveclusters.DisplayClustersInTable;
import edu.temple.cla.papolicy.wolfgang.resolveclusters.Util;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import static java.util.Collections.emptyMap;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import javax.persistence.Tuple;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
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
    public List<Map<String, Object>> findDocuments(String tableName) {
        Session sess = sessionFactory.getCurrentSession();
        Integer tableID = tablesIDByName(tableName);
        Map<String, Integer> statMap = getStatMap(sess, tableID);
        NativeQuery<Tuple> query
                = sess.createNativeQuery("select * from " + tableName
                        + " order by ID desc", Tuple.class);
        try {
            List<Map<String, Object>> queryList = applyStatusToQueryResult(query, statMap);
            return queryList;
        } catch (Exception ex) {
            throw new RuntimeException("Error in query " + query, ex);
        }
    }

    @Override
    @Transactional
    public List<Map<String, Object>> findDocumentsPage(String docType, int page) {
        Session sess = sessionFactory.getCurrentSession();
        Integer tableID = tablesIDByName(docType);
        int startRow = page * 25;
        Map<String, Integer> statMap = getStatMap(sess, tableID);
        NativeQuery<Tuple> query
                = sess.createNativeQuery("select * from " + docType
                        + " order by ID desc"
                        + " LIMIT " + startRow + ", 25", Tuple.class);
        try {
            List<Map<String, Object>> queryList = applyStatusToQueryResult(query, statMap);
            return queryList;
        } catch (Exception ex) {
            throw new RuntimeException("Error in query " + query, ex);
        }
    }

    private List<Map<String, Object>>
            applyStatusToQueryResult(NativeQuery<Tuple> query, Map<String, Integer> statMap) {
        List<Map<String, Object>> queryList = query.stream()
                .map(MyTupleToEntityMapTransformer.INSTANCE).collect(Collectors.toList());
        queryList.forEach(entry -> {
            String id = (String) entry.get("ID");
            Integer stat = statMap.getOrDefault(id, 0);
            entry.put("stat", stat);
        });
        return queryList;
    }

    private Map<String, Integer> getStatMap(Session sess, int tableID) {
        Table table = tableLoader.getTableById(tableID);
        int maxNumberOfCodes = table.getNumCodesRequired();
        NativeQuery<Tuple> statusQuery = sess.createNativeQuery("select DocumentID, "
                + "count(DocumentID) as stat from UserPolicyCode where "
                + "TablesID=" + tableID + " group by DocumentID", Tuple.class);
        Map<String, Integer> statMap = new HashMap<>();
        statusQuery.stream().forEach(tuple -> {
            String id = (String) tuple.get("DocumentID");
            int stat = ((BigInteger) tuple.get("stat")).intValue();
            if (stat > 0 && stat < maxNumberOfCodes) {
                stat = 1;
            } else if (stat == maxNumberOfCodes) {
                stat = 2;
            }
            statMap.put(id, stat);
        });
        // Check for cluster (applies to Bills_Data only)
        if (table.getDocumentName().equals("bills")) {
            NativeQuery<Tuple> clusterQuery = 
                    sess.createNativeQuery("Select ID, ClusterId from "
                            + "Bills_Data where not isNull(ClusterId)", Tuple.class);
            clusterQuery.stream().forEach(tuple -> {
                String id = (String) tuple.get("ID");
                statMap.put(id, -1);
            });
        }
        // Check for CAP Code Review
        String capOKQueryTemplate = "Select ID, CAPOk from %s where (not isNull(%s)) and (isNull(CAPOk) or not CAPOk)";
        String capOKQuery = String.format(capOKQueryTemplate, table.getTableName(), table.getCodeColumn());
        NativeQuery<Tuple> capCodeQuery = sess.createNativeQuery(capOKQuery, Tuple.class);
        capCodeQuery.stream().forEach(tuple -> {
            String id = tuple.get("ID").toString();
            statMap.put(id, -2);
        });
        return statMap;
    }

    @Override
    @Transactional
    public List<Map<String, Object>>
            findDocumentsNoBatch(String tableName, int assignmentType, int batch_id) {
        Session sess = sessionFactory.getCurrentSession();
        Table table = tableLoader.getTableByTableName(tableName);
        int tableID = table.getId();
        String codeColumn = table.getCodeColumn();
        int desiredStat = -1;
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
            case 5:
                desiredStat = -1;
                break;
            case 6:
                desiredStat = -2;
                break;
        }
        Map<String, Integer> statMap = getStatMap(sess, tableID);
        String selectQuery;
        if (desiredStat != -1 && desiredStat != -2) {
            selectQuery = "SELECT * FROM " + tableName + " ns "
                 + "WHERE isNull(ns." + codeColumn + ") AND ns.ID NOT IN "
                 + "(select DocumentID from UserPolicyCode where TablesID="
                 + tableID + " and Email in (SELECT Email from BatchUser where "
                 + "BatchID=" + batch_id + ")) AND ns.ID NOT IN "
                 + "(SELECT bd.DocumentID FROM BatchDocument bd "
                 + "JOIN Batches on bd.BatchID=Batches.BatchID WHERE "
                 + "AssignmentTypeID=" + assignmentType + " "
                 + "AND bd.TablesID = " + tableID + ") Order By ID Desc";
        } else if (desiredStat == -1) {
            selectQuery = "SELECT * FROM " + tableName + " ns WHERE NOT "
                + "ISNULL(ClusterId) AND ns.ID NOT IN "
                + "(SELECT bd.DocumentID FROM BatchDocument bd "
                + "JOIN Batches on bd.BatchID=Batches.BatchID WHERE "
                + "AssignmentTypeID=" + assignmentType + " "
                + "AND bd.TablesID = " + tableID + ") Order By ID Desc";
        } else if (desiredStat == -2) {
            selectQuery = "SELECT * FROM " + tableName + " ns WHERE "
                + "(isNull(CAPOk) OR not CAPOk) AND ns.ID NOT IN "
                + "(SELECT bd.DocumentID FROM BatchDocument bd "
                + "JOIN Batches on bd.BatchID=Batches.BatchID WHERE "
                + "AssignmentTypeID=" + assignmentType + " "
                + "AND bd.TablesID = " + tableID + ") Order By ID Desc";            
        } else {
            throw new RuntimeException("Unrecognized desiredStat " + desiredStat);
        }
        NativeQuery<Tuple> query = sess.createNativeQuery(selectQuery, Tuple.class);
        try {
            List<Map<String, Object>> queryList = applyStatusToQueryResult(query, statMap);
            if (desiredStat == -1 || desiredStat == -2) {
                return queryList;
            }
            List<Map<String, Object>> filteredQueryList = new ArrayList<>();
            for (Map<String, Object> entry : queryList) {
                if (entry.get("stat").equals(desiredStat)) {
                    filteredQueryList.add(entry);
                }
            }
            return filteredQueryList;
        } catch (Exception ex) {
            String sqlQuery = query.getQueryString();
            throw new RuntimeException("Error in query " + sqlQuery, ex);
        }
    }
            
    @Override
    @Transactional
    public String getDocumentCount(String tableName) {
        Session sess = sessionFactory.getCurrentSession();
        NativeQuery<Tuple> query
                = sess.createNativeQuery("select count(ID) as count from " + tableName,
                        Tuple.class);
        try {
            return query.uniqueResult().get("count").toString();
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
        NativeQuery<Tuple> query
                = sess.createNativeQuery("SELECT * FROM " + tableName
                        + " WHERE ID = '" + id + "'", Tuple.class);
        return query.stream()
                .map(MyTupleToEntityMapTransformer.INSTANCE)
                .findFirst().get();
    }

    @Transactional
    private Object findHearing(String id) {
        Session sess = sessionFactory.getCurrentSession();
        NativeQuery<Tuple> query
                = sess.createNativeQuery("SELECT * FROM Transcript WHERE ID = '"
                        + id + "'", Tuple.class);
        Map<String, Object> queryResult
                = query.stream()
                        .map(MyTupleToEntityMapTransformer.INSTANCE)
                        .findFirst()
                        .get();
        NativeQuery<Tuple> getCommittees
                = sess.createNativeQuery("select AlternateName from "
                        + "Transcript_Committee join CommitteeAliases on "
                        + "committeeId=ID where transcriptId= '" + id + "'", Tuple.class);
        List<String> committeesList
                = getCommittees.stream()
                        .map(tuple -> (String) tuple.get("AlternateName"))
                        .collect(Collectors.toList());
        String committees = formatCommitteesList(committeesList);
        queryResult.put("Committees", committees);
        NativeQuery<Tuple> getBills
                = sess.createNativeQuery("SELECT BillID from Transcript_BillID "
                        + "where TranscriptID = '" + id + "'", Tuple.class);
        List<String> billsList = getBills.stream()
                .map(tuple -> (String) tuple.get("BillID"))
                .collect(Collectors.toList());
        String bills = BillsUtil.createBillLinks(billsList);
        queryResult.put("Bills", bills);
        NativeQuery<Tuple> getWitnesses
                = sess.createNativeQuery("select testimonyURL from Witness "
                        + "where TranscriptID='" + id + "'", Tuple.class);
        List<String> witnessList = getWitnesses.stream()
                .map(tuple -> (String) tuple.get("testimonyURL"))
                .collect(Collectors.toList());
        String witnesses = formatWitnesses(witnessList);
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
    public List<Map<String, Object>> findDocumentCodes(String tableName, String id) {
        Table table = tableLoader.getTableByTableName(tableName);
        Session sess = sessionFactory.getCurrentSession();
        NativeQuery<Tuple> query
                = sess.createNativeQuery("SELECT * FROM UserPolicyCode WHERE "
                        + "documentID = '" + id + "' and TablesID = '"
                        + table.getId() + "'", Tuple.class);
        return query.stream()
                .map(MyTupleToEntityMapTransformer.INSTANCE)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Object findDocumentCode(String tableName, String id, String email) {
        Table table = tableLoader.getTableByTableName(tableName);
        Session sess = sessionFactory.getCurrentSession();
        NativeQuery<Tuple> query
                = sess.createNativeQuery("SELECT * FROM UserPolicyCode WHERE "
                        + "documentID = '" + id + "' and TablesID = '"
                        + table.getId() + "' and Email = '" + email + "'", Tuple.class);
        return query.stream()
                .map(MyTupleToEntityMapTransformer.INSTANCE)
                .findFirst().orElseGet(()->{
                    Map<String, Object> m = new HashMap<>();
//                    m.put("Code", "");
                    return m;
                });
    }

    @Override
    @Transactional
    public void addDocumentCode(String email, String tableName, String docid, String batchid, int codeid) {
        Table table = tableLoader.getTableByTableName(tableName);
        int tableID = table.getId();
        int maxNumOfCodes = table.getNumCodesRequired();
        Session sess = sessionFactory.getCurrentSession();
        NativeQuery<Tuple> query
                = sess.createNativeQuery("SELECT Code FROM UserPolicyCode WHERE "
                        + "DocumentID = '" + docid + "' AND TablesID = "
                        + tableID + " AND Email <> '" + email + "' and not isNull(Code)",
                        Tuple.class);
        List<Integer> userPolicyCodes = query.stream()
                .map(tuple -> (Integer) tuple.get("Code"))
                .collect(Collectors.toList());
        Integer matches = 1;
        if (userPolicyCodes.size() >= maxNumOfCodes) { // This is a tiebreak
            insertUserPolicyCode(email, tableName, docid, batchid, codeid);
            updateDocumentFinalCode(tableName, docid, batchid, codeid);
        } else if (userPolicyCodes.size() < maxNumOfCodes) {
            for (int i = 0; i < userPolicyCodes.size(); i++) {
                if (userPolicyCodes.get(i) == codeid) {
                    matches++;
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
    public void updateDocumentCode(String email, String tableName, String docid, String batchid, int codeid) {
        updateDocumentFinalCode(tableName, docid, batchid, codeid);
    }

    @Override
    @Transactional
    public void updateCAPCode(String email, String tableName, String docid, String batchid, int codeid) {
        Session sess = sessionFactory.getCurrentSession();
        String queryTemplate = "UPDATE %s SET CAPCode=%d, CAPOk=1 WHERE ID='%s'";
        String query = String.format(queryTemplate, tableName, codeid, docid);
        sess.createNativeQuery(query).executeUpdate();
    }
    
    @Override
    @Transactional
    public List<Map<String, Object>> findDocumentsNoCodes(String tableName, int batchid, String email) {
        Session sess = sessionFactory.getCurrentSession();
        Table table = tableLoader.getTableByTableName(tableName);
        String codingColumnsList = table.getCodingColumnsList();
        NativeQuery<Tuple> query = sess.createNativeQuery("select " + codingColumnsList
                + ", UserPolicyCode.Code as UserCode from BatchDocument "
                + "left join UserPolicyCode on "
                + "BatchDocument.BatchID=UserPolicyCode.BatchID "
                + "and BatchDocument.DocumentID=UserPolicyCode.DocumentID "
                + "join " + tableName + " on "
                + "BatchDocument.DocumentID=" + tableName + ".ID "
                + "where (BatchDocument.BatchId=" + batchid + ") "
                + "and (Email='" + email + "' or isNull(Email))", Tuple.class);

        return query.stream()
                .map(MyTupleToEntityMapTransformer.INSTANCE)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Map<String, Object>> findDocumentsTieBreak(String tableName, int batchid, String email) {
        Session sess = sessionFactory.getCurrentSession();
        Table table = tableLoader.getTableByTableName(tableName);
        int tableId = table.getId();
        String codingColumnsList = table.getCodingColumnsList();
        String codeColumn = codeColumnByName(tableName);
        NativeQuery<Tuple> query
                = sess.createNativeQuery("select " + codingColumnsList + ", "
                        + codeColumn + " as UserCode from BatchDocument "
                        + "join " + tableName + " on "
                        + "BatchDocument.DocumentID=" + tableName + ".ID "
                        + "where (BatchDocument.BatchId=" + batchid + ") "
                        + "order by BatchDocument.DocumentID", Tuple.class);
        List<Map<String, Object>> documentsList
                = query.stream()
                        .map(MyTupleToEntityMapTransformer.INSTANCE)
                        .collect(Collectors.toList());
        NativeQuery<Tuple> query2 = sess.createNativeQuery("select BatchDocument.DocumentID, "
                + "Code from BatchDocument join UserPolicyCode on "
                + "BatchDocument.TablesID=UserPolicyCode.TablesID and "
                + "BatchDocument.DocumentID=UserPolicyCode.DocumentID where "
                + "BatchDocument.TablesID=" + tableId + " and "
                + "BatchDocument.BatchId=" + batchid + " and "
                + "UserPolicyCode.Email<>'" + email + "'", Tuple.class);
        List<Map<String, Object>> codeList
                = query2.stream()
                        .map(MyTupleToEntityMapTransformer.INSTANCE)
                        .collect(Collectors.toList());
        Map<String, Map<String, Object>> resultMap = new HashMap<>();
        documentsList.forEach(row -> {
            resultMap.put((String) row.get("ID"), row);
        });
        codeList.forEach(codeRow -> {
            Map<String, Object> row = resultMap.getOrDefault(codeRow.get("DocumentID"), emptyMap());
            @SuppressWarnings("unchecked")
            List<Integer> codes = (List<Integer>) row.getOrDefault("Codes", new ArrayList<>());
            Integer code = (Integer) codeRow.get("Code");
            if (code != null) {
                codes.add(code);
            }
            row.put("Codes", (Object) codes);
        });
        List<Map<String, Object>> result = new ArrayList<>();
        resultMap.forEach((k, v) -> {
            @SuppressWarnings("unchecked")
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
    public List<Map<String, Object>> findDocumentsClusters(String tableName, 
            int batchid, String email) {
        Session sess = sessionFactory.getCurrentSession();
        Table table = tableLoader.getTableByTableName(tableName);
        String textColumn = table.getTextColumn();
        String linkColumn = table.getLinkColumn();
        String codeColumn = table.getCodeColumn();
        String selectQuery = "select " + tableName + ".ID, " + textColumn 
                + ", " + linkColumn + ", " + codeColumn + ", ClusterId from "
                + tableName + " where ClusterId in (select ClusterId from " 
                + tableName + " join BatchDocument on ID=DocumentID and BatchID="
                + batchid + ") order by ClusterId, ID desc";
        try {
        NativeQuery<Tuple> query = sess.createNativeQuery(selectQuery, Tuple.class);
        DisplayClustersInTable formatTable = new DisplayClustersInTable();
        List<Map<String, Object>> result = query.stream()
             .map(MyTupleToEntityMapTransformer.INSTANCE)
             .map(formatTable::processClusters)
                .collect(Collectors.toList());
        return result;
        } catch (Throwable ex) {
            throw new RuntimeException("Error in query " + selectQuery, ex);
        }
    }

    @Override
    @Transactional
    public List<Map<String, Object>> findDocumentsCAPReview(String tableName, 
            int batchid, String email) {
        Session sess = sessionFactory.getCurrentSession();
        Table table = tableLoader.getTableByTableName(tableName);
        String textColumn = table.getTextColumn();
        String linkColumn = table.getLinkColumn();
        String codeColumn = table.getCodeColumn();
        String selectQueryTemplate = "select ID, %s as Text, %s as Link, %s as Code, CAPCode, CAPOk from "
                + "(select * from BatchDocument where BatchID=%d) as docs left "
                + "join %s on DocumentID=ID";
        String selectQueryNoLinkTemplate = "select ID, %s as Text, %s as Code, CAPCode, CAPOk from "
                + "(select * from BatchDocument where BatchID=%d) as docs left "
                + "join %s on DocumentID=ID";
        String selectQuery;
        if (linkColumn != null) {
            selectQuery = String.format(selectQueryTemplate, textColumn, linkColumn, codeColumn, batchid, tableName);
        } else {
            selectQuery = String.format(selectQueryNoLinkTemplate,textColumn,codeColumn, batchid, tableName);
        }
        try {
        NativeQuery<Tuple> query = sess.createNativeQuery(selectQuery, Tuple.class);
        List<Map<String, Object>> documentsList
                = query.stream()
                        .map(MyTupleToEntityMapTransformer.INSTANCE)
                        .map(DocumentDAOImpl::fixHyperlink)
                        .collect(Collectors.toList());
        return documentsList;
        } catch (Throwable ex) {
            throw new RuntimeException("Error in query " + selectQuery, ex);
        }
    }
    
    private static Map<String, Object> fixHyperlink(Map<String, Object> row) {
        String id = (String) row.get("ID");
        String link = Util.reformatHyperlink((String) row.get("Link"));
        String idLink = String.format("<a href=\"%s\">%s</a>", link, id);
        row.put("IDLink", idLink);
        return row;
    }

    @Override
    @Transactional
    public void updateDocument(String tableName, Map<String, Object> docObj) {
        Session sess = sessionFactory.getCurrentSession();
        String[] keyArray = docObj.keySet().toArray(new String[0]);
        Object[] valueArray = docObj.values().toArray();
        Table table = tableLoader.getTableByTableName(tableName);
        Set<String> columns = table.getColumns();
        Object docID = "";
        int mapSize = docObj.size();
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
                if (keyArray[i].equals("ID")) {
                    docID = valueArray[i];
                } else {
                    sql = sql + sqlAppend;
                }
            }
        }
        sql = sql.substring(0, sql.length() - 2) + " WHERE ID = '" + docID + "'";
        try {
            NativeQuery query = sess.createNativeQuery(sql);
            query.executeUpdate();
        } catch (Exception ex) {
            throw new RuntimeException("Error in SQL query " + sql, ex);
        }
    }

    @Override
    @Transactional
    public int insertDocument(String tableName, Map<String, Object> docObj) {
        Session sess = sessionFactory.getCurrentSession();
        StringJoiner columns = new StringJoiner(", ", "(", ")");
        StringJoiner values = new StringJoiner(", ", "(", ")");
        NativeQuery<Tuple> findLastID
                = sess.createNativeQuery("SELECT max(ID) as maxID from " + tableName, Tuple.class);
        Integer lastID = findLastID.uniqueResult().get("maxID", Integer.class);
        int docID = lastID + 1;
        columns.add("ID");
        values.add(String.valueOf(docID));
        docObj.forEach((k, v) -> {
            if (!k.equals("ID")) {
                columns.add(k);
                if (v != null) {
                    values.add("'" + v.toString().replace("'", "''") + "'");
                } else {
                    values.add("null");
                }
            }
        });
        String sql = "INSERT INTO " + tableName + " " + columns + " VALUES " + values + ";";
        NativeQuery query = sess.createNativeQuery(sql);
        try {
            query.executeUpdate();
        } catch (Exception ex) {
            throw new RuntimeException("Error executing Hibernate query "
                    + query.getQueryString(), ex);
        }
        return docID;
    }

    public void insertUserPolicyCode(String email, String tableName, String docid, String newBatchId, int codeid) {
        Session sess = sessionFactory.getCurrentSession();
        Integer tableID = tablesIDByName(tableName);
        int newBatchIdInt = 0;
        try {
            newBatchIdInt = Integer.parseInt(newBatchId);
        } catch (NumberFormatException ex) {
            newBatchId = "NULL";
        }
        String criteria = "(Email='" + email + "' and DocumentID='" + docid
                + "' and TablesID=" + tableID + ")";
        NativeQuery<Tuple> findExistingCodeForThisUser
                = sess.createNativeQuery("SELECT BatchID FROM UserPolicyCode WHERE "
                        + criteria, Tuple.class);
        List<Integer> batchIds = findExistingCodeForThisUser.stream()
                .map(tuple -> tuple.get("BatchID", Integer.class))
                .collect(Collectors.toList());
        if (batchIds.isEmpty()) { // Insert new entry into table
            NativeQuery<?> query
                    = sess.createNativeQuery("INSERT INTO UserPolicyCode "
                            + "(Email, DocumentID ,TablesID, BatchID, Code)"
                            + " VALUES ('" + email + "','" + docid + "',"
                            + tableID + "," + newBatchId + "," + codeid + ");");
            try {
                if (query.executeUpdate() != 1) {
                    throw new RuntimeException("Insert to UserPolicyCode Failed " + query);
                }
            } catch (Exception e) {
                throw new RuntimeException("Insert into UserPolicyCode Failed " + query, e);
            }
        } else {
            for (Integer batchId : batchIds) {
                if ((batchId == null && (newBatchId == null || newBatchId.equals("NULL")))
                        || batchId == newBatchIdInt) {
                    NativeQuery<?> query;
                    if (newBatchId == null || newBatchId.equals("NULL")) {
                        query = sess.createNativeQuery("UPDATE UserPolicyCode SET Code = "
                                + codeid + " WHERE (Email = '" + email
                                + "' and DocumentID = '" + docid
                                + "' and TablesID = " + tableID
                                + " AND isNull(BatchID))");
                    } else {
                        query = sess.createNativeQuery("UPDATE UserPolicyCode SET Code = "
                                + codeid + " WHERE (Email = '" + email
                                + "' and DocumentID = '" + docid
                                + "' and TablesID = " + tableID
                                + " AND BatchID = " + newBatchId + ")");
                    }
                    if (query.executeUpdate() != 1) {
                        NativeQuery<Tuple> query2
                                = sess.createNativeQuery("SELECT BatchID from UserPolicyCode"
                                        + " WHERE (Email = '" + email + "' and DocumentID = '"
                                        + docid + "' and TablesID = " + tableID, Tuple.class);
                        List<Integer> resultList = query2.stream()
                                .map(tuple -> tuple.get("BatchID", Integer.class))
                                .collect(Collectors.toList());
                        if (!resultList.isEmpty()) {
                            throw new RuntimeException(email + " attempt to add a code "
                                    + "to batch " + newBatchId + " but aleady coded in batch "
                                    + resultList.get(0));
                        } else {
                            throw new RuntimeException("Update to UserPolicyCode Failed " + query);
                        }
                    }
                }
            }
        }
    }

    public void updateDocumentFinalCode(String tableName, String docid, String newBatchId, int codeid) {
        Session sess = sessionFactory.getCurrentSession();
        Integer tableID = tablesIDByName(tableName);
        String codeName = codeColumnByName(tableName);
        //sets the document as complete.
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        try {
            int batchIdInt = Integer.parseInt(newBatchId);
        } catch (NumberFormatException ex) {
            newBatchId = "NULL";
        }
        NativeQuery query;
        if ("NULL".equals(newBatchId)) {
            query = sess.createNativeQuery("UPDATE BatchDocument SET DateCompleted = '"
                    + dateString + "' WHERE DocumentID = '" + docid
                    + "' AND TablesID = " + tableID + " AND isNull(BatchID)");
        } else {
            query = sess.createNativeQuery("UPDATE BatchDocument SET DateCompleted = '"
                    + dateString + "' WHERE DocumentID = '" + docid
                    + "' AND TablesID = " + tableID + " AND BatchID = " + newBatchId);
        }
        query.executeUpdate();
        String updateCodeQuery;
        if (tableName.equals("Bills_Data")) { //Cluster only applies to bills.
            updateCodeQuery = "UPDATE Bills_Data SET Code = " + codeid + ", ClusterId=NULL WHERE ID = '" + docid + "'";
        } else {
            updateCodeQuery = "UPDATE " + tableName + " SET " + codeName
                + " = " + codeid + " WHERE ID = '" + docid + "'";
        }
        query = sess.createNativeQuery(updateCodeQuery);
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
        NativeQuery<Tuple> query
                = sess.createNativeQuery("select UserPolicyCode.documentID as id from BatchDocument "
                        + "join UserPolicyCode on BatchDocument.DocumentID=UserPolicyCode.DocumentID "
                        + "and BatchDocument.TablesID=UserPolicyCode.TablesID "
                        + "where Email='" + email + "' and BatchDocument.BatchID=" + batchid, Tuple.class);
        return query.stream()
                .map(tuple -> tuple.get("id", String.class))
                .collect(Collectors.toList());
    }
}
