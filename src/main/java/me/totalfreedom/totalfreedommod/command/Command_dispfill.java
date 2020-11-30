package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Fill nearby dispensers with a set of items of your choice, seperated by commas.", usage = "/<command> <radius> <item, item, item ...>")
public class Command_dispfill extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 2)
        {
            int radius;

            try
            {
                radius = Math.max(5, Math.min(25, Integer.parseInt(args[0])));
            }
            catch (NumberFormatException ex)
            {
                sender.sendMessage("Invalid radius.");
                return true;
            }

            final List<ItemStack> items = new ArrayList<>();

            final String[] itemsRaw = StringUtils.split(args[1], ",");
            for (final String searchItem : itemsRaw)
            {
                Material material = Material.matchMaterial(searchItem);

                if (material != null)
                {
                    items.add(new ItemStack(material, 64));
                }
                else
                {
                    sender.sendMessage("Skipping invalid item: " + searchItem + ".");
                }
            }

            final ItemStack[] itemsArray = items.toArray(new ItemStack[items.size()]);

            int affected = 0;
            final Location centerLocation = playerSender.getLocation();
            final Block centerBlock = centerLocation.getBlock();
            for (int xOffset = -radius; xOffset <= radius; xOffset++)
            {
                for (int yOffset = -radius; yOffset <= radius; yOffset++)
                {
                    for (int zOffset = -radius; zOffset <= radius; zOffset++)
                    {
                        final Block targetBlock = centerBlock.getRelative(xOffset, yOffset, zOffset);
                        if (targetBlock.getLocation().distanceSquared(centerLocation) < (radius * radius))
                        {
                            if (targetBlock.getType().equals(Material.DISPENSER))
                            {
                                sender.sendMessage("Filling dispenser @ " + FUtil.formatLocation(targetBlock.getLocation()) + ".");
                                setDispenserContents(targetBlock, itemsArray);
                                affected++;
                            }
                        }
                    }
                }
            }

            sender.sendMessage("Done. " + affected + " dispenser(s) filled.");
        }
        else
        {
            return false;
        }

        return true;
    }

    private static void setDispenserContents(final Block targetBlock, final ItemStack[] items)
    {
        if (targetBlock.getType() == Material.DISPENSER)
        {
            final Inventory dispenserInv = ((Dispenser)targetBlock.getState()).getInventory();
            dispenserInv.clear();
            dispenserInv.addItem(items);
        }
    }
}
