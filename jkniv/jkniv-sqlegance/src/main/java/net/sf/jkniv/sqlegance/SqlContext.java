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
package net.sf.jkniv.sqlegance;

import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;

/**
 * Main interface to load and access the set of queries defined at XML files for an application.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface SqlContext
{
    /**
     * Return context name that belong the statements
     * @return context name
     */
    String getName();
    
    /**
     * Retrieve one query according your name.
     * 
     * @param name Name of the query.
     * @return Return the query object with SQL.
     * @exception IllegalArgumentException
     *            if the parameter name does not refer names of query
     *            configured this exception is thrower.
     */
    Sql getQuery(String name);
    
    boolean containsQuery(String name);
    
    RepositoryConfig getRepositoryConfig();
    
    SqlDialect getSqlDialect();
}
