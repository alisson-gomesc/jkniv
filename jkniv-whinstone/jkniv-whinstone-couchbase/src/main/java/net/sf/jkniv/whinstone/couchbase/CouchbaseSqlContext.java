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
package net.sf.jkniv.whinstone.couchbase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jkniv.sqlegance.Deletable;
import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.Updateable;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.sqlegance.builder.xml.TagFactory;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.validation.ValidateType;

class CouchbaseSqlContext implements SqlContext
{
    private SqlContext sqlContext;
    private static final String GET_QUERY = "get", PUT_QUERY = "add", UPDATE_QUERY = "replace";//, DELETE_QUERY = "remove"; 
    private static final Map<String, Sql> BUILTIN = new HashMap<String, Sql>(); 
            
    public CouchbaseSqlContext(SqlContext sqlContext)
    {
        this.sqlContext = sqlContext;
        checkBuiltinFunctions();
    }

    public void buildInQueries()
    {
        // initialize built in Sql
        Insertable insertable = TagFactory.newInsert(PUT_QUERY, LanguageType.STORED, getSqlDialect());
        insertable.setValidateType(ValidateType.ADD);
        Updateable  updateable = TagFactory.newUpdate(UPDATE_QUERY, LanguageType.STORED, getSqlDialect());
        updateable.setValidateType(ValidateType.UPDATE);
        
        BUILTIN.put(GET_QUERY, TagFactory.newSelect(GET_QUERY, LanguageType.NATIVE, getSqlDialect()));
        BUILTIN.put(PUT_QUERY, insertable);
        BUILTIN.put(UPDATE_QUERY, updateable);
        /*
        Deletable deletable = TagFactory.newDelete(DELETE_QUERY, LanguageType.STORED, getSqlDialect());
        deletable.setValidateType(ValidateType.REMOVE);
        BUILTIN.put(DELETE_QUERY, deletable);
         */
    }

    private void checkBuiltinFunctions()
    {
        for(String k : BUILTIN.keySet())
        {
            if (this.sqlContext.containsQuery(k))
                throw new RepositoryException("SqlContext ["+sqlContext.getName()+"] has conflict query name ["+k+"] for native queries with Couchbase");
        }
    }
    
    @Override
    public String getName()
    {
        return sqlContext.getName();
    }

    @Override
    public Sql getQuery(String name)
    {
        Sql sql = BUILTIN.get(name);
        if(sql == null)
            sql = sqlContext.getQuery(name);
        return sql;
    }

    @Override
    public boolean containsQuery(String name)
    {
        return (BUILTIN.containsKey(name) || sqlContext.containsQuery(name));
    }

    @Override
    public RepositoryConfig getRepositoryConfig()
    {
        return sqlContext.getRepositoryConfig();
    }

    @Override
    public SqlDialect getSqlDialect()
    {
        return sqlContext.getSqlDialect();
    }
    
    @Override
    public void setSqlDialect(SqlDialect sqlDialect)
    {
        sqlContext.setSqlDialect(sqlDialect);
    }
    
    @Override
    public List<Sql> getPackage(String packageName)
    {
        return sqlContext.getPackage(packageName);
    }

    @Override
    public Map<String, List<Sql>> getPackageStartWith(String packageName)
    {
        return sqlContext.getPackageStartWith(packageName);
    }

    @Override
    public void close()
    {
        sqlContext.close();
    }
    
    @Override
    public Sql add(Sql sql)
    {
        return sqlContext.add(sql);
    }
    
    public static boolean isGet(String name)
    {
        return GET_QUERY.equals(name);
    }
}
