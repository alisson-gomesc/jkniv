package net.sf.jkniv.whinstone.jpa2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.reflect.BasicType;
import net.sf.jkniv.reflect.NumberFactory;
import net.sf.jkniv.reflect.Numerical;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.Selectable;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.classification.Groupable;
import net.sf.jkniv.whinstone.classification.GroupingBy;
import net.sf.jkniv.whinstone.classification.Transformable.TransformableType;

public abstract class AbstractQueryJpaAdapter implements QueryableJpaAdapter
{
    private static final Logger    LOG        = LoggerFactory.getLogger(AbstractQueryJpaAdapter.class);
    //protected SqlLogger sqlLogger;
    private static final BasicType BASIC_TYPE = BasicType.getInstance();
    protected Queryable queryable;
    protected Sql isql;
    protected EntityManager em;
    private Query queryJpaForPaging;
    protected Query queryJpa;
    
    public AbstractQueryJpaAdapter(EntityManager em, Queryable queryable, Sql isql)//, SqlLogger sqlLogger)
    {
        this.em = em;
        this.queryable = queryable;
        this.isql = isql;
        //this.sqlLogger = sqlLogger;
    }
    
    /**
     * Default behavior for {@code javax.persistence.Query} JPA.
     */
    @Override
    public <T> T getSingleResult()
    {
        T ret = null;
        try
        {
            ret = (T) queryJpa.getSingleResult();
            queryable.setTotal(1);
        }
        catch (javax.persistence.NoResultException returnNull) { 
            queryable.setTotal(0);
        }
        return ret;
    }

    /**
     * Default behavior for {@code javax.persistence.Query} JPA.
     */
    @Override
    public <T> List<T> getResultList()
    {
        List<T> list = queryJpa.getResultList();
        if (list == null)
            list = new ArrayList<T>();
        
        queryable.setTotal(list.size());
        setTotalPaging(queryable);
        
        return list;
    }
    
    @Override
    public int executeUpdate()
    {
        int rows = queryJpa.executeUpdate();
        return rows;
    }


    @Override
    public void setQueryJpaForPaging(Query queryForPaging)
    {
        this.queryJpaForPaging = queryForPaging;
    }
    
    
    @Override
    public Query getQueryJpaForPaging()
    {
        return queryJpaForPaging;
    }
    
    /**
     * Make the conversion from object list to another T
     * @param list list of original values
     * @param returnType class type of {@code T}
     * @param <T> class type 
     * @return list of casted objects
     */
    @SuppressWarnings("unchecked")
    protected <T> List<T> cast(List list, String returnType)// TODO test me case when jpa return array of objects (native query or select specific columns
    {
        List<T> castedList = null;
        Object firstValue = list.get(0);
        if (firstValue.getClass().getName().equals(returnType))
        {
            castedList = list;
        }
        else if (firstValue instanceof Number)
        {
            Numerical factory = NumberFactory.getInstance(returnType);
            castedList = new ArrayList<T>(list.size());
            List<?> listArray = (List<?>)list;
            for (Object o : listArray)
            {
                T casted = (T) factory.valueOf(o);
                castedList.add(casted);
            }
        }
//        else if (firstValue instanceof String)
//        {
//            
//        }
//        else if(BASIC_TYPE.isBasicType(firstValue.getClass()))
//        {
//            castedList = new ArrayList<T>(list.size());
//            List<?> listArray = (List<?>)list;
//            for (Object o : listArray)
//            {
//                ObjectProxy<T> proxy = ObjectProxyFactory.newProxy(returnType);
//                proxy.setConstructorArgs(o);
//                T casted = proxy.newInstance();
//                castedList.add(casted);
//            }
//        }
        else
        {
            castedList = new ArrayList<T>(list.size());
            List<Object[]> listArray = (List<Object[]>)list;
            for (Object[] o : listArray)
            {
                ObjectProxy<T> proxy = ObjectProxyFactory.newProxy(returnType);
                proxy.setConstructorArgs(o);
                T casted = proxy.newInstance();
                castedList.add(casted);
            }
        }
        if (castedList.size() != list.size())
            throw new RepositoryException("Wrong conversion type from List<Object[]> to List of [" + returnType + "]");
        return castedList;
    }
    
    protected <T> List<T> groupingBy(List<T> list) {
        List<T> newList = Collections.emptyList();
        List<String>  groupingBy = Collections.emptyList();
        groupingBy = ((Selectable)isql).getGroupByAsList();
        if(!list.isEmpty() && !groupingBy.isEmpty())
        {
            Class<T> returnedType = (Class<T>) list.get(0).getClass();
            Groupable<T, T> grouping = new GroupingBy<T, T>(groupingBy, returnedType, TransformableType.OBJECT);
            for(T  row : list)
                grouping.classifier(row);
    
            newList = grouping.asList();
        }
        else
            newList = list;
       return newList;
    }

    
    protected void setTotalPaging(Queryable queryable)
    {
        if (queryJpaForPaging != null)
        {
            try
            {
                Number total = (Number) queryJpaForPaging.getSingleResult();
                if (total != null)
                    queryable.setTotal(total.longValue());
            }
            catch (NoResultException e)
            {
                queryable.setTotal(0L);
            }
        }
    }
    /*
    
    private Query getQueryForAutoCount(Queryable queryable, ISql isql) // TODO delete, logic was moved 
    {
        Query queryJpa = null;
        ISql isqlCount = null;
        try
        {
            String queryName = queryable.getName() + "#count";
            isqlCount = sqlContext.getQuery(queryName);
            LOG.trace("executing count query [" + queryable.getName() + "#count" + "]");
            Queryable queryCopy = new QueryName(queryName, queryable.getParams(), 0, Integer.MAX_VALUE);
            queryJpa = QueryFactory.newQuery(isqlCount, em, queryCopy, sqlLogger);
        }
        catch (QueryNotFoundException e)
        {
            // but its very important remove the order clause, because cannot
            // execute this way wrapping with "select count(*) ... where exists"
            // and performance
            String sqlWithoutOrderBy = removeOrderBy(isql.getSql(queryable.getParams()));
            //String entityName = genericType.getSimpleName();
            String sql = "select count (*) from " + isql.getReturnType() + " where exists (" + sqlWithoutOrderBy + ")";
            LOG.trace("try to count rows using dynamically query [" + sql + "]");
            Queryable queryCopy = new QueryName(queryable.getName(), queryable.getParams(), 0, Integer.MAX_VALUE);
            queryJpa = QueryFactory.newQueryForCount(sql, isql.getLanguageType(), em, queryCopy, isql.getParamParser(), sqlLogger);
        }
        return queryJpa;
    }
    */


    protected void checkSqlCommandType(Sql isql, SqlType expected)
    {
        
        if (isql.getSqlType() != expected)
            throw new IllegalArgumentException("Cannot execute sql ["+isql.getName()+"] as ["+isql.getSqlType()+"], exptected is "+ expected);
        
    }
    


}
