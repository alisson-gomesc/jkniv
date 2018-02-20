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
package net.sf.jkniv.sqlegance.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Execute parsing at parameters values from XML files, extracting your values.
 * 
 * @author Alisson Gomes
 * @since 0.0.2
 * @deprecated use the ParamParse interface
 */
public final class ParameterParser
{
    // find the pattern 'a-z..0..9()*'
    private static final String  REGEX_SINGLE_QUOTE    = "|'[^']*')+";       //"\\?";
    
    // ":in:[\w\.?]"
    private static final String  REGEX_IN              = ":in:[\\w\\.?]+";  //FIXED bug :a.property.value -> :a
    
    // find the pattern #{id}
    private static final String  REGEX_HASH_MARK     = "(" + "#\\{[\\w\\.?]+\\}" + // #{id}
                                                         "|" + REGEX_IN + 
                                                         REGEX_SINGLE_QUOTE;
    
    // find the pattern :id
    private static final String  REGEX_COLON_MARK  = "(" + ":[\\w\\.?]+" +      //FIXED bug :a.property.value -> :a
                                                         "|" + REGEX_IN + 
                                                         REGEX_SINGLE_QUOTE;
    
    //":[\\w]+";// (:[\w]+|'[^']*')+  FIXED nao pode estar dentro de aspas 'YYYY-MM-DD HH24:MI:SS'
    // find the pattern ?
    private static final String  REGEX_QUESTION_MARK = "(" + "[\\?]+" + 
                                                         "|" + REGEX_IN + 
                                                         REGEX_SINGLE_QUOTE;              //"\\?";
    
    private static final Pattern PATTERN_HASH          = Pattern.compile(REGEX_HASH_MARK,       Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_TWO_DOTS      = Pattern.compile(REGEX_COLON_MARK,      Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_QUESTION      = Pattern.compile(REGEX_QUESTION_MARK,   Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_IN            = Pattern.compile(REGEX_IN,              Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    //private static final Pattern REPLACE_IN_PATTERN = Pattern.compile("\\:\\?in\\:(\\w+|\\$\\{[^\\}]+\\}|\\$simple\\{[^\\}]+\\})", Pattern.MULTILINE);
    
    private ParameterParser()
    {
        // hidden constructor form utility classes
    }
    
    /**
     * This is a null-safe method that extract the parameters from a sentence.
     * 
     * @param sentence
     *            The SQL sentence (is null-safe).
     * @return Return an array of parameter names find at sentence. The array is
     *         zero length if cannot find parameters or sentence is null.
     */
    public static String[] extract(final String sentence)
    {
        if (sentence == null)
            return new String[0];
        List<String> params = new ArrayList<String>();
        
        List<String> paramsHash = extractHash(sentence);
        List<String> paramsTwoDots= extractTwoDots(sentence);
        List<String> paramsQuestion = extractQuestion(sentence);
        List<String> paramsIN = extractInClause(sentence);

        int sizeHash = paramsHash.size()-paramsIN.size();
        int sizeTwoDots= paramsTwoDots.size()-paramsIN.size();
        int sizeQuestion = paramsQuestion.size()-paramsIN.size();
        int total = sizeHash+sizeQuestion+sizeTwoDots;
        if ( (total > sizeHash && sizeHash > 0) || 
             (total > sizeQuestion && sizeQuestion > 0) || 
             (total > sizeTwoDots && sizeTwoDots > 0))
            throw new RuntimeException(
                    "Cannot mix parameters placehold '#{param}', ':param' and '?' symbol, use just one don't mix. The sentence [" + sentence + "] is mixed!");

        if (sizeTwoDots > 0)
            params = paramsTwoDots;
        else if (sizeHash > 0)
            params = paramsHash;
        else if (sizeQuestion > 0)
            params = paramsQuestion;
        else
            params = paramsTwoDots;
        return params.toArray(new String[0]);
    }
    
    /*
            if (findPlaceHoldMarker && !params.isEmpty())
            {
            }
    
     */
    private static List<String> extractHash(final String sentence)
    {
        List<String> params = new ArrayList<String>();
        Matcher matcherHash = PATTERN_HASH.matcher(sentence);
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
    
    private static List<String> extractTwoDots(final String sentence)
    {
        List<String> params = new ArrayList<String>();
        Matcher matcherTwoDots = PATTERN_TWO_DOTS.matcher(sentence);
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
    
    private static List<String> extractQuestion(final String sentence)
    {
        List<String> params = new ArrayList<String>();
        Matcher matcherQuestion = PATTERN_QUESTION.matcher(sentence);
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

    private static List<String> extractInClause(final String sentence)
    {
        List<String> params = new ArrayList<String>();
        Matcher matcherIN = PATTERN_IN.matcher(sentence);
        int i = 0;
        while (matcherIN.find())
            params.add(i++, sentence.subSequence(matcherIN.start() + 1, matcherIN.end()).toString());
        return params;
    }
    
    
    public static String replaceTwoDotsForQuestionMark(final String sentence)
    {
        StringBuffer sb = new StringBuffer(sentence);
        Matcher matcherTwoDots = PATTERN_TWO_DOTS.matcher(sentence);
        int startIndex = 0;
        int endIndex = 0;
        while (matcherTwoDots.find())
        {
            if (matcherTwoDots.group().startsWith("'"))
                continue;
            startIndex = matcherTwoDots.start();
            endIndex = matcherTwoDots.end();
            sb.replace(startIndex, endIndex, padspace(endIndex - startIndex));
        }
        return sb.toString();
    }
    
    public static String replaceHashForQuestionMark(final String sentence)
    {
        StringBuffer sb = new StringBuffer(sentence);
        Matcher matcherHash = PATTERN_HASH.matcher(sentence);
        int startIndex = 0;
        int endIndex = 0;
        while (matcherHash.find())
        {
            if (matcherHash.group().startsWith("'"))
                continue;
            startIndex = matcherHash.start();
            endIndex = matcherHash.end();
            sb.replace(startIndex, endIndex, padspace(endIndex - startIndex));
        }
        return sb.toString();
    }
    
    private static String padspace(int size)
    {
        StringBuffer s = new StringBuffer("?");
        for (int i = 1; i < size; i++)
            s.append(" ");
        return s.toString();
    }
    
    /**
     * Check if query was wrote using #{param} parameters
     * @param sentence the query
     * @return return true is query has question mark, false otherwise
     */
    public static boolean hasHash(final String sentence)
    {
        Matcher matcherHash = PATTERN_HASH.matcher(sentence);
        boolean find = false;
        while (matcherHash.find())
        {
            if (matcherHash.group().startsWith("'"))
                continue;
            find = true;
            break;
        }
        return find;
    }
    
    /**
     * Check if query was wrote using :param parameters
     * @param sentence the query
     * @return return true is query has question mark, false otherwise
     */
    public static boolean hasTwoDots(final String sentence)
    {
        Matcher matcherTwoDots = PATTERN_TWO_DOTS.matcher(sentence);
        boolean find = false;
        while (matcherTwoDots.find())
        {
            if (matcherTwoDots.group().startsWith("'"))
                continue;
            find = true;
            break;
        }
        return find;
    }
    
    /**
     * Check if query was wrote using ? parameters
     * @param sentence the query
     * @return return true is query has question mark, false otherwise
     */
    public static boolean hasQuestion(final String sentence)
    {
        Matcher matcherQuestion = PATTERN_QUESTION.matcher(sentence);
        boolean find = false;
        while (matcherQuestion.find())
        {
            if (matcherQuestion.group().startsWith("'"))
                continue;
            find = true;
            break;
        }
        return find;
    }
    
}
