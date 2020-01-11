package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Material;
@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Block a player's Minecraft input. This is evil, and I never should have wrote it.", usage = "/<command> <all | purge | <<partialname> on | off>>")
public class Command_lockup extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("all"))
            {
                FUtil.adminAction(sender.getName(), "Locking up all players", true);

                for (Player player : server.getOnlinePlayers())
                {
                    startLockup(player);
                }
                msg("Locked up all players.");
            }
            else if (args[0].equalsIgnoreCase("purge"))
            {
                FUtil.adminAction(sender.getName(), "Unlocking all players", true);
                for (Player player : server.getOnlinePlayers())
                {
                    cancelLockup(player);
                }

                msg("Unlocked all players.");
            }
            else
            {
                return false;
            }
        }
        else if (args.length == 2)
        {
            if (args[1].equalsIgnoreCase("on"))
            {
                final Player player = getPlayer(args[0]);

                if (player == null)
                {
                    sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                    return true;
                }

                FUtil.adminAction(sender.getName(), "Locking up " + player.getName(), true);
                startLockup(player);
                msg("Locked up " + player.getName() + ".");
            }
            else if ("off".equals(args[1]))
            {
                final Player player = getPlayer(args[0]);

                if (player == null)
                {
                    sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                    return true;
                }

                FUtil.adminAction(sender.getName(), "Unlocking " + player.getName(), true);
                cancelLockup(player);
                msg("Unlocked " + player.getName() + ".");
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
        return true;
    }

    private void cancelLockup(FPlayer playerdata)
    {
        BukkitTask lockupScheduleId = playerdata.getLockupScheduleID();
        if (lockupScheduleId != null)
        {
            lockupScheduleId.cancel();
            playerdata.setLockedUp(false);
            playerdata.setInvSee(false);
            playerdata.setLockupScheduleId(null);
        }
    }

    private ItemStack makeItem(Material mat, String name)
    {
        ItemStack lockitem = new ItemStack(mat);
        ItemMeta meta = lockitem.getItemMeta();
        meta.setDisplayName(name);
        lockitem.setItemMeta(meta);
        return lockitem;
    }
    private void cancelLockup(final Player player)
    {
        cancelLockup(plugin.pl.getPlayer(player));
        player.closeInventory();
    }

    private void startLockup(final Player player)
    {
        final FPlayer playerdata = plugin.pl.getPlayer(player);
        cancelLockup(playerdata);
        final Inventory lockinv = server.createInventory(null, 9, ChatColor.GOLD + "Lockup");
        playerdata.setLockedUp(true);
        playerdata.setLockupScheduleId(new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (player.isOnline())
                {   
                    playerdata.setInvSee(true);
                    lockinv.setItem(4, makeItem(Material.REDSTONE_BLOCK, ChatColor.RED + sender.getName() + ChatColor.GOLD + " placed you in lockup."));
                    player.openInventory(lockinv);
                }
                else
                {
                    cancelLockup(playerdata);
                }
            }
        }.runTaskTimer(plugin, 0L, 5L));

    }
    
}
