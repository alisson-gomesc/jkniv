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

/**
 * No make the parser from sql parameter, used at tag without support like 
 * (<code>IncludeTag</code>, <code>ProcedureTag</code>, etc)
 * 
 * @author alisson gomes
 *
 */
public class ParamParserNoMark implements ParamParser
{
    private static final ParamParser emptyParser = new ParamParserNoMark();

    private ParamParserNoMark()
    {
    }
    
    /**
     * Empty parser from sql parameter
     * @return implementation doesn't make the parser
     */
    public static ParamParser emptyParser() {
        return emptyParser;
    }
    
    @Override
    public String[] find(String query)
    {
        return new String[0];
    }

    @Override
    public String replaceForQuestionMark(String query, Object params)
    {
        return query;
    }
    
    @Override
    public String replaceForQuestionMarkWithNumber(String query, Object params)
    {
        return query;
    }

    @Override
    public String replaceForQuestionMark(String query)
    {
        return query;
    }

    @Override
    public ParamMarkType getType()
    {
        return ParamMarkType.NO_MARK;
    }


}
