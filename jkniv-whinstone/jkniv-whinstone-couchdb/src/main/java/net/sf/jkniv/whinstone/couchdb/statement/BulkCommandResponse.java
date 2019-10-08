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
package net.sf.jkniv.whinstone.couchdb.statement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "id", "ok", "rev", "error", "reason" })
public class BulkCommandResponse
{
    @JsonProperty("id")
    private String  id;
    @JsonProperty("ok")
    private Boolean ok;
    @JsonProperty("rev")
    private String  rev;
    @JsonProperty("error")
    private String  error;
    @JsonProperty("reason")
    private String  reason;
    
    @JsonProperty("id")
    public String getId()
    {
        return id;
    }
    
    @JsonProperty("id")
    public void setId(String id)
    {
        this.id = id;
    }
    
    @JsonProperty("ok")
    public Boolean getOk()
    {
        return ok;
    }
    
    @JsonProperty("ok")
    public void setOk(Boolean ok)
    {
        this.ok = ok;
    }
    
    @JsonProperty("rev")
    public String getRev()
    {
        return rev;
    }
    
    @JsonProperty("rev")
    public void setRev(String rev)
    {
        this.rev = rev;
    }
    
    @JsonProperty("error")
    public String getError()
    {
        return error;
    }
    
    @JsonProperty("error")
    public void setError(String error)
    {
        this.error = error;
    }
    
    @JsonProperty("reason")
    public String getReason()
    {
        return reason;
    }
    
    @JsonProperty("reason")
    public void setReason(String reason)
    {
        this.reason = reason;
    }

    @JsonIgnore
    public boolean hasError()
    {
        return (this.error != null && this.error.length() > 0);
    }

    @JsonIgnore
    public boolean isOk()
    {
        return (this.ok != null && this.ok.booleanValue()) || (!hasError());
    }

    @Override
    public String toString()
    {
        return "BulkResponse [id=" + id + ", ok=" + ok + ", rev=" + rev + ", error=" + error + ", reason=" + reason
                + "]";
    }
    
}
