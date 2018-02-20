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

import net.sf.jkniv.sqlegance.ConnectionAdapter;
import net.sf.jkniv.sqlegance.transaction.TransactionContext;
import net.sf.jkniv.sqlegance.transaction.TransactionException;
import net.sf.jkniv.sqlegance.transaction.TransactionScope;
import net.sf.jkniv.sqlegance.transaction.TransactionSessions;
import net.sf.jkniv.sqlegance.transaction.TransactionStatus;
import net.sf.jkniv.sqlegance.transaction.Transactional;

public abstract class AbstractTransaction implements Transactional
{
    protected transient Logger       logger = LoggerFactory.getLogger(getClass());
    private boolean                  wasAutoCommit;
    protected final TransactionScope transactionScope;
    protected final String           contextName;
    
    /**
     * Open new connection with default connection properties from driver version.
     * @return new Connection
     */
    public abstract ConnectionAdapter open();
    
    public AbstractTransaction(String contextName, TransactionScope transactionScope)
    {
        this.contextName = contextName;
        this.transactionScope = transactionScope;// FIXME tx implements different scopes transactions
        this.wasAutoCommit = false;
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
        
        transactionContext.setStatus(TransactionStatus.PREPARING);
        ConnectionAdapter conn = transactionContext.getConnection();
        try
        {
            this.wasAutoCommit = conn.isAutoCommit();
            if (wasAutoCommit)
            {
                conn.autoCommitOff();
                transactionContext.setStatus(TransactionStatus.ACTIVE);
                if (logger.isDebugEnabled())
                    logger.debug("Transaction [{}] with scope [{}] ACTIVE", this.contextName, transactionScope);
            }
            else
                transactionContext.setStatus(TransactionStatus.ACTIVE);
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
        ConnectionAdapter conn = transactionContext.getConnection();
        try
        {
            if (!transactionContext.isActive())
                throw new TransactionException("Does not have transaction beginning. Autocommit is true!");
            
            conn.commit();
            transactionContext.setStatus(TransactionStatus.COMMITTED);
            if (logger.isDebugEnabled())
                logger.debug("Transaction [{}] with scope [{}] COMMITTED", this.contextName, transactionScope);
            
        }
        catch (SQLException sqle)
        {
            transactionContext.setStatus(TransactionStatus.MARKED_ROLLBACK);
            throw new TransactionException("Cannot commit transaction", sqle);
        }
        finally
        {
            try
            {
                if (wasAutoCommit)
                    conn.autoCommitOn();
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
        return getTransactionContext().getStatus();
    }
    
    @Override
    public final void rollback()
    {
        if (logger.isTraceEnabled())
            logger.trace("Rolling back transaction [{}] with scope [{}]", this.contextName, transactionScope);
        
        TransactionContext transactionContext = getTransactionContext();
        ConnectionAdapter conn = transactionContext.getConnection();
        if (conn == null)
        {
            logger.warn("Try to rollback transaction at context [{}] without connection!", this.contextName);
            return;
        }
        try
        {
            conn.rollback();
            transactionContext.setStatus(TransactionStatus.ROLLEDBACK);
            if (logger.isDebugEnabled())
                logger.debug("Transaction [{}] with scope [{}] ROLLBACK", this.contextName, transactionScope);
        }
        catch (SQLException sqle)
        {
            transactionContext.setStatus(TransactionStatus.MARKED_ROLLBACK);
            logger.warn("Fail to try rollback transaction. Reason: " + sqle.getLocalizedMessage());
            throw new TransactionException("Cannot rollback transaction", sqle);
        }
        finally
        {
            try
            {
                if (wasAutoCommit && !conn.isAutoCommit())
                    conn.autoCommitOn();
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
            ConnectionAdapter conn = open();
            transactionContext = TransactionSessions.set(this.contextName, conn);
        }
        return transactionContext;
    }
    
    //    @Override
    //    public final void setTransactionTimeout(int seconds) throws TransactionException
    //    {
    //        // TODO Auto-generated method stub
    //    }
    
    /*
    public Transaction getTransaction() //throws SystemException
    {
        
        // TODO Auto-generated method stub
        return null;
    }
    
    public void resume(Transaction transaction)//        throws InvalidTransactionException, IllegalStateException, SystemException
    {
        // TODO Auto-generated method stub
        
    }
    
    public void setRollbackOnly() //throws IllegalStateException, SystemException
    {
        // TODO Auto-generated method stub
        
    }
    
    public void setTransactionTimeout(int timeout) //throws SystemException
    {
        // TODO Auto-generated method stub
        
    }
    
    public Transaction suspend() //throws SystemException
    {
        // TODO Auto-generated method stub
        return null;
    }
    */
    
}
