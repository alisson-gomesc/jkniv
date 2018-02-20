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
package net.sf.jkniv.sqlegance.builder;

/**
 * Thrown to indicate that have some tag or value inappropriate at XML
 * configuration.
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 * @deprecated All exception it is throw with RepositoryException, must be removed at version 1.0.0
 */
public class ConfigurationException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public ConfigurationException(String message)
    {
        super(message);
    }
    
    public ConfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause)
    {
        super(cause);
    }

}
