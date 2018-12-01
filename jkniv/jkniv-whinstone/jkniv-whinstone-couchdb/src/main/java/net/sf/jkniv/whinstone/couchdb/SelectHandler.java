package net.sf.jkniv.whinstone.couchdb;

import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.DefaultQueryHandler;

public class SelectHandler extends DefaultQueryHandler
{
    //private static final Logger LOG = LoggerFactory.getLogger(SelectHandler.class);
    
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
