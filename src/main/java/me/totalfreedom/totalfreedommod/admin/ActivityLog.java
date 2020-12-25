package me.totalfreedom.totalfreedommod.admin;

import com.google.common.collect.Maps;
import java.util.Map;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.config.YamlConfig;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ActivityLog extends FreedomService
{

    public static final String FILENAME = "activitylog.yml";

    private final Map<String, ActivityLogEntry> allActivityLogs = Maps.newHashMap();
    private final Map<String, ActivityLogEntry> nameTable = Maps.newHashMap();
    private final Map<String, ActivityLogEntry> ipTable = Maps.newHashMap();

    private final YamlConfig config;

    public ActivityLog()
    {
        this.config = new YamlConfig(plugin, FILENAME, true);
    }

    public static String getFILENAME()
    {
        return FILENAME;
    }

    @Override
    public void onStart()
    {
        load();
    }

    @Override
    public void onStop()
    {
        save();
    }

    public void load()
    {
        config.load();

        allActivityLogs.clear();
        nameTable.clear();
        ipTable.clear();
        for (String key : config.getKeys(false))
        {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null)
            {
                FLog.warning("Invalid activity log format: " + key);
                continue;
            }

            ActivityLogEntry activityLogEntry = new ActivityLogEntry(key);
            activityLogEntry.loadFrom(section);

            if (!activityLogEntry.isValid())
            {
                FLog.warning("Could not load activity log: " + key + ". Missing details!");
                continue;
            }

            allActivityLogs.put(key, activityLogEntry);
        }

        updateTables();
        FLog.info("Loaded " + allActivityLogs.size() + " activity logs");
    }

    public void save()
    {
        // Clear the config
        for (String key : config.getKeys(false))
        {
            config.set(key, null);
        }

        for (ActivityLogEntry activityLog : allActivityLogs.values())
        {
            activityLog.saveTo(config.createSection(activityLog.getConfigKey()));
        }

        config.save();
    }

    public ActivityLogEntry getActivityLog(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            return getActivityLog((Player)sender);
        }

        return getEntryByName(sender.getName());
    }

    public ActivityLogEntry getActivityLog(Player player)
    {
        ActivityLogEntry activityLog = getEntryByName(player.getName());
        if (activityLog == null)
        {
            String ip = FUtil.getIp(player);
            activityLog = getEntryByIp(ip);
            if (activityLog != null)
            {
                // Set the new username
                activityLog.setName(player.getName());
                save();
                updateTables();
            }
            else
            {
                activityLog = new ActivityLogEntry(player);
                allActivityLogs.put(activityLog.getConfigKey(), activityLog);
                updateTables();

                activityLog.saveTo(config.createSection(activityLog.getConfigKey()));
                config.save();
            }
        }
        String ip = FUtil.getIp(player);
        if (!activityLog.getIps().contains(ip))
        {
            activityLog.addIp(ip);
            save();
            updateTables();
        }
        return activityLog;
    }

    public ActivityLogEntry getEntryByName(String name)
    {
        return nameTable.get(name.toLowerCase());
    }

    public ActivityLogEntry getEntryByIp(String ip)
    {
        return ipTable.get(ip);
    }

    public void updateTables()
    {
        nameTable.clear();
        ipTable.clear();

        for (ActivityLogEntry activityLog : allActivityLogs.values())
        {
            nameTable.put(activityLog.getName().toLowerCase(), activityLog);

            for (String ip : activityLog.getIps())
            {
                ipTable.put(ip, activityLog);
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if (plugin.al.isAdmin(player))
        {
            getActivityLog(event.getPlayer()).addLogin();
            plugin.acl.save();
            plugin.acl.updateTables();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if (plugin.al.isAdmin(player))
        {
            getActivityLog(event.getPlayer()).addLogout();
            plugin.acl.save();
            plugin.acl.updateTables();
        }
    }

    public Map<String, ActivityLogEntry> getAllActivityLogs()
    {
        return allActivityLogs;
    }

    public Map<String, ActivityLogEntry> getNameTable()
    {
        return nameTable;
    }

    public Map<String, ActivityLogEntry> getIpTable()
    {
        return ipTable;
    }

    public YamlConfig getConfig()
    {
        return config;
    }
}