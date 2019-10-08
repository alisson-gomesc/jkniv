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
 * Represent the <code>set</code> sentence from <code>update</code> SQL.
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 */
public class SetTag implements ITextTag
{
    public static final String TAG_NAME = "set";
    private List<? extends ITextTag>     listIfTag;
    //private StringBuilder      text;
    
    /**
     * Build a new <code>set</code> tag.
     * 
     * @param listIfTag
     *            list of texts from set tag, dynamic or static texts.
     */
    public SetTag(List<? extends ITextTag> listIfTag)
    {
        //text = new StringBuilder();
        this.listIfTag = listIfTag;
    }
    
    /**
     * Evaluate if attribute test is true. The dynamic text is building while
     * evaluate attribute test, for each true sentence the text is appended.
     * 
     * @return true if some test case its true, false otherwise.
     */
    public boolean eval(Object rootObjects)
    {
        boolean evaluation = false;
        for (int i = 0; i < listIfTag.size(); i++)
        {
            ITextTag tag = listIfTag.get(i);
            if (tag.eval(rootObjects))
            {
                evaluation = true;
                break;
            }
        }
        return evaluation;
    }
    
    /**
     * Retrieve the dynamic text from XML element.
     * 
     * @return text from XML element.
     */
    public String getText()
    {
        throw new IllegalStateException("Conditional tag group cannot getText directly, invoke getText(Object rootObjects)");
    }
    
    @Override
    public String getText(Object rootObjects)
    {
        return getConditionalText(rootObjects);
    }
    
    private String getConditionalText(Object rootObjects)
    {
        StringBuilder text = new StringBuilder();
        //boolean evaluation = false;
        for (int i = 0; i < listIfTag.size(); i++)
        {
            ITextTag tag = listIfTag.get(i);
            if (tag.eval(rootObjects))
            {
                //evaluation = true;
                String clause = tag.getText().trim();
                if (clause.endsWith(","))
                    clause = clause.substring(0, clause.length() - 1);
                
                if (text.indexOf("set") >= 0)
                {
                    text.append(", " + clause);
                }
                else
                {
                    text.append("set ");
                    text.append(clause);
                }
            }
        }
        return text.toString();
    }


    
    /**
     * Indicate if text is dynamic or static.
     * 
     * @return always true is returned, because this object save dynamic text.
     */
    @Override
    public boolean isDynamic()
    {
        return true;
    }
    
    @Override
    public boolean isDynamicGroup()
    {
        return true;
    }

    
    @Override
    public List<? extends ITextTag> getTags()
    {
        return this.listIfTag;
    }
}
