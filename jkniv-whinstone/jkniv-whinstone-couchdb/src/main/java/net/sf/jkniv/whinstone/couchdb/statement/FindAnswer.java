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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import net.sf.jkniv.whinstone.couchdb.commands.JsonMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
{ "warning", "docs", "execution_stats", "bookmark" })
public class FindAnswer
{
    @JsonProperty("warning")
    private String         warning;
    @JsonProperty("bookmark")
    private String         bookmark;
    @JsonProperty("docs")
    private List<Object>   listOfMap = null;
    @JsonProperty("execution_stats")
    private ExecutionStats executionStats;
    
    public String getWarning()
    {
        return warning;
    }
    
    public void setWarning(String warning)
    {
        this.warning = warning;
    }
    
    /**
     * Runtime convert original docs from couchdb answer to
     * list of {@code clazz}
     * @param clazz type of to be converted
     * @param <T> class type of documents
     * @return list of {@code clazz} instances or empty
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> List<T> getDocs(Class<T> clazz)
    {
        List<Object> docs = new ArrayList();
        if (listOfMap != null)
        {
            for (Object row : listOfMap)
                docs.add(JsonMapper.mapper((Map) row, clazz));
        }
        return (List<T>) docs;
    }
    
    public List<Object> getDocs()
    {
        return listOfMap;
    }
    
    public void setDocs(List<Object> docs)
    {
        this.listOfMap = docs;
    }
    
    public ExecutionStats getExecutionStats()
    {
        return executionStats;
    }
    
    public void setExecutionStats(ExecutionStats executionStats)
    {
        this.executionStats = executionStats;
    }
    
    public String getBookmark()
    {
        return bookmark;
    }
    
    public void setBookmark(String bookmark)
    {
        this.bookmark = bookmark;
    }
    
}
