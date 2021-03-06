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
import net.sf.jkniv.whinstone.Param;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.cassandra.commands.AddSequenceKeyJdbcCommand;
import net.sf.jkniv.whinstone.cassandra.commands.BulkCommand;
import net.sf.jkniv.whinstone.cassandra.commands.CassandraSequenceGeneratedKey;
import net.sf.jkniv.whinstone.cassandra.commands.DefaultCommand;
import net.sf.jkniv.whinstone.cassandra.commands.DefaultQuery;
import net.sf.jkniv.whinstone.cassandra.statement.CassandraPreparedStatementAdapter;
import net.sf.jkniv.whinstone.cassandra.statement.CassandraStatementAdapter;
import net.sf.jkniv.whinstone.commands.Command;
import net.sf.jkniv.whinstone.commands.CommandAdapter;
import net.sf.jkniv.whinstone.statement.AutoKey;
import net.sf.jkniv.whinstone.statement.StatementAdapter;
import net.sf.jkniv.whinstone.types.RegisterType;

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
    private final RegisterType registerType;
    private final RegisterCodec registerCodec;
    private final HandleableException handlerException;
    
    public CassandraCommandAdapter(String contextName, Cluster cluster, Session session, RegisterType registerType, RegisterCodec registerCodec, HandleableException handlerException)
    {
        NOT_NULL.verify(cluster, session, contextName);
        this.cluster = cluster;
        this.session = session;
        this.registerType = registerType;
        this.registerCodec = registerCodec;
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
    
//    @Override
//    public <T, R> StatementAdapter<T, R> newStatement(String sql, LanguageType languageType)
//    {
//        // FIXME UnsupportedOperationException
//        throw new UnsupportedOperationException("Cassandra repository Not implemented yet!");
//    }
    
//    @Override
//    public Object unwrap()
//    {
//        return session;
//    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T, R> Command asSelectCommand(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        Command command = null;
        String sql = queryable.query();
        if(SQLLOG.isInfoEnabled())
            SQLLOG.info("Bind Native SQL\n{}", sql);

        StatementAdapter<T, R> stmt = null;
        if (queryable.isPaging())
        {
            Param[] params = queryable.values();
            Statement statement = new SimpleStatement(sql, extracValues(params));
            stmt = new CassandraStatementAdapter(this.session, statement, queryable, registerType);
            stmt.setFetchSize(queryable.getMax());
        }
        else
        {
            PreparedStatement stmtPrep = this.stmtCache.prepare(sql);
            stmt = new CassandraPreparedStatementAdapter(this.session, stmtPrep, queryable, registerType, registerCodec);
        }
        queryable.bind(stmt).on();
        stmt.with(overloadResultRow);
        command = new DefaultQuery(queryable).with(stmt);
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
    
    @SuppressWarnings({ "rawtypes" })
    private <T,R> Command buildCommand(Queryable queryable)
    {
        Command command = null;
        String sql = queryable.query();
        AutoKey auto = null;
        if(SQLLOG.isInfoEnabled())
            SQLLOG.info("Bind Native SQL\n{}",sql);
        
        PreparedStatement stmtPrep = this.stmtCache.prepare(sql);
        CassandraPreparedStatementAdapter<Number, Row> stmt = new CassandraPreparedStatementAdapter<Number, Row>(this.session, stmtPrep, queryable, registerType, registerCodec);
        if (queryable.isTypeOfBulk())
            command = new BulkCommand(queryable).with(stmt);
        else if (queryable.getDynamicSql().isInsertable() && 
                 queryable.getDynamicSql().asInsertable().isAutoGenerateKey())
        {
            Insertable isql = queryable.getDynamicSql().asInsertable();
            // isSequenceStrategy and isAutoStrategy are the the same in cassandra uuid value is generated
            // if(isql.getAutoGeneratedKey().isAutoStrategy())
            auto = new CassandraSequenceGeneratedKey(isql, this.session, handlerException);
            command = new AddSequenceKeyJdbcCommand(queryable).with(stmt);
        }
        else
            command = new DefaultCommand(queryable).with(stmt);

        stmt.with(auto);
        return command;        
    }

    @Override
    public String toString()
    {
        return "CassandraCommandAdapter [session=" + session + ", cluster=" + cluster + "]";
    }
    
    private Object[] extracValues(Param[] params)
    {
        Object[] values = new Object[params.length];
        for(int i=0; i<params.length; i++)
            values[i] = params[i].getValueAs();
            
        return values;
    }
}
