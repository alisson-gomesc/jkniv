package net.sf.jkniv.whinstone.jdbc.statement;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.reflect.Injectable;
import net.sf.jkniv.reflect.InjectableFactory;
import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.sqlegance.types.Converter;
import net.sf.jkniv.sqlegance.types.Converter.EnumType;
import net.sf.jkniv.sqlegance.types.Convertible;
import net.sf.jkniv.sqlegance.types.EnumNameType;
import net.sf.jkniv.sqlegance.types.EnumOrdinalType;
import net.sf.jkniv.sqlegance.types.NoConverterType;
import net.sf.jkniv.whinstone.JdbcColumn;

/**
 * 
 * @param <T> Type of objects thats must be returned.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
abstract class AbstractResultRow<T> //implements ResultRow
{
    private final static Logger LOG = LoggerFactory.getLogger(AbstractResultRow.class);
    private final Logger        sqlLog;
    private final DataMasking   masking;
    
    public AbstractResultRow(final Logger log, final DataMasking masking)
    {
        this.sqlLog = log;
        this.masking = masking;
    }
    
    protected void setValueOf(JdbcColumn<ResultSet> column, ResultSet rs, ObjectProxy<T> proxy) throws SQLException
    {
        Injectable<T> reflect = InjectableFactory.of(proxy);
        Object jdbcObject = null;
        if (column.isBinary())
            jdbcObject = column.getBytes(rs);
        else
            jdbcObject = column.getValue(rs);
        
        if (sqlLog.isTraceEnabled())
            sqlLog.trace("Mapping index [{}] column [{}] type of [{}] to value [{}]", column.getIndex(),
                    column.getAttributeName(), (jdbcObject != null ? jdbcObject.getClass().getName() : "null"),
                    masking.mask(column.getAttributeName(), jdbcObject));
        
        if (column.isNestedAttribute())
        {
            // FIXME Convertible for nested attributes
            reflect.inject(column.getAttributeName(), jdbcObject);
        }
        else
        {
            String method = column.getMethodName();
            if (proxy.hasMethod(method))
            {
                Convertible<Object, Object> convertible = getConverter(column, proxy);
                reflect.inject(method, convertible.toAttribute(jdbcObject));
            }
            else
                LOG.warn("Method [{}] doesn't exists for [{}] to set value [{}]", method,
                        proxy.getTargetClass().getName(), jdbcObject);
        }
    }
    
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    /**
     * Retrieve a {@link Convertible} instance to customize the
     * value of database to class field.
     * @param column Column of row
     * @param proxy of return type from query
     * @return A convertible instance if found into class proxy or {@link NoConverterType}
     * instance when the field or method is not annotated.
     */
    protected Convertible<Object, Object> getConverter(JdbcColumn column, ObjectProxy proxy)
    {
        // TODO make me a cache
        Convertible convertible = NoConverterType.getInstance();
        proxy.mute(NoSuchFieldException.class);
        Converter converter = null;//proxy.getAnnotationMethod(Converter.class, column.getMethodName());
        if (converter == null)
            converter = (Converter) proxy.getAnnotationField(Converter.class, column.getAttributeName());
        
        if (converter != null)
        {
            ObjectProxy proxyConvertible = null;
            if(converter.converter().isEnum())
            {
                if (converter.isEnum() == EnumType.ORDINAL)
                    convertible = new EnumOrdinalType(converter.converter());
                else
                    convertible = new EnumNameType(converter.converter());
            }
            else
            {
                proxyConvertible = ObjectProxyFactory.of(converter.converter());
                if (converter.pattern() != null)
                    proxyConvertible.setConstructorArgs(converter.pattern());
                convertible = (Convertible) proxyConvertible.newInstance();
            }
        }
        return convertible;
    }
    
}
