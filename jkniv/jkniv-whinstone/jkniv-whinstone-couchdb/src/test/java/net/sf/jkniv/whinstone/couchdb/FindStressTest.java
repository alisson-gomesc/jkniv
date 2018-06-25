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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.internal.matchers.LessThan;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.experimental.Chronometer;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchdb.model.orm.Author;
import net.sf.jkniv.whinstone.couchdb.model.orm.Session;
import net.sf.jkniv.whinstone.couchdb.result.CustomResultRow;

public class FindStressTest extends BaseJdbc
{
    final int LOOP = 500;
    @Test
    public void whenCouchDbListWithFixedFind()
    {
        Repository repositoryDb = getRepositoryDb3t();
        Chronometer.timer("Without Cache");
        for(int i=0;  i <LOOP; i++)
        {
            Queryable q = QueryFactory.of("session", "email", "alisson.gomesc@gmail.com");
            List<Session> list = repositoryDb.list(q);
            assertThat(list.size(), is(1));
        }
        Chronometer.pause("Without Cache");

        Chronometer.timer("With Cache");
        for(int i=0;  i <LOOP; i++)
        {
            Queryable q = QueryFactory.of("sessionWithCache", "email", "alisson.gomesc@gmail.com");
            List<Session> list = repositoryDb.list(q);
            assertThat(list.size(), is(1));
        }
        Chronometer.pause("With Cache");

        System.out.println(Chronometer.log());
        assertThat(Chronometer.getTimer("With Cache"), lessThan(Chronometer.getTimer("Without Cache")));
    }
}
