/* 
 * JKNIV, SQLegance keeping queries maintainable.
 * 
 * Copyright (C) 2017, the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.jkniv.sqlegance.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.jkniv.logger.FormatterLogger;

/**
 * Centralize to write all SQL statements to console. 
 * 
 * This is an alternative to setting the logger category <code>net.sf.jkniv.sqlegance.SQL</code> to debug.
 * 
 * hibernate.show_sql
 * 
 * @author Alisson Gomes
 *
 */
public class SqlLogger// implements Logger
{
    private static final Logger          LOG       = LoggerFactory.getLogger("net.sf.jkniv.sqlegance.SQL");
    private static final FormatterLogger formatter = new FormatterLogger();
    private LogLevel                     logLevel;
    private DataMasking                  masking;
    
    public SqlLogger(LogLevel logLevel, DataMasking masking)
    {
        this.logLevel = logLevel;
        this.masking = masking;
    }
    
    public Object mask(String attributeName, Object data)
    {
        return this.masking.mask(attributeName, data);
    }
    
    public LogLevel getLogLevel()
    {
        return logLevel;
    }
    
    //    /**
    //     * Debug configured level
    //     * @param format TODO javadoc
    //     * @param args TODO javadoc
    //     */
    //    public void debug(String format, Object... args)
    //    {
    //        log(logLevel, format, args);
    //    }
    
    //    /**
    //     * Debug level connection
    //     * @param format TODO javadoc
    //     * @param args TODO javadoc
    //     */
    //    public void debugconn(String format, Object... args)
    //    {
    //        log(LogLevel.CONN, format, args);
    //    }
    
    //    /**
    //     * Debug level transaction
    //     * @param format TODO javadoc
    //     * @param args TODO javadoc
    //     */
    //    public void debugtx(String format, Object... args)
    //    {
    //        log(LogLevel.TX, format, args);
    //    }
    
    //    /**
    //     * Debug level Statement
    //     * @param format TODO javadoc
    //     * @param args TODO javadoc
    //     */
    //    public void debugstmt(String format, Object... args)
    //    {
    //        log(LogLevel.STMT, format, args);
    //    }
    
    /**
     * Debug level ResultSet
     * @param format TODO javadoc
     * @param args TODO javadoc
     */
    public void log(String format, Object... args)
    {
        log(LogLevel.RESULTSET, format, args);
    }
    
    public void log(LogLevel level, String format, Object... args)
    {
        if (LOG.isDebugEnabled())
            LOG.debug(format, args);
        else if (level.ordinal() <= logLevel.ordinal() && logLevel != LogLevel.NONE)
        {
            System.out.printf("[" + Thread.currentThread().getName() + "] " + "[" + this.logLevel + "] "
                    + formatter.formatterSlf4j(format) + "\n", formatter.toString(args));
        }
    }
    
    public boolean isEnabled(LogLevel level)
    {
        return (LOG.isDebugEnabled() || (level.ordinal() <= logLevel.ordinal() && logLevel != LogLevel.NONE));
    }
    
    //    public boolean isConnEnabled()
    //    {
    //        return (LOG.isDebugEnabled() || (LogLevel.CONN.ordinal() <= logLevel.ordinal() && logLevel != LogLevel.NONE));
    //    }
    
    //    public boolean isTxEnabled()
    //    {
    //        return (LOG.isDebugEnabled() || (LogLevel.TX.ordinal() <= logLevel.ordinal() && logLevel != LogLevel.NONE));
    //    }
    
    //    public boolean isStmtEnabled()
    //    {
    //        return (LOG.isDebugEnabled() || (LogLevel.STMT.ordinal() <= logLevel.ordinal() && logLevel != LogLevel.NONE));
    //    }
    //    
    //    public boolean isResultSetEnabled()
    //    {
    //        return (LOG.isDebugEnabled() || (LogLevel.RESULTSET.ordinal() <= logLevel.ordinal() && logLevel != LogLevel.NONE));
    //    }
    
    /*
    
    public void trace(String format, Object... args)
    {
        LOG.trace(format, args);
    }
    
    public void trace(String format, Object args)
    {
        LOG.trace(format, args);
    }
    
    public void trace(String format)
    {
        LOG.trace(format);
    }
    
    public void trace(String format, Throwable t)
    {
        LOG.trace(format, t);
    }
    
    public void trace(Marker marker, String format)
    {
        LOG.trace(marker, format);
    }
    
    ///
    public void trace(String format, Object arg1, Object arg2)
    {
        LOG.trace(format, arg1, arg2);
    }
    
    public void trace(Marker marker, String format, Object arg)
    {
        LOG.trace(marker, format, arg);
    }
    
    public void trace(Marker marker, String format, Object arg1, Object arg2)
    {
        LOG.trace(marker, format, arg1, arg2);
    }
    
    public void trace(Marker marker, String format, Object... argArray)
    {
        LOG.trace(marker, format, argArray);
    }
    
    public void trace(Marker marker, String msg, Throwable t)
    {
        LOG.trace(marker, msg, t);
    }
    
    public void debug(String format, Object... args)
    {
        LOG.debug(format, args);
    }
    
    public void debug(String format, Object args)
    {
        LOG.debug(format, args);
    }
    
    public void debug(String format)
    {
        LOG.debug(format);
    }
    
    public void debug(String format, Throwable t)
    {
        LOG.debug(format, t);
    }
    
    public void debug(Marker marker, String format)
    {
        LOG.debug(marker, format);
    }
    
    ///
    public void debug(String format, Object arg1, Object arg2)
    {
        LOG.debug(format, arg1, arg2);
    }
    
    public void debug(Marker marker, String format, Object arg)
    {
        LOG.debug(marker, format, arg);
    }
    
    public void debug(Marker marker, String format, Object arg1, Object arg2)
    {
        LOG.debug(marker, format, arg1, arg2);
    }
    
    public void debug(Marker marker, String format, Object... args)
    {
        LOG.debug(marker, format, args);
    }
    
    public void debug(Marker marker, String msg, Throwable t)
    {
        LOG.debug(marker, msg, t);
    }
    
    public void info(String format, Object... args)
    {
        LOG.info(format, args);
    }
    
    public void info(String format, Object args)
    {
        LOG.info(format, args);
    }
    
    public void info(String format)
    {
        LOG.error(format);
    }
    
    public void info(String format, Throwable t)
    {
        LOG.info(format, t);
    }
    
    public void info(Marker marker, String format)
    {
        LOG.info(marker, format);
    }
    
    public void info(String format, Object arg1, Object arg2)
    {
        LOG.info(format, arg1, arg2);
    }
    
    public void info(Marker marker, String format, Object arg)
    {
        LOG.info(marker, format, arg);
    }
    
    public void info(Marker marker, String format, Object arg1, Object arg2)
    {
        LOG.info(marker, format, arg1, arg2);
    }
    
    public void info(Marker marker, String format, Object... args)
    {
        LOG.info(marker, format, args);
    }
    
    public void info(Marker marker, String msg, Throwable t)
    {
        LOG.info(marker, msg, t);
    }
    
    public void warn(String format, Object... args)
    {
        LOG.warn(format, args);
    }
    
    public void warn(String format, Object args)
    {
        LOG.warn(format, args);
    }
    
    public void warn(String format)
    {
        LOG.warn(format);
    }
    
    public void warn(String format, Throwable t)
    {
        LOG.info(format, t);
    }
    
    public void warn(Marker marker, String format)
    {
        LOG.warn(marker, format);
    }
    
    public void warn(String format, Object arg1, Object arg2)
    {
        LOG.warn(format, arg1, arg2);
    }
    
    public void warn(Marker marker, String format, Object arg)
    {
        LOG.warn(marker, format, arg);
    }
    
    public void warn(Marker marker, String format, Object arg1, Object arg2)
    {
        LOG.warn(marker, format, arg1, arg2);
    }
    
    public void warn(Marker marker, String format, Object... args)
    {
        LOG.warn(marker, format, args);
    }
    
    public void warn(Marker marker, String msg, Throwable t)
    {
        LOG.warn(marker, msg, t);
    }
    
    public void error(String format, Object args)
    {
        LOG.error(format, args);
    }
    
    public void error(String format)
    {
        LOG.error(format);
    }
    
    public void error(String format, Throwable t)
    {
        LOG.error(format, t);
    }
    
    public void error(Marker marker, String format)
    {
        LOG.error(marker, format);
    }
    
    public void error(String format, Object arg1, Object arg2)
    {
        LOG.error(format, arg1, arg2);
    }
    
    public void error(String format, Object... args)
    {
        LOG.error(format, args);
    }
    
    public void error(Marker marker, String format, Object arg)
    {
        LOG.error(marker, format, arg);
    }
    
    public void error(Marker marker, String format, Object arg1, Object arg2)
    {
        LOG.error(marker, format, arg1, arg2);
    }
    
    public void error(Marker marker, String format, Object... args)
    {
        LOG.error(marker, format, args);
    }
    
    public void error(Marker marker, String msg, Throwable t)
    {
        LOG.error(marker, msg, t);
    }
    
    public String getName()
    {
        return LOG.getName();
    }
    
    public boolean isTraceEnabled()
    {
        return LOG.isTraceEnabled();
    }
    
    public boolean isTraceEnabled(Marker marker)
    {
        return LOG.isTraceEnabled(marker);
    }
    
    public boolean isDebugEnabled()
    {
        return LOG.isDebugEnabled();
    }
    
    public boolean isDebugEnabled(Marker marker)
    {
        return LOG.isDebugEnabled(marker);
    }
    
    public boolean isInfoEnabled()
    {
        return LOG.isInfoEnabled();
    }
    
    public boolean isInfoEnabled(Marker marker)
    {
        return LOG.isInfoEnabled(marker);
    }
    
    public boolean isWarnEnabled()
    {
        return LOG.isWarnEnabled();
    }
    
    public boolean isWarnEnabled(Marker marker)
    {
        return LOG.isWarnEnabled(marker);
    }
    
    public boolean isErrorEnabled()
    {
        return LOG.isErrorEnabled();
    }
    
    public boolean isErrorEnabled(Marker marker)
    {
        return LOG.isErrorEnabled(marker);
    }
    */
}
