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
package net.sf.jkniv.whinstone.couchdb;

import java.util.Properties;

import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.RepositoryService;
import net.sf.jkniv.sqlegance.RepositoryType;

public class BaseJdbc //extends BaseSpringJUnit4
{
    protected static final String url = "http://127.0.0.1:5984";
    protected static final String user = "sa";
    protected static final String pass = "";
    protected static final Properties config;
    static
    {
        config = new Properties();
        config.put(RepositoryProperty.JDBC_URL.key(), url);
        config.put(RepositoryProperty.JDBC_USER.key(), user);
        config.put(RepositoryProperty.JDBC_PASSWORD.key(), pass);
    }
    
    protected Repository getRepository()
    {
        return  RepositoryService.getInstance().lookup(RepositoryType.COUCHDB).newInstance();
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
