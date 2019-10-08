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
package net.sf.jkniv.whinstone.classifier;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.sf.jkniv.whinstone.domain.orm.Color;

public class GroupingByTest
{
    
    @Test
    public void testingGroupingByOneField()
    {
        GroupingProtoBy<Color, Map<String,Object>> grouping = new GroupingProtoBy<Color, Map<String,Object>>(Arrays.asList("name"), Color.class);
        List<Map<String, Object>> mapColors = createColors();
        for(Map<String,Object> row : mapColors) 
        {
            grouping.classifier(row);
        }
        Collection<Color> colors = grouping.build();

        for(Color c : colors)
            System.out.println(c);

        Iterator<Color> it = colors.iterator();
        Color black = it.next();
        Color blue = it.next();
        Color white = it.next();
        assertThat(colors.size(), is(3));
        assertThat(black.getPriorities().size(), is(1));
        assertThat(blue.getPriorities().size(), is(5));
        assertThat(white.getPriorities().size(), is(3));
    }

    @Test
    public void testingGroupingByTwoFields()
    {
        GroupingProtoBy<Color, Map<String,Object>> grouping = new GroupingProtoBy<Color, Map<String,Object>>(Arrays.asList("name","code"), Color.class);
        List<Map<String, Object>> mapColors = createColors();
        for(Map<String,Object> row : mapColors) 
        {
            grouping.classifier(row);
        }
        Collection<Color> colors = grouping.buildAsList();

        for(Color c : colors)
            System.out.println(c);

        Iterator<Color> it = colors.iterator();
        Color black = it.next();
        Color blueA = it.next();
        Color blueB = it.next();
        Color blueC = it.next();
        Color whiteA = it.next();
        Color whiteB = it.next();
        assertThat(colors.size(), is(6));
        System.out.println("---------------------");
        System.out.println(black); assertThat(black.getPriorities().size(), is(1));
        System.out.println(blueA); assertThat(blueA.getPriorities().size(), is(3));
        System.out.println(blueB); assertThat(blueB.getPriorities().size(), is(1));
        System.out.println(blueC); assertThat(blueC.getPriorities().size(), is(1));
        System.out.println(whiteA); assertThat(whiteA.getPriorities().size(), is(2));
        System.out.println(whiteA); assertThat(whiteB.getPriorities().size(), is(1));
    }
    

    private List<Map<String, Object>> createColors() 
    {
        List<Map<String, Object>> colors = new ArrayList<Map<String, Object>>();
        // blue 5
        // blue code 3
        // white 3
        // white code 2
        // black 1
        // black code 1
        colors.add(newMap("black", "NORMAL", 100));
        colors.add(newMap("blue", "HIGH", 100));
        colors.add(newMap("blue", "LOW", 100));
        colors.add(newMap("blue", "NORMAL", 100));
        colors.add(newMap("blue", "NORMAL", 101));
        colors.add(newMap("blue", "HIGH", 102));
        colors.add(newMap("white", "HIGH", 100));
        colors.add(newMap("white", "LOW", 100));
        colors.add(newMap("white", "HIGH", 101));

        return colors;
    }
    
    private Map<String, Object> newMap(String color, String level, int code) 
    {
        Map<String, Object>  map = new HashMap<String, Object>();
        map.put("name", color);
        map.put("priority", level);
        map.put("code", code);
        return map;
    }
    
    /*
    private List<Author> createAuthors() {
        List<Author> authors = new ArrayList<Author>();

        authors.add(newAuthor("A", "street 1a"));
        authors.add(newAuthor("B", "street 2bs"));
        authors.add(newAuthor("A", "street 3a"));
        authors.add(newAuthor("C", "street 4c"));
        authors.add(newAuthor("D", "street 5d"));
        authors.add(newAuthor("A", "street 6a"));
        authors.add(newAuthor("E", "street 1e"));
        authors.add(newAuthor("B", "street 1b"));

        return authors;
    }
    
    private Author newAuthor(String name, String street) {
        Author a = new Author();
        a.setName(name);
        a.setAddress(new Address(street));
        return a;
    }
    */
}
