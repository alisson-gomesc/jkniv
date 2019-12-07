package net.sf.jkniv.whinstone.couchbase.statement;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("rawtypes")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "docs" })
public class DocBulk
{
    @JsonProperty("docs")
    private Collection<?> docs = null;
    
    public DocBulk()
    {
        super();
    }
    
    public DocBulk(Collection docs)
    {
        this();
        this.docs = docs;
    }
    
    @JsonProperty("docs")
    public Collection<?> getDocs()
    {
        return docs;
    }
    
    @JsonProperty("docs")
    public void setDocs(Collection<?> docs)
    {
        this.docs = docs;
    }
    
    @Override
    public String toString()
    {
        return "DocBulk [docs=" + docs + "]";
    }
}
