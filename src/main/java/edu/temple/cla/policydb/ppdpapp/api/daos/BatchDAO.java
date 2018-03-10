package edu.temple.cla.policydb.ppdpapp.api.daos;

import edu.temple.cla.policydb.ppdpapp.api.models.Batch;
import edu.temple.cla.policydb.ppdpapp.api.models.User;
import java.util.List;

public interface BatchDAO {

    public List<Batch> list();

    public Batch find(int id);

    public Batch save(Batch batchObj);

    public void create(Batch batchObj);

    public void delete(int id);

    public List<User> findUsers(int id);

    public List<Object> findDocuments(int id);

    public void addDocument(int batchID, String docID);

    public void deleteDocument(int batchID, String docID);

    public void deleteUser(int batchID, String email);
}
