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
import static org.hamcrest.Matchers.instanceOf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.couchdb.model.orm.Author;
import net.sf.jkniv.whinstone.couchdb.model.orm.AuthorForDelete;

@Ignore("delete me test from couchdb")
public class CouchDbRepositoryBulkDeleteTest extends BaseJdbc
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    private final static String KEY1 = "DEL-01";
    private final static String KEY2 = "DEL-02";
    private final static String KEY3 = "DEL-01";
    private final static String KEY4 = "DEL-02";
    private final static String KEY5 = "DEL-01";
    private final static String KEY6 = "DEL-02";

    @BeforeClass
    public static void setUp()
    {
        Repository repository = getRepository();
        Author a1 = new Author(), a2 = new Author(), a3 = new Author(), a4 = new Author(), a5 = new Author(), a6 = new Author();
        a1.setId(KEY1);
        a2.setId(KEY2);
        a2.setId(KEY3);
        a2.setId(KEY4);
        a2.setId(KEY5);
        a2.setId(KEY6);
        
        a1.setName("Paulo Coelho");
        a2.setName("Augusto Cury");
        a3.setName("TOLLE, ECKHART");
        a4.setName("CORTELLA, MARIO SERGIO");
        a5.setName("Deepak Chopra");
        a6.setName("STAYING STRONG");
        repository.add(a1);
        repository.add(a2);
        repository.add(a3);
        repository.add(a4);
        repository.add(a5);
        repository.add(a6);
    }

    @Test
    public void whenDeleteBulkDocumentsWithoutDeleteProperty()
    {
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("DELETE Bulk command must have an [boolean isDeleted()] method annotated with @JsonProperty(\"_deleted\") for " + Author.class.getName() +" type and setted as TRUE.");

        Repository repository = getRepository();
        List<Author> authors = new ArrayList<Author>();
        Author a1 = repository.get(Author.class, KEY1);
        Author a2 = repository.get(Author.class, KEY2);
        authors.add(a1); authors.add(a2);

        Queryable q = QueryFactory.of("remove", authors);
        repository.remove(q);
    }

    @Test
    public void whenDeleteBulkDocumentsWithoutMarkToDelete()
    {
        catcher.expect(RepositoryException.class);
        catcher.expectMessage("DELETE Bulk command must have an [boolean isDeleted()] method annotated with @JsonProperty(\"_deleted\") for " + AuthorForDelete.class.getName() +" type and setted as TRUE.");
        Repository repository = getRepository();
        List<AuthorForDelete> authors = new ArrayList<AuthorForDelete>();
        AuthorForDelete a1 = repository.get(AuthorForDelete.class, KEY3);
        AuthorForDelete a2 = repository.get(AuthorForDelete.class, KEY4);
        
        authors.add(a1); authors.add(a2);
        Queryable q = QueryFactory.of("remove", authors);
        repository.remove(q);
    }

    @Test
    public void whenDeleteBulkDocuments()
    {
        Repository repository = getRepository();
        List<AuthorForDelete> authors = new ArrayList<AuthorForDelete>();
        AuthorForDelete a1 = repository.get(AuthorForDelete.class, KEY5);
        AuthorForDelete a2 = repository.get(AuthorForDelete.class, KEY6);
        
        a1.setDeleted(true);
        a2.setDeleted(true);
        authors.add(a1); authors.add(a2);
        
        Queryable q = QueryFactory.of("remove", authors);
        
        int rows = repository.remove(q);
        
        assertThat(rows, is(2));
        assertThat(repository.get(Author.class, KEY5),  nullValue());
        assertThat(repository.get(Author.class, KEY6), nullValue());
    }


}
