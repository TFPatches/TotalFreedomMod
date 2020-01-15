package me.totalfreedom.totalfreedommod;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;

public class EntityWiper extends FreedomService
{   
    private BukkitTask wiper;

    public EntityWiper(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {   
        wiper = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (World world : Bukkit.getWorlds())
                {
                    if (world.getEntities().size() > ConfigEntry.ENTITY_LIMIT.getInteger())
                    {
                        wipe(world);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 5);
    }

    @Override
    protected void onStop()
    {
        wiper.cancel();
        wiper = null;
    }

    // Methods for wiping

    public int wipe()
    {
        int removed = 0;
        for (World world : Bukkit.getWorlds())
        {
            for (Entity entity : world.getEntities())
            {
                if (!(entity instanceof Player))
                {
                    entity.remove();
                    removed++;
                }
            }
        }
        return removed;
    }

    public int wipe(World world)
    {
        int removed = 0;
        for (Entity entity : world.getEntities())
        {
            if (!(entity instanceof Player))
            {
                entity.remove();
                removed++;
            }
        }
        return removed;
    }

    public int setEntityCap(int integer)
    {
        ConfigEntry.ENTITY_LIMIT.setInteger(integer);
        return integer;
    }
}
