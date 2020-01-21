package me.totalfreedom.totalfreedommod;

import java.util.Arrays;
import java.util.List;
import me.totalfreedom.totalfreedommod.util.Groups;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class EntityWiper extends FreedomService
{
    private BukkitTask wiper;

    public EntityWiper(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    public List<EntityType> BLACKLIST = Arrays.asList(
            EntityType.ARMOR_STAND,
            EntityType.PAINTING,
            EntityType.BOAT,
            EntityType.PLAYER,
            EntityType.LEASH_HITCH,
            EntityType.ITEM_FRAME
    );

    @Override
    protected void onStart()
    {
        // Continuous Entity Wiper
        wiper = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                wipe(true);
            }
        }.runTaskTimer(plugin, 1L, 300 * 5); // 5 minutes
    }

    @Override
    protected void onStop()
    {
        wiper.cancel();
        wiper = null;
    }

    // Methods for wiping

    public int wipe(boolean isScheduled)
    {
        int removed = 0;
        for (World world : Bukkit.getWorlds())
        {
            for (Entity entity : world.getEntities())
            {
                EntityType type = entity.getType();
                if (!BLACKLIST.contains(type) && !Groups.MOB_TYPES.contains(type)
                        // Don't kill bees on scheduled wipes (#171) #SaveTheBees
                        && !(isScheduled && type == EntityType.BEE))
                {
                    entity.remove();
                    removed++;
                }
            }
        }
        return removed;
    }
}