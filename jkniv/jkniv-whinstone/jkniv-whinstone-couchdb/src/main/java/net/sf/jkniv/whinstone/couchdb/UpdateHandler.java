package net.sf.jkniv.whinstone.couchdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.Updateable;
import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.couchdb.dialect.CouchDbDialect;

public class UpdateHandler
{
    private static final Logger         LOG = LoggerFactory.getLogger(UpdateHandler.class);
    private Queryable                   queryable;
    private Updateable                  updateable;
    private HttpCookieConnectionAdapter adapterConn;
    
    public UpdateHandler(Queryable queryable, Updateable updateable, HttpCookieConnectionAdapter adapterConn)
    {
        this.queryable = queryable;
        this.updateable = updateable;
        this.adapterConn = adapterConn;
    }
    
    public int update()
    {
        if (LOG.isTraceEnabled())
            LOG.trace("Executing [{}] as update command with dialect [{}]", queryable, CouchDbDialect.class);
        
        if (!queryable.isBoundSql())
            queryable.bind(updateable);
        
        updateable.getValidateType().assertValidate(queryable.getParams());
        
        Command command = adapterConn.asUpdateCommand(queryable);
        int affected = command.execute();
        
        if (LOG.isDebugEnabled())
            LOG.debug("{} records was affected by update [{}] query", affected, queryable.getName());
        return affected;
    }
    
}
