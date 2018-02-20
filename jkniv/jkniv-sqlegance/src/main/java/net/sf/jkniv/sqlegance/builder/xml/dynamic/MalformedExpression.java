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
package net.sf.jkniv.sqlegance.builder.xml.dynamic;

/**
 * This exception is throwed if an OGNL expression its founded at bad format
 * 
 * @author Alisson Gomes
 */
public class MalformedExpression extends RuntimeException
{
    private static final long serialVersionUID = 3669532981080154420L;
    
    public MalformedExpression()
    {
        super();
    }
    
    public MalformedExpression(String message)
    {
        super(message);
    }
    
    public MalformedExpression(String message, Throwable cause)
    {
        super(message, cause);
    }
    
}
