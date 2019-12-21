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
package net.sf.jkniv.whinstone;

import net.sf.jkniv.sqlegance.RepositoryException;

public class UnsupportedDslOperationException extends RepositoryException
{
    private static final long serialVersionUID = 8383490925378813301L;

    /**
     * Throw message: this repository does not support DSL operation
     */
    public UnsupportedDslOperationException()
    {
        super("This repository does not support DSL operation");
    }
    
    /**
     * Constructor for UnsupportedDslOperationException.
     * @param msg detailed message about the exception
     */
    public UnsupportedDslOperationException(String msg)
    {
        super(msg);
    }
    
    /**
     * Constructor for UnsupportedDslOperationException.
     * @param msg detailed message about the exception
     * @param cause the trouble root cause , usually JDBC family exception
     */
    public UnsupportedDslOperationException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
    
    public UnsupportedDslOperationException(Throwable cause)
    {
        super(cause);
    }
    
}
