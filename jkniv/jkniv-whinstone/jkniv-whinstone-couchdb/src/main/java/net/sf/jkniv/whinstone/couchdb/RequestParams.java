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

import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

class RequestParams
{
    private String connection;  // Connection: keep-alive
    private String cookie;      // Cookie: AuthSession=YWRtaW46NUFCN0Y1Qzc6SQD7rM4vjA42_xp5ngAXYojGCEI
    private String accept;      // Accept: application/json, text/javascript, */*; q=0.01
    private String contentType; // Content-Type: application/x-www-form-urlencoded; charset=UTF-8
    private String schema;
    private long sessionTimeout;
    
    public RequestParams(String cookie, String schema)
    {
        super();
        this.connection = HTTP.CONN_KEEP_ALIVE;
        //this.accept = "application/json";
        this.contentType = "application/json; charset=UTF-8";
        this.sessionTimeout = TimeUnit.MINUTES.toSeconds(10L);// default cookie authentication timeout
        this.cookie = cookie;
    }

    public void setHeader(HttpRequestBase http)
    {
        if (connection != null)
            http.addHeader("Connection", connection);
        if (cookie != null)
            http.addHeader("Cookie", cookie);
        if (accept != null)
            http.addHeader("Accept", accept);
        if (contentType != null)
            http.addHeader(HTTP.CONTENT_TYPE, contentType);
    }
    
    public void setConnection(String connection)
    {
        this.connection = connection;
    }

    public Header getConnection()
    {
        return new BasicHeader("Connection", connection);
    }


    public void setCookie(String cookie)
    {
        this.cookie = cookie;
    }

    public Header getCookie()
    {
        return new BasicHeader("Cookie", cookie);
    }


    public void setAccept(String accept)
    {
        this.accept = accept;
    }
    
    public Header getAccept()
    {
        return new BasicHeader("Accept", accept);
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
    
    public Header getContentType()
    {
        return new BasicHeader(HTTP.CONTENT_TYPE, contentType);
    }
}
