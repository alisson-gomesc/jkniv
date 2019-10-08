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

/**
 * Represents the transaction type over EIS or database systems
 * 
 * @author Alisson Gomes
 *
 */
public enum TransactionType
{
    /** JTA - Global transaction, delimit JTA transactions using UserTransaction interface, can span multiple databases and processes*/
    GLOBAL,  
    /** JDBC - Local transaction, delimit JDBC transactions using connection interface, are native to a single database, use non-XA data sources, and are restricted within a single process. */
    LOCAL,
    /** EJB - Means the transaction it's controlled by JEE <b>container-managed</b> or <b>bean-managed</b> transaction demarcation */
    EJB;
    
    public static TransactionType get(String s)
    {
        TransactionType answer = TransactionType.LOCAL;
        for(TransactionType type : TransactionType.values())
        {
            if (type.name().equalsIgnoreCase(s))
                answer = type;
        }
        return answer;
    }

}
