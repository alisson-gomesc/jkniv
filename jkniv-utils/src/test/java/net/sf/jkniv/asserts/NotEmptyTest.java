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
package net.sf.jkniv.asserts;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.NotEmpty;

import org.junit.Assert;
import org.junit.Test;

public class NotEmptyTest
{
    @Test(expected = IllegalArgumentException.class)
    public void whenParamenterIsNull()
    {
        Assertable notEmpty = new NotEmpty();
        notEmpty.verify(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void whenParamenterArrayZero()
    {
        Assertable notEmpty = new NotEmpty();
        notEmpty.verify(new Object[0]);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void whenArrayHaveNullValues()
    {
        Assertable notEmpty = new NotEmpty();
        String[] elements = {"A", null, "C"};
        notEmpty.verify(elements);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void whenParamenterIsEmpty()
    {
        Assertable notEmpty = new NotEmpty();
        notEmpty.verify("");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void whenLeastOneParameterIsEmpty()
    {
        Assertable notEmpty = new NotEmpty();
        String s1 = "", s2 = null;
        notEmpty.verify(s1, s2);
    }
    
    @Test
    public void whenParamenterIsEmptyWithSpecifcMessage()
    {
        Assertable notEmpty = new NotEmpty("Oh! my object is empty");
        String s1 = "";
        try
        {
            notEmpty.verify(s1);
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertEquals("Oh! my object is empty", e.getMessage());
        }
    }
    
    @Test
    public void whenIsNotEmptyParameters()
    {
        Assertable notEmpty = new NotEmpty();
        String s1 = "A", s2 = "B", s3 = "C";
        notEmpty.verify(s1, s2, s3);
        Assert.assertTrue(true);
    }
    
    // ------------------------- verifyArray tests --------------------
    

    @Test(expected = IllegalArgumentException.class)
    public void whenArrayIsNull()
    {
        Assertable notEmpty = new NotEmpty();
        String[] elements = null;
        notEmpty.verifyArray(elements);
        Assert.assertTrue(true);
    }

    @Test
    public void whenArrayHaveNullValuesButArrayIsNotNull()
    {
        Assertable notEmpty = new NotEmpty();
        String[] elements = {"A", null, "C"};
        notEmpty.verifyArray(elements);
        Assert.assertTrue(true);
    }

}
