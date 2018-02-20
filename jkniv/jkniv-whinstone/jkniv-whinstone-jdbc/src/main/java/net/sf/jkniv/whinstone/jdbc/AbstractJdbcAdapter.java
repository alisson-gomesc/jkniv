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
package net.sf.jkniv.whinstone.jdbc;

import java.sql.CallableStatement;
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
import net.sf.jkniv.sqlegance.ConnectionFactory;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.sqlegance.transaction.TransactionContext;
import net.sf.jkniv.sqlegance.transaction.TransactionSessions;
import net.sf.jkniv.sqlegance.transaction.TransactionStatus;
import net.sf.jkniv.sqlegance.transaction.Transactional;
import net.sf.jkniv.whinstone.jdbc.transaction.LocalTransactionAdapter;

abstract class AbstractJdbcAdapter implements ConnectionFactory
{
    protected transient Logger                LOG         = LoggerFactory.getLogger(getClass());
    protected static final HandleableException handlerException = new HandlerException(RepositoryException.class,
            "Cannot get database connection");
    //protected static final boolean             isTraceEnabled   = LOG.isTraceEnabled();
    
    protected String                           contextName;
    
    public AbstractJdbcAdapter(String contextName)
    {
        this.contextName = contextName;
        //this.LOGSQL = sqlLogger;
    }
    
    @Override
    public String getContextName()
    {
        return contextName;
    }
    
    @Override
    public Transactional getTransactionManager()
    {
        return new LocalTransactionAdapter(this);
    }
    
    @Override
    public void close(ConnectionAdapter conn)
    {
        TransactionStatus status = TransactionStatus.NO_TRANSACTION;
        TransactionContext transactionCtx = TransactionSessions.get(contextName);
        if (transactionCtx != null)
            status = transactionCtx.getStatus();
        
        boolean mustClose = ((status == TransactionStatus.COMMITTED || status == TransactionStatus.ROLLEDBACK
                || status == TransactionStatus.NO_TRANSACTION));
        if (conn != null && mustClose)
        {
            try
            {
                if (!conn.isClosed())
                {
                    if(LOG.isDebugEnabled())
                        LOG.debug("Closing connection [{}] with transaction status [{}], auto-commit[{}]", conn, status,
                                conn.isAutoCommit());
                    conn.close();
                }
            }
            catch (SQLException sqle)
            {
                LOG.warn("Cannot close connection. Reason: " + sqle.getMessage());
            }
        }
        //else if (LOG.isTraceEnabled())
        //    LOG.trace("Connection [{}] is not closed because transaction status is [{}]", conn, status);
    }
    
    @Override
    public void close(PreparedStatement stmt)
    {
        if (stmt != null)
        {
            try
            {
                stmt.close();
            }
            catch (SQLException sqle)
            {
                LOG.warn("Cannot close prepared statement. Reason: " + sqle.getLocalizedMessage());
            }
            stmt = null;
        }
    }
    
    @Override
    public void close(Statement stmt)
    {
        if (stmt != null)
        {
            try
            {
                stmt.close();
            }
            catch (SQLException sqle)
            {
                LOG.warn("Cannot close statement. Reason: " + sqle.getLocalizedMessage());
            }
            stmt = null;
        }
    }
    
    @Override
    public void close(ResultSet rs)
    {
        if (rs != null)
        {
            try
            {
                rs.close();
            }
            catch (SQLException sqle)
            {
                LOG.warn("Cannot close resultset. Reason: " + sqle.getLocalizedMessage());
            }
            rs = null;
        }
    }
    
    @Override
    public void close(CallableStatement call)
    {
        
        if (call != null)
        {
            try
            {
                call.close();
            }
            catch (SQLException sqle)
            {
                LOG.warn("Cannot close callable. Reason: " + sqle.getLocalizedMessage());
            }
            call = null;
        }
    }
    
    protected void setIsolation(Connection conn, Isolation isolation)
    {
        try
        {
            //if (conn.getAutoCommit()) TODO log level review
            //    LOG.warn("Connection [{}] working with autocommit=true it's not recommended!", conn);
            
            if (isolation != null && isolation != Isolation.DEFAULT
                    && supportsTransactionIsolationLevel(conn, isolation))
                conn.setTransactionIsolation(isolation.level());
        }
        catch (SQLException e)
        {
            try
            {
                handlerException.handle(e, "Cannot change Transaction isolation level from ["
                        + Isolation.get(conn.getTransactionIsolation()) + "] to [" + isolation + "]");
            }
            catch (SQLException sqle)
            {
                LOG.warn("Cannot read transaction isolation level. reason: " + sqle.getLocalizedMessage(), sqle);
            }
        }
    }
    
    protected boolean supportsTransactionIsolationLevel(Connection conn, Isolation isolation)
    {
        boolean supports = false;
        try
        {
            DatabaseMetaData metaData = conn.getMetaData();
            supports = metaData.supportsTransactionIsolationLevel(isolation.level());
        }
        catch (SQLException e)
        {
            LOG.warn("Database doesn't support Connection.getMetaData(): ", e.getMessage());
        }
        return supports;
    }
}
