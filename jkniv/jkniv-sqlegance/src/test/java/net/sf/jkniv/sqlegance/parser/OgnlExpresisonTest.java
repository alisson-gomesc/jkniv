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
package net.sf.jkniv.sqlegance.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;

import org.junit.Assert;
import org.junit.Test;

import net.sf.jkniv.sqlegance.domain.orm.Address;
import net.sf.jkniv.sqlegance.domain.orm.Author;
import net.sf.jkniv.sqlegance.domain.orm.FooDomain;

public class OgnlExpresisonTest
{
    
    @Test
    public void understandExpresionOgnlTest1() throws OgnlException
    {
        Address address = new Address("590 Madison Ave Bsmt");
        Author author = new Author();
        author.setName("Bob Dylan");
        author.setAddress(address);
        FooDomain fooObj = new FooDomain(null, 5, null, null, author);
        
        Object exp1 = Ognl.parseExpression("name == null");
        Object exp2 = Ognl.parseExpression("name == null and age == 5");
        Object exp3 = Ognl.parseExpression("name == null and age == 58");
        Object exp4 = Ognl.parseExpression("author.name != null");
        Object exp5 = Ognl.parseExpression("author.address.name != null");
        
        Assert.assertTrue((Boolean) Ognl.getValue(exp1, fooObj));
        Assert.assertTrue((Boolean) Ognl.getValue(exp2, fooObj));
        Assert.assertFalse((Boolean) Ognl.getValue(exp3, fooObj));
        Assert.assertTrue((Boolean) Ognl.getValue(exp4, fooObj));
        Assert.assertTrue((Boolean) Ognl.getValue(exp5, fooObj));
    }
    
    @Test
    public void understandExpresionOgnlTest2() throws OgnlException
    {
        Map fooMap = new HashMap();
        fooMap.put("name", "Bob Marley");
        fooMap.put("age", 36);
        
        Object exp1 = Ognl.parseExpression("name != null");
        Object exp2 = Ognl.parseExpression("name == null");
        Object exp3 = Ognl.parseExpression("name != null and age == 36");
        
        Assert.assertTrue((Boolean) Ognl.getValue(exp1, fooMap));
        Assert.assertFalse((Boolean) Ognl.getValue(exp2, fooMap));
        Assert.assertTrue((Boolean) Ognl.getValue(exp3, fooMap));
    }
    
    @Test
    public void ognlArrayTest() throws OgnlException
    {
        int[] params = {10,20,30,50};        
        Object exp1 = Ognl.parseExpression("[0] == 10");
        Object exp2 = Ognl.parseExpression("[1] == 20");
        Object exp3 = Ognl.parseExpression("[2] == 30");
        Object exp4 = Ognl.parseExpression("[3] == 40");
        
        Assert.assertTrue((Boolean) Ognl.getValue(exp1, params));
        Assert.assertTrue((Boolean) Ognl.getValue(exp2, params));
        Assert.assertTrue((Boolean) Ognl.getValue(exp3, params));
        Assert.assertFalse((Boolean) Ognl.getValue(exp4, params));
    }

    @Test
    public void ognlListTest() throws OgnlException
    {
        List<Integer> params = new ArrayList<Integer>();
        params.add(10);
        params.add(20);
        params.add(30);
        Object exp1 = Ognl.parseExpression("[0] == 10");
        Object exp2 = Ognl.parseExpression("[1] == 20");
        Object exp3 = Ognl.parseExpression("[2] == 30");
        Object exp4 = Ognl.parseExpression("[2] == 40");
        
        Assert.assertTrue((Boolean) Ognl.getValue(exp1, params));
        Assert.assertTrue((Boolean) Ognl.getValue(exp2, params));
        Assert.assertTrue((Boolean) Ognl.getValue(exp3, params));
        Assert.assertFalse((Boolean) Ognl.getValue(exp4, params));
    }

}
