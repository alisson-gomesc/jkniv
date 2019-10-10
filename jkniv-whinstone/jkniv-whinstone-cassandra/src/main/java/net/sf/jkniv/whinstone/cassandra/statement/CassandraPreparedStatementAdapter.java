package net.sf.jkniv.whinstone.cassandra.statement;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import net.sf.jkniv.exception.HandlerException;
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
import net.sf.jkniv.whinstone.cassandra.CassandraColumn;
import net.sf.jkniv.whinstone.cassandra.LoggerFactory;
import net.sf.jkniv.whinstone.classification.Groupable;
import net.sf.jkniv.whinstone.classification.GroupingBy;
import net.sf.jkniv.whinstone.classification.NoGroupingBy;
import net.sf.jkniv.whinstone.classification.Transformable;
import net.sf.jkniv.whinstone.statement.AutoKey;
import net.sf.jkniv.whinstone.statement.StatementAdapter;

/**
 * https://docs.datastax.com/en/developer/java-driver/3.1/manual/statements/prepared/
 * 
 * //FIXME unsupported method bound.setMap(...Map)
 * //FIXME unsupported methodbound.setList(...List)
 * //FIXME unsupported methodbound.setInet(...)
 * //FIXME unsupported methodbound.setSet(...Set)
 * //FIXME unsupported methodbound.setConsistencyLevel(ConsistencyLevel)
 * //FIXME unsupported methodbound.setIdempotent(boolean)
 * //FIXME unsupported methodbound.setBytes(...ByteBuffer)
 * //FIXME unsupported methodbound.setInet(...InetAddress)
 * //FIXME unsupported methodbound.setPartitionKeyToken(Token)
 * //FIXME unsupported methodbound.setRoutingKey(ByteBuffer) 
 * //FIXME unsupported methodbound.setToken(...Token)
 * //FIXME unsupported methodbound.setUUID(...UUID)
 * //FIXME unsupported methodbound.set
 * //FIXME unsupported methodbound.set
 * //FIXME unsupported methodbound.set
 * //FIXME unsupported methodbound.set
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class CassandraPreparedStatementAdapter<T, R> implements StatementAdapter<T, Row>
{
    private static final Logger  LOG = LoggerFactory.getLogger();
    private static final Logger SQLLOG = net.sf.jkniv.whinstone.cassandra.LoggerFactory.getLogger();
    private static final DataMasking  MASKING = LoggerFactory.getDataMasking();
    private static final MethodName SETTER = MethodNameFactory.getInstanceSetter();
    private final HandlerException  handlerException;
    private final PreparedStatement stmt;
    private BoundStatement          bound;
    //private final SqlDateConverter  dtConverter;
    
    private int                     index, indexIN;
    private Class<T>                returnType;
    private ResultRow<T, Row>       resultRow;
    private boolean                 scalar;
    private Set<OneToMany>          oneToManies;
    private List<String>            groupingBy;
    private KeyGeneratorType        keyGeneratorType;
    private final Session           session;
    private final Queryable         queryable;
    private AutoKey                 autoKey;
    
    @SuppressWarnings("unchecked")
    public CassandraPreparedStatementAdapter(Session session, PreparedStatement stmt, Queryable queryable)
    {
        this.stmt = stmt;
        this.session = session;
        this.bound = stmt.bind();
        //this.dtConverter = new SqlDateConverter();
        this.oneToManies = Collections.emptySet();
        this.groupingBy = Collections.emptyList();
        this.handlerException = new HandlerException(RepositoryException.class, "Cannot set parameter [%s] value [%s]");
        this.queryable = queryable;
        this.returnType = (Class<T>) Map.class;
        if (queryable.getReturnType() != null)
            returnType = (Class<T>)queryable.getReturnType();
        else if (queryable.getDynamicSql().getReturnTypeAsClass() != null)
            returnType = (Class<T>)queryable.getDynamicSql().getReturnTypeAsClass();

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
    public StatementAdapter<T, Row> returnType(Class<T> returnType)
    {
        this.returnType = returnType;
        return this;
    }
    
    @Override
    public StatementAdapter<T, Row> resultRow(ResultRow<T, Row> resultRow)
    {
        this.resultRow = resultRow;
        return this;
    }
    
    @Override
    public StatementAdapter<T, Row> scalar()
    {
        this.scalar = true;
        return this;
    }
    
    @Override
    public StatementAdapter<T, Row> oneToManies(Set<OneToMany> oneToManies)
    {
        this.oneToManies = oneToManies;
        return this;
    }
    
    @Override
    public StatementAdapter<T, Row> groupingBy(List<String> groupingBy)
    {
        this.groupingBy = groupingBy;
        return this;
    }
    
    @Override
    public StatementAdapter<T, Row> keyGeneratorType(KeyGeneratorType keyGeneratorType)
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
    public StatementAdapter<T, Row> bind(Object value)
    {
        //this.index = position;
        log(value);
        try
        {
            if (value instanceof java.util.Date)
            {
                setInternalValue((java.util.Date) value);
            }
            else if (Enum.class.isInstance(value))
            {
                setInternalValue((Enum<?>) value);
            }
            else if (value instanceof java.util.Calendar)
            {
                setInternalValue((Calendar) value);
            }
            else
            {
                bindInternal(value);
            }
        }
        catch (SQLException e)
        {
            this.handlerException.handle(e);// FIXME handler default message with custom params
        }
        return this;
    }
    
    @Override
    public StatementAdapter<T, Row> bind(Object... values)
    {
        //this.bound = stmt.bind(values);
        //this.index += values.length-1;
        for (int j=0; j<values.length; j++)
        {
            Object v = values[j];
            bind(v);
        }
        return this;
    }
    
    @Override
    public void batch()
    {
        // TODO implements batch https://docs.datastax.com/en/drivers/java/3.0/com/datastax/driver/core/BatchStatement.html
        // TODO implements batch https://docs.datastax.com/en/drivers/python/3.2/api/cassandra/query.html
        // TODO implements batch https://docs.datastax.com/en/cql/3.3/cql/cql_using/useBatch.html
        // TODO implements batch https://docs.datastax.com/en/cql/3.3/cql/cql_using/useBatchGoodExample.html
        // TODO https://www.datastax.com/dev/blog/client-side-improvements-in-cassandra-2-0
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
            rs = session.execute(bound);
            JdbcColumn<Row>[] columns = getJdbcColumns(rs.getColumnDefinitions());
            setResultRow(columns);
            //LOG.debug("AvailableWithoutFetching={}, FullyFetched={}, Exhausted={}", rs.getAvailableWithoutFetching(), rs.isFullyFetched(), rs.isExhausted());
            Transformable<T> transformable = resultRow.getTransformable();
            if (!groupingBy.isEmpty())
            {
                grouping = new GroupingBy(groupingBy, returnType, transformable);
            }
            rsParser = new ObjectResultSetParser(resultRow, grouping);
            list = rsParser.parser(rs);
        }
        catch (SQLException e)
        {
            handlerException.handle(e, e.getMessage());
        }
        return list;
    }
    
    @Override
    public void bindKey()
    {
        String[] properties = queryable.getDynamicSql().asInsertable().getAutoGeneratedKey().getPropertiesAsArray();
        ObjectProxy<?> proxy = ObjectProxyFactory.of(queryable.getParams());
        Iterator<Object> it = autoKey.iterator();
        for(int i=0; i<properties.length; i++)
            setValueOfKey(proxy, properties[i], it.next());
    }
    
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

    
    @Override
    public StatementAdapter<T, Row> with(AutoKey generateKey)
    {
        this.autoKey = generateKey;
        return this;
    }
    
    public int execute()
    {
        session.execute(bound);
        return Statement.SUCCESS_NO_INFO; // FIXME design Statement.SUCCESS_NO_INFO
    }
    
    @Override
    public int reset()
    {
        int before = (index+indexIN);
        index = 0;
        indexIN = 0;
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
    @SuppressWarnings("rawtypes")
    private StatementAdapter<T, Row> bindInternal(Object value)
    {
        try
        {
            if (value == null)
                setToNull();
            else if (value instanceof String)
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
            else if (value instanceof BigInteger)
                setInternalValue((BigInteger) value);
            else if (value instanceof Short)
                setInternalValue((Short) value);
            else if (value instanceof Date)
                setInternalValue((Date) value);
            else if (value instanceof java.util.Calendar)
                setInternalValue((Calendar) value);
            else if (value instanceof com.datastax.driver.core.LocalDate)
                setInternalValue((com.datastax.driver.core.LocalDate) value);
            else if (Enum.class.isInstance(value))
                setInternalValue((Enum<?>) value);
            else if (value instanceof Byte)
                setInternalValue((Byte) value);
            else if (value instanceof List)
                setInternalValue((List) value);
            else if (value instanceof Set)
                setInternalValue((Set) value);            
            else if (value instanceof Map)
                setInternalValue((Map) value);            
            else
            {
                LOG.warn("CANNOT Set SQL Parameter from index [{}] with value of [{}] type of [{}]", (index+indexIN), 
                        value, (value == null ? "NULL" : value.getClass()));

                //setValue(value);
            }
        }
        catch (SQLException e)
        {
            this.handlerException.handle(e);// FIXME handler default message with custom params
        }
        return this;
    }
    
    private void setInternalValue(com.datastax.driver.core.LocalDate value)
    {
        bound.setDate(currentIndex(), value);
    }
    
    private void setInternalValue(Calendar value)
    {
        bound.setTimestamp(currentIndex(), value.getTime());
    }
    
    private void setInternalValue(Date value)
    {
        bound.setTimestamp(currentIndex(), value);
    }
    
    private void setInternalValue(Integer value)
    {
        bound.setInt(currentIndex(), value);
    }
    
    private void setInternalValue(Long value)
    {
        bound.setLong(currentIndex(), value);
    }
    
    private void setInternalValue(Float value)
    {
        bound.setFloat(currentIndex(), value);
    }
    
    private void setInternalValue(Double value)
    {
        bound.setDouble(currentIndex(), value);
    }
    
    private void setInternalValue(Short value)
    {
        bound.setShort(currentIndex(), value);
    }
    
    private void setInternalValue(Boolean value)
    {
        bound.setBool(currentIndex(), value);
    }
    
    private void setInternalValue(Byte value)
    {
        bound.setByte(currentIndex(), value);
    }
    
    private void setInternalValue(BigDecimal value)
    {
        bound.setDecimal(currentIndex(), value);
    }
    
    private void setInternalValue(BigInteger value)
    {
        bound.setVarint(currentIndex(), value);
    }
    
    private void setInternalValue(String value)
    {
        bound.setString(currentIndex(), value);
    }
    
    private void setToNull()
    {
        bound.setToNull(currentIndex());
    }
    
    private void setInternalValue(Enum<?> value) throws SQLException
    {
        // FIXME design converter to allow save ordinal value or other value from enum
        bound.setString(currentIndex(), value.name());
    }

    private void setInternalValue(List<?> value) throws SQLException
    {
        bound.setList(currentIndex(), value);
    }

    private void setInternalValue(Map<?,?> value) throws SQLException
    {
        bound.setMap(currentIndex(), value);
    }

    private void setInternalValue(Set<?> value) throws SQLException
    {
        bound.setSet(currentIndex(), value);
    }

//    private void setInternalValue(Object[] paramsIN) throws SQLException
//    {
//        int j = 0;
//        for (; j < paramsIN.length; j++)
//        {
//            bind(paramsIN[j]);
//            indexIN = indexIN + j;
//        }
//    }

    private int currentIndex()
    {
        return ( index++ + (indexIN));
    }

    
    /*******************************************************************************/
    
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    private void setResultRow(JdbcColumn<Row>[] columns)
    {
        if (resultRow != null)
        {
            resultRow.setColumns(columns);
            return;
        }
        
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
        else if (oneToManies.isEmpty())
        {
            resultRow = new FlatObjectResultRow(returnType, columns);
        }
        else
        {
            resultRow = new PojoResultRow(returnType, columns, oneToManies);
        }
    }
    
    private void log(String name, Object value)
    {
        if (SQLLOG.isDebugEnabled())
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", (index+indexIN), name,
                    MASKING.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }
    
    private void log(Object value)
    {
        String name = String.valueOf(index+indexIN);
        if (SQLLOG.isDebugEnabled())
            SQLLOG.debug("Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", (index+indexIN), name,
                    MASKING.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }
    
    /**
     * Summarize the columns from SQL result in binary data or not.
     * @param metadata  object that contains information about the types and properties of the columns in a <code>ResultSet</code> 
     * @return Array of columns with name and index
     */
    @SuppressWarnings("unchecked")
    private JdbcColumn<Row>[] getJdbcColumns(ColumnDefinitions metadata)
    {
        JdbcColumn<Row>[] columns = new JdbcColumn[metadata.size()];
        
        for (int i = 0; i < columns.length; i++)
        {
            //int columnNumber = i + 1;
            
            String columnName = metadata.getName(i);//getColumnName(metadata, columnNumber);
            int columnType = metadata.getType(i).getName().ordinal(); //metadata.getColumnType(columnNumber);
            //boolean binaryData = false;
            //if (columnType == Types.CLOB || columnType == Types.BLOB)
            //    binaryData = true;
            columns[i] = new CassandraColumn(i, columnName, columnType);
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
}
