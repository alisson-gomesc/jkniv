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
package net.sf.jkniv.whinstone.transaction;

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
 * @since 0.6.0
 */
public class TransactionSessions
{
    private static final Logger                                       LOG       = LoggerFactory
            .getLogger(TransactionSessions.class);
    private static final ThreadLocal<Map<String, TransactionContext>> RESOURCES = new ThreadLocal<Map<String, TransactionContext>>(); //"Transactional resources"
    
    public static boolean isEmpty(String contextName)
    {
        return (get(contextName) == null);
    }
    
    public static TransactionContext get(String contextName)
    {
        Map<String, TransactionContext> context = RESOURCES.get();
        if (context == null)
            return null;
        
        //throw new TransactionException("There is no current transaction session named ["+name+"]");
        return context.get(contextName);
    }
    
    public static TransactionContext set(String contextName, Transactional tx, ConnectionAdapter conn)
    {
        if (tx == null)
            throw new TransactionException("Cannot set a null connection to transaction context");
        
        Map<String, TransactionContext> transactionContext = RESOURCES.get();
        if (transactionContext == null)
        {
            transactionContext = new HashMap<String, TransactionContext>();
            RESOURCES.set(transactionContext);
        }
        if (transactionContext.isEmpty() || !transactionContext.containsKey(contextName))
        {
            transactionContext.put(contextName, new TransactionContext(contextName, tx, conn));
        }
        else if (transactionContext.containsKey(contextName))
            throw new TransactionException("Already exists a connection bound to context name [" + contextName + "] for this thread. jkniv-whinstone-jdbc doesn't support multiple or nested transactions in same Thread!");
        
        return transactionContext.get(contextName);
    }
    
    public static void close(String contextName)
    {
//  ACTIVE | MARKED_ROLLBACK | PREPARED | COMMITED | ROLLBACK | UNKNOW | NO_TRANSACTION | PREPARING | COMMITING | ROLLING_BACK
// release when: MARKED_ROLLBACK | ROLLING_BACK | ROLLBACK | PREPARED | COMMITED | COMMITING
        TransactionContext transactionContext = get(contextName);
        TransactionStatus txStatus =transactionContext.getTransactional().getStatus(); 
        if (txStatus == TransactionStatus.COMMITTED ||  txStatus == TransactionStatus.ROLLEDBACK)
            releaseResource(contextName);
        
        else if (txStatus == TransactionStatus.MARKED_ROLLBACK || txStatus == TransactionStatus.PREPARING)
        {
            LOG.warn("Be careful transaction was finished with status [{}]", txStatus);
            releaseResource(contextName);
        }
        else// ACTIVE? PREPARED? UNKNOWN? NO_TRANSACTION? ? COMMITTING ? ROLLING_BACK?
        {
            LOG.error("Transaction was closed with status [{}]", txStatus);
            releaseResource(contextName);
            // FIXME transaction design, what's happens with status: ACTIVE? PREPARED? UNKNOWN? NO_TRANSACTION? ? COMMITTING ? ROLLING_BACK?
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
            Map<String, TransactionContext> contexts = RESOURCES.get();
            if (contexts.size() == 1) //
            {
                contexts.clear();
                RESOURCES.remove();
            }
            else
            {
                contexts.remove(contextName);
            }
        }
        finally
        {
            RESOURCES.remove();
        }
    }
}
