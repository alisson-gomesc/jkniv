
package net.sf.jkniv.whinstone.couchdb.statement;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "_id", "_rev", "name", "born", "nationality", "books", "_revisions", "_local_seq" })
public class GetAnswer
{
    
    @JsonProperty("_id")
    private String              id;
    @JsonProperty("_rev")
    private String              rev;
    @JsonProperty("_revisions")
    private Revisions           revisions;
    @JsonProperty("_local_seq")
    private Long                localSeq;

    
    @JsonProperty("_deleted")
    private Boolean deleted;
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    @JsonProperty("_id")
    public String getId()
    {
        return id;
    }
    
    @JsonProperty("_id")
    public void setId(String id)
    {
        this.id = id;
    }
    
    @JsonProperty("_rev")
    public String getRev()
    {
        return rev;
    }
    
    @JsonProperty("_rev")
    public void setRev(String rev)
    {
        this.rev = rev;
    }
    
    @JsonProperty("_revisions")
    public Revisions getRevisions()
    {
        return revisions;
    }
    
    @JsonProperty("_revisions")
    public void setRevisions(Revisions revisions)
    {
        this.revisions = revisions;
    }
    
    @JsonProperty("_local_seq")
    public Long getLocalSeq()
    {
        return localSeq;
    }
    
    @JsonProperty("_local_seq")
    public void setLocalSeq(Long localSeq)
    {
        this.localSeq = localSeq;
    }
    
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties()
    {
        return this.additionalProperties;
    }
    
    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value)
    {
        this.additionalProperties.put(name, value);
    }
    
}
