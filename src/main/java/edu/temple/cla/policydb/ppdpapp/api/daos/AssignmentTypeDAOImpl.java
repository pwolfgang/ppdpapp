package edu.temple.cla.policydb.ppdpapp.api.daos;

import edu.temple.cla.policydb.ppdpapp.api.models.AssignmentType;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class AssignmentTypeDAOImpl implements AssignmentTypeDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public AssignmentTypeDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public List<AssignmentType> list() {
        List<AssignmentType> listNewsClipTypes = (List<AssignmentType>) sessionFactory
                .getCurrentSession()
                .createCriteria(AssignmentType.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
        return listNewsClipTypes;
    }
}
