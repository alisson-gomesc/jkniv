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
package net.sf.jkniv.whinstone.jpa2.statement;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.experimental.TimerKeeper;
import net.sf.jkniv.reflect.NumberFactory;
import net.sf.jkniv.reflect.Numerical;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.KeyGeneratorType;
import net.sf.jkniv.sqlegance.LanguageType;
import net.sf.jkniv.sqlegance.OneToMany;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.sqlegance.statement.ColumnParserFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.classification.Groupable;
import net.sf.jkniv.whinstone.classification.GroupingBy;
import net.sf.jkniv.whinstone.classification.Transformable.TransformableType;
import net.sf.jkniv.whinstone.statement.AutoKey;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

public class JpaStatementAdapter<T, R> implements StatementAdapter<T, ResultSet>
{
    private static final Logger       LOG     = LoggerFactory.getLogger(JpaStatementAdapter.class);
    private static final Logger       SQLLOG  = net.sf.jkniv.whinstone.jpa2.LoggerFactory.getLogger();
    private static final DataMasking  MASKING = net.sf.jkniv.whinstone.jpa2.LoggerFactory.getDataMasking();
    private final Query               query;
    private int                       index;
    //private int                       indexIN;
    
    private final HandleableException handlerException;
    private Queryable                 queryable;
    private Class<T>                  returnType;
    private ResultRow<T, ResultSet>   resultRow;
    private boolean                   scalar;
    //private Set<OneToMany>            oneToManies;
    //private List<String>              groupingBy;
    private KeyGeneratorType          keyGeneratorType;
    
    public JpaStatementAdapter(Query query, Queryable queryable, HandleableException handlerException)
    {
        this.query = query;
        this.queryable = queryable;
        this.handlerException = handlerException;
        this.reset();
    }
    
    @Override
    public StatementAdapter<T, ResultSet> bind(String name, Object value)
    {
        log(name, value);
        query.setParameter(++index, value);
        return this;
    }
    
    //    @Override
    //    public StatementAdapterOld setParameter(int position, Object value)
    //    {
    //        this.index = position;
    //        log(position, value);
    //        query.setParameter(index+indexIN, value);
    //        return this;
    //    }
    
    @Override
    public StatementAdapter<T, ResultSet> bind(Object... values)
    {
        for (int j=0; j < values.length; j++)
        {
            Object v = values[j];
            log(index, v);
            query.setParameter(++index, v);
        }
        return this;
    }
    
    @Override
    public int reset()
    {
        int before = index;
        index = 0;
        //indexIN = 0;
        return before;
    }
    
    @Override
    public StatementAdapter<T, ResultSet> bind(Object value)
    {
        log(index, value);
        query.setParameter(++index, value);
        return this;
    }
    
    @Override
    public StatementAdapter<T, ResultSet> returnType(Class<T> returnType)
    {
        this.returnType = returnType;
        return this;
    }
    
    @Override
    public StatementAdapter<T, ResultSet> resultRow(ResultRow<T, ResultSet> resultRow)
    {
        this.resultRow = resultRow;
        return this;
    }
    
    @Override
    public StatementAdapter<T, ResultSet> scalar()
    {
        this.scalar = true;
        return this;
    }
    
//    @Override
//    public StatementAdapter<T, ResultSet> oneToManies(Set<OneToMany> oneToManies)
//    {
//        this.oneToManies = oneToManies;
//        return this;
//    }
//    
//    @Override
//    public StatementAdapter<T, ResultSet> groupingBy(List<String> groupingBy)
//    {
//        this.groupingBy = groupingBy;
//        return this;
//    }
    
    @Override
    public StatementAdapter<T, ResultSet> keyGeneratorType(KeyGeneratorType keyGeneratorType)
    {
        this.keyGeneratorType = keyGeneratorType;
        return this;
    }
    
    @Override
    public KeyGeneratorType getKeyGeneratorType()
    {
        return this.keyGeneratorType;
    }
    
    @Override
    public void bindKey()
    {
        // FIXME JPA has native auto-key, How is behavior with NATIVE query
        //        String[] properties = queryable.getDynamicSql().asInsertable().getAutoGeneratedKey().getPropertiesAsArray();
        //        ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(queryable.getParams());
        //        Iterator<Object> it = autoKey.iterator();
        //        for(int i=0; i<properties.length; i++)
        //            setValueOfKey(proxy, properties[i], it.next());        
    }
    
    @Override
    public StatementAdapter<T, ResultSet> with(AutoKey generateKey)
    {
        // FIXME JPA has native auto-key, How is behavior with NATIVE query
        return this;
    }
    
    /*
    @Override
    public List<T> rows()
    {
        //ResultSet rs = null;
        //ResultSetParser<T, ResultSet> rsParser = null;
        Groupable<T, ?> grouping = new NoGroupingBy<T, T>();
        List<T> list = Collections.emptyList();
        try
        {
            TimerKeeper.start();
            rs = query.getResultList();
            if (queryable != null)// TODO design improve for use sql stats
                queryable.getDynamicSql().getStats().add(TimerKeeper.clear());
            
            JdbcColumn<ResultSet>[] columns = getJdbcColumns(rs.getMetaData());
            setResultRow(columns);
            
            Transformable<T> transformable = resultRow.getTransformable();
            if (!groupingBy.isEmpty())
            {
                grouping = new GroupingBy(groupingBy, queryable.getReturnType(), transformable);
            }
            rsParser = new ObjectResultSetParser(resultRow, grouping);
            list = rsParser.parser(rs);
        }
        catch (SQLException e)
        {
            if (queryable != null) // TODO design improve for use sql stats
                queryable.getDynamicSql().getStats().add(e);
            handlerException.handle(e, e.getMessage());
        }
        return list;
    }
    */
    
    @Override
    public List<T> rows()
    {
        List<T> list = Collections.emptyList();
        try
        {
            TimerKeeper.start();
            if (queryable.isPaging()) 
            {
                SqlDialect sqlDialect = queryable.getDynamicSql().getSqlDialect();
                if (sqlDialect.supportsFeature(SqlFeatureSupport.LIMIT))
                    query.setMaxResults(queryable.getMax());
                    if (sqlDialect.supportsFeature(SqlFeatureSupport.LIMIT_OFF_SET))
                        query.setFirstResult(queryable.getOffset());
            }
            TimerKeeper.start();
            list = query.getResultList();
            if (queryable != null)// TODO design improve for use sql stats
                queryable.getDynamicSql().getStats().add(TimerKeeper.clear());
            
            if (queryable.getDynamicSql().getLanguageType() == LanguageType.NATIVE
                    && queryable.getDynamicSql().hasReturnType() && list.size() > 0)
            {
                list = cast((List<Object[]>) list, queryable.getDynamicSql().getReturnTypeAsClass());
            }
            int totalBeforeGroup = list.size();
            
            list = handleGroupingBy(list);
            if (LOG.isDebugEnabled())
                LOG.debug("Executed [{}] query, {}/{} rows fetched transformed to -> {}", queryable.getName(),
                        totalBeforeGroup, queryable.getTotal(), list.size());
        }
        catch (Exception e)
        {
            if (queryable != null) // TODO design improve for use sql stats
                queryable.getDynamicSql().getStats().add(e);
            handlerException.handle(e);
        }
        finally {
            TimerKeeper.clear();
        }
        
        return list;
    }
    
    private List<T> handleGroupingBy(List<T> list)
    {
        List<T> newList = Collections.emptyList();
        List<String> groupingBy = Collections.emptyList();
        groupingBy = queryable.getDynamicSql().asSelectable().getGroupByAsList();
        if (!list.isEmpty() && !groupingBy.isEmpty())
        {
            Class<T> returnedType = (Class<T>) list.get(0).getClass();
            Groupable<T, T> grouping = new GroupingBy<T, T>(groupingBy, returnedType, TransformableType.OBJECT);
            for (T row : list)
                grouping.classifier(row);
            
            newList = grouping.asList();
        }
        else
            newList = list;
        return newList;
    }
    
    /**
     * Make the conversion from object list to another T
     * @param list list of original values
     * @param returnType class type of {@code T}
     * @param <T> class type 
     * @return list of casted objects
     */
    @SuppressWarnings("unchecked")
    private List<T> cast(List<?> list, Class<?> returnType)// TODO test me case when jpa return array of objects (native query or select specific columns
    {
        List<T> castedList = null;
        Object firstValue = list.get(0);
        if (firstValue.getClass().getName().equals(returnType) || returnType == null)
        {
            castedList = (List<T>)list;
        }
        else if (firstValue instanceof Number)
        {
            Numerical factory = NumberFactory.getInstance(returnType.getName());
            castedList = new ArrayList<T>(list.size());
            List<?> listArray = (List<?>) list;
            for (Object o : listArray)
            {
                T casted = (T) factory.valueOf(o);
                castedList.add(casted);
            }
        }
        else if (firstValue instanceof String)
        {
            castedList = new ArrayList<T>(list.size());
            List<?> listArray = (List<?>) list;
            for (Object o : listArray)
            {
                castedList.add((T)String.valueOf(o));
            }
        }
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
        else if (Map.class.isAssignableFrom(returnType))
        {
            // TODO check match between columns size and object array
            String[] columns = ColumnParserFactory.getInstance().extract(queryable.query());
            castedList = new ArrayList<T>(list.size());
            List<Object[]> listArray = (List<Object[]>) list;
            for (Object[] tupla : listArray)
            {
                Map<String, Object> map = new HashMap<String, Object>();
                for(int i=0; i<tupla.length; i++)
                    map.put(columns[i], tupla[i]);
                
                castedList.add((T)map);
            } 
        }
        else if (returnType != null)
        {
            castedList = new ArrayList<T>(list.size());
            List<Object[]> listArray = (List<Object[]>) list;
            for (Object[] o : listArray)
            {
                ObjectProxy<?> proxy = ObjectProxyFactory.of(returnType);
                proxy.setConstructorArgs(o);
                T casted = (T)proxy.newInstance();
                castedList.add(casted);
            }            
        }
        if (castedList.size() != list.size())
            throw new RepositoryException("Wrong conversion type from List<Object[]> to List of [" + returnType + "]");
        return castedList;
    }
    
    @Override
    public void batch()
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public int execute()
    {
        int ret = 0;
        try
        {
            TimerKeeper.start();
            ret = query.executeUpdate();
            queryable.getDynamicSql().getStats().add(TimerKeeper.clear());
        }
        catch (Exception e)
        {
            queryable.getDynamicSql().getStats().add(e);
            handlerException.handle(e, e.getMessage());
        }
        return ret;
    }
    
    @Override
    public void close()
    {
        // TODO implements close for JpaStatementAdapter
        LOG.warn("close Statement Adapter not implemented for RepositoryJpa!");
    }
    
    @Override
    public void setFetchSize(int rows)
    {
        query.setMaxResults(rows);
    }
    
    private void log(String name, Object value)
    {
        if (SQLLOG.isDebugEnabled())
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index,
                    name, MASKING.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }
    
    private void log(int position, Object value)
    {
        String name = String.valueOf(position);
        if (SQLLOG.isDebugEnabled())
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index,
                    name, MASKING.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }
    
    private boolean hasGroupBy()
    {
        return !queryable.getDynamicSql().asSelectable().getGroupByAsList().isEmpty();
    }

}
