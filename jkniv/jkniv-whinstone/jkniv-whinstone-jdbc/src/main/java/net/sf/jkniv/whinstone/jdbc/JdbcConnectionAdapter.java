package net.sf.jkniv.whinstone.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.statement.ResultSetConcurrency;
import net.sf.jkniv.sqlegance.statement.ResultSetHoldability;
import net.sf.jkniv.sqlegance.statement.ResultSetType;
import net.sf.jkniv.whinstone.Command;
import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.jdbc.commands.AddAutoKeyJdbcCommand;
import net.sf.jkniv.whinstone.jdbc.commands.AddSequenceKeyJdbcCommand;
import net.sf.jkniv.whinstone.jdbc.commands.BulkJdbcCommand;
import net.sf.jkniv.whinstone.jdbc.commands.DefaultJdbcCommand;
import net.sf.jkniv.whinstone.jdbc.commands.DefaultJdbcQuery;
import net.sf.jkniv.whinstone.statement.StatementAdapter;
import net.sf.jkniv.whinstone.transaction.TransactionContext;
import net.sf.jkniv.whinstone.transaction.TransactionSessions;

public class JdbcConnectionAdapter implements ConnectionAdapter
{
    private static final Logger LOG = LoggerFactory.getLogger(JdbcConnectionAdapter.class);
    private static final transient Assertable NOT_NULL = AssertsFactory.getNotNull();
    //private static final HandleableException handlerException = new HandlerException(RepositoryException.class,
    //        "Exception at connection session running %s");
    
    private final Connection          conn;
    private final String              contextName;
    
    public JdbcConnectionAdapter(Connection conn, String contextName)
    {
        NOT_NULL.verify(conn, contextName);
        this.conn = conn;
        this.contextName = contextName;
    }
    
    @Override
    public String getContextName()
    {
        return this.contextName;
    }
    
    @Override
    public void commit() throws SQLException
    {
        //        try
        //        {
        this.conn.commit();
        //        }
        //        catch (SQLException sqle)
        //        {
        //            handlerException.handle(sqle, "COMMIT");
        //        }
    }
    
    @Override
    public void rollback() throws SQLException
    {
        //        try
        //        {
        this.conn.rollback();
        //        }
        //        catch (SQLException sqle)
        //        {
        //            handlerException.handle(sqle, "ROLLBACK");
        //        }        
    }
    
    @Override
    public void close() //throws SQLException
    {
        try
        {
            TransactionContext ctx = TransactionSessions.get(contextName);
            if (ctx == null || !ctx.isActive())
                this.conn.close();
        }
        catch (SQLException sqle)
        {
            throw new RepositoryException("Cannot close connection [" + sqle.getMessage() + "]", sqle);
            //LOG.warn("Erro to closing connection. Reason: " + sqle.getMessage());
        }
    }
    
    @Override
    public boolean isClosed() throws SQLException
    {
        //        boolean closed = false;
        //        try
        //        {
        return conn.isClosed();
        //        }
        //        catch (SQLException sqle)
        //        {
        //            LOG.warn("Erro to check if connection is closed. Reason: " + sqle.getMessage());
        //        }
        //        return closed;
    }
    
    @Override
    public boolean isAutoCommit() throws SQLException
    {
        //        boolean isAuto = false;
        //        try
        //        {
        return conn.getAutoCommit();
        //        }
        //        catch (SQLException sqle)
        //        {
        //            LOG.warn("Erro to check if connection is auto-commit. Reason: " + sqle.getMessage());
        //        }
        //        return isAuto;
    }
    
    @Override
    public void autoCommitOn() throws SQLException
    {
        //        boolean autoOnChanged = false;
        //        try
        //        {
        conn.setAutoCommit(true);
        //            autoOnChanged = true;
        //        }
        //        catch (SQLException sqle)
        //        {
        //            LOG.error("Erro to change connection to auto-commit[true]. Reason: " + sqle.getMessage());
        //        }
        //        return autoOnChanged;
    }
    
    @Override
    public void autoCommitOff() throws SQLException
    {
        //        boolean autoOnChanged = false;
        //        try
        //        {
        conn.setAutoCommit(false);
        //            autoOnChanged = true;
        //        }
        //        catch (SQLException sqle)
        //        {
        //            LOG.error("Erro to change connection to auto-commit[false]. Reason: " + sqle.getMessage());
        //        }
        //        return autoOnChanged;
    }
    
    @Override
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    public <T, R> StatementAdapter<T, R> newStatement(Queryable queryable)
    {
        PreparedStatement stmt = prepareStatement(queryable);
        StatementAdapter<T, R> adapter = new net.sf.jkniv.whinstone.jdbc.statement.PreparedStatementAdapter(stmt,
                queryable);
        return adapter;
    }
    
    @Override
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    public <T, R> StatementAdapter<T, R> newStatement(String sql)
    {
        PreparedStatement stmt = null;
        StatementAdapter<T, R> adapter = null;
        try
        {
            stmt = conn.prepareStatement(sql);
            adapter = new net.sf.jkniv.whinstone.jdbc.statement.PreparedStatementAdapter(stmt, null);
        }
        catch (SQLException sqle)
        {
            throw new RepositoryException("Cannot prepare statement to [" + sql + "]", sqle);
        }
        return adapter;
    }
    
    @Override
    public Object getMetaData()
    {
        DatabaseMetaData metadata = null;
        try
        {
            metadata = conn.getMetaData();
        }
        catch (SQLException sqle)
        {
            LOG.error("Erro to read data base meta data. Reason: " + sqle.getMessage());
        }
        
        return metadata;
    }
    
    @Override
    public Object unwrap()
    {
        return conn;
    }
    
//    @Override
//    public boolean supportsPagingByRoundtrip()
//    {
//        return true;
//    }
    
    /**
     * Creates a PreparedStatement object that will generate ResultSet objects with the given type, concurrency, and holdability.
     * The parameters values is setting
     * @param conn Opened connection to database
     * @return a new PreparedStatement object, containing the pre-compiled SQL statement.
     * @throws net.sf.jkniv.sqlegance.RepositoryException wrapper SQLException
     * @see java.sql.SQLException
     */
    private PreparedStatement prepareStatement(Queryable queryable)
    {
        PreparedStatement stmt = null;
        Sql isql = queryable.getDynamicSql();
        if (LOG.isTraceEnabled())
            LOG.trace("Preparing SQL statement [{}] type [{}], concurrency [{}], holdability [{}] with [{}] parameters",
                    queryable.getName(), isql.getResultSetType(), isql.getResultSetConcurrency(), isql.getResultSetHoldability(),
                    queryable.getParamsNames().length);
        stmt = queryable.getDynamicSql().getSqlDialect().prepare(conn, isql, queryable.query());
        /*
        SqlDialect dialect = queryable.getDynamicSql().getSqlDialect();
        int rsType = isql.getResultSetType().getTypeScroll();
        int rsConcurrency = isql.getResultSetConcurrency().getConcurrencyMode();
        int rsHoldability = isql.getResultSetHoldability().getHoldability();
        
        try
        {
            if (queryable.getDynamicSql().isInsertable())
            {
                Insertable insertTag = isql.asInsertable();
                if (insertTag.isAutoGenerateKey() && insertTag.getAutoGeneratedKey().isAutoStrategy())
                {
                    String[] columns = insertTag.getAutoGeneratedKey().getColumnsAsArray();
                    stmt = conn.prepareStatement(queryable.query(), columns);
                }
            }
            if (stmt == null)
            {
                if (dialect.supportsStmtHoldability())
                    stmt = conn.prepareStatement(queryable.query(), rsType, rsConcurrency, rsHoldability);
                else
                {
                    // SQLServer doesn't support Holdability
                    stmt = conn.prepareStatement(queryable.query(), rsType, rsConcurrency);
                    if (dialect.supportsConnHoldability())
                        conn.setHoldability(rsHoldability);
                }
            }
            if (isql.getTimeout() > 0)
                stmt.setQueryTimeout(isql.getTimeout());
        }
        catch (SQLException sqle)
        {
            throw new RepositoryException("Cannot prepare statement [" + sqle.getMessage() + "]", sqle);
        }*/
        return stmt;
    }
    
    /**
     * Creates a {@code PreparedStatement} object object capable of returning the auto-generated 
     * keys designated by the given array.
     * 
     * @param conn Opened connection to database
     * @param columnNames an array of column names indicating the columns that should be returned from the inserted row or rows 
     * @return a new PreparedStatement object, containing the pre-compiled SQL statement.
     * @throws net.sf.jkniv.sqlegance.RepositoryException wrapper SQLException
     * @see java.sql.SQLException
     */
    private PreparedStatement prepareStatement(Queryable queryable, String[] columnNames)
    {
        PreparedStatement stmt = null;
        Sql isql = queryable.getDynamicSql();
        try
        {
            if (LOG.isTraceEnabled())
                LOG.trace("Preparing SQL statement type with [{}] column names", queryable.getParamsNames().length);
            
            if (columnNames.length > 0)
                stmt = conn.prepareStatement(queryable.query(), columnNames);
            else
                stmt = conn.prepareStatement(queryable.query(), Statement.RETURN_GENERATED_KEYS);
            
            if (isql.getTimeout() > 0)
                stmt.setQueryTimeout(isql.getTimeout());
        }
        catch (SQLException sqle)
        {
            throw new RepositoryException("Cannot prepare statement!", sqle);
        }
        return stmt;
    }
    
    @Override
    public <T, R> Command asUpdateCommand(Queryable queryable)
    {
        Command command = null;
        if (queryable.getDynamicSql().isBatch() || queryable.isTypeOfBulk())
            command = new BulkJdbcCommand(this.newStatement(queryable), queryable, this.conn);
        else
            command = new DefaultJdbcCommand(this.newStatement(queryable), queryable, this.conn);
        
        return command;
    }
    
    @Override
    public <T, R> Command asDeleteCommand(Queryable queryable)
    {
        Command command = null;
        if (queryable.getDynamicSql().isBatch() || queryable.isTypeOfBulk())
            command = new BulkJdbcCommand(this.newStatement(queryable), queryable, this.conn);
        else
            command = new DefaultJdbcCommand(this.newStatement(queryable), queryable, this.conn);
        
        return command;
    }
    
    @Override
    public <T, R> Command asAddCommand(Queryable queryable)
    {
        Command command = null;
        Insertable sql = queryable.getDynamicSql().asInsertable();
        if (sql.isAutoGenerateKey())
        {
            if (sql.getAutoGeneratedKey().isAutoStrategy())
            {
                command = new AddAutoKeyJdbcCommand(queryable, this.conn);
            }
            else if (sql.getAutoGeneratedKey().isSequenceStrategy())
            {
                command = new AddSequenceKeyJdbcCommand(queryable, this.conn);
            }
        }
        else if (queryable.getDynamicSql().isBatch() || queryable.isTypeOfBulk())
            command = new BulkJdbcCommand(this.newStatement(queryable), queryable, this.conn);
        else
            command = new DefaultJdbcCommand(this.newStatement(queryable), queryable, this.conn);
        
        return command;
    }
    
    @Override
    public <T, R> Command asSelectCommand(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        Command command = null;
        StatementAdapter<Number, ResultSet> adapterStmtCount = null;
        if (queryable.isPaging())
            adapterStmtCount = newStatement(queryable.queryCount());
        
        StatementAdapter<T, R> stmt = this.newStatement(queryable);
        Selectable select = queryable.getDynamicSql().asSelectable();
        stmt.resultRow(overloadResultRow).oneToManies(select.getOneToMany()).groupingBy(select.getGroupByAsList());
        
        command = new DefaultJdbcQuery(stmt, queryable, this.conn);
        return command;
    }
    
}
