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
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.reflect.beans.PropertyAccess;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.couchdb.HttpBuilder;

/*
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
public class UpdateCommand extends AbstractCommand implements CouchCommand
{
    private static final Logger LOG = LoggerFactory.getLogger(UpdateCommand.class);
    private static final Logger LOGSQL = net.sf.jkniv.whinstone.couchdb.LoggerFactory.getLogger();
    private Queryable           queryable;
    private HttpBuilder         httpBuilder;
    
    public UpdateCommand(HttpBuilder httpBuilder, Queryable queryable)
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
            HttpPut http = null;
            http = (HttpPut)asPut().newHttp(url);
            http.setEntity( getEntity() );
            
            // FIXME supports header request for PUT commands -> Headers: "If-Match", "X-Couch-Full-Commit"
            httpBuilder.setHeader(http);
            printRequest(http);            
            response = httpclient.execute(http);
            json = EntityUtils.toString(response.getEntity());
            printResponse(response, json);

            int statusCode = response.getStatusLine().getStatusCode();
            if (isCreated(statusCode))
            {
                processResponse(queryable, json);
                answer = (T)Integer.valueOf("1");
            }
            else if (isAccepted(statusCode))
            {
                LOG.info("Document data accepted, but not yet stored on disk");
                processResponse(queryable, json);
                answer = (T)Integer.valueOf("1");
            }
            else if (isNotFound(statusCode))
            {
                answer = (T)Integer.valueOf("0");
                // 204 No Content, 304 Not Modified, 205 Reset Content, 404 Not Found
                LOG.warn(errorFormat(http, response.getStatusLine(), json));
            }
            else
            {
                Map<String,String> result = JsonMapper.mapper(json, Map.class);
                String reason = result.get("reason");
                LOG.error(errorFormat(http, response.getStatusLine(), json));
                throw new RepositoryException(response.getStatusLine().toString() +", "+ reason);
            }
            //this.commandHandler.postCommit();
        }
        catch (Exception e) // ClientProtocolException | JsonParseException | JsonMappingException | IOException
        {
            //this.commandHandler.postException();
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
    
    private void processResponse(Queryable queryable, String json)
    {
        Map<String, Object> response = JsonMapper.mapper(json, Map.class);
        Object params = queryable.getParams();
        ObjectProxy<?> proxy = ObjectProxyFactory.of(params);
        PropertyAccess accessId = queryable.getDynamicSql().getSqlDialect().getAccessId();
        PropertyAccess accessRev = queryable.getDynamicSql().getSqlDialect().getAccessRevision();
        String id = (String) response.get(accessId.getFieldName());
        String rev = (String) response.get(accessRev.getFieldName());
        if (params instanceof Map)
        {
            if (!((Map) params).containsKey(accessId.getFieldName()))
                ((Map) params).put(accessId.getFieldName(), id);

            ((Map) params).put(accessRev.getFieldName(), rev);
        }
        else
        {
            proxy.invoke(accessId.getWriterMethodName(), id);
            proxy.invoke(accessRev.getWriterMethodName(), rev);
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
