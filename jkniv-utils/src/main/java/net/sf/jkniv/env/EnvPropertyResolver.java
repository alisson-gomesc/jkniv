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
package net.sf.jkniv.env;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolve environment variables:
 * 
 * <pre>
 * ${CONSUL_HOST=test.catchfy.me}:${CONSUL_PORT=8500} resolve to:  "test.catchfy.me:8500"
 * </pre>
 * 
 *  <ul>
 *   <li>first Operational System </li>
 *   <li>if null, from JVM parameters</li>
 *   <li>if null, default value</li>
 *   <li>if null, the variable name</li>
 *  </ul>
 */
public class EnvPropertyResolver
{
    
    /**
     * Prefix for placeholder in properties.
     */
    public static final String   PREFIX                   = "${";
    
    /**
     * Suffix for placeholder in properties.
     */
    public static final String   SUFFIX                   = "}";
    
    private static final char    COLON                    = '=';
    
    private static final Pattern ESCAPE_SEQUENCE          = Pattern.compile("(.+)?=`([^`]+?)`");
    
    private static final Pattern ENVIRONMENT_VAR_SEQUENCE = Pattern.compile("^[\\p{Lu}_{0-9}]+");
    
    private final String         prefix;
    
    public EnvPropertyResolver()
    {
        this.prefix = PREFIX;
    }
    
    public String getPrefix()
    {
        return this.prefix;
    }
    
    public String getValue(String str)
    {
        List<Segment> segments = buildSegments(str);
        StringBuilder value = new StringBuilder();
        for (Segment segment : segments)
        {
            value.append(segment.getValue());
        }
        return value.toString();
    }
    
    /**
     * Split a placeholder value into logic segments.
     *
     * @param str The placeholder
     * @return The list of segments
     */
    private List<Segment> buildSegments(String str)
    {
        List<Segment> segments = new ArrayList<Segment>();
        String value = str;
        int i = value.indexOf(PREFIX);
        while (i > -1)
        {
            //the text before the prefix
            if (i > 0)
            {
                String rawSegment = value.substring(0, i);
                segments.add(new RawSegment(rawSegment));
            }
            //everything after the prefix
            value = value.substring(i + PREFIX.length());
            int suffixIdx = value.indexOf(SUFFIX);
            if (suffixIdx > -1)
            {
                String expr = value.substring(0, suffixIdx).trim();
                segments.add(new PlaceholderSegment(expr));
                if (value.length() > suffixIdx)
                {
                    value = value.substring(suffixIdx + SUFFIX.length());
                }
            }
            else
            {
                throw new RuntimeException("Incomplete placeholder definitions detected: " + str);
            }
            i = value.indexOf(PREFIX);
        }
        if (value.length() > 0)
        {
            segments.add(new RawSegment(value));
        }
        return segments;
    }
    
    /**
     * Resolves a single expression.
     *
     * @param context The context of the expression
     * @param expression The expression
     * @return The resolved and converted expression
     */
    //@Nullable
    protected String resolveExpression(String context, String expression)
    {
        if (ENVIRONMENT_VAR_SEQUENCE.matcher(expression).matches())
        {
            String envVar = System.getenv(expression);// Operation System Env
            
            if (envVar == null)
                envVar = System.getProperty(expression);// Jvm Env
                
            return envVar;
        }
        return expression;
    }

    /**
     * A segment of placeholder resolution.
     */
    public interface Segment
    {
        /**
         * Returns the value of a given segment converted to
         * the provided type.
         * @param <T> generic return type
         * @return The converted value
         * @throws RuntimeException If any error occurs
         */
        <T> T getValue() throws RuntimeException;
    }
    
    /**
     * A segment that represents static text.
     */
    public class RawSegment implements Segment
    {
        private final String text;
        
        /**
         * Default constructor.
         *
         * @param text The static text
         */
        RawSegment(String text)
        {
            this.text = text;
        }
        
        public String getValue()
        {
            return text;
        }
        
        @Override
        public String toString()
        {
            return "RawSegment [text=" + text + "]";
        }
    }
    
    /**
     * A segment that represents one or more expressions
     * that should be searched for in the environment.
     */
    public class PlaceholderSegment implements Segment
    {
        private final String       placeholder;
        private final List<String> expressions = new ArrayList<String>();
        private String             defaultValue;
        
        /**
         * Default constructor.
         *
         * @param placeholder The placeholder value without
         *                    any prefix or suffix
         */
        PlaceholderSegment(String placeholder)
        {
            this.placeholder = placeholder;
            findExpressions(placeholder);
        }
        
        /**
         * @return The list of expressions that may be looked
         * up in the environment
         */
        public List<String> getExpressions()
        {
            return Collections.unmodifiableList(expressions);
        }
        
        public String getValue() throws RuntimeException
        {
            for (String expression : expressions)
            {
                String value = resolveExpression(placeholder, expression);
                if (value != null)
                {
                    return value;
                }
            }
            if (defaultValue != null)
            {
                return defaultValue;
            }
            else
            {
                throw new RuntimeException("Could not resolve placeholder ${" + placeholder + "}");
            }
        }
        
        private void findExpressions(String placeholder)
        {
            String defaultValue = null;
            String expression;
            Matcher matcher = ESCAPE_SEQUENCE.matcher(placeholder);
            
            boolean escaped = false;
            if (matcher.find())
            {
                defaultValue = matcher.group(2);
                expression = matcher.group(1);
                escaped = true;
            }
            else
            {
                int j = placeholder.indexOf(COLON);
                if (j > -1)
                {
                    defaultValue = placeholder.substring(j + 1);
                    expression = placeholder.substring(0, j);
                }
                else
                {
                    expression = placeholder;
                }
            }
            
            expressions.add(expression);
            
            if (defaultValue != null)
            {
                if (!escaped && (ESCAPE_SEQUENCE.matcher(defaultValue).find() || defaultValue.indexOf(COLON) > -1))
                {
                    findExpressions(defaultValue);
                }
                else
                {
                    this.defaultValue = defaultValue;
                }
            }
        }
        
        @Override
        public String toString()
        {
            return "EnvPropertyResolver [placeholder=" + placeholder + ", expressions=" + expressions + ", defaultValue="
                    + defaultValue + "]";
        }
    }
}
