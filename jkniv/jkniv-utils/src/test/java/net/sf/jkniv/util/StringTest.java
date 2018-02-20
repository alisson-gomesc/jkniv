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

import org.hamcrest.Matchers;

//import static org.hamcrest.Matchers.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
//import static org.hamcrest.core.*;

public class StringTest
{
    
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

    
}
