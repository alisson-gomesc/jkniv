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
package net.sf.jkniv.whinstone.jpa2;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.sf.jkniv.cache.Cacheable;
import net.sf.jkniv.cache.NoCache;
import net.sf.jkniv.sqlegance.Deletable;
import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.OneToMany;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.Statistical;
import net.sf.jkniv.sqlegance.Storable;
import net.sf.jkniv.sqlegance.Updateable;
import net.sf.jkniv.sqlegance.builder.xml.SqlStats;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.sqlegance.params.ParamParserNoMark;
import net.sf.jkniv.sqlegance.statement.ResultSetConcurrency;
import net.sf.jkniv.sqlegance.statement.ResultSetHoldability;
import net.sf.jkniv.sqlegance.statement.ResultSetType;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.sqlegance.validation.ValidateType;

class NamedQueryForSql implements Sql, Selectable
{
    private String      name;
    private Date        timestamp;
    private SqlDialect  sqlDialect;
    private Statistical stats;
    private String      groupBy;
    private Class<?>    returnType;
    
    public NamedQueryForSql(String name, Class<?> returnType, SqlDialect sqlDialect, Statistical stats)
    {
        this.name = name;
        this.timestamp = new Date();
        this.stats = stats;
        this.groupBy = "";
        this.returnType = returnType;
        this.sqlDialect = sqlDialect;
        //this.paramParser = ParamParserNoMark.emptyParser();
        //this.oneToMany = new HashSet<OneToMany>();
        //this.cacheName = null;
        //this.cache = NoCache.getInstance();
    }
    
    @Override
    public String getName()
    {
        return name;
    }
    
    @Override
    public String getSql()
    {
        return null;
    }
    
    @Override
    public String getSql(Object params)
    {
        return null;
    }
    
    @Override
    public SqlType getSqlType()
    {
        return SqlType.SELECT;
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
    public boolean isInsertable()
    {
        return false;
    }
    
    @Override
    public Insertable asInsertable()
    {
        throw new UnsupportedOperationException("Isn't Insertable object instance");
    }
    
    @Override
    public boolean isUpdateable()
    {
        return false;
    }
    
    @Override
    public Updateable asUpdateable()
    {
        throw new UnsupportedOperationException("Isn't Updateable object instance");
    }
    
    @Override
    public boolean isDeletable()
    {
        return false;
    }
    
    @Override
    public Deletable asDeletable()
    {
        throw new UnsupportedOperationException("Isn't Deletable object instance");
    }
    
    @Override
    public boolean isStorable()
    {
        return false;
    }
    
    @Override
    public Storable asStorable()
    {
        throw new UnsupportedOperationException("Isn't Storeable object instance");
    }
    
    @Override
    public LanguageType getLanguageType()
    {
        return LanguageType.JPQL;
    }
    
    @Override
    public Isolation getIsolation()
    {
        return Isolation.DEFAULT;
    }
    
    @Override
    public int getTimeout()
    {
        return -1;
    }
    
    @Override
    public ResultSetType getResultSetType()
    {
        return ResultSetType.DEFAULT;
    }
    
    @Override
    public ResultSetConcurrency getResultSetConcurrency()
    {
        return ResultSetConcurrency.DEFAULT;
    }
    
    @Override
    public ResultSetHoldability getResultSetHoldability()
    {
        return ResultSetHoldability.DEFAULT;
    }
    
    @Override
    public String getReturnType()
    {
        return this.returnType == null ? "" : this.returnType.getName();
    }
    
    @Override
    public boolean hasReturnType()
    {
        return (this.returnType != null);
    }
    
    @Override
    public Class<?> getReturnTypeAsClass()
    {
        return this.returnType;
    }
    
    @Override
    public Date getTimestamp()
    {
        return this.timestamp;
    }
    
    @Override
    public String getResourceName()
    {
        return "orm.xml";
    }
    
    @Override
    public String getXPath()
    {
        return null;
    }
    
    @Override
    public ParamParser getParamParser()
    {
        return ParamParserNoMark.emptyParser();
    }
    
    @Override
    public String[] extractNames(Object params)
    {
        // TODO review
        return new String[0];
    }
    
    @Override
    public String[] extractNames(String sql)
    {
        // TODO review
        return new String[0];
    }
    
    @Override
    public ValidateType getValidateType()
    {
        return ValidateType.NONE;
    }
    
    @Override
    public void setValidateType(ValidateType validateType)
    {
    }
    
    @Override
    public void bind(SqlDialect sqlDialect)
    {
        this.sqlDialect = sqlDialect;
        
    }
    
    @Override
    public SqlDialect getSqlDialect()
    {
        return this.sqlDialect;
    }
    
    @Override
    public String getPackage()
    {
        return "";
    }
    
    @Override
    public Statistical getStats()
    {
        return this.stats;
    }
    
    @Override
    public String getGroupBy()
    {
        return this.groupBy;
    }
    
    @Override
    public List<String> getGroupByAsList()
    {
        return Collections.emptyList();
    }
    
    @Override
    public Set<OneToMany> getOneToMany()
    {
        return Collections.emptySet();
    }
    
    @Override
    public void addOneToMany(OneToMany oneToMany)
    {
    }
    
    @Override
    public boolean hasCache()
    {
        return false;
    }
    
    @Override
    public String getCacheName()
    {
        return "";
    }
    
    @Override
    public <K, V> Cacheable<K, V> getCache()
    {
        return NoCache.getInstance();
    }
    
    @Override
    public Statistical getStatsPaging()
    {
        return this.stats;
    }
    
}
