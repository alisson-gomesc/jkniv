package net.sf.jkniv.whinstone.couchdb.commands;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.RepositoryException;

public class DeleteCommand implements DbCommand
{
    private static final Logger LOG = LoggerFactory.getLogger(DeleteCommand.class);
    private HttpDelete            delete;
    //private String body;
    
    public DeleteCommand(HttpDelete delete)
    {
        //this.body = body;
        this.delete = delete;
        
    }
    
    @Override
    public String getBody()
    {
        return null;
    }
    
    @Override
    public <T> T execute()
    {
        String json = null;
        CloseableHttpResponse response = null;
        try
        {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            response = httpclient.execute(this.delete);
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
                //LOG.error("Http Body\n{}", EntityUtils.toString(post.getEntity()));
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
