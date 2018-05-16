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
package net.sf.jkniv.whinstone.jpa2.classification;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.acme.domain.noorm.Author;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.jpa2.BaseTest;

public class OneToManyJpaTest extends BaseTest
{
    
    @Test
    public void whenGroupingForOneToMany()
    {
        Queryable q = QueryFactory.newInstance("oneToManyBooksFromAuthors");
        List<Author> authors = getRepository().list(q);
        Iterator<Author> it = authors.iterator();
        
        Author author1 = it.next();
        Author author2 = it.next();
        Author author3 = it.next();
        Author author4 = it.next();
        Author author5 = it.next();

        assertThat(authors.size(), is(5));
        assertThat(author1.getId(), is(5L));
        assertThat(author1.getName(), is("Albert Camus"));
        assertThat(author1.getBooks().size(), is(2));
        assertThat(author1.getBooks().get(0).getName(), is("The Rebel: An Essay on Man in Revolt"));
        assertThat(author1.getBooks().get(1).getName(), is("The Stranger"));
        
        assertThat(author2.getId(), is(3L));
        assertThat(author2.getName(), is("Carlos Drummond de Andrade"));
        assertThat(author2.getBooks().size(), is(5));
        assertThat(author2.getBooks().get(0).getName(), is("A Lição do Amigo"));
        assertThat(author2.getBooks().get(1).getName(), is("Alguma Poesia"));
        assertThat(author2.getBooks().get(2).getName(), is("Claro Enigma"));
        assertThat(author2.getBooks().get(3).getName(), is("José"));
        assertThat(author2.getBooks().get(4).getName(), is("Sentimento do Mundo"));
        
        assertThat(author3.getId(), is(4L));
        assertThat(author3.getName(), is("Franz Kafka"));
        assertThat(author3.getBooks().size(), is(2));
        assertThat(author3.getBooks().get(0).getName(), is("The Metamorphosis"));
        assertThat(author3.getBooks().get(1).getName(), is("The Trial"));
        
        assertThat(author4.getId(), is(1L));
        assertThat(author4.getName(), is("Friedrich Nietzsche"));
        assertThat(author4.getBooks().size(), is(1));
        assertThat(author4.getBooks().get(0).getName(), is("Beyond Good and Evil"));

        assertThat(author5.getId(), is(2L));
        assertThat(author5.getName(), is("Martin Fowler"));
        assertThat(author5.getBooks().size(), is(5));
        assertThat(author5.getBooks().get(0).getName(), is("Analysis Patterns: Reusable Object Models"));
        assertThat(author5.getBooks().get(1).getName(), is("Domain-Specific Languages (Addison-Wesley Signature Series"));
        assertThat(author5.getBooks().get(2).getName(), is("NoSQL Distilled: A Brief Guide to the Emerging World of Polyglot Persistence"));
        assertThat(author5.getBooks().get(3).getName(), is("Patterns of Enterprise Application Architecture"));
        assertThat(author5.getBooks().get(4).getName(), is("Refactoring: Ruby Edition: Ruby Edition"));
        
    }
    
}
