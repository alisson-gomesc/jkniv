<<<<<<< HEAD
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
=======

>>>>>>> refs/remotes/origin/master
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
