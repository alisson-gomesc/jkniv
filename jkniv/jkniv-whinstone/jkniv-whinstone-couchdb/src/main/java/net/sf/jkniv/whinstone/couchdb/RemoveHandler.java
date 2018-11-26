package net.sf.jkniv.whinstone.couchdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.Deletable;
import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.couchdb.dialect.CouchDbDialect;

public class RemoveHandler
{
    private static final Logger         LOG = LoggerFactory.getLogger(RemoveHandler.class);
    private Queryable                   queryable;
    private Deletable                   deletable;
    private HttpCookieConnectionAdapter adapterConn;
    
    public RemoveHandler(Queryable queryable, Deletable deletable, HttpCookieConnectionAdapter adapterConn)
    {
        this.queryable = queryable;
        this.deletable = deletable;
        this.adapterConn = adapterConn;
    }
    
    public int remove()
    {
        if (LOG.isTraceEnabled())
            LOG.trace("Executing [{}] as remove command with dialect [{}]", queryable, CouchDbDialect.class);
        
        if (!queryable.isBoundSql())
            queryable.bind(deletable);
        
        deletable.getValidateType().assertValidate(queryable.getParams());
        
        Command command = adapterConn.asDeleteCommand(queryable);
        int affected = command.execute();
        
        if (LOG.isDebugEnabled())
            LOG.debug("{} records was affected by remove [{}] query", affected, queryable.getName());
        return affected;
    }
    
}
