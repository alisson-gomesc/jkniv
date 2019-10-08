package net.sf.jkniv.whinstone.couchdb;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Ignore;
import org.junit.Test;

public class ApacheHttpClientTest
{
    
    @Test @Ignore("RequestParams doesn't accept cookie any more. Use CouchDbAuthenticate")
    public void whenAuthRequestGetRecord()
    {
        //"AuthSession=YWRtaW46NUFCODQ5MkU6z0cUPq7oPfFz6zh3-ghAZJeDNEE"
        RequestParams header = new RequestParams("tecno3t-useraccess");
        String uri = "http://127.0.0.1:5984/tecno3t-useraccess/1";
        String answer = executeGet(uri, header);
        System.out.println(answer);
        assertThat(answer, not(is("Unauthorized")));
    }

    @Test @Ignore("Use CouchDbAuthenticate")
    public void whenAuthRequestGetSecurity()
    {
        String uri = "http://admin:admin@127.0.0.1:5984/tecno3t-useraccess/_security";
        String answer = executeGet(uri, null);
        System.out.println(answer);
        assertThat(answer, not(is("Unauthorized")));
    }

    @Test //@Ignore("Use CouchDbAuthenticate")
    public void whenAuthenticate()
    {
        String uri = "http://127.0.0.1:5984/_session";
        String answer = authenticate(uri);
        System.out.println(answer);
        assertThat(answer, not(is("Unauthorized")));
    }

    @Test
    public void whenAuthRequestAllDbs()
    {
        String uri = "http://admin:admiin@127.0.0.1:5984/_all_dbs";
        String answer = executeGet(uri, null);
        System.out.println(answer);
        assertThat(answer, not(is("Unauthorized")));
    }
    
    private String executeGet(String uri, RequestParams header)
    {
        String content = "";
        try
        {
            Content response = null;
            if (header == null)
                response = Request.Get(uri).addHeader("Accept","application/json").execute().returnContent();
            else
            {
                response = Request.Get(uri)
                        .addHeader(header.getAccept())
                        .addHeader(header.getContentType())
                        .addHeader(header.getConnection())
                        .addHeader(header.getCookie())
                        .execute()
                        .returnContent();    
            }
            System.out.println(response.asString());
        }
        catch (HttpResponseException  ex)
        {
            content = ex.getMessage();
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException  e)
        {
            e.printStackTrace();
        }
        return content;
    }
    

    private String authenticate(String uri)
    {
        String content = "";
        try
        {
            HttpClientBuilder builder = HttpClientBuilder.create();
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(uri);
            httpPost.addHeader("Content-Type" , "application/x-www-form-urlencoded; charset=UTF-8");

            StringEntity xmlEntity = new StringEntity("name=admin&password=admin", Consts.UTF_8); // TODO config charset for HTTP body
            httpPost.setEntity(xmlEntity);
            
            HttpResponse response = httpclient.execute(httpPost);

            String status =  response.getStatusLine().toString();
            System.out.println("-------------\n"+status+"\n-------------");
         
            showResponseHeaders(response.headerIterator());
            content = EntityUtils.toString(response.getEntity());
        }
        catch (HttpResponseException  ex)
        {
            content = ex.getMessage();
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException  e)
        {
            e.printStackTrace();
        }
        return content;
    }

    private void showResponseHeaders(HeaderIterator it)
    {
        while(it.hasNext())
        {
            Header header = it.nextHeader();
            System.out.println(header.getName() +"=" +header.getValue());
        }        
    }
}
