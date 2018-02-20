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

import java.util.List;

/**
 * The text from node elements at XML file.
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 */
public interface ITextTag
{
    /**
     * Evaluate if attribute test is true or false with OGNL expression.
     * Something
     * 
     * @param rootObjects
     *            the root object for the OGNL expression.
     * @return true at expression is true, false otherwise.
     * @throws MalformedExpression
     *             if the expression is malformed.
     * @see ognl.Ognl
     */
    boolean eval(Object rootObjects);
    
    /**
     * Retrieve the text from XML element, can be dynamic or static.
     * 
     * @return the text (dynamic or static) from XML element
     */
    String getText();
    
    /**
     * Indicate if text is dynamic or static.
     * 
     * @return true returned if dynamic, false otherwise.
     */
    boolean isDynamic();
    
    /**
     * Collections from inner tags ({@code WhereTag}, {@code SetTag}...) 
     * @return inner tags, empty list if tag doesn't store collection.
     */
    List<? extends ITextTag> getTags();
}
