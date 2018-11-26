package net.sf.jkniv.whinstone.couchdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.DefaultCommandHandler;

public class AddHandler extends DefaultCommandHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(AddHandler.class);
    
    public AddHandler(HttpCookieConnectionAdapter adapterConn)
    {
        super(adapterConn);
        with(this);
    }
    
    /*
    public <T> T run()
    {
        if (LOG.isTraceEnabled())
            LOG.trace("Executing [{}] as add command with dialect [{}]", queryable, CouchDbDialect.class);
        
        if (!queryable.isBoundSql())
            queryable.bind(insertable);
        
        insertable.getValidateType().assertValidate(queryable.getParams());
        
        Command command = adapterConn.asAddCommand(queryable);
        T affected = command.execute();
        
        if (LOG.isDebugEnabled())
            LOG.debug("{} records was affected by add [{}] query", affected, queryable.getName());
        return affected;
    }
    */
    
    @Override
    public Command asCommand()
    {
        return adapterConn.asAddCommand(queryable);
    }
    
}
