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

import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.LockTimeoutException;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.QueryTimeoutException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.NonUniqueResultException;
import net.sf.jkniv.sqlegance.QueryNameStrategy;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.SqlType;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.whinstone.CommandAdapter;
import net.sf.jkniv.whinstone.CommandHandler;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.jpa2.dialect.JpaDialect;
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
    private static final Logger                       LOG                  = LoggerFactory
            .getLogger(RepositoryJpa.class);
    private static final Logger                       SQLLOG               = net.sf.jkniv.whinstone.jpa2.LoggerFactory
            .getLogger();
    private static final Assertable                   NOT_NULL             = AssertsFactory.getNotNull();
    private QueryNameStrategy                         strategyQueryName;
    private final static Map<String, PersistenceInfo> cachePersistenceInfo = new HashMap<String, PersistenceInfo>();
    private PersistenceInfo                           persistenceInfo;
    private JpaEmFactory                              emFactory;
    private SqlContext                                sqlContext;
    private boolean                                   isTraceEnabled;
    private boolean                                   isDebugEnabled;
    private HandleableException                       handlerException;
    private CommandAdapter                            cmdAdapter;
    
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
        NOT_NULL.verify(sqlContextName);
        this.sqlContext = SqlContextFactory.newInstance(sqlContextName);//, this.persistenceInfo.getProperties());
        String unitName = sqlContext.getName();
        this.persistenceInfo = getPersitenceInfo(unitName);
        sqlContext.getRepositoryConfig().add(this.persistenceInfo.getProperties());
        this.strategyQueryName = null;
        //this.emFactory = new JpaEmFactoryJndi(sqlContextName);
        //if (!emFactory.isActive())
        ///    this.emFactory = new JpaEmFactorySEenv(unitName);
        this.init();
    }
    
    RepositoryJpa(Properties props)
    {
        NOT_NULL.verify(props);
        this.persistenceInfo = getPersitenceInfo("");
        this.sqlContext = SqlContextFactory.newInstance("/repository-sql.xml", this.persistenceInfo.getProperties());
        this.sqlContext.getRepositoryConfig().add(props);
        this.strategyQueryName = null;
        //this.emFactory = new JpaEmFactoryJndi(persistenceInfo.getUnitName());
        //if (!emFactory.isActive())
        //    this.emFactory = new JpaEmFactorySEenv(persistenceInfo.getUnitName());
        
        this.init();
    }
    
    RepositoryJpa(Properties props, SqlContext sqlContext)
    {
        NOT_NULL.verify(props, sqlContext);
        this.persistenceInfo = getPersitenceInfo("");
        this.sqlContext = sqlContext;
        this.sqlContext.getRepositoryConfig().add(props);
        this.sqlContext.getRepositoryConfig().add(this.persistenceInfo.getProperties());
        this.strategyQueryName = null;
        //this.emFactory = new JpaEmFactoryJndi(persistenceInfo.getUnitName());
        //if (!emFactory.isActive())
        //    this.emFactory = new JpaEmFactorySEenv(persistenceInfo.getUnitName());
        this.init();
    }
    
    RepositoryJpa(SqlContext sqlContext)
    {
        NOT_NULL.verify(sqlContext);
        this.persistenceInfo = getPersitenceInfo("");
        this.sqlContext = sqlContext;
        this.sqlContext.getRepositoryConfig().add(this.persistenceInfo.getProperties());
        this.strategyQueryName = null;
        //this.emFactory = new JpaEmFactoryJndi(persistenceInfo.getUnitName());
        //if (!emFactory.isActive())
        //    this.emFactory = new JpaEmFactorySEenv(persistenceInfo.getUnitName());
        this.init();
    }
    
    RepositoryJpa(String unitName, SqlContext sqlContext)
    {
        NOT_NULL.verify(unitName, sqlContext);
        this.persistenceInfo = getPersitenceInfo(unitName);
        this.sqlContext = sqlContext;
        this.sqlContext.getRepositoryConfig().add(this.persistenceInfo.getProperties());
        this.strategyQueryName = null;
        //this.emFactory = new JpaEmFactoryJndi(unitName);
        //if (!emFactory.isActive())
        //    this.emFactory = new JpaEmFactorySEenv(unitName);
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
        NOT_NULL.verify(em, sqlContext);
        this.persistenceInfo = PersistenceReader.getPersistenceInfo(sqlContext.getName());
        //this.emFactory = new JpaEmFactoryHard(em);
        //this.sqlContext.getRepositoryConfig().add(this.persistenceInfo.getProperties());
        this.strategyQueryName = null;
        this.sqlContext = sqlContext;
        init();
    }
    
    private void init()
    {
        boolean showConfig = Boolean
                .valueOf(sqlContext.getRepositoryConfig().getProperty(RepositoryProperty.SHOW_CONFIG));
        String queryNameStrategyClass = sqlContext.getRepositoryConfig().getQueryNameStrategy();
        ObjectProxy<QueryNameStrategy> proxy = ObjectProxyFactory.newProxy(queryNameStrategyClass);
        this.strategyQueryName = proxy.newInstance();
        if (showConfig)
            showPersistenceConfig();
        isTraceEnabled = LOG.isTraceEnabled();
        isDebugEnabled = LOG.isDebugEnabled();
        this.sqlContext.getRepositoryConfig().add(RepositoryProperty.SQL_DIALECT.key(), JpaDialect.class.getName());
        this.sqlContext.setSqlDialect(this.sqlContext.getRepositoryConfig().getSqlDialect());
        configureEntityManagerFactory();
        configureHandlerException();
        this.cmdAdapter = new JpaCommandAdapter(this.sqlContext.getName(), emFactory, handlerException);
    }
    
    private EntityManager getEntityManager()
    {
        EntityManager em = emFactory.createEntityManager();
        LOG.trace("Lookup Entity Manager " + em);
        return em;
    }
    
    private void configureEntityManagerFactory()
    {
        this.emFactory = new JpaEmFactoryJndi(persistenceInfo.getUnitName());
        if (!emFactory.isActive())
            this.emFactory = new JpaEmFactorySEenv(persistenceInfo.getUnitName());
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
    @Override
    public <T> T add(T entity) // FIXME CommandHandler must be used (refactoring)
    {
        NOT_NULL.verify(entity);
        if (isTraceEnabled)
            LOG.trace("executing EntityManage.persist(" + entity.getClass().getName() + ")");
        EntityManager em = getEntityManager();
        em.persist(entity);
        return entity;
    }
    
    @Override
    public int add(Queryable queryable)// FIXME CommandHandler must be used (refactoring)
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
    @Override
    public <T> int remove(T entity)// FIXME CommandHandler must be used (refactoring)
    {
        NOT_NULL.verify(entity);
        if (isTraceEnabled)
            LOG.trace("executing EntityManage.remove(" + entity.getClass().getName() + ")");
        EntityManager em = getEntityManager();
        em.remove(entity);
        if (isDebugEnabled)
            LOG.debug("1 record [{}] MUST BE affected by remove command", entity);
        
        return Statement.SUCCESS_NO_INFO;
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
    @Override
    public int remove(Queryable queryable)// FIXME CommandHandler must be used (refactoring)
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
    @Override
    public int update(Queryable queryable)// FIXME CommandHandler must be used (refactoring)
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
    @Override
    public <T> T update(T entity)// FIXME CommandHandler must be used (refactoring)
    {
        NOT_NULL.verify(entity);
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
     * @param entity
     *            Properties values of clause where to get the object.
     * @return Return the object that matches with query. A null reference is
     *         returned if the query no match anyone object.
     */
    public <T> T get(T entity)
    {
        NOT_NULL.verify(entity);
        String queryName = this.strategyQueryName.toGetName(entity);
        Queryable queryable = QueryFactory.of(queryName, entity.getClass(), entity);
        T ret = (T) handleGet(queryable, null);
        return ret;
        /*
        // TODO test me case null object
        // TODO test me case !null object
        // TODO test me case return null object
        // TODO test me case return !null object
        // TODO test me case return list of object
        NOT_NULL.verify(object);
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
        return ret;
         */
    }
    
    @Override
    public <T> T get(Class<T> returnType, Object entity)
    {
        NOT_NULL.verify(entity);
        String queryName = this.strategyQueryName.toGetName(entity);
        Queryable queryable = QueryFactory.of(queryName, returnType, entity);
        T ret = (T) handleGet(queryable, null);
        return ret;
        /*
        NOT_NULL.verify(object, returnType);
        T ret = null;
        Queryable queryable = getDefaultQuery(object);
        if (isTraceEnabled)
            LOG.trace("executing get method with query [{}] returning [{}]", queryable.getName(), returnType.getName());
        QueryableJpaAdapter queryableJpaAdapter = QueryJpaFactory.build(getEntityManager(), sqlContext, queryable,
                returnType);
        ret = queryableJpaAdapter.getSingleResult();
        return ret;
        */
    }
    
    /**
     * Get one object instance from repository using a query.
     * 
     * @param queryable
     *            Query with parameters
     * @return Return the object that matches with query. A null reference is
     *         returned if the query no match anyone object.
     */
    public <T> T __get(Queryable queryable)
    {
        NOT_NULL.verify(queryable);
        
        if (isTraceEnabled)
            LOG.trace("executing get method with query [" + queryable.getName() + "]");
        T ret = null;
        QueryableJpaAdapter queryableJpaAdapter = QueryJpaFactory.build(getEntityManager(), sqlContext, queryable,
                null);
        ret = queryableJpaAdapter.getSingleResult();
        return ret;
    }
    
    @Override
    public <T> T get(Queryable queryable)
    {
        NOT_NULL.verify(queryable);
        T ret = handleGet(queryable, null);
        return ret;
    }
    
    @Override
    public <T> T get(Queryable queryable, Class<T> returnType)
    {
        NOT_NULL.verify(queryable, returnType);
        Queryable queryableClone = QueryFactory.clone(queryable, returnType);
        T ret = handleGet(queryableClone, null);
        queryable.setTotal(queryableClone.getTotal());
        return ret;
        /*
        NOT_NULL.verify(queryable, returnType);
        if (isTraceEnabled)
            LOG.trace("executing get method with query [" + queryable.getName() + "]");
        
        T ret = null;
        QueryableJpaAdapter queryableJpaAdapter = QueryJpaFactory.build(getEntityManager(), sqlContext, queryable,
                returnType);
        ret = queryableJpaAdapter.getSingleResult();
        return ret;
        */
    }
    
    @Override
    public <T, R> T get(Queryable queryable, ResultRow<T, R> customResultRow)
    {
        return handleGet(queryable, customResultRow);
        /*
        throw new UnsupportedOperationException("Cannot iterate over Jpa query. Not implemented.");
         */
    }
    
    /**
     * Get a set of objects from repository using a query.
     * 
     * @param queryable
     *            Query with parameters
     * @return Return a set of object that matches with query. A empty list is
     *         returned if the query no match anyone object.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> list(Queryable queryable)
    {
        return handleList(queryable, null);

        /*
        if (isTraceEnabled)
            LOG.trace("executing list method with query [" + queryable.getName() + "]");
        
        QueryableJpaAdapter queryableJpaAdapter = QueryJpaFactory.build(getEntityManager(), sqlContext, queryable,
                null);
        
        List<T> ret = queryableJpaAdapter.getResultList();
        
        return ret;
        */
    }
    
    @Override
    public <T, R> List<T> list(Queryable queryable, ResultRow<T, R> customResultRow)
    {
        return handleList(queryable, customResultRow);
        /*
        NOT_NULL.verify(customResultRow);
        throw new UnsupportedOperationException("Cannot iterate over Jpa query. Not implemented.");
        */
    }
    
    @Override
    public <T> List<T> list(Queryable queryable, Class<T> overloadReturnType)
    {
        List<T> list = Collections.emptyList();
        Queryable queryableClone = queryable;
        if (overloadReturnType != null)
            queryableClone = QueryFactory.clone(queryable, overloadReturnType);
        list = handleList(queryableClone, null);
        queryable.setTotal(queryableClone.getTotal());
        return list;
        /*
        NOT_NULL.verify(overloadReturnType);
        if (isTraceEnabled)
            LOG.trace("executing list method with query [" + queryable.getName() + "]");
        //throw new UnsupportedOperationException("Not implemented yet. list with Class<T> returnType.");
        QueryableJpaAdapter queryableJpaAdapter = QueryJpaFactory.build(getEntityManager(), sqlContext, queryable,
                overloadReturnType);
        
        List<T> ret = queryableJpaAdapter.getResultList();
        return ret;
        */
    }
    
    @Override
    public <T> T scalar(Queryable queryable)
    {
        NOT_NULL.verify(queryable);
        queryable.scalar();
        T ret = handleGet(queryable, null);
        return ret;
        /*
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
        */
    }
    
    private <T, R> T handleGet(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        T ret = null;
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = new SelectHandler(this.cmdAdapter);
        List<T> list = handler
                .with(queryable)
                .with(sql)
                .checkSqlType(SqlType.SELECT)
                .with(handlerException)
                .with(overloadResultRow)
                .run();
        if (list.size() > 1)
            throw new NonUniqueResultException("No unique result for query [" + queryable.getName() + "] with params ["+ queryable.getParams() + "result fetch ["+list.size()+"] rows, Repository.get(..) method must return just one row]");
            //throw new NonUniqueResultException("No unique result for query [" + queryable.getName() + "] with params ["+ queryable.getParams() + "]");
            //throw new NonUniqueResultException("Query ["+queryable.getName()+"] result fetch ["+listGrouped.size()+"] rows, Repository.get(..) method must return just one row");

        else if (list.size() == 1)
            ret = list.get(0);
        
        return ret;
    }
    
    private <T, R> List<T> handleList(Queryable queryable, ResultRow<T, R> overloadResultRow)
    {
        Sql sql = sqlContext.getQuery(queryable.getName());
        CommandHandler handler = new SelectHandler(this.cmdAdapter);
        List<T> list = handler
                .with(queryable)
                .with(sql)
                .checkSqlType(SqlType.SELECT)
                .with(handlerException)
                .with(overloadResultRow).run();
        return list;
    }
    
    @Override
    public Transactional getTransaction()
    {
        return emFactory.getTransaction();
    }
    
    @Override
    public void close()
    {
        // FIXME implements Repository.close()
        cachePersistenceInfo.clear();
        sqlContext.close();
    }
    
    /**
     * Command to execute SQL statements that are in the buffer.
     */
    @Override
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
    
    @Override
    public CriteriaBuilder getCriteriaBuilder()
    {
        return getEntityManager().getCriteriaBuilder();
    }
    
    @Override
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
            queryName = strategyQueryName.toGetName(object);
        else
            queryName = strategyQueryName.toListName(getGenericType());
        
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
                SQLLOG.info("{}={}", s, masking.mask(s, map.get(s)));
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
    
    private void configureHandlerException()
    {
        /*
         * TODO exception design JPA remove method
         * IllegalArgumentException - if the instance is not an entity or is a detached entity
         * TransactionRequiredException - if invoked on a container-managed entity manager of 
         * type PersistenceContextType.TRANSACTION and there is no transaction
         */
        
        
        this.handlerException = new HandlerException(RepositoryException.class, "JPA Error cannot execute SQL: %s");
        // JPA 2 throws Exceptions
        /* @throws IllegalStateException if called for a Java
        * Persistence query language UPDATE or DELETE statement */
        this.handlerException.config(IllegalStateException.class,
                "Cannot call SELECT statement with UPDATE or DELETE: %s");
        
        /* @throws QueryTimeoutException if the query execution exceeds
        * the query timeout value set and only the statement is rolled back */
        this.handlerException.config(QueryTimeoutException.class,
                "A query times out and only the statement is rolled back, if a current transaction is active, will be not be marked for rollback: %s");
        
        /* @throws TransactionRequiredException if a lock mode other than NONE has been been set
        *  and there is no transaction or the persistence context has not been joined to the transaction */
        this.handlerException.config(TransactionRequiredException.class,
                "A transaction is required but is not active: %s");
        
        /* @throws PessimisticLockException if pessimistic locking fails and the transaction is rolled back */
        this.handlerException.config(PessimisticLockException.class,
                "A pessimistic locking conflict occurs if a current transaction is active, will be not be marked for rollback: %s");
        
        /* @throws LockTimeoutException if pessimistic locking fails and only the statement is rolled back */
        this.handlerException.config(LockTimeoutException.class,
                "A pessimistic locking conflict occurs if a current transaction is active, will be not be marked for rollback: %s");
        //pessimistic locking
        /* @throws PersistenceException if the query execution exceeds the query timeout */
        this.handlerException.config(PersistenceException.class, "The query %s exceeds the timeout value: %s");
    }
}
