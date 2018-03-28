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

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

import net.sf.jkniv.sqlegance.ConnectionAdapter;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.statement.StatementAdapter;
import net.sf.jkniv.whinstone.couchdb.commands.DbCommand;
import net.sf.jkniv.whinstone.couchdb.commands.PostCommand;
import net.sf.jkniv.whinstone.couchdb.statement.PreparedStatementAdapter;

public class HttpCookieConnectionAdapter implements ConnectionAdapter
{
    private HttpBuilder httpBuilder;
    
    public HttpCookieConnectionAdapter(HttpBuilder httpBuilder)
    {
        this.httpBuilder = httpBuilder;
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
        /*
        if (session != null && !session.isClosed())
        {
            session.close();
            
        }
        if (cluster != null && !cluster.isClosed())
        {
            cluster.close();
        }
        cluster = null;
        session = null;
        */
    }
    
    @Override
    public boolean isClosed() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean isAutoCommit() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public void autoCommitOn() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void autoCommitOff() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public Object getMetaData()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public <T, R> StatementAdapter<T, R> newStatement(Queryable queryable)
    {
        String sql = queryable.query(); //queryable.getDynamicSql().getSql(queryable.getParams());
        //String positionalSql = queryable.getDynamicSql().getParamParser().replaceForQuestionMark(sql, queryable.getParams());
        //HttpRequestBase httpRequest = null;
        DbCommand command = null;
        if (queryable.getDynamicSql().getLanguageType() == LanguageType.STORED)
        {
            
        }
        else
        {
            //command = new PostCommand(httpBuilder, sql);
            //httpRequest = httpBuilder.newFind(sql);
        }
        StatementAdapter<T, R> adapter = new PreparedStatementAdapter(this.httpBuilder, sql, queryable.getDynamicSql().getParamParser());
        return adapter;
    }
    
    @Override
    public <T, R> StatementAdapter<T, R> newStatement(String sql)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Object unwrap()
    {
        return null;//session;
    }
    
    
    
    @Override
    public String toString()
    {
        return "HttpConnectionAdapter [httpBuilder=" + httpBuilder + "]";
    }
    
}
