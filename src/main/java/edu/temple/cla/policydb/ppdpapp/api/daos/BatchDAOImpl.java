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
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class BatchDAOImpl implements BatchDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public BatchDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public List<Batch> list() {
        return (List<Batch>) sessionFactory.getCurrentSession().createCriteria(Batch.class).list();
    }

    @Override
    @Transactional
    public Batch find(int id) {
        return (Batch) sessionFactory.getCurrentSession().get(Batch.class, id);
    }

    @Override
    @Transactional
    public Batch save(Batch batchObj) {
        sessionFactory.getCurrentSession().saveOrUpdate(batchObj);
        return batchObj;
    }

    @Override
    public void create(Batch batchObj) {
        Session sess = sessionFactory.getCurrentSession();
        SQLQuery query = sess.createSQLQuery("INSERT INTO Batches "
                + "(FileID, TablesID, AssignmentTypeID, AssignmentDescription, "
                + "Name, DateAdded, Creator, DateDue)"
                + "Values('" + batchObj.getFileID() + "','" + batchObj.getTablesID() 
                + "','" + batchObj.getAssignmentTypeID() + "," 
                + batchObj.getAssignmentDescription() + "," + batchObj.getName() 
                + "','" + batchObj.getDateAdded() + "','" + batchObj.getCreator() 
                + "','" + batchObj.getDateDue() + "')");
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void delete(int id) {
        Session sess = sessionFactory.getCurrentSession();
        Batch batchObj = (Batch) sess.get(Batch.class, id);

        try {
            sess.delete(batchObj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // manually delete the associated docs from BatchDocuments since we manually added them.
        try {
            SQLQuery query = sess.createSQLQuery("DELETE FROM BatchDocument WHERE BatchID = " + id);
            query.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public List<User> findUsers(int id) {
        Batch batchObj = (Batch) sessionFactory.getCurrentSession().get(Batch.class, id);
        return batchObj.getUsers();
    }

    @Override
    @Transactional
    public List<Object> findDocuments(int id) {
        // find batch.
        // is it file_id?
        // find the document types this batch consists of.
        // return mapping of it.
        SQLQuery query = null;
        Session sess = sessionFactory.getCurrentSession();

        Batch batchObj = (Batch) sess.get(Batch.class, id);
        query = sess.createSQLQuery("SELECT TableName FROM Tables WHERE ID = (SELECT TablesID FROM Batches WHERE BatchID = " + id + ")");
        String docType = query.uniqueResult().toString();

        query = sess.createSQLQuery("SELECT * FROM " + docType + " AS nc "
                + "LEFT JOIN BatchDocument AS bd ON nc.ID = bd.DocumentID "
                + "WHERE bd.BatchID = " + id);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return (List<Object>) query.list();
    }

    @Override
    @Transactional
    public void addDocument(int batchID, String docID) {
        Session sess = sessionFactory.getCurrentSession();
        SQLQuery query = sess.createSQLQuery("SELECT ID FROM Tables WHERE ID = (SELECT TablesID FROM Batches WHERE BatchID = " + batchID + ")");
        String tableID = query.uniqueResult().toString();
        query = sess.createSQLQuery("INSERT INTO BatchDocument (DocumentID, TablesID ,BatchID)"
                + "VALUES ('" + docID + "'," + tableID + "," + batchID + ");");
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void deleteDocument(int batchID, String docID) {
        Session sess = sessionFactory.getCurrentSession();

        SQLQuery query = sess.createSQLQuery("SELECT ID FROM Tables WHERE ID = (SELECT TablesID FROM Batches WHERE BatchID = " + batchID + ")");
        String tableID = query.uniqueResult().toString();

        // manually delete the associated docs from BatchDocuments since we manually added them.
        try {
            query = sess.createSQLQuery("DELETE FROM BatchDocument WHERE BatchID = " 
                    + batchID + " AND TablesID = " + tableID + " AND DocumentID = '" + docID + "'"); //
            query.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void deleteUser(int batchID, String email) {
        Session sess = sessionFactory.getCurrentSession();
        try {
            SQLQuery query = sess.createSQLQuery("DELETE FROM BatchUser WHERE BatchID = " 
                    + batchID + " AND Email = '" + email + "'");
            query.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
