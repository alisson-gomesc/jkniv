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
package net.sf.jkniv.sqlegance;

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
import net.sf.jkniv.sqlegance.params.ParamMarkType;
import net.sf.jkniv.sqlegance.params.ParamParser;
import net.sf.jkniv.sqlegance.params.ParameterException;
import net.sf.jkniv.sqlegance.params.ParameterNotFoundException;
import net.sf.jkniv.sqlegance.params.PrepareParamsFactory;
import net.sf.jkniv.sqlegance.params.AutoBindParams;
import net.sf.jkniv.sqlegance.statement.StatementAdapter;
import net.sf.jkniv.sqlegance.transaction.Isolation;

/**
 * The object used to name and parameterize a query.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
class QueryName implements Queryable
{
    private static final Assertable notNull    = AssertsFactory.getNotNull();
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
    private Object       params;
    private boolean      scalar;
    private int          size;
    private TYPEOF_PARAM paramType;
    private Class        returnType;
    private Sql          sql;
    
    protected String     sqlText;
    protected String     sqlTextPaginated;
    protected String     sqlTextToCount;
    protected String[]   paramsNames;
    protected int        countParams;
    private boolean      boundSql;
    private boolean      boundParams;
    
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
        sizeOfParams();
    }
    
    @Override
    public String getName()
    {
        return name;
    }
    
    @Override
    public Object getParams()
    {
        return this.params;
    }
    
    @Override
    public Object getProperty(String name)
    {
        if (params == null)
            return null;
        
        Object o = null;
        //        if (params == null)
        //            throw new ParameterNotFoundException(
        //                    "Query with parameter ["+name+"] is wrong, cannot found value with null instance from Queryable.params");
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
        if (isTypeOfArray())
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
        List<Object> params = new ArrayList<Object>();
        int i = 0;
        if (isTypeOfBasic())
        {
            params.add(getParams());
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
                        params.add(paramsIN[j]);//params[j + i + 1] = paramsIN[j];
                }
                else
                    params.add(getProperty(name));
                i++;
            }
        }
        return params.toArray(new Object[0]);
    }
    
    @Override
    public void bind(Sql sql)//TODO test Queryable.bind method
    {
        notNull.verify(sql);
        if (this.sql != null)
            throw new IllegalStateException("Cannot re-assign new Sql to queryable object");
        
        this.sql = sql;
        this.sqlText = sql.getSql(this.params);
        boolean pagingSelect = false;
        if (sql.isSelectable() && isPaging())
        {
            pagingSelect = true;
            this.sqlTextPaginated = this.sql.getSqlDialect().buildQueryPaging(sqlText, this.offset, this.max);
        }
        replaceForQuestionMark();
        if (pagingSelect)
        {
            this.sqlTextToCount = this.sql.getSqlDialect().buildQueryCount(sqlText);
            if (sql.getSqlDialect().supportsLimit() && this.sqlTextPaginated == null)// TODO test paginate
                throw new IllegalStateException("SqlDialect [" + sql.getSqlDialect().name()
                        + "] supports paging query but the query cannot be build");
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
        else if (isTypeOfArrayFromMap())
            prepareParams = PrepareParamsFactory.newPositionalCollectionMapParams(adapter, this);
        else if (isTypeOfCollectionFromBasicTypes())
            prepareParams = PrepareParamsFactory.newPositionalCollectionParams(adapter, this);
        else if (isTypeOfCollectionFromMap())
            prepareParams = PrepareParamsFactory.newPositionalCollectionMapParams(adapter, this);
        else if (isTypeOfCollectionFromPojo())
            prepareParams = PrepareParamsFactory.newPositionalCollectionPojoParams(adapter, this);
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
        if (!boundSql)//TODO test Queryable.query method
            throw new IllegalStateException("Needs to bind Sql before to call Queryable.query");
        return (this.sqlTextPaginated == null ? this.sqlText : this.sqlTextPaginated);
    }
    
    @Override
    public String queryCount()
    {
        if (!boundSql) //TODO test Queryable.queryCount method
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
    public Class getReturnType()
    {
        return this.returnType;
    }
    
    
    @Override
    public void setReturnType(Class clazz)
    {
        this.returnType = clazz;
    }
    @Override
    public String toString()
    {
        return "QueryName [name=" + name + ", offset=" + offset + ", max=" + max + ", timeout=" + timeout + ", batch="
                + batch + ", scalar=" + scalar + ", paramType=" + paramType + "]";
    }
}
