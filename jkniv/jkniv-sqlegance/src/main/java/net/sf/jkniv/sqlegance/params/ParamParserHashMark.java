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

public class ParamParserHashMark extends AbstractParamParser
{
    public ParamParserHashMark()
    {
        super(REGEX_HASH_MARK);
    }

    @Override
    public String[] find(String query)
    {
        return parserHash(query).toArray(new String[0]);
    }

    @Override
    public ParamMarkType getType()
    {
        return ParamMarkType.HASH;
    }
    
    private List<String> parserHash(final String sentence)
    {
        List<String> params = new ArrayList<String>();
        Matcher matcherHash = PATTERN_PARAMS.matcher(sentence);
        int i = 0;
        while (matcherHash.find())
        {
            if (matcherHash.group().startsWith("'"))
            {
                continue;
            }
            else if (matcherHash.group().startsWith(":in:"))
            {
                // :in:names -> in:names (subSequence)
                params.add(i++, sentence.subSequence(matcherHash.start() + 1, matcherHash.end()).toString());
                continue;
            }
            //System.out.printf("group[%s] [%s]\n", matcherHash.group(), sentence.subSequence(matcherHash.start() + 1, matcherHash.end()).toString());
            params.add(i++, sentence.subSequence(matcherHash.start() + 2, matcherHash.end() - 1).toString());
        }
        return params;
    }

}
