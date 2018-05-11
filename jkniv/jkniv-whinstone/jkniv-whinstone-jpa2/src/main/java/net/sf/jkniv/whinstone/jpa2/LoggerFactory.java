package net.sf.jkniv.whinstone.jpa2;

import org.slf4j.Logger;

import net.sf.jkniv.sqlegance.logger.DataMasking;
import net.sf.jkniv.sqlegance.logger.SimpleDataMasking;

public class LoggerFactory
{
    private static final Logger  LOG = org.slf4j.LoggerFactory.getLogger("jkniv.whinstone.jpa.SQL");
    private static final DataMasking masking = new SimpleDataMasking();
    
    public static Logger getLogger()
    {
        return LOG;
    }
    
    public static DataMasking getDataMasking()
    {
        return masking;
    }
}
