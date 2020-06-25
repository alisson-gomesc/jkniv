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

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "offset", "rows", "total_rows" })
@SuppressWarnings("rawtypes")
public class AllDocsAnswer
{
    @JsonProperty("offset")
    private Long                offset;
    
    @JsonProperty("total_rows")
    private Long                totalRows;
    
    @JsonProperty("rows")
    private List<Map>           rows = null;
    
    @JsonProperty("offset")
    public Long getOffset()
    {
        return offset;
    }
    
    @JsonProperty("offset")
    public void setOffset(Long offset)
    {
        this.offset = offset;
    }
    
    @JsonProperty("rows")
    public List<Map> getRows()
    {
        return rows;
    }
    
    @JsonProperty("rows")
    public void setRows(List<Map> rows)
    {
        this.rows = rows;
    }
    
    @JsonProperty("total_rows")
    public Long getTotalRows()
    {
        return totalRows;
    }
    
    @JsonProperty("total_rows")
    public void setTotalRows(Long totalRows)
    {
        this.totalRows = totalRows;
    }
}
