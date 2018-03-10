package edu.temple.cla.policydb.ppdpapp.api.daos;

import edu.temple.cla.policydb.ppdpapp.api.models.Role;
import java.util.List;

public interface RoleDAO {

    public List<Role> list();

    public Role find(int id);
}
