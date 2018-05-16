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
package net.sf.jkniv.whinstone.jpa2.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.transaction.TransactionType;
import net.sf.jkniv.whinstone.transaction.TransactionStatus;
import net.sf.jkniv.whinstone.transaction.Transactional;

public class EmptyTransactionAdapter implements Transactional
{
    private final static Logger LOG = LoggerFactory.getLogger(EmptyTransactionAdapter.class);
    
    private final static EmptyTransactionAdapter EMPTY_TRANSACTION_ADAPTER = new EmptyTransactionAdapter();
   
    public static Transactional empty() {
        return EMPTY_TRANSACTION_ADAPTER;
    }
    /**
     * Create an empty transaction adapter for JPA Entity Transaction
     */
    private EmptyTransactionAdapter()
    {
    }

    @Override
    public TransactionType geTransactionType()
    {
        return TransactionType.GLOBAL;
    }

    @Override
    public TransactionStatus getStatus()
    {
        return TransactionStatus.UNKNOWN;
    }

    @Override
    public void begin()
    {
        LOG.warn("{} container managed cannot BEGIN transaction use @TransactionAttribute(TransactionAttributeType.REQUIRED)", geTransactionType());
    }

    @Override
    public void commit()
    {
        LOG.warn("{} container managed cannot COMMIT transaction use @TransactionAttribute(TransactionAttributeType.REQUIRED)", geTransactionType());
    }

    @Override
    public void rollback()
    {
        LOG.warn("JPA container managed cannot ROLLBACK transaction use @TransactionAttribute(TransactionAttributeType.REQUIRED)");
    }

    @Override
    public String toString()
    {
        return "EmptyTransactionAdapter [Status=" + getStatus() + ", transactionType="+geTransactionType()+"]";
    }

}
