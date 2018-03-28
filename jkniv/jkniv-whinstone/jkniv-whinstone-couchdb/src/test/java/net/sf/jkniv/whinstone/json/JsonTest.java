package net.sf.jkniv.whinstone.json;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class JsonTest
{

    @Test
    public void whenFormatDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        //2018-04-02T15:21:23.225-0300
        //2018-04-02T15:21:39.698-0300
        System.out.println(sdf.format(new Date()));
        
    }
}
