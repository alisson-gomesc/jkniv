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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.couchdb.HttpBuilder;
import net.sf.jkniv.whinstone.couchdb.statement.BulkCommandResponse;
import net.sf.jkniv.whinstone.couchdb.statement.BulkResponse;
import net.sf.jkniv.whinstone.couchdb.statement.DocBulk;

/**
 * <pre>
 * 
 * http://docs.couchdb.org/en/2.2.0/api/database/bulk-api.html
 * 
 *  POST /{db}/_bulk_docs
 *
 *  The bulk document API allows you to create and update multiple 
 *  documents at the same time within a single request. The basic 
 *  operation is similar to creating or updating a single document, 
 *  except that you batch the document structure and information.
 *  
 *  Parameters: 
 *    db – Database name
 *
 *  Request Headers:
 *      
 *      
 *    - Accept 
 *      + application/json
 *      +  text/plain
 *    - Content-Type – application/json
 *    - X-Couch-Full-Commit – Overrides server’s commit policy. Possible values are: false and true. Optional
 *
 * 
 *  Request JSON Object:
 *  
 *     - docs (array) – List of documents objects
 *     - new_edits (boolean) – If false, prevents the database from assigning them new revision IDs. Default is true. Optional
 *     
 *
 *  Response Headers:
 *      
 *
 *      Content-Type
 *        + application/json
 *        + text/plain; charset=utf-8
 *
 *  Response JSON Array of Objects:
 *      
 *      
 *      - id (string) – Document ID
 *      - rev (string) – New document revision token. Available if document has saved without errors. Optional
 *      - error (string) – Error type. Optional
 *      - reason (string) – Error reason. Optional
 *
 *  Status Codes:   
 * 
 *     201 Created – Document(s) have been created or updated
 *     400 Bad Request – The request provided invalid JSON data
 *     417 Expectation Failed – Occurs when at least one document was rejected by a validation function
 * </pre>
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 *
 */
public class BulkCommand extends AbstractCommand implements CouchCommand
{
    private static final Logger LOG    = LoggerFactory.getLogger(BulkCommand.class);
    private static final Logger LOGSQL = net.sf.jkniv.whinstone.couchdb.LoggerFactory.getLogger();
    private Queryable           queryable;
    private HttpBuilder         httpBuilder;
    
    public BulkCommand(HttpBuilder httpBuilder, Queryable queryable)
    {
        super();
        this.httpBuilder = httpBuilder;
        this.queryable = queryable;
        this.method = HttpMethod.POST;
        if (!Collection.class.isInstance(queryable.getParams()))
            throw new RepositoryException("Bulk command no supports "
                    + (queryable.getParams() != null ? queryable.getParams().getClass().getName() : "null")
                    + " type, the parameters of queryable must be a Collection instance and > 0");
        
        if (queryable.getDynamicSql().isDeletable())
        {
            Collection<?> objectsToDelete = (Collection<?>) queryable.getParams();
            boolean broken = true;
            String className = "null";
            for (Object o : objectsToDelete)
            {
                ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(o);
                className = proxy.getTargetClass().getName();
                if (proxy.hasMethod("isDeleted"))
                {
                    Object mustDelete = proxy.invoke("isDeleted");
                    if (mustDelete instanceof Boolean && ((Boolean) mustDelete).booleanValue())
                        broken = false;
                }
            }
            if(broken)
                throw new RepositoryException(
                        "DELETE Bulk command must have an [boolean isDeleted()] method annotated with @JsonProperty(\"_deleted\") for "
                                + className + " type and setted as TRUE.");

        }
        DocBulk docs = new DocBulk((Collection<?>) queryable.getParams());
        this.body = JsonMapper.mapper(docs);
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
            String url = httpBuilder.getUrlForBulk();
            HttpRequestBase http = null;
            http = asPost().newHttp(url);
            ((HttpPost) http).setEntity(getEntity());
            
            // TODO supports header request X-Couch-Full-Commit – Overrides server’s commit policy. Possible values are: false and true. Optional
            httpBuilder.setHeader(http);
            if (LOGSQL.isInfoEnabled())
            {
                LOGSQL.info("\nHTTP POST {}\n{}", url, body);
            }
            
            response = httpclient.execute(http);
            json = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (isCreated(statusCode))
            {
                BulkResponse bulkResponse = processResponse(queryable, json);
                answer = (T) Integer.valueOf(bulkResponse.getTotalOk());
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
    
    @SuppressWarnings("rawtypes")
    private BulkResponse processResponse(Queryable queryable, String json)
    {
        BulkResponse response = new BulkResponse();
        List<BulkCommandResponse> bulk = JsonMapper.mapper(json, new TypeReference<List<BulkCommandResponse>>()
        {
        });
        response.setResponse(bulk);
        Sql sql = queryable.getDynamicSql();
        boolean autoGenerateKey = false;
        boolean isInsertable = sql.isInsertable();
        if (isInsertable && sql.asInsertable().isAutoGenerateKey())
            autoGenerateKey = true;
        
        Collection params = (Collection) queryable.getParams();
        int i = 0, totalOk = 0;
        for (Object param : params)
        {
            BulkCommandResponse commandResponse = bulk.get(i++);
            ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(param);
            String id = (String) commandResponse.getId();
            String rev = (String) commandResponse.getRev();
            boolean isOk = commandResponse.isOk();
            if (autoGenerateKey)
            {
                String properName = sql.asInsertable().getAutoGeneratedKey().getProperties();
                injectAutoIdentity(proxy, param, id, rev, properName);
            }
            else
            {
                injectIdentity(proxy, param, id, rev);
            }
            
            if (isInsertable)
            {
                if (isOk)
                    totalOk++;
                else
                    LOG.error("Document {} insert error", param);
            }
            else
            {
                if (isOk)
                    totalOk++;
                else
                {
                    LOG.error("Document {} update error [{}] reason [{}]", param, commandResponse.getError(),
                            commandResponse.getReason());
                }
            }
        }
        response.setTotalOk(totalOk);
        return response;
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
