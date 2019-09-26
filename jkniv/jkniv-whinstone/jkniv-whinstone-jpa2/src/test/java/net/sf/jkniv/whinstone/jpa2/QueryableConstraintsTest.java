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
package net.sf.jkniv.whinstone.jpa2;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.acme.domain.noorm.Author;

import net.sf.jkniv.sqlegance.NonUniqueResultException;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;

@SuppressWarnings("rawtypes")
public class QueryableConstraintsTest extends BaseTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();
    
    @Test //@Ignore("java.lang.ClassCastException: java.lang.String cannot be cast to [Ljava.lang.Object;")
    public void whenScalarReturnMoreOnRow()
    {
        Queryable q = QueryFactory.of("getScalarAuthorName");
        catcher.expectMessage("No unique result for query [" + q.getName() + "] with params [" + q.getParams()
                + "result fetch [6] rows, Repository.get(..) method must return just one row]");
        
        getRepository().scalar(q);
    }
    
    @Test
    public void whenGetReturnMoreOnRow()
    {
        Queryable q = QueryFactory.of("getScalarAuthorName", Author.class);
        catcher.expect(NonUniqueResultException.class);
        catcher.expectMessage("No unique result for query [" + q.getName() + "] with params [" + q.getParams()
                + "result fetch [6] rows, Repository.get(..) method must return just one row]");
        
        getRepository().get(q);
    }
    
    @Test
    public void whenGetScalarName()
    {
        Queryable q = QueryFactory.of("getScalarAuthorName", "id", 1);
        Repository repository = getRepository();
        String name = repository.scalar(q);
        assertThat(name, notNullValue());
        assertThat(name, is("Friedrich Nietzsche"));
        assertThat(q.isScalar(), is(true));
    }
}
