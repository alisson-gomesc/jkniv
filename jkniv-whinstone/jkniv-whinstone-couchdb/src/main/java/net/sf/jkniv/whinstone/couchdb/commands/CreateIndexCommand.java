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

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.couchdb.HttpBuilder;

/**
 * Create a new CouchDB index
 * 
 * <pre>
 * 
 *  https://docs.couchdb.org/en/stable/api/database/find.html#db-index
 * 
 *  POST /{db}/_index
 *  
 *  Create a new index on a database
 *  Parameters: 
 *  
 *  db – Database name
 *  
 *  Request Headers:
 *  
 *      Content-Type –
 *          application/json
 *          
 *   Query Parameters:
 *       index (json) – JSON object describing the index to create.
 *       ddoc (string) – Name of the design document in which the index will be created. By default, each index will be created in its own design document. Indexes can be grouped into design documents for efficiency. However, a change to one index in a design document will invalidate all other indexes in the same document (similar to views). Optional
 *       name (string) – Name of the index. If no name is provided, a name will be generated automatically. Optional
 *       type (string) – Can be "json" or "text". Defaults to json. Geospatial indexes will be supported in the future. Optional Text indexes are supported via a third party library Optional
 *       partial_filter_selector (json) – A selector to apply to documents at indexing time, creating a partial index. Optional
 *       partitioned (boolean) – Determines whether a JSON index is partitioned or global. The default value of partitioned is the partitioned property of the database. To create a global index on a partitioned database, specify false for the "partitioned" field. If you specify true for the "partitioned" field on an unpartitioned database, an error occurs.
 *
 *   Response Headers:
 *        
 *       Content-Type – application/json
 *       Transfer-Encoding – chunked
 *
 *   Response JSON Object:
 *        
 *        result (string) – Flag to show whether the index was created or one already exists. Can be “created” or “exists”.
 *        id (string) – Id of the design document the index was created in.
 *        name (string) – Name of the index created.
 *
 *    Status Codes:   
 *
 *        200 OK – Index created successfully or already exists
 *        400 Bad Request – Invalid request
 *        401 Unauthorized – Admin permission required
 *        500 Internal Server Error – Execution error
 * </pre>
 *
 * @author Alisson Gomes
 * @since 0.6.6
 *
 */
class CreateIndexCommand extends AbstractCommand implements CouchCommand
{
    private static final Logger     LOG     = LoggerFactory.getLogger(CreateIndexCommand.class);
    private final HttpBuilder       httpBuilder;
    
    public CreateIndexCommand(HttpBuilder httpBuilder, String indexJson)
    {
        super();
        this.httpBuilder = httpBuilder;
        this.body = indexJson;
        this.method = HttpMethod.POST;
    }
    
    @Override
    public <T> T execute()
    {
        String json = null;
        CloseableHttpResponse response = null;
        try
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(httpBuilder.getHostContext() + "_index");
            httpBuilder.setHeader(httpPost);
            httpPost.setEntity(getEntity());
            printRequest(httpPost);
            response = httpclient.execute(httpPost);
            json = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (isOk(statusCode))
            {
                LOG.info("{} view as created successfully {}", json);
            }
            else
            {
                LOG.error("Http Body\n{}", EntityUtils.toString(httpPost.getEntity()));
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
        return null;
    }
        
    @Override
    public String getBody()
    {
        return this.body.toString();
    }
    
    @Override
    public HttpMethod asPost()
    {
        this.method = HttpMethod.POST;
        return this.method;
    }
    
    @Override
    public HttpMethod asPut()
    {
        throw new RepositoryException("Design Command cannot be executed as PUT method");
    }
    
    @Override
    public HttpMethod asDelete()
    {
        throw new RepositoryException("Design Command cannot be executed as DELETE method");
    }
    
    @Override
    public HttpMethod asGet()
    {
        throw new RepositoryException("Design Command cannot be executed as GET method");
    }
}
