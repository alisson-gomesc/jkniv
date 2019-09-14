package net.sf.jkniv.whinstone.jdbc;

import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.CommandAdapter;
import net.sf.jkniv.whinstone.DefaultQueryHandler;

/**
 * JDBC Command to handler the {@code Select} life-cycle.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class SelectHandler extends DefaultQueryHandler
{
    public SelectHandler(CommandAdapter cmdAdapter)
    {
        super(cmdAdapter);
        //with(this);
    }
    
    @Override
    public Command asCommand()
    {
        Command c = getCommandAdapter().asSelectCommand(queryable, overloadResultRow);
        c.with(this);
        c.with(this.handleableException);
        return c;
    }
}
