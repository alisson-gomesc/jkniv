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
package net.sf.jkniv.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatterLogger
{
    
    private static final Pattern pattern = Pattern.compile("\\{[w]*\\}");
    
    /**
     * Formatter a string from <code>org.slf4j.Logger</code> format to <code>String.format</code>
     * Example:
     * "The user {} cannot make login with password [{}]"
     * "The user %1$s cannot make login with password [%2$s]"
     * 
     * @param format SLF4J formatter message
     * @return Return a String with new formatter.
     */
    public String formatterSlf4j(String format)
    {
        StringBuilder sb = new StringBuilder();
        Matcher matcher = pattern.matcher(format);
        int index = 1;
        int initial = 0;
        List<Integer> groups = new ArrayList<Integer>();
        while (matcher.find())
        {
            groups.add(matcher.start());
            groups.add(matcher.end());
            //matcher.group()
            sb.append( format.substring(initial, matcher.start()));
            sb.append("%" + (index++) + "$s"); // string format "%1$s";
            initial = matcher.end();
        }
        if (initial > 0)
            sb.append( format.substring(initial, format.length()));
        else
            sb.append(format);
        return sb.toString();
    }
    
    /**
     * to String the objects <code>args</code> calling toString method, when
     * the object is instance of <code>java.lang.Exception</code> the <code>getMessage()</code>
     * is called.
     * @param args objects to get message as string
     * @return Return the string values from objects. <code>null</code> values are converted to <b>"null"</b> string.
     */
    public Object[] toString(Object... args)
    {
        Object[] newArray = new Object[0];
        if (args != null)
        {
            newArray = new Object[args.length];
            for (int i=0; i<args.length;i++)
            {
                if ( args[i] instanceof Exception)
                    newArray[i] = String.valueOf(((Exception)args[i]).getMessage());
                else
                    newArray[i] = String.valueOf(args[i]);
            }
        }
        return newArray;
    }
}
