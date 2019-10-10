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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.sameInstance;

import org.junit.BeforeClass;
import org.junit.Test;

import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.sqlegance.builder.xml.NoSqlStats;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;

public class InstancesTest
{
    private static SqlContext sqlContext;
    
    @BeforeClass
    public static void setUp()
    {
        sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
    }
    
    @Test
    public void whenCheckInstancesOf()
    {
        SqlDialect dialect = null;
        for (Sql sql : sqlContext.getPackage(""))
        {
            if (dialect == null)
                dialect = sql.getSqlDialect();
            
            assertThat(dialect, sameInstance(sql.getSqlDialect()));
            assertThat(NoSqlStats.getInstance(), sameInstance(sql.getStats()));
        }
    }
}
