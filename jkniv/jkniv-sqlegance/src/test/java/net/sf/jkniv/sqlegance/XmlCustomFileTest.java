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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.sqlegance.params.ParamMarkType;

public class XmlCustomFileTest
{
    private static SqlContext sqlContext;
    
    @BeforeClass
    public static void setUp()
    {
        sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
    }


    @Test @Ignore("Time to live removed, automatic reload xml was implemented")
    public void whenLoadCustomFileSucessfullyAndTestRefresh() throws InterruptedException
    {
        //sqlContext.configFile("/customxml/sql-custom-test1.xml");
        Sql sql1 = sqlContext.getQuery("sql-custom-test1");
        Sql sql2 = sqlContext.getQuery("sql-custom-test2");
        
        assertThat(sql1, notNullValue());
        assertThat(sql2, notNullValue());
        assertThat(sql1.getParamParser().getType(), is(ParamMarkType.NO_MARK));
        assertThat(sql2.getParamParser().getType(), is(ParamMarkType.NO_MARK));
        
        //assertThat(sql1.getTimeToLive(), is(4L));
        //assertThat(sql2.getTimeToLive(), is(4L));
        
        assertThat(sql1, notNullValue());
        assertThat(sql2, notNullValue());
        
        //assertThat(sql1.getTimeToLive(), is(4L));
        //assertThat(sql2.getTimeToLive(), is(4L));

        //assertThat(sql1.isExpired(), is(false));
        //assertThat(sql2.isExpired(), is(false));
        
        System.out.println("waiting to expire sql... timestamp["+sql1.getTimestamp().getTime()+"]");
        Thread.sleep(5000);
        //assertThat(sql1.isExpired(), is(true));
        //assertThat(sql1.isExpired(), is(true));
        sql1 = sqlContext.getQuery("sql-custom-test1");
        sql2 = sqlContext.getQuery("sql-custom-test2");
        assertThat(sql1.getParamParser().getType(), is(ParamMarkType.NO_MARK));
        assertThat(sql2.getParamParser().getType(), is(ParamMarkType.NO_MARK));
        //assertThat(sql1.isExpired(), is(false));
        //assertThat(sql2.isExpired(), is(false));

    }
}
