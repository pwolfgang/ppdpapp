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


import edu.temple.cla.policydb.ppdpapp.api.models.Code;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class CodeDAOImpl implements CodeDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public CodeDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public List<Object> list(String tableName) {
        Session sess = sessionFactory.getCurrentSession();
        SQLQuery query = sess.createSQLQuery("SELECT MajorOnly FROM Tables WHERE TableName= '" + tableName + "'");
        int majorOnly = (Integer) query.uniqueResult();
        if (majorOnly == 1) {
            query = sess.createSQLQuery("SELECT * FROM MajorCode");
            query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
            return query.list();
        } else {
            return (List<Object>) sess.createCriteria(Code.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        }
    }

    @Override
    @Transactional
    public List<Object> listMajorMinor(String majorOrMinor) {
        Session sess = sessionFactory.getCurrentSession();
        SQLQuery query = sess.createSQLQuery("SELECT * FROM " + majorOrMinor);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }

    @Override
    @Transactional
    public Object find(String tableName, int id) {
        Session sess = sessionFactory.getCurrentSession();
        SQLQuery query = sess.createSQLQuery("SELECT MajorOnly FROM Tables WHERE TableName= '" + tableName + "'");
        int majorOnly = (Integer) query.uniqueResult();
        if (majorOnly == 1) {
            query = sess.createSQLQuery("SELECT * FROM MajorCode WHERE Code = " + id);
            query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
            return query.uniqueResult();
        } else {
            return sess.get(Code.class, id);
        }
    }

    @Override
    @Transactional
    public List<Object> findSearch(String tableName, String search) {
        Session sess = sessionFactory.getCurrentSession();
        SQLQuery query = sess.createSQLQuery("SELECT MajorOnly FROM Tables WHERE TableName= '" + tableName + "'");
        Integer majorOnly = (Integer) query.uniqueResult();
        String codeTable = null;
        if (majorOnly == 1) {
            codeTable = "MajorCode";
        } else {
            codeTable = "Code";
        }
        query = sess.createSQLQuery("SELECT * FROM " + codeTable + " WHERE Code LIKE '%" + search + "%' OR Description LIKE '%" + search + "%'");
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.list();
    }
}
