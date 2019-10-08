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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represent the <code>where</code> sentence from SQL.
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 */
public class WhereTag implements ITextTag
{
    public static final String TAG_NAME = "where";
    private static final Pattern patternAND =Pattern.compile("^(AND|^AND\\s*\\()", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static final Pattern patternOR =Pattern.compile("^(OR|^OR\\s*\\()", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    
    private List<ITextTag>     listIfTag;
    //private StringBuilder      text;
    
    /**
     * Build a new <code>where</code> tag.
     * 
     * @param listTestTag
     *            list of texts from set tag, dynamic or static texts.
     */
    public WhereTag(List<ITextTag> listTestTag)
    {
        //this.text = new StringBuilder();
        this.listIfTag = listTestTag;
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
        //return text.toString();
    }
    
    @Override
    public String getText(Object rootObjects)
    {
        return getConditionalText(rootObjects);
    }

    private String getConditionalText(Object rootObjects)
    {
        StringBuilder      text = new StringBuilder();
        for (int i = 0; i < listIfTag.size(); i++)
        {
            ITextTag tag = listIfTag.get(i);
            if (tag.eval(rootObjects))
            {
                String clause = tag.getText();
                if (tag.isDynamicGroup())
                    clause = tag.getText(rootObjects);
                if (text.indexOf("where") >= 0)
                {
                    text.append(" " + clause);
                }
                else
                {
                    text.append("where ");
                    Matcher matcherAnd = patternAND.matcher(clause);
                    Matcher matcherOr = patternOR.matcher(clause);
                    if (matcherAnd.find())
                        text.append(clause.substring(0, matcherAnd.start())).append(clause.substring(matcherAnd.end(), clause.length()).trim()) ;
                    else if (matcherOr.find())
                        text.append(clause.substring(0, matcherOr.start())).append(clause.substring(matcherOr.end(), clause.length()).trim()) ;
                    else
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
