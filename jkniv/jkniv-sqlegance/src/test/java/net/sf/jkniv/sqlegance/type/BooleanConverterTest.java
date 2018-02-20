package net.sf.jkniv.sqlegance.type;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder;

import java.sql.Types;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class BooleanConverterTest
{

    @Test
    public void whenBoleanValueIsConvertedToNumber()
    {
        Convertible<Boolean, Number> convertible = new BooleanConverter();
        Number NULL = null;
        Number ZERO = Integer.valueOf("0");
        Number ONE = Integer.valueOf("1");
        Number TWO = Integer.valueOf("2");
        Integer[] TYPES = toInteger(convertible.getTypes()); 
        
        assertThat(convertible.getClassType().getCanonicalName(), is(Boolean.class.getCanonicalName()));
        assertThat(TYPES, arrayContainingInAnyOrder(Types.BIT, Types.SMALLINT, Types.INTEGER, Types.BIGINT));
        assertThat(convertible.getTypes().length, is(4));
        assertThat(convertible.toAttribute(NULL), is(Boolean.FALSE));
        assertThat(convertible.toAttribute(ZERO), is(Boolean.FALSE));
        assertThat(convertible.toAttribute(ONE), is(Boolean.TRUE));
        assertThat(convertible.toAttribute(TWO), is(Boolean.FALSE));
        assertThat(convertible.toJdbc(Boolean.FALSE), is(ZERO));
        assertThat(convertible.toJdbc(Boolean.TRUE), is(ONE));
    }
    
    @Test
    public void whenBoleanValueIsConvertedToString()
    {
        Convertible<Boolean, String> convertible = new BooleanStringConverter();
        String NULL = null;
        String TRUE = "TRUE";
        String FALSE = "FALSE";
        Integer[] TYPES = toInteger(convertible.getTypes()); 
        
        assertThat(convertible.getClassType().getCanonicalName(), is(Boolean.class.getCanonicalName()));
        assertThat(TYPES, arrayContainingInAnyOrder(Types.CHAR, Types.VARCHAR, Types.NCHAR, Types.NVARCHAR));
        assertThat(convertible.getTypes().length, is(4));
        assertThat(convertible.toAttribute(NULL), is(Boolean.FALSE));
        assertThat(convertible.toAttribute(FALSE), is(Boolean.FALSE));
        assertThat(convertible.toAttribute(TRUE), is(Boolean.TRUE));
        assertThat(convertible.toJdbc(Boolean.FALSE), is(FALSE));
        assertThat(convertible.toJdbc(Boolean.TRUE), is(TRUE));
        assertThat(convertible.toJdbc(null), is(FALSE));
    }

    @Test
    public void whenBoleanValueIsConvertedToTrueOrFalse()
    {
        Convertible<Boolean, String> convertible = new BooleanTrueFalseConverter();
        String NULL = null;
        String FALSE = "F";
        String TRUE = "T";
        String TRUE2 = "TRUE";
        Integer[] TYPES = toInteger(convertible.getTypes()); 
        
        assertThat(convertible.getClassType().getCanonicalName(), is(Boolean.class.getCanonicalName()));
        assertThat(TYPES, arrayContainingInAnyOrder(Types.CHAR, Types.VARCHAR, Types.NCHAR, Types.NVARCHAR));
        assertThat(convertible.getTypes().length, is(4));
        assertThat(convertible.toAttribute(NULL), is(Boolean.FALSE));
        assertThat(convertible.toAttribute(FALSE), is(Boolean.FALSE));
        assertThat(convertible.toAttribute(TRUE), is(Boolean.TRUE));
        assertThat(convertible.toAttribute(TRUE2), is(Boolean.FALSE));
        assertThat(convertible.toJdbc(Boolean.FALSE), is(FALSE));
        assertThat(convertible.toJdbc(Boolean.TRUE), is(TRUE));
        assertThat(convertible.toJdbc(null), is(FALSE));
    }

    @Test
    public void whenBoleanValueIsConvertedToYesOrNo()
    {
        Convertible<Boolean, String> convertible = new BooleanYesNoConverter();
        String NULL = null;
        String YES = "Y", yes = "y";
        String NO = "N", no = "n", OTHER = "X";
        
        Integer[] TYPES = toInteger(convertible.getTypes()); 
        
        assertThat(convertible.getClassType().getCanonicalName(), is(Boolean.class.getCanonicalName()));
        assertThat(TYPES, arrayContainingInAnyOrder(Types.CHAR, Types.VARCHAR, Types.NCHAR, Types.NVARCHAR));
        assertThat(convertible.getTypes().length, is(4));
        assertThat(convertible.toAttribute(NULL), is(Boolean.FALSE));
        assertThat(convertible.toAttribute(YES), is(Boolean.TRUE));
        assertThat(convertible.toAttribute(yes), is(Boolean.TRUE));
        assertThat(convertible.toAttribute(NO), is(Boolean.FALSE));
        assertThat(convertible.toAttribute(no), is(Boolean.FALSE));
        assertThat(convertible.toAttribute(OTHER), is(Boolean.FALSE));
        assertThat(convertible.toJdbc(Boolean.FALSE), is(NO));
        assertThat(convertible.toJdbc(Boolean.TRUE), is(YES));
        assertThat(convertible.toJdbc(null), is(NO));
    }
    
    public static Integer[] toInteger(int[] intArray) {

        Integer[] result = new Integer[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            result[i] = Integer.valueOf(intArray[i]);
        }
        return result;
    }
}
