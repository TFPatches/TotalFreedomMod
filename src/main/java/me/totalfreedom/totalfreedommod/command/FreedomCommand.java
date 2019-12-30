package me.totalfreedom.totalfreedommod.command;

import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Displayable;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.spigotmc.SpigotConfig;

public abstract class FreedomCommand
{
    public final TotalFreedomMod plugin = TotalFreedomMod.getPlugin();
    public final Server server = plugin.getServer();

    public static final String UNKNOWN_COMMAND = ChatColor.WHITE + SpigotConfig.unknownCommandMessage;
    public static final String YOU_ARE_OP = ChatColor.YELLOW + "You are now op!";
    public static final String YOU_ARE_NOT_OP = ChatColor.YELLOW + "You are no longer op!";
    public static final String PLAYER_NOT_FOUND = ChatColor.GRAY + "Player not found!";

    public static final String COMMAND_PREFIX = "Command_";

    public static final String PERMISSION_MESSAGE = ChatColor.RED + "You do not have permission to use this command.";
    public static final String ONLY_CONSOLE_MESSAGE = ChatColor.RED + "This command can only be used from the console.";
    public static final String ONLY_PLAYER_MESSAGE = ChatColor.RED + "This command can only be used by players.";

    private static CommandMap cmap;
    private final String name;
    private final String description;
    private final String usage;
    private final String aliases;
    private final Rank rank;
    private final SourceType source;

    private final CommandParameters params;
    @Getter
    private final CommandPermissions perms;

    // Command Variables
    private CommandSender sender;
    private Player playerSender;
    private Command command;
    private String label;
    private String[] args;

    // Cooldown
    private Map<CommandSender, FCommand> commandCooldown = new HashMap<>();
    public final Timer timer = new Timer();

    public FreedomCommand()
    {
        params = getClass().getAnnotation(CommandParameters.class);
        perms = getClass().getAnnotation(CommandPermissions.class);
        this.name = getClass().getSimpleName().replace(COMMAND_PREFIX, "");
        this.description = params.description();
        this.usage = params.usage();
        this.aliases = params.aliases();
        this.rank = perms.level();
        this.source = perms.source();
    }

    public void register()
    {
        FCommand cmd = new FCommand(this.name);
        if (this.aliases != null) cmd.setAliases(Arrays.asList(StringUtils.split(this.aliases, ",")));
        if (this.description != null) cmd.setDescription(this.description);
        if (this.usage != null) cmd.setUsage(this.usage);
        getCommandMap().register("", cmd);
        cmd.setExecutor(this);
    }

    public static final CommandMap getCommandMap()
    {
        if (cmap == null)
        {
            try
            {
                final Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                f.setAccessible(true);
                cmap = (CommandMap) f.get(Bukkit.getServer());
                return getCommandMap();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if (cmap != null)
        {
            return cmap;
        }
        return getCommandMap();
    }

    private final class FCommand extends Command
    {
        private FreedomCommand cmd = null;
        private FCommand(String command)
        {
            super(command);
        }
        public void setExecutor(FreedomCommand cmd)
        {
            this.cmd = cmd;
        }
        public boolean execute(CommandSender sender, String label, String[] args)
        {
            if (cmd != null)
            {
                cmd.sender = sender;
                cmd.command = this;
                cmd.label = label;
                cmd.args = args;
                cmd.playerSender = sender instanceof Player ? (Player) sender : null;

                if (perms.source() == SourceType.ONLY_IN_GAME && sender instanceof ConsoleCommandSender)
                {
                    sender.sendMessage(ONLY_PLAYER_MESSAGE);
                    return true;
                }

                if (perms.source() == SourceType.ONLY_CONSOLE && sender instanceof Player)
                {
                    sender.sendMessage(ONLY_CONSOLE_MESSAGE);
                    return true;
                }

                if (!getRank(sender).isAtLeast(cmd.rank))
                {
                    sender.sendMessage(PERMISSION_MESSAGE);
                    return true;
                }

                if (isOnCooldown(sender))
                {
                    return true;
                }

                boolean run = cmd.run(sender, playerSender, this, label, args, sender instanceof ConsoleCommandSender);
                CommandPermissions perms = cmd.perms;
                if (perms.cooldown() > 0 && !plugin.al.isAdmin(sender))
                {
                    commandCooldown.put(sender, this);
                    timer.schedule(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            commandCooldown.remove(sender);
                        }
                    }, perms.cooldown() * 1000);
                }

                try
                {
                    return run;
                }
                catch (CommandFailException ex)
                {
                    msg(ex.getMessage());
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        {
            if (cmd != null)
            {
                return cmd.onTabComplete(sender, this, alias, args);
            }
            return null;
        }
    }

    protected abstract boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole);

    protected List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        List<String> options = getTabCompleteOptions(sender, command, alias, args);
        if (options == null) {
            return null;
        }
        return StringUtil.copyPartialMatches(args[args.length - 1], options, Lists.<String>newArrayList());
    }

    protected Rank getRank(CommandSender sender)
    {
        if (isConsole())
        {
            return ConfigEntry.ADMINLIST_CONSOLE_IS_SENIOR.getBoolean() ? Rank.SENIOR_CONSOLE : Rank.TELNET_CONSOLE;
        }
        return plugin.rm.getRank((Player) sender);
    }

    public Displayable getDisplay(CommandSender sender)
    {
        if (isConsole())
        {
            return ConfigEntry.ADMINLIST_CONSOLE_IS_SENIOR.getBoolean() ? Rank.SENIOR_CONSOLE : Rank.TELNET_CONSOLE;
        }
        return plugin.rm.getDisplay((Player) sender);
    }

    protected void checkConsole()
    {
        if (!isConsole())
        {
            throw new CommandFailException(ONLY_CONSOLE_MESSAGE);
        }
    }

    protected void checkPlayer()
    {
        if (isConsole())
        {
            throw new CommandFailException(ONLY_PLAYER_MESSAGE);
        }
    }

    protected void checkNotHostConsole()
    {
        if (isConsole() && FUtil.isFromHostConsole(sender.getName()))
        {
            throw new CommandFailException("This command can not be used from the host console.");
        }
    }

    protected void checkRank(Rank rank)
    {
        if (!plugin.rm.getRank(sender).isAtLeast(rank))
        {
            noPerms();
        }
    }

    protected boolean noPerms()
    {
        throw new CommandFailException(PERMISSION_MESSAGE);
    }

    protected boolean isConsole()
    {
        return !(sender instanceof Player);
    }

    protected Player getPlayer(String name)
    {
        return Bukkit.getPlayer(name);
    }

    protected void msg(final CommandSender sender, final String message, final ChatColor color)
    {
        if (sender == null)
        {
            return;
        }
        sender.sendMessage(color + message);
    }

    protected void msg(final String message, final ChatColor color)
    {
        msg(sender, message, color);
    }

    protected void msg(final CommandSender sender, final String message)
    {
        msg(sender, message, ChatColor.GRAY);
    }

    protected void msg(final String message)
    {
        msg(sender, message);
    }

    protected boolean isAdmin(CommandSender sender)
    {
        return plugin.al.isAdmin(sender);
    }

    protected Admin getAdmin(CommandSender sender)
    {
        return plugin.al.getAdmin(sender);
    }

    protected Admin getAdmin(Player player)
    {
        return plugin.al.getAdmin(player);
    }

    protected PlayerData getData(Player player)
    {
        return plugin.pl.getData(player);
    }

    public boolean isOnCooldown(CommandSender sender)
    {
        final FCommand command = new FCommand(this.name);
        if (commandCooldown.containsKey(sender) && commandCooldown.containsValue(command))
        {
            sender.sendMessage(ChatColor.RED + "You're on cooldown for this command.");
            return true;
        }
        return false;
    }

    public static FreedomCommand getFrom(Command command)
    {
        try
        {
            return (FreedomCommand) (((PluginCommand) command).getExecutor());
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}