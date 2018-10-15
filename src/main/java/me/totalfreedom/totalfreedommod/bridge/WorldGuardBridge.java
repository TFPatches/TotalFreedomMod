package me.totalfreedom.totalfreedommod.bridge;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.Map;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class WorldGuardBridge extends FreedomService
{
    private WorldGuardPlugin worldGuardPlugin;

    public WorldGuardBridge(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }

    public WorldGuardPlugin getWorldGuardPlugin()
    {
        if (worldGuardPlugin == null)
        {
            try
            {
                final Plugin worldGuard = server.getPluginManager().getPlugin("WorldGuard");
                if (worldGuard != null && worldGuard instanceof WorldGuardPlugin)
                {
                    worldGuardPlugin = (WorldGuardPlugin)worldGuard;
                }
            }
            catch (Exception ex)
            {
                FLog.severe(ex);
            }
        }

        return worldGuardPlugin;
    }

    public Boolean wipeRegions(World world)
    {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
        RegionManager rm = container.get(weWorld);
        if (rm != null)
        {
            if (rm.getRegions().isEmpty())
            {
                return false;
            }
            Map<String, ProtectedRegion> regions = rm.getRegions();
            for (ProtectedRegion region : regions.values())
            {
                rm.removeRegion(region.getId());
            }
            return true;
        }
        return true;
    }


    public boolean isEnabled()
    {
        final WorldGuardPlugin wg = getWorldGuardPlugin();

        return wg != null && wg.isEnabled();
    }
}