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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.couchdb.HttpBuilder;
import net.sf.jkniv.whinstone.couchdb.statement.AllDocsAnswer;
import net.sf.jkniv.whinstone.couchdb.statement.CouchDbStatementAdapter;

public class ViewCommand extends AbstractCommand implements CouchCommand
{
    private static final Logger LOG = LoggerFactory.getLogger(ViewCommand.class);
    private String              body;
    private Queryable queryable;
    private HttpBuilder         httpBuilder;
    
    public ViewCommand(CouchDbStatementAdapter<?, String> stmt, HttpBuilder httpBuilder, Queryable queryable)
    {
        super();
        this.httpBuilder = httpBuilder;
        this.queryable = queryable;
        stmt.rows();
        this.body = stmt.getBody();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> T execute()
    {
        String json = null;
        CloseableHttpResponse response = null;
        Class returnType = null;
        AllDocsAnswer answer = null;
        List list = Collections.emptyList();
        try
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            String url = httpBuilder.getUrlForView(queryable);
            LOG.debug(url);
            HttpGet http = (HttpGet)asGet().newHttp(url); 
            httpBuilder.setHeader(http);
            response = httpclient.execute(http);
            json = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (isOk(statusCode))
            {
                if (queryable.getReturnType() != null)
                    returnType = queryable.getReturnType();
                else if (queryable.getDynamicSql().getReturnTypeAsClass() != null)
                    returnType = queryable.getDynamicSql().getReturnTypeAsClass();
                
                answer = JsonMapper.mapper(json, AllDocsAnswer.class);
                if (returnType != null)
                {
                    list = new ArrayList();
                    // FIXME overload performance, writer better deserialization using jackson
                    for(Map map : answer.getRows())
                    {
                        Map content = (Map) map.get("value"); 
                        //ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(returnType);
                        //list.add(proxy.from(r));
                        list.add(JsonMapper.mapper(content, returnType));
                    }
                }
                else
                    list = answer.getRows();
            }
            else if (isNotFound(statusCode))
            {
                // 204 No Content, 304 Not Modified, 205 Reset Content
                LOG.info(response.getStatusLine().toString());
            }
            else
            {
                //LOG.error("Http Body\n{}", EntityUtils.toString(http.getEntity()));
                LOG.error("{} -> {} ", response.getStatusLine().toString(), json);
                throw new RepositoryException(response.getStatusLine().toString());
            }
        }
        catch (Exception e) // ClientProtocolException | JsonParseException | JsonMappingException | IOException
        {
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
        if (list == null)
            list = Collections.emptyList();
        return (T) list;
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
    
    @Override
    public HttpMethod asHead()
    {
        this.method = HttpMethod.HEAD;
        return this.method;
    }
    
    @Override
    public HttpMethod asPost()
    {
        this.method = HttpMethod.POST;
        return this.method;
    }
}
