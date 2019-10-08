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


public class WhenTag extends ConditionalTag
{
    public static final String TAG_NAME = "when";   

    /**
     * Parses the given OGNL expression that can be used by Ognl static methods.
     * 
     * @param ognlExpression
     *            the OGNL expression to be parsed
     * @param text to be evaluate by ognl expression
     * @exception MalformedExpression
     *                if the expression is malformed or if there is a
     *                pathological environmental problem.
     */
    public WhenTag(String ognlExpression, String text){
		super(ognlExpression, text);
	}
}
