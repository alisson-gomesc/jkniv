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

import com.acme.domain.flat.Color;

import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Repository;
import net.sf.jkniv.whinstone.jpa2.BaseTest;

public class GroupingByJpaTest extends BaseTest
{
    
    @Test
    public void whenGroupingByOneField()
    {
        Repository repository = getRepository();

        Queryable q = QueryFactory.newInstance("test-groupingby1-after-select");
        List<Color> colors = repository.list(q);
        Iterator<Color> it = colors.iterator();
        Color black = it.next();
        Color blue = it.next();
        Color white = it.next();
        assertThat(colors.size(), is(3));
        assertThat(black.getName(), is("black"));
        assertThat(black.getPriorities().size(), is(1));
        assertThat(blue.getName(), is("blue"));
        assertThat(blue.getPriorities().size(), is(5));
        assertThat(white.getName(), is("white"));
        assertThat(white.getPriorities().size(), is(3));
    }
    
    @Test
    public void whenGroupingByTwoFields()
    {
        Repository repository = getRepository();
        Queryable q = QueryFactory.newInstance("test-groupingby2-after-select");
        List<Color> colors = repository.list(q);
        Iterator<Color> it = colors.iterator();
        Color black = it.next();
        Color blueA = it.next();
        Color blueB = it.next();
        Color blueC = it.next();
        Color whiteA = it.next();
        Color whiteB = it.next();
        assertThat(colors.size(), is(6));
        assertThat(black.getPriorities().size(), is(1));
        assertThat(blueA.getPriorities().size(), is(3));
        assertThat(blueB.getPriorities().size(), is(1));
        assertThat(blueC.getPriorities().size(), is(1));
        assertThat(whiteA.getPriorities().size(), is(2));
        assertThat(whiteB.getPriorities().size(), is(1));
        
    }
    
}
