package net.sf.jkniv.sqlegance.io;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.sqlegance.SqlContext;
import net.sf.jkniv.sqlegance.builder.SqlContextFactory;

public class ReloadableFileTest
{

    @Test @Ignore("Sleeping too long for test")
    public void whenContinuousExecution() throws InterruptedException
    {
        BeeperControl beep = new BeeperControl();
        beep.beepContinuos();
        Thread.sleep(TimeUnit.SECONDS.toMillis(30));
        System.out.println("["+new Date()+"] END");
    }
    
    @Test @Ignore("Sleeping too long for test")
    public void testReload() throws InterruptedException
    {
        SqlContext context = SqlContextFactory.newInstance("/repository-sql.xml");
        Thread.sleep(TimeUnit.MINUTES.toMillis(3L));
    }

}
