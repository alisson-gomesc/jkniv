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
package net.sf.jkniv.whinstone;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.domain.flat.AuthorFlat;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.sqlegance.builder.xml.TagFactory;
import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.types.Convertible;
import net.sf.jkniv.sqlegance.types.NoConverterType;
import net.sf.jkniv.whinstone.classification.Transformable;
import net.sf.jkniv.whinstone.commands.Command;
import net.sf.jkniv.whinstone.commands.CommandAdapter;
import net.sf.jkniv.whinstone.commands.CommandHandler;
import net.sf.jkniv.whinstone.commands.DefaultCommandHandler;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

@SuppressWarnings("unchecked")
public class DefaultCommandHandlerTest
{
    @Rule
    public ExpectedException  catcher = ExpectedException.none();
    CommandHandler commandHandler = newCommandHandler();

    @Test
    public void whenCommandHaventSqlInstanceOf() 
    {
        catcher.expect(IllegalArgumentException.class);
        catcher.expectMessage("Null Sql reference wasn't expected");
        
        assertThat(commandHandler.checkSqlType(SqlType.SELECT), instanceOf(CommandHandler.class));
    }

    @Test
    public void whenCommandHasDiferentSqlType() 
    {
        catcher.expect(IllegalArgumentException.class);
        catcher.expectMessage("Cannot execute sql [dummy] as INSERT, SELECT was expect");

        Sql sql = TagFactory.newInsert("dummy", LanguageType.NATIVE, new AnsiDialect());
        Queryable queryable = QueryFactory.of("dummy");
        queryable.bind(sql);
        commandHandler.with(new HandlerException());
        commandHandler.with(queryable);
        commandHandler.with(new RepositoryConfig());
        commandHandler.with(newResultRow());
        commandHandler.with(sql);

        assertThat(commandHandler.checkSqlType(SqlType.SELECT), instanceOf(CommandHandler.class));
    }


    @Test
    public void whenUseInsertCommandHander() 
    {
        assertThat(commandHandler.asCommand(), instanceOf(Command.class));
        Sql sql = TagFactory.newInsert("dummy", LanguageType.NATIVE, new AnsiDialect());
        Queryable queryable = QueryFactory.of("dummy");
        commandHandler.with(new HandlerException());
        commandHandler.with(queryable);
        commandHandler.with(new RepositoryConfig());
        commandHandler.with(newResultRow());
        commandHandler.with(sql);
        
        assertThat(commandHandler.checkSqlType(SqlType.INSERT), instanceOf(CommandHandler.class));
        assertThat(commandHandler.postCallback(), instanceOf(CommandHandler.class));
        assertThat(commandHandler.postCommit(), instanceOf(CommandHandler.class));
        assertThat(commandHandler.postException(), instanceOf(CommandHandler.class));
        assertThat(commandHandler.preCallback(), instanceOf(CommandHandler.class));
        assertThat(commandHandler.run(), notNullValue());
    }

    @Test
    public void whenUseUpdateCommandHander() 
    {
        assertThat(commandHandler.asCommand(), instanceOf(Command.class));
        Sql sql = TagFactory.newUpdate("dummy", LanguageType.NATIVE, new AnsiDialect());
        Queryable queryable = QueryFactory.of("dummy");
        queryable.bind(sql);
        commandHandler.with(new HandlerException());
        commandHandler.with(queryable);
        commandHandler.with(new RepositoryConfig());
        commandHandler.with(newResultRow());
        commandHandler.with(sql);
        
        assertThat(commandHandler.checkSqlType(SqlType.UPDATE), instanceOf(CommandHandler.class));
        assertThat(commandHandler.postCallback(), instanceOf(CommandHandler.class));
        assertThat(commandHandler.postCommit(), instanceOf(CommandHandler.class));
        assertThat(commandHandler.postException(), instanceOf(CommandHandler.class));
        assertThat(commandHandler.preCallback(), instanceOf(CommandHandler.class));
        assertThat(commandHandler.run(), notNullValue());
    }

    @Test
    public void whenUseDeleteCommandHander() 
    {
        assertThat(commandHandler.asCommand(), instanceOf(Command.class));
        
        Sql sql = TagFactory.newDelete("dummy", LanguageType.NATIVE, new AnsiDialect());
        Queryable queryable = QueryFactory.of("dummy");
        queryable.bind(sql);
        commandHandler.with(new HandlerException());
        commandHandler.with(queryable);
        commandHandler.with(new RepositoryConfig());
        commandHandler.with(newResultRow());
        commandHandler.with(sql);
        
        assertThat(commandHandler.checkSqlType(SqlType.DELETE), instanceOf(CommandHandler.class));        
        assertThat(commandHandler.postCallback(), instanceOf(CommandHandler.class));
        assertThat(commandHandler.postCommit(), instanceOf(CommandHandler.class));
        assertThat(commandHandler.postException(), instanceOf(CommandHandler.class));
        assertThat(commandHandler.preCallback(), instanceOf(CommandHandler.class));
        assertThat(commandHandler.run(), notNullValue());
    }

    @Test
    public void whenUseSelectCommandHander() 
    {
        assertThat(commandHandler.asCommand(), instanceOf(Command.class));
        
        Sql sql = TagFactory.newSelect("dummy", LanguageType.NATIVE, new AnsiDialect());
        Queryable queryable = QueryFactory.of("dummy");
        queryable.bind(sql);
        commandHandler.with(new HandlerException());
        commandHandler.with(queryable);
        commandHandler.with(new RepositoryConfig());
        commandHandler.with(newResultRow());
        commandHandler.with(sql);
        
        assertThat(commandHandler.checkSqlType(SqlType.SELECT), instanceOf(CommandHandler.class));
        assertThat(commandHandler.postCallback(), instanceOf(CommandHandler.class));
        assertThat(commandHandler.postCommit(), instanceOf(CommandHandler.class));
        assertThat(commandHandler.postException(), instanceOf(CommandHandler.class));
        assertThat(commandHandler.preCallback(), instanceOf(CommandHandler.class));
        assertThat(commandHandler.run(), notNullValue());
    }

    @Test
    public void whenUseSelectCommandHanderWithException() 
    {
        //catcher.expect(RepositoryException.class);
        //catcher.expectMessage("SQL Exception");
        commandHandler = newCommandHandlerRunException();
        assertThat(commandHandler.asCommand(), instanceOf(Command.class));
        
        Sql sql = TagFactory.newSelect("dummy", LanguageType.NATIVE, new AnsiDialect());
        Queryable queryable = QueryFactory.of("dummy");
        commandHandler.with(new HandlerException());
        commandHandler.with(queryable);
        commandHandler.with(new RepositoryConfig());
        commandHandler.with(newResultRow());
        commandHandler.with(sql);
        try 
        {
            commandHandler.run();
        }
        catch(RepositoryException e) {}
        assertThat(queryable.getTotal(), is(new Long(Statement.EXECUTE_FAILED)));
    }

    @Test
    public void whenUseCommandHanderVerifySelectCallback() 
    {
        Sql sql = TagFactory.newSelect("dummy", LanguageType.NATIVE, new AnsiDialect());
        Queryable queryable = QueryFactory.of("dummy", new AuthorFlat());
        queryable.bind(sql);
        commandHandler.with(new HandlerException());
        commandHandler.with(queryable);
        commandHandler.with(new RepositoryConfig());
        commandHandler.with(newResultRow());
        commandHandler.with(sql);
        
        assertThat(commandHandler.checkSqlType(SqlType.SELECT), instanceOf(CommandHandler.class));
        assertThat(commandHandler.run(), notNullValue());
        assertThat(queryable.getTotal(), is(0L));
        
        AuthorFlat authorFlat = queryable.getParams();
        assertThat(authorFlat.getCallback().size(), is(2));
        assertThat(authorFlat.getCallback(), hasItems("PRE-SELECT","POST-SELECT"));
    }

    @Test
    public void whenUseCommandHanderVerifyInsertCallback() 
    {
        Sql sql = TagFactory.newInsert("dummy", LanguageType.NATIVE, new AnsiDialect());
        Queryable queryable = QueryFactory.of("dummy", new AuthorFlat());
        queryable.bind(sql);
        commandHandler.with(new HandlerException());
        commandHandler.with(queryable);
        commandHandler.with(new RepositoryConfig());
        commandHandler.with(newResultRow());
        commandHandler.with(sql);
        
        assertThat(commandHandler.checkSqlType(SqlType.INSERT), instanceOf(CommandHandler.class));
        assertThat(commandHandler.run(), notNullValue());
        
        AuthorFlat authorFlat = queryable.getParams();
        assertThat(authorFlat.getCallback().size(), is(2));
        assertThat(authorFlat.getCallback(), hasItems("PRE-ADD","POST-ADD"));
    }
    
    @Test
    public void whenUseCommandHanderVerifyUpdateCallback() 
    {
        Sql sql = TagFactory.newUpdate("dummy", LanguageType.NATIVE, new AnsiDialect());
        Queryable queryable = QueryFactory.of("dummy", new AuthorFlat());
        queryable.bind(sql);
        commandHandler.with(new HandlerException());
        commandHandler.with(queryable);
        commandHandler.with(new RepositoryConfig());
        commandHandler.with(newResultRow());
        commandHandler.with(sql);
        
        assertThat(commandHandler.checkSqlType(SqlType.UPDATE), instanceOf(CommandHandler.class));
        assertThat(commandHandler.run(), notNullValue());
        
        AuthorFlat authorFlat = queryable.getParams();
        assertThat(authorFlat.getCallback().size(), is(2));
        assertThat(authorFlat.getCallback(), hasItems("PRE-UPDATE","POST-UPDATE"));
    }

    @Test
    public void whenUseCommandHanderVerifyDeleteCallback() 
    {
        Sql sql = TagFactory.newDelete("dummy", LanguageType.NATIVE, new AnsiDialect());
        Queryable queryable = QueryFactory.of("dummy", new AuthorFlat());
        queryable.bind(sql);
        commandHandler.with(new HandlerException());
        commandHandler.with(queryable);
        commandHandler.with(new RepositoryConfig());
        commandHandler.with(newResultRow());
        commandHandler.with(sql);
        
        assertThat(commandHandler.checkSqlType(SqlType.DELETE), instanceOf(CommandHandler.class));
        assertThat(commandHandler.run(), notNullValue());
        
        AuthorFlat authorFlat = queryable.getParams();
        assertThat(authorFlat.getCallback().size(), is(2));
        assertThat(authorFlat.getCallback(), hasItems("PRE-REMOVE","POST-REMOVE"));
    }

    private CommandHandler newCommandHandler()
    {
        CommandHandler handler = new DefaultCommandHandler(newCommandAdapter())
        {
            @Override
            public Command asCommand()
            {
                return new Command()
                {
                    CommandHandler commandHandler;
                    @Override
                    public Command with(CommandHandler commandHandler)
                    {
                        this.commandHandler = commandHandler;
                        return this;
                    }
                    
                    @Override
                    public Command with(HandleableException handlerException)
                    {
                        return this;
                    }
                    
                    @Override
                    public <T> T execute()
                    {
                        return (T) new ArrayList();
                    }
                };
            }
        };
        return handler;
    }

    private CommandHandler newCommandHandlerRunException()
    {
        CommandHandler handler = new DefaultCommandHandler(newCommandAdapter())
        {
            @Override
            public Command asCommand()
            {
                return new Command()
                {
                    CommandHandler commandHandler;
                    @Override
                    public Command with(CommandHandler commandHandler)
                    {
                        this.commandHandler = commandHandler;
                        return this;
                    }
                    
                    @Override
                    public Command with(HandleableException handlerException)
                    {
                        return this;
                    }
                    
                    @Override
                    public <T> T execute()
                    {
                        throw new RepositoryException("SQL Exception");
                    }
                };
            }
        };
        return handler;
    }

    private ResultRow<AuthorFlat,ResultSet> newResultRow() {
        
        return new ResultRow<AuthorFlat, ResultSet>()
        {

            @Override
            public AuthorFlat row(ResultSet rs, int rownum) throws SQLException
            {
                return null;
            }

            @Override
            public Transformable<AuthorFlat> getTransformable()
            {
                return null;
            }

            @Override
            public void setColumns(JdbcColumn<ResultSet>[] columns)
            {
            }

        };
    }

    private CommandAdapter newCommandAdapter() {
        return new CommandAdapter()
        {
            @Override
            public String getContextName()
            {
                return null;
            }
            
            @Override
            public void close()
            {
                
            }
            
            @Override
            public <T, R> Command asUpdateCommand(Queryable queryable)
            {
                return null;
            }
            
            @Override
            public <T, R> Command asSelectCommand(Queryable queryable, ResultRow<T, R> overloadResultRow)
            {
                return null;
            }
            
            @Override
            public <T, R> Command asRemoveCommand(Queryable queryable)
            {
                return null;
            }
            
            @Override
            public <T, R> Command asAddCommand(Queryable queryable)
            {
                return null;
            }
        };
    }
}
