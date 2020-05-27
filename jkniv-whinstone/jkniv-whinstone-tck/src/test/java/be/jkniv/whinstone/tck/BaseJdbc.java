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
package be.jkniv.whinstone.tck;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.jkniv.whinstone.tck.jndi.JndiCreator;
import net.sf.jkniv.sqlegance.DefaultClassLoader;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;
import net.sf.jkniv.whinstone.couchdb.CouchDbAuthenticate;
import net.sf.jkniv.whinstone.couchdb.HttpBuilder;
import net.sf.jkniv.whinstone.couchdb.RequestParams;
import net.sf.jkniv.whinstone.jdbc.dialect.Derby10o7Dialect;

public class BaseJdbc extends BaseSpringJUnit4
{
    private static final Logger    LOG                 = LoggerFactory.getLogger(BaseCouchdb.class);
    //protected static final String url = "jdbc:derby:memory:derbwhinstone;create=true;user=admin;password=secret";
    protected static final String     url                 = "jdbc:derby:memory:tckwhinderby;create=true";
    protected static final String     driver              = "org.apache.derby.jdbc.EmbeddedDriver";
    protected static final String     user                = "";
    protected static final String     pass                = "";
    protected static final Properties config;
    private static boolean            SETUP_DATABASE_DONE = false;
    private static final SqlContext   ctxJdbc             = SqlContextFactory.newInstance("/repository-sql-jdbc.xml");
    static
    {
        config = new Properties();
        config.put(RepositoryProperty.JDBC_URL.key(), url);
        config.put(RepositoryProperty.JDBC_USER.key(), user);
        config.put(RepositoryProperty.JDBC_PASSWORD.key(), pass);
        config.put(RepositoryProperty.JDBC_DRIVER.key(), driver);
        config.put(RepositoryProperty.SQL_DIALECT.key(), Derby10o7Dialect.class.getName());
    }
    
    @BeforeClass
    public static void setUpDatabase()
    {
        if (SETUP_DATABASE_DONE)
            return;
        dropDatabase();
        createDatabase();
        loadDatabase();
        JndiCreator.bind();
        SETUP_DATABASE_DONE = true;
    }
    
    private static void dropDatabase()
    {
        Repository repository = getRepository();
        try {
            repository.remove(QueryFactory.of("Author#drop"));
            LOG.info("DROP Oracle DATABASE");
        }
        catch(RepositoryException ignore) {} 
    }
    
    private static void createDatabase()
    {
        Repository repository = getRepository();
        repository.add(QueryFactory.of("Author#create"));
        repository.add(QueryFactory.of("Author#sequence"));
        LOG.info("CREATE Oracle DATABASE");
    }
    
    private static void loadDatabase()
    {
        Repository repository = getRepository();
        for(int i=1;i<6;i++)
        {
            repository.add(QueryFactory.of("Author-add"+i));
        }
        LOG.info("DATABASE Oracle HAS DATA LOADED");
    }


    protected static Repository getRepository()
    {
        return RepositoryService.getInstance().lookup(RepositoryType.JDBC).newInstance(config, ctxJdbc);
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
}
