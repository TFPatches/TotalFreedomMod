package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.discord.Discord;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.staff.StaffMember;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Manage your admin entry. Seniors can also manage the entries of other admins.", usage = "/<command> [-o <admin name>] <clearips | clearip <ip> | setscformat <format> | clearscformat> | syncroles>")
public class Command_myadmin extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        Player init = null;
        StaffMember target = getStaffMember(playerSender);
        Player targetPlayer = playerSender;

        // -o switch
        if (args[0].equals("-o"))
        {
            checkRank(Rank.SENIOR_ADMIN);
            init = playerSender;
            targetPlayer = getPlayer(args[1]);
            if (targetPlayer == null)
            {
                msg(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }

            target = getStaffMember(targetPlayer);
            if (target == null)
            {
                msg("That player is not a staff member.", ChatColor.RED);
                return true;
            }

            // Shift 2
            args = Arrays.copyOfRange(args, 2, args.length);
            if (args.length < 1)
            {
                return false;
            }
        }

        final String targetIp = FUtil.getIp(targetPlayer);

        switch (args[0])
        {
            case "clearips":
            {
                if (args.length != 1)
                {
                    return false; // Double check: the player might mean "clearip"
                }

                if (init == null)
                {
                    FUtil.staffAction(sender.getName(), "Clearing my IPs.", true);
                }
                else
                {
                    FUtil.staffAction(sender.getName(), "Clearing " + target.getName() + "'s IPs.", true);
                }

                int counter = target.getIps().size() - 1;
                target.clearIPs();
                target.addIp(targetIp);

                plugin.sl.save(target);
                plugin.sl.updateTables();
                plugin.pl.syncIps(target);

                msg(counter + " IPs removed.");
                msg(targetPlayer, target.getIps().get(0) + " is now your only IP address.");
                return true;
            }

            case "clearip":
            {
                if (args.length != 2)
                {
                    return false; // Double check: the player might mean "clearips"
                }

                if (!target.getIps().contains(args[1]))
                {
                    if (init == null)
                    {
                        msg("That IP is not registered to you.");
                    }
                    else
                    {
                        msg("That IP does not belong to that player.");
                    }
                    return true;
                }

                if (targetIp.equals(args[1]))
                {
                    if (init == null)
                    {
                        msg("You cannot remove your current IP.");
                    }
                    else
                    {
                        msg("You cannot remove that staff members current IP.");
                    }
                    return true;
                }

                FUtil.staffAction(sender.getName(), "Removing an IP" + (init == null ? "" : " from " + targetPlayer.getName() + "'s IPs."), true);

                target.removeIp(args[1]);
                plugin.sl.save(target);
                plugin.sl.updateTables();

                plugin.pl.syncIps(target);

                msg("Removed IP " + args[1]);
                msg("Current IPs: " + StringUtils.join(target.getIps(), ", "));
                return true;
            }

            case "setscformat":
            {
                String format = StringUtils.join(args, " ", 1, args.length);
                target.setAcFormat(format);
                plugin.sl.save(target);
                plugin.sl.updateTables();
                msg("Set staff chat format to \"" + format + "\".", ChatColor.GRAY);
                String example = format.replace("%name%", "ExampleStaff").replace("%rank%", Rank.ADMIN.getAbbr()).replace("%rankcolor%", Rank.ADMIN.getColor().toString()).replace("%msg%", "The quick brown fox jumps over the lazy dog.");
                msg(ChatColor.GRAY + "Example: " + FUtil.colorize(example));
                return true;
            }

            case "clearscformat":
            {
                target.setAcFormat(null);
                plugin.sl.save(target);
                plugin.sl.updateTables();
                msg("Cleared staff chat format.", ChatColor.GRAY);
                return true;
            }

            case "syncroles":
            {
                if (plugin.dc.enabled)
                {
                    if (!ConfigEntry.DISCORD_ROLE_SYNC.getBoolean())
                    {
                        msg("Role syncing is not enabled.", ChatColor.RED);
                        return true;
                    }
                    PlayerData playerData = plugin.pl.getData(target.getName());
                    if (playerData.getDiscordID() == null)
                    {
                        msg("Please run /linkdiscord first!", ChatColor.RED);
                        return true;
                    }
                    boolean synced = Discord.syncRoles(target, playerData.getDiscordID());
                    if (synced)
                    {
                        msg("Successfully synced your roles.", ChatColor.GREEN);
                    }
                    else
                    {
                        msg("Failed to sync your roles, please check the console.", ChatColor.RED);
                    }
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
        if (!plugin.sl.isStaff(sender))
        {
            return Collections.emptyList();
        }

        List<String> singleArguments = Arrays.asList("clearips", "setscformat");
        List<String> doubleArguments = Arrays.asList("clearip", "clearscformat", "syncroles");
        if (args.length == 1)
        {
            List<String> options = new ArrayList<>();
            options.add("-o");
            options.addAll(singleArguments);
            options.addAll(doubleArguments);
            return options;
        }
        else if (args.length == 2)
        {
            if (args[0].equals("-o"))
            {
                return FUtil.getPlayerList();
            }
            else
            {
                if (doubleArguments.contains(args[0]))
                {
                    if (args[0].equals("clearip"))
                    {
                        List<String> ips = plugin.sl.getAdmin(sender).getIps();
                        ips.remove(FUtil.getIp((Player)sender));
                        return ips;
                    }
                }
            }
        }
        else if (args.length == 3)
        {
            if (args[0].equals("-o"))
            {
                List<String> options = new ArrayList<>();
                options.addAll(singleArguments);
                options.addAll(doubleArguments);
                return options;
            }
        }
        else if (args.length == 4)
        {
            if (args[0].equals("-o") && args[2].equals("clearip"))
            {
                StaffMember staffMember = plugin.sl.getEntryByName(args[1]);
                if (staffMember != null)
                {
                    return staffMember.getIps();
                }
            }
        }
        return FUtil.getPlayerList();
    }
}
