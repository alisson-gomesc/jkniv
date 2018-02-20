package net.sf.jkniv.sqlegance.type;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder;

import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.sqlegance.QueryNotFoundException;

public class DateNumberConverterTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();

    @Test
    public void whenNumberValueIsConvertedToBoolean() throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Convertible<Date, Number> convertible = new DateNumberConverter();
        Number NULL = null;
        Number D1 = Integer.valueOf("20170825");
        Integer[] TYPES = toInteger(convertible.getTypes()); 
        
        assertThat(convertible.getClassType().getCanonicalName(), is(Date.class.getCanonicalName()));
        assertThat(TYPES, arrayContainingInAnyOrder(Types.INTEGER, Types.BIGINT));
        assertThat(convertible.getTypes().length, is(2));
        assertThat(convertible.toAttribute(NULL), is(nullValue()));
        assertThat(convertible.toAttribute(D1), is(sdf.parse(D1.toString())));
        assertThat(convertible.toJdbc(null), is(nullValue()));
        //assertThat(convertible.toJdbc(sdf.parse(ZERO.toString())), is(ZERO));
        assertThat(convertible.toJdbc(sdf.parse(D1.toString())), is(D1));
    }
    
    @Test
    public void whenIntegerValueCannotBeConvertedToNumber() throws ParseException
    {
        catcher.expect(ConvertException.class);
        catcher.expectMessage("Cannot convert number [0] to date the format must be [yyyyMMdd]");

        Convertible<Date, Number> convertible = new DateNumberConverter();
        Number ZERO = Integer.valueOf("00000000");
        assertThat(convertible.toAttribute(ZERO), is(new Date()));
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
