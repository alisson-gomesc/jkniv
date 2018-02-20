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
package net.sf.jkniv.reflect.beans;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;


import static org.hamcrest.CoreMatchers.instanceOf;

import org.junit.Test;

import net.sf.jkniv.acme.domain.FooColor;
import net.sf.jkniv.exception.HandlerException;

public class BasicInvokeTest
{
    @Test
    public void whenBasicInvokedForProxyToBuildBasicTypes()
    {
        ObjectProxy<String> p = ObjectProxyFactory.newProxy(String.class);
        p.setConstructorArgs("Hi");
        String s = (String) p.newInstance();
        assertThat(s, is("Hi"));
        assertThat(s, instanceOf(String.class));
        
        ObjectProxy<Double> d = ObjectProxyFactory.newProxy(Double.class);
        d.setConstructorArgs("0.2");
        Double result2 = d.newInstance();
        assertThat(result2, is(0.2));
        assertThat(result2, instanceOf(Double.class));

        
//        ObjectProxy<Integer> i = ObjectProxyFactory.newProxy(int.class);
//        i.setConstructorArgs(33);
//        Integer result3 = i.newInstance();
//        assertThat(result3, is(33));
//        assertThat(result3, instanceOf(Integer.class));


    }
    
    @Test
    public void whenBasicInvokeToBuildBasicTypes()
    {
        Invokable call = new BasicInvoke(new HandlerException());

        boolean resultB = call.invoke(Boolean.class, "true");
        assertThat(resultB, is(true));
        assertThat(resultB, instanceOf(Boolean.class));
        resultB =  call.invoke(Boolean.class, "false");
        assertThat(resultB, is(false));
        assertThat(resultB, instanceOf(Boolean.class));
        resultB = call.invoke(Boolean.class, "yes");
        assertThat(resultB, is(false));
        assertThat(resultB, instanceOf(Boolean.class));

        Character resultC = call.invoke(Character.class, 'S');
        assertThat(resultC, is('S'));
        assertThat(resultC, instanceOf(Character.class));

        String result1 = call.invoke(String.class, "Hi");
        assertThat(result1.toString(), is("Hi"));
        assertThat(result1, instanceOf(String.class));
        
        Double result2 = call.invoke(Double.class, "0.2");
        assertThat(result2, is(0.2));
        assertThat(result2, instanceOf(Double.class));
        result2 = (Double) call.invoke(Double.class, 0.25D);
        assertThat(result2, is(0.25));
        assertThat(result2, instanceOf(Double.class));
        
        Float result3 = call.invoke(Float.class, "0.3");
        assertThat(result3, is(0.3F));
        assertThat(result3, instanceOf(Float.class));
        
        Long result4 = call.invoke(Long.class, "4");
        assertThat(result4, is(4L));
        assertThat(result4, instanceOf(Long.class));
        result4 = call.invoke(Long.class, 4L);
        assertThat(result4, is(4L));
        assertThat(result4, instanceOf(Long.class));
        
        Integer result5 = call.invoke(Integer.class, "5");
        assertThat(result5, is(5));
        assertThat(result5, instanceOf(Integer.class));
        result5 =  call.invoke(Integer.class, "55");
        assertThat(result5, is(55));
        assertThat(result5, instanceOf(Integer.class));
        
        Short result6 = call.invoke(Short.class, "6");
        assertThat(result6, is((short) 6));
        assertThat(result6, instanceOf(Short.class));
        
        BigDecimal result7 = call.invoke(BigDecimal.class, "7.567864");
        assertThat(result7, is(new BigDecimal("7.567864")));
        assertThat(result7, instanceOf(BigDecimal.class));

        BigInteger result8 = call.invoke(BigInteger.class, "8");
        assertThat(result8, is(new BigInteger("8")));
        assertThat(result8, instanceOf(BigInteger.class));
    }
    
    @Test
    public void whenBasicInvokeToBuildEnums()
    {
        Invokable call = new BasicInvoke(new HandlerException());
        
        
        FooColor red = call.invoke(FooColor.class, "RED");
        FooColor blue = call.invoke(FooColor.class, "BLUE");
        FooColor white = call.invoke(FooColor.class, "WHITE");
        FooColor black = call.invoke(FooColor.class, "BLACK");
        
        assertThat(red, is(FooColor.RED));
        assertThat(blue, is(FooColor.BLUE));
        assertThat(white, is(FooColor.WHITE));
        assertThat(black, is(FooColor.BLACK));

    
    }

}
