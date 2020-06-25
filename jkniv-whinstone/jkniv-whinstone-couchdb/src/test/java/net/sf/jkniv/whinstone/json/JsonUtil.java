package net.sf.jkniv.whinstone.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.sf.jkniv.sqlegance.DefaultClassLoader;

public class JsonUtil
{
    private final static ObjectMapper JSON = new ObjectMapper();
    static
    {
        JSON.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        JSON.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        JSON.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
        //JSON.registerModule(new JavaTimeModule());
        //JSON.registerModule(new Jdk8Module());
    }

    public static <T> T fromFile(String fileName, Class<T> type)
    {
        InputStream is = DefaultClassLoader.getResourceAsStream(fileName);
        T t = null;
        try
        {
            t = JSON.readValue(is, type);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return t;
    }

    public static String fromFile(String fileName)
    {
        InputStream is = DefaultClassLoader.getResourceAsStream(fileName);
        StringBuilder textBuilder = new StringBuilder();
        Reader reader = new BufferedReader(new InputStreamReader(is));
        int c = 0;
        try
        {
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return textBuilder.toString();
    }
}
