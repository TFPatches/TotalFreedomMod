package me.totalfreedom.totalfreedommod;

import java.text.DecimalFormat;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.projectiles.ProjectileSource;

public class Monitors extends FreedomService
{

    private final DecimalFormat decimalFormat = new DecimalFormat("#");

    public Monitors(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLingeringPotionSplash(LingeringPotionSplashEvent event)
    {
        ProjectileSource source = event.getEntity().getShooter();

        if (!(source instanceof Player))
        {
            return;
        }
        Player player = (Player)source;

        if (plugin.al.isAdmin((Player)event.getEntity().getShooter()))
        {
            return;
        }
        final Material droppedItem = event.getEntity().getItem().getType();
        final Location location = player.getLocation();

        for (Player p : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(p) && plugin.al.getAdmin(p).getPotionSpy())
            {
                FUtil.playerMsg(p, ChatColor.GRAY + player.getName() + ": " + droppedItem + ", X: " + decimalFormat.format(location.getX()) + ", Y: " + decimalFormat.format(location.getY()) + ", Z: " + decimalFormat.format(location.getZ()) + ", World: " + location.getWorld().getName());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPotionSplash(PotionSplashEvent event)
    {
        ProjectileSource source = event.getEntity().getShooter();

        if (!(source instanceof Player))
        {
            return;
        }
        Player player = (Player)source;

        if (plugin.al.isAdmin((Player)event.getEntity().getShooter()))
        {
            return;
        }
        final Material droppedItem = event.getPotion().getItem().getType();
        final Location location = player.getLocation();

        for (Player p : server.getOnlinePlayers())
        {
            if (plugin.al.isAdmin(p) && plugin.al.getAdmin(p).getPotionSpy())
            {
                FUtil.playerMsg(p, ChatColor.GRAY + player.getName() + ": " + droppedItem + ", X: " + decimalFormat.format(location.getX()) + ", Y: " + decimalFormat.format(location.getY()) + ", Z: " + decimalFormat.format(location.getZ()) + ", World: " + location.getWorld().getName());
            }
        }
    }
}