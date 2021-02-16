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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;

public class BaseJdbc extends BaseSpringJUnit4
{
    private static final Logger    LOG                 = LoggerFactory.getLogger(BaseJdbc.class);
    public static final Properties config, configDb3t;
    private static final String    URL                 = "http://127.0.0.1:5984";
    private static final String    SCHEMA              = "whinstone-author";
    private static final String    USER                = "admin";
    private static final String    PASSWD              = "admin";
    private static boolean         SETUP_DATABASE_DONE = false;
    static int                     TOTAL_AUTHORS       = 7;
    static int                     TOTAL_VIEWS         = 1;
    
    static
    {
        config = new Properties();
        configDb3t = new Properties();
        config.setProperty(RepositoryProperty.JDBC_URL.key(), URL);
        config.setProperty(RepositoryProperty.JDBC_SCHEMA.key(), "whinstone-author");
        config.setProperty(RepositoryProperty.JDBC_USER.key(), USER);
        config.setProperty(RepositoryProperty.JDBC_PASSWORD.key(), PASSWD);
        
        configDb3t.setProperty(RepositoryProperty.JDBC_URL.key(), URL);
        configDb3t.setProperty(RepositoryProperty.JDBC_SCHEMA.key(), "db3t-user-origin");
        configDb3t.setProperty(RepositoryProperty.JDBC_USER.key(), USER);
        configDb3t.setProperty(RepositoryProperty.JDBC_PASSWORD.key(), PASSWD);
    }
    
    
    protected static Repository getRepository()
    {
        return RepositoryService.getInstance().lookup(RepositoryType.COUCHDB).newInstance(config);
    }
    
    protected static Repository getRepositoryDb3t()
    {
        return RepositoryService.getInstance().lookup(RepositoryType.COUCHDB).newInstance(configDb3t,
                SqlContextFactory.newInstance("/repository-sql-db3t.xml"));
    }
    
    protected Queryable getQuery(String name)
    {
        Queryable q = QueryFactory.of(name);
        return q;
    }
    
    protected Queryable getQuery(String name, Object params)
    {
        Queryable q = QueryFactory.of(name, params);
        return q;
    }
    
    protected Queryable getQuery(String name, Object params, int offset, int max)
    {
        Queryable q = QueryFactory.of(name, params, offset, max);
        return q;
    }
    
    private static String streamToString(InputStream is)
    {
        int ch;
        StringBuilder sb = new StringBuilder();
        try
        {
            while ((ch = is.read()) != -1)
                sb.append((char) ch);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //reset();
        return sb.toString();
    }
    
    private static HttpEntity toBody(String body)
    {
        HttpEntity entity = null;
        entity = new StringEntity(body, Consts.UTF_8); // TODO config charset for HTTP body
        return entity;
    }
    
    protected long getTotalDocs()
    {
        return TOTAL_AUTHORS + TOTAL_VIEWS;
    }
    
    protected Map<String, Object> asParams(Object... args)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        int i = 1;
        String key = null;
        Object value = null;
        for (Object o : args)
        {
            if (i % 2 == 1)
            {
                key = o.toString();
            }
            else
            {
                value = o;
                params.put(key, value);
                key = null;
                value = null;
            }
            i++;
        }
        return params;
    }
}
