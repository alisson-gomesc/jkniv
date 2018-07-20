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
import java.io.UnsupportedEncodingException;
import java.util.Collection;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.couchdb.HttpBuilder;

public class DesignCommand extends AbstractCommand implements CouchCommand
{
    private static final Logger     LOG     = LoggerFactory.getLogger(DesignCommand.class);
    private static final Assertable notNull = AssertsFactory.getNotNull();
    private final HttpBuilder       httpBuilder;
    private final String            docsName;
    private StringBuilder           body;
    private DocumentDesign          documentDesign;
    
    public DesignCommand(HttpBuilder httpBuilder, String docs)
    {
        super();
        this.docsName = docs;
        this.httpBuilder = httpBuilder;
        this.body = new StringBuilder("{ \"views\": {");
        this.method = HttpMethod.PUT;
        this.documentDesign = new DocumentDesign();
    }
    
    public void add(Collection<ViewFunction> views)
    {
        for (ViewFunction view : views)
           this.documentDesign.add(view);
        /* \/\/ JSON SAMPLE \/\/ */
        /*
            "all": {
              "map": "function(doc) { emit(doc.title, doc) }",
              "reduce": "function(key, values){ return sum(values); }"
            }
        */
        /* /\/\ JSON SAMPLE /\/\ */
        //String name = view.getMapfun().getName().substring(4);
    }
    
    @Override
    public <T> T execute()
    {
        String json = null;
        CloseableHttpResponse response = null;
        DocumentDesign docDesign = getViewer();
        try
        {
            if (docDesign != null)
            {
                documentDesign.setId(docDesign.getId());
                documentDesign.setRev(docDesign.getRev());
            }
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPut httpPut = new HttpPut(httpBuilder.getHostContext() + docsName);
            httpBuilder.setHeader(httpPut);
            httpPut.setEntity(getEntity());
            LOG.debug(httpPut.getURI().toString());
            response = httpclient.execute(httpPut);
            json = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HTTP_CREATED)
            {
                LOG.info("{} view as created successfully {}", docsName, json);
            }
            else if (statusCode == HTTP_OK)
            {
                LOG.info("{} view as updated successfully {}", docsName, json);
            }
            else if (statusCode == HTTP_NO_CONTENT || statusCode == HTTP_NOT_MODIFIED
                    || statusCode == HTTP_RESET_CONTENT)
            {
                // 204 No Content, 304 Not Modified, 205 Reset Content
                LOG.info(response.getStatusLine().toString());
            }
            else
            {
                LOG.error("Http Body\n{}", EntityUtils.toString(httpPut.getEntity()));
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
    
    private DocumentDesign getViewer()
    {
        String json = null;
        CloseableHttpResponse response = null;
        DocumentDesign docDesign = null;
        try
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            
            HttpGet httpGet = new HttpGet(httpBuilder.getHostContext() + docsName);
            httpBuilder.setHeader(httpGet);
            LOG.debug(httpGet.getURI().toString());
            response = httpclient.execute(httpGet);
            json = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HTTP_OK)
            {
                docDesign = JsonMapper.mapper(json, DocumentDesign.class);
                LOG.info("Views from design {} are replaced", docsName);
            }
        }
        catch (Exception e) // ClientProtocolException | JsonParseException | JsonMappingException | IOException
        {
            handlerException.handle(e);
        }
        return docDesign;
    }
    
    @Override
    public String getBody()
    {
        return this.body.toString();
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
        throw new RepositoryException("Design Command cannot be executed as POST method");
        
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
    
    @Override
    protected HttpEntity getEntity()
    {
        HttpEntity entity = null;
        try
        {
            entity = new StringEntity(JsonMapper.mapper(documentDesign));
        }
        catch (UnsupportedEncodingException e)
        {
            handlerException.handle(e);
        }
        return entity;
    }
    
}
