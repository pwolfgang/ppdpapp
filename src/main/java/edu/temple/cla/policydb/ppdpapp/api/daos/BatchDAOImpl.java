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


import edu.temple.cla.policydb.ppdpapp.api.models.Batch;
import edu.temple.cla.policydb.ppdpapp.api.models.User;
import edu.temple.cla.policydb.ppdpapp.api.tables.TableLoader;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class BatchDAOImpl implements BatchDAO {

    @Autowired
    private final SessionFactory sessionFactory;
    
    @Autowired
    private TableLoader tableLoader;

    public BatchDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public List<Batch> list() {
        Session session = sessionFactory.getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Batch> criteria = builder.createQuery(Batch.class);
        criteria.from(Batch.class);
        return session.createQuery(criteria).getResultList();
    }

    @Override
    @Transactional
    public Batch find(int id) {
        return sessionFactory.getCurrentSession().get(Batch.class, id);
    }

    @Override
    @Transactional
    public Batch save(Batch batchObj) {
        sessionFactory.getCurrentSession().saveOrUpdate(batchObj);
        return batchObj;
    }

    @Override
    @Transactional
    public void delete(int id) {
        Session sess = sessionFactory.getCurrentSession();
        Batch batchObj = sess.get(Batch.class, id);

        try {
            sess.delete(batchObj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // manually delete the associated docs from BatchDocuments since we manually added them.
        try {
            NativeQuery<?> query = sess.createNativeQuery("DELETE FROM BatchDocument WHERE BatchID = " + id);
            query.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public List<User> findUsers(int id) {
        Batch batchObj = sessionFactory.getCurrentSession().get(Batch.class, id);
        return batchObj.getUsers();
    }

    @Override
    @Transactional
    public List<Map<String, Object>> findDocuments(int id) {
        Session sess = sessionFactory.getCurrentSession();       
        Integer tableId = getTableIdFromBatch(sess, id);
        String tableName = tableLoader.getTableById(tableId).getTableName();

        NativeQuery<Tuple> query = sess.createNativeQuery("SELECT * FROM " 
                + tableName + " AS nc LEFT JOIN BatchDocument AS bd ON "
                        + "nc.ID = bd.DocumentID "
                + "WHERE bd.BatchID = " + id, Tuple.class);
        return query.stream()
                .map(MyTupleToEntityMapTransformer.INSTANCE)
                .collect(Collectors.toList());
    }

    public Integer getTableIdFromBatch(Session sess, int id) {
        NativeQuery<Tuple> tableIdQuery =
                sess.createNativeQuery("SELECT TablesID from Batches "
                        + "WHERE BatchID=" + id, Tuple.class);
        try { 
            Integer tableId = tableIdQuery.stream()
                .map(tuple -> tuple.get("TablesID", Integer.class))
                .findFirst().get();
            return tableId;
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Batch " + id + " not found");
        }
    }

    @Override
    @Transactional
    public void addDocument(int batchID, String docID) {
        Session sess = sessionFactory.getCurrentSession();
        Integer tableID = getTableIdFromBatch(sess, batchID);
        NativeQuery<?> query = sess.createNativeQuery("INSERT INTO BatchDocument "
                + "(DocumentID, TablesID ,BatchID)"
                + "VALUES ('" + docID + "'," + tableID + "," + batchID + ");");
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void deleteDocument(int batchID, String docID) {
        Session sess = sessionFactory.getCurrentSession();
        Integer tableID = getTableIdFromBatch(sess, batchID);
        try {
            NativeQuery<?> deleteQuery = 
                    sess.createNativeQuery("DELETE FROM BatchDocument WHERE BatchID = " 
                    + batchID + " AND TablesID = " + tableID + " AND DocumentID = '" + docID + "'"); 
            deleteQuery.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void deleteUser(int batchID, String email) {
        Session sess = sessionFactory.getCurrentSession();
        try {
            NativeQuery<?> query = 
                    sess.createNativeQuery("DELETE FROM BatchUser WHERE BatchID = " 
                    + batchID + " AND Email = '" + email + "'");
            query.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
