package net.sf.jkniv.whinstone.httpapache;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

public class UriTest
{
    @Test
    public void whenInstanceNewUri() throws URISyntaxException
    {
        URI uri = new URI("http://127.0.0.1:5984");
        System.out.println(uri);
        System.out.println(uri.normalize());
    }
    
    @Test
    public void whenParseCookie()
    {
        String cookie = "AuthSession=YWRtaW46NUFCODY2MDg63KLf0HOr26oX3jlyizmsr5458Mc; Version=1; Path=/; HttpOnly";
        assertThat(cookie.split(";")[0], is("AuthSession=YWRtaW46NUFCODY2MDg63KLf0HOr26oX3jlyizmsr5458Mc"));
    }
}
