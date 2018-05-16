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

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.params.ParameterNotFoundException;

public abstract class AbstractCommand implements CouchCommand
{
    protected final static String    COUCHDB_ID  = "id";
    protected final static String    COUCHDB_REV = "rev";
    protected final HandlerException handlerException;
    protected String                 url;
    protected String                 body;
    protected HttpMethod             method;
    
    public AbstractCommand()
    {
        this(null);
    }
    
    public AbstractCommand(String url)
    {
        this(url, null);
    }
    
    public AbstractCommand(String url, String body)
    {
        this.handlerException = new HandlerException(RepositoryException.class, "Cannot set parameter [%s] value [%s]");
        this.url = url;
        this.body = body;
        this.method = HttpMethod.GET;
        this.configHanlerException();
    }
    
    private void configHanlerException()
    {
        // ClientProtocolException | JsonParseException | JsonMappingException | IOException
        handlerException.config(ClientProtocolException.class, "Error to HTTP protocol [%s]");
        handlerException.config(JsonParseException.class, "Error to parser json non-well-formed content [%s]");
        handlerException.config(JsonMappingException.class, "Error to deserialization content [%s]");
        handlerException.config(UnsupportedEncodingException.class, "Error at json content encoding unsupported [%s]");
        handlerException.config(IOException.class, "Error from I/O json content [%s]");
        handlerException.mute(ParameterNotFoundException.class);
    }
    
    protected HttpEntity getEntity()
    {
        HttpEntity entity = null;
        entity = new StringEntity(body, Consts.UTF_8);
        return entity;
    }
    
    protected String getContentType(HttpRequestBase http)
    {
        String content = "";
        Header h = http.getFirstHeader(HTTP.CONTENT_TYPE);
        if (h != null)
            content = h.getValue();
        return content;
    }
    
    protected String getContentEncode(HttpRequestBase http)
    {
        String content = "";
        Header h = http.getFirstHeader(HTTP.CONTENT_ENCODING);
        if (h != null)
            content = h.getValue();
        return content;
    }
    
    protected String errorFormat(HttpRequestBase http, StatusLine statusLine,  String json)
    {
        StringBuilder sb = new StringBuilder("URI -> " + http.getURI());
        try
        {
        if (http instanceof HttpPost)
            sb.append("\nHttp Body\n" +EntityUtils.toString( ((HttpPost)http).getEntity()));
        else if (http instanceof HttpPut)
            sb.append("\nHttp Body\n" +EntityUtils.toString( ((HttpPut)http).getEntity()));
        }
        catch (IOException io)
        {
            sb.append("\nHttp Body fail \n ***" +io.getMessage()+"***");
        }
        sb.append("\n" + http.getMethod()+ " "+statusLine.toString()
                + " " + getContentType(http) + " " + getContentEncode(http)
                + " -> " + json);

        return sb.toString();
    }
    
    @Override
    public HttpMethod asPut()
    {
        throw new RepositoryException("Abstract Command cannot be executed as PUT method");
    }
    
    @Override
    public HttpMethod asPost()
    {
        throw new RepositoryException("Abstract Command cannot be executed as POST method");
    }
    
    @Override
    public HttpMethod asDelete()
    {
        throw new RepositoryException("Abstract Command cannot be executed as DELETE method");
    }
    
    @Override
    public HttpMethod asGet()
    {
        throw new RepositoryException("Abstract Command cannot be executed as GET method");
    }
    
    @Override
    public HttpMethod asHead()
    {
        throw new RepositoryException("Abstract Command cannot be executed as HEAD method");
    }
    
    /**
     * Verify if http status represents a record not found
     * @param statusCode http status code
     * @return return {@code true} when the resource it's not found, {@code false} otherwise.
     */
    protected boolean isNotFound(int statusCode)
    {
        return (statusCode == HTTP_NO_CONTENT || statusCode == HTTP_NOT_MODIFIED || statusCode == HTTP_RESET_CONTENT
                || statusCode == HTTP_NOT_FOUND);
    }
    
    protected boolean isOk(int statusCode)
    {
        return (statusCode == HTTP_OK);
    }
    
    protected boolean isCreated(int statusCode)
    {
        return (statusCode == HTTP_CREATED);
    }
    
    protected boolean isAccepted(int statusCode)
    {
        return (statusCode == HTTP_ACCEPTED);
    }
    
    protected String getRevision(Queryable queryable)
    {
        String rev = (String) getProperty(queryable, "rev");
        if (rev == null)
            rev = (String) getProperty(queryable, "_rev");
        return rev;
    }
    
    private Object getProperty(Queryable queryable, String name)
    {
        Object v = null;
        try
        {
            v = queryable.getProperty(name);
        }
        catch (ParameterNotFoundException ignore)
        {
            /* parameter not exixts */}
        return v;
    }
    
}
