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
import java.util.regex.Pattern;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class ParamParserQuestionMark extends AbstractParamParser
{
    private static final Pattern PATTERN_PARAMS = Pattern.compile(REGEX_QUESTION_MARK,
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    
    private static ParamParser INSTANCE = new ParamParserQuestionMark();
    
    private ParamParserQuestionMark()
    {
        //super(REGEX_QUESTION_MARK);
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
        return parserQuestion(query).toArray(new String[0]);
    }

    @Override
    public ParamMarkType getType()
    {
        return ParamMarkType.QUESTION;
    }
    
    private List<String> parserQuestion(final String sentence)
    {
        List<String> params = new ArrayList<String>();
        Matcher matcherQuestion = PATTERN_PARAMS.matcher(sentence);
        int i = 0;
        while (matcherQuestion.find())
        {
            if (matcherQuestion.group().startsWith("'"))
            {
                continue;
            }
            else if (matcherQuestion.group().startsWith(":in:"))
            {
                params.add(i++, sentence.subSequence(matcherQuestion.start() + 1, matcherQuestion.end()).toString());
                continue;
            }
            //System.out.printf("group[%s] [%s]\n", matcherQuestion.group(), sentence.subSequence(matcherQuestion.start() + 1, matcherQuestion.end()).toString());
            params.add(i++, sentence.subSequence(matcherQuestion.start(), matcherQuestion.end()).toString());
        }
        return params;
    }
}
