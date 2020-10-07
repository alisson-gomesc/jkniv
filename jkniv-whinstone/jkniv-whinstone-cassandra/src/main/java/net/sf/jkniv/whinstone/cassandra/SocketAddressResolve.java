package net.sf.jkniv.whinstone.cassandra;

class SocketAddressResolve
{
    private String host;
    private int port;
    
    static SocketAddressResolve of(String host, int defaultPort)
    {
        return new SocketAddressResolve(host, defaultPort);
    }
    
    private SocketAddressResolve(String host, int defaultPort)
    {
        int index = host.indexOf(":");
        if (index > 0)
        {
            this.host = host.substring(0, index);
            this.port = Integer.valueOf(host.substring(index+1));
        }
        else {
            this.port = defaultPort;
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
