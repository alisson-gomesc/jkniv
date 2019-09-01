package net.sf.jkniv.whinstone.jpa2;

import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.DefaultQueryHandler;

/**
 * JDBC Command to handler the {@code Select} life-cycle.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class SelectHandler extends DefaultQueryHandler
{
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
