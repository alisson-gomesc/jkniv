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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.reflect.BasicType;

/**
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public abstract class AbstractParamParser implements ParamParser
{
    protected final Logger         log                 = LoggerFactory.getLogger(getClass());
    // find the pattern 'a-z..0..9()*'
    protected static final String  REGEX_SINGLE_QUOTE  = "|'[^']*')+"; //"\\?";
    
    // ":in:[\w\.?]"
    protected static final String  REGEX_IN            = ":in:[\\w\\.?]+"; //FIXED bug :a.property.value -> :a
    
    // find the pattern #{id}
    protected static final String  REGEX_HASH_MARK     = "(" + "#\\{[\\w\\.?]+\\}" +  // #{id}
                                                            "|" + REGEX_IN + REGEX_SINGLE_QUOTE;
    
    // find the pattern :id
    protected static final String  REGEX_COLON_MARK    = "(" + ":[\\w\\.?]+" + //FIXED bug :a.property.value -> :a
                                                             "|" + REGEX_IN + REGEX_SINGLE_QUOTE;
    
    //":[\\w]+";// (:[\w]+|'[^']*')+  FIXED nao pode estar dentro de aspas 'YYYY-MM-DD HH24:MI:SS'
    // find the pattern ?
    protected static final String  REGEX_QUESTION_MARK = "(" + "[\\?]+" + "|" + REGEX_IN + REGEX_SINGLE_QUOTE;    //"\\?";
    
    //private static final Pattern PATTERN_HASH          = Pattern.compile(REGEX_HASH_SYMBOL,     Pattern.CASE_INSENSITIVE);
    //private static final Pattern PATTERN_TWO_DOTS      = Pattern.compile(REGEX_TWODOTS_SYMBOL,  Pattern.CASE_INSENSITIVE);
    //private static final Pattern PATTERN_QUESTION      = Pattern.compile(REGEX_QUESTION_SYMBOL, Pattern.CASE_INSENSITIVE);
    protected final Pattern        PATTERN_PARAMS;                                                                //= Pattern.compile(REGEX_QUESTION_SYMBOL, Pattern.CASE_INSENSITIVE);
    protected static final Pattern PATTERN_IN          = Pattern.compile(REGEX_IN, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static final BasicType basicType           = BasicType.getInstance();
    
    public AbstractParamParser(String regex)
    {
        this.PATTERN_PARAMS = Pattern.compile(regex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    }
    
    @Override
    public String replaceForQuestionMark(String query)
    {
        return replaceForQuestionMark(query, Collections.emptyList());
    }

    @Override
    public String replaceForQuestionMarkWithNumber(String query, Object params)
    {
        StringBuffer sb = new StringBuffer(query);
        Matcher matcherTwoDots = PATTERN_PARAMS.matcher(query);
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
                if (paramsAsArray != null && paramsAsArray.length > 0)
                {
                    StringBuilder tmp = new StringBuilder();
                    for(int i=0; i<paramsAsArray.length; i++)
                        tmp.append( i>0? "," : "")
                           .append("?"+index++);
                    
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

    @Override
    public String replaceForQuestionMark(String query, Object params)
    {
        StringBuffer sb = new StringBuffer(query);
        Matcher matcherTwoDots = PATTERN_PARAMS.matcher(query);
        Map<String, String> mapForINClauseParams = new HashMap<String, String>();
        int startIndex = 0;
        int endIndex = 0;
        while (matcherTwoDots.find())
        {
            String match = matcherTwoDots.group();
            if (match.startsWith("'"))
                continue;
            if (match.startsWith(":in:"))
            {
                String paramName = match.substring(4, match.length());
                Object[] paramsAsArray = getParamsClauseIN(params, paramName);
                if (paramsAsArray!=null && paramsAsArray.length > 0)
                {
                    StringBuilder tmp = new StringBuilder();
                    for(int i=0; i<paramsAsArray.length; i++)
                        tmp.append( i>0? "," : "")
                           .append("?");
                    
                    mapForINClauseParams.put(match, tmp.toString());
                }
            }
            else
            {
                startIndex = matcherTwoDots.start();
                endIndex = matcherTwoDots.end();
                sb.replace(startIndex, endIndex, padspace(endIndex - startIndex, -1));
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

    
    protected String padspace(int size, int index)
    {
        StringBuffer s = new StringBuffer("?" +(index < 0 ? "" : index));
        int newSize = (index < 0 ? size : size - String.valueOf(index).length());// discount the length string from index
        for (int i = 1; i < newSize; i++)
            s.append(" ");
        return s.toString();
    }
    
    protected Object[] getParamsClauseIN(Object params, String name)
    {
        Object[] paramsClauseIN = getParamsAsArray(params);
        if (paramsClauseIN == null)
        {
            try
            {
                Object value = PropertyUtils.getProperty(params, name);
                if (value != null && (value instanceof String || basicType.isNumberType(value.getClass())))
                    paramsClauseIN = new Object[]{ value };
                else
                    paramsClauseIN = getParamsAsArray(value);
            }
            catch (Exception e)//IllegalAccessException, InvocationTargetException, NoSuchMethodException
            {
                log.warn("Cannot read property [{}] for object [{}]. cause={}", name,
                        (params == null ? "null" : params.toString()), e.getMessage());
            }
        }
        return paramsClauseIN;
    }
    
    private Object[] getParamsAsArray(Object params)
    {
        Object[] array = null;
        if (params instanceof Collection)
            array = ((Collection<?>) params).toArray();
        else if (params != null && params.getClass().isArray())
        {
            array = (Object[]) params;
        }
        return array;
    }
    
}
