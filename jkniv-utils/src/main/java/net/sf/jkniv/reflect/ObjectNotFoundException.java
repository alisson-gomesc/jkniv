package net.sf.jkniv.reflect;

/**
 * Indicate that some Object could not be found.
 * 
 * @author Alisson Gomes
 * @since 0.6.0
 */
public class ObjectNotFoundException extends ReflectionException
{
    private static final long serialVersionUID = -5813925852549886446L;
    public ObjectNotFoundException()
    {
        super();
    }
    
    public ObjectNotFoundException(String msg)
    {
        super(msg);
    }
    
    public ObjectNotFoundException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
    
    public ObjectNotFoundException(Throwable cause)
    {
        super(cause);
    }
 
}
