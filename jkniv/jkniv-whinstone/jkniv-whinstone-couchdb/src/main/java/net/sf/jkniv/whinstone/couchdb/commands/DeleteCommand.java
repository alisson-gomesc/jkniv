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

/*
 * <pre>
 * 
 * http://docs.couchdb.org/en/2.0.0/api/document/common.html
 * 
 * DELETE /{db}/{docid}
 *
 *  Marks the specified document as deleted by adding a field _deleted with the value true. Documents with this field will not be returned within requests anymore, but stay in the database. You must supply the current (latest) revision, either by using the rev parameter or by using the If-Match header to specify the revision.
 *
 *  Note
 *
 *  CouchDB doesn’t completely delete the specified document. Instead, it leaves a tombstone with very basic information about the document. The tombstone is required so that the delete action can be replicated across databases.
 *
 *  See also
 *
 *  Retrieving Deleted Documents
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
 *      If-Match – Document’s revision. Alternative to rev query parameter
 *      X-Couch-Full-Commit – Overrides server’s commit policy. Possible values are: false and true. Optional
 *
 *  Query Parameters:
 *      
 *
 *      rev (string) – Actual document’s revision
 *      batch (string) – Stores document in batch mode Possible values: ok. Optional
 *
 *  Response Headers:
 *      
 *
 *      Content-Type –
 *          application/json
 *          text/plain; charset=utf-8
 *      ETag – Double quoted document’s new revision
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
 *      200 OK – Document successfully removed
 *      202 Accepted – Request was accepted, but changes are not yet stored on disk
 *      400 Bad Request – Invalid request body or parameters
 *      401 Unauthorized – Write privileges required
 *      404 Not Found – Specified database or document ID doesn’t exists
 *      409 Conflict – Specified revision is not the latest for target document
 *
 *  Request:
 *
 * </pre>
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 *
 */
public class DeleteCommand extends AbstractCommand implements CouchCommand
{
    private static final Logger LOG = LoggerFactory.getLogger(DeleteCommand.class);
    private static final Logger LOGSQL = net.sf.jkniv.whinstone.couchdb.LoggerFactory.getLogger();
    private Queryable           queryable;
    private HttpBuilder         httpBuilder;
    
    public DeleteCommand(HttpBuilder httpBuilder, Queryable queryable)
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
            HttpDelete http = null;
            LOGSQL.debug(url);
            http = (HttpDelete)asDelete().newHttp(url);
            //http.setEntity( getEntity() );
            
            // FIXME supports header request for PUT commands -> Headers: "If-Match", "X-Couch-Full-Commit"
            httpBuilder.setHeader(http);
            http.addHeader("If-Match", getRevision(queryable));
            response = httpclient.execute(http);
            json = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (isOk(statusCode))
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
        return answer;
    }
    
    private void processResponse(Queryable queryable, String json)
    {
        Map<String, Object> response = JsonMapper.mapper(json, Map.class);
        //Updateable sql = queryable.getDynamicSql().asUpdateable();
        Object params = queryable.getParams();
        ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(params);
        String id = (String) response.get(COUCHDB_ID);
        String rev = (String) response.get(COUCHDB_REV);
        if (params instanceof Map)
        {
            if (!((Map) params).containsKey(COUCHDB_ID))
                ((Map) params).put(COUCHDB_ID, id);

            ((Map) params).put(COUCHDB_REV, rev);
        }
        else
        {
            if(proxy.hasMethod("setId"))
                proxy.invoke("setId", id);
            if(proxy.hasMethod("setRev"))
                proxy.invoke("setRev", rev);
        }
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
