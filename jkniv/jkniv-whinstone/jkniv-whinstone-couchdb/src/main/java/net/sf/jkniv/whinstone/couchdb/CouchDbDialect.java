package net.sf.jkniv.whinstone.couchdb;

import net.sf.jkniv.sqlegance.dialect.AnsiDialect;

public class CouchDbDialect extends AnsiDialect
{
    
    @Override
    public boolean supportsLimit()
    {
        return true;
    }
    
    @Override
    public boolean supportsLimitOffset()
    {
        return true;
    }
    
    @Override
    public String buildQueryPaging(String sqlText, int offset, int max)
    {
        return sqlText;
    }
    
    @Override
    public String buildQueryCount(String sqlText)
    {
        return sqlText;
    }
    
}
