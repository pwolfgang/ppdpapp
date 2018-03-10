package edu.temple.cla.policydb.ppdpapp.api;

import edu.temple.cla.policydb.ppdpapp.api.daos.AssignmentTypeDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.AssignmentTypeDAOImpl;
import edu.temple.cla.policydb.ppdpapp.api.daos.BatchDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.BatchDAOImpl;
import edu.temple.cla.policydb.ppdpapp.api.daos.CodeDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.CodeDAOImpl;
import edu.temple.cla.policydb.ppdpapp.api.daos.DocumentDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.DocumentDAOImpl;
import edu.temple.cla.policydb.ppdpapp.api.daos.FileDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.FileDAOImpl;
import edu.temple.cla.policydb.ppdpapp.api.daos.FilterDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.FilterDAOImpl;
import edu.temple.cla.policydb.ppdpapp.api.daos.NewsClipTypeDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.NewsClipTypeDAOImpl;
import edu.temple.cla.policydb.ppdpapp.api.daos.NewspaperDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.NewspaperDAOImpl;
import edu.temple.cla.policydb.ppdpapp.api.daos.RoleDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.RoleDAOImpl;
import edu.temple.cla.policydb.ppdpapp.api.daos.TablesDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.TablesDAOImpl;
import edu.temple.cla.policydb.ppdpapp.api.daos.UserDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.UserDAOImpl;
import edu.temple.cla.policydb.ppdpapp.api.tables.TableLoader;
import edu.temple.cla.policydb.ppdpapp.ldap.LDAP;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
@ComponentScan
public class Application {

    @Bean(name="ldap")
    public LDAP getLdap() {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            String ldapURL = (String) envCtx.lookup("ldapURL");
            String ldapPrincipal = (String) envCtx.lookup("ldapPrinciapl");
            String ldapCredentials = (String) envCtx.lookup("ldapCredentials");
            Hashtable<String, String> ldappw = new Hashtable<>();
            ldappw.put(Context.PROVIDER_URL, ldapURL);
            ldappw.put(Context.SECURITY_PRINCIPAL, ldapPrincipal);
            ldappw.put(Context.SECURITY_CREDENTIALS, ldapCredentials);
            return new LDAP(ldappw);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Bean(destroyMethod="")
    public DataSource getDataSource() {
        try {            
            JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
            factory.setJndiName("java:comp/env/jdbc/PAPolicy_Copy");
            factory.afterPropertiesSet();
            BasicDataSource datasource = (BasicDataSource)factory.getObject();
            if (!datasource.isClosed()){
                return datasource;
            } else {
                BasicDataSource newDataSource = new BasicDataSource();
                newDataSource.setDriver(datasource.getDriver());
                newDataSource.setUrl(datasource.getUrl());
                newDataSource.setUsername(datasource.getUsername());
                newDataSource.setPassword(datasource.getPassword());
                return newDataSource;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Autowired
    @Bean(name = "sessionFactory")
    public SessionFactory getSessionFactory(DataSource dataSource) {
        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
        sessionBuilder.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        sessionBuilder.scanPackages("edu.temple.cla.policydb.ppdpapp.api.models");
        return sessionBuilder.buildSessionFactory();
    }

    @Autowired
    @Bean(name = "transactionManager")
    public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
        return transactionManager;
    }

    @Bean
    MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement(null, 10 * 1024 * 1024, 10 * 1024 * 1024, 0);
    }

    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver commonsMultipartResolver() {
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding("utf-8");
        commonsMultipartResolver.setMaxUploadSize(50000000);
        return commonsMultipartResolver;
    }

    @Autowired
    @Bean(name = "batchDao")
    public BatchDAO getBatchDao(SessionFactory sessionFactory) {
        return new BatchDAOImpl(sessionFactory);
    }

    @Autowired
    @Bean(name = "codeDao")
    public CodeDAO getCodeDao(SessionFactory sessionFactory) {
        return new CodeDAOImpl(sessionFactory);
    }

    @Autowired
    @Bean(name = "documentDao")
    public DocumentDAO getDocumentDao(SessionFactory sessionFactory) {
        return new DocumentDAOImpl(sessionFactory);
    }

    @Autowired
    @Bean(name = "fileDao")
    public FileDAO getFileDao(SessionFactory sessionFactory) {
        return new FileDAOImpl(sessionFactory);
    }

    @Autowired
    @Bean(name = "filterDao")
    public FilterDAO getFilterDao(SessionFactory sessionFactory) {
        return new FilterDAOImpl(sessionFactory);
    }

    @Autowired
    @Bean(name = "newspaperDao")
    public NewspaperDAO getNewspaperDao(SessionFactory sessionFactory) {
        return new NewspaperDAOImpl(sessionFactory);
    }

    @Autowired
    @Bean(name = "roleDao")
    public RoleDAO getRoleDao(SessionFactory sessionFactory) {
        return new RoleDAOImpl(sessionFactory);
    }

    @Autowired
    @Bean(name = "userDao")
    public UserDAO getUserDao(SessionFactory sessionFactory) {
        return new UserDAOImpl(sessionFactory);
    }

    @Autowired
    @Bean(name = "tablesDao")
    public TablesDAO getTablesDao(SessionFactory sessionFactory) {
        return new TablesDAOImpl(sessionFactory);
    }

    @Autowired
    @Bean(name = "newsClipTypeDao")
    public NewsClipTypeDAO getNewsClipTypeDao(SessionFactory sessionFactory) {
        return new NewsClipTypeDAOImpl(sessionFactory);
    }

    @Autowired
    @Bean(name = "assignmentTypeDao")
    public AssignmentTypeDAO getAssignmentTypeDao(SessionFactory sessionFactory) {
        return new AssignmentTypeDAOImpl(sessionFactory);
    }
    
    @Autowired
    @Bean(name = "tableLoader")
    public TableLoader getTableLoader(SessionFactory sessionFactory) {
        return new TableLoader(sessionFactory);
    }
    
}
