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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.cache.Cacheable;
import net.sf.jkniv.domain.flat.AuthorFlat;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.sqlegance.builder.xml.TagFactory;
import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;
import net.sf.jkniv.sqlegance.params.ParamParserColonMark;
import net.sf.jkniv.sqlegance.types.Convertible;
import net.sf.jkniv.sqlegance.types.NoConverterType;
import net.sf.jkniv.sqlegance.validation.ValidateType;
import net.sf.jkniv.whinstone.classification.Transformable;
import net.sf.jkniv.whinstone.commands.Command;
import net.sf.jkniv.whinstone.commands.CommandAdapter;
import net.sf.jkniv.whinstone.commands.CommandHandler;
import net.sf.jkniv.whinstone.commands.DefaultQueryHandler;
import net.sf.jkniv.whinstone.params.AutoBindParams;

@SuppressWarnings("unchecked")
public class DefaultQueryHandlerTest
{
    @Rule
    public ExpectedException          catcher        = ExpectedException.none();
    private CommandHandler            commandHandler = newQueryHandler();
    
    private Command                   commandMock;
    private Command                   commandPagingMock;
    private CommandHandler            commandHandlerMock;
    private CommandAdapter            commandAdapterMock;
    //private StatementAdapter          stmtAdapterMock;
    private AutoBindParams            autoBindMock;
    private Queryable                 queryMock;
    private Selectable                selectableMock;
    private SqlDialect                sqlDialect;
    private ValidateType              validateTypeMock;
    private Cacheable<Object, Object> cacheableMock;
    private Cacheable.Entry           entry;
    private AuthorFlat                a1, a2;
    private static final long TOTAL_OF_RECORDS = 5;
    
    @Before
    public void setUp()
    {
        List<AuthorFlat> list = new ArrayList<AuthorFlat>();
        this.a1 = new AuthorFlat("A", "B1");
        this.a2 = new AuthorFlat("B", "B2");
        list.add(a1);
        list.add(a2);
        
        List<Number> listOfRows= new ArrayList<Number>();
        listOfRows.add(TOTAL_OF_RECORDS);
        
        this.commandAdapterMock = mock(CommandAdapter.class);
        this.commandHandlerMock = mock(CommandHandler.class);
        this.commandMock = mock(Command.class);
        this.commandPagingMock = mock(Command.class);
        //this.stmtAdapterMock = mock(StatementAdapter.class);
        this.autoBindMock = mock(AutoBindParams.class);
        //this.queryMock = mock(Queryable.class);
        this.selectableMock = mock(Selectable.class);
        this.sqlDialect = mock(SqlDialect.class);
        this.cacheableMock = mock(Cacheable.class);
        this.entry = mock(Cacheable.Entry.class);
        this.validateTypeMock = mock(ValidateType.class);
        //this.handlerException = mock(HandleableException.class);
        //this.exception = mock(Exception.class);
        
       
        given(this.entry.getValue()).willReturn(list);
        given(this.cacheableMock.getEntry(anyObject())).willReturn(entry);
//        given(this.queryMock.isCacheIgnore()).willReturn(false);
//        given(this.queryMock.getDynamicSql()).willReturn(selectableMock);
//        given(this.queryMock.bind(stmtAdapterMock)).willReturn(autoBindMock);
        given(this.selectableMock.isSelectable()).willReturn(true);
        given(this.selectableMock.hasCache()).willReturn(false);
        given(this.selectableMock.getCache()).willReturn(null);
        given(this.selectableMock.asSelectable()).willReturn(selectableMock);
        given(this.selectableMock.getSql(any())).willReturn("select id, name from author");
        given(this.selectableMock.getParamParser()).willReturn(ParamParserColonMark.getInstance());
        given(this.selectableMock.getSqlType()).willReturn(SqlType.SELECT);
        given(this.selectableMock.getLanguageType()).willReturn(LanguageType.NATIVE);
        given(this.selectableMock.getValidateType()).willReturn(validateTypeMock);
        given(this.selectableMock.getSqlDialect()).willReturn(this.sqlDialect);
        given(this.commandHandlerMock.asCommand()).willReturn(this.commandMock);
        given(this.commandMock.execute()).willReturn(list);
        given(this.commandPagingMock.execute()).willReturn(listOfRows);
        given(this.commandAdapterMock.asSelectCommand(any(Queryable.class), any(ResultRow.class))).willReturn(commandPagingMock);
        //given(this.commandAdapterMock.newStatement(anyString(), any(LanguageType.class))).willReturn(stmtAdapterMock);
        //given(this.stmtAdapterMock.returnType(any(Class.class))).willReturn(stmtAdapterMock);
        //given(this.stmtAdapterMock.rows()).willReturn(listOfRows);
        given(this.sqlDialect.supportsFeature(SqlFeatureSupport.PAGING_ROUNDTRIP)).willReturn(true);

        //doThrow(new RepositoryException()).when(this.handlerException).handle(this.exception);
    }
    
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
        catcher.expectMessage("Cannot execute sql [dummy] as SELECT, INSERT was expect");
        
        Sql sql = TagFactory.newSelect("dummy", LanguageType.NATIVE, new AnsiDialect());
        Queryable queryable = QueryFactory.of("dummy");
        queryable.bind(sql);
        commandHandler.with(new HandlerException());
        commandHandler.with(queryable);
        commandHandler.with(new RepositoryConfig());
        commandHandler.with(newResultRow());
        commandHandler.with(sql);
        
        assertThat(commandHandler.checkSqlType(SqlType.INSERT), instanceOf(CommandHandler.class));
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
    public void whenUseSelectCommandHanderWithPaging()
    {
        given(this.cacheableMock.getEntry(anyString())).willReturn(null);
        given(this.selectableMock.getCache()).willReturn(this.cacheableMock);
        given(this.selectableMock.extractNames(any())).willReturn(new String[0]);
        given(this.sqlDialect.buildQueryPaging(anyString(), any(Integer.class), any(Integer.class))).willReturn("select id, name from users LIMIT 10,5");
        given(this.sqlDialect.buildQueryCount(anyString())).willReturn("select count(1) from users");
        
        queryMock = QueryFactory.of("dummy", 5, 2);
        DefaultQueryHandler queryHandler = newDefaultQueryHandler();
        queryHandler.with(selectableMock);
        queryHandler.with(queryMock);
        queryHandler.with(new HandlerException());
        
        assertThat(queryHandler.checkSqlType(SqlType.SELECT), instanceOf(CommandHandler.class));
        List<AuthorFlat> answer = queryHandler.run();
        assertThat(answer, notNullValue());
        assertThat(answer, hasItems(a1, a2));
        
        assertThat(queryMock.isCached(), is(false));
        assertThat(queryMock.getTotal(), is(TOTAL_OF_RECORDS));
        
        verify(selectableMock).hasCache();
        verify(selectableMock).getValidateType();
        verify(validateTypeMock).assertValidate(anyObject());
        verify(commandMock).execute();
        verify(commandAdapterMock).close();
    }

    @Test
    public void whenUseSelectCommandHanderWithPagingNoSupportsRoundtrip()
    {
        given(this.cacheableMock.getEntry(anyString())).willReturn(null);
        given(this.selectableMock.getCache()).willReturn(this.cacheableMock);
        given(this.sqlDialect.supportsFeature(SqlFeatureSupport.PAGING_ROUNDTRIP)).willReturn(false);

        queryMock = QueryFactory.of("dummy", 5, 2);
        DefaultQueryHandler queryHandler = newDefaultQueryHandler();
        queryHandler.with(selectableMock);
        queryHandler.with(queryMock);
        queryHandler.with(new HandlerException());
        
        assertThat(queryHandler.checkSqlType(SqlType.SELECT), instanceOf(CommandHandler.class));
        List<AuthorFlat> answer = queryHandler.run();
        assertThat(answer, notNullValue());
        assertThat(answer, hasItems(a1, a2));
        
        assertThat(queryMock.isCached(), is(false));
        assertThat(queryMock.getTotal(), is((long)Statement.SUCCESS_NO_INFO));
        
        verify(selectableMock).hasCache();
        verify(selectableMock).getValidateType();
        verify(validateTypeMock).assertValidate(anyObject());
        verify(commandMock).execute();
        verify(commandAdapterMock).close();
    }


    @Test
    public void whenUseSelectCommandHanderWithPagingCannotCountTotalRecords()
    {
        given(this.cacheableMock.getEntry(anyString())).willReturn(null);
        given(this.selectableMock.getCache()).willReturn(this.cacheableMock);
        given(this.selectableMock.extractNames(any())).willReturn(new String[0]);
        given(this.sqlDialect.buildQueryPaging(anyString(), any(Integer.class), any(Integer.class))).willReturn("select id, name from users LIMIT 10,5");
        given(this.sqlDialect.buildQueryCount(anyString())).willReturn("select count(1) from users");        
        doThrow(new RepositoryException()).when(this.commandPagingMock).execute();

        queryMock = QueryFactory.of("dummy", 5, 2);
        DefaultQueryHandler queryHandler = newDefaultQueryHandler();
        queryHandler.with(selectableMock);
        queryHandler.with(queryMock);
        queryHandler.with(new HandlerException());
        
        assertThat(queryHandler.checkSqlType(SqlType.SELECT), instanceOf(CommandHandler.class));
        List<AuthorFlat> answer = queryHandler.run();
        assertThat(answer, notNullValue());
        assertThat(answer, hasItems(a1, a2));
        
        assertThat(queryMock.isCached(), is(false));
        assertThat(queryMock.getTotal(), is((long)Statement.SUCCESS_NO_INFO));
        
        verify(selectableMock).hasCache();
        verify(selectableMock).getValidateType();
        verify(validateTypeMock).assertValidate(anyObject());
        verify(commandMock).execute();
        verify(commandAdapterMock).close();
    }


    @Test
    public void whenUseSelectCommandHanderWithCache()
    {
        given(this.cacheableMock.getEntry(anyString())).willReturn(null);
        given(this.selectableMock.getCache()).willReturn(this.cacheableMock);
        
        queryMock = QueryFactory.of("dummy");
        DefaultQueryHandler queryHandler = newDefaultQueryHandler();
        queryHandler.with(selectableMock);
        queryHandler.with(queryMock);
        
        assertThat(queryHandler.checkSqlType(SqlType.SELECT), instanceOf(CommandHandler.class));
        List<AuthorFlat> answer = queryHandler.run();
        assertThat(answer, notNullValue());
        assertThat(answer, hasItems(a1, a2));        
        assertThat(queryMock.isCached(), is(false));
        assertThat(queryMock.getTotal(), is(2L));

        verify(selectableMock).hasCache();
        verify(selectableMock).getValidateType();
        verify(validateTypeMock).assertValidate(anyObject());
        verify(commandMock).execute();
        verify(commandAdapterMock).close();
    }
    
    @Test
    public void whenUseSelectCommandHanderFetchCache()
    {
        given(this.selectableMock.hasCache()).willReturn(true);
        given(this.selectableMock.getCache()).willReturn(this.cacheableMock);
        
        queryMock = QueryFactory.of("dummy");
        DefaultQueryHandler queryHandler = newDefaultQueryHandler();
        
        queryHandler.with(selectableMock);
        queryHandler.with(queryMock);
        
        assertThat(queryHandler.checkSqlType(SqlType.SELECT), instanceOf(CommandHandler.class));
        List<AuthorFlat> answer = queryHandler.run();
        assertThat(answer, notNullValue());
        assertThat(answer, hasItems(a1, a2));
        assertThat(queryMock.isCached(), is(true));
        assertThat(queryMock.getTotal(), is((long)Statement.SUCCESS_NO_INFO));

        verify(selectableMock, never()).hasCache();
        verify(selectableMock).getValidateType();
        verify(validateTypeMock).assertValidate(anyObject());
        verify(commandMock, never()).execute();
        verify(commandAdapterMock).close();
    }
    
    @Test
    public void whenUseSelectCommandHanderWithException()
    {
        //catcher.expect(RepositoryException.class);
        //catcher.expectMessage("SQL Exception");
        commandHandler = newQueryHandlerRunException();
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
        catch (RepositoryException e)
        {
        }
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
        assertThat(authorFlat.getCallback(), hasItems("PRE-SELECT", "POST-SELECT"));
    }
    
    private DefaultQueryHandler newDefaultQueryHandler()
    {
        return new DefaultQueryHandler(commandAdapterMock)
        {
            @Override
            public Command asCommand()
            {
                return commandMock;
            }
        };
        
    }
    
    private CommandHandler newQueryHandler()
    {
        CommandHandler handler = new DefaultQueryHandler(newCommandAdapter())
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
    
    private CommandHandler newQueryHandlerRunException()
    {
        CommandHandler handler = new DefaultQueryHandler(newCommandAdapter())
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
    
    private ResultRow<AuthorFlat, ResultSet> newResultRow()
    {
        
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
    
    private CommandAdapter newCommandAdapter()
    {
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
                return newCommandAsSelect();
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
    
    private Command newCommandAsSelect()
    {
        return new Command()
        {
            
            @Override
            public Command with(CommandHandler commandHandler)
            {
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
}
