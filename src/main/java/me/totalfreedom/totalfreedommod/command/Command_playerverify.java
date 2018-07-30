package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.util.Ips;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Manage your verification", usage = "/<command> <<enable | disable | clearips | status> <discord | forum> | settag <tag> | cleartag>", aliases = "playerverification,pv")
public class Command_playerverify extends FreedomCommand
{
    @Override
    protected boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        VPlayer target = plugin.pv.getVerificationPlayer(playerSender);

        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("clearips"))
            {
                int cleared = 0;
                for (String ip : target.getIps())
                {
                    if (!ip.equals(Ips.getIp(playerSender)))
                    {
                        target.removeIp(ip);
                        cleared++;
                    }
                }

                msg("Cleared all IP's except your current IP \"" + Ips.getIp(playerSender) + "\"");
                msg("Cleared " + cleared + " IP's.");
                plugin.pv.saveVerificationData(target);
                return true;
            }
        }

        if (args.length < 1)
        {
            return false;
        }

        if (plugin.al.isAdmin(sender))
        {
            msg("This command is only for OP's.", ChatColor.RED);
            return true;
        }

        switch (args[0])
        {
            case "cleartag":
            {
                msg("Cleared personal default tag");
                target.setTag(null);
                plugin.pv.saveVerificationData(target);
                return true;
            }
        }

        if (args.length < 2)
        {
            return false;
        }

        switch (args[0].toLowerCase())
        {
            case "enable":
                switch (args[1].toLowerCase())
                {
                    case "discord":
                        if (!plugin.dc.enabled)
                        {
                            msg("The Discord verification system is currently disabled.", ChatColor.RED);
                            return true;
                        }
                        VPlayer data = plugin.pv.getVerificationPlayer(playerSender);
                        if (data.getDiscordEnabled())
                        {
                            msg("Discord verification is already enabled for you.", ChatColor.RED);
                            return true;
                        }
                        data.setDiscordEnabled(true);
                        plugin.pv.saveVerificationData(data);
                        msg("Enabled Discord verification. Please type /linkdiscord to link a Discord account.", ChatColor.GREEN);
                        return true;
                    case "forum":
                        msg("TODO. This will be enabled in a later update. Please use Discord verification instead.");
                        return true;
                    default:
                        return false;
                }

            case "disable":
                switch (args[1].toLowerCase())
                {
                    case "discord":
                        VPlayer data = plugin.pv.getVerificationPlayer(playerSender);
                        if (!data.getDiscordEnabled())
                        {
                            msg("Discord verification is already disabled for you.", ChatColor.RED);
                            return true;
                        }
                        data.setDiscordEnabled(false);
                        plugin.pv.saveVerificationData(data);
                        msg("Disabled Discord verification.", ChatColor.GREEN);
                        return true;
                    case "forum":
                        msg("TODO. Forum verification will be enabled in a later update.");
                        return true;
                    default:
                        return false;
                }

            case "status":
                switch (args[1].toLowerCase())
                {
                    case "discord":
                        boolean enabled = target.getDiscordEnabled();
                        boolean specified = target.getDiscordId() != null;
                        msg(ChatColor.GRAY + "Discord Verification Enabled: " + (enabled ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
                        msg(ChatColor.GRAY + "Discord ID: " + (specified ? ChatColor.GREEN + target.getDiscordId() : ChatColor.RED + "not set"));
                        return true;
                    case "forum":
                        msg("TODO. Forum verification will be enabled in a later update.");
                        return true;
                    default:
                        return false;
                }

            case "settag":
            {
                final String inputTag = StringUtils.join(args, " ", 1, args.length);
                final String outputTag = FUtil.colorize(StringUtils.replaceEachRepeatedly(StringUtils.strip(inputTag),
                        new String[]
                                {
                                        "" + ChatColor.COLOR_CHAR, "&k"
                                },
                        new String[]
                                {
                                        "", ""
                                })) + ChatColor.RESET;

                final String rawTag = ChatColor.stripColor(outputTag).toLowerCase();

                if (rawTag.length() > 20)
                {
                    msg("That tag is too long (Max is 20 characters).");
                    return true;
                }

                for (String word : FUtil.FORBIDDEN_WORDS)
                {
                    if (rawTag.contains(word))
                    {
                        msg("That tag contains a forbidden word.");
                        return true;
                    }
                }

                target.setTag(outputTag);
                msg("Your default tag is now: " + outputTag);
                plugin.pv.saveVerificationData(target);
                return true;
            }

            default:
                return false;
        }
    }
}
