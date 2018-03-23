package net.sf.jkniv.whinstone.couchdb.statement;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.experimental.converters.SqlDateConverter;
import net.sf.jkniv.sqlegance.JdbcColumn;
import net.sf.jkniv.sqlegance.KeyGeneratorType;
import net.sf.jkniv.sqlegance.OneToMany;
import net.sf.jkniv.sqlegance.Queryable;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.ResultRow;
import net.sf.jkniv.sqlegance.ResultSetParser;
import net.sf.jkniv.sqlegance.classification.Groupable;
import net.sf.jkniv.sqlegance.classification.GroupingBy;
import net.sf.jkniv.sqlegance.classification.NoGroupingBy;
import net.sf.jkniv.sqlegance.classification.Transformable;
import net.sf.jkniv.sqlegance.logger.LogLevel;
import net.sf.jkniv.sqlegance.logger.SimpleDataMasking;
import net.sf.jkniv.sqlegance.logger.SqlLogger;
import net.sf.jkniv.sqlegance.statement.StatementAdapter;
import net.sf.jkniv.whinstone.couchdb.CouchDbColumn;
import net.sf.jkniv.whinstone.couchdb.result.FlatObjectResultRow;
import net.sf.jkniv.whinstone.couchdb.result.MapResultRow;
import net.sf.jkniv.whinstone.couchdb.result.NumberResultRow;
import net.sf.jkniv.whinstone.couchdb.result.ObjectResultSetParser;
import net.sf.jkniv.whinstone.couchdb.result.PojoResultRow;
import net.sf.jkniv.whinstone.couchdb.result.ScalarResultRow;
import net.sf.jkniv.whinstone.couchdb.result.StringResultRow;

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
 *
 */
public class PreparedStatementAdapter<T, R> implements StatementAdapter<T, Row>
{
    private final HandlerException  handlerException;
    private final PreparedStatement stmt;
    private BoundStatement          bound;
    //private final Queryable         queryable;
    private final SqlDateConverter  dtConverter;
    
    private int                     index, indexIN;
    private SqlLogger               sqlLogger;
    private Class<T>                returnType;
    private ResultRow<T, Row>       resultRow;
    private boolean                 scalar;
    private Set<OneToMany>          oneToManies;
    private List<String>            groupingBy;
    private KeyGeneratorType        keyGeneratorType;
    private Session                 session;
    
    public PreparedStatementAdapter(Session session, PreparedStatement stmt)
    {
        //this.queryable = queryable;
        this.stmt = stmt;
        this.session = session;
        this.bound = this.stmt.bind();
        this.dtConverter = new SqlDateConverter();
        this.oneToManies = Collections.emptySet();
        this.groupingBy = Collections.emptyList();
        this.handlerException = new HandlerException(RepositoryException.class, "Cannot set parameter [%s] value [%s]");
        this.reset();
        this.sqlLogger = new SqlLogger(LogLevel.ALL, new SimpleDataMasking());// FIXME design retrieve SqlLogger another way 
    }
    
    /*
    public PreparedStatementAdapter(PreparedStatement stmt)
    {
        //this.queryable = null;
        this.stmt = stmt;
        this.sqlLogger = new SqlLogger(LogLevel.ALL, new SimpleDataMasking());// FIXME design retrieve SqlLogger another way 
        this.index = 0;
        this.indexIN = 0;
        this.dtConverter = new SqlDateConverter();
        this.oneToManies = Collections.emptySet();
        this.groupingBy = Collections.emptyList();
        this.scalar = false;
        this.reset();
        this.handlerException = new HandlerException(RepositoryException.class, "Cannot set parameter [%s] value [%s]");
    }
    */
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
        //this.index++;
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
                setInternalValue((Enum) value);
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
        this.bound = stmt.bind(values);
        this.index = values.length-1;
        //        for (; index < values.length;)
        //        {
        //            Object v = values[index];
        //            bind(index, v);
        //        }
        return this;
    }
    
    @Override
    public void batch()
    {
        // TODO implements batch https://docs.datastax.com/en/drivers/java/3.0/com/datastax/driver/core/BatchStatement.html
        // TODO implements batch https://docs.datastax.com/en/drivers/python/3.2/api/cassandra/query.html
        // TODO implements batch https://docs.datastax.com/en/cql/3.3/cql/cql_using/useBatch.html
        // TODO implements batch https://docs.datastax.com/en/cql/3.3/cql/cql_using/useBatchGoodExample.html

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
            rs = session.execute(bound);
            JdbcColumn<Row>[] columns = getJdbcColumns(rs.getColumnDefinitions());
            setResultRow(columns);
            
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
    
    public ResultSetParser<T, Row> generatedKeys()
    {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
    
    public int execute()
    {
        //        try
        //        {
        session.execute(bound);
        
        
        return 0;
        //return stmt.executeUpdate();
        //        }
        //        catch (SQLException e)
        //        {
        //            handlerException.handle(e, e.getMessage());
        //        }
        //        return 0;
    }
    
    @Override
    public int reset()
    {
        int before = (index+indexIN);
        index = 0;
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
                    stmt.setObject(currentIndex(), paramValue);
                }
                else if (paramValue.getClass().isEnum())
                {
                    ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(paramValue);
                    stmt.setObject(currentIndex(), proxy.invoke("name"));
                }
                else if (paramValue instanceof Date)
                {
                    SqlDateConverter converter = new SqlDateConverter();
                    java.sql.Timestamp timestamp = converter.convert(java.sql.Timestamp.class, paramValue);
                    stmt.setObject(currentIndex(), timestamp);
                }
                else
                {
                    stmt.setObject(currentIndex(), paramValue);
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
    
    private void setValueIN(Object[] paramsIN) throws SQLException
    {
        int j = 0;
        for (; j < paramsIN.length; j++)
            bindInternal(paramsIN[j]);
        indexIN = indexIN + j;
    }
    
    /*******************************************************************************/
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
            else
            {
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
    
    private void setInternalValue(Object[] paramsIN) throws SQLException
    {
        int j = 0;
        for (; j < paramsIN.length; j++)
        {
            bind(paramsIN[j]);
            indexIN = indexIN + j;
        }
        //    stmt.setObject(currentIndex() + j, paramsIN[j]);
        indexIN = indexIN + j;
    }

    private int currentIndex()
    {
        return ( index++ +indexIN);
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
            resultRow = new ScalarResultRow(columns, sqlLogger);
        }
        else if (Map.class.isAssignableFrom(returnType))
        {
            resultRow = new MapResultRow(returnType, columns, sqlLogger);
        }
        else if (Number.class.isAssignableFrom(returnType)) // FIXME implements for date, calendar, boolean improve design
        {
            resultRow = new NumberResultRow(returnType, columns, sqlLogger);
        }
        else if (String.class.isAssignableFrom(returnType))
        {
            resultRow = new StringResultRow(columns, sqlLogger);
        }
        else if (oneToManies.isEmpty())
        {
            resultRow = new FlatObjectResultRow(returnType, columns, sqlLogger);
        }
        else
        {
            resultRow = new PojoResultRow(returnType, columns, oneToManies, sqlLogger);
        }
    }
    
    private void log(String name, Object value)
    {
        if (sqlLogger.isEnabled(LogLevel.STMT))
            sqlLogger.log(LogLevel.STMT,
                    "Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index, name,
                    sqlLogger.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }
    
    private void log(Object value)
    {
        String name = String.valueOf(index+indexIN);
        if (sqlLogger.isEnabled(LogLevel.STMT))
            sqlLogger.log(LogLevel.STMT,
                    "Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index, name,
                    sqlLogger.mask(name, value), (value == null ? "NULL" : value.getClass()));
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
            columns[i] = new CouchDbColumn(i, columnName, columnType);
        }
        return columns;
    }
    
}
