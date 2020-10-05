package net.sf.jkniv.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.notNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class URLTest
{

    
    @Test 
    public void whenInstanceURLfromFile() throws MalformedURLException
    {
        String path = "file:///C:/Users/alisson/Downloads/sapjco3-NTintel-3.0.16.zip";
        URL url = new URL(path);
        File file = new File(url.getFile());
        assertThat(url, notNullValue());
        assertThat(file, notNullValue());
        assertThat(file.exists(), is(true));
    }

//    @Test
//    public void whenInstanceURLfromResource() throws MalformedURLException
//    {
//        String path = "classpath:log4j.xml";
//        URL url = new URL(path);
//        File file = new File(url.getFile());
//        assertThat(url, notNullValue());
//        assertThat(file, notNullValue());
//        assertThat(file.exists(), is(true));
//    }


}
