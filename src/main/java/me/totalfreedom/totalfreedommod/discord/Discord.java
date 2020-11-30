package me.totalfreedom.totalfreedommod.discord;

import com.google.common.base.Strings;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import javax.security.auth.login.LoginException;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.staff.StaffMember;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.internal.utils.concurrent.CountingThreadFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Discord extends FreedomService
{

    public static HashMap<String, PlayerData> LINK_CODES = new HashMap<>();
    public static HashMap<String, PlayerData> VERIFICATION_CODES = new HashMap<>();
    public ScheduledThreadPoolExecutor RATELIMIT_EXECUTOR;
    public List<CompletableFuture<Message>> sentMessages = new ArrayList<>();

    public static JDA bot = null;
    public Boolean enabled = false;

    public void startBot()
    {
        enabled = !Strings.isNullOrEmpty(ConfigEntry.DISCORD_TOKEN.getString());
        if (!enabled)
        {
            return;
        }
        if (bot != null)
        {
            RATELIMIT_EXECUTOR = new ScheduledThreadPoolExecutor(5, new CountingThreadFactory(this::poolIdentifier, "RateLimit"));
            RATELIMIT_EXECUTOR.setRemoveOnCancelPolicy(true);
            for (Object object : bot.getRegisteredListeners())
            {
                bot.removeEventListener(object);
            }
        }
        try
        {
            bot = JDABuilder.createDefault(ConfigEntry.DISCORD_TOKEN.getString())
                    .addEventListeners(new PrivateMessageListener(),
                            new DiscordToMinecraftListener(),
                            new DiscordToAdminChatListener(),
                            new ListenerAdapter()
                            {
                                @Override
                                public void onReady(ReadyEvent event)
                                {
                                    new StartEvent(event.getJDA()).start();
                                }
                            })
                    .setAutoReconnect(true)
                    .setRateLimitPool(RATELIMIT_EXECUTOR)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .build();
            FLog.info("Discord verification bot has successfully enabled!");
        }
        catch (LoginException e)
        {
            FLog.warning("An invalid token for the discord verification bot, the bot will not enable.");
        }
        catch (IllegalArgumentException e)
        {
            FLog.warning("Discord verification bot failed to start.");
        }
        catch (NoClassDefFoundError e)
        {
            FLog.warning("The JDA plugin is not installed, therefore the discord bot cannot start.");
            FLog.warning("To resolve this error, please download the latest JDA from: https://github.com/TFPatches/Minecraft-JDA/releases");
        }

    }

    public String poolIdentifier()
    {
        return "JDA";
    }

    public void clearQueue()
    {
        for (CompletableFuture<Message> messages : sentMessages)
        {
            if (!messages.isDone())
            {
                messages.cancel(true);
            }
        }
        sentMessages.clear();
        messageChatChannel("**Message queue cleared.**");
    }

    // Do no ask why this is here. I spent two hours trying to make a simple thing work
    // ok
    public class StartEvent
    {
        private final JDA api;

        public StartEvent(JDA api)
        {
            this.api = api;
        }

        public void start()
        {
            messageChatChannel("**Server has started!**");
        }
    }

    public void sendPteroInfo(PlayerData playerData, String username, String password)
    {
        User user = getUser(playerData.getDiscordID());
        String message = "The following are your Pterodactyl details:\n\nUsername: " + username + "\nPassword: " + password + "\n\nYou can connect to the panel at " + plugin.ptero.URL;
        PrivateChannel privateChannel = user.openPrivateChannel().complete();
        privateChannel.sendMessage(message).complete();
    }

    public User getUser(String id)
    {
        Guild guild = bot.getGuildById(ConfigEntry.DISCORD_SERVER_ID.getString());

        if (guild == null)
        {
            FLog.severe("Either the bot is not in the Discord server or it doesn't exist. Check the server ID.");
            return null;
        }

        Member member = guild.getMemberById(id);

        if (member == null)
        {
            return null;
        }

        return member.getUser();
    }

    public boolean sendBackupCodes(PlayerData playerData)
    {
        List<String> codes = generateBackupCodes();
        List<String> encryptedCodes = generateEncryptedBackupCodes(codes);
        User user = getUser(playerData.getDiscordID());
        File file = generateBackupCodesFile(playerData.getName(), codes);
        if (file == null)
        {
            return false;
        }
        PrivateChannel privateChannel = user.openPrivateChannel().complete();
        privateChannel.sendMessage("Do not share these codes with anyone as they can be used to impose as you.").addFile(file).complete();
        playerData.setBackupCodes(encryptedCodes);
        plugin.pl.save(playerData);
        file.delete();
        return true;
    }

    public List<String> generateBackupCodes()
    {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            codes.add(FUtil.randomAlphanumericString(10));
        }
        return codes;
    }

    public String generateCode(int size)
    {
        String code = "";
        Random random = new Random();
        for (int i = 0; i < size; i++)
        {
            code += random.nextInt(10);
        }
        return code;
    }

    public List<String> generateEncryptedBackupCodes(List<String> codes)
    {
        List<String> encryptedCodes = new ArrayList<>();
        for (String code : codes)
        {
            encryptedCodes.add(getMD5(code));
        }
        return encryptedCodes;
    }

    public File generateBackupCodesFile(String name, List<String> codes)
    {
        StringBuilder text = new StringBuilder();
        text.append("Below are your backup codes for use on TotalFreedom in the event you lose access to your discord account.\n")
                .append("Simply pick a code, and run /verify <code> on the server. Each code is one use, so be sure to cross it off once you use it.\n")
                .append("To generate new codes, simply run /generatebackupcodes\n\n");

        for (String code : codes)
        {
            text.append(code + "\n");
        }

        String fileUrl = plugin.getDataFolder().getAbsolutePath() + "/TF-Backup-Codes-" + name + ".txt";
        try
        {
            FileWriter fileWriter = new FileWriter(fileUrl);
            fileWriter.write(text.toString());
            fileWriter.close();
        }
        catch (IOException e)
        {
            FLog.severe("Failed to generate backup codes file: " + e.toString());
            return null;
        }
        return new File(fileUrl);
    }

    public static String getMD5(String string)
    {
        return DigestUtils.md5Hex(string);
    }

    public void addVerificationCode(String code, PlayerData playerData)
    {
        VERIFICATION_CODES.put(code, playerData);
    }

    public void removeVerificationCode(String code)
    {
        VERIFICATION_CODES.remove(code);
    }

    public HashMap<String, PlayerData> getVerificationCodes()
    {
        return VERIFICATION_CODES;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        try
        {
            if (!event.getEntity().getWorld().getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES))
            {
                return;
            }
        }
        catch (NullPointerException e)
        {
        }

        messageChatChannel("**" + deformat(event.getDeathMessage()) + "**");
    }

    @Override
    public void onStart()
    {
        startBot();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        if (!plugin.sl.isVanished(event.getPlayer().getName()))
        {
            messageChatChannel("**" + deformat(event.getPlayer().getName()) + " joined the server." + "**");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        if (!plugin.sl.isVanished(event.getPlayer().getName()))
        {
            messageChatChannel("**" + deformat(event.getPlayer().getName()) + " left the server." + "**");
        }
    }

    public void messageChatChannel(String message)
    {
        String chat_channel_id = ConfigEntry.DISCORD_CHAT_CHANNEL_ID.getString();
        if (message.contains("@everyone") || message.contains("@here"))
        {
            message = StringUtils.remove(message, "@");
        }

        if (message.toLowerCase().contains("discord.gg"))
        {
            return;
        }

        if (enabled && !chat_channel_id.isEmpty())
        {
            CompletableFuture<Message> sentMessage = bot.getTextChannelById(chat_channel_id).sendMessage(message).submit(true);
            sentMessages.add(sentMessage);
        }
    }

    public void messageAdminChatChannel(String message)
    {
        String chat_channel_id = ConfigEntry.DISCORD_ADMINCHAT_CHANNEL_ID.getString();
        if (message.contains("@everyone") || message.contains("@here"))
        {
            message = StringUtils.remove(message, "@");
        }

        if (message.toLowerCase().contains("discord.gg"))
        {
            return;
        }

        if (enabled && !chat_channel_id.isEmpty())
        {
            CompletableFuture<Message> sentMessage = bot.getTextChannelById(chat_channel_id).sendMessage(message).submit(true);
            sentMessages.add(sentMessage);
        }
    }

    public String formatBotTag()
    {
        SelfUser user = bot.getSelfUser();
        return user.getName() + "#" + user.getDiscriminator();
    }

    public static String getCode(PlayerData playerData)
    {
        for (String code : LINK_CODES.keySet())
        {
            if (LINK_CODES.get(code).equals(playerData))
            {
                return code;
            }
        }
        return null;
    }

    @Override
    public void onStop()
    {
        if (bot != null)
        {
            messageChatChannel("**Server has stopped.**");
        }
        FLog.info("Discord verification bot has successfully shutdown.");
    }

    public String deformat(String input)
    {
        return input.replace("_", "\\_");
    }

    public boolean sendReport(Player reporter, Player reported, String reason)
    {
        if (ConfigEntry.DISCORD_REPORT_CHANNEL_ID.getString().isEmpty())
        {
            return false;
        }
        if (ConfigEntry.DISCORD_SERVER_ID.getString().isEmpty())
        {
            FLog.severe("No Discord server ID was specified in the config, but there is a report channel ID.");
            return false;
        }
        Guild server = bot.getGuildById(ConfigEntry.DISCORD_SERVER_ID.getString());
        if (server == null)
        {
            FLog.severe("The Discord server ID specified is invalid, or the bot is not on the server.");
            return false;
        }
        TextChannel channel = server.getTextChannelById(ConfigEntry.DISCORD_REPORT_CHANNEL_ID.getString());
        if (channel == null)
        {
            FLog.severe("The report channel ID specified in the config is invalid.");
            return false;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Report for " + reported.getName());
        embedBuilder.setDescription(reason);
        embedBuilder.setFooter("Reported by " + reporter.getName(), "https://minotar.net/helm/" + reporter.getName() + ".png");
        embedBuilder.setTimestamp(Instant.from(ZonedDateTime.now()));
        String location = "World: " + reported.getLocation().getWorld().getName() + ", X: " + reported.getLocation().getBlockX() + ", Y: " + reported.getLocation().getBlockY() + ", Z: " + reported.getLocation().getBlockZ();
        embedBuilder.addField("Location", location, true);
        embedBuilder.addField("Game Mode", WordUtils.capitalizeFully(reported.getGameMode().name()), true);
        com.earth2me.essentials.User user = plugin.esb.getEssentialsUser(reported.getName());
        embedBuilder.addField("God Mode", WordUtils.capitalizeFully(String.valueOf(user.isGodModeEnabled())), true);
        if (user.getNickname() != null)
        {
            embedBuilder.addField("Nickname", user.getNickname(), true);
        }
        MessageEmbed embed = embedBuilder.build();
        channel.sendMessage(embed).complete();
        return true;
    }

    public static boolean syncRoles(StaffMember staffMember, String discordID)
    {
        if (discordID == null)
        {
            return false;
        }

        Guild server = bot.getGuildById(ConfigEntry.DISCORD_SERVER_ID.getString());
        if (server == null)
        {
            FLog.severe("The Discord server ID specified is invalid, or the bot is not on the server.");
            return false;
        }

        Member member = server.getMemberById(discordID);
        if (member == null)
        {
            return false;
        }

        Role adminRole = server.getRoleById(ConfigEntry.DISCORD_NEW_ADMIN_ROLE_ID.getString());
        if (adminRole == null)
        {
            FLog.severe("The specified Admin role does not exist!");
            return false;
        }

        Role senioradminRole = server.getRoleById(ConfigEntry.DISCORD_SENIOR_ADMIN_ROLE_ID.getString());
        if (senioradminRole == null)
        {
            FLog.severe("The specified Senior Admin role does not exist!");
            return false;
        }

        if (!staffMember.isActive())
        {
            if (member.getRoles().contains(adminRole))
            {
                server.removeRoleFromMember(member, adminRole).complete();
            }
            if (member.getRoles().contains(senioradminRole))
            {
                server.removeRoleFromMember(member, senioradminRole).complete();
            }
            return true;
        }

        if (staffMember.getRank().equals(Rank.ADMIN))
        {
            if (!member.getRoles().contains(adminRole))
            {
                server.addRoleToMember(member, adminRole).complete();
            }
            if (member.getRoles().contains(senioradminRole))
            {
                server.removeRoleFromMember(member, senioradminRole).complete();
            }
            return true;
        }
        else if (staffMember.getRank().equals(Rank.SENIOR_ADMIN))
        {
            if (!member.getRoles().contains(senioradminRole))
            {
                server.addRoleToMember(member, senioradminRole).complete();
            }
            if (member.getRoles().contains(adminRole))
            {
                server.removeRoleFromMember(member, adminRole).complete();
            }
            return true;
        }
        return false;
    }
}