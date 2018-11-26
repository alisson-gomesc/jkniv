package net.sf.jkniv.whinstone.couchdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.Updateable;
import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.DefaultCommandHandler;
import net.sf.jkniv.whinstone.Queryable;

public class UpdateHandler extends DefaultCommandHandler
{
    private static final Logger         LOG = LoggerFactory.getLogger(UpdateHandler.class);

    public UpdateHandler(HttpCookieConnectionAdapter adapterConn)
    {
        super(adapterConn);
        with(this);
    }

    @Override
    public Command asCommand()
    {
        return adapterConn.asUpdateCommand(queryable);
    }

    
/*    
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
*/    
}
