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
package net.sf.jkniv.whinstone.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class EnumTypeTest
{

    @Test
    public void whenEnumSaveName()   
    {
        EnumNameType type = new EnumNameType(TimeUnit.class);
        
        assertThat(type.toAttribute("MINUTES").name(), is(TimeUnit.MINUTES.name()));
        assertThat(type.toAttribute("HOURS").name(), is(TimeUnit.HOURS.name()));

        assertThat(type.toJdbc(TimeUnit.MINUTES), is("MINUTES"));
        assertThat(type.toJdbc(TimeUnit.HOURS), is("HOURS"));
    }

    @Test
    public void whenEnumSaveOrdinal()  
    {
        EnumOrdinalType type = new EnumOrdinalType(TimeUnit.class);
        
        assertThat(type.toAttribute(4).ordinal(), is(TimeUnit.MINUTES.ordinal()));
        assertThat(type.toAttribute(5).ordinal(), is(TimeUnit.HOURS.ordinal()));
        
        assertThat(type.toJdbc(TimeUnit.MINUTES), is(4));
        assertThat(type.toJdbc(TimeUnit.HOURS), is(5));
    }
}
