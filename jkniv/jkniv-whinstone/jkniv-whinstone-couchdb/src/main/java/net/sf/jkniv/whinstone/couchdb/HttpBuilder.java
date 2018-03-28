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
package net.sf.jkniv.whinstone.couchdb;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;

import net.sf.jkniv.sqlegance.RepositoryException;

public class HttpBuilder
{
    private String        url;
    private String        schema;
    private Charset       charset;
    private RequestParams requestParams;
    
    public HttpBuilder(String url, String schema, RequestParams requestParams)
    {
        super();
        this.url = url;
        this.schema = schema;
        this.requestParams = requestParams;
        this.charset = Charset.forName("UTF-8");
    }
    
    public void setHeader(HttpRequestBase http)
    {
        requestParams.setHeader(http);
    }
    
    public HttpPost newFind(String bodyStr)
    {
        HttpPost httpPost = null;
        try
        {
            StringEntity body = new StringEntity(bodyStr);
            String fullUrl = this.url+"/"+this.schema+"/_find";
            if (this.url.endsWith("/"))
                fullUrl = this.url+this.schema+"/_find";
            
            httpPost = new HttpPost(fullUrl);
            requestParams.setHeader(httpPost);
            httpPost.setEntity(body);
        }
        catch (Exception ex)
        {
            throw new RepositoryException("Cannot build new URI ["+this.url+"/_find]");
        }
        requestParams.setHeader(httpPost);
        return httpPost;
    }

    public HttpPost newFind()
    {
        HttpPost httpPost = null;
        try
        {
            String fullUrl = this.url+"/"+this.schema+"/_find";
            if (this.url.endsWith("/"))
                fullUrl = this.url+this.schema+"/_find";
            
            httpPost = new HttpPost(fullUrl);
            requestParams.setHeader(httpPost);
        }
        catch (Exception ex)
        {
            throw new RepositoryException("Cannot build new URI ["+this.url+"/_find]");
        }
        requestParams.setHeader(httpPost);
        return httpPost;
    }
    

}
