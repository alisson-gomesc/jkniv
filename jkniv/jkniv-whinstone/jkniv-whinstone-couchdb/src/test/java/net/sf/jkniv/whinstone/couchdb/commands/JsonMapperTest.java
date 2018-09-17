package net.sf.jkniv.whinstone.couchdb.commands;

import static org.hamcrest.Matchers.instanceOf;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;


import org.junit.Test;

public class JsonMapperTest
{

    @Test
    public void whenJsonMapperStringfyData()
    {
        assertThat("1", is(JsonMapper.mapper(1)));
        assertThat("\"2\"", is(JsonMapper.mapper("2")));
        assertThat("3.0", is(JsonMapper.mapper(3F)));
        assertThat("4.0", is(JsonMapper.mapper(4D)));
        assertThat("5", is(JsonMapper.mapper(5L)));
        assertThat("6", is(JsonMapper.mapper(Short.valueOf("6"))));
        assertThat("true", is(JsonMapper.mapper(true)));
        assertThat("false", is(JsonMapper.mapper(Boolean.FALSE)));
        
        
        assertThat("[]", is(JsonMapper.mapper(Collections.emptyList())));
        assertThat("[\"a\",\"b\",\"c\"]", is(JsonMapper.mapper(Arrays.asList("a","b","c"))));
        assertThat("[\"a\",\"b\",\"c\"]", is(JsonMapper.mapper(new String[]{"a","b","c"})));
    }
}
