package net.sf.jkniv.whinstone.json;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.jkniv.whinstone.couchdb.commands.JsonMapper;
import net.sf.jkniv.whinstone.couchdb.model.orm.Author;

public class JsonTest
{

    @Test
    public void whenFormatDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        //2018-04-02T15:21:23.225-0300
        //2018-04-02T15:21:39.698-0300
        //System.out.println(sdf.format(new Date()));
        
    }
    
    @SuppressWarnings("rawtypes")
    @Test 
    public void whenReadPartialJsonResponse() throws JsonParseException, JsonMappingException, IOException {
        String findResponse = "{" +
                "\"docs\": [" +
                         "{\"_id\":\"3\",\"_rev\":\"1-db179283a1e19876337637299176de51\","+
                        "\"name\":\"Carlos Drummond de Andrade\",\"nationality\":\"BR\"}"+
                       "]," +
                       "\"bookmark\": \"g1AAAAAyeJzLYWBgYMpgSmHgKy5JLCrJTq2MT8lPzkzJBYozGoMkOGASEKEsAErgDR0\","+
                       "\"warning\": \"no matching index found, create an index to optimize query time\"" +
                      "}";
        
        JsonNode rootNode = new ObjectMapper().readValue(findResponse, JsonNode.class); 

        String warn = rootNode.get("warning").asText();
        String bookmark = rootNode.get("bookmark").asText();
        JsonNode docs = rootNode.get("docs");
        Author a = null;
        List list = new ArrayList();
        for (int i=0; i<docs.size(); i++)
        {
            JsonNode n = docs.get(i);
            list.add(JsonMapper.mapper(n.toString(), Author.class));
            //System.out.println(n.toString());
            //Object currentRow = row;
            //list.add(JsonMapper.mapper((Map) row, returnType));
        }        

        assertThat(warn, is("no matching index found, create an index to optimize query time"));
        assertThat(bookmark, is("g1AAAAAyeJzLYWBgYMpgSmHgKy5JLCrJTq2MT8lPzkzJBYozGoMkOGASEKEsAErgDR0"));
        assertThat(list.size(), is(1));
        assertThat(list.get(0), instanceOf(Author.class));
        //System.out.println(docs);
    }
    
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
