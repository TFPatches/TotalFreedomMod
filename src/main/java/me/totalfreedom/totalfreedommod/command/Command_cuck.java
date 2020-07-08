package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Cuck someone - sends an unclearable title to the specified player.", usage = "/<command> <player>")
public class Command_cuck extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            msg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        ((CraftWorld)player.getWorld()).getHandle().removePlayer(((CraftPlayer)player).getHandle()); // Bypass Paper and remove the player entity directly in the NMS World.
        msg("Cucked " + player.getName());
        player.sendTitle(ChatColor.DARK_RED + "HAHA CUCKED", ChatColor.RED + "relog if u want to be uncucked loser", 20, 200, 60);
        return true;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1 && plugin.al.isAdmin(sender))
        {
            return FUtil.getPlayerList();
        }
        return Collections.emptyList();
    }
}