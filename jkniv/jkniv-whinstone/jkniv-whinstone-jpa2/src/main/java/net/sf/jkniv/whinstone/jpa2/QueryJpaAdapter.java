package net.sf.jkniv.whinstone.jpa2;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.NonUniqueResultException;
import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.logger.SqlLogger;
import net.sf.jkniv.sqlegance.params.AutoBindParams;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.sqlegance.params.ParamParserQuestionMark;
import net.sf.jkniv.sqlegance.params.PrepareParamsFactory;
import net.sf.jkniv.sqlegance.params.StatementAdapterOld;
import net.sf.jkniv.whinstone.jpa2.params.JpaStatementAdapter;

@SuppressWarnings("unchecked")
public class QueryJpaAdapter extends AbstractQueryJpaAdapter
{
    private static final Logger LOG = LoggerFactory.getLogger(QueryJpaAdapter.class);

    public QueryJpaAdapter(EntityManager em, Queryable queryable, Sql isql, Class<?> overloadReturnType, SqlLogger sqlLogger)
    {
        super(em, queryable, isql, sqlLogger);
        String sql = isql.getSql(queryable.getParams());
        ParamParser paramParser = isql.getParamParser();
        String[] paramsNames = isql.getParamParser().find(sql);
        Queryable queryable2 = queryable;
        switch (isql.getLanguageType())
        {
            case JPQL:
                if (overloadReturnType == null)
                    queryJpa = em.createQuery(sql);
                else if(isql.isReturnTypeManaged())
                    queryJpa = em.createQuery(sql, overloadReturnType);
                else
                    queryJpa = em.createQuery(sql);
                
                break;
            case NATIVE:
                // EclipseLink doesn't support named params to native queries, adopting ?1 ?2 ?3 for every implementation
                paramParser = new ParamParserQuestionMark();
                String positionalSql = isql.getParamParser().replaceForQuestionMarkWithNumber(sql, queryable.getParams());
                Object[] params = queryable.values(paramsNames);
                queryable2 = QueryFactory.newInstance(queryable.getName(), params, queryable.getOffset(), queryable.getMax());
                if (overloadReturnType == null)
                    queryJpa = em.createNativeQuery(positionalSql);
                else if(isql.isReturnTypeManaged())
                    queryJpa = em.createNativeQuery(positionalSql, overloadReturnType);
                else
                    queryJpa = em.createNativeQuery(positionalSql);
                
                break;
//            case STORED:
//                queryJpa = em.createStoredProcedureQuery(isql.asStorable().getSpName());
//                break;
            
            default:
                throw new UnsupportedOperationException("RepositoryJpa supports JPQL or NATIVE queries none other, Stored Procedure is pending to implements");
        }
        StatementAdapterOld stmtAdapter = new JpaStatementAdapter(queryJpa, sqlLogger);
        AutoBindParams prepareParams = PrepareParamsFactory.newPrepareParams(stmtAdapter, paramParser, queryable2);
        prepareParams.parameterized(paramsNames);
        if (queryable.isPaging() && isql.isSelectable())
        {
            queryJpa.setFirstResult(queryable.getOffset()).setMaxResults(queryable.getMax());
        }
    }

    
 // TODO test case zero return
 // TODO test case one return
 // TODO test case multiple result
 // TODO test case multiple result using groupBy    
    @Override
    public <T> T getSingleResult()
    {
        T ret = null;
        //ret = super.getSingleResult();
        
        List<T> list = super.getResultList();
        if (list.size() > 1)
        {
            List<T> listGrouped = null;
            if (isql.getLanguageType() == LanguageType.NATIVE && 
                !"".equals(isql.getReturnType()) && list.size() > 0)
            {
                list = cast((List<Object[]>) list, isql.getReturnType());
            }
            listGrouped = groupingBy(list);
            if (listGrouped.size() > 1)
                throw new NonUniqueResultException("Query ["+queryable.getName()+"] result fetch ["+listGrouped.size()+"] rows, Repository.get(..) method must return just one row");

            ret = listGrouped.get(0);
            queryable.setTotal(1);
        }
        else if(list.size() == 1) // TODO test when result is/not scalar, is/not native, != null
        {
            ret = list.get(0);
            if (!queryable.isScalar() && isql.getLanguageType() == LanguageType.NATIVE && isql.getReturnType() != null)
            {
                ObjectProxy<T> proxy = ObjectProxyFactory.newProxy(isql.getReturnType());
                proxy.setConstructorArgs((Object[])ret);// test when return value is not array
                ret = proxy.newInstance();
            }
            queryable.setTotal(1);
        }
        
        
        // FIXME transform result using group by needs call getResultList instead getSingleResult
//      if (isDebugEnabled)
//      LOG.debug("Executed [{}] query, {}/{} rows fetched transformed to -> {}", queryable.getName(), totalBeforeGroup, queryable.getTotal(), ret.size());

        return ret;
    }
    
    @Override
    public <T> List<T> getResultList()
    {
        List<T> list = super.getResultList();
        if (isql.getLanguageType() == LanguageType.NATIVE && 
            !"".equals(isql.getReturnType()) && list.size() > 0)
        {
            list = cast((List<Object[]>) list, isql.getReturnType());
        }
        int totalBeforeGroup = list.size();

        list = groupingBy(list);
        if (LOG.isDebugEnabled())
          LOG.debug("Executed [{}] query, {}/{} rows fetched transformed to -> {}", queryable.getName(), totalBeforeGroup, queryable.getTotal(), list.size());

        return list;        
    }
    
}
