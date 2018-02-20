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

import javax.persistence.EntityTransaction;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.sqlegance.transaction.TransactionStatus;
import net.sf.jkniv.sqlegance.transaction.TransactionType;
import net.sf.jkniv.sqlegance.transaction.Transactional;

public class JpaTransactionAdapter implements Transactional
{
    private final static  Assertable notNull = AssertsFactory.getNotNull();
    private final EntityTransaction tx;
    private TransactionStatus status;
    
    /**
     * Create transaction adapter for JPA Entity Transaction
     * @param tx control transactions on resource-local entity managers
     */
    public JpaTransactionAdapter(EntityTransaction  tx)
    {
        notNull.verify(tx);
        this.tx = tx;
        this.status = (tx.isActive() ? TransactionStatus.ACTIVE : TransactionStatus.NO_TRANSACTION);
    }

    @Override
    public TransactionType geTransactionType()
    {
        return TransactionType.LOCAL;
    }

    @Override
    public TransactionStatus getStatus()
    {
        return status;
    }

    @Override
    public void begin()
    {
        this.tx.begin();
        this.status = TransactionStatus.ACTIVE;
    }

    @Override
    public void commit()
    {
        this.tx.commit();
        this.status = TransactionStatus.COMMITTED;
    }

    @Override
    public void rollback()
    {
        this.tx.rollback();
        this.status = TransactionStatus.ROLLEDBACK;
    }
    
    @Override
    public String toString()
    {
        return "JpaTransactionAdapter [EntityTransaction=" + tx + ", status=" +status+ "]";
    }


}
