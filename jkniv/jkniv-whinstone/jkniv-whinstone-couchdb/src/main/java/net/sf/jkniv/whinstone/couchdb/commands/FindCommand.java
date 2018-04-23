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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.couchdb.HttpBuilder;
import net.sf.jkniv.whinstone.couchdb.statement.CouchDbStatementAdapter;
import net.sf.jkniv.whinstone.couchdb.statement.FindAnswer;

public class FindCommand extends AbstractCommand implements CouchCommand
{
    private static final Logger LOG = LoggerFactory.getLogger(FindCommand.class);
    private String body;
    //private CouchDbStatementAdapter<?, String> stmt;
    private HttpBuilder httpBuilder;
    private Queryable queryable;
    
    public FindCommand(CouchDbStatementAdapter<?, String> stmt, HttpBuilder httpBuilder, Queryable queryable)
    {
        super();
        this.queryable = queryable;
        this.httpBuilder = httpBuilder;
        //this.stmt = stmt;
        stmt.rows();
        this.body = stmt.getBody();
    }
    
    @Override
    public <T> T execute()
    {
        String json = null;
        CloseableHttpResponse response = null;
        Class returnType = null;
        FindAnswer answer = null;
        List list = Collections.emptyList();
        try
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = httpBuilder.newFind(body);
            response = httpclient.execute(httpPost);
            json = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (isOk(statusCode))
            {
                if (queryable.getReturnType() != null)
                    returnType = queryable.getReturnType();
                else if (queryable.getDynamicSql().getReturnTypeAsClass() != null)
                    returnType = queryable.getDynamicSql().getReturnTypeAsClass();

                
                answer = JsonMapper.mapper(json, FindAnswer.class);
                if (answer.getWarning() != null)
                    LOG.warn(answer.getWarning());
                
                if (returnType != null)
                {
                    list = new ArrayList();
                    // FIXME overload performance, writer better deserialization using jackson
                    for(Object row : answer.getDocs())
                    {
                        list.add(JsonMapper.mapper((Map)row, returnType));
                    }
                }
                else
                    list =  answer.getDocs();
            }
            else if (isNotFound(statusCode))
            {
                // 204 No Content, 304 Not Modified, 205 Reset Content
                LOG.info(response.getStatusLine().toString());
            }
            else
            {
                LOG.error(errorFormat(httpPost, response.getStatusLine(), json));
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

    @Override
    public HttpMethod asPost()
    {
        this.method = HttpMethod.POST;
        return this.method;
    }
}
