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

import edu.temple.cla.policydb.ppdpapp.api.models.File;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.hibernate.query.NativeQuery;

public class FileDAOImpl implements FileDAO {

    @Autowired
    private final SessionFactory sessionFactory;

    public FileDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public List<File> list() {
        return (List<File>) sessionFactory
                .getCurrentSession()
                .createCriteria(File.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Override
    @Transactional
    public File find(int id) {
        return sessionFactory.getCurrentSession().get(File.class, id);
    }

    @Override
    @Transactional
    public File save(File fileObj) {
        sessionFactory.getCurrentSession().saveOrUpdate(fileObj);
        return fileObj;
    }

    @Override
    @Transactional
    public Object findBatchByFileID(int fileid) {
        Session sess = sessionFactory.getCurrentSession();
        NativeQuery query = sess.createNativeQuery("SELECT * FROM Batches WHERE FileID = " + fileid);
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.uniqueResult();
    }

    @Override
    @Transactional
    public int create(File fileObj) {
        Session sess = sessionFactory.getCurrentSession();
       NativeQuery query = sess.createNativeQuery("INSERT INTO Files (Name, FileURL, DateAdded, Creator)"
                + "Values('" + fileObj.getName() + "','" + fileObj.getFileURL() + "'," + fileObj.getDateAdded() + "'," + fileObj.getCreator() + "'");
        query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return query.executeUpdate();
    }
}
