package net.sf.jkniv.whinstone.couchdb;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.sqlegance.ConstraintException;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.whinstone.ConnectionAdapter;

public class HttpConnectionFactoryTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  
    private Properties props;
    private final HandleableException handlerException = new HandlerException(RepositoryException.class, "Repository error %s");
    
    @Before
    public void setUp()
    {
        props = new Properties();
        props.setProperty(RepositoryProperty.JDBC_URL.key(), "http://127.0.0.1:5984");
        props.setProperty(RepositoryProperty.JDBC_SCHEMA.key(), "db3t-useraccess");
        props.setProperty(RepositoryProperty.JDBC_USER.key(), "admin");
        props.setProperty(RepositoryProperty.JDBC_PASSWORD.key(), "admin");
    }
    
    @Test
    public void whenOpenHttpConnection()
    {
        HttpConnectionFactory factory = new HttpConnectionFactory(props, "default");
        factory.with(handlerException);
        ConnectionAdapter conn  = factory.open();
        assertThat(conn, notNullValue());
    }
    
    @Test @Ignore("FAIL, wrong password was connecting")
    public void whenCannotOpenHttpConnection()
    {
        props.setProperty(RepositoryProperty.JDBC_PASSWORD.key(), "wrong");
        //catcher.expect(RepositoryException.class);
        //catcher.expectMessage("Access denied, unauthorized for user [admin] and url [http://127.0.0.1:5984]");
        HttpConnectionFactory factory = new HttpConnectionFactory(props, "default");
        factory.with(handlerException);
        factory.open();
    }

    @Test
    public void whenOpenHttpConnectionGetRequest()
    {
        HttpConnectionFactory factory = new HttpConnectionFactory(props, "default");
        factory.with(handlerException);
        ConnectionAdapter conn = factory.open();
        
        //conn.
    }

}
