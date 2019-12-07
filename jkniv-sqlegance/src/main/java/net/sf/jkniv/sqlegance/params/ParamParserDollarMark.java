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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamParserDollarMark extends AbstractParamParser
{
    private static final Pattern PATTERN_PARAMS = Pattern.compile(REGEX_DOLLAR_MARK, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static final ParamParser INSTANCE = new ParamParserDollarMark();
    
    private ParamParserDollarMark()
    {
    }

    public static ParamParser getInstance()
    {
        return INSTANCE;
    }
    
    @Override
    Pattern getPatternParams()
    {
        return PATTERN_PARAMS;
    }

    @Override
    public String[] find(String query)
    {
        return parserDollar(query).toArray(new String[0]);
    }
    
    @Override
    public ParamMarkType getType()
    {
        return ParamMarkType.DOLLAR;
    }
    
    /**
     * Is {@code $}
     */
    @Override
    public String getPlaceholder()
    {
        return "$";
    }
    
    private List<String> parserDollar(final String sentence)
    {
        List<String> params = new ArrayList<String>();
        Matcher matcherDollar = PATTERN_PARAMS.matcher(sentence);
        int i = 0;
        while (matcherDollar.find())
        {
            if (matcherDollar.group().startsWith("'"))
            {
                continue;
            }
            else if (matcherDollar.group().startsWith(":in:"))
            {
                params.add(i++, sentence.subSequence(matcherDollar.start() + 1, matcherDollar.end()).toString());
                continue;
            }
            //System.out.printf("group[%s] [%s]\n", matcherTwoDots.group(), sentence.subSequence(matcherTwoDots.start() + 1, matcherTwoDots.end()).toString());
            params.add(i++, sentence.subSequence(matcherDollar.start() + 1, matcherDollar.end()).toString());
        }
        return params;
    }   
    
    
    @Override
    public String replaceForPlaceholder(String query, Object params)
    {
        return replaceForPlaceholderWithNumber(query, params);
    }
    
    @Override
    public String replaceForPlaceholderWithNumber(String query, Object params)
    {
        StringBuffer sb = new StringBuffer(query);
        Matcher matcherTwoDots = getPatternParams().matcher(query);
        Map<String, String> mapForINClauseParams = new HashMap<String, String>();
        int startIndex = 0;
        int endIndex = 0;
        int index = 1;
        while (matcherTwoDots.find())
        {
            String match = matcherTwoDots.group();
            if (match.startsWith("'"))
                continue;
            if (match.startsWith(":in:"))
            {
                String paramName = match.substring(4, match.length());
                Object[] paramsAsArray = getParamsClauseIN(params, paramName);
                if (paramsAsArray != null 
                        && paramsAsArray.length > 0 && !mapForINClauseParams.containsKey(match))
                {
                    StringBuilder tmp = new StringBuilder();
                    for(int i=0; i<paramsAsArray.length; i++)
                        tmp.append( i>0? "," : "")
                           .append("\\"+getPlaceholder()+index++);
                    
                    mapForINClauseParams.put(match, tmp.toString());
                }
            }
            else
            {
                startIndex = matcherTwoDots.start();
                endIndex = matcherTwoDots.end();
                sb.replace(startIndex, endIndex, padspace(endIndex - startIndex, index++));
            }
        }
        if (!mapForINClauseParams.isEmpty())
        {
            String newSql = sb.toString();
            for(String key : mapForINClauseParams.keySet())
                newSql = newSql.replaceAll(key, mapForINClauseParams.get(key));
            
            sb = new StringBuffer(newSql);
        }
        return sb.toString();
    }    

}
