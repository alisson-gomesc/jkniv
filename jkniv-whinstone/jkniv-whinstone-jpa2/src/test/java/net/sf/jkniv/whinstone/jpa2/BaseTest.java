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
package net.sf.jkniv.whinstone.jpa2;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jpa2.test.infra.JndiJpaCreator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={ "/spring-context.xml" })
public class BaseTest
{
    public static final int   TOTAL_BOOKS = 15;
    
    @PersistenceContext(unitName="whinstone")
    protected EntityManager   em;

    //@PersistenceContext(unitName="sqlserver")
    protected EntityManager   emSqlServer;

    private static boolean    createdTx   = false;
    private static SqlContext sqlContext;
    private static SqlContext sqlContextSqlServer;
    
    protected Repository getRepository()
    {
        //EntityManager em = emf.createEntityManager();
        return new RepositoryJpa(em, sqlContext);
    }

    protected Repository getRepositorySqlServer()
    {
        return new RepositoryJpa(emSqlServer, sqlContextSqlServer);
    }

    @Before
    @Transactional
    public void setUp()
    {
        if (!createdTx)
        {
            System.out.println("@Before");
            JndiJpaCreator.bind("java:comp/env/persistence/whinstone",em);
            //JndiJpaCreator.bind("java:comp/env/persistence/sqlserver",emSqlServer);
            JndiJpaCreator.activate();
            sqlContext = SqlContextFactory.newInstance("/repository-sql.xml");
            sqlContextSqlServer = SqlContextFactory.newInstance("/repository-sql-server.xml");
            createdTx = true;
        }
    }
    
    @Test
    public void dummyTest()
    {
        org.junit.Assert.assertTrue(true);
    }
}
