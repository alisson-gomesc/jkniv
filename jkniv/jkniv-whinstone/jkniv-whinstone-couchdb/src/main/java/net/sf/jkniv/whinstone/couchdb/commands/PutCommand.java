/* 
 * JKNIV, whinstone one contract to access your database.
 * 
 * Copyright (C) 2017, the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jkniv.whinstone.couchdb.commands;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.Consts;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.sqlegance.RepositoryException;

public class PutCommand implements DbCommand
{
    private static final Logger LOG = LoggerFactory.getLogger(PutCommand.class);
    private HttpPut            put;
    private String body;

    public PutCommand(HttpPut put)
    {
        this(put,"");
    }

    public PutCommand(HttpPut put, String body)
    {
        this.body = body;
        this.put = put;
        StringEntity ebody = new StringEntity(body, Consts.UTF_8); // TODO config charset for HTTP body
        this.put.setEntity(ebody);
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
            response = httpclient.execute(this.put);
            json = EntityUtils.toString(response.getEntity());
            //LOG.debug(response.getStatusLine().toString());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HTTP_OK || statusCode == HTTP_CREATED)
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
                LOG.error("Http Body\n{}", EntityUtils.toString(put.getEntity()));
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
