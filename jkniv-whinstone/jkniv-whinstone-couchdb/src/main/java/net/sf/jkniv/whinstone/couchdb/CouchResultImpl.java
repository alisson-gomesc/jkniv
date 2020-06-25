package net.sf.jkniv.whinstone.couchdb;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CouchResultImpl implements CouchResult
{
    private Long totalRows;
    private Long offset;
    private List<?> rows;
    private String bookmark;
    private String warning;
    
    public CouchResultImpl()
    {
    }
    
    public static CouchResultImpl of(Long totalRows, Long offset, String bookmark, String warning, List<?> rows)
    {
        CouchResultImpl answer = new CouchResultImpl();
        answer.totalRows = totalRows;
        answer.offset = offset;
        answer.bookmark = bookmark;
        answer.warning = warning;
        answer.rows = rows;
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
}
