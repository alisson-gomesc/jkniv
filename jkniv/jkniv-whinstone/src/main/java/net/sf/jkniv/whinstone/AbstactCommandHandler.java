package net.sf.jkniv.whinstone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.reflect.beans.ObjectProxy;
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
