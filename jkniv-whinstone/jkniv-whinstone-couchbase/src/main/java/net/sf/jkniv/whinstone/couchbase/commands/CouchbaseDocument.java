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
package net.sf.jkniv.whinstone.couchbase.commands;

import com.couchbase.client.java.document.Document;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.reflect.beans.PropertyAccess;
import net.sf.jkniv.whinstone.Queryable;

/**
 * Implements the parse value between POJO objects and JsonDocument for Couchbase.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class CouchbaseDocument
{
    private String         id;
    private int            expire;
    private long           cas;
    private JsonObject     jsonObject;
    private boolean        supportsExpire;
    private boolean        supportsCas;
    private ObjectProxy<?> proxy;
    private boolean        json;
    
    public CouchbaseDocument()
    {
        super();
        this.expire = 0;
        this.cas = -1;
        this.supportsCas = false;
        this.supportsExpire = false;
        this.json = false;
    }
    
    public CouchbaseDocument(Queryable queryable)
    {
        this();
        PropertyAccess accessId = queryable.getDynamicSql().getSqlDialect().getAccessId();
        Object value = queryable.getParams();
        this.proxy = ObjectProxyFactory.of(value);
        if (value instanceof JsonDocument)// TODO implements supports for RawJsonDocument, EntityDocument, LegacyDocument
        {
            this.json = true;
            this.supportsExpire = true;
            this.supportsCas = true;
            this.jsonObject = ((JsonDocument)value).content();
            this.id = this.jsonObject.getString("id");
        }
        else if(queryable.isTypeOfBasic())
        {
            this.id = queryable.getParams().toString();
        }
        else
        {
            if (proxy.hasMethod("getExpire"))
            {
                this.supportsExpire = true;
                this.expire = (int) proxy.invoke("getExpire");
            }
            if (this.proxy.hasMethod("getCas"))
            {
                this.supportsCas = true;
                this.cas = (long) proxy.invoke("getCas");
            }
            this.id = String.valueOf(proxy.invoke(accessId.getReadMethodName()));
            this.jsonObject = JsonObject.fromJson(JsonMapper.mapper(value));
        }
    }
    
    public JsonDocument getJsonDocument()
    {
        if (json)
            return (JsonDocument)this.proxy.getInstance();
        
        JsonDocument jsonDocument = null;
        if (hasCas())
        {
            if (hasExpire())
                jsonDocument = JsonDocument.create(id, expire, jsonObject, cas);
            else
                jsonDocument = JsonDocument.create(id, jsonObject, cas);
        }
        else if (hasExpire())
            jsonDocument = JsonDocument.create(id, expire, jsonObject);
        else
            jsonDocument = JsonDocument.create(id, jsonObject);
        
        return jsonDocument;
    }
    
    public void merge(Document<?> document)
    {
        if (hasCas())
            this.proxy.invoke("setCas", document.cas());
    }
    
    public boolean hasCas()
    {
        return this.supportsCas;
    }
    
    public boolean hasExpire()
    {
        return this.supportsExpire;
    }
    
    public long getCas()
    {
        return cas;
    }
    
    public int getExpire()
    {
        return expire;
    }
    
    public JsonObject getJsonObject()
    {
        return this.jsonObject;
    }
    
    public String getId()
    {
        return id;
    }
    
    @Override
    public String toString()
    {
        return "CouchbaseDocument [id=" + id + ", cas=" + cas + ", expire=" + expire + ", jsonObject=" + jsonObject
                + ", supportsExpire=" + supportsExpire + ", supportsCas=" + supportsCas + ", proxy=" + proxy + ", json="
                + json + "]";
    }
    
}
