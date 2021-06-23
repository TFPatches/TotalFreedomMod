package me.totalfreedom.totalfreedommod.fun;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.SplittableRandom;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.util.Groups;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public class Trailer extends FreedomService
{
    private final SplittableRandom random = new SplittableRandom();
    private final Set<String> trailPlayers = new HashSet<>(); // player name

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if (trailPlayers.isEmpty())
        {
            return;
        }

        if (!trailPlayers.contains(event.getPlayer().getName()))
        {
            return;
        }

        if (event.getPlayer().getWorld().equals(plugin.wm.masterBuilderWorld.getWorld()))
        {
            return;
        }

        Block fromBlock = event.getFrom().getBlock();
        if (!fromBlock.isEmpty())
        {
            return;
        }

        Block toBlock = Objects.requireNonNull(event.getTo()).getBlock();
        if (fromBlock.equals(toBlock))
        {
            return;
        }

        fromBlock.setType(Groups.WOOL_COLORS.get(random.nextInt(Groups.WOOL_COLORS.size())));
        BlockData data = fromBlock.getBlockData();
        Material material = Material.getMaterial(String.valueOf(fromBlock.getType()));
        for (int x = -1; x <= 1; x++)
        {
            for (int z = -1; z <= 1; z++)
            {
                final Location trail_pos;
                trail_pos = new Location(event.getPlayer().getWorld(), fromBlock.getX() + x, fromBlock.getY(), fromBlock.getZ() + z);
                if (trailPlayers.contains(event.getPlayer().getName()) && plugin.cpb.isEnabled())
                {
                    plugin.cpb.getCoreProtectAPI().logPlacement(event.getPlayer().getName(), trail_pos, material, data);
                }
            }
        }
    }

    public void remove(Player player)
    {
        trailPlayers.remove(player.getName());
    }

    public void add(Player player)
    {
        trailPlayers.add(player.getName());
    }

    public boolean contains(Player player)
    {
        return trailPlayers.contains(player.getName());
    }
}
