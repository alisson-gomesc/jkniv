package net.sf.jkniv.whinstone.couchdb.commands;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.RepositoryException;

public class PostCommand implements DbCommand
{
    private static final Logger LOG = LoggerFactory.getLogger(PostCommand.class);
    private HttpPost            post;
    private String body;
    
    public PostCommand(HttpPost post, String body)
    {
        this.body = body;
        this.post = post;
        try
        {
            StringEntity ebody = new StringEntity(body);
            this.post.setEntity(ebody);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RepositoryException("Cannot build new URI ["+this.post.getURI()+"] encoding unsupported\n"+body);
        }
    }
    
    @Override
    public String getBody()
    {
        return this.body;
    }
    
    @Override
    public <T> T execute()
    {
        String json = null;
        CloseableHttpResponse response = null;
        try
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            response = httpclient.execute(this.post);
            json = EntityUtils.toString(response.getEntity());
            //LOG.debug(response.getStatusLine().toString());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HTTP_OK)
            {
                // FIXME
                ///rs = session.execute(bound);
                //JdbcColumn<Row>[] columns = getJdbcColumns(rs.getColumnDefinitions());
                //setResultRow(columns);
                //
                //Transformable<T> transformable = resultRow.getTransformable();
                //            if (!groupingBy.isEmpty())
                //            {
                //                grouping = new GroupingBy(groupingBy, returnType, transformable);
                //            }
                //rsParser = new ObjectResultSetParser(resultRow, grouping);
                //list = rsParser.parser(json);
            }
            else if (statusCode == HTTP_NO_CONTENT || statusCode == HTTP_NOT_MODIFIED
                    || statusCode == HTTP_RESET_CONTENT)
            {
                // 204 No Content, 304 Not Modified, 205 Reset Content
                LOG.info(response.getStatusLine().toString());
            }
            else
            {
                LOG.error("Http Body\n{}", EntityUtils.toString(post.getEntity()));
                LOG.error("{} -> {} ", response.getStatusLine().toString(), json);
                throw new RepositoryException(response.getStatusLine().toString());
            }
        }
        catch (ClientProtocolException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally
        {
            if (response != null)
            {
                try
                {
                    response.close();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return (T)json;
    }
    
}
