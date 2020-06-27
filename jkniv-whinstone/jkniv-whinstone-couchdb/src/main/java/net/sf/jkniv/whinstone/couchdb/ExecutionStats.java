
package net.sf.jkniv.whinstone.couchdb;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "total_keys_examined",
    "total_docs_examined",
    "total_quorum_docs_examined",
    "results_returned",
    "execution_time_ms"
})
public class ExecutionStats {

    @JsonProperty("total_keys_examined")
    private Long totalKeysExamined;
    @JsonProperty("total_docs_examined")
    private Long totalDocsExamined;
    @JsonProperty("total_quorum_docs_examined")
    private Long totalQuorumDocsExamined;
    @JsonProperty("results_returned")
    private Long resultsReturned;
    @JsonProperty("execution_time_ms")
    private Double executionTimeMs;
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("total_keys_examined")
    public Long getTotalKeysExamined() {
        return totalKeysExamined;
    }

    @JsonProperty("total_keys_examined")
    public void setTotalKeysExamined(Long totalKeysExamined) {
        this.totalKeysExamined = totalKeysExamined;
    }

    @JsonProperty("total_docs_examined")
    public Long getTotalDocsExamined() {
        return totalDocsExamined;
    }

    @JsonProperty("total_docs_examined")
    public void setTotalDocsExamined(Long totalDocsExamined) {
        this.totalDocsExamined = totalDocsExamined;
    }

    @JsonProperty("total_quorum_docs_examined")
    public Long getTotalQuorumDocsExamined() {
        return totalQuorumDocsExamined;
    }

    @JsonProperty("total_quorum_docs_examined")
    public void setTotalQuorumDocsExamined(Long totalQuorumDocsExamined) {
        this.totalQuorumDocsExamined = totalQuorumDocsExamined;
    }

    @JsonProperty("results_returned")
    public Long getResultsReturned() {
        return resultsReturned;
    }

    @JsonProperty("results_returned")
    public void setResultsReturned(Long resultsReturned) {
        this.resultsReturned = resultsReturned;
    }

    @JsonProperty("execution_time_ms")
    public Double getExecutionTimeMs() {
        return executionTimeMs;
    }

    @JsonProperty("execution_time_ms")
    public void setExecutionTimeMs(Double executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString()
    {
        return "ExecutionStats [totalKeysExamined=" + totalKeysExamined + ", totalDocsExamined=" + totalDocsExamined
                + ", totalQuorumDocsExamined=" + totalQuorumDocsExamined + ", resultsReturned=" + resultsReturned
                + ", executionTimeMs=" + executionTimeMs + "]";
    }

}
