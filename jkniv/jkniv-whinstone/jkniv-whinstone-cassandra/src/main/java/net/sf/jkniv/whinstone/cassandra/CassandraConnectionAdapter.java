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
package net.sf.jkniv.whinstone.cassandra;

import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.cassandra.commands.DeleteCommand;
import net.sf.jkniv.whinstone.cassandra.commands.InsertCommand;
import net.sf.jkniv.whinstone.cassandra.commands.SelectCommand;
import net.sf.jkniv.whinstone.cassandra.commands.UpdateCommand;
import net.sf.jkniv.whinstone.cassandra.statement.CassandraStatementAdapter;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

class CassandraConnectionAdapter implements ConnectionAdapter
{
    private static final transient Logger  LOG = LoggerFactory.getLogger();
    private Session session;
    private Cluster cluster;
    private StatementCache stmtCache;
    
    public CassandraConnectionAdapter(Cluster cluster, Session session)
    {
        this.cluster = cluster;
        this.session = session;
        this.stmtCache = new StatementCache(session);
    }
    
    @Override
    public void commit() throws SQLException
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
    }
    
    @Override
    public void rollback() throws SQLException
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
    }
    
    @Override
    public void close() throws SQLException
    {
        if (session != null && !session.isClosed())
        {
            LOG.info("Closing Cassandra Session");
            session.close();
        }   
        if (cluster != null && !cluster.isClosed())
        {
            LOG.info("Closing Cassandra Cluster connection");
            cluster.close();
        }
        if(stmtCache != null)
        {
            LOG.info("Clean-up [{}] PreparedStatements cached", stmtCache.size());
            stmtCache.clear();
        }
        cluster = null;
        session = null;
        stmtCache = null;
    }
    
    @Override
    public boolean isClosed() throws SQLException
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
    }
    
    @Override
    public boolean isAutoCommit() throws SQLException
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
    }
    
    @Override
    public void autoCommitOn() throws SQLException
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
    }
    
    @Override
    public void autoCommitOff() throws SQLException
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
    }
    
    @Override
    public Object getMetaData()
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
    }
    
    @Override
    public <T, R> StatementAdapter<T, R> newStatement(Queryable queryable)
    {
        String sql = queryable.getDynamicSql().getSql(queryable.getParams());
        String positionalSql = queryable.getDynamicSql().getParamParser().replaceForQuestionMark(sql,
                queryable.getParams());
        PreparedStatement stmt = session.prepare(positionalSql);
        StatementAdapter<T, R> adapter = new CassandraStatementAdapter(session, stmt, queryable);
        return adapter;
    }
    
    @Override
    public <T, R> StatementAdapter<T, R> newStatement(String sql)
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
    }
    
    @Override
    public Object unwrap()
    {
        return session;
    }
    
    @Override
    public <T, R> Command asSelectCommand(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        Class returnType = Map.class;
        SelectCommand command = null;
        String sql = queryable.query();
        if(LOG.isInfoEnabled())
            LOG.debug("Bind Native SQL\n{}", sql);

        PreparedStatement stmtPrep = this.stmtCache.prepare(sql);
        StatementAdapter<T, R> stmt = new CassandraStatementAdapter(this.session, stmtPrep, queryable);
        
        queryable.bind(stmt).on();
        
        if (queryable.getReturnType() != null)
            returnType = queryable.getReturnType();
        else if (queryable.getDynamicSql().getReturnTypeAsClass() != null)
            returnType = queryable.getDynamicSql().getReturnTypeAsClass();
        
        stmt//.returnType(returnType)
            .resultRow(overloadResultRow)
            .oneToManies(queryable.getDynamicSql().asSelectable().getOneToMany())
            .groupingBy(queryable.getDynamicSql().asSelectable().getGroupByAsList());
        
        if (queryable.isScalar())
            stmt.scalar();
        
        command = new SelectCommand((CassandraStatementAdapter) stmt, queryable);
        return command;
    }
    
    @Override
    public <T, R> Command asUpdateCommand(Queryable queryable)
    {
        UpdateCommand command = null;
        String sql = queryable.query();
        if(LOG.isInfoEnabled())
            LOG.debug("Bind Native SQL\n{}",sql);

        PreparedStatement stmtPrep = this.stmtCache.prepare(sql);
        CassandraStatementAdapter<Number, ResultSet> stmt = new CassandraStatementAdapter<Number, ResultSet>(this.session, stmtPrep, queryable);
        queryable.bind(stmt).on();
        command = new UpdateCommand((CassandraStatementAdapter) stmt, queryable);
        return command;
    }
    
    @Override
    public <T, R> Command asDeleteCommand(Queryable queryable)//, ResultRow<T, R> overloadResultRow)
    {
        DeleteCommand command = null;

        String sql = queryable.query();
        if(LOG.isInfoEnabled())
            LOG.debug("Bind Native SQL\n{}",sql);

        PreparedStatement stmtPrep = this.stmtCache.prepare(sql);
        CassandraStatementAdapter<Number, ResultSet> stmt = new CassandraStatementAdapter<Number, ResultSet>(this.session, stmtPrep, queryable);
        queryable.bind(stmt).on();
        command = new DeleteCommand((CassandraStatementAdapter) stmt, queryable);
        return command;
    }
    
    @Override
    public <T, R> Command asAddCommand(Queryable queryable)//, ResultRow<T, R> overloadResultRow)
    {
        InsertCommand command = null;
        String sql = queryable.query();
        if(LOG.isInfoEnabled())
            LOG.debug("Bind Native SQL\n{}",sql);

        PreparedStatement stmtPrep = this.stmtCache.prepare(sql);
        CassandraStatementAdapter<Number, ResultSet> stmt = new CassandraStatementAdapter<Number, ResultSet>(this.session, stmtPrep, queryable);
        queryable.bind(stmt).on();
        command = new InsertCommand((CassandraStatementAdapter) stmt, queryable);
        return command;
    }

    @Override
    public String toString()
    {
        return "CassandraConnectionAdapter [session=" + session + ", cluster=" + cluster + "]";
    }
}
