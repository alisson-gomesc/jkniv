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

public class ChooseTag implements ITextTag
{
    public static final String TAG_NAME = "choose";
    private List<WhenTag>     listWhenTag;
    //private String text;
    
    /**
     * Build a new <code>choose</code> tag.
     * 
     * @param listWhenTag
     *            list of texts from <code>when</code> tags
     */
    public ChooseTag(List<WhenTag> listWhenTag)
    {
        this.listWhenTag = listWhenTag;
    }
    
    /**
     * Evaluate if attribute test is true from <code>when</code> list tags, the first option is selected.
     * 
     * @return true if some test case its true, false otherwise. If have an <code>otherwise</code> always true is returned.
     */
    public boolean eval(Object rootObjects)
    {
        boolean evaluation = false;
        for (int i = 0; i < listWhenTag.size(); i++)
        {
            ITextTag tag = listWhenTag.get(i);
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
        return "";
        //throw new IllegalStateException("Conditional tag group cannot getText directly, invoke getText(Object rootObjects)");
    	//return this.text;
    }

    @Override
    public String getText(Object rootObjects)
    {
        return getConditionalText(rootObjects);
    }

    public String getConditionalText(Object rootObjects)
    {
        String text = "";
        for (int i = 0; i < listWhenTag.size(); i++)
        {
            ITextTag tag = listWhenTag.get(i);
            if (tag.eval(rootObjects))
            {
                text = tag.getText();
                break;
            }
        }
        return text;
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
        return this.listWhenTag;
    }

}
