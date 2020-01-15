package me.totalfreedom.totalfreedommod;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntityWiper extends FreedomService
{   
    List<Integer> entitycap = new ArrayList<Integer>();
    private BukkitTask wiper;

    public EntityWiper(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {   
        entitycap.add(400);
        wiper = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (World world : Bukkit.getWorlds())
                {
                    if (world.getEntities().size() > entitycap.get(0))
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
        entitycap.removeAll(entitycap);
        entitycap.add(integer);
        return integer;
    }
}
