package net.sf.jkniv.hibernate4;


import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.type.StringType;

import net.sf.jkniv.hibernate4.types.IntervalDsUserType;

/**
 * Register the oracle types:
 * <ul>
 *  <li><code>-104 INTERVALDS</code> to <code>Duration</code></li>
 *  <li><code>2011 NCLOB</code> to <code>String</code></li>
 *  <li><code>2005 CLOB</code> to <code>String</code></li>
 * </ul>
 * @author Alisson Gomes
 *
 */
public class Oracle10gExtendDialect extends Oracle10gDialect
{
    public Oracle10gExtendDialect()
    {
        super();
        registerHibernateType(oracle.jdbc.OracleTypes.INTERVALDS, IntervalDsUserType.class.getName()); 
        registerHibernateType(oracle.jdbc.OracleTypes.NCLOB, StringType.class.getName());
        registerHibernateType(oracle.jdbc.OracleTypes.CLOB, StringType.class.getName());
    }
}
