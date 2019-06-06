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

import net.sf.jkniv.whinstone.ConnectionAdapter;

/**
 * 
 * Hold the JDBC connection and transaction status association with a repository context {@code name}.
 * 
 * @author Alisson Gomes
 *
 */
public class TransactionContext
{
    private final String            name;
    private final ConnectionAdapter conn;
    private TransactionStatus       status;
    
    public TransactionContext()
    {
        this("Empty", null);
    }
    
    public TransactionContext(String name, ConnectionAdapter conn)
    {
        super();
        this.name = name;
        this.status = TransactionStatus.NO_TRANSACTION;
        this.conn = conn;
    }
    
    public boolean isActive()
    {
        return (status == TransactionStatus.ACTIVE);
    }
    
    public String getName()
    {
        return name;
    }
    
    public ConnectionAdapter getConnection()
    {
        return conn;
    }
    
    public TransactionStatus getStatus()
    {
        return status;
    }
    
    public void setStatus(TransactionStatus status)
    {
        this.status = status;
    }
}
