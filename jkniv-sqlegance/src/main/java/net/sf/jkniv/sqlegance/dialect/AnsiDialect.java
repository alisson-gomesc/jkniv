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

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.reflect.beans.PropertyAccess;

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
    private PropertyAccess                               propertyAccessId;
    private PropertyAccess                               propertyAccessRevision;
    
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
        this.sqlFeatures.put(SqlFeatureSupport.SEQUENCE,
                             SqlFeatureFactory.newInstance(SqlFeatureSupport.SEQUENCE));
        this.maxOfParameters = Integer.MAX_VALUE;
        this.propertyAccessId = new PropertyAccess("id", "getId", "setId");
        this.propertyAccessRevision = new PropertyAccess("rev", "getRev", "setRev");
        //this.countParams = 0;
    }
    
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
        return "select count(1) from (%1$s) jkniv_ct_tmp_table";
    }
    
    @Override
    public String getSqlPatternPaging()
    {
        StringBuilder pattern = new StringBuilder("%1$s");
        if (supportsFeature(SqlFeatureSupport.LIMIT))
        {
            pattern.append(" LIMIT %2$s");
            if (supportsFeature(SqlFeatureSupport.LIMIT_OFF_SET))
                pattern.append(", %3$s");
        }
        return pattern.toString();//%1$s LIMIT %2$s, %3$s
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
    
    @Override
    public PropertyAccess getAccessId()
    {
        return this.propertyAccessId;
    }
    
    @Override
    public PropertyAccess getAccessRevision()
    {
        return this.propertyAccessRevision;
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
            sqlToCount = String.format(getSqlPatternCount(), removeOrderBy(sqlText));
        
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
    
    /**
     * Remove the order by clause from the query.
     * 
     * @param hql
     *            SQL, JPQL or HQL
     * @return return the query without order by clause.
     */
    private String removeOrderBy(String hql)// TODO Single Responsibility
    {
        Matcher m = patternORDER_BY.matcher(hql);
        StringBuffer sb = new StringBuffer();
        while (m.find())
        {
            if (m.hitEnd())
                m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
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
