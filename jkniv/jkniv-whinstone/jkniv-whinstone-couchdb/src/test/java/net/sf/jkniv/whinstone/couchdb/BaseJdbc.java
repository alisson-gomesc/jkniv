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
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.DefaultClassLoader;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;
import net.sf.jkniv.whinstone.couchdb.commands.PutCommand;
import net.sf.jkniv.whinstone.couchdb.jndi.JndiCreator;

public class BaseJdbc extends BaseSpringJUnit4
{
    private static final Logger LOG           = LoggerFactory.getLogger(BaseJdbc.class);
    public  static final Properties config;
    private static final String URL           = "http://127.0.0.1:5984";
    private static final String SCHEMA        = "whinstone-author";
    private static final String USER          = "admin";
    private static final String PASSWD        = "admin";
    private static boolean      CREATED       = false;
    static int                  TOTAL_AUTHORS = 7;
    static int                  TOTAL_VIEWS   = 1;
    
    static 
    {
        config = new Properties();
        config.setProperty(RepositoryProperty.JDBC_URL.key(), URL);
        config.setProperty(RepositoryProperty.JDBC_SCHEMA.key(), SCHEMA);
        config.setProperty(RepositoryProperty.JDBC_USER.key(), USER);
        config.setProperty(RepositoryProperty.JDBC_PASSWORD.key(), PASSWD);
    }

    
    // Serialize Date to ISO-8601
    // pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    // https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html#iso8601timezone
    @BeforeClass
    public static void setUpDatabase()
    {
        if (!CREATED)
        {
            CouchDbAuthenticate auth = new CouchDbAuthenticate(URL, USER, PASSWD);
            String token = auth.authenticate();
            HttpBuilder httpBuilder = new HttpBuilder(auth, URL, SCHEMA, new RequestParams(SCHEMA));
            
            dropDatabase(httpBuilder);
            createDatabase(httpBuilder);
            loadDatabase(httpBuilder);
            JndiCreator.bind();
        }
        CREATED = true;
    }
    
    private static void dropDatabase(HttpBuilder httpBuilder)
    {
        HttpDelete http = new HttpDelete(URL + "/" + SCHEMA);
        httpBuilder.setHeader(http);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try
        {
            httpclient.execute(http);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        LOG.info("DROP DATABASE {}", httpBuilder.getHostContext());
    }
    
    private static void createDatabase(HttpBuilder httpBuilder)
    {
        HttpPut http = new HttpPut(URL + "/" + SCHEMA);
        httpBuilder.setHeader(http);
        PutCommand command = new PutCommand(http);
        command.execute();
        LOG.info("CREATE DATABASE {}", httpBuilder.getHostContext());
    }
    
    private static void loadDatabase(HttpBuilder httpBuilder)
    {
        List<String> files = Arrays.asList("author1.json", "author2.json", "author3.json", "author4.json",
                "author5.json", "author6.json", "author7.json");
        int id = 1;
        for (String f : files)
        {
            HttpPut http = new HttpPut(URL + "/" + SCHEMA + "/" + id++);
            httpBuilder.setHeader(http);
            InputStream is = DefaultClassLoader.getResourceAsStream("/database/" + f);
            String json = isToString(is);
            PutCommand command = new PutCommand(http, json);
            command.execute();
        }
        LOG.info("DATABASE {} HAS DATA LOADED", httpBuilder.getHostContext());
    }
    
    protected static Repository getRepository()
    {
        return RepositoryService.getInstance().lookup(RepositoryType.COUCHDB).newInstance(config);
    }
    
    protected Queryable getQuery(String name)
    {
        Queryable q = QueryFactory.newInstance(name);
        return q;
    }
    
    protected Queryable getQuery(String name, Object params)
    {
        Queryable q = QueryFactory.newInstance(name, params);
        return q;
    }
    
    protected Queryable getQuery(String name, Object params, int offset, int max)
    {
        Queryable q = QueryFactory.newInstance(name, params, offset, max);
        return q;
    }
    
    private static String isToString(InputStream is)
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
        try
        {
            entity = new StringEntity(body);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RepositoryException("Cannot build encoding unsupported\n" + body);
        }
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
