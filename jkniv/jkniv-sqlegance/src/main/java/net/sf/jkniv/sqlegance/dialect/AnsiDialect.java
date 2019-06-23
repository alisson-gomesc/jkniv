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
package net.sf.jkniv.sqlegance.dialect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Sql;

/**
 * Represents the support from SQL ANSI that are queries cross-platform.
 *
 * Represents a dialect of SQL implemented by a particular RDBMS. Subclasses implement Hibernate compatibility
 * with different systems.  Subclasses should provide a public default constructor that register a set of type
 * mappings and default Hibernate properties.  Subclasses should be immutable.
 *
 * <ul>
 *  <li>Supports limits? false</li>
 *  <li>Supports limit off set? false</li>
 *  <li>Supports rownum? false</li>
 * </ul>
 * 
 * @author Alisson Gomes
 *
 */
public class AnsiDialect implements SqlDialect
{
    private static final Assertable                      notNull                     = AssertsFactory.getNotNull();
    // find the pattern start with 'with'
    private static final String                          REGEX_START_WITH            = "^\\s*(with)\\s";
    // find the pattern start with 'select'
    private static final String                          REGEX_START_SELECT          = "^\\s*(select)\\s";
    
    // find the pattern start with 'select distinct'
    private static final String                          REGEX_START_SELECT_DISTINCT = "^\\s*(select\\s+distinct|select)\\s";
    
    // find the pattern end with'for update'
    private static final String                          REGEX_ENDS_FORUPDATE        = "\\s+(for\\s+update)\\s*$";
    
    private static final String                          REGEX_ENDS_ORDERBY          = "\\s*(order\\s+by)\\s*[a-zA-Z0-9,_\\.\\)\\s]*$";
    
    private static final Pattern                         patternWith                 = Pattern.compile(REGEX_START_WITH,
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static final Pattern                         patternSelect               = Pattern
            .compile(REGEX_START_SELECT, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static final Pattern                         patternSelectDistinct       = Pattern
            .compile(REGEX_START_SELECT_DISTINCT, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static final Pattern                         patternForUpdate            = Pattern
            .compile(REGEX_ENDS_FORUPDATE, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    public static final Pattern                          patternORDER_BY             = Pattern
            .compile(REGEX_ENDS_ORDERBY, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    
    protected String                                     name;
    private final HashMap<SqlFeatureSupport, SqlFeature> sqlFeatures;
    private int                                          maxOfParameters;
    //protected Queryable          queryable;
    //protected String             sql;
    //protected String             sqlWithLimit;
    //protected String             sqlToCount;
    //protected String[]           paramsNames;             
    //protected int countParams;
    
    public AnsiDialect()
    {
        this.name = getClass().getSimpleName();
        this.sqlFeatures = new HashMap<SqlFeatureSupport, SqlFeature>();
        this.sqlFeatures.put(SqlFeatureSupport.LIMIT, 
                             SqlFeatureFactory.newInstance(SqlFeatureSupport.LIMIT));
        this.sqlFeatures.put(SqlFeatureSupport.LIMIT_OFF_SET,
                             SqlFeatureFactory.newInstance(SqlFeatureSupport.LIMIT_OFF_SET));
        this.sqlFeatures.put(SqlFeatureSupport.ROWNUM, 
                             SqlFeatureFactory.newInstance(SqlFeatureSupport.ROWNUM));
        this.sqlFeatures.put(SqlFeatureSupport.STMT_HOLDABILITY,
                             SqlFeatureFactory.newInstance(SqlFeatureSupport.STMT_HOLDABILITY));
        this.sqlFeatures.put(SqlFeatureSupport.CONN_HOLDABILITY,
                             SqlFeatureFactory.newInstance(SqlFeatureSupport.CONN_HOLDABILITY, true));
        this.sqlFeatures.put(SqlFeatureSupport.BOOKMARK_QUERY,
                SqlFeatureFactory.newInstance(SqlFeatureSupport.BOOKMARK_QUERY));
        this.sqlFeatures.put(SqlFeatureSupport.PAGING_ROUNDTRIP,
                SqlFeatureFactory.newInstance(SqlFeatureSupport.PAGING_ROUNDTRIP, true));
        this.maxOfParameters = Integer.MAX_VALUE;
        //this.countParams = 0;
    }
    
    //    public AnsiDialect()
    //    {
    //        notNull.verify(queryable.getSql());
    //        this.name = getClass().getSimpleName();
    //        this.isql = isql;
    //        this.queryable = queryable;
    //        this.init();
    //    }
    
    //    private void init()
    //    {
    //        buildSqlLimits();
    //        if (queryable.isPaging())
    //            buildSqlCount();
    //    }
    
    public String name()
    {
        return this.name;
    }
    
    @Override
    public boolean supportsFeature(SqlFeatureSupport feature)
    {
        boolean answer = false;
        
        SqlFeature sqlFeature = sqlFeatures.get(feature);
        if (sqlFeature != null)
            answer = sqlFeature.supports();
        
        return answer;
    }
    
    @Override
    public SqlFeature addFeature(SqlFeature sqlFeature)
    {
        return this.sqlFeatures.put(sqlFeature.getSqlFeature(), sqlFeature);
    }
    
    /*
    @Override
    public boolean supportsLimit()
    {
        return false;
    }
    
    @Override
    public boolean supportsLimitOffset()
    {
        return false;
    }
    
    @Override
    public boolean supportsRownum()
    {
        return false;
    }
    
    
    @Override
    public boolean supportsStmtHoldability()
    {
        return false;
    }
    
    @Override
    public boolean supportsConnHoldability()
    {
        return true;
    }
    */
    @Override
    public int getMaxOfParameters()
    {
        return maxOfParameters;
    }
    
    @Override
    public void setMaxOfParameters(int max)
    {
        if (max > 0)
            this.maxOfParameters = max;
    }
    
    @Override
    public String getSqlPatternCount()
    {
        // using String.format argument index
        return "select count(1) from (%1$s) jkniv_ct_tmp_table";// FIXME BUG when conflict alias name
    }
    
    public String getSqlPatternPaging()
    {
        String pattern = "%1$s";
        if (supportsFeature(SqlFeatureSupport.LIMIT))
        {
            pattern += " LIMIT %2$s";
            if (supportsFeature(SqlFeatureSupport.LIMIT_OFF_SET))
                pattern += ", %3$s";
        }
        return pattern;//%1$s LIMIT %2$s, %3$s
    }
    
    @Override
    public PreparedStatement prepare(Connection conn, Sql isql, String query)
    {
        PreparedStatement stmt = null;
        int rsType = isql.getResultSetType().getTypeScroll();
        int rsConcurrency = isql.getResultSetConcurrency().getConcurrencyMode();
        int rsHoldability = isql.getResultSetHoldability().getHoldability();
        try
        {
            if (isql.isInsertable())
            {
                Insertable insertTag = isql.asInsertable();
                if (insertTag.isAutoGenerateKey() && insertTag.getAutoGeneratedKey().isAutoStrategy())
                {
                    String[] columns = insertTag.getAutoGeneratedKey().getColumnsAsArray();
                    stmt = conn.prepareStatement(query, columns);
                }
            }
            if (stmt == null)
            {
                if (supportsFeature(SqlFeatureSupport.STMT_HOLDABILITY))
                    stmt = conn.prepareStatement(query, rsType, rsConcurrency, rsHoldability);
                else
                {
                    // SQLServer/Oracle12 doesn't support Holdability
                    stmt = conn.prepareStatement(query, rsType, rsConcurrency);
                    if (supportsFeature(SqlFeatureSupport.CONN_HOLDABILITY))
                        conn.setHoldability(rsHoldability);
                }
            }
            if (isql.getTimeout() > 0)
                stmt.setQueryTimeout(isql.getTimeout());
        }
        catch (SQLException sqle)
        {
            throw new RepositoryException("Cannot prepare statement [" + sqle.getMessage() + "]", sqle);
        }
        return stmt;
    }
    
    //protected void buildSqlLimits()
    @Override
    public String buildQueryPaging(final String sqlText, int offset, int max)
    {
        String sqlTextPaginated = null;
        if (supportsFeature(SqlFeatureSupport.LIMIT))
        {
            String pagingSelectTemplate = getSqlPatternPaging();
            //sqlText = queryable.getSql().getSql(queryable.getParams());
            //assertSelect(this.sql);
            Matcher matcher = sqlEndWithForUpdate(sqlText);
            String forUpdate = "";
            String sqlTextWithouForUpdate = sqlText;
            if (matcher.find())
            {
                forUpdate = sqlText.substring(matcher.start(), matcher.end());// select name from author ^for update^
                sqlTextWithouForUpdate = sqlText.substring(0, matcher.start());// ^select name from author^ for update
            }
            if (supportsFeature(SqlFeatureSupport.LIMIT_OFF_SET))
                sqlTextPaginated = String.format(pagingSelectTemplate, sqlTextWithouForUpdate, max, offset) + forUpdate;
            else
                sqlTextPaginated = String.format(pagingSelectTemplate, sqlTextWithouForUpdate, max) + forUpdate;
            
        }
        //replaceForQuestionMark();
        return sqlTextPaginated;
    }
    
    @Override
    public String buildQueryPaging(final String sqlText, int offset, int max, String bookmark)
    {
        return buildQueryPaging(sqlText, offset, max);
    }
    
    /*
    protected void replaceForQuestionMark()
    {
        this.paramsNames = queryable.getSql().getParamParser().find(sql);
        if(sqlWithLimit != null)
            this.sqlWithLimit = queryable.getSql().getParamParser().replaceForQuestionMark(sqlWithLimit, queryable.getParams());
        this.sql = queryable.getSql().getParamParser().replaceForQuestionMark(sql, queryable.getParams());
        this.countParams = countOccurrencesOf(this.sql, "?");
    }
    */
    
    @Override
    public String buildQueryCount(String sqlText)
    {
        String sqlToCount = null;
        Matcher matcher = sqlEndWithForUpdate(sqlText);
        if (matcher.find())
            sqlToCount = String.format(getSqlPatternCount(), sqlText.substring(0, matcher.start()));// ^select name from author^ for update
        else
            sqlToCount = String.format(getSqlPatternCount(), sqlText);
        
        return sqlToCount;
    }
    
    protected Matcher sqlStartWithSelect(String sql)
    {
        Matcher matcher = patternSelect.matcher(sql);
        return matcher;
    }
    
    protected Matcher sqlStartWithSelectOrDistinct(String sql)
    {
        Matcher matcher = patternSelectDistinct.matcher(sql);
        return matcher;
    }
    
    protected Matcher sqlEndsWithOrderBy(String sql)
    {
        Matcher matcher = patternORDER_BY.matcher(sql);
        return matcher;
    }
    
    protected Matcher sqlEndWithForUpdate(String sql)
    {
        Matcher matcher = patternForUpdate.matcher(sql);
        return matcher;
    }
    
    //    protected boolean isSelect()
    //    {
    //        return queryable.getSql().isSelect();
    //    }
    
    /** 
     * Count the occurrences of the substring in string s.
     * @param str string to search in. Return 0 if this is null.
     * @param sub string to search for. Return 0 if this is null.
     * @return number of occurrences from {@code str}
     */
    protected int countOccurrencesOf(String str, String sub)
    {
        if (str == null || sub == null || str.length() == 0 || sub.length() == 0)
        {
            return 0;
        }
        int count = 0;
        int pos = 0;
        int idx;
        while ((idx = str.indexOf(sub, pos)) != -1)
        {
            ++count;
            pos = idx + sub.length();
        }
        return count;
    }
    
    /*
    protected void assertSelect(String sql)
    {
        if (sqlStartWithSelectDistinct(sql).find() || sqlStartWithSelect(sql).find())
            return;
        else if (patternWith.matcher(sql).find())
            throw new RepositoryException("JDBC repository doesn't support paginate query started with WITH clause");
        
        throw new RepositoryException(
                "JDBC repository cannot paginate [DELETE | UPDATE | INSERT] queries, just SELECT");
    }
    
    protected void assertSelect(ISql sql)
    {
        if (sql.getSqlCommandType() != SqlCommandType.SELECT)
            throw new RepositoryException(
                    "JDBC repository cannot paginate [DELETE | UPDATE | INSERT] queries, just SELECT");
    }
    */
}
