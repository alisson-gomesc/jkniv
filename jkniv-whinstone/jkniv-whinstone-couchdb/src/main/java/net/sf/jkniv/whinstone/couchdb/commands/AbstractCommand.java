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

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;
import net.sf.jkniv.whinstone.Param;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.commands.Command;
import net.sf.jkniv.whinstone.commands.CommandHandler;
import net.sf.jkniv.whinstone.commands.NoCommandHandler;
import net.sf.jkniv.whinstone.params.ParameterNotFoundException;

/**
 * 
 * HTTP code documentation from 
 * <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.18">W3C RFC 2616</a>
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public abstract class AbstractCommand implements CouchCommand
{
    protected final static String COUCHDB_ID  = "id";
    protected final static String COUCHDB_REV = "rev";
    protected HandleableException handlerException;
    protected CommandHandler      commandHandler;
    protected String              url;
    protected String              body;
    protected HttpMethod          method;
    
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
        // TODO design exception message to handler exception
        this.handlerException = new HandlerException(RepositoryException.class, "Cannot set parameter [%s] value [%s]");
        this.url = url;
        this.body = body;
        this.method = HttpMethod.GET;
        this.commandHandler = NoCommandHandler.getInstance();
    }
    
    @Override
    public Command with(HandleableException handlerException)
    {
        this.handlerException = handlerException;
        return this;
    }
    
    @Override
    public Command with(CommandHandler commandHandler)
    {
        this.commandHandler = commandHandler;
        return this;
    }
    
    protected HttpEntity getEntity()
    {
        HttpEntity entity = null;
        entity = new StringEntity(body, Consts.UTF_8); // TODO config charset for HTTP body 
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
    
    protected String errorFormat(HttpRequestBase http, StatusLine statusLine, String json)
    {
        StringBuilder sb = new StringBuilder("URI -> " + http.getURI());
        try
        {
            if (http instanceof HttpPost)
                sb.append("\nHttp Body\n" + EntityUtils.toString(((HttpPost) http).getEntity()));
            else if (http instanceof HttpPut)
                sb.append("\nHttp Body\n" + EntityUtils.toString(((HttpPut) http).getEntity()));
        }
        catch (IOException io)
        {
            sb.append("\nHttp Body fail \n ***" + io.getMessage() + "***");
        }
        sb.append("\n" + http.getMethod() + " " + statusLine.toString() + " " + getContentType(http) + " "
                + getContentEncode(http) + " -> " + json);
        
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
     * @param statusCode HTTP status code
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
    
    /**
     * 201 Created
     * <p>
     * The request has been fulfilled and resulted in a new resource being created. 
     * The newly created resource can be referenced by the URI(s) returned in the 
     * entity of the response, with the most specific URI for the resource given by 
     * a Location header field. The response SHOULD include an entity containing a 
     * list of resource characteristics and location(s) from which the user or user 
     * agent can choose the one most appropriate. The entity format is specified by 
     * the media type given in the Content-Type header field. The origin server MUST 
     * create the resource before returning the 201 status code. If the action cannot 
     * be carried out immediately, the server SHOULD respond with 202 (Accepted) 
     * response instead. 
     * 
     * @param statusCode HTTP status code
     * @return {@code true} when is 202 code, {@code false} otherwise
     */
    protected boolean isCreated(int statusCode)
    {
        return (statusCode == HTTP_CREATED);
    }
    
    /**
     * 202 Accepted
     * <p>
     * The request has been accepted for processing, but the processing has not 
     * been completed. The request might or might not eventually be acted upon, 
     * as it might be disallowed when processing actually takes place. There is 
     * no facility for re-sending a status code from an asynchronous operation 
     * such as this.
     * <p>
     * The 202 response is intentionally non-committal. Its purpose is to allow 
     * a server to accept a request for some other process (perhaps a batch-oriented 
     * process that is only run once per day) without requiring that the user agent's 
     * connection to the server persist until the process is completed. The entity 
     * returned with this response SHOULD include an indication of the request's current 
     * status and either a pointer to a status monitor or some estimate of when the user 
     * can expect the request to be fulfilled. 
     *  
     * @param statusCode HTTP status code
     * @return {@code true} when is 202 code, {@code false} otherwise
     */
    protected boolean isAccepted(int statusCode)
    {
        return (statusCode == HTTP_ACCEPTED);
    }
    
    /**
     * 417 Expectation Faile
     * <p>
     * The expectation given in an Expect request-header field (see section 14.20) 
     * could not be met by this server, or, if the server is a proxy, the server has 
     * unambiguous evidence that the request could not be met by the next-hop server.
     * 
     *  @param statusCode HTTP status code
     *  @return {@code true} when is 202 code, {@code false} otherwise
     */
    protected boolean isExpectationFailed(int statusCode)
    {
        return (statusCode == HTTP_EXPECTATION_FAILED);
    }
    
    protected String getRevision(Queryable queryable)
    {
        Param rev = getProperty(queryable, "rev");
        if (rev.getValue() == null)
            rev = getProperty(queryable, "_rev");
        return rev.getValue().toString();
    }
    
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    protected void injectIdentity(ObjectProxy<?> proxy, Object param, String id, String rev)
    {
        if (param instanceof Map)
        {
            Map map = (Map) param;
            if (!map.containsKey(COUCHDB_ID))
                map.put(COUCHDB_ID, id);
            map.put(COUCHDB_REV, rev);
        }
        else
        {
            if (proxy.hasMethod("setId"))
                proxy.invoke("setId", id);
            if (proxy.hasMethod("setRev"))
                proxy.invoke("setRev", rev);
        }
    }
    
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    protected void injectAutoIdentity(ObjectProxy<?> proxy, Object param, String id, String rev, String properName)
    {
        if (param instanceof Map)
        {
            Map map = (Map) param;
            if (!map.containsKey(properName))
                map.put(properName, id);
            map.put(COUCHDB_REV, rev);
        }
        else
        {
            if (proxy.hasMethod("setId"))
                proxy.invoke("setId", id);
        }
    }
    
    protected void setBookmark(String bookmark, Queryable queryable)
    {
        if (bookmark != null && queryable.getDynamicSql().getSqlDialect().supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY))
            queryable.setBookmark(bookmark);
    }
    
    private Param getProperty(Queryable queryable, String name)
    {
        Param v = null;
        try
        {
            v = queryable.getProperty(name);
        }
        catch (ParameterNotFoundException ignore) {/* parameter not exixts */}
        return (v != null ? v : new Param());
    }
    
}
