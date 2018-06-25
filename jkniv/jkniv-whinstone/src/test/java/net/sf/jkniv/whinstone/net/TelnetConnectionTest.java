package net.sf.jkniv.whinstone.net;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

public class TelnetConnectionTest
{
    
    @Test
    public void when() 
    {
        assertThat(isUp(5984), is(true));
        assertThat(isUp(1521), is(true));
        assertThat(isUp(1445), is(false));
    }
    
    public static boolean isUp(int port) {
        boolean b = true;
        try{
            InetSocketAddress sa = new InetSocketAddress("localhost", port);
            Socket ss = new Socket();
            ss.connect(sa, 200);
            ss.close();
        }catch(Exception e) {
            b = false;
        }
        return b;
    }
}
