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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Updateable;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.couchdb.HttpBuilder;

/**
 * <pre>
 * 
 * http://docs.couchdb.org/en/2.0.0/api/document/common.html
 * 
 * PUT /{db}/{docid}
 *
 *  The PUT method creates a new named document, or creates a new revision of the existing document. 
 *  Unlike the POST /{db}, you must specify the document ID in the request URL.
 *  
 *  Parameters: 
 *
 *      db – Database name
 *      docid – Document ID
 *
 *  Request Headers:
 *      
 * 
 *      Accept –
 *          application/json
 *          text/plain
 *      Content-Type – application/json
 *      If-Match – Document’s revision. Alternative to rev query parameter
 *      X-Couch-Full-Commit – Overrides server’s commit policy. Possible values are: false and true. Optional
 * 
 *  Query Parameters:
 *      
 *
 *      batch (string) – Stores document in batch mode. Possible values: ok. Optional
 *      new_edits (boolean) – Prevents insertion of a conflicting document. 
 *                            Possible values: true (default) and false. If false, a well-formed _rev must be included 
 *                            in the document. new_edits=false is used by the replicator to insert documents into 
 *                            the target database even if that leads to the creation of conflicts. Optional
 *
 *  Response Headers:
 *      
 *
 *      Content-Type –
 *                     application/json
 *                     text/plain; charset=utf-8
 *      ETag – Quoted document’s new revision
 *      Location – Document URI
 *
 *  Response JSON Object:
 *      
 *
 *      id (string) – Document ID
 *      ok (boolean) – Operation status
 *      rev (string) – Revision MVCC token
 *
 *  Status Codes:   
 * 
 *      201 Created – Document created and stored on disk
 *      202 Accepted – Document data accepted, but not yet stored on disk
 *      400 Bad Request – Invalid request body or parameters
 *      401 Unauthorized – Write privileges required
 *      404 Not Found – Specified database or document ID doesn’t exists
 *      409 Conflict – Document with the specified ID already exists or specified revision is not 
 *                     latest for target document
 *
 * </pre>
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 *
 */
public class AddCommand extends AbstractCommand implements CouchCommand
{
    private static final Logger LOG = LoggerFactory.getLogger(AddCommand.class);
    private static final Logger LOGSQL = net.sf.jkniv.whinstone.couchdb.LoggerFactory.getLogger();
    private Queryable           queryable;
    private HttpBuilder         httpBuilder;
    
    public AddCommand(HttpBuilder httpBuilder, Queryable queryable)
    {
        super();
        this.httpBuilder = httpBuilder;
        this.queryable = queryable;
        this.method = HttpMethod.PUT;
        this.body = JsonMapper.mapper(queryable.getParams());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T execute()
    {
        String json = null;
        CloseableHttpResponse response = null;
        //Map<String, Object> answer = null;
        T answer = null;
        try
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            String url = httpBuilder.getUrlForAddOrUpdateOrDelete(queryable);
            HttpRequestBase http = null;
            LOGSQL.debug(url);
            if (url.equals(httpBuilder.getHostContext()))
            {
                http = asPost().newHttp(url);
                ((HttpPost) http).setEntity(getEntity());
            }
            else
            {
                http = asPut().newHttp(url);
                ((HttpPut) http).setEntity(getEntity());
            }
            // FIXME supports header request for PUT commands -> Headers: "If-Match", "X-Couch-Full-Commit"
            httpBuilder.setHeader(http);
            response = httpclient.execute(http);
            json = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (isCreated(statusCode))
            {
                processResponse(queryable, json);
                answer = (T) Integer.valueOf("1");
            }
            else if (isAccepted(statusCode))
            {
                LOG.info("Document data accepted, but not yet stored on disk");
                processResponse(queryable, json);
                answer = (T) Integer.valueOf("1");
            }
            else if (isNotFound(statusCode))
            {
                answer = (T) Integer.valueOf("0");
                // 204 No Content, 304 Not Modified, 205 Reset Content, 404 Not Found
                LOG.warn(errorFormat(http, response.getStatusLine(), json));
            }
            else
            {
                Map<String, String> result = JsonMapper.mapper(json, Map.class);
                String reason = result.get("reason");
                LOG.error(errorFormat(http, response.getStatusLine(), json));
                throw new RepositoryException(response.getStatusLine().toString() + ", " + reason);
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
        return answer;
    }
    
    @SuppressWarnings("unchecked")
    private void processResponse(Queryable queryable, String json)
    {
        Map<String, Object> response = JsonMapper.mapper(json, Map.class);
        Insertable sql = queryable.getDynamicSql().asInsertable();
        Object params = queryable.getParams();
        ObjectProxy<?> proxy = ObjectProxyFactory.of(params);
        String id = (String) response.get(COUCHDB_ID);
        String rev = (String) response.get(COUCHDB_REV);
        if (sql.isAutoGenerateKey())
        {
            String properName = sql.getAutoGeneratedKey().getProperties();
            injectAutoIdentity(proxy, params, id, rev, properName);
        }
        else
        {
            injectIdentity(proxy, params, id, rev);
        }
    }
    
    @Override
    public String getBody()
    {
        return this.body;
    }
    
    @Override
    public HttpMethod asPut()
    {
        this.method = HttpMethod.PUT;
        return this.method;
    }
    
    @Override
    public HttpMethod asPost()
    {
        this.method = HttpMethod.POST;
        return this.method;
    }
    
}
