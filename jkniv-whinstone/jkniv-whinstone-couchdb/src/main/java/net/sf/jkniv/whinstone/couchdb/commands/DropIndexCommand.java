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
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.couchdb.HttpBuilder;

/**
 * D
 * 
 * <pre>
 * 
 * https://docs.couchdb.org/en/stable/api/database/find.html#delete--db-_index-designdoc-json-name
 * 
 * 
 * DELETE /{db}/_index/{designdoc}/json/{name}
 *     Parameters: 
 * 
 *         db – Database name.
 *         designdoc – Design document name.
 *         name – Index name.
 * 
 *     Response Headers:
 * 
 *         Content-Type – application/json
 * 
 *     Response JSON Object:
 *         
 *         ok (string) – “true” if successful.
 * 
 *     Status Codes:   
 * 
 *         200 OK – Success
 *         400 Bad Request – Invalid request
 *         401 Unauthorized – Writer permission required
 *         404 Not Found – Index not found
 *         500 Internal Server Error – Execution error
 * </pre>
 * 
 * @author Alisson Gomes
 * @since 0.6.6
 *
 */
@SuppressWarnings("unchecked")
public class DropIndexCommand extends AbstractCommand implements CouchCommand
{
    private static final Logger LOG = LoggerFactory.getLogger(DropIndexCommand.class);
    private static final Logger LOGSQL = net.sf.jkniv.whinstone.couchdb.LoggerFactory.getLogger();
    private HttpBuilder         httpBuilder;
    private String designDoc;
    private String indexName;
    
    public DropIndexCommand(HttpBuilder httpBuilder, String designDoc, String indexName)
    {
        super();
        this.httpBuilder = httpBuilder;
        this.method = HttpMethod.DELETE;
        this.designDoc = designDoc;
        this.indexName = indexName;
    }
    
    @Override
    public <T> T execute()
    {
        String json = null;
        CloseableHttpResponse response = null;
        T answer = null;
        try
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            // {db}/_index/{designdoc}/json/{name}
            String url =   String.format("%s_index/%s/json/%s", httpBuilder.getHostContext(), designDoc, indexName);
            HttpDelete http = null;
            http = (HttpDelete)asDelete().newHttp(url);
            httpBuilder.setHeader(http);
            printRequest(http);
            response = httpclient.execute(http);
            json = EntityUtils.toString(response.getEntity());
            printResponse(response, json);

            int statusCode = response.getStatusLine().getStatusCode();
            if (isOk(statusCode))
            {
                LOGSQL.info("Index {} was dropped successfully", url);
                answer = (T)Integer.valueOf("1");
            }
            else if(!isNotFound(statusCode))
            {
                LOG.warn(errorFormat(http, response.getStatusLine(), json));
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
        return answer;
    }
    
    @Override
    public String getBody()
    {
        return this.body;
    }
    
    @Override
    public HttpMethod asDelete()
    {
        this.method = HttpMethod.DELETE;
        return this.method;
    }
}
