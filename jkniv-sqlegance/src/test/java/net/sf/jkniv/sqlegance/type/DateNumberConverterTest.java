package net.sf.jkniv.sqlegance.type;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class DateNumberConverterTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();

    
    public static Integer[] toInteger(int[] intArray) {

        Integer[] result = new Integer[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            result[i] = Integer.valueOf(intArray[i]);
        }
        return result;
    }
}
