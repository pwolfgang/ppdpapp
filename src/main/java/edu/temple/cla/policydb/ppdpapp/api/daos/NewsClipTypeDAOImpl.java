package edu.temple.cla.policydb.ppdpapp.api.daos;

import edu.temple.cla.policydb.ppdpapp.api.models.NewsClipType;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Paul
 */
public class NewsClipTypeDAOImpl implements NewsClipTypeDAO {
    
    @Autowired
    private SessionFactory sessionFactory;

    public NewsClipTypeDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public List<NewsClipType> list() {
        List<NewsClipType> listNewsClipTypes = (List<NewsClipType>) sessionFactory
                .getCurrentSession()
                .createCriteria(NewsClipType.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
        return listNewsClipTypes;
    }

}
