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
package net.sf.jkniv.sqlegance.transaction;

import java.sql.Connection;

/**
 * Enumeration that represents transaction isolation levels for use with the connection data source.
 * <ul>
 *  <li>NONE</li>
 *  <li>READ_UNCOMMITTED</li>
 *  <li>READ_COMMITTED</li>
 *  <li>REPEATABLE_READ</li>
 *  <li>SERIALIZABLE</li>
 * </ul>
 * @author Alisson Gomes
 *
 */
public enum Isolation
{
    /** Use the default isolation level of the underlying connection data source. 
     * This level cannot be set at connection isolation.*/
    DEFAULT {
        /**
         * @throws UnsupportedOperationException
         */
        public int level() { throw new UnsupportedOperationException("java.sql.Connection haven't this isolation level. Get a valid level."); }
    }, 
    /** Transactions are not supported */
    NONE {
        public int level() { return Connection.TRANSACTION_NONE; }
    },
    /** Dirty reads, non-repeatable reads and phantom reads can occur. */
    READ_UNCOMMITTED {
        public int level() { return Connection.TRANSACTION_READ_UNCOMMITTED; }
    },
    /** Dirty reads are prevented; non-repeatable reads and phantom reads can occur. */
    READ_COMMITTED {
        public int level() { return Connection.TRANSACTION_READ_COMMITTED; }
    },
    /** Dirty reads and non-repeatable reads are prevented; phantom reads can occur. */
    REPEATABLE_READ {
        public int level() { return Connection.TRANSACTION_REPEATABLE_READ; }
    },
    /** Dirty reads, non-repeatable reads and phantom reads are prevented. */
    SERIALIZABLE {
        public int level() { return Connection.TRANSACTION_SERIALIZABLE; }
    };
    
    public abstract int level();
    
    public static Isolation get(int level)
    {
        Isolation isolation = Isolation.DEFAULT;
        switch (level)
        {
            case Connection.TRANSACTION_NONE:
                isolation = NONE;
                break;
            case Connection.TRANSACTION_READ_COMMITTED:
                isolation = READ_COMMITTED;
                break;
            case Connection.TRANSACTION_READ_UNCOMMITTED:
                isolation = READ_UNCOMMITTED;
                break;
            case Connection.TRANSACTION_REPEATABLE_READ:
                isolation = Isolation.REPEATABLE_READ;
                break;
            case Connection.TRANSACTION_SERIALIZABLE:
                isolation = SERIALIZABLE;
                break;
        }
        return isolation;
    }
    
    /**
     * @param type String that represent enum ignoring case
     * @return the value of <code>type</code>, type of not found return <code>DEFAULT</code> enum. 
     */
    public static Isolation get(String type)
    {
        Isolation isolation = Isolation.DEFAULT;
        for (Isolation iso : Isolation.values())
        {
            if (String.valueOf(type).equalsIgnoreCase(iso.toString()))
            {
                isolation = iso;
                break;
            }
        }
        return isolation;
    }

}
