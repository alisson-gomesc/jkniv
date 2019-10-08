package net.sf.jkniv.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
*
* Bruce Eckel developed with the help of Heinz Kabutz. 
* It converts any checked exception into a RuntimeException while preserving 
* all the information from the checked exception. 
* 
* @author Bruce Eckel 
* @author Heinz Kabutz
* @see <a href="http://www.mindview.net/Etc/Discussions/CheckedExceptions">ExceptionAdapter</a>
*/
class ExceptionAdapter extends RuntimeException
{
    private static final long serialVersionUID = 738261894761051726L;
    private final String      stackTrace;
    public Exception          originalException;
    
    public ExceptionAdapter(Exception e)
    {
        super(e.toString());
        originalException = e;
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        stackTrace = sw.toString();
    }
    
    public void printStackTrace()
    {
        printStackTrace(System.err);
    }
    
    public void printStackTrace(java.io.PrintStream s)
    {
        synchronized (s)
        {
            s.print(getClass().getName() + ": ");
            s.print(stackTrace);
        }
    }
    
    public void printStackTrace(java.io.PrintWriter s)
    {
        synchronized (s)
        {
            s.print(getClass().getName() + ": ");
            s.print(stackTrace);
        }
    }
    
    public void rethrow() throws Exception
    {
        throw originalException;
    }
}
