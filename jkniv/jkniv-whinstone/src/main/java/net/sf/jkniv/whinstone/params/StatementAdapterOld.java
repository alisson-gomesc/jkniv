/* 
 * JKNIV, whinstone one contract to access your database.
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
package net.sf.jkniv.whinstone.params;

/**
 * Adapter for java.sql.PreparedStatement or javax.persistence.Query
 * 
 * @author Alisson Gomes
 * @since 0.6.0 DELETE ME
 * @deprecated
 */
public interface StatementAdapterOld
{
    /**
     * Bind an argument to a named parameter.
     * @param name of parameter
     * @param value of parameter
     * @return this instance
     */
    StatementAdapterOld setParameter(String name, Object value);

    /**
     * Bind an argument to a position parameter.
     * @param position index of parameter positions, initial is 1
     * @param value of parameter
     * @return this instance
     */
    StatementAdapterOld setParameter(int position, Object value);

    /**
     * Bind the varargs parameters to statement
     * @param values of parameters as arbitrary number
     * @return instance of this object
     */
    StatementAdapterOld setParameters(Object... values);
    
    /**
     * reset the internal index position parameter to zero.
     * @return return current index position before reset
     */
    int reset();
    
    
    
}
