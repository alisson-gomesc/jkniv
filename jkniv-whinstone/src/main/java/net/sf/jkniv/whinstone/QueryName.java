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
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.reflect.beans.PropertyAccess;
import net.sf.jkniv.sqlegance.Sql;
import net.sf.jkniv.sqlegance.dialect.SqlDialect;
import net.sf.jkniv.sqlegance.dialect.SqlFeatureSupport;
import net.sf.jkniv.sqlegance.params.ParamMarkType;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.sqlegance.transaction.Isolation;
import net.sf.jkniv.sqlegance.types.Convertible;
import net.sf.jkniv.sqlegance.types.NoConverterType;
import net.sf.jkniv.whinstone.params.AutoBindParams;
import net.sf.jkniv.whinstone.params.ParameterException;
import net.sf.jkniv.whinstone.params.ParameterNotFoundException;
import net.sf.jkniv.whinstone.params.PrepareParamsFactory;
import net.sf.jkniv.whinstone.statement.ConvertibleFactory;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * The object used to name and parameterize a query.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
@SuppressWarnings(
{ "unchecked", "rawtypes" })
class QueryName implements Queryable
{
    private static final Assertable NOT_NULL   = AssertsFactory.getNotNull();
    private static final Assertable IS_NULL    = AssertsFactory.getIsNull();
    private static final BasicType  BASIC_TYPE = BasicType.getInstance();
    
    private enum TYPEOF_PARAM
    {
        NULL, BASIC, ARRAY_BASIC, ARRAY_POJO, ARRAY_MAP, COLLECTION_BASIC, COLLECTION_POJO, COLLECTION_MAP, COLLECTION_ARRAY, LIST_BASIC, MAP, POJO
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
        return (T) this.params;
    }
    
    @Override
    public Param getProperty(String name)
    {
        Param param = null;
        if (!isTypeOfNull())
        {
            
            try
            {
                Object o = PropertyUtils.getProperty(params, name);
                param = new Param(o, getConverter(name).toJdbc(o), name);
            }
            catch (Exception e)
            {
                throw new ParameterNotFoundException("Cannot find the property [" + name + "] at param object ["
                        + (params != null ? params.getClass().getName() : "null") + "] ");
            }
        }
        return param;
    }
    
    @Override
    public Param getProperty(String name, int index)
    {
        Param param = null;
        if (!isTypeOfNull())
        {
            try
            {
                Object o = PropertyUtils.getProperty(params, name);
                param = new Param(o, getConverter(name).toJdbc(o), name, index);
            }
            catch (Exception e)
            {
                throw new ParameterNotFoundException("Cannot find the property [" + name + "] at param object ["
                        + (params != null ? params.getClass().getName() : "null") + "] ");
            }
        }
        return param;
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
    public boolean isTypeOfPojo()
    {
        return (this.paramType == TYPEOF_PARAM.POJO);
    }
    
    @Override
    public boolean isTypeOfArrayBasicTypes()
    {
        return (this.paramType == TYPEOF_PARAM.ARRAY_BASIC);
    }
    
    @Override
    public boolean isTypeOfArrayPojo()
    {
        return (this.paramType == TYPEOF_PARAM.ARRAY_POJO);
    }
    
    @Override
    public boolean isTypeOfArrayMap()
    {
        return (this.paramType == TYPEOF_PARAM.ARRAY_MAP);
    }
    
    @Override
    public boolean isTypeOfCollectionBasicTypes()
    {
        return (this.paramType == TYPEOF_PARAM.LIST_BASIC || this.paramType == TYPEOF_PARAM.COLLECTION_BASIC);
    }
    
    private boolean isTypeOfListBasicTypes()
    {
        return (this.paramType == TYPEOF_PARAM.LIST_BASIC);
    }
    
    @Override
    public boolean isTypeOfCollectionPojo()
    {
        return (this.paramType == TYPEOF_PARAM.COLLECTION_POJO);
    }
    
    @Override
    public boolean isTypeOfCollectionMap()
    {
        return (this.paramType == TYPEOF_PARAM.COLLECTION_MAP);
    }
    
    @Override
    public boolean isTypeOfCollectionArray()
    {
        return (this.paramType == TYPEOF_PARAM.COLLECTION_ARRAY);
    }
    
    @Override
    public boolean isTypeOfArray()
    {
        return (this.paramType == TYPEOF_PARAM.ARRAY_BASIC || 
                this.paramType == TYPEOF_PARAM.ARRAY_POJO || 
                this.paramType == TYPEOF_PARAM.ARRAY_MAP);
    }
    
    @Override
    public boolean isTypeOfCollection()
    {
        return (this.paramType == TYPEOF_PARAM.LIST_BASIC ||  
                this.paramType == TYPEOF_PARAM.COLLECTION_BASIC || 
                this.paramType == TYPEOF_PARAM.COLLECTION_POJO || 
                this.paramType == TYPEOF_PARAM.COLLECTION_MAP || 
                this.paramType == TYPEOF_PARAM.COLLECTION_ARRAY);
    }
    
    @Override
    public boolean isTypeOfBulk()
    {
        return (this.paramType == TYPEOF_PARAM.COLLECTION_POJO || 
                this.paramType == TYPEOF_PARAM.COLLECTION_MAP || 
                this.paramType == TYPEOF_PARAM.COLLECTION_ARRAY || 
                this.paramType == TYPEOF_PARAM.ARRAY_MAP || 
                this.paramType == TYPEOF_PARAM.ARRAY_POJO);
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
                    this.paramType = (this.params instanceof List ? TYPEOF_PARAM.LIST_BASIC
                            : TYPEOF_PARAM.COLLECTION_BASIC);
                else if (param instanceof Map)
                    this.paramType = TYPEOF_PARAM.COLLECTION_MAP;
                else if (param.getClass().isArray())
                    this.paramType = TYPEOF_PARAM.COLLECTION_ARRAY;
            }
        } while (param == null && it.hasNext());
    }
    
    @Override
    public Iterator<Param> iterator()
    {
        if (this.params == null)
            throw new NullPointerException("Cannot iterate over null reference");
        
        Iterator<Param> it = null;
        if (this.params instanceof Map)
            it = new ArrayIterator((Map) this.params);
        else if (isTypeOfArray())
            it = new ArrayIterator((Object[]) this.params, this.size);
        else if (isTypeOfCollection())
            it = new ArrayIterator((Collection) this.params);
        else
            throw new UnsupportedOperationException(
                    "Cannot iterate over another type of object, just Arrays or Collections");
        
        return it;
    }
    
    @Override
    public Param[] values()
    {
        List<Param> paramsValues = new ArrayList<Param>();
        int i = 0;
        if (isTypeOfBasic())
        {
            paramsValues.add(new Param(getParams(), i));
        }
        else if (isTypeOfBulk()) 
        {
            Iterator<Param>  it = iterator();
            while(it.hasNext())
                paramsValues.add(it.next());
        }
        ////        else if(isTypeOfMap())
        ////        {
        ////            Set<Entry<String,Object>> entries = ((Map)this.params).entrySet();
        ////            for(Entry<String, Object> entry : entries)
        ////                paramsValues.add(new Param(entry.getValue(), i++, entry.getKey()));
        ////        }
        //        else if(isTypeOfArrayFromBasicTypes())
        //        {
        //            if(!hasInClause(paramsNames) && paramsNames.length != getParamsAsArray().length)
        //                throw new ParameterException("A query [" + this.name
        //                        + "] with positional parameters needs an array exactly have the same number of parameters from query.");
        //
        //            Object[] arrayOfParams = (Object[])this.params;
        //            for(int j=0; j<arrayOfParams.length; j++)
        //                paramsValues.add(new Param(arrayOfParams[j], j));
        //        }
        //        else if(isTypeOfCollectionFromBasicTypes())
        //        {
        //            if(!hasInClause(paramsNames) && paramsNames.length != getParamsAsCollection().size())
        //                throw new ParameterException("A query [" + this.name
        //                        + "] with positional parameters needs an collection exactly have the same number of parameters from query.");
        //
        //            Collection<?> colOfParams = (Collection<?>)this.params;
        //            int j=0;
        //            for(Object o : colOfParams)
        //                paramsValues.add(new Param(o, j++));
        //        }
        else
        {
            //int k = 0; // index params for clause IN
            for (String name : paramsNames)
            {
                Param paramValue = null;
                if (paramsNames[i].toLowerCase().startsWith("in:"))
                {
                    String paramName = paramsNames[i].substring(3, paramsNames[i].length());// :in:myValueArray -> myValueArray
                    if (this.size == this.countParams)
                        paramValue = new Param(this.params, name, i);
                    else
                        paramValue = getParamsFromIndex(i);
                    
                    if (paramValue == null)
                        paramValue = getProperty(paramName);
                    
//                    else if (isTypeOfArray())
//                        paramValue = new Param(((Object[]) this.params)[i]);
//                    else if (isTypeOfCollection())
//                        paramValue = new Param(((List) this.params).get(i));
//                    else
//                        paramValue = getProperty(paramName);
                    
                    Param[] paramsIN = null;
                    if (paramValue.isCollection() || paramValue.isArray())
                        paramsIN = paramValue.asArray();
                    else
                        paramsIN = new Param[] { paramValue };
                    
                    if (paramValue.getValue() == null)
                        throw new ParameterException(
                                "Cannot set parameter [" + paramsNames[i] + "] from IN clause with NULL");
                    
                    int j = 0;
                    for (; j < paramsIN.length; j++)
                        paramsValues.add(paramsIN[j]);
                    /*
                    if (paramValue.getValue() != null && paramValue.getValue().getClass().isArray())
                        paramsIN = (Object[]) paramValue.getValue();
                    else if (paramValue.getValue() instanceof Collection)
                        paramsIN = ((Collection) paramValue.getValue()).toArray();
                    
                    if (paramsIN == null)
                        throw new ParameterException(
                                "Cannot set parameter [" + paramsNames[i] + "] from IN clause with NULL");
                    
                    for (; j < paramsIN.length; j++)
                        paramsValues.add(new Param(paramsIN[j], i+j, name));//params[j + i + 1] = paramsIN[j];
                        */
                }
                else if ("?".equals(name))
                {
                    if (this.size != paramsNames.length && !hasInClause(paramsNames))
                        throw new ParameterException("A query [" + this.name
                                + "] with positional parameters needs an array exactly have the same number of parameters from query.");
                    
                    paramsValues.add(getParamsFromIndex(i));
//                    if (isTypeOfArrayBasicTypes())
//                    {
//                        paramsValues.add(new Param(getParamsAsArray()[i], i, name));
//                    }
                    //                    else if(isTypeOfCollectionBasicTypes())
                    //                    {
                    //                        paramsValues.add(((Collection)this.params). ); CANNOT ACCESS COLLECTION BY INDEX
                    //                    }
                }
                else
                    paramsValues.add(getProperty(name, i));
                i++;
            }
        }
        return paramsValues.toArray(new Param[0]);
    }
    
    @Override
    public void bind(Sql sql)//TODO test Queryable.bind method
    {
        IS_NULL.verify(new IllegalStateException("Cannot re-assign new Sql to queryable [" + this.name + "] object"),
                this.sql);
        NOT_NULL.verify(sql);
        this.sql = sql;
        this.sqlText = sql.getSql(this.params);
        boolean pagingSelect = false;
        SqlDialect sqlDialect = this.sql.getSqlDialect();
        if (sql.isSelectable() && isPaging())
        {
            pagingSelect = true;
            if (sqlDialect.supportsFeature(SqlFeatureSupport.BOOKMARK_QUERY))
                this.sqlTextPaginated = sqlDialect.buildQueryPaging(sqlText, this.offset, this.max, this.bookmark);
            else
                this.sqlTextPaginated = sqlDialect.buildQueryPaging(sqlText, this.offset, this.max);
        }
        replaceForPlaceholder();
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
        else if (isTypeOfArrayBasicTypes())
            prepareParams = PrepareParamsFactory.newPositionalArrayParams(adapter, this);
        else if (isTypeOfArrayMap() || isTypeOfCollectionMap())
            prepareParams = PrepareParamsFactory.newPositionalCollectionMapParams(adapter, this);
        else if (isTypeOfArrayPojo() || isTypeOfCollectionPojo())
            prepareParams = PrepareParamsFactory.newPositionalCollectionPojoParams(adapter, this);
        else if (isTypeOfCollectionBasicTypes())
            prepareParams = PrepareParamsFactory.newPositionalCollectionParams(adapter, this);
        else if (isTypeOfCollectionArray())
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
    
    private void replaceForPlaceholder()
    {
        ParamParser paramParser = sql.getParamParser();
        this.paramsNames = paramParser.find(sqlText);
        if (this.sqlTextPaginated != null)
            this.sqlTextPaginated = paramParser.replaceForPlaceholder(this.sqlTextPaginated, params);
        
        this.sqlText = paramParser.replaceForPlaceholder(this.sqlText, params);
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
    
    private Param getParamsFromIndex(int i)
    {
        Param param = null;
        if (this.params == null)
        {
            param = new Param();
        }
        else if (isTypeOfArrayBasicTypes())
        {
            param = new Param(((Object[]) this.params)[i], "?", i);
        }
        else if (isTypeOfListBasicTypes())
        {
            param = new Param(((List) this.params).get(i), "?", i);
        }
        else if (isTypeOfCollectionBasicTypes())
        {
            Iterator<Object> it = ((Collection<Object>) this.params).iterator();
            int j = 0;
            while (it.hasNext())
            {
                if (j == i)
                {
                    param = new Param(it.next(), "?", i);
                    break;
                }
                j++;
            }
        }
        return param;
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
    
    /**
     * Retrieve a {@link Convertible} instance to customize the
     * value of parameter to database field.
     * @param fieldName name of field
     * @return A convertible instance if found into class proxy or {@link NoConverterType}
     * instance when the field or method is not annotated.
     */
    private Convertible<Object, Object> getConverter(String fieldName)
    {
        Convertible<Object, Object> convertible = NoConverterType.getInstance();
        if(isTypeOfPojo() || isTypeOfCollectionPojo() || isTypeOfArrayPojo())
        {
            ObjectProxy<?> proxy = ObjectProxyFactory.of(getParams());
            convertible = ConvertibleFactory.toJdbc(new PropertyAccess(fieldName, getParams().getClass()), proxy);
        }
        return convertible;
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
}
