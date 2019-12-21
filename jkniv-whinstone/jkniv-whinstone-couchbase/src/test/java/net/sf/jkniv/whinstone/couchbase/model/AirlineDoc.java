package net.sf.jkniv.whinstone.couchbase.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AirlineDoc implements Serializable
{
    private static final long serialVersionUID = -6768367216011678157L;
    @JsonProperty("callsign")
    private String callsign;
    @JsonProperty("country")
    private String country;
    @JsonProperty("iata")
    private String iata;
    @JsonProperty("icao")
    private String icao;
    @JsonProperty("name")
    private String name;
    @JsonProperty("id")
    private String id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("cas")
    private long   cas;
    @JsonProperty("expire")
    private int    expire;
    
    //    public AirlineDoc(Document d)
    //    {
    //        super(d.id(), d.expiry(), null, d.cas(), d.mutationToken());
    //    }
    //    
    //    public static AirlineDoc create(String id)
    //    {
    //      return new AirlineDoc(id, 0, null, 0L, null);
    //    }
//    
//    public static AirlineDoc create(String id, AirlineDoc content)
//    {
//        return new AirlineDoc(id, 0, content, 0L, null);
//    }
//    
//    public static AirlineDoc create(String id, AirlineDoc content, long cas)
//    {
//        return new AirlineDoc(id, 0, content, cas, null);
//    }
//    
//    public static AirlineDoc create(String id, int expiry, AirlineDoc content)
//    {
//        return new AirlineDoc(id, expiry, content, 0L, null);
//    }
//    
//    public static AirlineDoc create(String id, int expiry, AirlineDoc content, long cas)
//    {
//        return new AirlineDoc(id, expiry, content, cas, null);
//    }
//    
//    public static AirlineDoc create(String id, int expiry, AirlineDoc content, long cas, MutationToken mutationToken)
//    {
//        return new AirlineDoc(id, expiry, content, cas, mutationToken);
//    }
//    
//    private AirlineDoc(String id, int expiry, AirlineDoc content, long cas, MutationToken mutationToken)
//    {
//        super(id, expiry, content, cas, mutationToken);
//    }
    
    public AirlineDoc()
    {
    }
    
    public String getCallsign()
    {
        return callsign;
    }
    
    public void setCallsign(String callsign)
    {
        this.callsign = callsign;
    }
    
    public String getCountry()
    {
        return country;
    }
    
    public void setCountry(String country)
    {
        this.country = country;
    }
    
    public String getIata()
    {
        return iata;
    }
    
    public void setIata(String iata)
    {
        this.iata = iata;
    }
    
    public String getIcao()
    {
        return icao;
    }
    
    public void setIcao(String icao)
    {
        this.icao = icao;
    }
    
    public String getId()
    {
        return this.id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }

    public long getCas()
    {
        return cas;
    }

    public void setCas(long cas)
    {
        this.cas = cas;
    }

    public int getExpire()
    {
        return expire;
    }

    public void setExpire(int expire)
    {
        this.expire = expire;
    }
    
    
}
