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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.QueryNameStrategy;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.transaction.Transactional;

/**
 * This an abstract class that implements Repository pattern using JPA 2. 
 * <br>
 * The EntityManager is injected by @PersistenceContext, must be configured at
 * META-INF/persistence.xml file.
 * 
 * @author Alisson Gomes
 *
 */
class RepositoryJpa implements RepositoryJpaExtend
{
    private static final Logger                       LOG                  = LoggerFactory.getLogger(RepositoryJpa.class);
    private static final Logger                       SQLLOG               = net.sf.jkniv.whinstone.jpa2.LoggerFactory.getLogger();
    private static final Assertable                   notNull              = AssertsFactory.getNotNull();
    private QueryNameStrategy                         xmlQueryName;
    private final static Map<String, PersistenceInfo> cachePersistenceInfo = new HashMap<String, PersistenceInfo>();
    private PersistenceInfo                           persistenceInfo;
    private JpaEmFactory                              emFactory;
    private SqlContext                                sqlContext;
    private boolean                                   isTraceEnabled;
    private boolean                                   isDebugEnabled;
    
    /**
     * Create a JPA repository using default persistence unit name, where
     * EntityManager is a container-managed persistence context.
     */
    RepositoryJpa()
    {
        this("");
    }
    
    /*
     * Create a JPA repository using a specific persistence unit name, where
     * EntityManager is a container-managed persistence context.
     * 
     * @param unitName
     *            Name of persistence unit name at from persistence.xml.
     *            <code>&lt;persistence-unit name="...</code>
     */
    RepositoryJpa(String sqlContextName)
    {
        notNull.verify(sqlContextName);
        this.sqlContext = SqlContextFactory.newInstance(sqlContextName);//, this.persistenceInfo.getProperties());
        String unitName = sqlContext.getName();
        this.persistenceInfo = getPersitenceInfo(unitName);
        sqlContext.getRepositoryConfig().add(this.persistenceInfo.getProperties());
        this.xmlQueryName = null;
        this.emFactory = new JpaEmFactoryJndi(sqlContextName);
        if (!emFactory.isActive())
            this.emFactory = new JpaEmFactorySEenv(unitName);
        
        this.init();
    }
    
    RepositoryJpa(Properties props)
    {
        notNull.verify(props);
        this.persistenceInfo = getPersitenceInfo("");
        this.sqlContext = SqlContextFactory.newInstance("/repository-sql.xml", this.persistenceInfo.getProperties());
        this.sqlContext.getRepositoryConfig().add(props);
        this.xmlQueryName = null;
        this.emFactory = new JpaEmFactoryJndi(persistenceInfo.getUnitName());
        if (!emFactory.isActive())
            this.emFactory = new JpaEmFactorySEenv(persistenceInfo.getUnitName());
        
        this.init();
    }
    
    RepositoryJpa(Properties props, SqlContext sqlContext)
    {
        notNull.verify(props, sqlContext);
        this.persistenceInfo = getPersitenceInfo("");
        this.sqlContext = sqlContext;
        this.sqlContext.getRepositoryConfig().add(props);
        this.sqlContext.getRepositoryConfig().add(this.persistenceInfo.getProperties());
        this.xmlQueryName = null;
        this.emFactory = new JpaEmFactoryJndi(persistenceInfo.getUnitName());
        if (!emFactory.isActive())
            this.emFactory = new JpaEmFactorySEenv(persistenceInfo.getUnitName());
        
        this.init();
    }
    
    RepositoryJpa(SqlContext sqlContext)
    {
        notNull.verify(sqlContext);
        this.persistenceInfo = getPersitenceInfo("");
        this.sqlContext = sqlContext;
        this.sqlContext.getRepositoryConfig().add(this.persistenceInfo.getProperties());
        this.xmlQueryName = null;
        this.emFactory = new JpaEmFactoryJndi(persistenceInfo.getUnitName());
        if (!emFactory.isActive())
            this.emFactory = new JpaEmFactorySEenv(persistenceInfo.getUnitName());
        
        this.init();
    }
    
    RepositoryJpa(String unitName, SqlContext sqlContext)
    {
        notNull.verify(unitName, sqlContext);
        this.persistenceInfo = getPersitenceInfo(unitName);
        this.sqlContext = sqlContext;
        this.sqlContext.getRepositoryConfig().add(this.persistenceInfo.getProperties());
        this.xmlQueryName = null;
        this.emFactory = new JpaEmFactoryJndi(unitName);
        if (!emFactory.isActive())
            this.emFactory = new JpaEmFactorySEenv(unitName);
        
        this.init();
    }
    
    /**
     * This constructor must be evict because <b>ISN'T thread-safe</b>, the programmer is responsible to
     * to provider a new instance of Repository for each thread.
     * <p>
     * Note: An entity manager must not be shared among multiple concurrently executing threads, as the entity
     * manager and persistence context are not required to be threadsafe. Entity managers must only be
     * accessed in a single-threaded manner.
     * </p>
     * @param em container-managed entity managers
     * @param sqlContext sql context loaded from {@code SqlContextFactory.newInstance(String)}
     */
    RepositoryJpa(EntityManager em, SqlContext sqlContext)
    {
        notNull.verify(em, sqlContext);
        this.persistenceInfo = PersistenceReader.getPersistenceInfo(sqlContext.getName());
        this.emFactory = new JpaEmFactoryHard(em);
        //this.sqlContext.getRepositoryConfig().add(this.persistenceInfo.getProperties());
        this.xmlQueryName = null;
        this.sqlContext = sqlContext;
        init();
    }
    
    private void init()
    {
        boolean showConfig = Boolean
                .valueOf(sqlContext.getRepositoryConfig().getProperty(RepositoryProperty.SHOW_CONFIG));
        String queryNameStrategyClass = sqlContext.getRepositoryConfig().getQueryNameStrategy();
        ObjectProxy<QueryNameStrategy> proxy = ObjectProxyFactory.newProxy(queryNameStrategyClass);
        this.xmlQueryName = proxy.newInstance();
        if (showConfig)
            showPersistenceConfig();
        isTraceEnabled = LOG.isTraceEnabled();
        isDebugEnabled = LOG.isDebugEnabled();
    }
    
    private EntityManager getEntityManager()
    {
        EntityManager em = emFactory.createEntityManager();
        LOG.trace("Lookup Entity Manager " + em);
        return em;
    }
    
    /*
    public AbstractRepository(EntityManagerFactory emf)
    {
        Assert.notNull(emf);
        this.initialized = false;
        this.showConfig = false;
        this.sqlNames = null;
        this.em = emf.createEntityManager();
        init();
    }*/
    
    //    @SuppressWarnings("unchecked")
    //    private void initSqlNameStrategy(String nameStrategy)
    //    {
    //        if (nameStrategy == null || "".equals(nameStrategy))
    //        {
    //            this.xmlQueryName = new SimpleQueryNameStrategy();
    //        }
    //        else
    //        {
    //            Class clazz = ReflectionUtils.forName(nameStrategy, SimpleQueryNameStrategy.class);
    //            this.xmlQueryName = (SimpleQueryNameStrategy) ReflectionUtils.newInstance(clazz);
    //        }
    //    }
    
    /**
     * Add a new Object at repository.
     * 
     * @param entity Entity object to persist
     */
    public <T> T add(T entity)
    {
        notNull.verify(entity);
        if (isTraceEnabled)
            LOG.trace("executing EntityManage.persist(" + entity.getClass().getName() + ")");
        EntityManager em = getEntityManager();
        em.persist(entity);
        return entity;
    }
    
    public int add(Queryable queryable)
    {
        if (isTraceEnabled)
            LOG.trace("executing add method with query [" + queryable.getName() + "]");
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.INSERT);
        isql.getValidateType().assertValidate(queryable.getParams());
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        int rowsAffected = executeUpdate(queryable, isql);
        if (isDebugEnabled)
            LOG.debug("{} records was affected by add [{}] command", rowsAffected, queryable.getName());
        return rowsAffected;
    }
    
    /**
     * Remove an existent object at repository.
     * 
     * @param entity Entity object to delete
     */
    public <T> int remove(T entity)
    {
        notNull.verify(entity);
        if (isTraceEnabled)
            LOG.trace("executing EntityManage.remove(" + entity.getClass().getName() + ")");
        EntityManager em = getEntityManager();
        em.remove(entity);
        if (isDebugEnabled)
            LOG.debug("1 record [{}] MUST BE affected by remove command", entity);
        
        return 1;
    }
    
    /**
     * Execute a query to remove one or many objects from repository.
     * 
     * @param queryable
     *            Query with parameters
     * @return Return the numbers of objects removed.
     * @throws IllegalArgumentException
     *             when the query is different from delete
     */
    public int remove(Queryable queryable)
    {
        if (isTraceEnabled)
            LOG.trace("executing remove method with query [" + queryable.getName() + "]");
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.DELETE);
        isql.getValidateType().assertValidate(queryable.getParams());
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        int rowsAffected = executeUpdate(queryable, isql);
        if (isDebugEnabled)
            LOG.debug("{} records was affected by remove [{}] command", rowsAffected, queryable.getName());
        return rowsAffected;
    }
    
    public boolean enrich(Queryable queryable) //FIXME test enrich
    {
        boolean enriched = false;
        List<Object> list = list(queryable);
        for (Object o : list)
        {
            ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(queryable.getParams());
            proxy.merge(o);
            enriched = true;
        }
        return enriched;
    }
    
    /**
     * Up date by query many objects.
     * 
     * @param queryable
     *            Query with parameters
     * @return Return the numbers of objects updated.
     * @throws IllegalArgumentException
     *             when the query is different from update sentence
     */
    public int update(Queryable queryable)
    {
        if (isTraceEnabled)
            LOG.trace("executing update method with query [" + queryable.getName() + "]");
        Sql isql = sqlContext.getQuery(queryable.getName());
        checkSqlType(isql, SqlType.UPDATE);
        isql.getValidateType().assertValidate(queryable.getParams());
        if (!queryable.isBoundSql())
            queryable.bind(isql);
        
        int rowsAffected = executeUpdate(queryable, isql);
        if (isDebugEnabled)
            LOG.debug("{} records was affected by update [{}] command", rowsAffected, queryable.getName());
        
        return rowsAffected;
    }
    
    /**
     * Up date the object value at repository.
     * 
     * @param entity Entity object to update
     */
    public <T> T update(T entity)
    {
        notNull.verify(entity);
        if (isTraceEnabled)
            LOG.trace("executing EntityManage.merge(" + entity.getClass().getName() + ")");
        EntityManager em = getEntityManager();
        T t = em.merge(entity);
        return t;
    }
    
    /**
     * Get one object instance from repository using a query with name "T.get",
     * where 'T' it's a generic type.
     * 
     * @param object
     *            Properties values of clause where to get the object.
     * @return Return the object that matches with query. A null reference is
     *         returned if the query no match anyone object.
     */
    public <T> T get(T object)
    {
        // TODO test me case null object
        // TODO test me case !null object
        // TODO test me case return null object
        // TODO test me case return !null object
        // TODO test me case return list of object
        notNull.verify(object);
        T ret = null;
        Queryable queryable = getDefaultQuery(object);
        if (isTraceEnabled)
            LOG.trace("executing get method with query [" + queryable.getName() + "]");
        
        QueryableJpaAdapter queryableJpaAdapter = QueryJpaFactory.build(getEntityManager(), sqlContext, queryable,
                null);
        ret = queryableJpaAdapter.getSingleResult();
        /*
        Query queryJpa = _getQueryJpa_(queryable);
        try
        {
            ret = (T) queryJpa.getSingleResult();
        }
        catch (javax.persistence.NoResultException returnNull)
        {
        }
        */
        return ret;
    }
    
    @Override
    public <T> T get(Class<T> returnType, Object object)
    {
        notNull.verify(object, returnType);
        T ret = null;
        Queryable queryable = getDefaultQuery(object);
        if (isTraceEnabled)
            LOG.trace("executing get method with query [{}] returning [{}]", queryable.getName(), returnType.getName());
        QueryableJpaAdapter queryableJpaAdapter = QueryJpaFactory.build(getEntityManager(), sqlContext, queryable,
                returnType);
        ret = queryableJpaAdapter.getSingleResult();
        return ret;
    }
    
    /**
     * Get one object instance from repository using a query.
     * 
     * @param queryable
     *            Query with parameters
     * @return Return the object that matches with query. A null reference is
     *         returned if the query no match anyone object.
     */
    public <T> T get(Queryable queryable)
    {
        notNull.verify(queryable);
        
        if (isTraceEnabled)
            LOG.trace("executing get method with query [" + queryable.getName() + "]");
        T ret = null;
        QueryableJpaAdapter queryableJpaAdapter = QueryJpaFactory.build(getEntityManager(), sqlContext, queryable,
                null);
        ret = queryableJpaAdapter.getSingleResult();
        return ret;
        
        /*
        Query queryJpa = _getQueryJpa_(queryable);
        try
        {
            ret = (T) queryJpa.getSingleResult();
        }
        catch (javax.persistence.NoResultException nre)
        {
            // null reference is returned
        }
        return ret;
        */
    }
    
    public <T> T get(Queryable queryable, Class<T> returnType)
    {
        notNull.verify(queryable, returnType);
        if (isTraceEnabled)
            LOG.trace("executing get method with query [" + queryable.getName() + "]");
        
        T ret = null;
        QueryableJpaAdapter queryableJpaAdapter = QueryJpaFactory.build(getEntityManager(), sqlContext, queryable,
                returnType);
        ret = queryableJpaAdapter.getSingleResult();
        return ret;
        /*
        Object entity = get(queryable);
        T answer = null;
        if (returnType.isInstance(entity))
            answer = (T) entity;
        else
        {
            ObjectProxy<T> proxy = ObjectProxyFactory.newProxy(returnType);
            //FIXME implements get another specific returnType
            throw new UnsupportedOperationException("Cannot iterate over Jpa query. Not implemented.");
        }
        return answer;
        */
    }
    
    public <T, R> T get(Queryable queryName, ResultRow<T, R> customResultRow)
    {
        throw new UnsupportedOperationException("Cannot iterate over Jpa query. Not implemented.");
    }
    
    /*
     * Get a set of objects from repository using a query with name "T.list",
     * where 'T' it's a generic type.
     * 
     * @return Return a set of object that matches with query. A empty list is
     *         returned if the query no match anyone object.
     *
    public <T> List<T> list()
    {
        init();
        List<T> ret = null;
        IQuery q = getDefaultQuery(null);
        LOG.trace("executing list method with query [" + q.getName() + "]");
        Query queryJpa = getQueryJpa(q);
        ret = queryJpa.getResultList();
        if (ret == null)
            ret = new ArrayList<T>();
        LOG.trace("The query [" + q.getName() + "] found " + ret.size() + " objects");
        return ret;
    }*/
    
    /**
     * Get a set of objects from repository using a query.
     * 
     * @param queryable
     *            Query with parameters
     * @return Return a set of object that matches with query. A empty list is
     *         returned if the query no match anyone object.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> list(Queryable queryable)
    {
        if (isTraceEnabled)
            LOG.trace("executing list method with query [" + queryable.getName() + "]");
        
        QueryableJpaAdapter queryableJpaAdapter = QueryJpaFactory.build(getEntityManager(), sqlContext, queryable,
                null);
        
        List<T> ret = queryableJpaAdapter.getResultList();
        
        return ret;
    }
    
    public <T, R> List<T> list(Queryable queryName, ResultRow<T, R> customResultRow)
    {
        notNull.verify(customResultRow);
        throw new UnsupportedOperationException("Cannot iterate over Jpa query. Not implemented.");
    }
    
    public <T> List<T> list(Queryable queryable, Class<T> returnType)
    {
        notNull.verify(returnType);
        if (isTraceEnabled)
            LOG.trace("executing list method with query [" + queryable.getName() + "]");
        //throw new UnsupportedOperationException("Not implemented yet. list with Class<T> returnType.");
        QueryableJpaAdapter queryableJpaAdapter = QueryJpaFactory.build(getEntityManager(), sqlContext, queryable,
                returnType);
        
        List<T> ret = queryableJpaAdapter.getResultList();
        return ret;
        //        List<T> ret = null;
        //        SelectTag isql = null;
        //        Query queryJpa = null;
        //        List<String>  groupingBy = Collections.emptyList();
        //        int totalBeforeGroup = 0;
        //        boolean containQuery = sqlContext.containsQuery(queryable.getName());
        //        if (containQuery)
        //        {
        //            isql = (SelectTag)sqlContext.getQuery(queryable.getName());
        //            isql.getValidateType().assertValidate(queryable.getParams());
        //            queryJpa = QueryFactory.newQuery(isql, getEntityManager(), queryable, sqlLogger);
        //            groupingBy = isql.getGroupByAsList();
        //        }
        //        else
        //        {
        //            // read from orm.xml named query
        //            queryJpa = QueryFactory.newNamedQuery(getEntityManager(), queryable);
        //        }
        //        
        //        ret = queryJpa.getResultList();
        //        if (containQuery && isql.getLanguageType() == LanguageType.NATIVE && isql.getReturnType() != null)
        //        {
        //            //ret = cast((List<Object[]>) ret, isql.getReturnType());
        //        }
        //        
        //        if (containQuery)
        //        {
        //            setTotalObjectsOfQuery(queryable, isql);// FIXME implements count of total objects
        //            if (queryable.getTotal() == -1)
        //                queryable.setTotal(Long.valueOf(ret.size()));
        //        }
        //        else if (ret != null)
        //            queryable.setTotal(Long.valueOf(ret.size()));
        //        else
        //            ret = new ArrayList<T>();
        //        
        //        totalBeforeGroup = ret.size();
        //        if (!groupingBy.isEmpty() && !ret.isEmpty())
        //        {
        //            Class<T> returnedType = (Class<T>) ret.get(0).getClass();
        //            Groupable<T, T> grouping = new GroupingBy<T, T>(groupingBy, returnedType, TransformableType.OBJECT);
        //            for(T  row : ret)
        //                grouping.classifier(row);
        //
        //            ret = grouping.asList();
        //        }
        //        if (isDebugEnabled)
        //            LOG.debug("Executed [{}] query, {}/{} rows fetched transformed to -> {}", queryable.getName(), totalBeforeGroup, queryable.getTotal(), ret.size());
        //
        //        return ret;
    }
    
    public <T> T scalar(Queryable queryable)
    {
        if (isTraceEnabled)
            LOG.trace("executing get method with query [" + queryable.getName() + "]");
        queryable.scalar();
        T ret = null;
        
        QueryableJpaAdapter queryableJpaAdapter = QueryJpaFactory.build(getEntityManager(), sqlContext, queryable,
                null);
        ret = (T) queryableJpaAdapter.getSingleResult();
        
        if (isDebugEnabled)
            LOG.debug("Executed scalar query [{}] retrieving [{}] type of [{}]", queryable.getName(), ret,
                    (ret != null ? ret.getClass().getName() : "NULL"));
        
        return ret;
    }
    
    public Transactional getTransaction()
    {
        return emFactory.getTransaction();
    }
    
    public void close()
    {
        // FIXME implements Repository.close()
        cachePersistenceInfo.clear();
        sqlContext.close();
    }
    
    /**
     * Command to execute SQL statements that are in the buffer.
     */
    public void flush()
    {
        if (isTraceEnabled)
            LOG.trace("executing EntityManager flush");
        getEntityManager().flush();
    }
    
    @Override
    public boolean containsQuery(String name)
    {
        return sqlContext.containsQuery(name);
    }

    
    public CriteriaBuilder getCriteriaBuilder()
    {
        return getEntityManager().getCriteriaBuilder();
    }
    
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery)
    {
        return getEntityManager().createQuery(criteriaQuery);
    }
    
    private int executeUpdate(Queryable queryable, Sql isql)
    {
        int rowsAffected = 0;
        EntityManager em = getEntityManager();
        if (queryable.isTypeOfCollectionFromPojo() || queryable.isTypeOfCollectionFromMap()
                || queryable.isTypeOfCollectionFromArray() || queryable.isTypeOfArrayFromPojo())
        {
            Iterator<Object> it = queryable.iterator();
            while (it.hasNext())
            {
                Queryable queryableIt = QueryFactory.of(queryable.getName(), it.next(), queryable.getOffset(),
                        queryable.getMax());
                QueryableJpaAdapter queryableJpaAdapter = QueryJpaFactory.build(em, sqlContext, queryableIt, null);
                rowsAffected += queryableJpaAdapter.executeUpdate();
            }
        }
        else
        {
            QueryableJpaAdapter queryableJpaAdapter = QueryJpaFactory.build(em, sqlContext, queryable, null);
            rowsAffected = queryableJpaAdapter.executeUpdate();
        }
        return rowsAffected;
    }
    
    /*
     * Verify if query is paginated, that must return the total records of the
     * query.
     * 
     * @param query The query
     * @return Return true if the query is paginated, false otherwise.
     *
    private boolean isCountObjects(Queryable query)
    {
        return (query.getMax() < Integer.MAX_VALUE);
    }
    */
    
    private void setTotalObjectsOfQuery(Queryable queryable, Sql isql)
    {
        
        Number total = 0L;
        if (queryable.isPaging())
        {
            /*
            Query queryJpa = getQueryForAutoCount(queryable, isql);
            try
            {
                total = (Number) queryJpa.getSingleResult();
                if (total != null)
                    queryable.setTotal(total.longValue());
            }
            catch (NoResultException e)
            {
                queryable.setTotal(0L);
            }
            */
        }
    }
    
    /*
    private Query _getQueryJpa_(Queryable queryable)
    {
        Query queryJpa = null;
        EntityManager em = getEntityManager();
        ISql isql = null;
        try
        {
            isql = sqlContext.getQuery(queryable.getName());
            checkSqlCommandType(isql, SqlCommandType.SELECT);
            isql.getValidateType().assertValidate(queryable.getParams());
            queryJpa = QueryFactory.newQuery(isql, em, queryable, sqlLogger);
        }
        catch (QueryNotFoundException e)
        {
            queryJpa = QueryFactory.newNamedQuery(em, queryable);
        }
        return queryJpa;
    }
    */
    /**
     * Create a default instance of Queryable object to execute list and get
     * methods.
     * 
     * @param object
     *            Parameter of the Queryable.
     * @return Return a new instance of query with parameters.
     */
    private Queryable getDefaultQuery(Object object)
    {
        String queryName = null;
        if (object != null)
            queryName = xmlQueryName.toGetName(object);
        else
            queryName = xmlQueryName.toListName(getGenericType());
        
        return QueryFactory.of(queryName, object);
    }
    
    /**
     * Return a new instance of generic type object.
     * 
     * @return Return a new instance from Generic type T. At Repository<Book>,
     *         this sample it's Book.
     */
    private Object getGenericType()
    {
        Object obj = null;
        try
        {
            obj = getClass().newInstance();
        }
        catch (Exception e)
        {
            // InstantiationException, IllegalAccessException
            throw new RuntimeException("Cannot make a new instance of " + getClass().getName()
                    + ". Make sure that repository implementation there is public default constructor.");
        }
        return obj;
    }
    
    void showPersistenceConfig()
    {
        EntityManager em = getEntityManager();
        Map<String, Object> map = new TreeMap<String, Object>(em.getEntityManagerFactory().getProperties());
        // getEntityManager().getDelegate();
        map.putAll(em.getProperties());
        if (SQLLOG.isInfoEnabled()) 
        {
            DataMasking masking = this.sqlContext.getRepositoryConfig().getDataMasking();
            for (String s : map.keySet())
                SQLLOG.info("{}={}",s , masking.mask(s, map.get(s)));
        }
    }
    
    private PersistenceInfo getPersitenceInfo(String unitName)
    {
        PersistenceInfo pInfo = RepositoryJpa.cachePersistenceInfo.get(unitName);
        if (pInfo == null)
        {
            if ("".equals(unitName))
                pInfo = PersistenceReader.getPersistenceInfo();
            else
                pInfo = PersistenceReader.getPersistenceInfo(unitName);
            RepositoryJpa.cachePersistenceInfo.put(unitName, pInfo);
        }
        return pInfo;
    }
    
    private void checkSqlType(Sql isql, SqlType expected)
    {
        if (isql.getSqlType() != expected)
            throw new IllegalArgumentException("Cannot execute sql [" + isql.getName() + "] as [" + isql.getSqlType()
                    + "], exptected is " + expected);
    }
    
    //private Object[] getSimpleProperties(Object[] values) {
    //    
    //}
    
    /*
    private void persistenceConfig()
    {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("META-INF/persistence.xml");
    }
    */
    /*
     * According JPA specification the entity manager an persistence context are
     * not required to be thread-safe. This way the entity manager instance is recovered by JNDI.
     * 
     * @return
     *
    private EntityManager getEntityManager()
    {
        // EntityManager em = null;
        try
        {
            if (em == null)
                em = (EntityManager) ctx.lookup("java:comp/env/persistence/whinstone");
            System.out.println("EntityManager is " + em);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.error("Cannot lookup [" + this.persistenceUnitName + "]", e);// TODO
                                                                             // Auto-generated
                                                                             // catch
                                                                             // block
        }
        return em;
    }
    */
    
}
