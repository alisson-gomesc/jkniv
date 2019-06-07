package net.sf.jkniv.whinstone.jdbc;

import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.DefaultQueryHandler;

public class SelectHandler extends DefaultQueryHandler
{
    //private static final Logger LOG = LoggerFactory.getLogger(SelectHandler.class);
    
    public SelectHandler(ConnectionAdapter adapterConn)
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
