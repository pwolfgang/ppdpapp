package edu.temple.cla.policydb.ppdpapp.api.daos;

import java.util.List;

public interface CodeDAO {

    public List<Object> list(String tableName);

    public List<Object> listMajorMinor(String majorOrMinor);

    public Object find(String tableName, int id);

    public List<Object> findSearch(String tableName, String search);
}
