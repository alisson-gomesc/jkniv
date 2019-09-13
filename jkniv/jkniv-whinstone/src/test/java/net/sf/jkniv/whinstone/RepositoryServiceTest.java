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
package net.sf.jkniv.whinstone;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.RepositoryConfigException;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.whinstone.spi.RepositoryFactory;

public class RepositoryServiceTest
{
    @Rule
    public ExpectedException  catcher = ExpectedException.none();

    @Test
    public void whenLookupNotFoundService() 
    {
        catcher.expect(RepositoryConfigException.class);
        catcher.expectMessage("RepositoryFactory for [JPA] type cannot be found, verify if jar file from repository it's set in classpath");
        
        RepositoryService service = RepositoryService.getInstance();
        assertThat(service.lookup(RepositoryType.JPA), nullValue());
    }
    
    @Test
    public void whenLookupRepositoryService() 
    {
        RepositoryService service = RepositoryService.getInstance();
        RepositoryFactory factory = service.lookup(RepositoryType.JDBC);
        assertThat(factory, notNullValue());
        assertThat(factory, instanceOf(RepositoryFactory.class));
        assertThat(factory.newInstance(), instanceOf(Repository.class));
        assertThat(factory.newInstance(new Properties()), instanceOf(Repository.class));
        assertThat(factory.newInstance("/repository-sql.xml"), instanceOf(Repository.class));
    }
}
