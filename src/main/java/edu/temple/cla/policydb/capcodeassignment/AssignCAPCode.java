package edu.temple.cla.policydb.capcodeassignment;

import edu.temple.cla.policydb.ppdpapp.api.tables.Table;
import edu.temple.cla.policydb.queryBuilder.Comparison;
import edu.temple.cla.policydb.queryBuilder.FreeTextParser;
import edu.temple.cla.policydb.queryBuilder.QueryBuilder;
import edu.temple.cla.policydb.queryBuilder.SetClause;
import javax.sql.DataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AssignCAPCode {
    
    private SessionFactory sessionFactory;
    private DataSource datasource;
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setDataSource(DataSource datasource) {
        this.datasource = datasource;
    }
    
    public ResponseEntity<?> doAssignment(Table table) {
        try (Session sess = sessionFactory.openSession()) {
            Transaction tx = sess.beginTransaction();
            // Initially set CAPCode to Code
            String setDefaultTemplate = "update %s set CAPCode=%s CAPOk=1";
            String setDefaultQuery = String.format(setDefaultTemplate, 
                    table.getTableName(), table.getCodeColumn());
            sess.createNativeQuery(setDefaultQuery)
                    .executeUpdate();
            for (Criteria criteria: Criteria.getCriteria()) {
                String query = createQuery(criteria, table);
                sess.createNativeQuery(query)
                    .executeUpdate();
            }
            tx.commit();
        }
        return new ResponseEntity<>("Updated " + table.getTableName(), HttpStatus.OK);
    }
    
    static String createQuery(Criteria criteria, Table table) {     
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.setTable(table.getTableName());
        queryBuilder.addSetClause(new SetClause("CAPCode", criteria.getNewCAPCode()));
        queryBuilder.addSetClause(new SetClause("CAPOk", 0));
        queryBuilder.addToSelectCriteria(new Comparison(table.getCodeColumn(), 
                "=", Integer.toString(criteria.getCurrentCode())));
        queryBuilder.addToSelectCriteria(FreeTextParser.parse(table.getTextColumn(), 
                criteria.getKeyWords()));
        String filter = criteria.getFilter();
        if (filter != null && !filter.isEmpty() ) {
            queryBuilder.addFilter(new Comparison(filter, "<>", "0"));
        }
        return queryBuilder.buildUpdate();
    }

}
