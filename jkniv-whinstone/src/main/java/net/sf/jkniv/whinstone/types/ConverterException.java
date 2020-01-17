package net.sf.jkniv.whinstone.types;

import net.sf.jkniv.sqlegance.RepositoryException;

public class ConverterException extends RepositoryException
{
    private static final long serialVersionUID = 133741996020374064L;

    /**
     * Constructor for ConverterException without message detail
     */
    public ConverterException()
    {
        super();
    }
    
    /**
     * Constructor for ConverterException.
     * @param msg the detail from exception message
     */
    public ConverterException(String msg)
    {
        super(msg);
    }
    
    /**
     * Constructor for ConverterException.
     * @param msg the detail from exception message
     * @param cause the trouble root cause , usually JDBC family exception
     */
    public ConverterException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
    
    public ConverterException(Throwable cause)
    {
        super(cause);
    }
    
}
