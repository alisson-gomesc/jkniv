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

//import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;


import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.builder.SqlContextFactory;

public class StatisticalTest
{
    private final static Logger LOG = LoggerFactory.getLogger(StatisticalTest.class);
    private static SqlContext sqlContext;
    
    @BeforeClass
    public static void setUp()
    {
        sqlContext = SqlContextFactory.newInstance("/repository-sql-stats.xml");
    }
    
    @Test
    public void whenRepositoryConfigHasStatsEnable()
    {
        String statsParam = sqlContext.getRepositoryConfig().getProperty(RepositoryProperty.SQL_STATS);
        assertThat(statsParam, is("true"));
    }
    
    @Test
    public void whenStatsSelectQueries()
    {
        LOG.debug("statistical for select-roles");
        Sql sql = sqlContext.getQuery("select-roles");
        sql.getStats().add(100);        
        assertThat(sql.getStats().getCount(), is(1L));
        assertThat(sql.getStats().getTotalTime(), is(100L));
        assertThat(sql.getStats().getMinTime(), is(100L));
        assertThat(sql.getStats().getMaxTime(), is(100L));
        assertThat(sql.getStats().getAvgTime(), is(100L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(100L));
        assertThat(sql.getStats().getTotalException(), is(0L));
        assertThat(sql.getStats().getFirstException(), nullValue());
        assertThat(sql.getStats().getLastException(), nullValue());

        sql.getStats().add(200);
        assertThat(sql.getStats().getCount(), is(2L));
        assertThat(sql.getStats().getTotalTime(), is(300L));
        assertThat(sql.getStats().getMinTime(), is(100L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(150L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(200L));
        assertThat(sql.getStats().getTotalException(), is(0L));
        assertThat(sql.getStats().getFirstException(), nullValue());
        assertThat(sql.getStats().getLastException(), nullValue());

        sql.getStats().add(90);
        assertThat(sql.getStats().getCount(), is(3L));
        assertThat(sql.getStats().getTotalTime(), is(390L));
        assertThat(sql.getStats().getMinTime(), is(90L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(130L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(90L));
        assertThat(sql.getStats().getTotalException(), is(0L));
        assertThat(sql.getStats().getFirstException(), nullValue());
        assertThat(sql.getStats().getLastException(), nullValue());

        sql.getStats().add(new RuntimeException("dummy"));
        assertThat(sql.getStats().getCount(), is(3L));
        assertThat(sql.getStats().getTotalTime(), is(390L));
        assertThat(sql.getStats().getMinTime(), is(90L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(130L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(90L));
        assertThat(sql.getStats().getTotalException(), is(1L));
        assertThat(sql.getStats().getFirstException(), instanceOf(RuntimeException.class));
        assertThat(sql.getStats().getLastException(), instanceOf(RuntimeException.class));

        sql.getStats().add(new UnsupportedOperationException("dummy"));
        assertThat(sql.getStats().getCount(), is(3L));
        assertThat(sql.getStats().getTotalTime(), is(390L));
        assertThat(sql.getStats().getMinTime(), is(90L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(130L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(90L));
        assertThat(sql.getStats().getTotalException(), is(2L));
        assertThat(sql.getStats().getFirstException(), instanceOf(RuntimeException.class));
        assertThat(sql.getStats().getLastException(), instanceOf(UnsupportedOperationException.class));
    }


    @Test
    public void whenStatsUpdateQueries()
    {
        LOG.debug("statistical for update-author");
        Sql sql = sqlContext.getQuery("update-author");
        sql.getStats().add(100);        
        assertThat(sql.getStats().getCount(), is(1L));
        assertThat(sql.getStats().getTotalTime(), is(100L));
        assertThat(sql.getStats().getMinTime(), is(100L));
        assertThat(sql.getStats().getMaxTime(), is(100L));
        assertThat(sql.getStats().getAvgTime(), is(100L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(100L));
        assertThat(sql.getStats().getTotalException(), is(0L));
        assertThat(sql.getStats().getFirstException(), nullValue());
        assertThat(sql.getStats().getLastException(), nullValue());

        sql.getStats().add(200);
        assertThat(sql.getStats().getCount(), is(2L));
        assertThat(sql.getStats().getTotalTime(), is(300L));
        assertThat(sql.getStats().getMinTime(), is(100L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(150L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(200L));
        assertThat(sql.getStats().getTotalException(), is(0L));
        assertThat(sql.getStats().getFirstException(), nullValue());
        assertThat(sql.getStats().getLastException(), nullValue());

        sql.getStats().add(90);
        assertThat(sql.getStats().getCount(), is(3L));
        assertThat(sql.getStats().getTotalTime(), is(390L));
        assertThat(sql.getStats().getMinTime(), is(90L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(130L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(90L));
        assertThat(sql.getStats().getTotalException(), is(0L));
        assertThat(sql.getStats().getFirstException(), nullValue());
        assertThat(sql.getStats().getLastException(), nullValue());

        sql.getStats().add(new RuntimeException("dummy"));
        assertThat(sql.getStats().getCount(), is(3L));
        assertThat(sql.getStats().getTotalTime(), is(390L));
        assertThat(sql.getStats().getMinTime(), is(90L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(130L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(90L));
        assertThat(sql.getStats().getTotalException(), is(1L));
        assertThat(sql.getStats().getFirstException(), instanceOf(RuntimeException.class));
        assertThat(sql.getStats().getLastException(), instanceOf(RuntimeException.class));

        sql.getStats().add(new UnsupportedOperationException("dummy"));
        assertThat(sql.getStats().getCount(), is(3L));
        assertThat(sql.getStats().getTotalTime(), is(390L));
        assertThat(sql.getStats().getMinTime(), is(90L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(130L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(90L));
        assertThat(sql.getStats().getTotalException(), is(2L));
        assertThat(sql.getStats().getFirstException(), instanceOf(RuntimeException.class));
        assertThat(sql.getStats().getLastException(), instanceOf(UnsupportedOperationException.class));
    }

    @Test
    public void whenStatsDeleteQueries()
    {
        LOG.debug("statistical for delete-author");
        Sql sql = sqlContext.getQuery("delete-author");
        sql.getStats().add(100);        
        assertThat(sql.getStats().getCount(), is(1L));
        assertThat(sql.getStats().getTotalTime(), is(100L));
        assertThat(sql.getStats().getMinTime(), is(100L));
        assertThat(sql.getStats().getMaxTime(), is(100L));
        assertThat(sql.getStats().getAvgTime(), is(100L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(100L));
        assertThat(sql.getStats().getTotalException(), is(0L));
        assertThat(sql.getStats().getFirstException(), nullValue());
        assertThat(sql.getStats().getLastException(), nullValue());

        sql.getStats().add(200);
        assertThat(sql.getStats().getCount(), is(2L));
        assertThat(sql.getStats().getTotalTime(), is(300L));
        assertThat(sql.getStats().getMinTime(), is(100L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(150L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(200L));
        assertThat(sql.getStats().getTotalException(), is(0L));
        assertThat(sql.getStats().getFirstException(), nullValue());
        assertThat(sql.getStats().getLastException(), nullValue());

        sql.getStats().add(90);
        assertThat(sql.getStats().getCount(), is(3L));
        assertThat(sql.getStats().getTotalTime(), is(390L));
        assertThat(sql.getStats().getMinTime(), is(90L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(130L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(90L));
        assertThat(sql.getStats().getTotalException(), is(0L));
        assertThat(sql.getStats().getFirstException(), nullValue());
        assertThat(sql.getStats().getLastException(), nullValue());

        sql.getStats().add(new RuntimeException("dummy"));
        assertThat(sql.getStats().getCount(), is(3L));
        assertThat(sql.getStats().getTotalTime(), is(390L));
        assertThat(sql.getStats().getMinTime(), is(90L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(130L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(90L));
        assertThat(sql.getStats().getTotalException(), is(1L));
        assertThat(sql.getStats().getFirstException(), instanceOf(RuntimeException.class));
        assertThat(sql.getStats().getLastException(), instanceOf(RuntimeException.class));

        sql.getStats().add(new UnsupportedOperationException("dummy"));
        assertThat(sql.getStats().getCount(), is(3L));
        assertThat(sql.getStats().getTotalTime(), is(390L));
        assertThat(sql.getStats().getMinTime(), is(90L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(130L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(90L));
        assertThat(sql.getStats().getTotalException(), is(2L));
        assertThat(sql.getStats().getFirstException(), instanceOf(RuntimeException.class));
        assertThat(sql.getStats().getLastException(), instanceOf(UnsupportedOperationException.class));
    }

    @Test
    public void whenStatsInsertQueries()
    {
        LOG.debug("statistical for insert-author");
        Sql sql = sqlContext.getQuery("insert-author");
        sql.getStats().add(100);        
        assertThat(sql.getStats().getCount(), is(1L));
        assertThat(sql.getStats().getTotalTime(), is(100L));
        assertThat(sql.getStats().getMinTime(), is(100L));
        assertThat(sql.getStats().getMaxTime(), is(100L));
        assertThat(sql.getStats().getAvgTime(), is(100L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(100L));
        assertThat(sql.getStats().getTotalException(), is(0L));
        assertThat(sql.getStats().getFirstException(), nullValue());
        assertThat(sql.getStats().getLastException(), nullValue());

        sql.getStats().add(200);
        assertThat(sql.getStats().getCount(), is(2L));
        assertThat(sql.getStats().getTotalTime(), is(300L));
        assertThat(sql.getStats().getMinTime(), is(100L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(150L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(200L));
        assertThat(sql.getStats().getTotalException(), is(0L));
        assertThat(sql.getStats().getFirstException(), nullValue());
        assertThat(sql.getStats().getLastException(), nullValue());

        sql.getStats().add(90);
        assertThat(sql.getStats().getCount(), is(3L));
        assertThat(sql.getStats().getTotalTime(), is(390L));
        assertThat(sql.getStats().getMinTime(), is(90L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(130L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(90L));
        assertThat(sql.getStats().getTotalException(), is(0L));
        assertThat(sql.getStats().getFirstException(), nullValue());
        assertThat(sql.getStats().getLastException(), nullValue());

        sql.getStats().add(new RuntimeException("dummy"));
        assertThat(sql.getStats().getCount(), is(3L));
        assertThat(sql.getStats().getTotalTime(), is(390L));
        assertThat(sql.getStats().getMinTime(), is(90L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(130L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(90L));
        assertThat(sql.getStats().getTotalException(), is(1L));
        assertThat(sql.getStats().getFirstException(), instanceOf(RuntimeException.class));
        assertThat(sql.getStats().getLastException(), instanceOf(RuntimeException.class));

        sql.getStats().add(new UnsupportedOperationException("dummy"));
        assertThat(sql.getStats().getCount(), is(3L));
        assertThat(sql.getStats().getTotalTime(), is(390L));
        assertThat(sql.getStats().getMinTime(), is(90L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(130L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(90L));
        assertThat(sql.getStats().getTotalException(), is(2L));
        assertThat(sql.getStats().getFirstException(), instanceOf(RuntimeException.class));
        assertThat(sql.getStats().getLastException(), instanceOf(UnsupportedOperationException.class));
    }
    
    @Test
    public void whenStatsProcedures()
    {
        LOG.debug("statistical for sp-item");
        Sql sql = sqlContext.getQuery("sp-item");
        sql.getStats().add(100);        
        assertThat(sql.getStats().getCount(), is(1L));
        assertThat(sql.getStats().getTotalTime(), is(100L));
        assertThat(sql.getStats().getMinTime(), is(100L));
        assertThat(sql.getStats().getMaxTime(), is(100L));
        assertThat(sql.getStats().getAvgTime(), is(100L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(100L));
        assertThat(sql.getStats().getTotalException(), is(0L));
        assertThat(sql.getStats().getFirstException(), nullValue());
        assertThat(sql.getStats().getLastException(), nullValue());
        
        sql.getStats().add(200);
        assertThat(sql.getStats().getCount(), is(2L));
        assertThat(sql.getStats().getTotalTime(), is(300L));
        assertThat(sql.getStats().getMinTime(), is(100L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(150L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(200L));
        assertThat(sql.getStats().getTotalException(), is(0L));
        assertThat(sql.getStats().getFirstException(), nullValue());
        assertThat(sql.getStats().getLastException(), nullValue());
        
        sql.getStats().add(90);
        assertThat(sql.getStats().getCount(), is(3L));
        assertThat(sql.getStats().getTotalTime(), is(390L));
        assertThat(sql.getStats().getMinTime(), is(90L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(130L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(90L));
        assertThat(sql.getStats().getTotalException(), is(0L));
        assertThat(sql.getStats().getFirstException(), nullValue());
        assertThat(sql.getStats().getLastException(), nullValue());
        
        sql.getStats().add(new RuntimeException("dummy"));
        assertThat(sql.getStats().getCount(), is(3L));
        assertThat(sql.getStats().getTotalTime(), is(390L));
        assertThat(sql.getStats().getMinTime(), is(90L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(130L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(90L));
        assertThat(sql.getStats().getTotalException(), is(1L));
        assertThat(sql.getStats().getFirstException(), instanceOf(RuntimeException.class));
        assertThat(sql.getStats().getLastException(), instanceOf(RuntimeException.class));
        
        sql.getStats().add(new UnsupportedOperationException("dummy"));
        assertThat(sql.getStats().getCount(), is(3L));
        assertThat(sql.getStats().getTotalTime(), is(390L));
        assertThat(sql.getStats().getMinTime(), is(90L));
        assertThat(sql.getStats().getMaxTime(), is(200L));
        assertThat(sql.getStats().getAvgTime(), is(130L));
        assertThat(sql.getStats().getFirstTime(), is(100L));
        assertThat(sql.getStats().getLastTime(), is(90L));
        assertThat(sql.getStats().getTotalException(), is(2L));
        assertThat(sql.getStats().getFirstException(), instanceOf(RuntimeException.class));
        assertThat(sql.getStats().getLastException(), instanceOf(UnsupportedOperationException.class));
    }

    @Test
    public void whenStatsHaveNewInstanceForEachStatement()
    {
        LOG.debug("statistical for select/update/insert/delete/procedure");
        Sql sql = sqlContext.getQuery("select-roles-2");
        sql.getStats().add(100);
        assertThat(sql.getStats().getCount(), is(1L));
        
        sql = sqlContext.getQuery("insert-author-2");
        sql.getStats().add(100);
        sql.getStats().add(100);
        assertThat(sql.getStats().getCount(), is(2L));

        sql = sqlContext.getQuery("update-author-2");
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        assertThat(sql.getStats().getCount(), is(3L));

        sql = sqlContext.getQuery("delete-author-2");
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        assertThat(sql.getStats().getCount(), is(4L));

        sql = sqlContext.getQuery("sp-item-2");
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        assertThat(sql.getStats().getCount(), is(5L));
        

        sql = sqlContext.getQuery("insert-author-3");
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        assertThat(sql.getStats().getCount(), is(6L));

        sql = sqlContext.getQuery("update-author-3");
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        assertThat(sql.getStats().getCount(), is(7L));

        sql = sqlContext.getQuery("delete-author-3");
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        assertThat(sql.getStats().getCount(), is(8L));

        sql = sqlContext.getQuery("sp-item-3");
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        sql.getStats().add(100);
        assertThat(sql.getStats().getCount(), is(9L));
    }
}
