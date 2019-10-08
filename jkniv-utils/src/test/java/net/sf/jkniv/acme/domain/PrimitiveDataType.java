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
package net.sf.jkniv.acme.domain;

import java.util.Arrays;

public class PrimitiveDataType
{
    byte      myByte;
    short     myShort;
    int       myInt;
    long      myLong;
    float     myFloat;
    double    myDouble;
    boolean   myBoolean;
    char      myChar;
    
    byte[]    myBytes;
    short[]   myShorts;
    int[]     myInts;
    long[]    myLongs;
    float[]   myFloats;
    double[]  myDoubles;
    boolean[] myBooleans;
    char[]    myChars;
    
    public PrimitiveDataType()
    {
    }
    
    public PrimitiveDataType(byte myByte, short myShort, int myInt, long myLong, float myFloat, double myDouble,
            boolean myBoolean, char myChar)
    {
        super();
        this.myByte = myByte;
        this.myShort = myShort;
        this.myInt = myInt;
        this.myLong = myLong;
        this.myFloat = myFloat;
        this.myDouble = myDouble;
        this.myBoolean = myBoolean;
        this.myChar = myChar;
    }
    
    public PrimitiveDataType(byte[] myBytes, short[] myShorts, int[] myInts, long[] myLongs, float[] myFloats,
            double[] myDoubles, boolean[] myBooleans, char[] myChars)
    {
        super();
        this.myBytes = myBytes;
        this.myShorts = myShorts;
        this.myInts = myInts;
        this.myLongs = myLongs;
        this.myFloats = myFloats;
        this.myDoubles = myDoubles;
        this.myBooleans = myBooleans;
        this.myChars = myChars;
    }

    public byte getMyByte()
    {
        return myByte;
    }

    public void setMyByte(byte myByte)
    {
        this.myByte = myByte;
    }

    public short getMyShort()
    {
        return myShort;
    }

    public void setMyShort(short myShort)
    {
        this.myShort = myShort;
    }

    public int getMyInt()
    {
        return myInt;
    }

    public void setMyInt(int myInt)
    {
        this.myInt = myInt;
    }

    public long getMyLong()
    {
        return myLong;
    }

    public void setMyLong(long myLong)
    {
        this.myLong = myLong;
    }

    public float getMyFloat()
    {
        return myFloat;
    }

    public void setMyFloat(float myFloat)
    {
        this.myFloat = myFloat;
    }

    public double getMyDouble()
    {
        return myDouble;
    }

    public void setMyDouble(double myDouble)
    {
        this.myDouble = myDouble;
    }

    public boolean isMyBoolean()
    {
        return myBoolean;
    }

    public void setMyBoolean(boolean myBoolean)
    {
        this.myBoolean = myBoolean;
    }

    public char getMyChar()
    {
        return myChar;
    }

    public void setMyChar(char myChar)
    {
        this.myChar = myChar;
    }

    public byte[] getMyBytes()
    {
        return myBytes;
    }

    public void setMyBytes(byte[] myBytes)
    {
        this.myBytes = myBytes;
    }

    public short[] getMyShorts()
    {
        return myShorts;
    }

    public void setMyShorts(short[] myShorts)
    {
        this.myShorts = myShorts;
    }

    public int[] getMyInts()
    {
        return myInts;
    }

    public void setMyInts(int[] myInts)
    {
        this.myInts = myInts;
    }

    public long[] getMyLongs()
    {
        return myLongs;
    }

    public void setMyLongs(long[] myLongs)
    {
        this.myLongs = myLongs;
    }

    public float[] getMyFloats()
    {
        return myFloats;
    }

    public void setMyFloats(float[] myFloats)
    {
        this.myFloats = myFloats;
    }

    public double[] getMyDoubles()
    {
        return myDoubles;
    }

    public void setMyDoubles(double[] myDoubles)
    {
        this.myDoubles = myDoubles;
    }

    public boolean[] getMyBooleans()
    {
        return myBooleans;
    }

    public void setMyBooleans(boolean[] myBooleans)
    {
        this.myBooleans = myBooleans;
    }

    public char[] getMyChars()
    {
        return myChars;
    }

    public void setMyChars(char[] myChars)
    {
        this.myChars = myChars;
    }

    @Override
    public String toString()
    {
        return "TypePrimitive [myByte=" + myByte + ", myShort=" + myShort + ", myInt=" + myInt + ", myLong=" + myLong
                + ", myFloat=" + myFloat + ", myDouble=" + myDouble + ", myBoolean=" + myBoolean + ", myChar=" + myChar
                + ", myBytes=" + Arrays.toString(myBytes) + ", myShorts=" + Arrays.toString(myShorts) + ", myInts="
                + Arrays.toString(myInts) + ", myLongs=" + Arrays.toString(myLongs) + ", myFloats="
                + Arrays.toString(myFloats) + ", myDoubles=" + Arrays.toString(myDoubles) + ", myBooleans="
                + Arrays.toString(myBooleans) + ", myChars=" + Arrays.toString(myChars) + "]";
    }

    
}
