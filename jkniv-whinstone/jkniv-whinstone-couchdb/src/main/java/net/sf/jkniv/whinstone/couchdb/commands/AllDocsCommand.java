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
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.couchdb.HttpBuilder;
import net.sf.jkniv.whinstone.couchdb.statement.AllDocsAnswer;

public class AllDocsCommand extends AbstractCommand implements CouchCommand 
{
    private static final Logger LOG = LoggerFactory.getLogger(FindCommand.class);
    private static final Logger LOGSQL = net.sf.jkniv.whinstone.couchdb.LoggerFactory.getLogger();
    private HttpBuilder httpBuilder;
    private Queryable queryable;

    public AllDocsCommand(HttpBuilder httpBuilder, Queryable queryable)
    {
        super();
        //stmt.rows();
        this.httpBuilder = httpBuilder;
        //this.body = stmt.getBody();
        this.queryable = queryable;
        this.method = HttpMethod.GET;
        this.url = httpBuilder.getUrlForAllDocs(queryable);
    }

    
    @Override
    public <T> T execute()
    {
        //http = new HttpGet(uri.toString()+uriParams.toString());
        //requestParams.setHeader(http);
        String json = null;
        CloseableHttpResponse response = null;
        AllDocsAnswer answer = null;
        List<?> list = Collections.emptyList();
        HttpRequestBase http = null;
        try
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            if (method == HttpMethod.GET)
            {
                http = new HttpGet(url);
                this.httpBuilder.setHeader(http);
            }
            if(LOGSQL.isInfoEnabled())
            {
                StringBuilder sb = new StringBuilder("\nHTTP GET " + url);
                for (Header h : http.getAllHeaders())
                    sb.append("\n ").append(h.getName()+": "+h.getValue());
                LOGSQL.info(sb.toString());
            }
            response = httpclient.execute(http);
            json = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HTTP_OK)
            {
                answer = JsonMapper.mapper(json, AllDocsAnswer.class);
                //if (answer.getWarning() != null)
                //    LOG.warn(answer.getWarning());
                list =  answer.getRows();
                queryable.setTotal(answer.getTotalRows());
            }
            else if (isNotFound(statusCode))
            {
                // 204 No Content, 304 Not Modified, 205 Reset Content
                LOG.warn(errorFormat(http, response.getStatusLine(), json));
            }
            else
            {
                LOG.error(errorFormat(http, response.getStatusLine(), json));
                throw new RepositoryException(response.getStatusLine().toString());
            }
            //commandHandler.postCommit();
        }
        catch (Exception e) // ClientProtocolException | JsonParseException | JsonMappingException | IOException
        {
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

    @Override
    public String getBody()
    {
        return this.body;
    }
    
}
