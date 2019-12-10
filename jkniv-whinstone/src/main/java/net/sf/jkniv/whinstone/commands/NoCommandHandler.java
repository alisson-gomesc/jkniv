/* 
 * JKNIV, whinstone one contract to access your database.
 * 
 * Copyright (C) 2017, the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jkniv.whinstone.commands;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;

/**
 * Dummy/Empty implementation for {@link CommandHandler}
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 *
 */
public class NoCommandHandler implements CommandHandler
{
    private static final CommandHandler NO_COMMAND_HANDLER = new NoCommandHandler();
    
    public static CommandHandler getInstance()
    {
        return NO_COMMAND_HANDLER;
    }
    
    private NoCommandHandler() { }
    
//    @Override
//    public CommandHandler with(CommandHandler handler)
//    {
//        return this;
//    }
    
    @Override
    public CommandHandler with(ResultRow<?, ?> overloadResultRow)
    {
        return this;
    }
    
    @Override
    public CommandHandler with(Queryable queryable)
    {
        return this;
    }
    
    @Override
    public CommandHandler with(Sql sql)
    {
        return this;
    }
    
//    @Override
//    public CommandHandler with(RepositoryConfig repositoryConfig)
//    {
//        return this;
//    }
    
    @Override
    public CommandHandler with(HandleableException handlerException)
    {
        return this;
    }
    
    @Override
    public Command asCommand()
    {
        return NoCommand.getInstance();
    }
    
    @Override
    public CommandHandler preCallback()
    {
        return this;
    }
    
    @Override
    public CommandHandler postCallback()
    {
        return this;
    }
    
    @Override
    public CommandHandler postCommit()
    {
        return this;
    }
    
    @Override
    public CommandHandler postException()
    {
        return this;
    }
    
    @Override
    public <T> T run()
    {
        return null;
    }
 
    @Override
    public CommandHandler checkSqlType(SqlType expected)
    {
        return this;
    }
}
