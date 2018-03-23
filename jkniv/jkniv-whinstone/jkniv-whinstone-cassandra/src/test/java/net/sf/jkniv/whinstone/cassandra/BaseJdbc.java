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
package net.sf.jkniv.whinstone.cassandra;

import java.util.Properties;

import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.sqlegance.RepositoryProperty;

public class BaseJdbc //extends BaseSpringJUnit4
{
    protected static final String url = "127.0.0.1";
    protected static final String schema = "dev_data_3t";
    protected static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    protected static final String user = "cassandra";
    protected static final String pass = "cassandra";
    protected static final Properties config;
    static
    {
        config = new Properties();
        config.put(RepositoryProperty.JDBC_URL.key(), url);
        config.put(RepositoryProperty.JDBC_SCHEMA.key(), schema);
        config.put(RepositoryProperty.JDBC_USER.key(), user);
        config.put(RepositoryProperty.JDBC_PASSWORD.key(), pass);
        config.put(RepositoryProperty.JDBC_DRIVER.key(), driver);
    }
    
    protected Repository getRepository()
    {
        return new RepositoryCassandra(config);
    }

    protected Queryable getQuery(String name)
    {
        Queryable q = QueryFactory.newInstance(name);
        return q;
    }
    
    protected Queryable getQuery(String name, Object params)
    {
        Queryable q = QueryFactory.newInstance(name,params);
        return q;
    }    
    
    protected Queryable getQuery(String name, Object params, int offset, int max)
    {
        Queryable q = QueryFactory.newInstance(name, params, offset, max);
        return q;
    }
}
