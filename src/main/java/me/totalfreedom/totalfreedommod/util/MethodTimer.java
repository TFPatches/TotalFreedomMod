package me.totalfreedom.totalfreedommod.util;

import java.time.Instant;

public class MethodTimer
{

    private long lastStart;
    private long total = 0;

    public MethodTimer()
    {
    }

    public void start()
    {
        this.lastStart = Instant.now().getEpochSecond();
    }

    public void update()
    {
        this.total += (Instant.now().getEpochSecond() - this.lastStart);
    }

    public long getTotal()
    {
        return this.total;
    }

    public void printTotalToLog(String timerName)
    {
        FLog.info("DEBUG: " + timerName + " used " + this.getTotal() + " ms.");
    }
}
