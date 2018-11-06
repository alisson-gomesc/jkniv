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
package net.sf.jkniv.whinstone.jdbc.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.reflect.BasicType;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.ConnectionFactory;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.jdbc.DefaultPreparedStatementStrategy;
import net.sf.jkniv.whinstone.jdbc.PreparedStatementStrategy;
import net.sf.jkniv.whinstone.jdbc.experimental.commands.DbCommand;
import net.sf.jkniv.whinstone.jdbc.experimental.commands.DbCommandFactory;
import net.sf.jkniv.whinstone.jdbc.result.FlatObjectResultRow;
import net.sf.jkniv.whinstone.jdbc.result.MapResultRow;
import net.sf.jkniv.whinstone.jdbc.result.NumberResultRow;
import net.sf.jkniv.whinstone.jdbc.result.PojoResultRow;
import net.sf.jkniv.whinstone.jdbc.result.ScalarResultRow;
import net.sf.jkniv.whinstone.jdbc.result.StringResultRow;
import net.sf.jkniv.whinstone.statement.StatementAdapter;
import net.sf.jkniv.whinstone.transaction.TransactionContext;
import net.sf.jkniv.whinstone.transaction.TransactionSessions;
import net.sf.jkniv.whinstone.transaction.TransactionStatus;
import net.sf.jkniv.whinstone.transaction.Transactional;

public class UnitOfWork implements Work
{
    private final static Logger     LOG        = LoggerFactory.getLogger(UnitOfWork.class);
    private final static Assertable notNull    = AssertsFactory.getNotNull();
    private final static BasicType  BASIC_TYPE = BasicType.getInstance();
    
    private ConnectionFactory       connectionFactory;
    private Transactional           transaction;
    private HandleableException     handlerException;
    private RepositoryConfig        config;
    
    public UnitOfWork(ConnectionFactory connectionFactory, RepositoryConfig config)
    {
        this.handlerException = new HandlerException(RepositoryException.class, "JDBC Error cannot execute SQL");//TODO handler exception needs better configuration
        this.connectionFactory = connectionFactory;
        this.transaction = connectionFactory.getTransactionManager();
        this.config = config;
    }
    
    @Override
    public TransactionStatus getTransactionStatus()
    {
        return transaction.getStatus();
    }
    
    @Override
    public Transactional getTransaction()
    {
        return transaction;
    }
    
    @Override
    public String getContextName()
    {
        return Thread.currentThread().getName() + "-" + connectionFactory.getContextName();
    }
    
    @Override
    public int execute(Queryable queryable)
    {
        ConnectionAdapter adapterConn = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        Integer affected = 0;
        try
        {
            //SqlDialect sqlDialect = newDialect(queryable);
            adapterConn = getConnection(queryable.getDynamicSql().getIsolation());
            conn = (Connection) adapterConn.unwrap();
            PreparedStatementStrategy stmtStrategy = new DefaultPreparedStatementStrategy(queryable);//getPreparedStatementStrategy(sqlDialect);
            LOG.info(queryable.query());
            if (queryable.getDynamicSql().isInsertable())
            {
                DbCommand command = DbCommandFactory.newInstance(queryable, conn, stmtStrategy);
                affected = command.execute();
            }
            else
            {
                DbCommand command = DbCommandFactory.newInstance(queryable, adapterConn, stmtStrategy);
                affected = command.execute();
                //                StatementAdapter<Number, ResultSet> adapterStmt = adapterConn.newStatement(queryable);
                //                queryable.bind(adapterStmt).on();
                //                affected = adapterStmt.execute();
            }
        }
        finally
        {
            connectionFactory.close(stmt);
            connectionFactory.close(adapterConn);
        }
        return affected;
    }
    
    @Override
    public <T> List<T> select(Queryable queryable)
    {
        return select(queryable, null, null);
    }
    
    @Override
    public <T> List<T> select(Queryable queryable, Class<T> returnType)
    {
        return select(queryable, returnType, null);
    }
    
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    @Override
    public <T> List<T> select(Queryable queryable, Class<T> overloadReturnType,
            ResultRow<T, ResultSet> overloadResultRow)
    {
        ConnectionAdapter adapterConn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<T> list = null;
        Selectable select = queryable.getDynamicSql().asSelectable();
        Class<T> returnType = (Class<T>) Map.class;
        try
        {
            //SqlDialect sqlDialect = newDialect(queryable);
            adapterConn = getConnection(select.getIsolation());
            
            if (overloadReturnType != null)
                returnType = overloadReturnType;
            else if (select.getReturnTypeAsClass() != null)
                returnType = (Class<T>) select.getReturnTypeAsClass();
            
            StatementAdapter<T, ResultSet> adapterStmt = adapterConn.newStatement(queryable);
            
            LOG.info(queryable.query());
            queryable.bind(adapterStmt).on();
            
            adapterStmt.returnType(returnType)
                       .resultRow(overloadResultRow)
                       .oneToManies(select.getOneToMany())
                       .groupingBy(select.getGroupByAsList());
            
            if (queryable.isScalar())
                adapterStmt.scalar();
            
            list = adapterStmt.rows();            
            if (queryable.isPaging())
            {
                StatementAdapter<Number, ResultSet> adapterStmtCount = adapterConn.newStatement(queryable.queryCount());
                queryable.bind(adapterStmtCount).on();
                adapterStmtCount.returnType(Number.class).scalar();
                try
                {
                    Long rows = adapterStmtCount.rows().get(0).longValue();
                    queryable.setTotal(rows);
                }
                catch (RepositoryException e)
                {
                    // FIXME BUG select count with ORDER BY 
                    // The ORDER BY clause is invalid in views, inline functions, derived tables, subqueries, and common table expressions, unless TOP or FOR XML is also specified.
                    LOG.error("Cannot count the total of rows from full query [{}]", queryable.getName(), e);
                }
            }
            else
                queryable.setTotal(list.size());            
        }
        catch (RepositoryException e)
        {
            //jdbcAdapterFactory.getTransactionManager().rollback();
            handlerException.handle(e, e.getMessage());// FIXME HandleableException as helper doesn't throw exception
        }
        finally
        {
            connectionFactory.close(rs);
            connectionFactory.close(stmt);
            connectionFactory.close(adapterConn);
        }
        return list;
    }
    
    @Override
    public void close()
    {
        LOG.warn("Close works not implemented yet...");
        // TODO implementation close work to release resources
    }
    
    private ConnectionAdapter getConnection(Isolation isolation)
    {
        ConnectionAdapter conn = null;
        TransactionContext transactionContext = TransactionSessions.get(connectionFactory.getContextName());
        
        if (transactionContext != null && transactionContext.isActive())
        {
            LOG.debug("Taking existent Connection from Transaction Context");
            conn = transactionContext.getConnection();
        }
        if (conn == null)
        {
            conn = connectionFactory.open(isolation);
        }
        return conn;
    }
    
    //    /**
    //     * Create new Dialect to generate the queries.
    //     * @param queryable query name and your params
    //     * @return return new instance of SqlDialect
    //     */
    //    private SqlDialect newDialect(Queryable queryable)
    //    {
    //        //notNull.verify(queryable, isql);
    //        // TODO performance keep instance from SqlDialect implementation
    //        SqlDialect sqlDialect = null;// TODO auto-discover dialect over jdbc connection
    //        String sqlDialectName = config.getSqlDialect();
    //        ObjectProxy<SqlDialect> proxy = ObjectProxyFactory.newProxy(sqlDialectName);
    //        sqlDialect = proxy.newInstance();
    //        sqlDialect.setQueryable(queryable);
    //        return sqlDialect;
    //    }
    
    private <T> void setParseRow(Queryable queryable, Sql isql, Class<T> returnType, JdbcColumn[] columns)
    {
        ResultRow<T, ?> rsRowParser = null;
        //JdbcColumn[] columns = JdbcFactory.getJdbcColumns(rs.getMetaData());
        Selectable selectTag = (Selectable) isql;
        if (queryable.isScalar())
        {
            rsRowParser = new ScalarResultRow(columns);
        }
        else if (Map.class.isAssignableFrom(returnType))
        {
            rsRowParser = new MapResultRow(returnType, columns);
            //transformable = new MapTransform();
        }
        else if (Number.class.isAssignableFrom(returnType)) // FIXME implements for date, calendar, boolean improve design
        {
            rsRowParser = new NumberResultRow(returnType, columns);
        }
        else if (String.class.isAssignableFrom(returnType))
        {
            rsRowParser = new StringResultRow(columns);
        }
        else if (selectTag.getOneToMany().isEmpty())
        {
            rsRowParser = new FlatObjectResultRow(returnType, columns);
            //transformable = new ObjectTransform();
        }
        else
        {
            rsRowParser = new PojoResultRow(returnType, columns, selectTag.getOneToMany());
            //transformable = new ObjectTransform();
        }
        
    }
    
    @Override
    public String toString()
    {
        return "UnitOfWork [config=" + config + ", jdbcAdapterFactory=" + connectionFactory + ", transaction="
                + transaction + "]";
    }
    
}
