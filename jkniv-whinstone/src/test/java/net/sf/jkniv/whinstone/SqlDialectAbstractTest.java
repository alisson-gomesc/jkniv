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
package net.sf.jkniv.whinstone;

import java.util.Collections;

import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.xml.SqlTag;
import net.sf.jkniv.sqlegance.builder.xml.TagFactory;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;

public abstract class SqlDialectAbstractTest
{
    /** select name from author */
    protected static final String SQL_SELECT              = "select name from author";
    protected static final String SQL_SELECT_COUNT_RESULT = "select count(1) from (select name from author)";
    
    /** select name from author for update */
    protected static final String SQL_SELECT_FOR_UPDDATE  = "select name from author for update";
    /** select distinct name from author */
    protected static final String SQL_SELECT_DISTINCT     = "select distinct name from author";
    /** Select DIStinct name from author FOR Update */
    protected static final String SQL_SELECT_SENSITIVE    = "Select DIStinct name from author FOR Update";
    /** delete from author where id = 1 */
    protected static final String SQL_DELETE              = "delete from author where id = 1";
    /** update author set name = 'John' where id = 1 */
    protected static final String SQL_UPDATE              = "update author set name = 'John' where id = 1";
    /** insert into author (id, name) values (1,'Mary') */
    protected static final String SQL_INSERT              = "insert into author (id, name) values (1,'Mary')";
    
    public SqlDialectAbstractTest()
    {
        //super("Abstract Test", getSql(SQL_SELECT, SqlCommandType.SELECT), new QueryName("sql-test", Collections.emptyMap(), 10, 50));
    }
    
    protected Sql getSql(String query, SqlType type)
    {
        SqlTag tag = null;
        if (type == SqlType.SELECT)
            tag =  (SqlTag) TagFactory.newSelect("id", LanguageType.NATIVE);
        else if (type == SqlType.INSERT)
            tag = (SqlTag) TagFactory.newInsert("id", LanguageType.NATIVE);
        else if (type == SqlType.DELETE)
            tag = (SqlTag) TagFactory.newDelete("id", LanguageType.NATIVE);
        else if (type == SqlType.UPDATE)
            tag = (SqlTag) TagFactory.newUpdate("id", LanguageType.NATIVE);
        
        tag.addTag(query);
        return tag;
    }
    
    protected Queryable getQueryName()
    {
        Queryable q = QueryFactory.of("sql-test", Collections.emptyMap(), 10, 50);
        return q;
    }
    
    abstract public void whenDatabaseSupportLimit();
    
    abstract public void whenDatabaseSupportOffset();
    
    abstract public void whenDatabaseSupportRownum();
    
    abstract public void whenPagingSqlStartWithSelect();
    
    abstract public void whenPagingSqlStartWithSelectSensitive();
    
    abstract public void whenPagingSqlStartWithSelectDistinct();
    
    abstract public void whenPagingSqlStartWithSelectForUpdate();
    
    abstract public void whenPagingSqlStartWithDelete();
    
    abstract public void whenPagingSqlStartWithUpdate();
    
    abstract public void whenPagingSqlStartWithInsert();
    
    abstract public void whenCountSqlStartWithSelect();
    
    abstract public void whenCountSqlStartWithSelectForUpdate();
    
}
