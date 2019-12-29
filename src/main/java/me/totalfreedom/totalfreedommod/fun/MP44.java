package me.totalfreedom.totalfreedommod.fun;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

public class MP44 extends FreedomService
{

    public MP44()
    {
        super();
    }

    @Override
    public void start()
    {
    }

    @Override
    public void stop()
    {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        plugin.pl.getPlayer(event.getPlayer()).disarmMP44();
    }

}
