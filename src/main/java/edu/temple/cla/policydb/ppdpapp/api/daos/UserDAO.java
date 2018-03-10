package edu.temple.cla.policydb.ppdpapp.api.daos;

import edu.temple.cla.policydb.ppdpapp.api.models.Batch;
import edu.temple.cla.policydb.ppdpapp.api.models.User;
import java.util.List;

public interface UserDAO {

    public List<User> list();

    public User find(String email);

    public User findByToken(String token);

    public User save(User userObj);

    public void update(User userObj);

    public List<Batch> findBatches(String email);
}
