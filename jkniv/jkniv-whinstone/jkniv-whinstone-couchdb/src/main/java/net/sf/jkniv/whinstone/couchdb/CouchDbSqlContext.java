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
package net.sf.jkniv.whinstone.couchdb;

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
import net.sf.jkniv.whinstone.couchdb.dialect.CouchDbDialect;

public class CouchDbSqlContext implements SqlContext
{
    private SqlContext sqlContext;
    private static final String ALL_DOCS_QUERY = "_all_docs";
    private static final String GET_QUERY = "get", PUT_QUERY = "add", UPDATE_QUERY = "update", DELETE_QUERY = "remove"; 
    private static final Map<String, Sql> BUILTIN = new HashMap<String, Sql>(); 
            
    private static final CouchDbDialect couchDbDialect = new CouchDbDialect();
    
    static {
        // initialize built in Sql
        BUILTIN.put(ALL_DOCS_QUERY, TagFactory.newSelect(ALL_DOCS_QUERY, LanguageType.STORED, couchDbDialect));
        BUILTIN.put(GET_QUERY, TagFactory.newSelect(GET_QUERY, LanguageType.STORED, couchDbDialect));
        
        Insertable insertable = TagFactory.newInsert(PUT_QUERY, LanguageType.STORED, couchDbDialect);
        insertable.setValidateType(ValidateType.ADD);
        
        Updateable  updateable = TagFactory.newUpdate(UPDATE_QUERY, LanguageType.STORED, couchDbDialect);
        updateable.setValidateType(ValidateType.UPDATE);
        
        Deletable deletable = TagFactory.newDelete(DELETE_QUERY, LanguageType.STORED, couchDbDialect);
        deletable.setValidateType(ValidateType.REMOVE);

        BUILTIN.put(PUT_QUERY, insertable);
        BUILTIN.put(UPDATE_QUERY, updateable);
        BUILTIN.put(DELETE_QUERY, deletable);
    }
    
    public CouchDbSqlContext(SqlContext sqlContext)
    {
        this.sqlContext = sqlContext;
        checkBuiltinFunctions();
    }
    
    private void checkBuiltinFunctions()
    {
        for(String k : BUILTIN.keySet())
        {
            if (this.sqlContext.containsQuery(k))
                throw new RepositoryException("SqlContext ["+sqlContext.getName()+"] has conflict query name ["+k+"] for native queries from CouchDb");
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
    
    public static boolean isAllDocs(String name)
    {
        return ALL_DOCS_QUERY.equals(name);
    }

    public static boolean isGet(String name)
    {
        return GET_QUERY.equals(name);
    }

}
