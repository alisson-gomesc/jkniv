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
package net.sf.jkniv.whinstone.cassandra;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.NonUniqueResultException;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.cassandra.model.Vehicle;

public class CassandraEnricchTest extends BaseJdbc
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @Test
    public void whenEnrichObject()
    {
        Repository repositoryDb = getRepository();
        Vehicle v1 = new Vehicle("OMN7176");
        
        Vehicle bugatti = repositoryDb.get(v1);
        
        assertThat(bugatti.getName(), is("bugatti"));
        assertThat(bugatti.getColor(), is("white"));
        assertThat(bugatti.getPlate(), is("OMN7176"));
        assertThat(bugatti.getAlarms().size(), is(2));
        assertThat(bugatti.getAlarms().get(0), is("anchor"));
        assertThat(bugatti.getAlarms().get(1), is("over_speed"));
        
        bugatti.setPlate("OMN7001");
        Queryable q2 = QueryFactory.of("Vehicle#get", bugatti);
        
        boolean enriched = repositoryDb.enrich(q2);
        assertThat(enriched, is(true));
        assertThat(bugatti.getName(), is("mustang"));
        assertThat(bugatti.getColor(), is("blue"));
        assertThat(bugatti.getPlate(), is("OMN7001"));
        assertThat(bugatti.getAlarms().size(), is(2));
        assertThat(bugatti.getAlarms().get(0), is("anchor"));
        assertThat(bugatti.getAlarms().get(1), is("over_speed"));
    }
    
    @Test
    public void whenScalarReturnNonUniqueResult()
    {
        catcher.expect(NonUniqueResultException.class);
        catcher.expectMessage("No unique result for query [all-vehicles]");

        Repository repositoryDb = getRepository();
        Vehicle bugatti = new Vehicle("OMN7001");
        Queryable q2 = QueryFactory.of("all-vehicles", bugatti);
        repositoryDb.enrich(q2);
    }

}
