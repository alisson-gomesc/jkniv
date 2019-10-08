/* 
 * JKNIV, utils - Helper utilities for jdk code.
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
package net.sf.jkniv.util;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import org.hamcrest.Matchers;

//import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
//import static org.hamcrest.core.*;

public class StringTest
{
    @Test
    public void whenUseNameOrToStringForEnums()
    {
        System.out.printf("%s = %s\n", TimeUnit.MINUTES.toString(), TimeUnit.MINUTES.name());
        assertThat(TimeUnit.MINUTES.toString(), is(TimeUnit.MINUTES.name()));
    }

    @Test
    public void whenParseDateWithVariableString() throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd'st' MMM yyyy");
        
        Date d = sdf.parse("01st Mar 2019");
        assertThat(d, notNullValue());
    }
    @Test
    public void whenMyStringHasPercentToFormatter()
    {
        String message = "My Message with zero arg %s";
        int position = message.indexOf("%s");
        assertThat(position>0, is(true));
    }
    
    @Test
    public void whenStringHasFormatterNullOrEmptyReplaceThat()
    {
        String message = "My Message with zero arg";
        String messageArgEmpty = String.format(message+"%s", "");
        String messageArgNull = String.format(message+"%s", null);
        
        assertThat(messageArgEmpty, is(message));
        assertThat(messageArgNull, is(message+"null"));
    }
    
    @Test
    public void whenSplitStringsNoMatchers()
    {
        String[] s = "".split(",");
        assertThat(s, Matchers.arrayWithSize(1));
        assertThat(s[0],  is(""));
        s = "hola".split(",");
        assertThat(s,  Matchers.arrayWithSize(1));
        assertThat(s[0],  is("hola"));
    }

    
    @Test 
    public void whenInstanceNullReference()
    {
        String s = null;
        Integer i = null;
        newString(s);
        newInteger(i);
        assertThat(s, nullValue());
        assertThat(i, nullValue());
    }

    @Test 
    public void whenInstanceNonNullReference()
    {
        String s = "A";
        Integer i = 1;
        newString(s);
        newInteger(i);
        assertThat(s, notNullValue());
        assertThat(i, notNullValue());
        assertThat(s, is("A"));
        assertThat(i, is(1));
    }

    private void newString(String s)
    {
        s = "B";
        assertThat(s, is("B"));
    }

    private void newInteger(Integer i)
    {
        i = Integer.valueOf(2);
        assertThat(i, is(2));
    }

}
