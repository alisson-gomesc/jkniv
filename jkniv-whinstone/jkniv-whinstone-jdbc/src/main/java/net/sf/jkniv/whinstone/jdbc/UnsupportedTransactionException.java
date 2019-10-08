package net.sf.jkniv.whinstone.jdbc;

import net.sf.jkniv.sqlegance.RepositoryException;

public class UnsupportedTransactionException extends RepositoryException
{
    private static final long serialVersionUID = 5034231876045628260L;
    
    
    /**
     * Constructor for UnsupportedTransactionException.
     * @param msg the detail from exception message
     */
    public UnsupportedTransactionException(String msg) {
        super(msg);
    }

    /**
     * Constructor for UnsupportedTransactionException.
     * @param msg the detail from exception message
     * @param cause the trouble root cause , usually JDBC family exception
     */
    public UnsupportedTransactionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
