package net.sf.jkniv.whinstone.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.sqlegance.ConnectionAdapter;
import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.statement.ResultSetConcurrency;
import net.sf.jkniv.sqlegance.statement.ResultSetHoldability;
import net.sf.jkniv.sqlegance.statement.ResultSetType;
import net.sf.jkniv.sqlegance.statement.StatementAdapter;

public class JdbcConnectionAdapter implements ConnectionAdapter
{
    private static final Logger LOG = LoggerFactory.getLogger(JdbcConnectionAdapter.class);
    private static final HandleableException handlerException = new HandlerException(RepositoryException.class,
            "Exception at connection session running %s");

    private Connection conn;
    
    public JdbcConnectionAdapter(Connection conn)
    {
        this.conn = conn;
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
    public void close() throws SQLException
    {
//        try
//        {
            this.conn.close();
//        }
//        catch (SQLException sqle)
//        {
//            LOG.warn("Erro to closing connection. Reason: " + sqle.getMessage());
//        }        
    }
    
    @Override
    public boolean isClosed()  throws SQLException
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
    public <T, R> StatementAdapter<T, R> newStatement(Queryable queryable)
    {
        PreparedStatement stmt = prepareStatement(queryable);
        StatementAdapter<T, R> adapter = new net.sf.jkniv.whinstone.jdbc.statement.PreparedStatementAdapter(stmt);
        
        //AutoBindParams prepareParams = PrepareParamsFactory.newPrepareParams(adapter, queryable);
        //prepareParams.parameterized(queryable.getParamsNames());
        
        return adapter;
    }
    
    @Override
    public <T, R> StatementAdapter<T, R> newStatement(String sql)
    {
        PreparedStatement stmt = null;
        StatementAdapter<T, R> adapter = null;
        try
        {
            stmt = conn.prepareStatement(sql);
            adapter = new net.sf.jkniv.whinstone.jdbc.statement.PreparedStatementAdapter(stmt);
            /*
                StatementAdapterOld stmtAdapterCount = new PreparedStatementAdapterOld(stmtCount,stmtStrategy.getSqlLogger());
                AutoBindParams prepareParamsCount = PrepareParamsFactory.newPrepareParams(stmtAdapterCount,isql.getParamParser(), sqlDialect.getQueryable());
                prepareParamsCount.parameterized(sqlDialect.getParamsNames());
                rsCount = stmtCount.executeQuery();
             */
        }
        catch (SQLException sqle)
        {
            throw new RepositoryException(
                    "Cannot prepare statement to [" + sql + "]!", sqle);
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
        int rsType = ResultSet.TYPE_FORWARD_ONLY;
        int rsConcurrency = ResultSet.CONCUR_READ_ONLY;
        int rsHoldability = ResultSet.CLOSE_CURSORS_AT_COMMIT;
        Sql isql = queryable.getDynamicSql();
        if (isql.getResultSetType() == ResultSetType.TYPE_SCROLL_INSENSITIVE)
            rsType = ResultSet.TYPE_SCROLL_INSENSITIVE;
        else if (isql.getResultSetType() == ResultSetType.TYPE_SCROLL_SENSITIVE)
            rsType = ResultSet.TYPE_SCROLL_SENSITIVE;
        
        if (isql.getResultSetConcurrency() == ResultSetConcurrency.CONCUR_UPDATABLE)
            rsConcurrency = ResultSet.CONCUR_UPDATABLE;
        if (isql.getResultSetHoldability() == ResultSetHoldability.HOLD_CURSORS_OVER_COMMIT)
            rsHoldability = ResultSet.HOLD_CURSORS_OVER_COMMIT;
        
        try
        {
            if(LOG.isTraceEnabled())
                LOG.trace("Preparing SQL statement type [{}], concurrency [{}], holdability [{}] with [{}] parameters", 
                        isql.getResultSetType(), 
                        isql.getResultSetConcurrency(), 
                        isql.getResultSetHoldability(), 
                        queryable.getParamsNames().length);

            
            if(queryable.getDynamicSql().isInsertable())
            {
                Insertable insertTag = isql.asInsertable();
                if (insertTag.isAutoGenerateKey() && insertTag.getAutoGeneratedKeyTag().isAutoStrategy())
                {
                    String[] columns = insertTag.getAutoGeneratedKeyTag().getColumnsAsArray();
                    stmt = conn.prepareStatement(queryable.query(), columns);
                }
            }
            else
            {
                if (queryable.getDynamicSql().getSqlDialect().supportsStmtHoldability())
                    stmt = conn.prepareStatement(queryable.query(), rsType, rsConcurrency, rsHoldability);
                else 
                {
                    // SQLServer doesn't support Holdability
                    stmt = conn.prepareStatement(queryable.query(), rsType, rsConcurrency);
                    conn.setHoldability(rsHoldability);
                }
            }
            if (isql.getTimeout() > 0)
                stmt.setQueryTimeout(isql.getTimeout());
        }
        catch (SQLException sqle)
        {
            throw new RepositoryException("Cannot prepare statement ["+sqle.getMessage()+"]", sqle);
        }
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
            if(LOG.isTraceEnabled())
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



}
