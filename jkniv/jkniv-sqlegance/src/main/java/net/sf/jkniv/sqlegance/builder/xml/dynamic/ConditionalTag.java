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
package net.sf.jkniv.sqlegance.builder.xml.dynamic;

import java.util.Collections;
import java.util.List;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * Represent the <code>if</code> tag from XML file to put dynamic SQLs.
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 */
public abstract class ConditionalTag implements ITextTag
{
    public static final String ATTRIBUTE_TEST = "test";
    private Object             ognlExpression;
    private String             text;
    
    /**
     * Parses the given OGNL expression that can be used by Ognl static methods.
     * 
     * @param ognlExpression
     *            the OGNL expression to be parsed
     * @param text content of expression to be evaluated
     * @exception MalformedExpression
     *                if the expression is malformed or if there is a
     *                pathological environmental problem.
     */
    public ConditionalTag(String ognlExpression, String text)
    {
        this.text = text;
        if (text == null || "".equals(text))
            throw new MalformedExpression(
                    "Error parsing text from tag <if test=\"...\">text</if>, cannot be null or empty.");
        
        try
        {
            this.ognlExpression = Ognl.parseExpression(ognlExpression);
        }
        catch (OgnlException e)
        {
            throw new MalformedExpression("Error parsing expression '" + ognlExpression + "'. Cause: " + e.getMessage(),
                    e);
        }
    }
    
    /**
     * Evaluate the expression from test attribute is true or false.
     * 
     * @return true if expression is true, false otherwise.
     */
    public boolean eval(Object rootObjects)
    {
        return ExpressionEvaluator.eval(ognlExpression, rootObjects);
    }
    
    /**
     * Retrieve the text from XML element.
     * 
     * @return text from XML element.
     */
    public String getText()
    {
        return this.text;
    }
    
    /**
     * Indicate if text is dynamic or static.
     * 
     * @return always true is returned, because this object save dynamic text.
     */
    public boolean isDynamic()
    {
        return true;
    }

    @Override
    public List<? extends ITextTag> getTags()
    {
        return Collections.emptyList();
    }
    
    
    @Override
    public String toString()
    {
        return "text=" + this.text;
    }
}
