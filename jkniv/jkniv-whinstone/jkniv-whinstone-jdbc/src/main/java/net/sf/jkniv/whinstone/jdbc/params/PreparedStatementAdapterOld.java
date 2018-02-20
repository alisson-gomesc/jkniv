package net.sf.jkniv.whinstone.jdbc.params;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.experimental.converters.SqlDateConverter;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.logger.LogLevel;
import net.sf.jkniv.sqlegance.logger.SqlLogger;
import net.sf.jkniv.sqlegance.params.StatementAdapterOld;
import net.sf.jkniv.whinstone.jdbc.statement.PreparedStatementAdapter;

/**
 * 
 * @author Alisson Gomes
 * @deprecated use {@link PreparedStatementAdapter}
 *
 */
public class PreparedStatementAdapterOld implements StatementAdapterOld
{
    private final PreparedStatement stmt;
    private int                     index, indexIN;
    private final SqlLogger         sqlLogger;
    private final HandlerException  handerException;
    private final SqlDateConverter  dtConverter;
    
    public PreparedStatementAdapterOld(PreparedStatement stmt, SqlLogger sqlLogger)
    {
        this.stmt = stmt;
        this.sqlLogger = sqlLogger;
        this.index = 0;
        this.indexIN = 0;
        this.handerException = new HandlerException(RepositoryException.class, "Cannot set parameter [%s] value [%s]");
        this.dtConverter = new SqlDateConverter();
        this.reset();
    }
    
    @Override
    public StatementAdapterOld setParameter(String name, Object value)
    {
        this.index++;
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
    }
    
    @Override
    public StatementAdapterOld setParameter(int position, Object value)
    {
        this.index = position;
        log(position, value);
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
                setValue(value);
            }
        }
        catch (SQLException e)
        {
            this.handerException.handle(e);// FIXME handler default message with custom params
        }
        return this;
    }
    
    @Override
    public StatementAdapterOld setParameters(Object... values)
    {
        for(;index<values.length;)
        {
            Object v = values[index];
            setParameter(index, v);
        }
        return this;
    }

    
    @Override
    public int reset()
    {
        int before = index;
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
    
    private void setValue(Object value) throws SQLException
    {
        stmt.setObject(index + indexIN, value);
    }
    
    private void setValue(Object[] paramsIN) throws SQLException
    {
        int j = 0;
        for (; j < paramsIN.length; j++)
            stmt.setObject(index + indexIN + j, paramsIN[j]);
        indexIN = indexIN + j;
    }
    
    private void setValue(Date value) throws SQLException
    {
        java.sql.Timestamp timestamp = dtConverter.convert(java.sql.Timestamp.class, value);
        stmt.setObject(index + indexIN, timestamp);
    }
    
    private void setValue(Calendar value) throws SQLException
    {
        java.sql.Timestamp timestamp = dtConverter.convert(java.sql.Timestamp.class, value.getTime());
        stmt.setObject(index + indexIN, timestamp);
    }
    
    private void setValue(Enum<?> value) throws SQLException
    {
        //ObjectProxy<?> proxy = ObjectProxyFactory.newProxy(value);
        //stmt.setObject(index+indexIN, proxy.invoke("name"));
        stmt.setObject(index + indexIN, value.name());// FIXME design converter to allow save ordinal value or other value from enum
    }
    
    private void log(String name, Object value)
    {
        if (sqlLogger.isEnabled(LogLevel.STMT))
            sqlLogger.log(LogLevel.STMT,
                    "Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index, name,
                    sqlLogger.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }
    
    private void log(int position, Object value)
    {
        String name = String.valueOf(position);
        if (sqlLogger.isEnabled(LogLevel.STMT))
            sqlLogger.log(LogLevel.STMT,
                    "Setting SQL Parameter from index [{}] with name [{}] with value of [{}] type of [{}]", index, name,
                    sqlLogger.mask(name, value), (value == null ? "NULL" : value.getClass()));
    }
    
}
