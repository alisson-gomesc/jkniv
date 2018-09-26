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
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.cache.Cacheable;
import net.sf.jkniv.cache.MemoryCache;
import net.sf.jkniv.cache.NoCache;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.sqlegance.builder.XmlBuilderSql;
import net.sf.jkniv.sqlegance.statement.ResultSetConcurrency;
import net.sf.jkniv.sqlegance.statement.ResultSetHoldability;
import net.sf.jkniv.sqlegance.statement.ResultSetType;
import net.sf.jkniv.sqlegance.params.ParamMarkType;
import net.sf.jkniv.sqlegance.transaction.Isolation;

public class CacheTest
{
    private static SqlContext context1;
    private static SqlContext context2;
    
    @BeforeClass
    public static void setUp()
    {
        context1 = SqlContextFactory.newInstance("/repository-sql.xml");
        context2 = SqlContextFactory.newInstance("/cache/repository-cache-sql.xml");
    }
    
    @Test
    public void whenHasOneCacheConfigured()
    {
        Selectable sql = context1.getQuery("usersInCache").asSelectable();
        assertThat(sql.getCache().getName(), is("user-cache"));
        assertThat(sql.getCache(), instanceOf(Cacheable.class));
        assertThat(sql.getCache(), instanceOf(MemoryCache.class));
    }
    
    @Test
    public void whenCacheIsNoConfigured()
    {
        Selectable sql = context1.getQuery("usersNoCache").asSelectable();
        assertThat(sql.getCache().getName(), is("NoCache"));
        assertThat(sql.getCache(), instanceOf(Cacheable.class));
        assertThat(sql.getCache(), instanceOf(NoCache.class));
    }
    
    @Test
    public void whenOneQueryHasCacheAndAnotherNot()
    {
        Selectable sql1 = context2.getQuery("usersInCache").asSelectable();
        assertThat(sql1.getCache().getName(), is("user-cache"));
        assertThat(sql1.getCache(), instanceOf(Cacheable.class));
        assertThat(sql1.getCache(), instanceOf(MemoryCache.class));
        
        Selectable sql2 = context2.getQuery("usersNoCache").asSelectable();
        assertThat(sql2.getCache().getName(), is("NoCache"));
        assertThat(sql2.getCache(), instanceOf(Cacheable.class));
        assertThat(sql2.getCache(), instanceOf(NoCache.class));
    }
    
}
