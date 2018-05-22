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

/**
 * A {@code BoundException} is thrown if {@link Queryable} instance is re-used for
 * different SQL or return types overloaded, this exception indicate that values binding was associated.
 * 
 * @author alisson
 * @since 0.6.0 
 */
public class BoundException extends RepositoryException
{
    private static final long serialVersionUID = -7290720478728189993L;

    /**
     * Constructor for BoundException without message detail
     */
    public BoundException()
    {
        super();
    }
    
    /**
     * Constructor for BoundException.
     * @param msg the detail from exception message
     */
    public BoundException(String msg)
    {
        super(msg);
    }
    
    /**
     * Constructor for BoundException.
     * @param msg the detail from exception message
     * @param cause the trouble root cause , usually JDBC family exception
     */
    public BoundException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
    
    public BoundException(Throwable cause)
    {
        super(cause);
    }
    
}
