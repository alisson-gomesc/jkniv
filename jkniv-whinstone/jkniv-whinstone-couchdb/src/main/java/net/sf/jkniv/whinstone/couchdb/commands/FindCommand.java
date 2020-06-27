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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.couchdb.CouchDbResult;
import net.sf.jkniv.whinstone.couchdb.HttpBuilder;

@SuppressWarnings({"unchecked", "rawtypes"})
public class FindCommand extends AbstractCommand implements CouchCommand
{
    private static final Logger LOG = LoggerFactory.getLogger(FindCommand.class);
    private static final Logger LOGSQL = net.sf.jkniv.whinstone.couchdb.LoggerFactory.getLogger();
    private HttpBuilder httpBuilder;
    private Queryable queryable;
    
    public FindCommand(HttpBuilder httpBuilder, Queryable queryable)
    {
        super();
        this.queryable = queryable;
        this.httpBuilder = httpBuilder;
    }
    
    
    @Override
    public <T> T execute()
    {
        String json = null;
        CloseableHttpResponse response = null;
        CouchDbResult answer = null;
        List list = Collections.emptyList();
        try
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = httpBuilder.newFind(body);
            printRequest(httpPost);
            response = httpclient.execute(httpPost);
            json = EntityUtils.toString(response.getEntity());
            printResponse(response, json);

            int statusCode = response.getStatusLine().getStatusCode();
            if (isOk(statusCode))
            {
                JsonMapper.setCurrentQuery(queryable);
                answer = JsonMapper.MAPPER.readerFor(CouchDbResultImpl.class).readValue(json);
                if(CouchDbResult.class.isAssignableFrom(queryable.getReturnType()))
                {
                    list = new ArrayList();
                    list.add(answer);
                }
                else
                    list = answer.getRows();
                setBookmark(answer.getBookmark(), queryable);
                if(queryable.isPaging())
                    queryable.setTotal(Statement.SUCCESS_NO_INFO);
                else
                    queryable.setTotal(answer.getTotalRows());
            }
            else if (isNotFound(statusCode))
            {
                queryable.setTotal(0);
                // 204 No Content, 304 Not Modified, 205 Reset Content
                LOGSQL.warn(errorFormat(httpPost, response.getStatusLine(), json));
            }
            else
            {
                LOG.error(errorFormat(httpPost, response.getStatusLine(), json));
                throw new RepositoryException(response.getStatusLine().toString());
            }
            //commandHandler.postCommit();
        }
        catch (Exception e) // ClientProtocolException | JsonParseException | JsonMappingException | IOException
        {
            queryable.setTotal(Statement.EXECUTE_FAILED);
            //commandHandler.postException();
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
    
    /*
    public <T> List<T> transformRows(List<?> listOfMap, Class<T> clazz)
    {
        List<Object> docs = new ArrayList();
        if (listOfMap != null)
        {
            for (Object row : listOfMap)
                docs.add(JsonMapper.mapper((Map) row, clazz));
        }
        return (List<T>) docs;
    }
    */

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
