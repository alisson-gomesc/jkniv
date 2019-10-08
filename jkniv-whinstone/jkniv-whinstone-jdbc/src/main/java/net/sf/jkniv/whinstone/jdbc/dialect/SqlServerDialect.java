/* 
 * JKNIV, whinstone one contract to access your database.
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
package net.sf.jkniv.whinstone.jdbc.dialect;

import java.sql.PreparedStatement;
import java.util.Locale;
import java.util.regex.Matcher;

import net.sf.jkniv.sqlegance.dialect.AnsiDialect;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureFactory;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;

/**
 * Dialect to SQLServer
 * 
 * <p>
 * Limit clause:
 *  <code>select TOP 2 name from author</code>
 * </p>
 * 
 * <ul>
 *  <li>Supports limits? true</li>
 *  <li>Supports limit off set? false</li>
 *  <li>Supports rownum? false</li>
 * </ul>
 *
 * @author Alisson Gomes 
 * @since 0.6.0
 */
public class SqlServerDialect extends AnsiDialect
{
    public SqlServerDialect()
    {
        super();
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.LIMIT, true));
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.LIMIT_OFF_SET, true));
        addFeature(SqlFeatureFactory.newInstance(SqlFeatureSupport.STMT_HOLDABILITY, false));
    }
    
    /**
     *  LIMIT clause for SqlServer, where TOP is a parameter from
     *  String.format
     *  
     *  @return Return query pattern: 
     *  <pre>
     *   {@code
     *   WITH query AS (
     *           SELECT inner_query.*
     *                , ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as _jkniv_rownum_
     *             FROM ( select ID, SERVICO from ine.configuracao ) inner_query
     *         )
     *        SELECT * FROM query WHERE _jkniv_rownum_ >= 3 AND _jkniv_rownum_ < 5 + 3
     *   }
     * </pre>
     */
    @Override
    public String getSqlPatternPaging()
    {
        //return "select TOP %2$s %1$s";
        //return "%1$s OFFSET %2$s ROWS FETCH NEXT %3$s ROWS ONLY";
        final StringBuilder pagingSelect = new StringBuilder(100);
        pagingSelect.append(" WITH query AS (");
        pagingSelect.append("  SELECT inner_query.*");
        pagingSelect.append("      , ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) as _jkniv_rownum_");
        pagingSelect.append("  FROM ( %1$s ) inner_query");
        pagingSelect.append(" )");
        pagingSelect.append(" SELECT * FROM query WHERE _jkniv_rownum_ > %2$s AND _jkniv_rownum_ <= %3$s + %2$s");
        return pagingSelect.toString();

    }
    
    //@Override protected void buildSqlLimits()
    @Override
    public String buildQueryPaging(final String sqlText, int offset, int max)
    {
        String sqlTextPaginated = null;
//        if (isSelect() && queryable.isPaging())
//        {
            //sqlText = queryable.getSql().getSql(queryable.getParams());
            String pagingSelect = getSqlPatternPaging();
            //String sqlPreparedToTop = this.sql.replaceFirst("(?i)select", "");
            if (supportsFeature(SqlFeatureSupport.LIMIT_OFF_SET))
            {
                StringBuilder sb = new StringBuilder(sqlText);
                //final int orderByIndex = shallowIndexOfWord( sb, "order by", 0 );
                Matcher matcher = sqlEndsWithOrderBy(sqlText);
                if ( matcher.find() ) {
                    // ORDER BY requires using TOP.
                    addTopExpression( sb, offset, max );
                }
                sqlTextPaginated = String.format(pagingSelect, sb.toString(), offset, max);
            }
////            else
////                this.sqlWithLimit = String.format(pagingSelect, sqlPreparedToTop, queryable.getMax());
//        }
//        else
//            this.sql = queryable.getSql().getSql(queryable.getParams());
//        
//        replaceForQuestionMark();
        return sqlTextPaginated;
    }
    
    /**
     * Adds {@code TOP} expression. Parameter value is bind in
     * {@link #bindLimitParametersAtStartOfQuery(PreparedStatement, int)} method.
     *
     * @param sql SQL query.
     */
    private void addTopExpression(StringBuilder sql, int offset, int max) {
        Matcher matcher = super.sqlStartWithSelectOrDistinct(sql.toString());
        if(matcher.find())
            sql.insert(matcher.end(), "TOP(" + offset + "+" + max +") " );
        /*
        final int distinctStartPos = shallowIndexOfWord( sql, "distinct", 0 );
        if ( distinctStartPos > 0 ) {
            // Place TOP after DISTINCT.
            sql.insert( distinctStartPos + "distinct".length(), " TOP(" + offset + "+" + max +")" );
        }
        else {
            final int selectStartPos = shallowIndexOf( sql, "select ", 0 );
            // Place TOP after SELECT.
            sql.insert( selectStartPos + "select".length(), " TOP(" + offset + "+" + max +")" );
        }
        */
    }

    /**
     * Returns index of the first case-insensitive match of search term surrounded by spaces
     * that is not enclosed in parentheses.
     *
     * @param sb String to search.
     * @param search Search term.
     * @param fromIndex The index from which to start the search.
     *
     * @return Position of the first match, or {@literal -1} if not found.
     */
    private static int shallowIndexOfWord(final StringBuilder sb, final String search, int fromIndex) {
        final int index = shallowIndexOf( sb, ' ' + search + ' ', fromIndex );
        // In case of match adding one because of space placed in front of search term.
        return index != -1 ? ( index + 1 ) : -1;
    }

    /**
     * Returns index of the first case-insensitive match of search term that is not enclosed in parentheses.
     *
     * @param sb String to search.
     * @param search Search term.
     * @param fromIndex The index from which to start the search.
     *
     * @return Position of the first match, or {@literal -1} if not found.
     */
    private static int shallowIndexOf(StringBuilder sb, String search, int fromIndex) {
        // case-insensitive match
        final String lowercase = sb.toString().toLowerCase(Locale.ROOT);
        final int len = lowercase.length();
        final int searchlen = search.length();
        int pos = -1;
        int depth = 0;
        int cur = fromIndex;
        do {
            pos = lowercase.indexOf( search, cur );
            if ( pos != -1 ) {
                for ( int iter = cur; iter < pos; iter++ ) {
                    final char c = sb.charAt( iter );
                    if ( c == '(' ) {
                        depth = depth + 1;
                    }
                    else if ( c == ')' ) {
                        depth = depth - 1;
                    }
                }
                cur = pos + searchlen;
            }
        } while ( cur < len && depth != 0 && pos != -1 );
        return depth == 0 ? pos : -1;
    }
}
