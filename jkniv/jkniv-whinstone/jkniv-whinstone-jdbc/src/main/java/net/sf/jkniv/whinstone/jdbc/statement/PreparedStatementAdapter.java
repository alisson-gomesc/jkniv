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
import net.sf.jkniv.experimental.converters.SqlDateConverter;
import net.sf.jkniv.reflect.beans.MethodName;
import net.sf.jkniv.reflect.beans.MethodNameFactory;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.KeyGeneratorType;
import net.sf.jkniv.sqlegance.OneToMany;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.ResultSetParser;
import net.sf.jkniv.whinstone.classification.Groupable;
import net.sf.jkniv.whinstone.classification.GroupingBy;
import net.sf.jkniv.whinstone.classification.NoGroupingBy;
import net.sf.jkniv.whinstone.classification.Transformable;
import net.sf.jkniv.whinstone.jdbc.DefaultJdbcColumn;
import net.sf.jkniv.whinstone.jdbc.LoggerFactory;
import net.sf.jkniv.whinstone.jdbc.result.BooleanResultRow;
import net.sf.jkniv.whinstone.jdbc.result.FlatObjectResultRow;
import net.sf.jkniv.whinstone.jdbc.result.MapResultRow;
import net.sf.jkniv.whinstone.jdbc.result.NumberResultRow;
import net.sf.jkniv.whinstone.jdbc.result.ObjectResultSetParser;
import net.sf.jkniv.whinstone.jdbc.result.PojoResultRow;
import net.sf.jkniv.whinstone.jdbc.result.ScalarResultRow;
import net.sf.jkniv.whinstone.jdbc.result.StringResultRow;
import net.sf.jkniv.whinstone.statement.AutoKey;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

public class PreparedStatementAdapter<T, R> implements StatementAdapter<T, ResultSet>
{
    private static final Logger  LOG = LoggerFactory.getLogger();
    private static final Logger SQLLOG = net.sf.jkniv.whinstone.jdbc.LoggerFactory.getLogger();
    private static final DataMasking  MASKING = LoggerFactory.getDataMasking();
    private static final MethodName SETTER = MethodNameFactory.getInstanceSetter();

    private final PreparedStatement stmt;
    private final HandlerException  handlerException;
    private final SqlDateConverter  dtConverter;
    private int                     index, indexIN;
    private Class<T>                returnType;
    private ResultRow<T, ResultSet> resultRow;
    private boolean                 scalar;
    private Set<OneToMany>          oneToManies;
    private List<String>            groupingBy;
    private KeyGeneratorType        keyGeneratorType;
    private Queryable               queryable;
    private AutoKey                 autoKey;
    
    @SuppressWarnings("unchecked")
    public PreparedStatementAdapter(PreparedStatement stmt, Queryable queryable)
    {
        this.stmt = stmt;
        this.handlerException = new HandlerException(RepositoryException.class, "Cannot set parameter [%s] value [%s]");
        this.dtConverter = new SqlDateConverter();
        this.oneToManies = Collections.emptySet();
        this.groupingBy = Collections.emptyList();
        this.scalar = false;
        this.queryable = queryable;
        this.returnType = (Class<T>) Map.class;

        if(queryable != null)
        {
            if (queryable.isScalar())
                scalar();
            
            if (queryable.getReturnType() != null)
                returnType = (Class<T>)queryable.getReturnType();
            else if (queryable.getDynamicSql().getReturnTypeAsClass() != null)
                returnType = (Class<T>)queryable.getDynamicSql().getReturnTypeAsClass();
        }
        this.reset();
    }
    
    public StatementAdapter<T, ResultSet> returnType(Class<T> returnType)
    {
        this.returnType = returnType;
        return this;
    }
    
    
    public StatementAdapter<T, ResultSet> resultRow(ResultRow<T, ResultSet> resultRow)
    {
        this.resultRow = resultRow;
        return this;
    }
    
    public StatementAdapter<T, ResultSet> scalar()
    {
        this.scalar = true;
        return this;
    }
    
    public StatementAdapter<T, ResultSet> oneToManies(Set<OneToMany> oneToManies)
    {
        this.oneToManies = oneToManies;
        return this;
    }
    
    public StatementAdapter<T, ResultSet> groupingBy(List<String> groupingBy)
    {
        this.groupingBy = groupingBy;
        return this;
    }
    
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
                setValue(value);
            }
        }
        catch (SQLException e)
        {
            this.handlerException.handle(e);// FIXME handler default message with custom params
        }
        return this;
    }
    
    @Override
    //public StatementAdapter<T, ResultSet> bind(int position, Object value)
    public StatementAdapter<T, ResultSet> bind(Object value)
    {
        //this.index = position;
        log(value);
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
                setValue(value);
            }
        }
        catch (SQLException e)
        {
            this.handlerException.handle(e);// FIXME handler default message with custom params
        }
        return this;
    }
    
    @Override
    public StatementAdapter<T, ResultSet> bind(Object... values)
    {
        for (; index < values.length;)
        {
            Object v = values[index];
            bind(v);
        }
        return this;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
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
            if(queryable != null)// TODO design improve for use sql stats
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
            if(queryable != null) // TODO design improve for use sql stats
                queryable.getDynamicSql().getStats().add(e);
            handlerException.handle(e, e.getMessage());
        }
        return list;
    }
    
    @Override
    public void bindKey()
    {
        String[] properties = queryable.getDynamicSql().asInsertable().getAutoGeneratedKey().getPropertiesAsArray();
        ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(queryable.getParams());
        Iterator<Object> it = autoKey.iterator();
        for(int i=0; i<properties.length; i++)
            setValueOfKey(proxy, properties[i], it.next());
        
        /*
        try
        {
            if (!this.queryable.isTypeOfArray() && !this.queryable.isTypeOfCollection())// TODO test generate key params input as collection or array
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
            else if(this.queryable.isTypeOfMap())// TODO test generate key params input as Map
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
    
    @Override
    public int reset()
    {
        int before = (index+indexIN);
        index = 1;
        indexIN = 0;
        return before;
    }
    /*
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    private void setValue(String name, Object paramValue)
    {
        log(name, paramValue);
        try
        {
            if (name.toLowerCase().startsWith("in:"))
            {
                int j = 0;
                Object[] paramsIN = (Object[]) paramValue;
                if (paramsIN == null)
                    throw new ParameterException("Cannot set parameter [" + name + "] from IN clause with NULL");
                
                for (; j < paramsIN.length; j++)
                    stmt.setObject(j + index, paramsIN[j]);
                indexIN = indexIN + j;
            }
            else
            {
                if (paramValue == null)
                {
                    stmt.setObject(index + indexIN, paramValue);
                }
                else if (paramValue.getClass().isEnum())
                {
                    ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(paramValue);
                    stmt.setObject(index + indexIN, proxy.invoke("name"));
                }
                else if (paramValue instanceof Date)
                {
                    SqlDateConverter converter = new SqlDateConverter();
                    java.sql.Timestamp timestamp = converter.convert(java.sql.Timestamp.class, paramValue);
                    stmt.setObject(index + indexIN, timestamp);
                }
                else
                {
                    stmt.setObject(index + indexIN, paramValue);
                }
            }
        }
        catch (SQLException e)
        {
            throw new RepositoryException(
                    "Cannot set parameter [" + name + "] value [" + sqlLogger.mask(name, paramValue) + "]", e);
        }
    }
    */
    
    private void setValueOfKey(ObjectProxy<?> proxy, String property, Object value)
    {
        Object parsedValue = value;
        
        if(value instanceof java.sql.Time)
            parsedValue = new Date(((java.sql.Time)value).getTime());
        else if (value instanceof java.sql.Date)
            parsedValue = new Date(((java.sql.Date)value).getTime());
        else if (value instanceof java.sql.Timestamp)
            parsedValue = new Date(((java.sql.Timestamp)value).getTime());
        
        proxy.invoke(SETTER.capitalize(property), parsedValue);
    }

    private void setValue(Object value) throws SQLException
    {
        stmt.setObject(currentIndex(), value);
    }
    
    private void setValue(Object[] paramsIN) throws SQLException
    {
        int j = 0;
        for (; j < paramsIN.length; j++)
            stmt.setObject(index+indexIN + j, paramsIN[j]);
        indexIN = indexIN + j;
    }
    
    private void setValue(Date value) throws SQLException
    {
        java.sql.Timestamp timestamp = dtConverter.convert(java.sql.Timestamp.class, value);
        stmt.setObject(currentIndex(), timestamp);
    }
    
    private void setValue(Calendar value) throws SQLException
    {
        java.sql.Timestamp timestamp = dtConverter.convert(java.sql.Timestamp.class, value.getTime());
        stmt.setObject(currentIndex(), timestamp);
    }
    
    private void setValue(Enum<?> value) throws SQLException
    {
        //ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(value);
        //stmt.setObject(currentIndex(), proxy.invoke("name"));
        stmt.setObject(currentIndex(), value.name());// FIXME design converter to allow save ordinal value or other value from enum
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void setResultRow(JdbcColumn<ResultSet>[] columns)
    {
        //Class returnType = queryable.getReturnType();
        if (resultRow != null)
            return;
        
        if (scalar)
        {
            resultRow = new ScalarResultRow(columns);
        }
        else if (Map.class.isAssignableFrom(returnType))
        {
            resultRow = new MapResultRow(returnType, columns);
        }
        else if (Number.class.isAssignableFrom(returnType)) // FIXME implements for date, calendar, boolean improve design
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
        else if (oneToManies.isEmpty())
        {
            resultRow = new FlatObjectResultRow(returnType, columns);
        }
        else
        {
            resultRow = new PojoResultRow(returnType, columns, oneToManies);
        }
    }
    
    private int currentIndex()
    {
        return ( index++ +indexIN);
    }
    
    private void log(String name, Object value)
    {
        if (SQLLOG.isDebugEnabled())
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index,
                        name, MASKING.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }
    
    private void log(Object value)
    {
        String name = String.valueOf(index+indexIN);
        if (SQLLOG.isDebugEnabled())
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index,
                        name, MASKING.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }

    /**
     * Summarize the columns from SQL result in binary data or not.
     * @param metadata  object that contains information about the types and properties of the columns in a <code>ResultSet</code> 
     * @return Array of columns with name and index
     * @throws SQLException Errors that occurs when access {@code ResultSetMetaData} methods.
     */
    private JdbcColumn[] getJdbcColumns(ResultSetMetaData metadata) throws SQLException
    {
        JdbcColumn[] columns = new JdbcColumn[metadata.getColumnCount()];
        
        for (int i = 0; i < columns.length; i++)
        {
            int columnNumber = i + 1;
            String columnName = getColumnName(metadata, columnNumber);
            int columnType = metadata.getColumnType(columnNumber);
            //boolean binaryData = false;
            //if (columnType == Types.CLOB || columnType == Types.BLOB)
            //    binaryData = true;
            columns[i] = new DefaultJdbcColumn(columnNumber, columnName, columnType);
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
            this.handlerException.handle(e);// TODO design handlerException for Statement setFetchSize exception
        }
    }
}
