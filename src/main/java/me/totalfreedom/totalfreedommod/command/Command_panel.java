package me.totalfreedom.totalfreedommod.command;

import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.staff.StaffMember;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Manage your Pterodactyl panel account.", usage = "/<command> <create | delete>")
public class Command_panel extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (!plugin.ptero.isEnabled())
        {
            msg("Pterodactyl integration is currently disabled.", ChatColor.RED);
            return true;
        }

        PlayerData playerData = getData(playerSender);

        if (playerData.getDiscordID() == null)
        {
            msg("You must have a linked discord account.", ChatColor.RED);
            return true;
        }

        if (args.length == 0)
        {
            return false;
        }

        if (args[0].equals("create"))
        {
            msg("Creating your Pterodactyl account...", ChatColor.GREEN);
            StaffMember staffMember = getStaffMember(playerSender);

            if (staffMember.getPteroID() != null)
            {
                msg("You already have a Pterodactyl account.", ChatColor.RED);
                return true;
            }

            String username = sender.getName();
            String password = FUtil.randomString(30);

            String id = plugin.ptero.createAccount(username, password);
            if (Strings.isNullOrEmpty(id))
            {
                msg("Failed to create your Pterodactyl account.", ChatColor.RED);
                return true;
            }

            plugin.ptero.addAccountToServer(id);

            staffMember.setPteroID(id);
            plugin.sl.save(staffMember);
            plugin.sl.updateTables();

            plugin.dc.sendPteroInfo(playerData, username, password);
            msg("Successfully created your Pterodactyl account. Check your DMs from " + plugin.dc.formatBotTag() + " on discord to get your credentials.", ChatColor.GREEN);
            return true;
        }
        else if (args[0].equals("delete"))
        {
            msg("Deleting your Pterodactyl account...", ChatColor.GREEN);
            StaffMember staffMember = getStaffMember(playerSender);

            if (staffMember.getPteroID() == null)
            {
                msg("You do not have a Pterodactyl account.", ChatColor.RED);
                return true;
            }

            boolean deleted = plugin.ptero.deleteAccount(staffMember.getPteroID());

            if (!deleted)
            {
                msg("Failed to delete your Pterodactyl account.", ChatColor.RED);
                return true;
            }

            staffMember.setPteroID(null);
            plugin.sl.save(staffMember);
            plugin.sl.updateTables();

            msg("Successfully deleted your Pterodactyl account.", ChatColor.GREEN);
            return true;
        }
        /*else if (args[0].equals("resetpassword"))
        {
            StaffMember staffMember = getAdmin(playerSender);

            if (staffMember.getAmpUsername() == null)
            {
                msg("You do not have a Pterodactyl account.", ChatColor.RED);
                return true;
            }

            msg("Resetting your password...", ChatColor.GREEN);

            String id = staffMember.getPteroID();
            String password = FUtil.randomString(30);
            plugin.ptero.setPassword(id, password);
            plugin.dc.sendPteroInfo(playerData, null, password);

            msg("Successfully reset your AMP account password. Check your DMs from " + plugin.dc.formatBotTag() + " on discord to get your credentials.", ChatColor.GREEN);
            return true;
        }*/

        return false;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1 && plugin.sl.isAdmin(sender))
        {
            return Arrays.asList("create", "delete");
        }

        return Collections.emptyList();
    }

}
