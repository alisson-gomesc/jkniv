package net.sf.jkniv.whinstone.couchdb;

import java.util.List;

public interface CouchResult
{
    Long getTotalRows();
    
    Long getOffset();
    
    List<?> getRows();
 
    String getBookmark();
    
    String getWarning();

}
