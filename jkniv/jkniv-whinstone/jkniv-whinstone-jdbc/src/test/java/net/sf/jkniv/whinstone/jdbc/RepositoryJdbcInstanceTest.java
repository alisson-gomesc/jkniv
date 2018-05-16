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
package net.sf.jkniv.whinstone.jdbc;

import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.acme.domain.FlatBook;
import net.sf.jkniv.whinstone.jdbc.jndi.JndiCreator;
import net.sf.jkniv.whinstone.jdbc.test.BaseSpringJUnit4;

public class RepositoryJdbcInstanceTest extends BaseSpringJUnit4
{
    private static final String DERBY_URL = "jdbc:derby:memory:derbwhinstone;create=true";
    public static final int TOTAL_BOOKS = 15;
    
    @Autowired
    DataSource dataSourceDerby;

    @BeforeClass
    public static void setUpJndiResources()
    {
        JndiCreator.bind();
        JndiCreator.bind();
    }
    
    @Test
    public void whenCreateRepositoryWithDataSource()
    {
        Repository repository = new RepositoryJdbc(dataSourceDerby);
        Assert.assertNotNull(repository);
    }

    @Test
    public void whenCreateRepositoryWithContext()
    {
        Repository repository = new RepositoryJdbc(SqlContextFactory.newInstance("/repository-sql.xml"));
        Assert.assertNotNull(repository);
    }
    
    @Test
    public void whenCreateRepositoryWithProperties()
    {
        Repository repository = createRepositoryWithProperties();
        Assert.assertNotNull(repository);
    }
    

    @Test @Ignore("Cannot prepare statement [Table/View 'BOOK' does not exist.]")
    public void whenListWithRepositoryConfigByDataSource()
    {
        Repository repository = new RepositoryJdbc(dataSourceDerby);
        Queryable q = QueryFactory.newInstance("getAllBooks");
        List<FlatBook> list = repository.list(q);
        Assert.assertTrue(list.size() == TOTAL_BOOKS);
        Assert.assertTrue(list.get(0) instanceof FlatBook);
        for (FlatBook b : list)
        {
            Assert.assertNotNull(b.getAuthor());
            Assert.assertNotNull(b.getIsbn());
            Assert.assertNotNull(b.getName());
            Assert.assertNotNull(b.getId());
        }
    }

    @Test @Ignore("Cannot prepare statement [Table/View 'BOOK' does not exist.]")
    public void whenListWithRepositoryConfigWithContext()
    {
        Repository repository = new RepositoryJdbc(SqlContextFactory.newInstance("/repository-sql.xml"));
        Queryable q = QueryFactory.newInstance("getAllBooks");
        List<FlatBook> list = repository.list(q);
        Assert.assertTrue(list.size() == TOTAL_BOOKS);
        Assert.assertTrue(list.get(0) instanceof FlatBook);
        for (FlatBook b : list)
        {
            Assert.assertNotNull(b.getAuthor());
            Assert.assertNotNull(b.getIsbn());
            Assert.assertNotNull(b.getName());
            Assert.assertNotNull(b.getId()); 
        }
    }

    @Test @Ignore("Cannot prepare statement [Table/View 'BOOK' does not exist.]")
    public void whenListWithRepositoryConfigByProperties()
    {
        Repository repository = createRepositoryWithProperties();
        Queryable q = QueryFactory.newInstance("getAllBooks");
        List<FlatBook> list = repository.list(q);
        Assert.assertTrue(list.size() == TOTAL_BOOKS);
        Assert.assertTrue(list.get(0) instanceof FlatBook);
        for (FlatBook b : list)
        {
            Assert.assertNotNull(b.getAuthor());
            Assert.assertNotNull(b.getIsbn());
            Assert.assertNotNull(b.getName());
            Assert.assertNotNull(b.getId());
        }
    }
    
    private Repository createRepositoryWithProperties()
    {
        //databaseName=newDB
        Properties props = new Properties();//BaseJdbc.config;// new Properties();
        props.put(RepositoryProperty.JDBC_URL.key(), DERBY_URL);
        //props.put(RepositoryProperty.JDBC_USER.key(), BaseJdbc.user);
        //props.put(RepositoryProperty.JDBC_PASSWORD.key(), BaseJdbc.pass);
        //props.put(RepositoryProperty.JDBC_DRIVER.key(), BaseJdbc.driver);
        //props.put(RepositoryProperty.JDBC_URL.key(), BaseJdbc.url);//+";databaseName=APP");
        // FIXME when use DriverManagerAdapter Cannot prepare statement [Schema 'ADMIN' does not exist]
        props.put(RepositoryProperty.JDBC_ADAPTER_FACTORY.key(), DriverManagerAdapter.class.getName());
        Repository repository = new RepositoryJdbc(props);
        return repository;
    }

}
