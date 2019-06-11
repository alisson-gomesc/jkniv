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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.sqlegance.Insertable;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.statement.ResultSetConcurrency;
import net.sf.jkniv.sqlegance.statement.ResultSetHoldability;
import net.sf.jkniv.sqlegance.statement.ResultSetType;

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
    private static final Assertable notNull = AssertsFactory.getNotNull();
    // find the pattern start with 'with'
    private static final String  REGEX_START_WITH            = "^\\s*(with)\\s";
    // find the pattern start with 'select'
    private static final String  REGEX_START_SELECT          = "^\\s*(select)\\s";
    
    // find the pattern start with 'select distinct'
    private static final String  REGEX_START_SELECT_DISTINCT = "^\\s*(select\\s+distinct|select)\\s";
    
    // find the pattern end with'for update'
    private static final String  REGEX_ENDS_FORUPDATE        = "\\s+(for\\s+update)\\s*$";
    
    private static final String REGEX_ENDS_ORDERBY          = "\\s*(order\\s+by)\\s*[a-zA-Z0-9,_\\.\\)\\s]*$";
    

    
    private static final Pattern patternWith                 = Pattern.compile(REGEX_START_WITH,
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static final Pattern patternSelect               = Pattern.compile(REGEX_START_SELECT,
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static final Pattern patternSelectDistinct       = Pattern.compile(REGEX_START_SELECT_DISTINCT,
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private static final Pattern patternForUpdate            = Pattern.compile(REGEX_ENDS_FORUPDATE,
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    public static final Pattern patternORDER_BY              = Pattern.compile(REGEX_ENDS_ORDERBY, 
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    protected String             name;
    //protected Queryable          queryable;
    //protected String             sql;
    //protected String             sqlWithLimit;
    //protected String             sqlToCount;
    //protected String[]           paramsNames;             
    //protected int countParams;
    
    public AnsiDialect()
    {
        this.name = getClass().getSimpleName();
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
    
//    @Override
//    public boolean supportsFeature(SqlFeatureSupports feature)
//    {
//        return feature.supports();
//    }
    
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
    public int getLimitOfParameters()
    {
        return Integer.MAX_VALUE;
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
        if (supportsLimit())
        {
            pattern += " LIMIT %2$s";
            if (supportsLimitOffset())
                pattern += ", %3$s";
        }
        return pattern;//%1$s LIMIT %2$s, %3$s
    }
    
//    @Override
//    public String query()
//    {
//        return (this.sqlWithLimit == null ? this.sql : this.sqlWithLimit);
//    }
    
//    @Override
//    public String queryCount()
//    {
//        return this.sqlToCount;
//    }
    
//    @Override
//    public Sql getISql()
//    {
//        return this.isql;
//    }
//
//    @Override
//    public Queryable getQueryable()
//    {
//        return this.queryable;
//    }
//    
//    @Override
//    public void setQueryable(Queryable queryable)
//    {
//        //this.isql = isql;
//        this.queryable = queryable;
//        init();
//    }
    
//    @Override
//    public int countParams() 
//    {
//        return countParams;
//    }
//    
//    @Override
//    public String[] getParamsNames()
//    {
//        return this.paramsNames;
//    }
//
//    @Override
//    public Object[] getParamsValues()
//    {
//        return queryable.values(this.paramsNames);
//    }

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
                if (supportsStmtHoldability())
                    stmt = conn.prepareStatement(query, rsType, rsConcurrency, rsHoldability);
                else
                {
                    // SQLServer doesn't support Holdability
                    stmt = conn.prepareStatement(query, rsType, rsConcurrency);
                    if (supportsConnHoldability())
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
        if (supportsLimit())
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
            if (supportsLimitOffset())
                sqlTextPaginated = String.format(pagingSelectTemplate, sqlTextWithouForUpdate, max, offset) + forUpdate;
            else
                sqlTextPaginated = String.format(pagingSelectTemplate, sqlTextWithouForUpdate, max) + forUpdate;
            
        }
        //replaceForQuestionMark();
        return sqlTextPaginated;
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
    protected int countOccurrencesOf(String str, String sub) {
        if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
            return 0;
        }
        int count = 0;
        int pos = 0;
        int idx;
        while ((idx = str.indexOf(sub, pos)) != -1) {
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
