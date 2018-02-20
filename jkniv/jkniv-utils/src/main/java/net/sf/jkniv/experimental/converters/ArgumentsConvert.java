package net.sf.jkniv.experimental.converters;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArgumentsConvert
{
    private static final Logger LOG = LoggerFactory.getLogger(ArgumentsConvert.class);
    
    
    public static Object[] makeAssignableTo(Class<?>[] argTypes, Object[] argValues)
    {
        Object[] argsAssignables = new Object[argValues.length];
        
        for (int i = 0; i < argTypes.length; i++)
        {
            Object instance = argValues[i];
            if (instance != null)
            {
                Class<?> t = argTypes[i];
                Class<?> r = argValues[i].getClass();
                if (!t.isAssignableFrom(r))
                {
                    if (t.isEnum())
                    {
                        TypeConvertible converter = new EnumConverter();
                        argsAssignables[i] = converter.convert(t, instance);
                    }
                    else if (isNumberType(t))
                    {
                        TypeConvertible converter = new NumberConverter();
                        argsAssignables[i] = converter.convert(t, instance);
                    }
                    else if (t == Boolean.class || t == boolean.class)
                    {
                        TypeConvertible converter = new BooleanConverter();
                        argsAssignables[i] = converter.convert(t, instance);
                    }
                    // FIXME test me with java.oracle.TIMESTAMP
//                    else if (isDateType(t))
//                    {
//                        TypeConvertible converter = new DateConverter();
//                        argsAssignables[i] = converter.convert(t, instance);
//                    }
//                    else if (isCalendarType(t))
//                    {
//                        TypeConvertible converter = new CalendarConverter();
//                        argsAssignables[i] = converter.convert(t, instance);
//                    }
                    else
                    {
                        LOG.error("Type of [" + t.getCanonicalName() + "] is not assignable to [" + r.getCanonicalName()
                                + "] trying using same way without converter! Write a TypeConvertible between types.");
                        argsAssignables[i] = instance;
                    }
                }
                else
                {
                    argsAssignables[i] = instance;
                }
            }
        }
        return argsAssignables;
    }

    private static boolean isNumberType(Class<?> type)
    {
        String canonicalName = type.getCanonicalName();
        boolean isNumber = false;
        if (Integer.class.getCanonicalName().equals(canonicalName)|| "int".equals(canonicalName))
            isNumber = true;
        else if (Long.class.getCanonicalName().equals(canonicalName)|| "long".equals(canonicalName))
            isNumber = true;
        else if (Double.class.getCanonicalName().equals(canonicalName) || "double".equals(canonicalName))
            isNumber = true;
        else if (Float.class.getCanonicalName().equals(canonicalName)|| "float".equals(canonicalName))
            isNumber = true;
        else if (BigDecimal.class.getCanonicalName().equals(canonicalName))
            isNumber = true;
        else if (Short.class.getCanonicalName().equals(canonicalName)|| "short".equals(canonicalName))
            isNumber = true;
        else if (BigInteger.class.getCanonicalName().equals(canonicalName))
            isNumber = true;
        
        return isNumber;
    }

}
