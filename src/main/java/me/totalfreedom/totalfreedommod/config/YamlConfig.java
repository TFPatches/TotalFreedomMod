package me.totalfreedom.totalfreedommod.config;

import java.io.File;
import java.io.IOException;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlConfig extends YamlConfiguration
{

    private final TotalFreedomMod plugin;
    private final File file;

    public YamlConfig(TotalFreedomMod plugin, String name)
    {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), name);
        if (!file.exists())
        {
            options().copyDefaults(true);
            save();
        }
        load();
    }

    public YamlConfig(TotalFreedomMod plugin, File file)
    {
        this.plugin = plugin;
        this.file = file;
        if (!file.exists())
        {
            options().copyDefaults(true);
            save();
        }
        load();
    }

    public void load()
    {
        try
        {
            super.load(file);
        }
        catch (InvalidConfigurationException | IOException e)
        {
            e.printStackTrace();
        }
    }

    public void save()
    {
        try
        {
            super.save(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void clear()
    {
        for (String key : super.getKeys(false))
        {
            super.set(key, null);
        }
    }
}