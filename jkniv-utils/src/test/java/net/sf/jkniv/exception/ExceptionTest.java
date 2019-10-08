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
package net.sf.jkniv.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class ExceptionTest
{

    @Test
    public void testCatch()
    {
        try
        {
            throwException();
        }
        catch (Exception e)
        {
           Assert.assertTrue(true);// print to view line number from stacktrace
        }
    }
    
    @Test
    public void test()
    {

    }
    
    
    private void throwException() throws Exception
    {
        System.out.println("line 15");
        int a = 1;
        int b = 2;
        int c = 3;
        int r = a + b + c;
        System.out.println("a+b+c=" + r);
        Exception e = new Exception("Erro sum");
        throw e;
    }
    
    @Test
    public void whenExtractLineFromException()
    {
        Exception ex = new IllegalAccessException("Testing parse stack");
        System.out.println(parseException(ex));
    }
    
    private static String parseException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String line = null;
        Pattern pattern = Pattern.compile("\\tat\\s[\\w|.|\\(|:|\\)]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sw.toString());
        if (matcher.find())
            line = matcher.group();

        return line + ", msg=" + e.getMessage();
    }
}
