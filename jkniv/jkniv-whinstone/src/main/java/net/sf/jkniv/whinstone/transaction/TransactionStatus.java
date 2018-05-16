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

/**
 * The Status interface defines static variables used for transaction 
 * status codes.
 */
public enum TransactionStatus
{
    /**
     * A transaction is associated with the target object and it is in the
     * active state. An implementation returns this status after a
     * transaction has been started and prior to a Coordinator issuing
     * any prepares, unless the transaction has been marked for rollback.
     */
    ACTIVE {
        public int value() { return 0; }
    },
    
    /**
     * A transaction is associated with the target object and it has been
     * marked for rollback, perhaps as a result of a setRollbackOnly operation.
     */
    MARKED_ROLLBACK {
        public int value() { return 1; }
    },
    
    /**
     * A transaction is associated with the target object and it has been
     * prepared. That is, all subordinates have agreed to commit. The
     * target object may be waiting for instructions from a superior as to how
     * to proceed.
     */
    PREPARED {
        public int value() { return 2; }
    },
    
    /**
     * A transaction is associated with the target object and it has been
     * committed. It is likely that heuristics exist; otherwise, the
     * transaction would have been destroyed and NoTransaction returned.
     */
    COMMITTED {
        public int value() { return 3; }
    },
    
    /**
     * A transaction is associated with the target object and the outcome
     * has been determined to be rollback. It is likely that heuristics exist;
     * otherwise, the transaction would have been destroyed and NoTransaction
     * returned.
     */
    ROLLEDBACK {
        public int value() { return 4; }
    },
    
    /**
     * A transaction is associated with the target object but its
     * current status cannot be determined. This is a transient condition
     * and a subsequent invocation will ultimately return a different status.
     */
    UNKNOWN {
        public int value() { return 5; }
    },
    
    /**
     * No transaction is currently associated with the target object. This
     * will occur after a transaction has completed.
     */
    NO_TRANSACTION {
        public int value() {return 6; }
    },
        
    /**
     * A transaction is associated with the target object and it is in the
     * process of preparing. An implementation returns this status if it
     * has started preparing, but has not yet completed the process. The
     * likely reason for this is that the implementation is probably
     * waiting for responses to prepare from one or more Resources.
     */
    PREPARING {
        public int value() { return 7; }
    },
    
    /**
     * A transaction is associated with the target object and it is in the
     * process of committing. An implementation returns this status if it
     * has decided to commit but has not yet completed the committing process. 
     * This occurs because the implementation is probably waiting for 
     * responses from one or more Resources.
     */
    COMMITTING {
        public int value() { return 8; }
    },
    
    /**
     * A transaction is associated with the target object and it is in the
     * process of rolling back. An implementation returns this status if
     * it has decided to rollback but has not yet completed the process.
     * The implementation is probably waiting for responses from one or more
     * Resources.
     */
    ROLLING_BACK {
        public int value() { return 9; }
    };
    
    /**
     * The javax.transaction.Status constant code.
     * @return Return the javax.transaction.Status code.
     */
    public abstract int value();
    
}
