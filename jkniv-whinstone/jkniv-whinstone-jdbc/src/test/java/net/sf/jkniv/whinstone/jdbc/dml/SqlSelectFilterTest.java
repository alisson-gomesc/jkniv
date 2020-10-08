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
package net.sf.jkniv.whinstone.jdbc.dml;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.jdbc.BaseJdbc;
import net.sf.jkniv.whinstone.jdbc.domain.flat.Item;
import net.sf.jkniv.whinstone.jdbc.domain.flat.ItemFilter;

public class SqlSelectFilterTest extends BaseJdbc
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  

    @Autowired
    Repository repositoryDerby;

    @Test 
    public void whenSelectItemsWithJavaOrderBy()
    {
        Queryable queryable = QueryFactory.builder()
                .filter(new ItemFilter(150F))
                .build("getAllItems");
        
        List<Item> items = repositoryDerby.list(queryable);
        Float MIN_PRICE = 150F;
        assertThat(items.get(0).getName(), is("F"));
        assertThat(items.get(0).getPrice(), greaterThanOrEqualTo(MIN_PRICE));
        assertThat(items.get(1).getName(), is("G"));
        assertThat(items.get(1).getPrice(), greaterThanOrEqualTo(MIN_PRICE));
        assertThat(items.get(2).getName(), is("H"));
        assertThat(items.get(2).getPrice(), greaterThanOrEqualTo(MIN_PRICE));
        assertThat(items.get(3).getName(), is("I"));
        assertThat(items.get(3).getPrice(), greaterThanOrEqualTo(MIN_PRICE));
        assertThat(items.get(4).getName(), is("J"));
        assertThat(items.get(4).getPrice(), greaterThanOrEqualTo(MIN_PRICE));
    }
}
