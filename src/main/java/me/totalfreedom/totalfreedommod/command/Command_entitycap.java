package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.apache.commons.lang3.StringUtils;
import java.lang.Integer;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Sets the maximum allowed number of entites until they are removed.", usage = "/<command> <integer>", aliases = "ecap")
public class Command_entitycap extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            FUtil.adminAction(sender.getName(), "Resetting the entity limit" , true); 
            plugin.ew.setEntityCap(400); 
            msg("Successfully reset the entity cap.");
            return true;
        }

        if (StringUtils.isNumeric(args[0]))
        {
            FUtil.adminAction(sender.getName(), "Setting entity limit to " + Integer.parseInt(args[0]), true);
            int removed = plugin.ew.setEntityCap(Integer.parseInt(args[0]));
            msg("Successfully set the entity cap.");
        } else
        {
            return false;
        }
        return true;
    }
}
