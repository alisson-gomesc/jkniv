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
package net.sf.jkniv.sqlegance.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class CalendarAsIntTypeTest
{
    @Test
    public void whenCalendarIsInt()  
    {
        CalendarAsIntType type = new CalendarAsIntType("yyyyMMdd");
        Calendar d1 = Calendar.getInstance();
        Calendar d2 = Calendar.getInstance();
        d1.setTime(new Date(2019-1900, 0, 1));
        d2.setTime(new Date(2019-1900, 1, 10));
        
        assertThat(type.toAttribute(20190101), is(d1));
        assertThat(type.toAttribute(20190210), is(d2));

        assertThat(type.toJdbc(d1), is(20190101));
        assertThat(type.toJdbc(d2), is(20190210));
    }
}
