package edu.temple.cla.policydb.ppdpapp.api.daos;

import edu.temple.cla.policydb.ppdpapp.api.models.File;
import java.util.List;

public interface FileDAO {

    public List<File> list();

    public File find(int id);

    public File save(File fileObj);

    public Object findBatchByFileID(int fileid);

    public int create(File fileObj);
}
