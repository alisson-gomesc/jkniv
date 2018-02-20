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

/**
 * This class represent text found at NodeValue from tag at XML document. Sample
 * 
 * <pre>
 *     &lt;update id="updateAuthor2" type="NATIVE"&gt;
 *       update Author
 *       &lt;set&gt;
 *         &lt;if test="username != null"&gt;username = #{username},&lt;/if&gt;
 *         &lt;if test="password != null"&gt;password = #{password},&lt;/if&gt;
 *         &lt;if test="email != null"&gt;email = #{email},&lt;/if&gt;
 *         &lt;if test="bio != null"&gt;bio = #{bio},&lt;/if&gt;
 *       &lt;/set&gt;
 *       where id=#{id}
 *   &lt;/update&gt;
 * </pre>
 * 
 * Here we have two StaticText objects:
 * 
 * <pre>
 * update Author
 * </pre>
 * 
 * and
 * 
 * <pre>
 * where id=#{id}
 * </pre>
 * 
 * @author Alisson Gomes
 */
public class StaticText implements ITextTag
{
    private String text;
    
    /**
     * build static text object.
     * 
     * @param text
     *            static
     */
    public StaticText(String text)
    {
        this.text = text;
    }
    
    /**
     * Evaluate if attribute test is true.
     * 
     * @return always true, this is static text.
     */
    public boolean eval(Object rootObjects)
    {
        return true;
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
     * @return always false is returned, because this object save static text.
     */
    public boolean isDynamic()
    {
        return false;
    }
    
    @Override
    public List<? extends ITextTag> getTags()
    {
        return Collections.emptyList();
    }
    
}
