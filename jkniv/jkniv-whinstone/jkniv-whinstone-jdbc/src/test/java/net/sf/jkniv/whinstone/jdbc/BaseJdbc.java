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
package net.sf.jkniv.whinstone.jdbc;

import java.util.Properties;

import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.dialect.Derby10o7Dialect;
import net.sf.jkniv.whinstone.jdbc.test.BaseSpringJUnit4;

public class BaseJdbc extends BaseSpringJUnit4
{
    //protected static final String url = "jdbc:derby:memory:derbwhinstone;create=true;user=admin;password=secret";
    protected static final String url = "jdbc:derby:memory:derbwhinstone;create=true";
    protected static final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    protected static final String user = "sa";
    protected static final String pass = "";
    protected static final Properties config;
    static
    {
        config = new Properties();
        config.put(RepositoryProperty.JDBC_URL.key(), url);
        config.put(RepositoryProperty.JDBC_USER.key(), user);
        config.put(RepositoryProperty.JDBC_PASSWORD.key(), pass);
        config.put(RepositoryProperty.JDBC_DRIVER.key(), driver);
        config.put(RepositoryProperty.SQL_DIALECT.key(),Derby10o7Dialect.class.getName());
    }
    
    protected Repository getRepository()
    {
        return new RepositoryJdbc(config);
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
