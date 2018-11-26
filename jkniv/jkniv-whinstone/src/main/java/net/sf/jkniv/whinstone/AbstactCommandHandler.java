package net.sf.jkniv.whinstone;

import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;

abstract class AbstactCommandHandler implements CommandHandler
{

    @Override
    public CommandHandler with(CommandHandler handler)
    {
        return null;
    }

    @Override
    public CommandHandler with(Queryable queryable)
    {
        return null;
    }

    @Override
    public CommandHandler with(Sql sql)
    {
        return null;
    }

    @Override
    public CommandHandler with(RepositoryConfig repositoryConfig)
    {
        return null;
    }

    @Override
    abstract public Command asCommand();

    @Override
    abstract public <T> T run();
    
}
