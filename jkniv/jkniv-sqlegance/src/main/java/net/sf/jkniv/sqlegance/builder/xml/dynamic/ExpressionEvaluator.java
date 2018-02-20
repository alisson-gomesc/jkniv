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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ognl.Ognl;
import ognl.OgnlException;

/**
 * Evaluate with ONGL expression.
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 */
final class ExpressionEvaluator
{
    private ExpressionEvaluator()
    {
        // hidden constructor form utility classes
    }
    
    /**
     * Evaluates the given OGNL expression tree to extract a value from the
     * given root object.
     * 
     * @param expression
     *            the OGNL expression tree to evaluate, as returned by
     *            parseExpression()
     * @param parameterObject
     *            the root object for the OGNL expression
     * @return the result of evaluating the expression true or false.
     * @exception MalformedExpression
     *                if the expression called a method which failed or if the
     *                expression referred to a nonexistent property
     */
    static boolean eval(Object expression, Object parameterObject)
    {
        boolean result = false;
        if (expression != null)
        {
            try
            {
                Object value = null;
                if (parameterObject != null)
                    value = Ognl.getValue(expression, parameterObject);
                else
                    value = Ognl.getValue(expression, Collections.emptyMap());
                
                if (value instanceof Boolean)
                    result = (Boolean) value;
                else if (value instanceof Number)
                    result = !new BigDecimal(String.valueOf(value)).equals(BigDecimal.ZERO);
                // else
                // result = value != null;
            }
            catch (OgnlException e)
            {
                throw new MalformedExpression("Error evaluating expression '" + expression + "'. Cause: " + e, e);
            }
        }
        return result;
    }
}
