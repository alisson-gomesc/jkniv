
package net.sf.jkniv.whinstone.couchdb.statement;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "warning", "docs", "execution_stats" })
public class FindAnswer
{
    
    @JsonProperty("warning")
    private String         warning;
    @JsonProperty("docs")
    private List<Object>   docs = null;
    @JsonProperty("execution_stats")
    private ExecutionStats executionStats;
    
    @JsonProperty("warning")
    public String getWarning()
    {
        return warning;
    }
    
    @JsonProperty("warning")
    public void setWarning(String warning)
    {
        this.warning = warning;
    }
    
    @JsonProperty("docs")
    public List<Object> getDocs()
    {
        return docs;
    }
    
    @JsonProperty("docs")
    public void setDocs(List<Object> docs)
    {
        this.docs = docs;
    }
    
    @JsonProperty("execution_stats")
    public ExecutionStats getExecutionStats()
    {
        return executionStats;
    }
    
    @JsonProperty("execution_stats")
    public void setExecutionStats(ExecutionStats executionStats)
    {
        this.executionStats = executionStats;
    }
    
}
