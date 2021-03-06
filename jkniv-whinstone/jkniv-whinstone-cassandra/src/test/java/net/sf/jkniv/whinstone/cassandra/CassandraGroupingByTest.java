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
package net.sf.jkniv.whinstone.cassandra;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.cassandra.model.Color;

public class CassandraGroupingByTest extends BaseJdbc
{
    @Test
    public void whenRelationOneToManyByOneFieldUsingSimpleString()
    {
        Repository repository = getRepository();
        Queryable q = QueryFactory.of("oneToManyColor");
        List<Color> colors = repository.list(q);
        Iterator<Color> it = colors.iterator();
        Color black = it.next();
        Color blue = it.next();
        Color white = it.next();
        assertThat(colors.size(), is(3));
        assertThat(blue.getPriorities().size(), is(3));
        assertThat(white.getPriorities().size(), is(3));
        assertThat(black.getPriorities().size(), is(3));
        
        assertThat(blue.getPriorities(), containsInAnyOrder("HIGH", "NORMAL", "LOW"));
        assertThat(white.getPriorities(), containsInAnyOrder("HIGH", "NORMAL", "LOW"));
        assertThat(black.getPriorities(), containsInAnyOrder("HIGH", "NORMAL", "LOW"));
    }
    
    /*    
    @Test
    public void whenGroupingByTwoFields()
    {
        Repository repository = getRepository();
        Queryable q = QueryFactory.of("test-groupingby2-after-select");
        List<Color> colors = repository.list(q);
        Iterator<Color> it = colors.iterator();
        Color black = it.next();
        Color blueA = it.next();
        Color blueB = it.next();
        Color blueC = it.next();
        Color whiteA = it.next();
        Color whiteB = it.next();
        assertThat(colors.size(), is(6));
        assertThat(blueA.getPriorities().size(), is(3));
        assertThat(blueB.getPriorities().size(), is(1));
        assertThat(blueC.getPriorities().size(), is(1));
        assertThat(whiteA.getPriorities().size(), is(2));
        assertThat(whiteB.getPriorities().size(), is(1));
        assertThat(black.getPriorities().size(), is(1));
    }
    
    @Test
    public void whenGroupingForOneToMany()
    {
        Repository repository = getRepository();
        Queryable q = QueryFactory.of("oneToManyBooksFromAuthors");
        List<Author> authors = repository.list(q);
        assetOneToMany(authors);
    }
    
    @Test
    public void whenGroupingForOneToManyWithourOrderBy()
    {
        Repository repository = getRepository();
        Queryable q = QueryFactory.of("oneToManyBooksFromAuthorsWithourOrderBy");
        List<Author> authors = repository.list(q);
        assetOneToMany(authors);
    }    

    private void assetOneToMany(List<Author> authors)
    {
        Iterator<Author> it = authors.iterator();
        
        Author author1 = it.next();
        Author author2 = it.next();
        Author author3 = it.next();
        Author author4 = it.next();
        Author author5 = it.next();

        assertThat(authors.size(), is(5));
                        
        assertThat(author1.getId(), is(1L));
        assertThat(author1.getName(), is("Friedrich Nietzsche"));
        assertThat(author1.getBooks().size(), is(1));
        assertThat(author1.getBooks().get(0).getName(), is("Beyond Good and Evil"));

        assertThat(author2.getId(), is(2L));
        assertThat(author2.getName(), is("Martin Fowler"));
        assertThat(author2.getBooks().size(), is(5));
        assertThat(author2.getBooks().get(0).getName(), is("Analysis Patterns: Reusable Object Models"));
        assertThat(author2.getBooks().get(1).getName(), is("Domain-Specific Languages (Addison-Wesley Signature Series"));
        assertThat(author2.getBooks().get(2).getName(), is("NoSQL Distilled: A Brief Guide to the Emerging World of Polyglot Persistence"));
        assertThat(author2.getBooks().get(3).getName(), is("Patterns of Enterprise Application Architecture"));
        assertThat(author2.getBooks().get(4).getName(), is("Refactoring: Ruby Edition: Ruby Edition"));        
        
        assertThat(author3.getId(), is(3L));
        assertThat(author3.getName(), is("Carlos Drummond"));
        assertThat(author3.getBooks().size(), is(5));
        assertThat(author3.getBooks().get(0).getName(), is("A Lição do Amigo"));
        assertThat(author3.getBooks().get(1).getName(), is("Alguma Poesia"));
        assertThat(author3.getBooks().get(2).getName(), is("Claro Enigma"));
        assertThat(author3.getBooks().get(3).getName(), is("José"));
        assertThat(author3.getBooks().get(4).getName(), is("Sentimento do Mundo"));
        
        assertThat(author4.getId(), is(4L));
        assertThat(author4.getName(), is("Franz Kafka"));
        assertThat(author4.getBooks().size(), is(2));
        assertThat(author4.getBooks().get(0).getName(), is("The Metamorphosis"));
        assertThat(author4.getBooks().get(1).getName(), is("The Trial"));

        assertThat(author5.getId(), is(5L));
        assertThat(author5.getName(), is("Albert Camus"));
        assertThat(author5.getBooks().size(), is(2));
        assertThat(author5.getBooks().get(0).getName(), is("The Rebel: An Essay on Man in Revolt"));
        assertThat(author5.getBooks().get(1).getName(), is("The Stranger"));

    }
    */
}
