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
package net.sf.jkniv.whinstone.couchdb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;

import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;

@Ignore("delete me test from couchdb")
public class CouchDbRepositoryFindClauseINTest extends BaseJdbc
{
    
    @Test @Ignore("Test copy of another scenary")
    public void whenCouchDbListWithFixedFind()
    {
        Repository repositoryDb = getRepository();
        Queryable q = getQuery("authorsBR");
        
        List<Map> list = repositoryDb.list(q);
        assertThat(list.size(), greaterThan(0));
        assertThat(list.get(0), instanceOf(Map.class));
        System.out.println(list.get(0));
    }
    

}
