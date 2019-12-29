package me.totalfreedom.totalfreedommod;

import java.util.logging.Logger;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.Server;
import org.bukkit.event.Listener;

public abstract class FreedomService implements Listener
{
    protected final TotalFreedomMod plugin = TotalFreedomMod.getPlugin();
    protected final Server server = plugin.getServer();
    protected final Logger logger = FLog.getServerLogger();

    public FreedomService()
    {
        server.getPluginManager().registerEvents(this, plugin);
        start();
    }

    public abstract void start();
    public abstract void stop();
}
