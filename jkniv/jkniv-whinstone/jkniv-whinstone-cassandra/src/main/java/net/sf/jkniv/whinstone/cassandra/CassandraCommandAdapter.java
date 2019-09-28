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

import java.util.Map;

import org.slf4j.Logger;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.CommandAdapter;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.cassandra.commands.AddSequenceKeyJdbcCommand;
import net.sf.jkniv.whinstone.cassandra.commands.BulkCommand;
import net.sf.jkniv.whinstone.cassandra.commands.CassandraSequenceGeneratedKey;
import net.sf.jkniv.whinstone.cassandra.commands.DefaultCommand;
import net.sf.jkniv.whinstone.cassandra.commands.DefaultQuery;
import net.sf.jkniv.whinstone.cassandra.statement.CassandraPreparedStatementAdapter;
import net.sf.jkniv.whinstone.cassandra.statement.CassandraStatementAdapter;
import net.sf.jkniv.whinstone.statement.AutoKey;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class CassandraCommandAdapter implements CommandAdapter
{
    private static final Logger  LOG = LoggerFactory.getLogger();
    private static final Logger SQLLOG = net.sf.jkniv.whinstone.cassandra.LoggerFactory.getLogger();
    private static final Assertable NOT_NULL = AssertsFactory.getNotNull();
    private Session session;
    private Cluster cluster;
    private StatementCache stmtCache;
    private final String contextName;
    private final HandleableException handlerException;
    
    public CassandraCommandAdapter(Cluster cluster, Session session, String contextName, HandleableException handlerException)
    {
        NOT_NULL.verify(cluster, session, contextName);
        this.cluster = cluster;
        this.session = session;
        this.stmtCache = new StatementCache(session);
        this.contextName = contextName;
        this.handlerException = handlerException;
    }
    
    @Override
    public String getContextName()
    {
        return this.contextName;
    }

    
//    @Override
//    public void commit() throws SQLException
//    {
//        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
//    }
    
//    @Override
//    public void rollback() throws SQLException
//    {
//        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
//    }
    
    @Override
    public void close() //throws SQLException
    {
        // Cassandra session is built in multi-thread
    }

    //@Override
    public void shutdown() //throws SQLException
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

//    @Override
//    public boolean isClosed() throws SQLException
//    {
//        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
//    }
    
//    @Override
//    public boolean isAutoCommit() throws SQLException
//    {
//        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
//    }
    
//    @Override
//    public void autoCommitOn() throws SQLException
//    {
//        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
//    }
    
//    @Override
//    public void autoCommitOff() throws SQLException
//    {
//        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
//    }
    
//    @Override
//    public Object getMetaData()
//    {
//        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
//    }
    
    /*
    @Override
    public <T, R> StatementAdapter<T, R> newStatement(Queryable queryable)
    {
        String sql = queryable.getDynamicSql().getSql(queryable.getParams());
        String positionalSql = queryable.getDynamicSql().getParamParser().replaceForQuestionMark(sql,
                queryable.getParams());
        PreparedStatement stmt = session.prepare(positionalSql);
        StatementAdapter<T, R> adapter = new CassandraPreparedStatementAdapter(session, stmt, queryable);
        return adapter;
    }
     */
    
    @Override
    public <T, R> StatementAdapter<T, R> newStatement(String sql, LanguageType languageType)
    {
        // FIXME UnsupportedOperationException
        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
    }
    
//    @Override
//    public Object unwrap()
//    {
//        return session;
//    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T, R> Command asSelectCommand(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        Class returnType = Map.class;
        DefaultQuery command = null;
        String sql = queryable.query();
        if(SQLLOG.isInfoEnabled())
            SQLLOG.info("Bind Native SQL\n{}", sql);

        StatementAdapter<T, R> stmt = null;
        if (queryable.isPaging())
        {
            String[] names = queryable.getParamsNames();
            Object[] params = queryable.values(names);
            Statement statement = new SimpleStatement(sql, params);
            stmt = new CassandraStatementAdapter(this.session, statement, queryable);
            stmt.setFetchSize(queryable.getMax());
        }
        else
        {
            PreparedStatement stmtPrep = this.stmtCache.prepare(sql);
            stmt = new CassandraPreparedStatementAdapter(this.session, stmtPrep, queryable);
        }
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
        
        command = new DefaultQuery(stmt, queryable);
        return command;
    }
    
    @Override
    public <T, R> Command asUpdateCommand(Queryable queryable)
    {
        return buildCommand(queryable);
    }
    
    @Override
    public <T, R> Command asRemoveCommand(Queryable queryable)//, ResultRow<T, R> overloadResultRow)
    {
        return buildCommand(queryable);
    }
    
    @Override
    public <T, R> Command asAddCommand(Queryable queryable)//, ResultRow<T, R> overloadResultRow)
    {
        return buildCommand(queryable);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private <T,R> Command buildCommand(Queryable queryable)
    {
        Command command = null;
        String sql = queryable.query();
        AutoKey auto = null;
        if(SQLLOG.isInfoEnabled())
            SQLLOG.info("Bind Native SQL\n{}",sql);
        
        PreparedStatement stmtPrep = this.stmtCache.prepare(sql);
        CassandraPreparedStatementAdapter<Number, Row> stmt = new CassandraPreparedStatementAdapter<Number, Row>(this.session, stmtPrep, queryable);
        if (queryable.getDynamicSql().isBatch() || queryable.isTypeOfBulk())
            command = new BulkCommand((CassandraPreparedStatementAdapter) stmt, queryable);        
        else if (queryable.getDynamicSql().isInsertable() && 
                 queryable.getDynamicSql().asInsertable().isAutoGenerateKey())
        {
            Insertable isql = queryable.getDynamicSql().asInsertable();
            // isSequenceStrategy and isAutoStrategy are the the same in cassandra uuid value is generated
            // if(isql.getAutoGeneratedKey().isAutoStrategy())
            auto = new CassandraSequenceGeneratedKey(isql, this.session, handlerException);
            command = new AddSequenceKeyJdbcCommand(stmt, queryable);
        }
        else
            command = new DefaultCommand((CassandraPreparedStatementAdapter) stmt, queryable);

        stmt.with(auto);
        return command;        
    }

    @Override
    public String toString()
    {
        return "CassandraCommandAdapter [session=" + session + ", cluster=" + cluster + "]";
    }
}
