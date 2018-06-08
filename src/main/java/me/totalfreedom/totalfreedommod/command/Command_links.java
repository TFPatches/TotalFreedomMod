package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Get social media links.", usage = "/<command>", aliases = "link")
public class Command_links extends FreedomCommand {
    @Override
    protected boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("social_links");
        if (section != null) {
            Map<String, Object> values = section.getValues(false);

            List<String> lines = new ArrayList<>();

            for (String key : values.keySet()) {
                if (!(values.get(key) instanceof String)) {
                    continue;
                }
                String link = (String) values.get(key);
                lines.add(ChatColor.GOLD + "- " + key + ": " + ChatColor.AQUA + link);
            }

            sender.sendMessage(ChatColor.AQUA + "Social Media Links:");
            sender.sendMessage(lines.toArray(new String[0]));
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "There are no links added in the configuration file.");
        }
        return true;
    }
}