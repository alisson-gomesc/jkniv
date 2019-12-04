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
package net.sf.jkniv.whinstone.jdbc.statement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.experimental.TimerKeeper;
import net.sf.jkniv.reflect.beans.CapitalNameFactory;
import net.sf.jkniv.reflect.beans.Capitalize;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.reflect.beans.PropertyAccess;
import net.sf.jkniv.sqlegance.OneToMany;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.sqlegance.types.CalendarAsSqlTimestampType;
import net.sf.jkniv.sqlegance.types.Convertible;
import net.sf.jkniv.sqlegance.types.DateAsSqlTimestampType;
import net.sf.jkniv.sqlegance.types.NoConverterType;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.Param;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.ResultSetParser;
import net.sf.jkniv.whinstone.classification.Groupable;
import net.sf.jkniv.whinstone.classification.GroupingBy;
import net.sf.jkniv.whinstone.classification.NoGroupingBy;
import net.sf.jkniv.whinstone.classification.Transformable;
import net.sf.jkniv.whinstone.jdbc.DefaultJdbcColumn;
import net.sf.jkniv.whinstone.jdbc.LoggerFactory;
import net.sf.jkniv.whinstone.statement.AutoKey;
import net.sf.jkniv.whinstone.statement.ConvertibleFactory;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

public class JdbcPreparedStatementAdapter<T, R> implements StatementAdapter<T, ResultSet>
{
    private static final Logger      LOG     = LoggerFactory.getLogger();
    private static final Logger      SQLLOG  = net.sf.jkniv.whinstone.jdbc.LoggerFactory.getLogger();
    private static final DataMasking MASKING = LoggerFactory.getDataMasking();
    private static final Capitalize  CAPITAL_SETTER  = CapitalNameFactory.getInstanceOfSetter();
    
    private final PreparedStatement  stmt;
    private final HandlerException   handlerException;
    private int                      index;
    private ResultRow<T, ResultSet>  resultRow;
    private Queryable                queryable;
    private AutoKey                  autoKey;
    private String[]                 paramNames;
    
    public JdbcPreparedStatementAdapter(PreparedStatement stmt, Queryable queryable)
    {
        this.stmt = stmt;
        // FIXME handler exception for default message with custom params
        this.handlerException = new HandlerException(RepositoryException.class, "Cannot set parameter [%s] value [%s]");
        this.queryable = queryable;
        this.paramNames = this.queryable.getParamsNames();
        this.reset();
    }
    
    @Override
    public StatementAdapter<T, ResultSet> with(ResultRow<T, ResultSet> resultRow)
    {
        this.resultRow = resultRow;
        return this;
    }
    
    @Override
    public StatementAdapter<T, ResultSet> bind(String name, Object value)
    {
        log(name, value);
        try
        {
            if (name.toLowerCase().startsWith("in:"))
            {
                setValue((Object[]) value);
            }
            else if (value instanceof java.util.Date)
            {
                setValue((java.util.Date) value);
            }
            else if (Enum.class.isInstance(value))
            {
                setValue((Enum<?>) value);
            }
            else if (value instanceof java.util.Calendar)
            {
                setValue((Calendar) value);
            }
            else
            {
                setValue(new Param(value, name, this.index));
            }
        }
        catch (SQLException e)
        {
            this.handlerException.handle(e);
        }
        return this;
    }
    
    @Override
    public StatementAdapter<T, ResultSet> bind(Param param)
    {
        Object value = param.getValueAs();
        log(param);
        try
        {
            if (value instanceof java.util.Date)
            {
                setValue((java.util.Date) value);
            }
            else if (Enum.class.isInstance(value))
            {
                setValue((Enum<?>) value);
            }
            else if (value instanceof java.util.Calendar)
            {
                setValue((Calendar) value);
            }
            else
            {
                setValue(param);
            }
        }
        catch (SQLException e)
        {
            this.handlerException.handle(e);
        }
        return this;
    }
    
    @Override
    public StatementAdapter<T, ResultSet> bind(Param... values)
    {
        for (int j = 0; j < values.length; j++)
        {
            Param v = values[j];
            bind(v);
        }
        return this;
    }
    
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    public List<T> rows()
    {
        ResultSet rs = null;
        ResultSetParser<T, ResultSet> rsParser = null;
        Groupable<T, ?> grouping = new NoGroupingBy<T, T>();
        List<T> list = Collections.emptyList();
        try
        {
            TimerKeeper.start();
            rs = stmt.executeQuery();
            queryable.getDynamicSql().getStats().add(TimerKeeper.clear());
            
            JdbcColumn<ResultSet>[] columns = getJdbcColumns(rs.getMetaData());
            setResultRow(columns);
            
            Transformable<T> transformable = resultRow.getTransformable();
            if (hasGroupingBy())
            {
                grouping = new GroupingBy(getGroupingBy(), queryable.getReturnType(), transformable);
            }
            rsParser = new ObjectResultSetParser(resultRow, grouping);
            list = rsParser.parser(rs);
        }
        catch (SQLException e)
        {
            queryable.getDynamicSql().getStats().add(e);
            handlerException.handle(e, e.getMessage());
        }
        finally
        {
            TimerKeeper.clear();
        }
        return list;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void bindKey()
    {
        String[] properties = queryable.getDynamicSql().asInsertable().getAutoGeneratedKey().getPropertiesAsArray();
        ObjectProxy<?> proxy = ObjectProxyFactory.of(queryable.getParams());
        Iterator<Object> it = autoKey.iterator();
        for (int i = 0; i < properties.length; i++)
            setValueOfKey(proxy, properties[i], it.next());
        /*
        try
        {
            if (!this.queryable.isTypeOfArray() && !this.queryable.isTypeOfCollection())
            {
                ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(queryable.getParams());
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                
                if (generatedKeys.next())
                {
                    for(int i=0; i<properties.length; i++)
                        setValue(proxy, properties[i], generatedKeys.getObject(i+1));
                }
                while(generatedKeys.next())
                {
                    for(int i=0; i<properties.length; i++)
                        setValue(proxy, properties[i], generatedKeys.getObject(i+1));
                }
            }
            else if(this.queryable.isTypeOfMap())
            {
                Map<String, Object> instance = (Map)queryable.getParams();
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next())
                {
                    for(int i=0; i<properties.length; i++)
                        instance.put(properties[i], generatedKeys.getObject(i+1));
                }
                while(generatedKeys.next())
                {
                    for(int i=0; i<properties.length; i++)
                        instance.put(properties[i], generatedKeys.getObject(i+1));
                }                
            }
            else
                handlerException.throwMessage("Cannot set auto generated key for collections or array instance of parameters at query [%s]", queryable.getName());
        }
        catch (SQLException sqle)
        {
            handlerException.handle(sqle);
        }
        */
    }
    
    @Override
    @SuppressWarnings("rawtypes")
    public StatementAdapter<T, ResultSet> with(AutoKey autoKey)
    {
        this.autoKey = autoKey;
        return this;
    }
    
    public int execute()
    {
        int ret = 0;
        try
        {
            TimerKeeper.start();
            ret = stmt.executeUpdate();
            queryable.getDynamicSql().getStats().add(TimerKeeper.clear());
        }
        catch (SQLException e)
        {
            queryable.getDynamicSql().getStats().add(e);
            handlerException.handle(e, e.getMessage());
        }
        return ret;
    }
    /*
    @Override
    public void batch()
    {
        try
        {
            stmt.addBatch();
        }
        catch (SQLException e)
        {
            handlerException.handle(e, e.getMessage());
        }
    }
    */
    @Override
    public int reset()
    {
        int before = index;
        index = 1;
        return before;
    }
    
    private void setValueOfKey(ObjectProxy<?> proxy, String property, Object value)
    {
        Object parsedValue = value;
        if (value instanceof java.sql.Time)
            parsedValue = new Date(((java.sql.Time) value).getTime());
        else if (value instanceof java.sql.Date)
            parsedValue = new Date(((java.sql.Date) value).getTime());
        else if (value instanceof java.sql.Timestamp)
            parsedValue = new Date(((java.sql.Timestamp) value).getTime());
        
        proxy.invoke(CAPITAL_SETTER.does(property), parsedValue);
    }
    
    private void setValue(Param param) throws SQLException
    {
        int i = currentIndex();
        stmt.setObject(i, param.getValueAs());
    }
    
    private void setValue(Object[] paramsIN) throws SQLException
    {
        int j = 0;
        for (; j < paramsIN.length; j++) {
            Convertible<Object, Object> convertible = getConverter(this.paramNames[index+j-1]);
            stmt.setObject(index + j, convertible.toJdbc(paramsIN[j]));
        }
    }
    
    private void setValue(Date value) throws SQLException
    {
        int i = currentIndex();
        Convertible<Object, Object> convertible = getConverter(this.paramNames[i-1]);
        if (convertible instanceof NoConverterType)
        {
            Convertible<java.util.Date, java.sql.Timestamp> convert2Timestamp = new DateAsSqlTimestampType();
            stmt.setObject(i, convert2Timestamp.toJdbc(value));
        }
        else
            stmt.setObject(i, convertible.toJdbc(value));
    }
    
    private void setValue(Calendar value) throws SQLException
    {
        int i = currentIndex();
        Convertible<java.util.Calendar, java.sql.Timestamp> convertible = new CalendarAsSqlTimestampType();
        stmt.setObject(i, convertible.toJdbc(value));
    }
    
    private void setValue(Enum<?> value) throws SQLException
    {
        int i = currentIndex();
        Convertible<Object, Object> convertible = getConverter(this.paramNames[i-1]);
        stmt.setObject(i, convertible.toJdbc(value));
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
        if(queryable.getParams() != null &&
           (queryable.isTypeOfPojo() || queryable.isTypeOfCollectionPojo() || queryable.isTypeOfArrayPojo()))
        {
            ObjectProxy<?> proxy = ObjectProxyFactory.of(queryable.getParams());
            convertible = ConvertibleFactory.toJdbc(new PropertyAccess(fieldName, queryable.getParams().getClass()), proxy);
        }
        return convertible;
    }
    
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    private void setResultRow(JdbcColumn<ResultSet>[] columns)
    {
        Class<?> returnType = queryable.getReturnType();
        if (resultRow != null)
            return;
        
        if (queryable.isScalar())
        {
            resultRow = new ScalarResultRow(columns);
        }
        else if (Map.class.isAssignableFrom(returnType))
        {
            resultRow = new MapResultRow(returnType, columns);
        }
        else if (Number.class.isAssignableFrom(returnType)) 
            // FIXME implements for date, calendar, boolean improve design
        {
            resultRow = new NumberResultRow(returnType, columns);
        }
        else if (String.class.isAssignableFrom(returnType))
        {
            resultRow = new StringResultRow(columns);
        }
        else if (Boolean.class.isAssignableFrom(returnType))
        {
            resultRow = new BooleanResultRow(columns);
        }
        else if (!hasOneToMany())
        {
            resultRow = new FlatObjectResultRow(returnType, columns);
        }
        else
        {
            resultRow = new PojoResultRow(returnType, columns, getOneToMany());
        }
    }
    
    private int currentIndex()
    {
        return (index++);
    }
    
    private void log(String name, Object value)
    {
        if (SQLLOG.isDebugEnabled())
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index,
                    name, MASKING.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }
    
    private void log(Param param)
    {
        //String name = this.paramNames[index-1];
        if (SQLLOG.isDebugEnabled())
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", param.getIndex(),
                    param.getName(), MASKING.mask(param.getName(), param.getValue()), (param.getValue() == null ? "NULL" : param.getValue().getClass()));
    }
    
    /**
     * Summarize the columns from SQL result in binary data or not.
     * @param metadata  object that contains information about the types and properties of the columns in a <code>ResultSet</code> 
     * @return Array of columns with name and index
     * @throws SQLException Errors that occurs when access {@code ResultSetMetaData} methods.
     */
    @SuppressWarnings("unchecked")
    private JdbcColumn<ResultSet>[] getJdbcColumns(ResultSetMetaData metadata) throws SQLException
    {
        JdbcColumn<ResultSet>[] columns = new JdbcColumn[metadata.getColumnCount()];
        for (int i = 0; i < columns.length; i++)
        {
            int columnNumber = i + 1;
            String columnName = getColumnName(metadata, columnNumber);
            int columnType = metadata.getColumnType(columnNumber);
            columns[i] = new DefaultJdbcColumn(columnNumber, columnName, columnType, queryable.getReturnType());
        }
        return columns;
    }
    
    private String getColumnName(ResultSetMetaData metaData, int columnIndex) throws SQLException
    {
        try
        {
            return metaData.getColumnLabel(columnIndex);
        }
        catch (SQLException e)
        {
            return metaData.getColumnName(columnIndex);
        }
    }
    
    @Override
    public void close()
    {
        if (this.stmt != null)
        {
            try
            {
                this.stmt.close();
            }
            catch (SQLException e)
            {
                LOG.warn("Cannot close prepared statement from query [{}]", this.queryable.getName(), e);
            }
        }
    }
    
    @Override
    public void setFetchSize(int rows)
    {
        try
        {
            this.stmt.setFetchSize(rows);
        }
        catch (SQLException e)
        {
            // TODO handler exception for Statement setFetchSize exception
            this.handlerException.handle(e);
        }
    }
    
    private boolean hasOneToMany()
    {
        return !queryable.getDynamicSql().asSelectable().getOneToMany().isEmpty();
    }
    
    private Set<OneToMany> getOneToMany()
    {
        return queryable.getDynamicSql().asSelectable().getOneToMany();
    }
    
    private boolean hasGroupingBy()
    {
        return !queryable.getDynamicSql().asSelectable().getGroupByAsList().isEmpty();
    }
    
    private List<String> getGroupingBy()
    {
        return queryable.getDynamicSql().asSelectable().getGroupByAsList();
    }
}