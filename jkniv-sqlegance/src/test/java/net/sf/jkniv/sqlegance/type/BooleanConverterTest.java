package net.sf.jkniv.sqlegance.type;

public class BooleanConverterTest
{
    
    public static Integer[] toInteger(int[] intArray) {

        Integer[] result = new Integer[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            result[i] = Integer.valueOf(intArray[i]);
        }
        return result;
    }
}
