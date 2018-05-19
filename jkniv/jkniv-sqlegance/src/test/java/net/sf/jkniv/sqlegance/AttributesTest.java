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
package net.sf.jkniv.sqlegance;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;

import net.sf.jkniv.cache.Cacheable;
import net.sf.jkniv.cache.NoCache;
import net.sf.jkniv.sqlegance.builder.XmlBuilderSql;
import net.sf.jkniv.sqlegance.statement.ResultSetConcurrency;
import net.sf.jkniv.sqlegance.statement.ResultSetHoldability;
import net.sf.jkniv.sqlegance.statement.ResultSetType;
import net.sf.jkniv.sqlegance.params.ParamMarkType;
import net.sf.jkniv.sqlegance.transaction.Isolation;

public class AttributesTest
{
        
    @Test
    public void whenReadXmlSelectNodeAttributesElementMustMatch() { 
        Sql sql = XmlBuilderSql.getQuery("sql1-attributes-all");
        Sql sqlDefault = XmlBuilderSql.getQuery("sql2-attributes-default");
        
        assertThat(sql.getSql().toLowerCase(), is("select id, name from users".toLowerCase()));        
        assertThat(sqlDefault.getSql().toLowerCase(), is("select id, name from users".toLowerCase()));
        
        assertThat(sql.getLanguageType(), is(LanguageType.NATIVE));
        assertThat(sql.getIsolation(), is(Isolation.NONE));
        assertThat(sql.getTimeout(), is(1500));
        assertThat(sql.isBatch(), is(true));
        assertThat(sql.getHint(), is("/*+ index(usr idx_primary_key)*/"));
        assertThat(sql.getResultSetType(), is(ResultSetType.TYPE_FORWARD_ONLY));
        assertThat(sql.getResultSetConcurrency(), is(ResultSetConcurrency.CONCUR_READ_ONLY));
        assertThat(sql.getResultSetHoldability(), is(ResultSetHoldability.CLOSE_CURSORS_AT_COMMIT));

        assertThat(sqlDefault.getLanguageType(), is(LanguageType.NATIVE));
        assertThat(sqlDefault.getIsolation(), is(Isolation.DEFAULT));
        assertThat(sqlDefault.getTimeout(), is(-1));
        assertThat(sqlDefault.isBatch(), is(false));
        assertThat(sqlDefault.getHint(), is(""));
        assertThat(sqlDefault.getResultSetType(), is(ResultSetType.DEFAULT));
        assertThat(sqlDefault.getResultSetConcurrency(), is(ResultSetConcurrency.DEFAULT));
        assertThat(sqlDefault.getResultSetHoldability(), is(ResultSetHoldability.DEFAULT));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.NO_MARK));
        assertThat(sqlDefault.getParamParser().getType(), is(ParamMarkType.NO_MARK));
        
    }

    @Test
    public void whenReadXmlInsertNodeAttributesElementMustMatch() { 
        Sql sql = XmlBuilderSql.getQuery("sql3-in-attributes-all");
        Sql sqlDefault = XmlBuilderSql.getQuery("sql4-in-attributes-default");
        
        assertThat(sql.getSql().toLowerCase(), is("INSERT INTO users VALUES (#{name})".toLowerCase()));        
        assertThat(sqlDefault.getSql().toLowerCase(), is("INSERT INTO users VALUES (#{name})".toLowerCase()));        

        assertThat(sql.getLanguageType(), is(LanguageType.HQL));
        assertThat(sql.getIsolation(), is(Isolation.READ_UNCOMMITTED));
        assertThat(sql.getTimeout(), is(2500));
        assertThat(sql.isBatch(), is(true));
        assertThat(sql.getHint(), is("/*+ index(usr idx_primary_key)*/"));

        assertThat(sqlDefault.getLanguageType(), is(LanguageType.HQL));
        assertThat(sqlDefault.getIsolation(), is(Isolation.DEFAULT));
        assertThat(sqlDefault.getTimeout(), is(-1));
        assertThat(sqlDefault.isBatch(), is(false));
        assertThat(sqlDefault.getHint(), is(""));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
        assertThat(sqlDefault.getParamParser().getType(), is(ParamMarkType.HASH));
    }

    @Test
    public void whenReadXmlDeleteNodeAttributesElementMustMatch() { 
        Sql sql = XmlBuilderSql.getQuery("sql5-de-attributes-all");
        Sql sqlDefault = XmlBuilderSql.getQuery("sql6-de-attributes-default");
        assertThat(sql.getSql().toLowerCase(), is("delete from users where id = #{id}".toLowerCase()));        
        assertThat(sqlDefault.getSql().toLowerCase(), is("delete from users where id = #{id}".toLowerCase()));
        
        assertThat(sql.getLanguageType(), is(LanguageType.NATIVE));
        assertThat(sql.getIsolation(), is(Isolation.READ_COMMITTED));
        assertThat(sql.getTimeout(), is(3500));
        assertThat(sql.isBatch(), is(true));
        assertThat(sql.getHint(), is("/*+ index(usr idx_primary_key)*/"));

        assertThat(sqlDefault.getLanguageType(), is(LanguageType.NATIVE));
        assertThat(sqlDefault.getIsolation(), is(Isolation.DEFAULT));
        assertThat(sqlDefault.getTimeout(), is(-1));
        assertThat(sqlDefault.isBatch(), is(false));
        assertThat(sqlDefault.getHint(), is(""));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
        assertThat(sqlDefault.getParamParser().getType(), is(ParamMarkType.HASH));
    }
    

    @Test
    public void whenReadXmlUpdateNodeAttributesElementMustMatch() { 
        Sql sql = XmlBuilderSql.getQuery("sql7-up-attributes-all");
        Sql sqlDefault = XmlBuilderSql.getQuery("sql8-up-attributes-default");
        
        assertThat(sql.getSql().toLowerCase(), is("update users set name = #{name} where id = #{id}".toLowerCase()));        
        assertThat(sqlDefault.getSql().toLowerCase(), is("update users set name = #{name} where id = #{id}".toLowerCase()));

        assertThat(sql.getLanguageType(), is(LanguageType.NATIVE));
        assertThat(sql.getIsolation(), is(Isolation.REPEATABLE_READ));
        assertThat(sql.getTimeout(), is(4500));
        assertThat(sql.isBatch(), is(true));
        assertThat(sql.getHint(), is("/*+ index(usr idx_primary_key)*/"));

        assertThat(sqlDefault.getLanguageType(), is(LanguageType.NATIVE));
        assertThat(sqlDefault.getIsolation(), is(Isolation.SERIALIZABLE));
        assertThat(sqlDefault.getTimeout(), is(-1));
        assertThat(sqlDefault.isBatch(), is(false));
        assertThat(sqlDefault.getHint(), is(""));
        assertThat(sql.getParamParser().getType(), is(ParamMarkType.HASH));
        assertThat(sqlDefault.getParamParser().getType(), is(ParamMarkType.HASH));
    }

    
    @Test
    public void whenReadXmlWithGroupByAttributesElementMustMatch() 
    { 
        Selectable sql1 = XmlBuilderSql.getQuery("test-groupingby1-after-select").asSelectable();
        Selectable sql2 = XmlBuilderSql.getQuery("test-groupingby2-after-select").asSelectable();
        
        assertThat(sql1.getSql().toLowerCase(), is("select name, code, priority from color"));        
        assertThat(sql1.getLanguageType(), is(LanguageType.NATIVE));
        assertThat(sql1.isBatch(), is(false));
        assertThat(sql1.getHint(), is(""));
        assertThat(sql1.getParamParser().getType(), is(ParamMarkType.NO_MARK));
        assertThat(sql1.getGroupBy(), is("name"));
        assertThat(sql1.getGroupByAsList().size(), is(1));
        assertThat(sql1.getCache().getName(), is("NoCache"));
        assertThat(sql1.getCache(), instanceOf(Cacheable.class));
        assertThat(sql1.getCache(), instanceOf(NoCache.class));
        
        assertThat(sql2.getSql().toLowerCase(), is("select name, code, priority from color"));        
        assertThat(sql2.getLanguageType(), is(LanguageType.NATIVE));
        assertThat(sql2.isBatch(), is(false));
        assertThat(sql2.getHint(), is(""));
        assertThat(sql2.getParamParser().getType(), is(ParamMarkType.NO_MARK));
        assertThat(sql2.getGroupBy(), is("name,code"));
        assertThat(sql2.getGroupByAsList().size(), is(2));
        assertThat(sql2.getCache().getName(), is("NoCache"));
        assertThat(sql2.getCache(), instanceOf(Cacheable.class));
        assertThat(sql2.getCache(), instanceOf(NoCache.class));
    }
}
