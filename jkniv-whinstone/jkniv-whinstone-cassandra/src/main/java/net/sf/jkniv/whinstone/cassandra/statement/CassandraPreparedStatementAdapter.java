package net.sf.jkniv.whinstone.cassandra.statement;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ColumnDefinitions;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;

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
import net.sf.jkniv.whinstone.JdbcColumn;
import net.sf.jkniv.whinstone.Param;
import net.sf.jkniv.whinstone.Queryable;
import net.sf.jkniv.whinstone.ResultRow;
import net.sf.jkniv.whinstone.ResultSetParser;
import net.sf.jkniv.whinstone.cassandra.CassandraColumn;
import net.sf.jkniv.whinstone.cassandra.LoggerFactory;
import net.sf.jkniv.whinstone.cassandra.RegisterCodec;
import net.sf.jkniv.whinstone.classification.Groupable;
import net.sf.jkniv.whinstone.classification.GroupingBy;
import net.sf.jkniv.whinstone.classification.NoGroupingBy;
import net.sf.jkniv.whinstone.classification.Transformable;
import net.sf.jkniv.whinstone.statement.AutoKey;
import net.sf.jkniv.whinstone.statement.StatementAdapter;
import net.sf.jkniv.whinstone.types.Convertible;
import net.sf.jkniv.whinstone.types.RegisterType;

/*
 * https://docs.datastax.com/en/developer/java-driver/3.1/manual/statements/prepared/
 * 
 * //FIXME unsupported method bound.setInet(...)
 * //FIXME unsupported method bound.setConsistencyLevel(ConsistencyLevel)
 * //FIXME unsupported method bound.setIdempotent(boolean)
 * //FIXME unsupported method bound.setBytes(...ByteBuffer)
 * //FIXME unsupported method bound.setInet(...InetAddress)
 * //FIXME unsupported method bound.setPartitionKeyToken(Token)
 * //FIXME unsupported method bound.setRoutingKey(ByteBuffer) 
 * //FIXME unsupported method bound.setToken(...Token)
 * //FIXME unsupported method bound.setUUID(...UUID)
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CassandraPreparedStatementAdapter<T, R> implements StatementAdapter<T, Row>
{
    private static final Logger  LOG = LoggerFactory.getLogger();
    private static final Logger SQLLOG = net.sf.jkniv.whinstone.cassandra.LoggerFactory.getLogger();
    private static final DataMasking  MASKING = LoggerFactory.getDataMasking();
    private static final Capitalize  CAPITAL_SETTER = CapitalNameFactory.getInstanceOfSetter();
    private final HandlerException  handlerException;
    private final PreparedStatement stmt;
    private final Class<T>          returnType;
    private final CqlSession        session;
    private final Queryable         queryable;
    private BoundStatement          bound;
    private int                     index;
    private ResultRow<T, Row>       resultRow;
    private boolean                 scalar;
    private AutoKey                 autoKey;
    private final RegisterType      registerType;
    private final RegisterCodec    registerCodec;
    
    public CassandraPreparedStatementAdapter(CqlSession session, PreparedStatement stmt, Queryable queryable, RegisterType registerType, RegisterCodec registerCodec)
    {
        this.stmt = stmt;
        this.session = session;
        this.registerType = registerType;
        this.registerCodec = registerCodec;
        this.bound = stmt.bind();
        this.handlerException = new HandlerException(RepositoryException.class, "Cannot set parameter [%s]");
        this.queryable = queryable;
        this.returnType = (Class<T>)queryable.getReturnType();
        this.reset();
    }
    
    /**
     * Creates a new BoundStatement object for this prepared statement. 
     * This method do not bind any values to any of the prepared variables.
     */
    public void reBound() 
    {
        this.bound = stmt.bind();
    }
    
    @Override
    public StatementAdapter<T, Row> with(ResultRow<T, Row> resultRow)
    {
        this.resultRow = resultRow;
        return this;
    }
    
    @Override
    public StatementAdapter<T, Row> bind(String name, Object value)
    {
        log(name, value);
        if (name.toLowerCase().startsWith("in:"))
        {
            try
            {
                setValueIN((Object[]) value);
                return this;
            }
            catch (SQLException e)
            {
                this.handlerException.handle(e);// FIXME handler default message with custom params
            }
        }
        return bindInternal(value);
    }
    
    @Override
    public StatementAdapter<T, Row> bind(Param param)
    {
        Object value = param.getValueAs();
        log(param);
        try
        {
            bindInternal(value);
        }
        catch (Exception e)
        {
            this.handlerException.handle(e);// FIXME handler default message with custom params
        }
        return this;
    }
    
    @Override
    public StatementAdapter<T, Row> bind(Param... values)
    {
        //this.bound = stmt.bind(values);
        //this.index += values.length-1;
        for (int j=0; j<values.length; j++)
        {
            Param v = values[j];
            bind(v);
        }
        return this;
    }
    /*
    @Override
    public void batch()
    {
        // TODO implements batch https://docs.datastax.com/en/drivers/java/3.0/com/datastax/driver/core/BatchStatement.html
        // TODO implements batch https://docs.datastax.com/en/drivers/python/3.2/api/cassandra/query.html
        // TODO implements batch https://docs.datastax.com/en/cql/3.3/cql/cql_using/useBatch.html
        // TODO implements batch https://docs.datastax.com/en/cql/3.3/cql/cql_using/useBatchGoodExample.html
        // TODO https://www.datastax.com/dev/blog/client-side-improvements-in-cassandra-2-0
    }
    */
    
    public List<T> rows()
    {
        ResultSet rs = null;
        ResultSetParser<T, ResultSet> rsParser = null;
        Groupable<T, ?> grouping = new NoGroupingBy<T, T>();
        List<T> list = Collections.emptyList();
        try
        {
            TimerKeeper.start();
            rs = session.execute(bound);
            queryable.getDynamicSql().getStats().add(TimerKeeper.clear());
            
            JdbcColumn<Row>[] columns = getJdbcColumns(rs.getColumnDefinitions());
            setResultRow(columns);
            //LOG.debug("AvailableWithoutFetching={}, FullyFetched={}, Exhausted={}", rs.getAvailableWithoutFetching(), rs.isFullyFetched(), rs.isExhausted());
            Transformable<T> transformable = resultRow.getTransformable();
            if (hasGroupingBy())
            {
                grouping = new GroupingBy(getGroupingBy(), returnType, transformable);
            }
            rsParser = new ObjectResultSetParser(resultRow, grouping);
            list = rsParser.parser(rs);
        }
        catch (SQLException e)
        {
            queryable.getDynamicSql().getStats().add(e);
            handlerException.handle(e, e.getMessage());
        }
        finally {
            TimerKeeper.clear();            
        }
        return list;
    }
    
    @Override
    public void bindKey()
    {
        String[] properties = queryable.getDynamicSql().asInsertable().getAutoGeneratedKey().getPropertiesAsArray();
        ObjectProxy<?> proxy = ObjectProxyFactory.of((Object)queryable.getParams());
        Iterator<Object> it = autoKey.iterator();
        for(int i=0; i<properties.length; i++)
            setValueOfKey(proxy, properties[i], it.next());
    }
    
    private void setValueOfKey(ObjectProxy<?> proxy, String property, Object value)
    {
        Convertible<Object, Object> converter = registerType.toJdbc(new PropertyAccess(property, proxy.getTargetClass()), proxy);
        Object parsedValue = value;
        if (!converter.getType().isInstance(value))
            parsedValue = converter.toAttribute(value);
        proxy.invoke(CAPITAL_SETTER.does(property), parsedValue);
    }

    @Override
    public StatementAdapter<T, Row> with(AutoKey generateKey)
    {
        this.autoKey = generateKey;
        return this;
    }
    
    @Override
    public int execute()
    {
        session.execute(bound);
        return Statement.SUCCESS_NO_INFO; // FIXME design Statement.SUCCESS_NO_INFO
    }
    
    @Override
    public int reset()
    {
        int before = (index);
        index = 0;
        reBound();
        return before;
    }
    
    private void setValueIN(Object[] paramsIN) throws SQLException
    {
        int j = 0;
        for (; j < paramsIN.length; j++)
            bindInternal(paramsIN[j]);
        //indexIN = indexIN + j;
        /*
        int j = 0;
        for (; j < paramsIN.length; j++)
            stmt.setObject(index+indexIN + j, paramsIN[j]);
        indexIN = indexIN + j;
         */
    }
    
    /*******************************************************************************/
    private StatementAdapter<T, Row> bindInternal(Object value)
    {
        if (value == null)
        {       
            setToNull();
            return this;
        }
        try
        {
            String classNameValue = value.getClass().getName();
            if (Enum.class.isInstance(value))
                setInternalValue((Enum<?>) value);
            else if (value instanceof List)
                setInternalValue((List) value);
            else if (value instanceof Set)
                setInternalValue((Set) value);
            else if (value instanceof Map)
                setInternalValue((Map) value);
            else if (value instanceof Instant)
                setValue((Instant) value);
            else if (value instanceof LocalDate)
                setValue((LocalDate) value);
            else if (value instanceof LocalTime)
                setValue((LocalTime) value);
            else if (value instanceof Date)
                setValue((Date) value);
            
            
            else if (classNameValue.equals("java.time.Instant"))
                bound.set(currentIndex(), value, registerCodec.getCodec("InstantCodec").instance);
            else if (classNameValue.equals("java.time.LocalDate"))
                bound.set(currentIndex(), value, registerCodec.getCodec("LocalDateCodec").instance);
            else if (classNameValue.equals("java.time.LocalDateTime"))
                bound.set(currentIndex(), value, registerCodec.getCodec("LocalDateTimeCodec").instance);
            else if (classNameValue.equals("java.time.LocalTime"))
                bound.set(currentIndex(), value, registerCodec.getCodec("LocalTimeCodec").instance);
            else if (classNameValue.equals("java.time.ZonedDateTime"))
                bound.set(currentIndex(), value, registerCodec.getCodec("jkd8.ZonedDateTimeCodec").instance);
            else if (classNameValue.equals("java.util.Optional"))
                bound.set(currentIndex(), value, registerCodec.getCodec("jkd8.OptionalCodec").instance);
            else if (classNameValue.equals("java.time.ZoneId"))
                bound.set(currentIndex(), value, registerCodec.getCodec("jkd8.ZoneIdCodec").instance);
            else
                setObjectValue(value);
            /*
            String classNameValue = value.getClass().getName();
            if (value instanceof String)
                setInternalValue((String) value);
            else if (value instanceof Integer)
                setInternalValue((Integer) value);
            else if (value instanceof Long)
                setInternalValue((Long) value);
            else if (value instanceof Double)
                setInternalValue((Double) value);
            else if (value instanceof Float)
                setInternalValue((Float) value);
            else if (value instanceof Boolean)
                setInternalValue((Boolean) value);
            else if (value instanceof BigDecimal)
                setInternalValue((BigDecimal) value);
            else if (value instanceof Date)
                setInternalValue((Date) value);
            else if (value instanceof java.util.Calendar)
                setInternalValue((Calendar) value);
            else if (Enum.class.isInstance(value))
                setInternalValue((Enum<?>) value);
            else if (value instanceof List)
                setInternalValue((List) value);
            else if (value instanceof Set)
                setInternalValue((Set) value);
            else if (value instanceof Map)
                setInternalValue((Map) value);
            else if (value instanceof com.datastax.driver.core.Duration)
                setValue((com.datastax.driver.core.Duration)value);
            else if (classNameValue.equals("java.time.Instant"))
                bound.set(currentIndex(), value, registerCodec.getCodec("InstantCodec").instance);
            else if (classNameValue.equals("java.time.LocalDate"))
                bound.set(currentIndex(), value, registerCodec.getCodec("LocalDateCodec").instance);
            else if (classNameValue.equals("java.time.LocalDateTime"))
                bound.set(currentIndex(), value, registerCodec.getCodec("LocalDateTimeCodec").instance);
            else if (classNameValue.equals("java.time.LocalTime"))
                bound.set(currentIndex(), value, registerCodec.getCodec("LocalTimeCodec").instance);
            else if (classNameValue.equals("java.time.ZonedDateTime"))
                bound.set(currentIndex(), value, registerCodec.getCodec("jkd8.ZonedDateTimeCodec").instance);
            else if (classNameValue.equals("java.util.Optional"))
                bound.set(currentIndex(), value, registerCodec.getCodec("jkd8.OptionalCodec").instance);
            else if (classNameValue.equals("java.time.ZoneId"))
                bound.set(currentIndex(), value, registerCodec.getCodec("jkd8.ZoneIdCodec").instance);
            else if (value instanceof BigInteger)
                setInternalValue((BigInteger) value);
            else if (value instanceof Short)
                setInternalValue((Short) value);
            else if (value instanceof com.datastax.driver.core.LocalDate)
                setInternalValue((com.datastax.driver.core.LocalDate) value);
            else if (value instanceof Byte)
                setInternalValue((Byte) value);
            else if (value instanceof ByteBuffer)
                setInternalValue((ByteBuffer) value);
            else
            {
                LOG.warn("CANNOT Set SQL Parameter from index [{}] with value of [{}] type of [{}]", (index), 
                        value, (value == null ? "NULL" : value.getClass()));

                //setValue(value);
            }
            */
        }
        catch (Exception e)
        {
            this.handlerException.handle(e);// FIXME handler default message with custom params
        }
        return this;
    }

    private void setObjectValue(Object value)
    {
        ObjectProxy proxy = ObjectProxyFactory.of(value);
        bound = bound.set(currentIndex(), value, proxy.getTargetClass());
    }
    
    private void setValue(Instant value)
    {
        bound = bound.setInstant(currentIndex(), value);
    }
    private void setValue(LocalDate value)
    {
        bound = bound.setLocalDate(currentIndex(), value);
    }
    private void setValue(LocalTime value)
    {
        bound = bound.setLocalTime(currentIndex(), value);
    }
    private void setValue(Date value)
    {
        bound = bound.setInstant(currentIndex(), value.toInstant());
    }
//    private void setValue(com.datastax.driver.core.Duration value)
//    {
//        bound.set(currentIndex(), value, com.datastax.driver.core.Duration.class);
//    }
    
//    private void setInternalValue(com.datastax.driver.core.LocalDate value)
//    {
//        bound.setDate(currentIndex(), value);
//    }
    
    private void setInternalValue(Calendar value)
    {
        bound = bound.set(currentIndex(), value.getTime(), Date.class);
    }
    
    private void setInternalValue(Date value)
    {
        bound = bound.set(currentIndex(), value, Date.class);
    }
    
    private void setInternalValue(Integer value)
    {
        bound = bound.setInt(currentIndex(), value);
    }
    
    private void setInternalValue(Long value)
    {
        bound = bound.setLong(currentIndex(), value);
    }
    
    private void setInternalValue(Float value)
    {
        bound = bound.setFloat(currentIndex(), value);
    }
    
    private void setInternalValue(Double value)
    {
        bound = bound.setDouble(currentIndex(), value);
    }
    
    private void setInternalValue(Short value)
    {
        bound = bound.setShort(currentIndex(), value);
    }
    
    private void setInternalValue(Boolean value)
    {
        bound = bound.setBool(currentIndex(), value);
    }
    
    private void setInternalValue(Byte value)
    {
        bound = bound.setByte(currentIndex(), value);
    }

    private void setInternalValue(ByteBuffer value)
    {
        bound = bound.setByteBuffer(currentIndex(), value);
    }
    
    private void setInternalValue(BigDecimal value)
    {
        bound = bound.setBigDecimal(currentIndex(), value);
    }
    
    private void setInternalValue(BigInteger value)
    {
        bound = bound.setBigInteger(currentIndex(), value);
    }
    
    private void setInternalValue(String value)
    {
        bound = bound.setString(currentIndex(), value);
    }
    
    private void setToNull()
    {
        bound = bound.setToNull(currentIndex());
    }
    
    private BoundStatement setInternalValue(Enum<?> value) throws SQLException
    {
        // FIXME design converter to allow save ordinal value or other value from enum
        return bound.setString(currentIndex(), value.name());
    }

    private void setInternalValue(List<?> value) throws SQLException
    {
        bound = bound.set(currentIndex(), value, List.class);
    }

    private void setInternalValue(Map<?,?> value) throws SQLException
    {
        bound = bound.set(currentIndex(), value, Map.class);
    }

    private void setInternalValue(Set<T> value) throws SQLException
    {
        bound = bound.set(currentIndex(), value, Set.class);
    }

    private int currentIndex()
    {
        return (index++);
    }
    
    /*******************************************************************************/
    
    private void setResultRow(JdbcColumn<Row>[] columns)
    {
        if (resultRow != null)
        {
            resultRow.setColumns(columns);
            return;
        }
        if (scalar)
            resultRow = new ScalarResultRow(columns);
        else if (Map.class.isAssignableFrom(returnType))
            resultRow = new MapResultRow(returnType, columns);
        else if (Number.class.isAssignableFrom(returnType)) // FIXME implements for date, calendar, boolean improve design
            resultRow = new NumberResultRow(returnType, columns);
        else if (String.class.isAssignableFrom(returnType))
            resultRow = new StringResultRow(columns);
        else if (!hasOneToMany())
            resultRow = new FlatObjectResultRow(returnType, columns);
        else
            resultRow = new PojoResultRow(returnType, columns, getOneToMany());
    }
    
    private void log(String name, Object value)
    {
        if (SQLLOG.isDebugEnabled())
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index, name,
                    MASKING.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }
    
    private void log(Param param)
    {
        if (SQLLOG.isDebugEnabled())
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index, param.getName(),
                    MASKING.mask(param.getName(), param.getValueAs()), (param.getValueAs() == null ? "NULL" : param.getValueAs().getClass()));
    }
    
    /**
     * Summarize the columns from SQL result in binary data or not.
     * @param metadata  object that contains information about the types and properties of the columns in a <code>ResultSet</code> 
     * @return Array of columns with name and index
     */
    private JdbcColumn<Row>[] getJdbcColumns(ColumnDefinitions metadata)
    {
        JdbcColumn<Row>[] columns = new JdbcColumn[metadata.size()];
        
        for (int i = 0; i < columns.length; i++)
        {
            String columnName = metadata.get(i).getName().asInternal();//getColumnName(metadata, columnNumber);
            //columns[i] = new CassandraColumn(i, columnName, metadata.getType(i).getName(), registerType, queryable.getReturnType());
            columns[i] = new CassandraColumn(i, columnName, metadata.get(i).getType(), registerType, queryable.getReturnType());
        }
        return columns;
    }

    @Override
    public void close()
    {
        // TODO how to close cassandra statement ?
    }
    
    @Override
    public void setFetchSize(int rows)
    {
        LOG.warn("Cassandra " + stmt.getClass() + " doesn't support fetch size!");
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
