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

import java.util.Date;

import net.sf.jkniv.acme.domain.Child;
import net.sf.jkniv.acme.domain.Father;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrimitiveTypesTest
{
    private static final Logger LOG = LoggerFactory.getLogger(PrimitiveTypesTest.class);
    
    @Test
    public void whenSubtypeIsAssignableFromSuperType()
    {
        assertThat(Father.class.isAssignableFrom(Child.class), is(true));
    }
        
    @Test
    public void castTest()
    {
        double d = 0.1;
        Double D = Double.valueOf(0.5);
        float f = 0.1F;
        Float F = 0.1F;
        boolean b = true;
        Boolean B = Boolean.FALSE;
        int i = 0;
        Integer I = new Integer(1);
        char c = 'r';
        Character C = new Character('D');
        
        Assert.assertFalse(Double.class.isInstance(f));
        Assert.assertTrue(Double.class.isInstance(d));
        Assert.assertTrue(Double.class.isInstance(D));
        Assert.assertTrue(Boolean.class.isInstance(b));
        Assert.assertTrue(Boolean.class.isInstance(B));
        Assert.assertTrue(Float.class.isInstance(f));
        Assert.assertTrue(Float.class.isInstance(F));
        Assert.assertTrue(Integer.class.isInstance(i));
        Assert.assertTrue(Integer.class.isInstance(I));
        Assert.assertTrue(Character.class.isInstance(c));
        Assert.assertTrue(Character.class.isInstance(C));
    }
    
    @Test
    public void howToGetCanonicalNameFromArray()
    {
        boolean[] boleans = new boolean[2];
        boleans[0] = true;
        boleans[1] = false;
        
        assertThat(boleans.getClass().getCanonicalName(), is("boolean[]"));
        assertThat(boleans.getClass().getName(), is("[Z"));
        assertThat(Integer.class.getCanonicalName(), is("java.lang.Integer"));
        assertThat(Integer.class.getName(), is("java.lang.Integer"));
    }
    
    @Test
    public void main()
    {
        LOG.error("Cannot converter {} to java.sql.Time.", new Date().getClass().getCanonicalName());
    }
    
}
