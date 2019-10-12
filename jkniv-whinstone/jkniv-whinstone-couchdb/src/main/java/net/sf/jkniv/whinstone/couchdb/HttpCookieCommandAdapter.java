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
package net.sf.jkniv.whinstone.couchdb;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.commands.Command;
import net.sf.jkniv.whinstone.commands.CommandAdapter;
import net.sf.jkniv.whinstone.couchdb.commands.AddCommand;
import net.sf.jkniv.whinstone.couchdb.commands.AllDocsCommand;
import net.sf.jkniv.whinstone.couchdb.commands.BulkCommand;
import net.sf.jkniv.whinstone.couchdb.commands.CouchCommand;
import net.sf.jkniv.whinstone.couchdb.commands.DeleteCommand;
import net.sf.jkniv.whinstone.couchdb.commands.FindCommand;
import net.sf.jkniv.whinstone.couchdb.commands.FullResponseFindCommand;
import net.sf.jkniv.whinstone.couchdb.commands.GetCommand;
import net.sf.jkniv.whinstone.couchdb.commands.UpdateCommand;
import net.sf.jkniv.whinstone.couchdb.commands.ViewCommand;
import net.sf.jkniv.whinstone.couchdb.statement.CouchDbStatementAdapter;
import net.sf.jkniv.whinstone.couchdb.statement.FindAnswer;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

class HttpCookieCommandAdapter implements CommandAdapter
{
    private static final Logger LOG = LoggerFactory.getLogger(HttpCookieCommandAdapter.class);
    private static final Assertable NOT_NULL = AssertsFactory.getNotNull();
    private HttpBuilder httpBuilder;
    private final String contextName;
    
    public HttpCookieCommandAdapter(HttpBuilder httpBuilder, String contextName)
    {
        NOT_NULL.verify(httpBuilder, contextName);
        this.httpBuilder = httpBuilder;
        this.contextName = contextName;
    }
 
    public HttpBuilder getHttpBuilder()
    {
        return httpBuilder;
    }
    
    @Override
    public String getContextName()
    {
        return this.contextName;
    }
        
    @Override
    public void close() //throws SQLException
    {
        LOG.debug("CouchDb repository doesn't implement close() method!");
    }
    
//    @Override
//    public <T, R> StatementAdapter<T, R> newStatement(String sql, LanguageType languageType)
//    {
//        throw new UnsupportedOperationException("CouchDb repository  doesn't implement this method yet!");
//    }
    
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T, R> Command asSelectCommand(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        Class returnType = Map.class;
        CouchCommand command = null;
        Sql dynamicSql = queryable.getDynamicSql();
        String sql = queryable.query();
        StatementAdapter<T, R> stmt = new CouchDbStatementAdapter(this.httpBuilder, sql, queryable.getDynamicSql().getParamParser());

        queryable.bind(stmt).on();
        returnType = queryable.getReturnType();
        stmt.with(overloadResultRow);
            
        if (CouchDbSqlContext.isGet(queryable.getName()))
            command = new GetCommand(this.httpBuilder, queryable);
        else if (CouchDbSqlContext.isAllDocs(queryable.getName()))
            command = new AllDocsCommand((CouchDbStatementAdapter) stmt, this.httpBuilder, queryable);
        else if (queryable.getDynamicSql().getLanguageType() == LanguageType.STORED)
            command = new ViewCommand((CouchDbStatementAdapter) stmt, this.httpBuilder, queryable);
        else if (queryable.getReturnType() != null && FindAnswer.class.getName().equals(queryable.getReturnType().getName()))
            command = new FullResponseFindCommand((CouchDbStatementAdapter) stmt, this.httpBuilder, queryable);
        else
            command = new FindCommand((CouchDbStatementAdapter) stmt, this.httpBuilder, queryable);
        return command;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T, R> Command asUpdateCommand(Queryable queryable)
    {
        CouchCommand command = null;
        String sql = queryable.query();
        StatementAdapter<T, R> stmt = new CouchDbStatementAdapter(this.httpBuilder, sql, queryable.getDynamicSql().getParamParser());
        if (queryable.isTypeOfBulk())
            command = new BulkCommand(httpBuilder, queryable);
        else
            command = new UpdateCommand(httpBuilder, queryable);
        return command;
    }

    @Override
    public <T, R> Command asRemoveCommand(Queryable queryable)
    {
        CouchCommand command = null;
        String sql = queryable.query();
        StatementAdapter<T, R> stmt = new CouchDbStatementAdapter(this.httpBuilder, sql, queryable.getDynamicSql().getParamParser());
        if (queryable.isTypeOfBulk())
            command = new BulkCommand(httpBuilder, queryable);
        else
            command = new DeleteCommand(httpBuilder, queryable);
        return command;
    }

    @Override
    public <T, R> Command asAddCommand(Queryable queryable)
    {
        CouchCommand command = null;
        String sql = queryable.query();
        StatementAdapter<T, R> stmt = new CouchDbStatementAdapter(this.httpBuilder, sql, queryable.getDynamicSql().getParamParser());
        if (queryable.isTypeOfBulk())
            command = new BulkCommand(httpBuilder, queryable);
        else
            command = new AddCommand(httpBuilder, queryable);
        return command;
    }

    @Override
    public String toString()
    {
        return "HttpCookieCommandAdapter [httpBuilder=" + httpBuilder + "]";
    }

    
}
