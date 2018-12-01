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
package net.sf.jkniv.whinstone.couchdb.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.couchdb.HttpBuilder;

public class GetCommand extends AbstractCommand implements CouchCommand
{
    private static final Logger LOG = LoggerFactory.getLogger(GetCommand.class);
    private static final Logger LOGSQL = net.sf.jkniv.whinstone.couchdb.LoggerFactory.getLogger();
    private String              body;
    private Queryable           queryable;
    private HttpBuilder         httpBuilder;
    
    public GetCommand(HttpBuilder httpBuilder, Queryable queryable)
    {
        super();
        this.httpBuilder = httpBuilder;
        this.queryable = queryable;
    }
    
    @Override
    public <T> T execute()
    {
        String json = null;
        CloseableHttpResponse response = null;
        //Map<String, Object> answer = null;
        List<T> list = new ArrayList<T>(1);
        T answer = null;
        try
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            String url = httpBuilder.getUrlForGet(queryable);
            HttpGet http = null;
            if (this.method == HttpMethod.GET)
            {
                http = new HttpGet(url);
            }
            else
                throw new RepositoryException("Get Command just support GET HTTP method!");
            
            httpBuilder.setHeader(http);
            if(LOGSQL.isInfoEnabled())
            {
                StringBuilder sb = new StringBuilder("\nHTTP GET " + url);
                for (Header h : http.getAllHeaders())
                    sb.append("\n ").append(h.getName()+": "+h.getValue());
                LOGSQL.debug(sb.toString());
            }
            response = httpclient.execute(http);
            json = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HTTP_OK)
            {
                if (queryable.getReturnType() != null)
                    answer =  (T) JsonMapper.mapper(json, queryable.getReturnType());
                else
                    answer = (T) JsonMapper.mapper(json, Map.class);
                
                list.add(answer);
            }
            else if (isNotFound(statusCode))
            {
                // 204 No Content, 304 Not Modified, 205 Reset Content
                LOGSQL.warn(errorFormat(http, response.getStatusLine(), json));
            }
            else
            {
                //LOG.error("Http Body\n{}", EntityUtils.toString(http.getEntity()));
                LOG.error(errorFormat(http, response.getStatusLine(), json));
                throw new RepositoryException(response.getStatusLine().toString());
            }
            commandHandler.postCommit();
        }
        catch (Exception e) // ClientProtocolException | JsonParseException | JsonMappingException | IOException
        {
            commandHandler.postException();
            handlerException.handle(e);
        }
        finally
        {
            if (response != null)
            {
                try
                {
                    response.close();
                }
                catch (IOException e)
                {
                    handlerException.handle(e);
                }
            }
        }
        return (T)list;
    }
    
    @Override
    public String getBody()
    {
        return this.body;
    }
    
    @Override
    public HttpMethod asGet()
    {
        this.method = HttpMethod.GET;
        return this.method;
    }
}
