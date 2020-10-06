package net.sf.jkniv.whinstone.cassandra;

class SocketAddressResolve
{
    private String host;
    private int port;
    
    static SocketAddressResolve of(String host)
    {
        return new SocketAddressResolve(host);
    }
    
    private SocketAddressResolve(String host)
    {
        int index = host.indexOf(":");
        if (index > 0)
        {
            this.host = host.substring(0, index);
            this.port = Integer.valueOf(host.substring(index));
        }
        else {
            this.port = 80;
            this.host = host;
        }
    }
    
    public String getHost()
    {
        return this.host;
    }

    public int getPort()
    {
        return this.port;
    }
    
    @Override
    public String toString()
    {
        return this.host+":"+this.port;
    }
}
