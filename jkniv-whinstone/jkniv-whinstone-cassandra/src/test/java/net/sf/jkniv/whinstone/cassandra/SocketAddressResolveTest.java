package net.sf.jkniv.whinstone.cassandra;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


import org.junit.Test;

public class SocketAddressResolveTest
{
    
    @Test
    public void whenResolveAddress()
    {
        String url = "127.0.0.1:8080";
        SocketAddressResolve resolve = SocketAddressResolve.of(url, 8080);
        assertThat(resolve.getHost(), is("127.0.0.1"));
        assertThat(resolve.getPort(), is(8080));
        
    }
}
