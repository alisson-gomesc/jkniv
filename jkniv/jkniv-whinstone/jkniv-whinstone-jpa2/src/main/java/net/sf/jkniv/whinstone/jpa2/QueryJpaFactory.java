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
package net.sf.jkniv.whinstone.jpa2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.QueryNotFoundException;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.jpa2.params.JpaStatementAdapter;
import net.sf.jkniv.whinstone.params.AutoBindParams;
import net.sf.jkniv.whinstone.params.PrepareParamsFactory;
import net.sf.jkniv.whinstone.params.StatementAdapterOld;

@Deprecated
class QueryJpaFactory
{
    private static final Logger  LOG              = LoggerFactory.getLogger(QueryJpaFactory.class);
    private static final Pattern PATTERN_ORDER_BY = Pattern.compile("order\\s+by\\s+[\\w|\\W|\\s|\\S]*",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    
    public static QueryableJpaAdapter build(EntityManager em, SqlContext sqlContext, Queryable queryable,
            Class<?> overloadReturnedType)
    {
        QueryableJpaAdapter adapter = null;
        Class<?> mandatoryReturnType = null;
        Sql isql = null;
        boolean containQuery = sqlContext.containsQuery(queryable.getName());
        if (containQuery)
        {
            isql = sqlContext.getQuery(queryable.getName());
            if (isql.isSelectable())
                checkSqlConstraints(isql.asSelectable());
            
            isql.getValidateType().assertValidate(queryable.getParams());
            if (!queryable.isBoundSql())
                queryable.bind(isql);

            if (overloadReturnedType != null)
                mandatoryReturnType = overloadReturnedType;
            else if (isql.getReturnTypeAsClass() != null)
                mandatoryReturnType = isql.getReturnTypeAsClass();

            adapter = new QueryJpaAdapter(em, queryable, isql, mandatoryReturnType);
            if (queryable.isPaging())
                adapter.setQueryJpaForPaging(getQueryForPaging(em, sqlContext, queryable, isql));
        }
        else
        {
            adapter = new NamedQueryJpaAdapter(em, queryable, overloadReturnedType);
        }
        return adapter;
    }
    
    private static Query getQueryForPaging(EntityManager em, SqlContext sqlContext, Queryable queryable, Sql isql)
    {
        Query queryJpa = null;
        Sql isqlCount = null;
        try
        {
            String queryName = queryable.getName() + "#count";
            isqlCount = sqlContext.getQuery(queryName);
            LOG.trace("creating count query [{}] for {}", queryName, queryable.getName());
            Queryable queryCopy = QueryFactory.of(queryName, queryable.getParams(), 0, Integer.MAX_VALUE);
            queryJpa = QueryJpaFactory.newQuery(isqlCount, em, queryCopy);
        }
        catch (QueryNotFoundException e)
        {
            // but its very important remove the order clause, because cannot
            // execute this way wrapping with "select count(*) ... where exists" and performance
            String sqlWithoutOrderBy = removeOrderBy(isql.getSql(queryable.getParams()));
            //String entityName = genericType.getSimpleName();
            String sql = "select count (*) from " + isql.getReturnType() + " where exists (" + sqlWithoutOrderBy + ")";
            LOG.trace("creating counttry to count rows using dynamically query [" + sql + "]");
            Queryable queryCopy = QueryFactory.of(queryable.getName(), queryable.getParams(), 0,
                    Integer.MAX_VALUE);
            queryJpa = QueryJpaFactory.newQueryForCount(sql, isql.getLanguageType(), em, queryCopy,
                    isql.getParamParser());
        }
        return queryJpa;
    }
    
    /**
     * Remove the order by clause from the query.
     * 
     * @param hql
     *            SQL, JPQL or HQL
     * @return return the query without order by clause.
     */
    private static String removeOrderBy(String hql)
    {
        Matcher m = PATTERN_ORDER_BY.matcher(hql);
        StringBuffer sb = new StringBuffer();
        while (m.find())
        {
            if (m.hitEnd())
                m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }
    
    private static Query newQuery(Sql isql, EntityManager em, Queryable query)
    {
        String sql = isql.getSql(query.getParams());
        Query queryJpa = create(isql.getLanguageType(), sql, em);
        StatementAdapterOld stmtAdapter = new JpaStatementAdapter(queryJpa);
        AutoBindParams prepareParams = PrepareParamsFactory.newPrepareParams(stmtAdapter, isql.getParamParser(), query);
        //prepareParams.parameterized(isql.getParamParser().find(sql));
        if (query.isPaging() && isql.isSelectable())
        {
            queryJpa.setFirstResult(query.getOffset());
            queryJpa.setMaxResults(query.getMax());
        }
        return queryJpa;
    }
    
    private static Query newQueryForCount(String sql, LanguageType languageType, EntityManager em, Queryable queryable,
            ParamParser paramParser)
    {
        Query queryJpa = create(languageType, sql, em);
        StatementAdapterOld stmtAdapter = new JpaStatementAdapter(queryJpa);
        AutoBindParams prepareParams = PrepareParamsFactory.newPrepareParams(stmtAdapter, paramParser, queryable);
        
        prepareParams.parameterized(paramParser.find(sql));
        return queryJpa;
    }
    
    private static Query create(LanguageType languageType, String sql, EntityManager em)
    {
        Query query = null;
        if (languageType == LanguageType.JPQL)
        {
            query = em.createQuery(sql);
        }
        else if (languageType == LanguageType.NATIVE)
        {
            query = em.createNativeQuery(sql);
        }
        else
        {
            throw new RepositoryException(
                    "This implementation just supports LanguageType.JPQL and LanguageType.NATIVE queries");
        }
        return query;
    }
    
    private static void checkSqlConstraints(Selectable tag)
    {
        LanguageType type = tag.getLanguageType();
        if ((type == LanguageType.JPQL || type == LanguageType.HQL) && tag.getGroupBy().trim().length() > 0)
        {
            throw new IllegalArgumentException("JPQL cannot have group by, just NATIVE or STORED, change the type ["
                    + type + "] from SQL [" + tag.getName() + "] to execute");
        }
//        if (tag.getLanguageType() == LanguageType.NATIVE && "".equals(tag.getReturnType()))
//            throw new RepositoryException(
//                    "JPA NATIVE ["+tag.getName()+"] query require returnType with appropriate constructor with parameters from");
    }
    
}
