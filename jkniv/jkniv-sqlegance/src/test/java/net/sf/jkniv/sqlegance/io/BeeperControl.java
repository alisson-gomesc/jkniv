package net.sf.jkniv.sqlegance.io;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BeeperControl
{
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    public void beepContinuos()
    {
        final Runnable beeper = new Runnable()
        {
            public void run()
            {
                System.out.println("["+new Date()+"] beep");
            }
        };
        final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 0, 5, TimeUnit.SECONDS);
        scheduler.schedule(new Runnable()
        {
            public void run()
            {
                System.out.println("["+new Date()+"] cancel beep");
                beeperHandle.cancel(true);
            }
        }, 20, TimeUnit.SECONDS);
    }
}
