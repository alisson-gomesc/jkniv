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
package net.sf.jkniv.whinstone.jdbc.database.cassandra;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;


@Ignore("RepositoryJdbc doesn't supports Cassandra driver")
public class CassandraRepositoryTest extends BaseJdbc
{
    @Autowired
    Repository repositoryCas;
    Object[] params = {"k001",new Date(), "CAR001",20.683940,-88.567740,2};
            
    @Test
    public void whenSimplePagingQueryWorks()
    {
        Queryable q = getQuery("simpleInsert", params);
        repositoryCas.add(q);
    }
    
    @Test
    public void whenPagingWithParamsQueryWorks()
    {
        Queryable q1 = getQuery("simpleInsert", params);
        Queryable q2 = getQuery("simpleSelect");
        
        repositoryCas.add(q1);
        
        List<Map> list = repositoryCas.list(q2);
        assertThat(list.size(), greaterThan(0));
    }
    
}
