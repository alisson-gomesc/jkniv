package net.sf.jkniv.whinstone.couchdb.model.orm;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.sf.jkniv.sqlegance.validation.AddValidate;
import net.sf.jkniv.sqlegance.validation.UpdateValidate;

/**
 * Representa os atributos da sessao do usuario: 
 * {@code id da sessao}, {@code hostname}, {@code aplicaçao}, {@code HTTP ou WS}, {@code timestamp}.
 * 
 * @author alisson
 */
@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Session
{
    private static final transient Logger LOG = LoggerFactory.getLogger(Session.class);
    
    public static enum Protocol
    {
        HTTP, WS
    };
    
    @NotNull(groups =
    { UpdateValidate.class, AddValidate.class })
    @JsonProperty("_id")
    private String     id;
    @NotNull(groups =
    { UpdateValidate.class, AddValidate.class })
    @JsonProperty
    private Protocol   protocol;
    @JsonProperty("_rev")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String     rev;
    @JsonProperty private final String type;
    @JsonProperty private String     email;
    @JsonProperty private String     host;
    @JsonProperty private String     context;
    @JsonProperty private String     ipv4;
    @JsonProperty private Date       timestamp;
    
    public Session()
    {
        this.type = "SESSION";
        this.timestamp = new Date();
        this.host = hostname();
    }
    
    public Session(String id)
    {
        this();
        this.id = id;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setRev(String rev)
    {
        this.rev = rev;
    }
    
    public String getRev()
    {
        return rev;
    }
    
    public String getHost()
    {
        return host;
    }
    
    public void setHost(String host)
    {
        this.host = host;
    }
    
    public String getContext()
    {
        return context;
    }
    
    public void setContext(String context)
    {
        this.context = context;
    }
    
    public Date getTimestamp()
    {
        return timestamp;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public Protocol getProtocol()
    {
        return protocol;
    }
    
    public void setProtocol(Protocol protocol)
    {
        this.protocol = protocol;
    }
    
    public String getType()
    {
        return type;
    }

    public String getIpv4()
    {
        return ipv4;
    }
    
    public void setIpv4(String ipv4)
    {
        this.ipv4 = ipv4;
    }
    
    private String hostname()
    {
        String hostname = "localhost";
        try
        {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e)
        {
            LOG.warn("Unknown host [{}]", e.getMessage());
        }
        return hostname;
    }
    
    @Override
    public String toString()
    {
        return "Session [id=" + id + ", email=" + email + ", protocol=" + protocol + ", host=" + host + ", context="
                + context + ", timestamp=" + timestamp + ", type=" + type + "]";
    }
    
}
