/* 
 * JKNIV, utils - Helper utilities for jdk code.
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
package net.sf.jkniv.experimental.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import net.sf.jkniv.asserts.Assertable;
import net.sf.jkniv.asserts.AssertsFactory;

/*
 * Even and odd numbers
 * Prime numbers
 */
public class Arithmetic<T extends Number>
{
    private T value;
    private static final MathContext defaultContext = new MathContext(3);
    private boolean useDefaultContext;
    private MathContext context;
    private final static Assertable notNull = AssertsFactory.getNotNull();
    
    public Arithmetic(T value)
    {
        notNull.verify(value);
        this.value = value;
        this.useDefaultContext = true;
    }

    public Arithmetic(T value, MathContext context)
    {
        notNull.verify(value, context);
        this.value = value;
        this.context = context;
        this.useDefaultContext = false;
    }
    
    public Arithmetic<T> plus(T val)
    {
        Arithmetic<T> v = null;
        return v;
    }

    public Arithmetic<T> plus(short val)
    {
        Arithmetic<T> v = null;
        return v;
    }

    public Arithmetic<T> plus(int val)
    {
        Arithmetic<T> v = null;
        return v;
    }
    
    public Arithmetic<T> plus(long val)
    {
        Arithmetic<T> v = null;
        return v;
    }
    
    public Arithmetic<T> plus(float val)
    {
        Arithmetic<T> v = null;
        return v;
    }
    
    public Arithmetic<T> plus(double val)
    {
        Arithmetic<T> v = null;
        return v;
    }

    public Arithmetic<T> subtract(T val)
    {
        Arithmetic<T> v = null;
        return v;
    }

    public Arithmetic<T> subtract(short val)
    {
        Arithmetic<T> v = null;
        return v;
    }

    public Arithmetic<T> subtract(int val)
    {
        Arithmetic<T> v = null;
        return v;
    }
    
    public Arithmetic<T> subtract(long val)
    {
        Arithmetic<T> v = null;
        return v;
    }
    
    public Arithmetic<T> subtract(float val)
    {
        Arithmetic<T> v = null;
        return v;
    }
    
    public Arithmetic<T> subtract(double val)
    {
        Arithmetic<T> v = null;
        return v;
    }
    
    public Arithmetic<T> multiply(T val)
    {
        Arithmetic<T> v = null;
        return v;
    }
    
    public Arithmetic<T> multiply(short val)
    {
        Arithmetic<T> v = null;
        return v;
    }

    public Arithmetic<T> multiply(int val)
    {
        Arithmetic<T> v = null;
        return v;
    }
    
    public Arithmetic<T> multiply(long val)
    {
        Arithmetic<T> v = null;
        return v;
    }
    
    public Arithmetic<T> multiply(float val)
    {
        Arithmetic<T> v = null;
        return v;
    }
    
    public Arithmetic<T> multiply(double val)
    {
        Arithmetic<T> v = null;
        return v;
    }

    public Arithmetic<T> divide(T val)
    {
        Arithmetic<T> v = null;
        return v;
    }

    public Arithmetic<T> divide(short val)
    {
        Arithmetic<T> v = null;
        return v;
    }

    public Arithmetic<T> divide(int val)
    {
        Arithmetic<T> v = null;
        return v;
    }
    
    public Arithmetic<T> divide(long val)
    {
        Arithmetic<T> v = null;
        return v;
    }
    
    public Arithmetic<T> divide(float val)
    {
        Arithmetic<T> v = null;
        return v;
    }
    
    public Arithmetic<T> divide(double val)
    {
        Arithmetic<T> v = null;
        return v;
    }
    
    public Arithmetic<T> max(T val)
    {
        Arithmetic<T> max = null;
        if (isBigDecimal())
        {
            BigDecimal _this = (BigDecimal) value;
            BigDecimal _val = (BigDecimal) val;
            max = newArithmetic((T) _this.max(_val));
        }
        else if (isBigInteger())
        {
            BigInteger _this = (BigInteger) value;
            BigInteger _val = (BigInteger) val;
            max = newArithmetic((T) _this.max(_val));
        }
        else
        {
            Number _this = value.doubleValue();
            Number _val = val.doubleValue();
            max = (_this.doubleValue() > _val.doubleValue() ? this : newArithmetic(val));
        }
        return max;
    }
    
    public Arithmetic<T> min(T val)
    {
        Arithmetic<T> v = null;
        return v;
    }

    public Arithmetic<T> negate()
    {
        Arithmetic<T> v = null;
        return v;
    }

    public Arithmetic<T> positive()
    {
        Arithmetic<T> v = null;
        return v;
    }

    public Arithmetic<T> abs()
    {
        Arithmetic<T> v = null;
        return v;
    }
    
    public T value()
    {
        return this.value;
    }
    
    public BigDecimal bigDecimalValue()
    {
        BigDecimal v =  null;
        if (isBigDecimal())
            v = (BigDecimal)value;
        else if (isBigInteger())
            v = new BigDecimal((BigInteger)value);
        else
            v = new BigDecimal(this.toString());
        return v;
    }
    
    public BigInteger bigIntegerValue()
    {
        BigInteger v =  null;
        if (isBigDecimal())
            v = new BigInteger(value.toString());
        else if (isBigInteger())
            v = (BigInteger)value;
        else
            v = new BigInteger(this.toString());
        return v;
    }
    
    /**
     * Returns the value of the specified number as an {@code int},
     * which may involve rounding or truncation.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type {@code int}.
     */
    public int intValue()
    {
        return value.intValue();
    }

    /**
     * Returns the value of the specified number as a {@code long},
     * which may involve rounding or truncation.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type {@code long}.
     */
    public long longValue()
    {
        return value.longValue();
    }

    /**
     * Returns the value of the specified number as a {@code float},
     * which may involve rounding.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type {@code float}.
     */
    public float floatValue()
    {
        return value.floatValue();

    }

    /**
     * Returns the value of the specified number as a {@code double},
     * which may involve rounding.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type {@code double}.
     */
    public double doubleValue()
    {
        return value.doubleValue();
    }

    /**
     * Returns the value of the specified number as a {@code byte},
     * which may involve rounding or truncation.
     *
     * <p>This implementation returns the result of {@link #intValue} cast
     * to a {@code byte}.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type {@code byte}.
     */
    public byte byteValue() {
        return (byte)intValue();
    }

    /**
     * Returns the value of the specified number as a {@code short},
     * which may involve rounding or truncation.
     *
     * <p>This implementation returns the result of {@link #intValue} cast
     * to a {@code short}.
     *
     * @return  the numeric value represented by this object after conversion
     *          to type {@code short}.
     */
    public short shortValue() {
        return (short)intValue();
    }

    private Arithmetic<T> newArithmetic(T val)
    {
        MathContext c = useDefaultContext ? defaultContext : context;
        return new Arithmetic<T>(val, c);
    }
    
    public boolean isBigDecimal()
    {
        return (value instanceof BigDecimal);
    }
    
    public boolean isBigInteger()
    {
        return (value instanceof BigInteger);
    }

    public boolean isShort()
    {
        return (value instanceof Short || short.class.isInstance(value));
    }

    public boolean isInt()
    {
        return (value instanceof Integer || int.class.isInstance(value));
    }
    
    public boolean isLong()
    {
        return (value instanceof Long || long.class.isInstance(value));
    }
    
    public boolean isFloat()
    {
        return (value instanceof Short || float.class.isInstance(value));
    }
    
    public boolean isDouble()
    {
        return (value instanceof Double || double.class.isInstance(value));
    }
    
    public boolean isEven()
    {
        // TODO
        throw new UnsupportedOperationException("TODO implements");
    }

    public boolean isOdd()
    {
        // TODO
        throw new UnsupportedOperationException("TODO implements");
    }

    private MathContext getContext()
    {
        return (useDefaultContext ? defaultContext : context);
    }


}
