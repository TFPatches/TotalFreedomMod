package me.totalfreedom.totalfreedommod.blocking;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class InventoryBlocker extends FreedomService
{
    public InventoryBlocker(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    private BukkitTask inventoryChecker;

    @Override
    protected void onStart()
    {
        inventoryChecker = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    Inventory inv = player.getInventory();
                    for (int i = 0; i < inv.getSize(); i++)
                    {
                        ItemStack item = inv.getItem(i);
                        if (item == null)
                            continue;
                        if (item.getType() == Material.JUKEBOX)
                        {
                            inv.setItem(i, new ItemStack(Material.JUKEBOX));
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1);
    }

    @Override
    protected void onStop()
    {
        inventoryChecker.cancel();
        inventoryChecker = null;
    }
}
