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

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.whinstone.ConnectionAdapter;
import net.sf.jkniv.whinstone.transaction.TransactionContext;
import net.sf.jkniv.whinstone.transaction.TransactionException;
import net.sf.jkniv.whinstone.transaction.TransactionScope;
import net.sf.jkniv.whinstone.transaction.TransactionSessions;
import net.sf.jkniv.whinstone.transaction.TransactionStatus;
import net.sf.jkniv.whinstone.transaction.Transactional;

public abstract class AbstractTransaction implements Transactional
{
    protected static final transient Logger   logger   = LoggerFactory.getLogger(AbstractTransaction.class);
    private static final transient Assertable NOT_NULL = AssertsFactory.getNotNull();
    private boolean                           wasAutoCommit;
    private TransactionStatus                 status;
    protected final TransactionScope          transactionScope;
    protected final String                    contextName;
    protected final ConnectionAdapter         connAdapter;
    
    /**
     * Open new connection with default connection properties from driver version.
     * @return new Connection
     */
    public abstract ConnectionAdapter open();
    
    public AbstractTransaction(String contextName, ConnectionAdapter connAdapter, TransactionScope transactionScope)
    {
        NOT_NULL.verify(contextName, connAdapter, transactionScope);
        this.contextName = contextName;
        this.connAdapter = connAdapter;
        this.transactionScope = transactionScope;// FIXME tx implements different scopes transactions
        try
        {
            this.wasAutoCommit = connAdapter.isAutoCommit();
        }
        catch (SQLException e)
        {
            throw new TransactionException("Could not create transaction for " + toString(), e);
        }
    }
    
    @Override
    public final void begin() //throws NotSupportedException, SystemException
    {
        if (logger.isTraceEnabled())
            logger.trace("Creating new transaction [{}] with scope [{}]", this.contextName, transactionScope);
        TransactionContext transactionContext = getTransactionContext();
        
        if (transactionContext.isActive())
            throw new TransactionException(
                    "Transaction is active. Local transaction does not support nested transactions");
        
        this.status = TransactionStatus.PREPARING;
        //ConnectionAdapter conn = transactionContext.getConnection();
        try
        {
            //this.wasAutoCommit = conn.isAutoCommit();
            if (wasAutoCommit)
            {
                connAdapter.autoCommitOff();
                this.status = TransactionStatus.ACTIVE;
                if (logger.isDebugEnabled())
                    logger.debug("Transaction [{}] with scope [{}] ACTIVE", this.contextName, transactionScope);
            }
            else
                this.status = TransactionStatus.ACTIVE;
        }
        catch (SQLException sqle)
        {
            //TransactionContext.setTransactionStatus(TransactionStatus.);
            throw new TransactionException("Cannot begin transaction", sqle);
        }
        finally
        {
            
        }
    }
    
    @Override
    public final void commit() //throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException
    {
        if (logger.isTraceEnabled())
            logger.trace("Committing transaction [{}] with scope [{}]", this.contextName, transactionScope);
        
        TransactionContext transactionContext = getTransactionContext();
        //transactionContext.setStatus(TransactionStatus.COMMITTING);
        //ConnectionAdapter conn = transactionContext.getConnection();
        try
        {
            if (!transactionContext.isActive())
                throw new TransactionException("Does not have transaction beginning. Autocommit is true!");
            
            connAdapter.commit();
            this.status = TransactionStatus.COMMITTED;
            if (logger.isDebugEnabled())
                logger.debug("Transaction [{}] with scope [{}] COMMITTED", this.contextName, transactionScope);
            
        }
        catch (SQLException sqle)
        {
            this.status = TransactionStatus.MARKED_ROLLBACK;
            throw new TransactionException("Cannot commit transaction", sqle);
        }
        finally
        {
            try
            {
                if (wasAutoCommit)
                    connAdapter.autoCommitOn();
            }
            catch (SQLException sqle)
            {
                logger.warn("Cannot change connection to autocommit=true. Reason: " + sqle.getMessage());
            }
            TransactionSessions.close(this.contextName);
        }
    }
    
    @Override
    public final TransactionStatus getStatus() //throws SystemException
    {
        return this.status;
        //return getTransactionContext().getStatus();
    }
    
    @Override
    public final void rollback()
    {
        if (logger.isTraceEnabled())
            logger.trace("Rolling back transaction [{}] with scope [{}]", this.contextName, transactionScope);
        
        TransactionContext transactionContext = getTransactionContext();
        //ConnectionAdapter conn = transactionContext.getConnection();
//        if (conn == null)
//        {
//            logger.warn("Try to rollback transaction at context [{}] without connection!", this.contextName);
//            return;
//        }
        try
        {
            connAdapter.rollback();
            this.status = TransactionStatus.ROLLEDBACK;
            if (logger.isDebugEnabled())
                logger.debug("Transaction [{}] with scope [{}] ROLLBACK", this.contextName, transactionScope);
        }
        catch (SQLException sqle)
        {
            this.status = TransactionStatus.MARKED_ROLLBACK;
            logger.warn("Fail to try rollback transaction. Reason: " + sqle.getLocalizedMessage());
            throw new TransactionException("Cannot rollback transaction", sqle);
        }
        finally
        {
            try
            {
                if (wasAutoCommit && !connAdapter.isAutoCommit())
                    connAdapter.autoCommitOn();
            }
            catch (SQLException sqle)
            {
                logger.warn("Cannot back auto-commit connection to TRUE. Reason: " + sqle.getMessage());
            }
            TransactionSessions.close(this.contextName);
        }
    }
    
    private TransactionContext getTransactionContext()
    {
        TransactionContext transactionContext = TransactionSessions.get(this.contextName);
        if (transactionContext == null)
        {
            //ConnectionAdapter conn = open();
            transactionContext = TransactionSessions.set(this.contextName, this, connAdapter);
        }
        return transactionContext;
    }
    
    @Override
    public String toString()
    {
        return getClass() + " [contextName=" + contextName + ", connAdapter=" + connAdapter + "]";
    }
    
    //    @Override
    //    public final void setTransactionTimeout(int seconds) throws TransactionException
    //    {
    //    }
    
    /*
    public Transaction getTransaction() //throws SystemException
    {
        
        return null;
    }
    
    public void resume(Transaction transaction)//        throws InvalidTransactionException, IllegalStateException, SystemException
    {
        
    }
    
    public void setRollbackOnly() //throws IllegalStateException, SystemException
    {
        
    }
    
    public void setTransactionTimeout(int timeout) //throws SystemException
    {
        
    }
    
    public Transaction suspend() //throws SystemException
    {
        return null;
    }
    */
    
}
