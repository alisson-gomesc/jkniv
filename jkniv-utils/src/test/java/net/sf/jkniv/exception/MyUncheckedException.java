/* 
 * JKNIV, utils - Helper utilities for jdk code.
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
package net.sf.jkniv.exception;

public class MyUncheckedException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public MyUncheckedException()
    {
    }
    
    public MyUncheckedException(String msg)
    {
        super(msg);
    }
    
    public MyUncheckedException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
    
    public MyUncheckedException(Throwable cause)
    {
        super(cause);
    }
    
}
