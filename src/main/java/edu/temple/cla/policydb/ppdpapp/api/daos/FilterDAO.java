package edu.temple.cla.policydb.ppdpapp.api.daos;

import edu.temple.cla.policydb.ppdpapp.api.models.Filter;
import java.util.List;

public interface FilterDAO {

    public List<Filter> list();

    public Filter find(Integer ID);
}
