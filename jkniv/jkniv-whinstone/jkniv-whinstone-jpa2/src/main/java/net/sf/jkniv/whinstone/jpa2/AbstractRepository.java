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

import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.DotQueryNameStrategy;
import net.sf.jkniv.sqlegance.IQuery;
import net.sf.jkniv.sqlegance.IRepository;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.QueryFactory;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.builder.XmlBuilderSql;

/**
 * This an abstract class that implements Repository pattern using JPA 2. 
 * <br>
 * The EntityManager is injected by @PersistenceContext, must be configured at
 * META-INF/persistence.xml file.
 * 
 * @author Alisson Gomes
 * @param <T>
 *            Generic object mapping with @javax.persistence.Entity
 *
 * deprecated will be removed in the future. You must use IRepositorySingle as repository contract.
 */
@Deprecated
class AbstractRepository<T> implements IRepository<T>
{
    private Logger               LOG            = LoggerFactory.getLogger(getClass());
    private DotQueryNameStrategy xmlQueryName;
    private static final Pattern patternOrderBy = Pattern.compile("order\\s+by\\s+[\\w|\\W|\\s|\\S]*",
                                                        Pattern.CASE_INSENSITIVE);
    private boolean              initialized;
    private boolean              showConfig;
    private PersistenceInfo      persistenceInfo;
    private String               persistenceUnitName;
    private Class<T>             genericType;
    private EntityManager        em;
    
    /**
     * Create a JPA repository using default persistence unit name, where
     * EntityManager is a container-managed persistence context.
     */
    public AbstractRepository()
    {
        this("");
    }
    
    /**
     * Create a JPA repository using a specific persistence unit name, where
     * EntityManager is a container-managed persistence context.
     * 
     * @param persistenceUnitName
     *            Name of persistence unit name at from persistence.xml.
     *            <code>&lt;persistence-unit name="...</code>
     */
    @SuppressWarnings("unchecked")
    public AbstractRepository(String persistenceUnitName)
    {
        this.persistenceUnitName = persistenceUnitName;
        // PersistenceInfo persistenceInfo = PersistenceReader.getProvider(persistenceUnitName);
        // PersistenceProvider provider = (PersistenceProvider)
        // ReflectionUtil.newInstance(persistenceInfo.provider);
        // this.em = provider.createEntityManagerFactory(persistenceInfo.unitName, null).createEntityManager();
        this.initialized = false;
        this.showConfig = false;
        this.xmlQueryName = null;
        this.genericType = (Class<T>) getGenericType().getClass();
        //try
        //{
        // FIXME Configuring the JNDI Service Provider
        // Hashtable env = new Hashtable();
        // env.put(Context.INITIAL_CONTEXT_FACTORY,
        // "com.sun.jndi.ldap.LdapCtxFactory");
        // env.put(Context.PROVIDER_URL, "localhost:1099");
        // env.put(Context.SECURITY_PRINCIPAL, "joeuser");
        // env.put(Context.SECURITY_CREDENTIALS, "joepassword");
        //this.ctx = new InitialContext();// .lookup("java:comp/env");
        //em = (EntityManager) ctx.lookup("java:comp/env/persistence/whinstone");
        //}
        //catch (NamingException e)
        //{
        //    LOG.error("Cannot initialize initial context", e);
        //}
        init();
    }
    
    public AbstractRepository(EntityManager em)
    {
        Assert.notNull(em);
        this.initialized = false;
        this.showConfig = false;
        this.xmlQueryName = null;
        this.em = em;
        init();
    }
    
    private EntityManager getEntityManager()
    {
        Context envCtx;
        EntityManager emJndi = null;
        if (this.em != null)
            return this.em;
        
        try
        {
            envCtx = (Context) new InitialContext().lookup("java:comp/env");
            emJndi = (EntityManager) envCtx.lookup("persistence/" + persistenceUnitName);
        }
        catch (NamingException e)
        {
            throw new RuntimeException(e);
            // TODO Auto-generated catch block
        }
        return emJndi;
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
    
    private void init()
    {
        if (!initialized)
        {
            if (persistenceUnitName == null || "".equals(persistenceUnitName.trim()))
            {
                persistenceInfo = PersistenceReader.getPersistenceInfo();
                this.persistenceUnitName = persistenceInfo.getUnitName();
            }
            else
                this.persistenceInfo = PersistenceReader.getPersistenceInfo(persistenceUnitName);
            
            String classname = persistenceInfo.getPropertyValue(RepositoryProperty.QUERY_NAME_STRATEGY.key());
            showConfig = "true".equalsIgnoreCase(persistenceInfo.getPropertyValue(IRepository.PROPERTY_SHOW_CONFIG));
            initSqlNameStrategy(classname);
            initialized = true;
            if (showConfig)
                showPersistenceConfig();
        }
    }
    
    private void initSqlNameStrategy(String nameStrategy)
    {
        if (nameStrategy == null || "".equals(nameStrategy))
        {
            this.xmlQueryName = new DotQueryNameStrategy();
        }
        else
        {
            try
            {
                this.xmlQueryName = (DotQueryNameStrategy) Class.forName(nameStrategy).newInstance();
            }
            catch (Exception e)
            {
                // InstantiationException, IllegalAccessException,
                // ClassNotFoundException
                this.xmlQueryName = new DotQueryNameStrategy();
            }
        }
    }
    
    /**
     * Add a new Object at repository.
     * 
     * @param object
     */
    public void add(T object)
    {
        getEntityManager().persist(object);
        LOG.trace("executed add method");
    }
    
    /**
     * Remove an existent object at repository.
     * 
     * @param object
     */
    public void remove(T object)
    {
        getEntityManager().remove(object);
        LOG.trace("executed remove method");
    }
    
    /**
     * Execute a query to remove one or many objects from repository.
     * 
     * @param query
     *            Query with parameters
     * @return Return the numbers of objects removed.
     * @throws IllegalArgumentException
     *             when the query is different from delete
     */
    public int remove(IQuery query)
    {
        LOG.trace("executing remove method with query [" + query.getName() + "]");
        int ret = -1;
        /*
         * ISql isql = XmlBuilderSql.getQuery(query.getName()); if
         * (isql.getSqlCommandType() != SqlCommandType.DELETE) throw new
         * IllegalArgumentException
         * ("Cannot execute an remove when the query is different from delete");
         * Query queryJpa = QueryBuilder.newQuery(isql, em, query);
         */
        Query queryJpa = getQueryJpa(query);
        ret = queryJpa.executeUpdate();
        return ret;
    }
    
    /**
     * Up date the object value at repository.
     * 
     * @param entity
     */
    public T update(T entity)
    {
        LOG.trace("executing update method with object [" + entity + "]");
        T t = getEntityManager().merge(entity);
        return t;
    }
    
    /**
     * Up date by query many objects.
     * 
     * @param query
     *            Query with parameters
     * @return Return the numbers of objects updated.
     * @throws IllegalArgumentException
     *             when the query is different from update sentence
     */
    public int update(IQuery query)
    {
        LOG.trace("executing update method with query [" + query.getName() + "]");
        int ret = -1;
        /*
         * ISql isql = XmlBuilderSql.getQuery(query.getName()); if
         * (isql.getSqlCommandType() != SqlCommandType.UPDATE) throw new
         * IllegalArgumentException
         * ("Cannot execute an update when the query is different from update");
         * Query queryJpa = QueryBuilder.newQuery(isql, em, query);
         */
        Query queryJpa = getQueryJpa(query);
        ret = queryJpa.executeUpdate();
        return ret;
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
    public T get(T object)
    {
        init();
        T ret = null;
        IQuery q = getDefaultQuery(object);
        LOG.trace("executing get method with query [" + q.getName() + "]");
        Query queryJpa = getQueryJpa(q);
        try
        {
            ret = (T) queryJpa.getSingleResult();
        }
        catch (javax.persistence.NoResultException nre)
        {
        }
        return ret;
    }
    
    /**
     * Get one object instance from repository using a query.
     * 
     * @param query
     *            Query with parameters
     * @return Return the object that matches with query. A null reference is
     *         returned if the query no match anyone object.
     */
    public T get(IQuery query)
    {
        LOG.trace("executing get method with query [" + query.getName() + "]");
        T ret = null;
        // ISql isql = XmlBuilderSql.getQuery(query.getName());
        // Query queryJpa = QueryBuilder.newQuery(isql, em, query);
        Query queryJpa = getQueryJpa(query);
        try
        {
            ret = (T) queryJpa.getSingleResult();
        }
        catch (javax.persistence.NoResultException nre)
        {
        }
        return ret;
    }
    
    /**
     * Get a set of objects from repository using a query with name "T.list",
     * where 'T' it's a generic type.
     * 
     * @return Return a set of object that matches with query. A empty list is
     *         returned if the query no match anyone object.
     */
    public List<T> list()
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
    }
    
    /**
     * Get a set of objects from repository using a query.
     * 
     * @param query
     *            Query with parameters
     * @return Return a set of object that matches with query. A empty list is
     *         returned if the query no match anyone object.
     */
    public List<T> list(IQuery query)
    {
        LOG.trace("executing list method with query [" + query.getName() + "]");
        List<T> ret = null;
        Sql isql = null;
        Query queryJpa = null;
        boolean containQuery = XmlBuilderSql.containsQuery(query.getName());
        if (containQuery)
        {
            isql = XmlBuilderSql.getQuery(query.getName());
            queryJpa = QueryBuilder.newQuery(isql, getEntityManager(), query);
        }
        else
        {
            queryJpa = QueryBuilder.newQuery(getEntityManager(), query);
        }
        ret = queryJpa.getResultList();
        if (containQuery && isCountObjects(query))
        {
            setTotalObjectsOfQuery(query, isql);
            if (query.getTotal() == -1)
                ((net.sf.jkniv.sqlegance.Query) query).setTotal(Long.valueOf(ret.size()));
        }
        else if (ret != null)
        {
            Long total = new Long(ret.size());
            ((net.sf.jkniv.sqlegance.Query) query).setTotal(total);
        }
        if (ret == null)
            ret = new ArrayList<T>();
        LOG.trace("The query [" + query.getName() + "] found " + ret.size() + " objects");
        return ret;
    }
    
    /**
     * Get a set of objects 'G' from repository using a query.
     * 
     * @param query
     *            Query with parameters
     * @param clazz
     *            Type of object from list of object
     * @return Return a set of object that matches with query. A empty list is
     *         returned if the query no match anyone object.
     */
    public <G> List<G> list(IQuery query, Class<G> clazz)
    {
        LOG.trace("executing list method with query [" + query.getName() + "]");
        List<G> ret = null;
        Sql isql = null;
        Query queryJpa = null;
        boolean containQuery = XmlBuilderSql.containsQuery(query.getName());
        if (containQuery)
        {
            isql = XmlBuilderSql.getQuery(query.getName());
            queryJpa = QueryBuilder.newQuery(isql, getEntityManager(), query);
        }
        else
        {
            queryJpa = QueryBuilder.newQuery(getEntityManager(), query);
        }
        ret = queryJpa.getResultList();
        if (containQuery && isCountObjects(query))
        {
            setTotalObjectsOfQuery(query, isql);
            if (query.getTotal() == -1)
                ((net.sf.jkniv.sqlegance.Query) query).setTotal(Long.valueOf(ret.size()));
        }
        else if (ret != null)
        {
            Long total = new Long(ret.size());
            ((net.sf.jkniv.sqlegance.Query) query).setTotal(total);
        }
        if (ret == null)
            ret = new ArrayList<G>(1);
        else if (!ret.isEmpty())
        {
            G o = ret.get(0);
            if (!o.getClass().getName().equals(clazz.getName()))
                throw new IllegalArgumentException("The result of search don't match with [" + clazz.getName() + "]");
        }
        LOG.trace("The query [" + query.getName() + "] found " + ret.size() + " objects");
        return ret;
    }
    
    /**
     * Command to execute SQL statements that are in the buffer.
     */
    public void flush()
    {
        LOG.trace("executing flush method.");
        getEntityManager().flush();
    }
    
    public CriteriaBuilder getCriteriaBuilder()
    {
        return getEntityManager().getCriteriaBuilder();
    }
    
    public TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery)
    {
        return getEntityManager().createQuery(criteriaQuery);
    }
    
    /**
     * Verify if query is paginated, that must return the total records of the
     * query.
     * 
     * @param query
     *            The query
     * @return Return true if the query is paginated, false otherwise.
     */
    private boolean isCountObjects(IQuery query)
    {
        return (query.getMax() < Integer.MAX_VALUE);
    }
    
    private void setTotalObjectsOfQuery(IQuery query, Sql isql)
    {
        Number total = new Long(0);
        // we need count the total objects of the query
        if (isCountObjects(query))
        {
            Query queryJpa = getQueryForAutoCount(query, isql);
            try
            {
                total = (Number) queryJpa.getSingleResult();
                if (total != null)
                    ((net.sf.jkniv.sqlegance.Query) query).setTotal(total.longValue());
            }
            catch (NoResultException e)
            {
                // total is zero
                ((net.sf.jkniv.sqlegance.Query) query).setTotal(total.longValue());
            }
        }
    }
    
    private Query getQueryForAutoCount(IQuery query, Sql isql)
    {
        Query queryJpa = null;
        EntityManager em = getEntityManager();
        try
        {
            String queryName = query.getName() + ".count";
            Sql isqlCount = XmlBuilderSql.getQuery(queryName);
            LOG.trace("executing count query [" + query.getName() + ".count" + "]");
            Queryable queryCopy = QueryFactory.newInstance(queryName, query.getParams(), 0, Integer.MAX_VALUE);
            queryJpa = QueryBuilder.newQuery(isqlCount, em, queryCopy);
        }
        catch (IllegalArgumentException e)
        {
            // but its very important remove the order clause, because cannot
            // execute this way wrapping with "select count(*) ... where exists"
            // and performance
            String sqlWithoutOrderBy = removeOrders(isql.getSql(query.getParams()));
            String entityName = genericType.getSimpleName();
            String sql = "select count (*) from " + entityName + " where exists (" + sqlWithoutOrderBy + ")";
            LOG.trace("try count query dynamic [" + sql + "]");
            IQuery queryCopy = new net.sf.jkniv.sqlegance.Query(query.getName(), query.getParams(), 0,
                    Integer.MAX_VALUE);
            queryJpa = QueryBuilder.newQuery(sql, isql.getLanguageType(), em, queryCopy);
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
    private String removeOrders(String hql)
    {
        Matcher m = patternOrderBy.matcher(hql);
        StringBuffer sb = new StringBuffer();
        while (m.find())
        {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }
    
    private Query getQueryJpa(IQuery query)
    {
        Query queryJpa = null;
        EntityManager em = getEntityManager();
        try
        {
            Sql isql = XmlBuilderSql.getQuery(query.getName());
            queryJpa = QueryBuilder.newQuery(isql, em, query);
        }
        catch (IllegalArgumentException e)
        {
            queryJpa = QueryBuilder.newQuery(em, query);
        }
        return queryJpa;
    }
    
    /**
     * Create a default instance of IQuery object to execute list and get
     * methods.
     * 
     * @param object
     *            Parameter of the Queryable.
     * @return Return a new instance of query with parameters.
     */
    private IQuery getDefaultQuery(Object object)
    {
        String queryName = null;
        if (object != null)
            queryName = xmlQueryName.toGetName(object);
        else
            queryName = xmlQueryName.toListName(getGenericType());
        
        return new net.sf.jkniv.sqlegance.Query(queryName, object);
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
            obj = ((Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0])
                    .newInstance();
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
        for (String s : map.keySet())
            LOG.info("JPA property ["
                    + s
                    + "] value = "
                    + ((s.indexOf("password") < 0 || s.indexOf("pwd") < 0 || s.indexOf("passwd") < 0) ? map.get(s)
                            : "******"));
    }
    
    private void persistenceConfig()
    {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("META-INF/persistence.xml");
    }
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
