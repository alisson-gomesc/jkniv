package net.sf.jkniv.whinstone.jdbc.domain.bank;

public class InsufficientFundsException extends RuntimeException
{
    
    public InsufficientFundsException()
    {
        super();
    }
    
    public InsufficientFundsException(String message)
    {
        super(message);
    }
    
}
