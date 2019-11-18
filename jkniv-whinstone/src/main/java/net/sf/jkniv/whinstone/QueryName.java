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
package net.sf.jkniv.whinstone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;
import net.sf.jkniv.reflect.BasicType;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;
import net.sf.jkniv.sqlegance.params.ParamMarkType;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.whinstone.params.AutoBindParams;
import net.sf.jkniv.whinstone.params.ParameterException;
import net.sf.jkniv.whinstone.params.ParameterNotFoundException;
import net.sf.jkniv.whinstone.params.PrepareParamsFactory;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * The object used to name and parameterize a query.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class QueryName implements Queryable
{
    private static final Assertable NOT_NULL    = AssertsFactory.getNotNull();
    private static final Assertable IS_NULL    = AssertsFactory.getIsNull();
    private static final BasicType  BASIC_TYPE = BasicType.getInstance();
    
    private enum TYPEOF_PARAM
    {
        NULL, BASIC, ARRAY_BASIC, ARRAY_POJO, ARRAY_MAP, COLLECTION_BASIC, COLLECTION_POJO, COLLECTION_MAP, COLLECTION_ARRAY, MAP, POJO
    };
    
    private String       name;
    private Integer      offset;
    private Integer      max;
    private Long         total;
    private Isolation    isolation;
    private String       hint;
    private int          timeout;
    private boolean      batch;
    private final Object params;
    private boolean      scalar;
    private int          size;
    private TYPEOF_PARAM paramType;
    private Class<?>     returnType;
    private Sql          sql;
    
    protected String     sqlText;
    protected String     sqlTextPaginated;
    protected String     sqlTextToCount;
    protected String[]   paramsNames;
    protected int        countParams;
    private String       bookmark;
    private boolean      boundSql;
    private boolean      boundParams;
    private boolean      cached;
    private boolean      cacheIgnore;
    
    /**
     * Creates a Query object parameterized starting at first row and retrieve all rows, isolation default, no timeout and online (no batch).
     * 
     * @param name a name for query
     * @param params parameters from query
     */
    public QueryName(String name, Object params)
    {
        this(name, params, 0, Integer.MAX_VALUE);
    }
    
    /**
     * Creates a Query object parameterized starting at first row and retrieve all rows, isolation default, no timeout and online (no batch).
     * 
     * @param name a name for query
     */
    public QueryName(String name)
    {
        this(name, null, 0, Integer.MAX_VALUE);
    }
    
    /**
     * Creates a Query object parameterized with: isolation default, no timeout and online (no batch).
     * 
     * @param name a name for query
     * @param params parameters from query
     * @param offset the first row
     * @param max row numbers
     */
    public QueryName(String name, Object params, int offset, int max)
    {
        this.name = name;
        this.params = params;
        this.offset = offset;
        this.max = max;
        this.total = -1L;
        this.isolation = Isolation.DEFAULT;
        this.timeout = -1;
        this.hint = "";
        this.batch = false;
        this.boundSql = false;
        this.boundParams = false;
        this.cached = false;
        this.cacheIgnore = false;
        sizeOfParams();
    }
    
    @Override
    public String getName()
    {
        return name;
    }
    
    @Override
    public <T> T getParams()
    {
        return (T)this.params;
    }
    
    @Override
    public Object getProperty(String name)
    {
        if (params == null)
            return null;
        
        Object o = null;
        try
        {
            o = PropertyUtils.getProperty(params, name);
        }
        catch (Exception e)
        {
            throw new ParameterNotFoundException("Cannot find the property [" + name + "] at param object ["
                    + (params != null ? params.getClass().getName() : "null") + "] ");
        }
        return o;
    }
    
    @Override
    public int getOffset()
    {
        return this.offset;
    }
    
    /**
     * @param value initial value of row number
     */
    @Override
    public void setOffset(int value)
    {
        this.offset = value;
    }
    
    @Override
    public int getMax()
    {
        return this.max;
    }
    
    @Override
    public void setMax(int value)
    {
        this.max = value;
    }
    
    @Override
    public long getTotal()
    {
        return this.total;
    }
    
    @Override
    public void setTotal(long total)
    {
        this.total = total;
    }
    
    @Override
    public boolean hasRowsOffset()
    {
        return (offset > 0);
    }
    
    @Override
    public boolean isPaging()
    {
        return (max < Integer.MAX_VALUE);
    }
    
    @Override
    public void scalar()
    {
        this.scalar = true;
    }
    
    @Override
    public boolean isScalar()
    {
        return scalar;
    }
    
    @Override
    public boolean isBoundSql()
    {
        return this.boundSql;
    }
    
    @Override
    public boolean isBoundParams()
    {
        return this.boundParams;
    }

    @Override
    public boolean isTypeOfNull()
    {
        return (this.params == null);
    }
    
    @Override
    public boolean isTypeOfBasic()
    {
        return (this.paramType == TYPEOF_PARAM.BASIC);
    }
    
    @Override
    public boolean isTypeOfArrayFromBasicTypes()
    {
        return (this.paramType == TYPEOF_PARAM.ARRAY_BASIC);
    }
    
    @Override
    public boolean isTypeOfArrayFromPojo()
    {
        return (this.paramType == TYPEOF_PARAM.ARRAY_POJO);
    }
    
    @Override
    public boolean isTypeOfArrayFromMap()
    {
        return (this.paramType == TYPEOF_PARAM.ARRAY_MAP);
    }
    
    @Override
    public boolean isTypeOfCollectionFromBasicTypes()
    {
        return (this.paramType == TYPEOF_PARAM.COLLECTION_BASIC);
    }
    
    @Override
    public boolean isTypeOfCollectionFromPojo()
    {
        return (this.paramType == TYPEOF_PARAM.COLLECTION_POJO);
    }
    
    @Override
    public boolean isTypeOfCollectionFromMap()
    {
        return (this.paramType == TYPEOF_PARAM.COLLECTION_MAP);
    }
    
    @Override
    public boolean isTypeOfCollectionFromArray()
    {
        return (this.paramType == TYPEOF_PARAM.COLLECTION_ARRAY);
    }
    
    @Override
    public boolean isTypeOfArray()
    {
        return (this.paramType == TYPEOF_PARAM.ARRAY_BASIC || this.paramType == TYPEOF_PARAM.ARRAY_POJO
                || this.paramType == TYPEOF_PARAM.ARRAY_MAP);
    }
    
    @Override
    public boolean isTypeOfCollection()
    {
        return (this.paramType == TYPEOF_PARAM.COLLECTION_BASIC || this.paramType == TYPEOF_PARAM.COLLECTION_POJO
                || this.paramType == TYPEOF_PARAM.COLLECTION_MAP || this.paramType == TYPEOF_PARAM.COLLECTION_ARRAY);
    }
    
    @Override
    public boolean isTypeOfBulk()
    {
        return (this.paramType == TYPEOF_PARAM.COLLECTION_POJO || this.paramType == TYPEOF_PARAM.COLLECTION_MAP
                || this.paramType == TYPEOF_PARAM.COLLECTION_ARRAY || this.paramType == TYPEOF_PARAM.ARRAY_MAP
                || this.paramType == TYPEOF_PARAM.ARRAY_POJO);
    }
    
    @Override
    public boolean isTypeOfMap()
    {
        return (this.paramType == TYPEOF_PARAM.MAP);
    }
    
    /**
     * determine the type of from query parameters
     */
    private void sizeOfParams()
    {
        this.size = 0;
        if (this.params == null)
        {
            this.paramType = TYPEOF_PARAM.NULL;
        }
        else if (BASIC_TYPE.isBasicType(this.params.getClass()))
        {
            this.size = 1;
            this.paramType = TYPEOF_PARAM.BASIC;
        }
        else if (Map.class.isInstance(params))
        {
            this.size = ((Map) this.params).size();
            this.paramType = TYPEOF_PARAM.MAP;
        }
        else if (this.params.getClass().isArray())
        {
            this.size = ((Object[]) this.params).length;
            checkArrayParams();
        }
        else if (Collection.class.isInstance(this.params))
        {
            this.size = ((Collection) this.params).size();
            checkCollectionParams();
        }
        else
        {
            this.size = 1;
            this.paramType = TYPEOF_PARAM.POJO;
        }
    }
    
    private void checkArrayParams()
    {
        this.paramType = TYPEOF_PARAM.ARRAY_POJO;
        Object param = null;
        if (((Object[]) params).length > 0)
            param = ((Object[]) params)[0];
        if (param != null)
        {
            if (BASIC_TYPE.isBasicType(param.getClass()))
                this.paramType = TYPEOF_PARAM.ARRAY_BASIC;
            else if (param instanceof Map)
                this.paramType = TYPEOF_PARAM.ARRAY_MAP;
        }
    }
    
    private void checkCollectionParams()
    {
        Iterator<?> it = ((Collection<?>) params).iterator();
        Object param = null;
        this.paramType = TYPEOF_PARAM.COLLECTION_POJO;
        do
        {
            if (it.hasNext())
                param = it.next();
            if (param != null)
            {
                if (BASIC_TYPE.isBasicType(param.getClass()))
                    this.paramType = TYPEOF_PARAM.COLLECTION_BASIC;
                else if (param instanceof Map)
                    this.paramType = TYPEOF_PARAM.COLLECTION_MAP;
                else if (param.getClass().isArray())
                    this.paramType = TYPEOF_PARAM.COLLECTION_ARRAY;
            }
        } while (param == null && it.hasNext());
    }
    
    @Override
    public Iterator<Object> iterator()
    {
        if (this.params == null)
            throw new NullPointerException("Cannot iterate over null reference");
        
        Iterator<Object> it = null;
        if (this.params instanceof Map)
            it = ((Map)this.params).values().iterator();
        else if (isTypeOfArray())
            it = new ArrayIterator(this.params, this.size);
        else if (isTypeOfCollection())
            it = ((Collection) this.params).iterator();
        else
            throw new UnsupportedOperationException(
                    "Cannot iterate over another type of object, just Arrays or Collections");
        
        return it;
    }
    
    @Override
    public Object[] values(String[] paramsNames)
    {
        List<Object> paramsValues = new ArrayList<Object>();
        int i = 0;
        if (isTypeOfBasic())
        {
            paramsValues.add(getParams());
        }
        else if(isTypeOfArrayFromBasicTypes())
        {
            if(!hasInClause(paramsNames) && paramsNames.length != getParamsAsArray().length)
            {
                throw new ParameterException("A query [" + this.name
                        + "] with positional parameters needs an array exactly have the same number of parameters from query.");
            }
            return (Object[]) this.params;
        }
        else
        {
            //int k = 0; // index params for clause IN
            for (String name : paramsNames)
            {
                Object paramValue = null;
                if (paramsNames[i].toLowerCase().startsWith("in:"))
                {
                    String paramName = paramsNames[i].substring(3, paramsNames[i].length());// :in:myValueArray -> myValueArray 
                    if (paramValue == null)
                    {
                        paramValue = getProperty(paramName);
                    }
                    Object[] paramsIN = null;
                    int j = 0;
                    if (paramValue != null && paramValue.getClass().isArray())
                        paramsIN = (Object[]) paramValue;
                    else if (paramValue instanceof Collection)
                        paramsIN = ((Collection) paramValue).toArray();
                    
                    if (paramsIN == null)
                        throw new ParameterException(
                                "Cannot set parameter [" + paramsNames[i] + "] from IN clause with NULL");
                    
                    for (; j < paramsIN.length; j++)
                        paramsValues.add(paramsIN[j]);//params[j + i + 1] = paramsIN[j];
                }
                else if ("?".equals(name))
                {
                    if(isTypeOfArrayFromBasicTypes())
                    {
                        paramsValues.add(getParamsAsArray()[i]);
                    }
//                    else if(isTypeOfCollectionFromBasicTypes())
//                    {
//                        paramsValues.add(((Collection)this.params). ); CANNOT ACCESS COLLECTION BY INDEX
//                    }
                }
                else
                    paramsValues.add(getProperty(name));
                i++;
            }
        }
        return paramsValues.toArray(new Object[0]);
    }
    
    @Override
    public void bind(Sql sql)//TODO test Queryable.bind method
    {
        IS_NULL.verify(new IllegalStateException("Cannot re-assign new Sql to queryable ["+this.name+"] object"), this.sql);
        NOT_NULL.verify(sql);
        this.sql = sql;
        this.sqlText = sql.getSql(this.params);
        boolean pagingSelect = false;
        SqlDialect sqlDialect = this.sql.getSqlDialect();
        if (sql.isSelectable() && isPaging())
        {
            pagingSelect = true;
            if(sqlDialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY))
                this.sqlTextPaginated = sqlDialect.buildQueryPaging(sqlText, this.offset, this.max, this.bookmark);
            else
                this.sqlTextPaginated = sqlDialect.buildQueryPaging(sqlText, this.offset, this.max);
        }
        replaceForQuestionMark();
        if (pagingSelect && sqlDialect.supportsFeature(SqlFeatureSupport.PAGING_ROUNDTRIP))
        {
            this.sqlTextToCount = sqlDialect.buildQueryCount(sqlText);
            if (sql.getSqlDialect().supportsFeature(SqlFeatureSupport.LIMIT) && this.sqlTextPaginated == null)// TODO test paginate
                throw new IllegalStateException("SqlDialect [" + sql.getSqlDialect().name()
                        + "] supports paging query but the COUNT query cannot be build");
        }
        this.boundSql = true;
    }
    
    @Override
    public <T, R> AutoBindParams bind(StatementAdapter<T, R> adapter)
    {
        AutoBindParams prepareParams = null;
        if (isTypeOfNull())
            prepareParams = PrepareParamsFactory.newNoParams(adapter);
        else if (isTypeOfBasic() || params instanceof Date || params instanceof Calendar)
            prepareParams = PrepareParamsFactory.newBasicParam(adapter, this);
        else if (isTypeOfArrayFromBasicTypes())
            prepareParams = PrepareParamsFactory.newPositionalArrayParams(adapter, this);
        else if (isTypeOfArrayFromMap() || isTypeOfCollectionFromMap())
            prepareParams = PrepareParamsFactory.newPositionalCollectionMapParams(adapter, this);
        else if (isTypeOfArrayFromPojo() || isTypeOfCollectionFromPojo())
            prepareParams = PrepareParamsFactory.newPositionalCollectionPojoParams(adapter, this);
        else if (isTypeOfCollectionFromBasicTypes())
            prepareParams = PrepareParamsFactory.newPositionalCollectionParams(adapter, this);
        else if (isTypeOfCollectionFromArray())
            prepareParams = PrepareParamsFactory.newPositionalCollectionArrayParams(adapter, this);
        else if (sql.getParamParser().getType() == ParamMarkType.QUESTION)
            prepareParams = PrepareParamsFactory.newPositionalParams(adapter, this);
        else
            prepareParams = PrepareParamsFactory.newNamedParams(adapter, this);
        
        this.boundParams = true;
        return prepareParams;
    }
    
    @Override
    public String query()
    {
        if (!boundSql)
            throw new IllegalStateException("Needs to bind Sql before to call Queryable.query");
        return (this.sqlTextPaginated == null ? this.sqlText : this.sqlTextPaginated);
    }
    
    @Override
    public String queryCount()
    {
        if (!boundSql)
            throw new IllegalStateException("Needs to bind Sql before to call Queryable.queryCount");
        return this.sqlTextToCount;
    }
    
    @Override
    public String[] getParamsNames()
    {
        if (!boundSql) //TODO test Queryable.getParamsName method
            throw new IllegalStateException("Needs to bind Sql before to call Queryable.getParamsName");
        return this.paramsNames;
    }
    
    private void replaceForQuestionMark()
    {
        ParamParser paramParser = sql.getParamParser();
        this.paramsNames = paramParser.find(sqlText);
        if (this.sqlTextPaginated != null)
            this.sqlTextPaginated = paramParser.replaceForQuestionMark(this.sqlTextPaginated, params);
        
        this.sqlText = paramParser.replaceForQuestionMark(this.sqlText, params);
        this.countParams = countOccurrencesOf(this.sqlText, "?");
    }
    
    /** 
     * Count the occurrences of the substring in string s.
     * @param str string to search in. Return 0 if this is null.
     * @param sub string to search for. Return 0 if this is null.
     * @return number of occurrences from {@code str}
     */
    private int countOccurrencesOf(String str, String sub)
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
    
    @Override
    public Sql getDynamicSql()
    {
        return this.sql;
    }

    @Override
    public Class<?> getReturnType()
    {
        Class<?> returnAnswer = Map.class;
        if (this.returnType != null)
            returnAnswer = this.returnType;
        else if (isBoundSql() && getDynamicSql().getReturnTypeAsClass() != null)
            returnAnswer = getDynamicSql().getReturnTypeAsClass();
        
        return returnAnswer;
    }
    
    void setReturnType(Class<?> clazz)
    {
        IS_NULL.verify(this.returnType);
        this.returnType = clazz;
    }
    

    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((max == null) ? 0 : max.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((params == null) ? 0 : params.hashCode());
        result = prime * result + ((returnType == null) ? 0 : returnType.getName().hashCode());
        result = prime * result + size;
        result = prime * result + ((sql == null) ? 0 : sql.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        QueryName other = (QueryName) obj;
        if (max == null)
        {
            if (other.max != null)
                return false;
        }
        else if (!max.equals(other.max))
            return false;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        if (params == null)
        {
            if (other.params != null)
                return false;
        }
        else if (!params.equals(other.params))
            return false;
        if (returnType == null)
        {
            if (other.returnType != null)
                return false;
        }
        else if (!returnType.equals(other.returnType))
            return false;
        if (size != other.size)
            return false;
        if (sql == null)
        {
            if (other.sql != null)
                return false;
        }
        else if (!sql.equals(other.sql))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "QueryName [name=" + name + ", offset=" + offset + ", max=" + max + ", timeout=" + timeout + ", batch="
                + batch + ", scalar=" + scalar + ", paramType=" + paramType + "]";
    }

    @Override
    public void cacheIgnore()
    {
        this.cacheIgnore = true;
    }

    @Override
    public boolean isCacheIgnore()
    {
        return this.cacheIgnore;
    }

    @Override
    public void cached()
    {
        this.cached = true;        
    }

    @Override
    public boolean isCached()
    {
        return this.cached;        
    }
    
    @Override
    public void setBookmark(String bookmark)
    {
        this.bookmark = bookmark;
    }

    @Override
    public String getBookmark()
    {
        return this.bookmark;
    }
    
    private Object[] getParamsAsArray() 
    {
        if(this.params == null)
            return new Object[0];
        
        return (Object[]) this.params;
    }
    
    private boolean hasInClause(String[] paramsNames)
    {
        for (String p : paramsNames)
        {
            if (hasInClause(p))
                return true;
        }
        return false;
    }
    
    private boolean hasInClause(String paramName)
    {
        return (paramName.toLowerCase().startsWith("in:"));
    }
/*
    private void checkIfParamIsEntity() {
        ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(this.params);
        proxy.ge
        Class<? extends Annotation> entityAnnotation = (Class<? extends Annotation>) forName("javax.persistence.Entity");
        if (returnTypeClass != null && entityAnnotation != null)
            this.returnTypeManaged = returnTypeClass.isAnnotationPresent(entityAnnotation);
        
    }

    private Class<?> forName(String typeOfClass)
    {
        try
        {
            return Class.forName(typeOfClass);
        }
        catch (ClassNotFoundException returnNULL) {  // TODO ClassNotFoundException NULL type, returnType undefined 
        return null;
    }
*/
}
