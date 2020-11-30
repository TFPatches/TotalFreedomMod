package me.totalfreedom.totalfreedommod.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "List, add, or remove master builders. Master builders can also clear their own IPs.", usage = "/<command> <list | clearip <ip> | clearips | <add | remove> <username>>")
public class Command_mbconfig extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        switch (args[0])
        {
            case "list":
            {
                msg("Master Builders: " + StringUtils.join(plugin.pl.getMasterBuilderNames(), ", "), ChatColor.GOLD);
                return true;
            }

            case "clearips":
            {
                if (args.length > 1)
                {
                    return false;
                }

                if (senderIsConsole)
                {
                    msg("Only in-game players may use this command.", ChatColor.RED);
                    return true;
                }

                PlayerData data = plugin.pl.getData(sender.getName());
                if (!data.isMasterBuilder())
                {
                    msg("You are not a master builder!", ChatColor.RED);
                    return true;
                }

                int counter = data.getIps().size() - 1;
                data.clearIps();
                data.addIp(FUtil.getIp(playerSender));
                plugin.sql.addPlayer(data);
                msg(counter + " IPs removed.");
                msg(data.getIps().get(0) + " is now your only IP address.");
                FUtil.staffAction(sender.getName(), "Clearing my IPs.", true);
                return true;
            }
            case "clearip":
            {
                if (args.length < 2)
                {
                    return false;
                }

                if (senderIsConsole)
                {
                    msg("Only in-game players may use this command.", ChatColor.RED);
                    return true;
                }

                PlayerData data = plugin.pl.getData(sender.getName());
                final String targetIp = FUtil.getIp(playerSender);

                if (!data.isMasterBuilder())
                {
                    msg("You are not a master builder!", ChatColor.RED);
                    return true;
                }

                if (targetIp.equals(args[1]))
                {
                    msg("You cannot remove your current IP.");
                    return true;
                }
                data.removeIp(args[1]);
                plugin.sql.addPlayer(data);
                msg("Removed IP " + args[1]);
                msg("Current IPs: " + StringUtils.join(data.getIps(), ", "));
                return true;
            }
            case "add":
            {
                if (args.length < 2)
                {
                    return false;
                }

                if (!plugin.pl.canManageMasterBuilders(sender.getName()))
                {
                    return noPerms();
                }

                final Player player = getPlayer(args[1]);

                PlayerData data = player != null ? plugin.pl.getData(player) : plugin.pl.getData(args[1]);

                if (data == null)
                {
                    msg(PLAYER_NOT_FOUND, ChatColor.RED);
                    return true;
                }

                if (data.isMasterBuilder() && plugin.pl.isPlayerImpostor(player))
                {
                    FUtil.staffAction(sender.getName(), "Re-adding " + data.getName() + " to the Master Builder list.", true);

                    if (plugin.pl.getPlayer(player).getFreezeData().isFrozen())
                    {
                        plugin.pl.getPlayer(player).getFreezeData().setFrozen(false);
                    }
                    if (player != null)
                    {
                        plugin.pl.verify(player, null);
                        plugin.rm.updateDisplay(player);
                        player.setOp(true);
                        player.sendMessage(YOU_ARE_OP);
                    }
                }
                else if (!data.isMasterBuilder())
                {
                    FUtil.staffAction(sender.getName(), "Adding " + data.getName() + " to the Master Builder list.", true);
                    data.setMasterBuilder(true);
                    data.setVerification(true);
                    plugin.pl.save(data);
                    if (player != null)
                    {
                        plugin.rm.updateDisplay(player);
                    }
                    return true;
                }
                else
                {
                    msg("That player is already on the Master Builder list.");
                    return true;
                }
            }
            case "remove":
            {
                if (args.length < 2)
                {
                    return false;
                }

                if (!plugin.pl.canManageMasterBuilders(sender.getName()))
                {
                    return noPerms();
                }

                Player player = getPlayer(args[1]);
                PlayerData data = player != null ? plugin.pl.getData(player) : plugin.pl.getData(args[1]);

                if (data == null || !data.isMasterBuilder())
                {
                    msg("Master Builder not found: " + args[1]);
                    return true;
                }

                FUtil.staffAction(sender.getName(), "Removing " + data.getName() + " from the Master Builder list.", true);
                data.setMasterBuilder(false);
                if (data.getDiscordID() == null)
                {
                    data.setVerification(false);
                }
                plugin.pl.save(data);
                if (player != null)
                {
                    plugin.rm.updateDisplay(player);
                }
                return true;
            }
            default:
            {
                return false;
            }
        }
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1)
        {
            return Arrays.asList("add", "remove", "list", "clearips", "clearip");
        }
        else if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("add"))
            {
                return FUtil.getPlayerList();
            }
            else if (args[0].equalsIgnoreCase("remove"))
            {
                return plugin.pl.getMasterBuilderNames();
            }
            else if (args[0].equalsIgnoreCase("clearip"))
            {
                PlayerData data = plugin.pl.getData(sender.getName());
                if (data.isMasterBuilder())
                {
                    return data.getIps();
                }
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }
}
