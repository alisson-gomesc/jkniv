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
package net.sf.jkniv.sqlegance.builder;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;

import java.util.Properties;

import org.junit.Test;

import net.sf.jkniv.sqlegance.SqlContext;


public class SqlContextFactoryTest
{

    @Test
    public void whenInvokeConstructor()
    {
        assertThat(new SqlContextFactory(), instanceOf(SqlContextFactory.class));
    }
    
    @Test
    public void whenLoadXmlFile()
    {
        SqlContext sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
        assertThat(sqlContext, instanceOf(SqlContext.class));
        assertThat(sqlContext, instanceOf(ClassPathSqlContext.class));
    }
    
    @Test
    public void whenLoadXmlFileWithProperties()
    {
        Properties props = new Properties();
        props.put("A", "B");
        SqlContext sqlContext = SqlContextFactory.newInstance("/repository-sql.xml", props);
        assertThat(sqlContext, instanceOf(SqlContext.class));
        assertThat(sqlContext, instanceOf(ClassPathSqlContext.class));
        assertThat(sqlContext.getRepositoryConfig().getProperty("A"), is("B"));
    }
    
    @Test
    public void whenLoadXmlFileWithSpecificContextName()
    {
        SqlContext sqlContext = SqlContextFactory.newInstance("/repository-sql.xml", "my-context");
        assertThat(sqlContext, instanceOf(SqlContext.class));
        assertThat(sqlContext, instanceOf(ClassPathSqlContext.class));
        assertThat(sqlContext.getName(), is("my-context"));
    }

 }
