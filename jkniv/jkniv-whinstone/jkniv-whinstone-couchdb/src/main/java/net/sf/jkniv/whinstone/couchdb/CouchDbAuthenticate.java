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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.sqlegance.RepositoryException;

public class CouchDbAuthenticate
{
    private static final Logger   LOG         = LoggerFactory.getLogger(HttpConnectionFactory.class);
    private static final String   AUTH_COOKIE = "Set-Cookie";
    private final HandlerException handlerException;
    
    public CouchDbAuthenticate()
    {
        this.handlerException = new HandlerException(RepositoryException.class, "Cannot connect to couchdb [%s]");
        configHanlerException();
    }
    
    private void configHanlerException()
    {
        // ClientProtocolException | URISyntaxException | UnsupportedEncodingException | IOException
        handlerException.config(ClientProtocolException.class, "Error at HTTP protocol [%s]");
        handlerException.config(URISyntaxException.class, "Error to parser URI [%s]");
        handlerException.config(UnsupportedEncodingException.class, "Character encoding unsupported");
        handlerException.config(IOException.class, "Error from I/O json content [%s]");
    }

    
    /**
     * Retrieve the cookie session for user authenticated
     * @param url
     * @param username couchdb username
     * @param password couchdb password
     * @return cookie session number for this user authenticated, default session time it's for 10 minutes
     */
    public String authenticate(String url, String username, String password)
    {
        String token = "";
        CloseableHttpResponse response = null;
        try
        {
            URIBuilder uri = new URIBuilder(new URI(url)).setCharset(Charset.defaultCharset()).setPath("_session");
            CloseableHttpClient httpclient = HttpClients.createDefault();
            
            HttpPost httpPost = new HttpPost(uri.build());
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            StringEntity xmlEntity = new StringEntity("name=" + username + "&password=" + password);
            httpPost.setEntity(xmlEntity);
            
            response = httpclient.execute(httpPost);
            LOG.debug(response.getStatusLine().toString());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200)
            {
                HeaderIterator it = response.headerIterator();
                while (it.hasNext())
                {
                    Header header = it.nextHeader();
                    if (AUTH_COOKIE.equals(header.getName()))
                        token = header.getValue().split(";")[0];
                }
            }
            else if (statusCode == 401)
            {
                
                throw new RepositoryException("Access denied, unauthorized");
            }
            else
                throw new RepositoryException("Http code["+statusCode+"] " + response.getStatusLine().getReasonPhrase());
        }
        // ClientProtocolException | URISyntaxException | UnsupportedEncodingException | IOException 
        catch (Exception ex)
        {
            this.handlerException.handle(ex, url);
        }
        finally 
        {
            if(response != null)
            {
                try
                {
                    response.close();
                }
                catch (IOException e)
                {
                    this.handlerException.handle(e, url);
                }
            }
        }
        return token;
    }    
}
