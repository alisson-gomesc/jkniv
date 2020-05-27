package net.sf.jkniv.whinstone.httpapache;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class ClientAuthentication
{
    public static void main(String[] args) throws Exception
    {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope("192.168.99.100", 5984),
                new UsernamePasswordCredentials("tecno3t", "tecno"));
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        try
        {
            //HttpGet httpget = new HttpGet("http://10.1.194.161:5984/estados");
            //HttpGet httpget = new HttpGet("http://192.168.99.100:5984/albums/_all_docs");
            String uri = "http://tecno3t:tecno@192.168.99.100:5984/tecno3t-useraccess/1";
            HttpGet httpget = new HttpGet(uri);
            
            System.out.println("Executing request " + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try
            {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println(EntityUtils.toString(response.getEntity()));
            }
            finally
            {
                response.close();
            }
        }
        finally
        {
            httpclient.close();
        }
    }
}
