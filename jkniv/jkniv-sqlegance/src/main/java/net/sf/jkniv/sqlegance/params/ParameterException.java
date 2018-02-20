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
package net.sf.jkniv.sqlegance.params;

import net.sf.jkniv.sqlegance.RepositoryException;

public class ParameterException extends RepositoryException
{
    private static final long serialVersionUID = 5911476699510280411L;

    /**
     * Constructor for ParameterException without message detail
     */
    public ParameterException()
    {
        super();
    }
    
    /**
     * Constructor for ParameterException.
     * @param msg the detail from exception message
     */
    public ParameterException(String msg)
    {
        super(msg);
    }
    
    /**
     * Constructor for ParameterException.
     * @param msg the detail from exception message
     * @param cause the trouble root cause , usually JDBC family exception
     */
    public ParameterException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
    
    public ParameterException(Throwable cause)
    {
        super(cause);
    }
    
}
