package net.sf.jkniv.whinstone.couchdb;

import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.DefaultQueryHandler;

/**
 * Couchdb Command to handler the {@code Select} life-cycle.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class SelectHandler extends DefaultQueryHandler
{
    public SelectHandler(HttpCookieConnectionAdapter adapterConn)
    {
        super(adapterConn);
        with(this);
    }
    
    @Override
    public Command asCommand()
    {
        Command c = getConnectionAdapter().asSelectCommand(queryable, overloadResultRow);
        c.with(this);
        c.with(this.handleableException);
        return c;
    }
}
