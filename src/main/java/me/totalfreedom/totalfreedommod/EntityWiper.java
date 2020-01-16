package me.totalfreedom.totalfreedommod;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntitySpawnEvent;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;

public class EntityWiper extends FreedomService
{   

    public EntityWiper(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {   
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntitySpawn(EntitySpawnEvent event)
    {
        for (World world : Bukkit.getWorlds())
        {
            if (world.getEntities().size() > ConfigEntry.ENTITY_LIMIT.getInteger())
            {
                event.setCancelled(true);
                wipe(world);
            }
        }
    }

    @Override
    protected void onStop()
    {
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
