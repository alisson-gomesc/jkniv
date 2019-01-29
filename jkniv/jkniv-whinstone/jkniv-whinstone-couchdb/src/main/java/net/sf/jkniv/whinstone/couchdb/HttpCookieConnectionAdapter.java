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

import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.whinstone.statement.StatementAdapter;
import net.sf.jkniv.whinstone.couchdb.commands.AllDocsCommand;
import net.sf.jkniv.whinstone.couchdb.commands.BulkCommand;
import net.sf.jkniv.whinstone.couchdb.commands.CouchCommand;
import net.sf.jkniv.whinstone.couchdb.commands.DeleteCommand;
import net.sf.jkniv.whinstone.couchdb.commands.FindCommand;
import net.sf.jkniv.whinstone.couchdb.commands.FullResponseFindCommand;
import net.sf.jkniv.whinstone.couchdb.commands.GetCommand;
import net.sf.jkniv.whinstone.couchdb.commands.UpdateCommand;
import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.couchdb.commands.AddCommand;
import net.sf.jkniv.whinstone.couchdb.commands.ViewCommand;
import net.sf.jkniv.whinstone.couchdb.statement.CouchDbStatementAdapter;
import net.sf.jkniv.whinstone.couchdb.statement.FindAnswer;

class HttpCookieConnectionAdapter implements ConnectionAdapter
{
    private static final transient Logger LOG = LoggerFactory.getLogger(HttpCookieConnectionAdapter.class);
    private HttpBuilder httpBuilder;
    
    public HttpCookieConnectionAdapter(HttpBuilder httpBuilder)
    {
        this.httpBuilder = httpBuilder;
    }
 
    public HttpBuilder getHttpBuilder()
    {
        return httpBuilder;
    }
    
    @Override
    public void commit() throws SQLException
    {
        throw new UnsupportedOperationException("CouchDb doesn't support commit operation");
    }
    
    @Override
    public void rollback() throws SQLException
    {
        throw new UnsupportedOperationException("CouchDb doesn't support rollback operation");
    }
    
    @Override
    public void close() throws SQLException
    {
        LOG.warn("CouchDb repository doesn't implement this method yet!");
    }
    
    @Override
    public boolean isClosed() throws SQLException
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("CouchDb repository  doesn't implement this method yet!");
    }
    
    @Override
    public boolean isAutoCommit() throws SQLException
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("CouchDb repository  doesn't implement this method yet!");
    }
    
    @Override
    public void autoCommitOn() throws SQLException
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("CouchDb repository  doesn't implement this method yet!");
    }
    
    @Override
    public void autoCommitOff() throws SQLException
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("CouchDb repository  doesn't implement this method yet!");
    }
    
    @Override
    public Object getMetaData()
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("CouchDb repository  doesn't implement this method yet!");
    }
    
    @Override
    public <T, R> StatementAdapter<T, R> newStatement(Queryable queryable)
    {
        String sql = queryable.query(); 
        //queryable.getDynamicSql().getSql(queryable.getParams());
        //String positionalSql = queryable.getDynamicSql().getParamParser().replaceForQuestionMark(sql, queryable.getParams());
        //HttpRequestBase httpRequest = null;
        CouchCommand command = null;
        if (queryable.getDynamicSql().getLanguageType() == LanguageType.STORED)
        {
            
        }
        else
        {
            //command = new PostCommand(httpBuilder, sql);
            //httpRequest = httpBuilder.newFind(sql);
        }
        StatementAdapter<T, R> adapter = new CouchDbStatementAdapter(this.httpBuilder, sql, queryable.getDynamicSql().getParamParser());
        return adapter;
    }
    
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
        
        if (queryable.getReturnType() != null)
            returnType = queryable.getReturnType();
        else if (queryable.getDynamicSql().getReturnTypeAsClass() != null)
            returnType = queryable.getDynamicSql().getReturnTypeAsClass();
        
            stmt
            .returnType(returnType)
            .resultRow(overloadResultRow)
            .oneToManies(dynamicSql.asSelectable().getOneToMany())
            .groupingBy(dynamicSql.asSelectable().getGroupByAsList());

            if(queryable.isScalar())
                stmt.scalar();
            
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
    public <T, R> Command asDeleteCommand(Queryable queryable)
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
    public <T, R> StatementAdapter<T, R> newStatement(String sql)
    {
        throw new UnsupportedOperationException("CouchDb repository  doesn't implement this method yet!");
    }
    
    @Override
    public Object unwrap()
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("CouchDb repository  doesn't implement this method yet!");
    }
    
    
    @Override
    public String toString()
    {
        return "HttpConnectionAdapter [httpBuilder=" + httpBuilder + "]";
    }

    
}
