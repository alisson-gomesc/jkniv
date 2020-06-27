package net.sf.jkniv.whinstone.couchdb.commands;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.sf.jkniv.whinstone.couchdb.CouchDbResult;
import net.sf.jkniv.whinstone.couchdb.ExecutionStats;

class CouchDbResultImpl implements CouchDbResult
{
    private Long totalRows;
    private Long offset;
    private List<?> rows;
    private String bookmark;
    private String warning;
    private ExecutionStats executionStats;

    public CouchDbResultImpl()
    {
    }
    
    public static CouchDbResultImpl of(
            Long totalRows, 
            Long offset, 
            String bookmark, 
            String warning, 
            List<?> rows,
            ExecutionStats executionStats)
    {
        CouchDbResultImpl answer = new CouchDbResultImpl();
        answer.totalRows = (totalRows != null && totalRows > 0) ? totalRows : rows.size();
        answer.offset = offset;
        answer.bookmark = bookmark;
        answer.warning = warning;
        answer.rows = rows;
        answer.executionStats = executionStats;
        return answer;
    }
    
    @Override
    public Long getTotalRows()
    {
        return this.totalRows;
    }
    
    @Override
    public Long getOffset()
    {
        return this.offset;
    }
    @Override
    public List<?> getRows(){
        return this.rows;
    }

    @Override
    public String getBookmark()
    {
        return this.bookmark;
    }

    @Override
    public ExecutionStats getExecutionStats()
    {
        return this.executionStats;
    }
    
    @Override
    public String getWarning()
    {
        return this.warning;
    }
    
    @JsonProperty("docs")
    private void deserializeDocs(List<Map<String,Object>> docs) {
        this.rows = docs;
    }

    @JsonProperty("rows")
    private void deserializeRows(List<Map<String,Object>> rows) {
        this.rows = rows;
    }
    
    
    @JsonProperty("total_rows")
    private void deserializeTotalRows(Long totalRows) {
        this.totalRows = totalRows;
    }
    
    
    @JsonProperty("offset")
    private void deserializeOffset(Long offset) {
        this.offset = offset;
    }
    
    @JsonProperty("bookmark")
    private void deserializeBookmark(String bookmark) {
        this.bookmark = bookmark;
    }
    
    @JsonProperty("warning")
    private void deserializeWarning(String warning) {
        this.warning = warning;
    }
    
    @JsonProperty("execution_stats")
    private void deserializeExecutionStats(ExecutionStats executionStats) {
        this.executionStats = executionStats;
    }

}
