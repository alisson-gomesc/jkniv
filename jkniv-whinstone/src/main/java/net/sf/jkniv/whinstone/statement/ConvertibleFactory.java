package net.sf.jkniv.whinstone.statement;

import net.sf.jkniv.reflect.beans.ObjectProxy;
import net.sf.jkniv.reflect.beans.ObjectProxyFactory;
import net.sf.jkniv.reflect.beans.PropertyAccess;
import net.sf.jkniv.sqlegance.types.Converter;
import net.sf.jkniv.sqlegance.types.Converter.EnumType;
import net.sf.jkniv.sqlegance.types.Convertible;
import net.sf.jkniv.sqlegance.types.EnumNameType;
import net.sf.jkniv.sqlegance.types.EnumOrdinalType;
import net.sf.jkniv.sqlegance.types.NoConverterType;

public class ConvertibleFactory
{
    /**
     * Retrieve a {@link Convertible} instance to customize the
     * value of database to class field.
     * @param column Column of row
     * @param proxy of return type from query
     * @return A convertible instance if found into class proxy or {@link NoConverterType}
     * instance when the field or method is not annotated.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Convertible<Object, Object> getConverter(PropertyAccess access, ObjectProxy proxy)
    {
        // TODO make me a cache
        Convertible convertible = NoConverterType.getInstance();
        proxy.mute(NoSuchFieldException.class);
        Converter converter = null;//proxy.getAnnotationMethod(Converter.class, column.getMethodName());
        if (converter == null)
            converter = (Converter) proxy.getAnnotationField(Converter.class, access.getFieldName());
        
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
