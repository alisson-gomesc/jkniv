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
package net.sf.jkniv.whinstone.spi;

import java.util.Properties;

import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.whinstone.Repository;

/**
 * Build a new instance of {@code Repository} Factory to construct the {@code Repository} 
 * according the data access technology like JPA, JDBC, CASSANDRA, COUCHDB, etc
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public interface RepositoryFactory
{
    /**
     * New instance of Repository using default name from {@code SqlContext} "repository-sql.xml".
     * 
     * @return new instance of {@code Repository}.
     */
    Repository newInstance();
    
    /**
     * New instance of {@code Repository} using default name from {@code SqlContext} "repository-sql.xml"
     * with additional properties.
     * 
     * @param props additional properties of {@code Repository}
     * @return new instance of {@code Repository}.
     */
    Repository newInstance(Properties props);
    
    /**
     * New instance of {@code Repository} using additional properties and {@code SqlContext} instance.
     * 
     * @param props additional properties of {@code Repository}
     * @param sqlContext the {@code SqlContext} with the queries from {@code Repository}.
     * @return new instance of {@code Repository}.
     */
    Repository newInstance(Properties props, SqlContext sqlContext);
    
    /**
     * 
     * @param sqlContext the name of XML file with the queries from {@code Repository}.
     * @return new instance of {@code Repository}.
     */
    Repository newInstance(String sqlContext);
    
    /**
     * 
     * @param sqlContext the {@code SqlContext} with the queries from {@code Repository}.
     * @return new instance of {@code Repository}.
     */
    Repository newInstance(SqlContext sqlContext);
    
    /**
     * The type of {@code Repository}
     * @return the type of {@code Repository} like: JDBC, JPA, COUCHDB, CASSANDRA
     */
    RepositoryType getType();
}
