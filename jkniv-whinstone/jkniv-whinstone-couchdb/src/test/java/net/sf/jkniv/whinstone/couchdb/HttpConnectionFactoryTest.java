package net.sf.jkniv.whinstone.couchdb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sf.jkniv.exception.HandleableException;
import net.sf.jkniv.exception.HandlerException;
import net.sf.jkniv.sqlegance.RepositoryException;
import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.builder.RepositoryConfig;
import net.sf.jkniv.whinstone.commands.CommandAdapter;

public class HttpConnectionFactoryTest
{
    @Rule
    public ExpectedException catcher = ExpectedException.none();  
    private RepositoryConfig config;
    private final HandleableException handlerException = new HandlerException(RepositoryException.class, "Repository error %s");
    
    @Before
    public void setUp()
    {
        Properties props = new Properties();
        props.setProperty(RepositoryProperty.JDBC_URL.key(), "http://127.0.0.1:5984");
        props.setProperty(RepositoryProperty.JDBC_SCHEMA.key(), "db3t-useraccess");
        props.setProperty(RepositoryProperty.JDBC_USER.key(), "admin");
        props.setProperty(RepositoryProperty.JDBC_PASSWORD.key(), "admin");

    }
    
    @Test @Ignore("RepositoryConfig need contructor with Properties")
    public void whenOpenHttpConnection()
    {
        HttpConnectionFactory factory = new HttpConnectionFactory(config, "default");
        factory.with(handlerException);
        CommandAdapter conn  = factory.open();
        assertThat(conn, notNullValue());
    }
    
    @Test @Ignore("FAIL, wrong password was connecting")
    public void whenCannotOpenHttpConnection()
    {
        //config.setProperty(RepositoryProperty.JDBC_PASSWORD.key(), "wrong");
        ////catcher.expect(RepositoryException.class);
        ////catcher.expectMessage("Access denied, unauthorized for user [admin] and url [http://127.0.0.1:5984]");
        HttpConnectionFactory factory = new HttpConnectionFactory(config, "default");
        factory.with(handlerException);
        factory.open();
    }

    @Test @Ignore("RepositoryConfig need contructor with Properties")
    public void whenOpenHttpConnectionGetRequest()
    {
        HttpConnectionFactory factory = new HttpConnectionFactory(config, "default");
        factory.with(handlerException);
        CommandAdapter conn = factory.open();
    }

}
