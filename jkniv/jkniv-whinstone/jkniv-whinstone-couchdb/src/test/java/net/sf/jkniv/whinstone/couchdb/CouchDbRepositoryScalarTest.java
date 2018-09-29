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

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchdb.model.orm.Author;

public class CouchDbRepositoryScalarTest extends BaseJdbc
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @Test
    public void whenScalarReturnOnField()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("authorName", "natio", "GB");
        
        String name = repositoryDb.scalar(q);
        assertThat(name, instanceOf(String.class));
        assertThat(name, is("Martin Fowler"));
    }

    @Test
    public void whenScalarReturnNonUniqueResult()
    {
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("No unique result for query [authorName]");
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("authorName", "natio", "DE");
        repositoryDb.scalar(q);
    }

    @Test
    public void whenScalarReturnNonUniqueField()
    {
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("Query [authorNameIds] no return scalar value, scalar function must return unique field");
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("authorNameIds", "natio", "GB");
        repositoryDb.scalar(q);
    }

    @Test
    public void whenScalarNullReturn()
    {
        Repository repositoryDb = getRepository();
        Queryable q = QueryFactory.of("authorName", "natio", "NO_EXIST");
        String name = repositoryDb.scalar(q);
        assertThat(name, nullValue());        
    }

}
