package net.sf.jkniv.whinstone.couchdb.params;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;

import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.experimental.converters.SqlDateConverter;
import net.sf.jkniv.sqlegance.KeyGeneratorType;
import net.sf.jkniv.sqlegance.OneToMany;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.ResultRow;
import net.sf.jkniv.sqlegance.ResultSetParser;
import net.sf.jkniv.sqlegance.logger.LogLevel;
import net.sf.jkniv.sqlegance.logger.SqlLogger;
import net.sf.jkniv.sqlegance.statement.StatementAdapter;

/**
 *  https://docs.datastax.com/en/developer/java-driver/3.1/manual/statements/prepared/
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
 * @author Alisson Gomes
 *
 */
public class PreparedStatementAdapter<T, R> implements StatementAdapter<T, ResultSet>
{
    private final PreparedStatement stmt;
    private final BoundStatement    bound;
    private int                     index, indexIN;
    private final SqlLogger         sqlLogger;
    private final HandlerException  handlerException;
    private final SqlDateConverter  dtConverter;
    
    public PreparedStatementAdapter(PreparedStatement stmt, SqlLogger sqlLogger)
    {
        this.stmt = stmt;
        this.bound = stmt.bind();
        this.sqlLogger = sqlLogger;
        this.index = 0;
        this.indexIN = 0;
        this.handlerException = new HandlerException(RepositoryException.class, "Cannot set parameter [%s] value [%s]");
        this.dtConverter = new SqlDateConverter();
        this.reset();
    }
    
    @Override
    public StatementAdapter<T, ResultSet> bind(String name, Object value)
    {
        //this.index++;
        log(name, value);
        if (name.toLowerCase().startsWith("in:"))
        {
            try
            {
                setValue((Object[]) value);
                return this;
            }
            catch (SQLException e)
            {
                this.handlerException.handle(e);// FIXME handler default message with custom params
            }
        }
        return bindInternal(value);
        /*
        try
        {
            if (value == null)
                setToNull();
            else if (name.toLowerCase().startsWith("in:"))
            {
                setValue((Object[]) value);
            }
            else if (value instanceof java.util.Date)
            {
                setValue((java.util.Date) value);
            }
            else if (Enum.class.isInstance(value))
            {
                setValue((Enum) value);
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
            this.handerException.handle(e);// FIXME handler default message with custom params
        }
        return this;
        */
    }
    
    @Override
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
                setValue((Enum) value);
            }
            else if (value instanceof java.util.Calendar)
            {
                setValue((Calendar) value);
            }
            else
            {
                bindInternal(value);
                //setValue(value);
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
        bound.bind(values);
        return this;
    }

    
    private StatementAdapter<T,ResultSet> bindInternal(Object value)
    {
        try
        {
            if (value == null)
                setToNull();
            else if (value instanceof String)
                setValue((String)value);
            else if (value instanceof Integer)
                setValue((Integer)value);
            else if (value instanceof Long)
                setValue((Long)value);
            else if (value instanceof Double)
                setValue((Double)value);
            else if (value instanceof Float)
                setValue((Float)value);
            else if (value instanceof Boolean)
                setValue((Boolean)value);
            else if (value instanceof BigDecimal)
                setValue((BigDecimal)value);
            else if (value instanceof BigInteger)
                setValue((BigInteger)value);
            else if (value instanceof Short)
                setValue((Short)value);
            else if (value instanceof Date)
                setValue((Date)value);
            else if (value instanceof java.util.Calendar)
                setValue((Calendar)value);
            else if (value instanceof com.datastax.driver.core.LocalDate)
                setValue((com.datastax.driver.core.LocalDate)value);
            else if (Enum.class.isInstance(value))
                setValue((Enum<?>)value);
            else if (value instanceof Byte)
                setValue((Byte)value);
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

    private void setValue(com.datastax.driver.core.LocalDate value)
    {
        bound.setDate(currentIndex(), value);
    }

    private void setValue(Calendar value)
    {
        bound.setTimestamp(currentIndex(), value.getTime());
    }

    private void setValue(Date value)
    {
        bound.setTimestamp(currentIndex(), value);
    }

    private void setValue(Integer value)
    {
        bound.setInt(currentIndex(), value);
    }

    private void setValue(Long value)
    {
        bound.setLong(currentIndex(), value);
    }

    private void setValue(Float value)
    {
        bound.setFloat(currentIndex(), value);
    }
    
    private void setValue(Double value)
    {
        bound.setDouble(currentIndex(), value);
    }

    private void setValue(Short value)
    {
        bound.setShort(currentIndex(), value);
    }
    
    private void setValue(Boolean value)
    {
        bound.setBool(currentIndex(), value);
    }
        
    private void setValue(Byte value)
    {
        bound.setByte(currentIndex(), value);
    }
    
    private void setValue(BigDecimal value)
    {
        bound.setDecimal(currentIndex(), value);
    }

    private void setValue(BigInteger value)
    {
        bound.setVarint(currentIndex(), value);
    }
    
    private void setValue(String value)
    {
        bound.setString(currentIndex(), value);
    }

    private void setToNull()
    {
        bound.setToNull(currentIndex());
    }
    
    private void setValue(Enum<?> value) throws SQLException
    {
     // FIXME design converter to allow save ordinal value or other value from enum
        bound.setString(currentIndex(), value.name());
    }

    private void setValue(Object[] paramsIN) throws SQLException
    {
        int j = 0;
        for (; j < paramsIN.length; j++)
        {
            bind(paramsIN[j]);
            indexIN = indexIN + j;
        }
        //    stmt.setObject(index + indexIN + j, paramsIN[j]);
        //indexIN = indexIN + j;
    }

    @Override
    public int reset()
    {
        int before = (index+indexIN);
        index = 0;
        indexIN = 0;
        return before;
    }
    
    private int currentIndex()
    {
        return ( index++ +indexIN);
    }
    
    @Override
    public void batch()
    {
        // TODO implements batch https://docs.datastax.com/en/drivers/java/3.0/com/datastax/driver/core/BatchStatement.html
        // TODO implements batch https://docs.datastax.com/en/drivers/python/3.2/api/cassandra/query.html
        // TODO implements batch https://docs.datastax.com/en/cql/3.3/cql/cql_using/useBatch.html
        // TODO implements batch https://docs.datastax.com/en/cql/3.3/cql/cql_using/useBatchGoodExample.html

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

    /*
    private void setValue(Object value) throws SQLException
    {
        bound.setObject(index + indexIN, value);
    }
    */
    
    /*
    private void setValue(Date value) throws SQLException
    {
        //java.sql.Timestamp timestamp = dtConverter.convert(java.sql.Timestamp.class, value);
        bound.setTimestamp(currentIndex(), value);
        //stmt.setObject(currentIndex(), timestamp);
    }
    */
    /*
    private void setValue(Calendar value) throws SQLException
    {
        java.sql.Timestamp timestamp = dtConverter.convert(java.sql.Timestamp.class, value.getTime());
        stmt.setObject(currentIndex(), timestamp);
    }
    */
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

    @Override
    public StatementAdapter<T, ResultSet> returnType(Class<T> returnType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementAdapter<T, ResultSet> resultRow(ResultRow<T, ResultSet> resultRow)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementAdapter<T, ResultSet> scalar()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementAdapter<T, ResultSet> oneToManies(Set<OneToMany> oneToManies)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementAdapter<T, ResultSet> groupingBy(List<String> groupingBy)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public StatementAdapter<T, ResultSet> keyGeneratorType(KeyGeneratorType keyGeneratorType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public KeyGeneratorType getKeyGeneratorType()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultSetParser<T, ResultSet> generatedKeys()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<T> rows()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int execute()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
}
