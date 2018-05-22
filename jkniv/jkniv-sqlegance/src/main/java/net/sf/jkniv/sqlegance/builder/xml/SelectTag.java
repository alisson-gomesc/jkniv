/* 
 * JKNIV, SQLegance keeping queries maintainable.
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
package net.sf.jkniv.sqlegance.builder.xml;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jkniv.cache.Cacheable;
import net.sf.jkniv.cache.MemoryCache;
import net.sf.jkniv.cache.NoCache;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.OneToMany;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.statement.ResultSetConcurrency;
import net.sf.jkniv.sqlegance.statement.ResultSetHoldability;
import net.sf.jkniv.sqlegance.statement.ResultSetType;
import net.sf.jkniv.sqlegance.validation.ValidateType;
import net.sf.jkniv.sqlegance.transaction.Isolation;

/**
 * Tag of select sentence.
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 */
class SelectTag extends AbstractSqlTag implements Selectable
{
    private String          groupBy;
    private Set<OneToMany>  oneToMany;
    private String          cacheName;
    private Cacheable       cache;
    
    /**
     * Build a new <code>select</code> tag from XML file.
     * 
     * @param id
     *            Name/Identify from tag.
     * @param languageType type of language from tag.
     */
    public SelectTag(String id, LanguageType languageType)
    {
        super(id, languageType);
        init();
    }
    
    /**
     * Build a new <code>select</code> tag from XML file.
     * 
     * @param id Name/Identify from tag
     * @param languageType type of language from tag
     * @param sqlDialect dialect from database
     */
    public SelectTag(String id, LanguageType languageType, SqlDialect sqlDialect)
    {
        super(id, languageType, sqlDialect);
        init();
    }

    
    /**
     * Build a new <code>select</code> tag from XML file.
     * 
     * @param id
     *            Name/Identify from tag.
     * @param languageType
     *            type of language from tag.
     * @param isolation Retrieves the current transaction isolation level for the query.
     * @param timeout Retrieves the number of seconds the repository will wait for a Query
     * object to execute.
     * @param batch Indicate if query is a batch of commands.
     * @param cacheName cache name to keep result queries
     * @param hint A SQL hint can be used on certain database platforms.
     * @param resultSetType TODO javadoc
     * @param resultSetConcurrency TODO javadoc
     * @param resultSetHoldability holdability from {@code ResultSet}
     * @param returnType type of object must be returned
     * @param groupBy columns name to group the result of query (columns name separated by comma)
     * @param validateType validation to apply before execute SQL.
     */
    public SelectTag(String id, LanguageType languageType, Isolation isolation, int timeout, boolean batch,
            String cacheName, String hint, ResultSetType resultSetType, ResultSetConcurrency resultSetConcurrency,
            ResultSetHoldability resultSetHoldability, String returnType, String groupBy, ValidateType validateType)
    {
        super(id, languageType, isolation, timeout, batch/*, cache*/, hint, resultSetType, resultSetConcurrency,
                resultSetHoldability, returnType, validateType);
        this.groupBy = groupBy;
        this.oneToMany = new HashSet<OneToMany>();
        this.cacheName = cacheName;
        if(cacheName != null && !"".equals(cacheName.trim()))
            this.cache = new MemoryCache(cacheName);
        else
            this.cache = NoCache.getInstance();
    }

    private void init()
    {
        this.oneToMany = new HashSet<OneToMany>();
        this.groupBy = "";
        this.cacheName = null;
        this.cache = NoCache.getInstance();
    }
    /**
     * Retrieve the tag name.
     * 
     * @return name from tag <code>select</code>.
     */
    public String getTagName()
    {
        return TAG_NAME;
    }
    
    /**
     * Command type to execute.
     * 
     * @return the type of command used, <code>SELECT</code>.
     */
    @Override
    public SqlType getSqlType()
    {
        return SqlType.SELECT;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.builder.xml.Selectable#getGroupBy()
     */
    @Override
    public String getGroupBy()
    {
        return groupBy;
    }
    
    public void setGroupBy(String groupBy)
    {
        this.groupBy = groupBy;
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.builder.xml.Selectable#getGroupByAsList()
     */
    @Override
    public List<String> getGroupByAsList()
    {
        if (groupBy == null || "".equals(groupBy))
            return Collections.emptyList();
        
        return Arrays.asList(groupBy.split(","));
    }
    
    /* (non-Javadoc)
     * @see net.sf.jkniv.sqlegance.builder.xml.Selectable#getOneToMany()
     */
    @Override
    public Set<OneToMany> getOneToMany()
    {
        return oneToMany;
    }
    
    public void addOneToMany(OneToMany oneToMany)
    {
        this.oneToMany.add(oneToMany);
    }
    
    @Override
    public boolean isSelectable()
    {
        return true;
    }
    
    @Override
    public Selectable asSelectable()
    {
        return this;
    }

    @Override
    public boolean hasCache()
    {
        return (this.cacheName != null && "".equals(this.cacheName.trim()));
    }

    @Override
    public <K,V> Cacheable<K,V> getCache()
    {
        return this.cache;
    }
}
