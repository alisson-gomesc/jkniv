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
package net.sf.jkniv.sqlegance.params;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class ParamParserColonMark extends AbstractParamParser
{
    public ParamParserColonMark()
    {
        super(REGEX_COLON_MARK);
    }
    
    @Override
    public String[] find(String query)
    {
        return parserColon(query).toArray(new String[0]);
    }
    
    @Override
    public ParamMarkType getType()
    {
        return ParamMarkType.COLON;
    }
    
    private List<String> parserColon(final String sentence)
    {
        List<String> params = new ArrayList<String>();
        Matcher matcherTwoDots = PATTERN_PARAMS.matcher(sentence);
        int i = 0;
        while (matcherTwoDots.find())
        {
            if (matcherTwoDots.group().startsWith("'"))
            {
                continue;
            }
            else if (matcherTwoDots.group().startsWith(":in:"))
            {
                params.add(i++, sentence.subSequence(matcherTwoDots.start() + 1, matcherTwoDots.end()).toString());
                continue;
            }
            //System.out.printf("group[%s] [%s]\n", matcherTwoDots.group(), sentence.subSequence(matcherTwoDots.start() + 1, matcherTwoDots.end()).toString());
            params.add(i++, sentence.subSequence(matcherTwoDots.start() + 1, matcherTwoDots.end()).toString());
        }
        
        return params;
    }
   
    private boolean isArrayWithElements(Object o)
    {
        boolean answer = false;
        
        if (o != null)
        {
            
        }
        
        return answer;
    }
}
