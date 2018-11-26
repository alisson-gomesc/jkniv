package net.sf.jkniv.whinstone.couchdb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.couchdb.dialect.CouchDbDialect;

public class AddHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(AddHandler.class);
    private Queryable queryable;
    private Insertable insertable;
    private HttpCookieConnectionAdapter       adapterConn;

    public AddHandler(Queryable queryable, Insertable insertable, HttpCookieConnectionAdapter adapterConn)
    {
        this.queryable = queryable;
        this.insertable = insertable;
        this.adapterConn = adapterConn;
    }

    public int add()
    {
        if (LOG.isTraceEnabled())
            LOG.trace("Executing [{}] as add command with dialect [{}]", queryable, CouchDbDialect.class);
        
        if (!queryable.isBoundSql())
            queryable.bind(insertable);
        
        insertable.getValidateType().assertValidate(queryable.getParams());
        
        Command command = adapterConn.asAddCommand(queryable);
        int affected = command.execute();
        
        if (LOG.isDebugEnabled())
            LOG.debug("{} records was affected by add [{}] query", affected, queryable.getName());
        return affected;
    }

    
}
