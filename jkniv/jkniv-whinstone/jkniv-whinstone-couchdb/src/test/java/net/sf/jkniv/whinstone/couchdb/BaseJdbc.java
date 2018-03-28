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
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.junit.Before;
import org.junit.BeforeClass;

import net.sf.jkniv.sqlegance.DefaultClassLoader;
import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.RepositoryService;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.whinstone.couchdb.commands.DeleteCommand;
import net.sf.jkniv.whinstone.couchdb.commands.PutCommand;

public class BaseJdbc extends BaseSpringJUnit4
{
    protected static Properties config;
    private static final String URL ="http://127.0.0.1:5984";
    private static final String SCHEMA = "whinstone-author";
    private static final String USER = "admin";
    private static final String PASSWD = "admin";

    @BeforeClass 
    public static void setUpDatabase()
    {
        CouchDbAuthenticate auth = new CouchDbAuthenticate();
        String token = auth.authenticate(URL, USER, PASSWD);
        HttpBuilder httpBuilder = new HttpBuilder(URL , SCHEMA, new RequestParams(token, SCHEMA));

        dropDatabase(httpBuilder);
        createDatabase(httpBuilder);
        loadDatabase(httpBuilder);
    }
    
    private static void dropDatabase(HttpBuilder httpBuilder)
    {
        HttpDelete http = new HttpDelete(URL+"/"+SCHEMA);
        httpBuilder.setHeader(http);
        DeleteCommand command = new DeleteCommand(http);
        try
        {
            command.execute();
        }
        catch(RepositoryException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    private static void createDatabase(HttpBuilder httpBuilder)
    {
        HttpPut http = new HttpPut(URL+"/"+SCHEMA);
        httpBuilder.setHeader(http);
        PutCommand command = new PutCommand(http);
        command.execute();
    }
    
    private static void loadDatabase(HttpBuilder httpBuilder)
    {
        List<String> files  = Arrays.asList("author1.json","author2.json","author3.json","author4.json","author5.json");
        int id = 1;
        for(String f : files)
        {
            HttpPut http = new HttpPut(URL+"/"+SCHEMA+"/"+id++);
            httpBuilder.setHeader(http);
            InputStream is = DefaultClassLoader.getResourceAsStream("/database/"+f);
            String json = isToString(is);
            PutCommand command = new PutCommand(http, json);
            command.execute();        
        }
    }

    
    @Before
    public void setUp()
    {
        config = new Properties();
        config.setProperty(RepositoryProperty.JDBC_URL.key(), URL);
        config.setProperty(RepositoryProperty.JDBC_SCHEMA.key(), SCHEMA);
        config.setProperty(RepositoryProperty.JDBC_USER.key(), USER);
        config.setProperty(RepositoryProperty.JDBC_PASSWORD.key(), PASSWD);
    }
    
    protected Repository getRepository()
    {
        return  RepositoryService.getInstance().lookup(RepositoryType.COUCHDB).newInstance(config);
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
    
    private static String isToString(InputStream is)
    {
        int ch;
        StringBuilder sb = new StringBuilder();
        try
        {
            while((ch = is.read()) != -1)
                sb.append((char)ch);
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
            throw new RepositoryException("Cannot build encoding unsupported\n"+body);
        }
        return entity;
    }
}
