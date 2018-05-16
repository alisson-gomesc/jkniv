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

<<<<<<< HEAD
import static net.sf.jkniv.whinstone.couchdb.statement.AllDocsQueryParams.KEY_limit;
import static net.sf.jkniv.whinstone.couchdb.statement.AllDocsQueryParams.KEY_skip;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.couchdb.statement.QueryParam;
import net.sf.jkniv.whinstone.params.ParameterNotFoundException;

public class HttpBuilder
{
    private final String                  url;
    private final String                  schema;
    private final String                  hostContext;
    private final Charset                 charset;
    private RequestParams                 requestParams;
    private CouchDbAuthenticate           auth;
    
    private static final List<String>     KEY_PARAMS_ALLDOCS     = Arrays.asList("conflicts", "descending", "endkey",
            "end_key", "endkey_docid", "end_key_doc_id", "include_docs", "inclusive_end", "key", "keys", "stale",
            "startkey", "start_key", "startkey_docid", "start_key_doc_id", "update_seq");                            /*"limit","skip"*/
    
    private static final List<QueryParam> KEY_PARAMS_VIEW        = Arrays.asList(new QueryParam("key", true),
            new QueryParam("keys", true), new QueryParam("startkey", true), new QueryParam("endkey", true),
            new QueryParam("descending"), new QueryParam("group"), new QueryParam("group_level"),
            new QueryParam("startkey_docid"), new QueryParam("endkey_docid"), new QueryParam("limit"),
            new QueryParam("skip"), new QueryParam("descending"), new QueryParam("stale"), new QueryParam("reduce"),
            new QueryParam("include_docs"), new QueryParam("inclusive_end"), new QueryParam("update_seq"));
    
    private static final List<QueryParam> KEY_PARAMS_GET         = Arrays.asList(new QueryParam("attachments"),
            new QueryParam("att_encoding_info"), new QueryParam("atts_since"), new QueryParam("conflicts"),
            new QueryParam("deleted_conflicts"), new QueryParam("latest"), new QueryParam("local_seq"),
            new QueryParam("meta"), new QueryParam("open_revs"), new QueryParam("rev"), new QueryParam("revs"),
            new QueryParam("revs_info"));
    
    private static final List<QueryParam> KEY_PARAMS_SAVE        = Arrays.asList(new QueryParam("batch"),
            new QueryParam("new_edits"));
    
    private static final List<String>     KEY_PARAMS_UNSUPPORTED = Arrays.asList("attachments");
    
    public HttpBuilder(CouchDbAuthenticate auth, String url, String schema, RequestParams requestParams)
    {
        super();
        this.auth = auth;
        this.url = url;
        this.schema = schema;
        this.hostContext = getHostContext();
        this.requestParams = requestParams;
        this.charset = Charset.forName("UTF-8");
    }
    
    public void setHeader(HttpRequestBase http)
    {
        String token = auth.getCookieSession();
        
        if (auth.isExpired())
            token = auth.authenticate();
        
        http.addHeader("Cookie", token);
        requestParams.setHeader(http);
    }
    
    /**
     * Return the URL for _design of documents.
     * <p>
     * <code>
     * http://{host}:{port}/{schema}/_design/
     * </code>
     * 
     * @return URL follow the pattern http://{host}:{port}/{schema}/_design/ 
     */
    public String getUrlForDesign()
    {
        return this.hostContext + "_design/";
    }
    
    public HttpPost newFind(String bodyStr)
    {
        HttpPost httpPost = null;
        try
        {
            StringEntity body = new StringEntity(bodyStr);
            String fullUrl = this.hostContext + "_find";
            httpPost = new HttpPost(fullUrl);
            requestParams.setHeader(httpPost);
            httpPost.setEntity(body);
        }
        catch (Exception ex)
        {
            throw new RepositoryException("Cannot build new URI [" + this.hostContext + "_find]");
        }
        return httpPost;
    }
    
    public HttpPost newFind()
    {
        HttpPost http = null;
        try
        {
            String fullUrl = this.hostContext + "_find";
            http = new HttpPost(fullUrl);
            requestParams.setHeader(http);
        }
        catch (Exception ex)
        {
            throw new RepositoryException("Cannot build new URI [" + this.hostContext + "_find]");
        }
        return http;
    }
    
    public String getUrlForView(Queryable queryable)
    {
        checkUnsupportedQueryParams(queryable);
        StringBuilder urlParams = new StringBuilder("?");
        StringBuilder urlAllDocs = new StringBuilder(this.hostContext + "_design/" + queryable.getName());
        try
        {
            for (QueryParam k : KEY_PARAMS_VIEW)
            {
                Object v = getProperty(queryable, k.name());
                if (v != null)
                {
                    if (urlParams.length() > 1)
                        urlParams.append("&" + k.name() + "=" + k.getValue(v));
                    else
                        urlParams.append(k.name() + "=" + k.getValue(v));
                }
            }
        }
        catch (Exception ex)
        {
            throw new RepositoryException("Cannot build new URI [" + this.hostContext + "_view]");
        }
        return urlAllDocs.toString() + urlParams.toString();
    }
    
    public String getUrlForGet(Queryable queryable)
    {
        checkUnsupportedQueryParams(queryable);
        StringBuilder urlParams = new StringBuilder("?");
        StringBuilder urlGet = new StringBuilder(this.hostContext);
        if (queryable.isTypeOfBasic())
            urlGet.append(queryable.getParams());// FIXME design docid is Date, Calendar, how to parser
        else
        {
            Object id = (String) getProperty(queryable, "id");
            if (id != null)
                urlGet.append(id);
            else
            {
                id = (String) getProperty(queryable, "_id");
                if (id != null)
                    urlGet.append(id);
                else
                {
                    id = (String) getProperty(queryable, "docid");
                    if (id != null)
                        urlGet.append(id);
                    else
                        throw new RepositoryException("Cannot lookup [ id | _id | docid ] from [" + queryable + "]");
                    
                }
            }
        }
        try
        {
            for (QueryParam k : KEY_PARAMS_GET)
            {
                Object v = getProperty(queryable, k.name());
                if (v != null)
                {
                    if (urlParams.length() > 1)
                        urlParams.append("&" + k.name() + "=" + k.getValue(v));
                    else
                        urlParams.append(k.name() + "=" + k.getValue(v));
                }
            }
        }
        catch (Exception ex)
        {
            throw new RepositoryException("Cannot build new URI [" + urlGet.toString() + urlParams.toString() + "]");
        }
        return urlGet.toString() + urlParams.toString();
    }
    
    public String getUrlForAddOrUpdateOrDelete(Queryable queryable)
    {
        checkUnsupportedQueryParams(queryable);
        StringBuilder urlSave = new StringBuilder(this.hostContext);
        Object id = (String) getProperty(queryable, "id");
        if (id != null)
            urlSave.append(id);
        else
        {
            id = (String) getProperty(queryable, "_id");
            if (id != null)
                urlSave.append(id);
            else
            {
                id = (String) getProperty(queryable, "docid");
                if (id != null)
                    urlSave.append(id);
            }
        }
        return urlSave.toString();
    }

    public String getUrlForAllDocs(Queryable queryable)
    {
        checkUnsupportedQueryParams(queryable);
        StringBuilder urlParams = new StringBuilder("?");
        StringBuilder urlAllDocs = new StringBuilder(this.hostContext + "_all_docs");
        try
        {
            if (queryable.isPaging())
                urlParams.append(KEY_limit + "=" + queryable.getMax() + "&" + KEY_skip + "=" + queryable.getOffset());
            
            for (String k : KEY_PARAMS_ALLDOCS)
            {
                Object v = getProperty(queryable, k);
                if (v != null)
                {
                    if (urlParams.length() > 1)
                        urlParams.append("&" + k + "=" + v.toString());
                    else
                        urlParams.append(k + "=" + v.toString());
                }
            }
        }
        catch (Exception ex)
        {
            throw new RepositoryException("Cannot build new URI [" + this.hostContext + "_all_docs]");
        }
        return urlAllDocs.toString() + urlParams.toString();
    }
    
    public String getHostContext()
    {
        String hostContext = this.url + "/" + this.schema;
        if (this.url.endsWith("/"))
            hostContext = this.url + this.schema;
        
        return hostContext + "/";
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
    
    private void checkUnsupportedQueryParams(Queryable queryable)
    {
        for (String k : KEY_PARAMS_UNSUPPORTED)
        {
            Object v = getProperty(queryable, k);
            if (v != null)
                throw new RepositoryException("Query Parameters [" + k + "] isn't supported yet!");
        }
    }
=======
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
    

>>>>>>> refs/remotes/origin/master
}
