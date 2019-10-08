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
package net.sf.jkniv.reflect;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.Locale;

import org.junit.Test;

public class MethodReflectionTest
{
    @Test
    public void whenInvokeMethodWithSubtypeAsArgument()
    {
        // TODO test me
    }
    
    
    @Test
    public void whenGetAttributeNameFromSetMethodName()
    {        
        String name = "setValueOfString";
        if (name.startsWith("set"))
        {
            name = name.substring(3);
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }
        assertThat(name, is("valueOfString"));
    }

    @Test
    public void whenGetAttributeNameFromGetMethodName()
    {        
        String name = "getValueOfString";
        if (name.startsWith("get"))
        {
            name = name.substring(3);
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }
        else if (name.startsWith("is"))
        {
            name = name.substring(2);
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }
        assertThat(name, is("valueOfString"));
    }
}
