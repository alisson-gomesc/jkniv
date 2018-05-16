/* 
 * JKNIV, SQLegance keeping queries maintainable.
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
package net.sf.jkniv.whinstone.transaction;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.whinstone.ConnectionAdapter;

/**
 * Represent a set of transactions for thread, where one thread can use many TransactionContexts.
 * 
 * Responsible to release (close) the connection with data source.
 * 
 * @author Alisson Gomes
 *
 */
public class TransactionSessions
{
    private static final Logger                                       LOG       = LoggerFactory
            .getLogger(TransactionSessions.class);
    private static final ThreadLocal<Map<String, TransactionContext>> resources = new ThreadLocal<Map<String, TransactionContext>>(); //"Transactional resources"
    
    public static boolean isEmpty(String contextName)
    {
        return (get(contextName) == null);
    }
    
    public static TransactionContext get(String contextName)
    {
        Map<String, TransactionContext> context = resources.get();
        if (context == null)
            return null;
        
        //throw new TransactionException("There is no current transaction session named ["+name+"]");
        return context.get(contextName);
    }
    
    public static TransactionContext set(String contextName, ConnectionAdapter conn)
    {
        //final int timeout = 30;
        try
        {
            if (conn == null)
                throw new TransactionException("Cannot set a null connection to transaction context");
            if (conn.isClosed())
                throw new TransactionException("Cannot set a closed connection to transaction context");
        }
        catch (SQLException sqle)
        {
            throw new TransactionException(sqle);// TODO handler exception
        }
        Map<String, TransactionContext> transactionContext = resources.get();
        if (transactionContext == null)
        {
            transactionContext = new HashMap<String, TransactionContext>();
            resources.set(transactionContext);
        }
        if (transactionContext.isEmpty() || !transactionContext.containsKey(contextName))
        {
            transactionContext.put(contextName, new TransactionContext(contextName, conn));
        }
        else if (transactionContext.containsKey(contextName))
            throw new TransactionException("Already exists a connection bind to context name [" + contextName + "] for this thread!");
        
        return transactionContext.get(contextName);
    }
    
    private static void setTransactionStatus(String contextName, TransactionStatus status)
    {
        TransactionContext txCtx = get(contextName);
        LOG.trace("Context[{}] setting transaction status from [{}] to [{}]", contextName, txCtx.getStatus(), status);
        
        txCtx.setStatus(status);
        if (status == TransactionStatus.NO_TRANSACTION)
        {
            //setActualTransactionInactive();
        }
        else if (status == TransactionStatus.PREPARING)
        {
            
        }
        else if (status == TransactionStatus.PREPARED)
        {
            
        }
        else if (status == TransactionStatus.ACTIVE)
        {
            //setActualTransactionActive();
        }
        else if (status == TransactionStatus.COMMITTING)
        {
            
        }
        else if (status == TransactionStatus.COMMITTED)
        {
            close(contextName);
        }
        else if (status == TransactionStatus.MARKED_ROLLBACK)
        {
            
        }
        else if (status == TransactionStatus.ROLLING_BACK)
        {
            
        }
        else if (status == TransactionStatus.ROLLEDBACK)
        {
            close(contextName);
        }
        else if (status == TransactionStatus.UNKNOWN)
        {
            
        }
    }
    
    public static void close(String contextName)
    {
//  ACTIVE | MARKED_ROLLBACK | PREPARED | COMMITED | ROLLBACK | UNKNOW | NO_TRANSACTION | PREPARING | COMMITING | ROLLING_BACK
// release when: MARKED_ROLLBACK | ROLLING_BACK | ROLLBACK | PREPARED | COMMITED | COMMITING
        TransactionContext transactionContext = get(contextName);
        if (transactionContext.getStatus() == TransactionStatus.COMMITTED || 
            transactionContext.getStatus() == TransactionStatus.ROLLEDBACK)
            releaseResource(contextName);
        
        else if (transactionContext.getStatus() == TransactionStatus.MARKED_ROLLBACK || 
            transactionContext.getStatus() == TransactionStatus.PREPARING)
        {
            LOG.warn("Be careful transaction was finished with status [{}]", transactionContext.getStatus());
            releaseResource(contextName);
        }
        else
        {
            // FIXME behavior pendent, whats happens another status?
            //throw new TransactionException(" exists a connection bind to context name [" + name + "] for this thread!");
        }
        //setTransactionStatus(TransactionStatus.NO_TRANSACTION);
    }
    
    private static void releaseResource(String contextName)
    {
        try
        {
            TransactionContext txContext = get(contextName);
            txContext.getConnection().close();
            Map<String, TransactionContext> contexts = resources.get();
            if (contexts.size() == 1) //
            {
                contexts.clear();
                resources.remove();
            }
            else
            {
                contexts.remove(contextName);
            }
        }
        catch (SQLException sqle)
        {
            LOG.warn("Erro to closing connection. Reason: " + sqle.getMessage());
        }
        finally
        {
            resources.remove();
        }
    }
    
}
