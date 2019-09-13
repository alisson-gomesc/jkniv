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

import net.sf.jkniv.sqlegance.transaction.TransactionType;

public interface Transactional
{
    /*
    //Transaction getTransaction();
    /**
     * A call to the TransactionManager.suspend method temporarily suspends the transaction 
     * that is currently associated with the calling thread. If the thread is not associated 
     * with any transaction, a null object reference is returned; otherwise, a valid Transaction 
     * object is returned. The Transaction object can later be passed to the resume method to 
     * reinstate the transaction context association with the calling thread.
     * @return
     *
    Transaction suspend();
    
    /**
     * The TransactionManager.resume method re-associates the specified transaction context with 
     * the calling thread. If the transaction specified is a valid transaction, the transaction 
     * context is associated with the calling thread; otherwise, the thread is associated with 
     * no transaction.
     * 
     * If TransactionManager.resume is invoked when the calling thread is already associated with 
     * another transaction, the transaction manager throws the IllegalStateException exception.
     * 
     * @param transaction
     * 
     *
    void resume(Transaction transaction);
    //void setRollbackOnly();
    */
    
    /**
     * The TransactionManager.begin method starts a global transaction and associates 
     * the transaction context with the calling thread.
     * 
     * If the TransactionManager implementation does not support nested transactions, 
     * the TransactionManager.begin method throws the NotSupportedException when the 
     * calling thread is already associated with a transaction.
     * 
     * The TransactionManager.getTransaction method returns the Transaction object that 
     * represents the transaction context currently associated with the calling thread. 
     * This Transaction object can be used to perform various operations on the target transaction. 
     * Examples of Transaction object operations are resource enlistment and synchronization registration. 
     * @throws TransactionException TODO doc me when some error occurs
     */
    public void begin(); 
    
    /***
     * The TransactionManager.commit method completes the transaction currently associated 
     * with the calling thread. After the commit method returns, the calling thread is not 
     * associated with a transaction. If the commit method is called when the thread is not 
     * associated with any transaction context, the TransactionManager throws an exception. 
     * In some implementations, the commit operation is restricted to the transaction originator only. 
     * If the calling thread is not allowed to commit the transaction, the TransactionManager throws an exception.
     * @throws TransactionException TODO doc me when some error occurs
     */
    void commit();
    
    /**
     * The TransactionManager.rollback method rolls back the transaction associated with the current thread. 
     * After the rollback method completes, the thread is associated with no transaction.
     * @throws TransactionException TODO doc me when some error occurs
     */
    void rollback();
    
    TransactionStatus getStatus();
    
    /**
     * Type of transactions supported by the repository
     * @return type of transaction
     */
    TransactionType geTransactionType();
    
    /*
     * Modify the timeout value that is associated with transactions started
     * by the current thread with the begin method.
     *
     * <p> If an application has not called this method, the transaction
     * service uses some default value for the transaction timeout.
     *
     * @param seconds The value of the timeout in seconds. If the value is zero,
     *        the transaction service restores the default value. If the value
     *        is negative a SystemException is thrown.
     *
     * @exception TransactionException Thrown if the transaction manager
     *    encounters an unexpected error condition.
     *
    void setTransactionTimeout(int seconds) throws TransactionException;
     */
}
