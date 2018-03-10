package edu.temple.cla.policydb.ppdpapp.api.daos;

import edu.temple.cla.policydb.ppdpapp.api.models.Newspaper;
import java.util.List;

public interface NewspaperDAO {

    public List<Newspaper> list();

    public void add(String name);
}
