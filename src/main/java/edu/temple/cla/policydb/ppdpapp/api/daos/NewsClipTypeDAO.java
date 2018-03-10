package edu.temple.cla.policydb.ppdpapp.api.daos;

import edu.temple.cla.policydb.ppdpapp.api.models.NewsClipType;
import java.util.List;

/**
 *
 * @author Paul
 */
public interface NewsClipTypeDAO {
    public List<NewsClipType> list();
}
