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
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.http.Consts;
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
    private static final Logger    LOG         = LoggerFactory.getLogger(CouchDbAuthenticate.class);
    private static final String    AUTH_COOKIE = "Set-Cookie";
    private final HandlerException handlerException;
    private Date startSession;
    private long sessionTimeout;
    private String url;
    private String username;
    private String password;
    private String cookieSession;

    /* Header Response from CouchDB authentication http://localhost:5984/_session

Cache-Control: must-revalidate
Content-Length: 41
Content-Type: application/json
Date: Wed, 06 Jan 2021 22:25:53 GMT
Server: CouchDB/3.1.1 (Erlang OTP/20)
Set-Cookie: AuthSession=ZGV2YWRtaW46NUZGNjM4RjE6IAmuiZI0H6cFmMDcuax-2_afG6Q; Version=1; Expires=Wed, 06-Jan-2021 22:35:53 GMT; Max-Age=600; Path=/; HttpOnly

 
     */
    /**
     * Build a new Authentication with 10 minutes to expire after authenticate.
     * @param url Uniform Resource Locator pointing to CouchDb instance
     * @param username couchDb user name
     * @param password couchDb password
     */
    public CouchDbAuthenticate(String url, String username, String password)
    {
        super();
        this.url = url;
        this.username = username;
        this.password = password;
        this.sessionTimeout = TimeUnit.MINUTES.toMillis(10L);
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
     * @return cookie session number for this user authenticated, default session time it's for 10 minutes
     */
    public String authenticate()
    {
        String token = "";
        CloseableHttpResponse response = null;
        try
        {
            URIBuilder uri = new URIBuilder(new URI(url)).setCharset(Charset.defaultCharset()).setPath("_session");
            CloseableHttpClient httpclient = HttpClients.createDefault();
            
            HttpPost httpPost = new HttpPost(uri.build());
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            StringEntity xmlEntity = new StringEntity("name=" + username + "&password=" + password, Consts.UTF_8); // TODO config charset for HTTP body
            httpPost.setEntity(xmlEntity);
            
            response = httpclient.execute(httpPost);
            LOG.debug(response.getStatusLine().toString());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200)
            {
                this.startSession = new Date();
                HeaderIterator it = response.headerIterator();
                while (it.hasNext())
                {
                    Header header = it.nextHeader();
                    if (AUTH_COOKIE.equals(header.getName()))
                    {
                        token = header.getValue().split(";")[0];
                        this.cookieSession = token;
                    }
                }
            }
            else if (statusCode == 401)
            {
                throw new RepositoryException("Access denied, unauthorized for user ["+username+"] and url ["+url+"]");
            }
            else
                throw new RepositoryException(
                        "Http code[" + statusCode + "] " + response.getStatusLine().getReasonPhrase());
        }
        // ClientProtocolException | URISyntaxException | UnsupportedEncodingException | IOException 
        catch (Exception ex)
        {
            this.handlerException.handle(ex, url);
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
                    this.handlerException.handle(e, url);
                }
            }
        }
        return token;
    }
    
    public String getCookieSession()
    {
        return cookieSession;
    }
    
    public boolean isExpired() 
    {
        if (this.startSession == null)
            return true;
        
        return (new Date().getTime() > (this.startSession.getTime()+sessionTimeout));
    }
    
    public void keepAlive()
    {
        this.startSession = new Date();
    }
}
