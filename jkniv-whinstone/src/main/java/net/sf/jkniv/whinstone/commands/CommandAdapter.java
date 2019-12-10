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
package net.sf.jkniv.whinstone.commands;

import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;

/**
 * Adapter to abstract the JDBC Connection {@link java.sql.Connection} 
 * representing a connection/session to a specific database (RDBMS or NoSQL).
 * 
 * @author Alisson Gomes
 * @since 0.6.0 
 */
public interface CommandAdapter
{
    String getContextName();
    
    <T,R> Command asSelectCommand(Queryable queryable, ResultRow<T, R> overloadResultRow);

    <T, R> Command asUpdateCommand(Queryable queryable);

    <T, R> Command asRemoveCommand(Queryable queryable);

    <T, R> Command asAddCommand(Queryable queryable);

    void close();
}
