package net.sf.jkniv.whinstone.couchdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.DefaultCommandHandler;

public class RemoveHandler extends DefaultCommandHandler
{
    private static final Logger         LOG = LoggerFactory.getLogger(RemoveHandler.class);
    
    public RemoveHandler(HttpCookieConnectionAdapter adapterConn)
    {
        super(adapterConn);
        with(this);
    }
    
    @Override
    public Command asCommand()
    {
        return adapterConn.asDeleteCommand(queryable);
    }

    /*
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
    */
}
