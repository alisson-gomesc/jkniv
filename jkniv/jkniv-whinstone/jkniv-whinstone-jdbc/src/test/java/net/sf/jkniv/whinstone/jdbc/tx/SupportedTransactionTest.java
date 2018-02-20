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
package net.sf.jkniv.whinstone.jdbc.tx;


import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.whinstone.jdbc.RepositoryJdbc;
import net.sf.jkniv.whinstone.jdbc.UnsupportedTransactionException;

public class SupportedTransactionTest
{
    @Test(expected=UnsupportedTransactionException.class)
    public void whenRepositoryJdbcUnsupportedGlobalTransaction()
    {
        SqlContext sqlContext = SqlContextFactory.newInstance("/repository-sql-drivermanager.xml", "whinstone-global-tx-unsupported");
        new RepositoryJdbc(sqlContext);
        assertThat("RepositoryJdbc cannot supports Global transaction", true, is(false));
    }
    
    @Test(expected=UnsupportedTransactionException.class)
    public void whenRepositoryJdbcUnsupportedEjbTransaction()
    {
        SqlContext sqlContext = SqlContextFactory.newInstance("/repository-sql-drivermanager.xml", "whinstone-ejb-tx-unsupported");
        new RepositoryJdbc(sqlContext);
        assertThat("RepositoryJdbc cannot supports Ejb transaction", true, is(false));
    }
    
    @Test
    public void whenRepositoryJdbcSupportsLocalTransaction()
    {
        SqlContext sqlContext = SqlContextFactory.newInstance("/repository-sql-drivermanager.xml", "whinstone-ejb-local-supported");
        Repository repo = new RepositoryJdbc(sqlContext);
        assertThat("Repository Loaded", repo, is(notNullValue()));
    }
}
