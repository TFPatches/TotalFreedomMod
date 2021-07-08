package me.totalfreedom.totalfreedommod.util;

import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.bukkit.Bukkit.getServer;

public class FUtil
{

    public static final String SAVED_FLAGS_FILENAME = "savedflags.dat";
    /* See https://github.com/TotalFreedom/License - None of the listed names may be removed.
    Leaving this list here for anyone running TFM on a cracked server:
    public static final List<String> DEVELOPERS = Arrays.asList("Madgeek1450", "Prozza", "WickedGamingUK", "Wild1145", "aggelosQQ", "scripthead", "Telesphoreo", "CoolJWB");
    */
    public static final List<String> DEVELOPERS = Arrays.asList(
            "1156a81a-23fb-435e-9aff-fe9c2ea7e82d", // Madgeek1450
            "f9a1982e-252e-4ed3-92ed-52b0506a39c9", // Prozza
            "90eb5d86-ed60-4165-a36e-bb77aa3c6664", // WickedGamingUK
            "604cbb51-842d-4b43-8b0a-d1d7c6cd2869", // Wild1145
            "e67d77c4-fff9-4cea-94cc-9f1f1ab7806b", // aggelosQQ
            "0061326b-8b3d-44c8-830a-5f2d59f5dc1b", // scripthead
            "78408086-1991-4c33-a571-d8fa325465b2", // Telesphoreo
            "67ce0e28-3d6b-469c-ab71-304eec81b614", // CoolJWB
            "03b41e15-d03f-4025-86f5-f1812df200fa", // elmon_
            "d018f2b8-ce60-4672-a45f-e580e0331299", // speednt
            "458de06f-36a5-4e1b-aaa6-ec1d1751c5b6", // SupItsDillon
            "c8e5af82-6aba-4dd7-83e8-474381380cc9", // Paldiu
            "38ea7c82-7bdc-4f48-b7fd-0e93fc26813d", // AwesomePinch
            "ba5aafba-9012-418f-9819-a7020d591068",  // TFTWPhoenix
            "d6dd9740-40db-45f5-ab16-4ee16a633009", // Abhi
            "2e06e049-24c8-42e4-8bcf-d35372af31e6", // NotInSync
            "f97c0d7b-6413-4558-a409-88f09a8f9adb" // videogamesm12
    );
    public static final List<String> DEVELOPER_NAMES = Arrays.asList("Madgeek1450", "Prozza", "WickedGamingUK", "Wild1145", "aggelosQQ", "scripthead", "CoolJWB", "elmon_", "speednt", "SupItsDillon", "Paldiu", "AwesomePinch", "TFTWPhoenix", "abhithedev", "NotInSync", "videogamesm12");
    public static final Map<String, ChatColor> CHAT_COLOR_NAMES = new HashMap<>();
    public static final List<ChatColor> CHAT_COLOR_POOL = Arrays.asList(
            ChatColor.DARK_RED,
            ChatColor.RED,
            ChatColor.GOLD,
            ChatColor.YELLOW,
            ChatColor.GREEN,
            ChatColor.DARK_GREEN,
            ChatColor.AQUA,
            ChatColor.DARK_AQUA,
            ChatColor.BLUE,
            ChatColor.DARK_BLUE,
            ChatColor.DARK_PURPLE,
            ChatColor.LIGHT_PURPLE);
    private static final SplittableRandom RANDOM = new SplittableRandom();
    private static final String CHARACTER_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Map<Integer, String> TIMEZONE_LOOKUP = new HashMap<>();
    public static String DATE_STORAGE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";

    static
    {
        for (ChatColor chatColor : CHAT_COLOR_POOL)
        {
            CHAT_COLOR_NAMES.put(chatColor.name().toLowerCase().replace("_", ""), chatColor);
        }

        for (int i = -12; i <= 12; i++)
        {
            String sec = String.valueOf(i).replace("-", "");
            if (i > -10 && i < 10)
            {
                sec = "0" + sec;
            }
            if (i >= 0)
            {
                sec = "+" + sec;
            }
            else
            {
                sec = "-" + sec;
            }
            TIMEZONE_LOOKUP.put(i, "GMT" + sec + ":00");
        }
    }

    public static void cancel(BukkitTask task)
    {
        if (task == null)
        {
            return;
        }

        try
        {
            task.cancel();
        }
        catch (Exception ignored)
        {
        }
    }

    public static boolean isExecutive(String name)
    {
        return ConfigEntry.SERVER_OWNERS.getStringList().contains(name) || ConfigEntry.SERVER_EXECUTIVES.getStringList().contains(name) || ConfigEntry.SERVER_ASSISTANT_EXECUTIVES.getStringList().contains(name);
    }

    public static boolean isDeveloper(Player player)
    {
        if (Bukkit.getOnlineMode())
        {
            return DEVELOPERS.contains(player.getUniqueId().toString());
        }
        else
        {
            return DEVELOPER_NAMES.contains(player.getName());
        }
    }

    public static boolean inDeveloperMode()
    {
        return ConfigEntry.DEVELOPER_MODE.getBoolean();
    }

    public static String formatName(String name)
    {
        return WordUtils.capitalizeFully(name.replace("_", " "));
    }

    public static String showS(int count)
    {
        return (count == 1 ? "" : "s");
    }

    public static List<String> getPlayerList()
    {
        List<String> names = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (!TotalFreedomMod.getPlugin().al.isVanished(player.getName()))
            {
                names.add(player.getName());
            }
        }
        return names;
    }

    public static String listToString(List<String> list)
    {
        if (list.size() == 0)
        {
            return null;
        }

        return String.join(", ", list);
    }

    public static List<String> stringToList(String string)
    {
        if (string == null)
        {
            return new ArrayList<>();
        }

        return Arrays.asList(string.split(", "));
    }

    /**
     * A way to get a sublist with a page index and a page size.
     *
     * @param list  A list of objects that should be split into pages.
     * @param size  The size of the pages.
     * @param index The page index, if outside of bounds error will be thrown. The page index starts at 0 as with all lists.
     * @return A list of objects that is the page that has been selected from the previous last parameter.
     */
    public static List<String> getPageFromList(List<String> list, int size, int index)
    {
        try
        {
            if (size >= list.size())
            {
                return list;
            }
            else if (size * (index + 1) <= list.size())
            {
                return list.subList(size * index, size * (index + 1));
            }
            else
            {
                return list.subList(size * index, (size * index) + (list.size() % size));
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            return new ArrayList<>();
        }
    }

    public static List<String> getAllMaterialNames()
    {
        List<String> names = new ArrayList<>();
        for (Material material : Material.values())
        {
            names.add(material.name());
        }
        return names;
    }

    @SuppressWarnings("unchecked")
    public static UUID nameToUUID(String name)
    {
        try
        {
            JSONArray json = new JSONArray();
            json.add(name);
            List<String> headers = new ArrayList<>();
            headers.add("Accept:application/json");
            headers.add("Content-Type:application/json");
            Response response = sendRequest("https://api.mojang.com/profiles/minecraft", "POST", headers, json.toString());
            // Don't care how stupid this looks, couldn't find anything to parse a json string to something readable in java with something not horrendously huge, maybe im just retarded
            Pattern pattern = Pattern.compile("(?<=\"id\":\")[a-f0-9].{31}");
            Matcher matcher = pattern.matcher(response.getMessage());
            if (matcher.find())
            {
                String rawUUID = matcher.group(0).replaceFirst("([a-f0-9]{8})([a-f0-9]{4})([a-f0-9]{4})([a-f0-9]{4})([a-f0-9]+)", "$1-$2-$3-$4-$5");
                return UUID.fromString(rawUUID);
            }
        }
        catch (Exception e)
        {
            FLog.severe("Failed to convert name to UUID:\n" + e.toString());
        }
        return null;
    }

    public static Response sendRequest(String endpoint, String method, List<String> headers, String body) throws IOException
    {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(method);

        if (headers != null)
        {

            for (String header : headers)
            {
                String[] kv = header.split(":");
                connection.setRequestProperty(kv[0], kv[1]);
            }
        }

        FLog.info(connection.getRequestMethod());

        if (body != null)
        {
            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(body);
            outputStream.flush();
            outputStream.close();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null)
        {
            response.append(inputLine);
        }

        in.close();

        return new Response(connection.getResponseCode(), response.toString());
    }

    public static void bcastMsg(String message, ChatColor color)
    {
        bcastMsg(message, color, true);
    }

    public static void bcastMsg(String message, ChatColor color, Boolean toConsole)
    {
        if (toConsole)
        {
            FLog.info(message, true);
        }

        for (Player player : Bukkit.getOnlinePlayers())
        {
            player.sendMessage((color == null ? "" : color) + message);
        }
    }

    public static void bcastMsg(String message, Boolean toConsole)
    {
        bcastMsg(message, null, toConsole);
    }

    public static void bcastMsg(String message)
    {
        FUtil.bcastMsg(message, null, true);
    }

    // Still in use by listeners
    public static void playerMsg(CommandSender sender, String message, ChatColor color)
    {
        sender.sendMessage(color + message);
    }

    // Still in use by listeners
    public static void playerMsg(CommandSender sender, String message)
    {
        FUtil.playerMsg(sender, message, ChatColor.GRAY);
    }

    public static void setFlying(Player player, boolean flying)
    {
        player.setAllowFlight(true);
        player.setFlying(flying);
    }

    public static void adminAction(String adminName, String action, boolean isRed)
    {
        FUtil.bcastMsg(adminName + " - " + action, (isRed ? ChatColor.RED : ChatColor.AQUA));
    }

    public static String formatLocation(Location location)
    {
        return String.format("%s: (%d, %d, %d)",
                Objects.requireNonNull(location.getWorld()).getName(),
                Math.round(location.getX()),
                Math.round(location.getY()),
                Math.round(location.getZ()));
    }

    public static boolean deleteFolder(File file)
    {
        if (file.exists() && file.isDirectory())
        {
            return FileUtils.deleteQuietly(file);
        }
        return false;
    }

    public static void deleteCoreDumps()
    {
        final File[] coreDumps = new File(".").listFiles(file -> file.getName().startsWith("java.core"));

        for (File dump : coreDumps)
        {
            FLog.info("Removing core dump file: " + dump.getName());
            dump.delete();
        }
    }

    private static final List<String> regxList = new ArrayList<String>()
    {{
        add("y");
        add("mo");
        add("w");
        add("d");
        add("h");
        add("m");
        add("s");
    }};

    private static long a(String parse)
    {
        StringBuilder sb = new StringBuilder();

        regxList.forEach(obj -> {
            if (parse.endsWith(obj))
            {
                sb.append(parse.split(obj)[0]);
            }
        });

        return Long.parseLong(sb.toString());
    }

    private static TimeUnit verify(String arg)
    {
        TimeUnit unit = null;
        for (String c : regxList)
        {
            if (arg.endsWith(c))
            {
                switch (c)
                {
                    case "y":
                        unit = (TimeUnit.YEAR);
                        break;
                    case "mo":
                        unit = (TimeUnit.MONTH);
                        break;
                    case "w":
                        unit = (TimeUnit.WEEK);
                        break;
                    case "d":
                        unit = (TimeUnit.DAY);
                        break;
                    case "h":
                        unit = (TimeUnit.HOUR);
                        break;
                    case "m":
                        unit = (TimeUnit.MINUTE);
                        break;
                    case "s":
                        unit = (TimeUnit.SECOND);
                        break;
                }
                break;
            }
        }
        return (unit != null) ? unit : TimeUnit.DAY;
    }

    public static Date parseDateOffset(String... time)
    {
        Instant instant = Instant.now();
        for (String arg : time)
        {
            instant = instant.plusSeconds(verify(arg).get() * a(arg));
        }
        return Date.from(instant);
    }

    public static long parseLongOffset(long unix, String... time)
    {
        Instant instant = Instant.ofEpochMilli(unix);
        for (String arg : time)
        {
            instant = instant.plusSeconds(verify(arg).get() * a(arg));
        }
        return FUtil.getUnixTime(Date.from(instant));
    }

    public static String playerListToNames(Set<OfflinePlayer> players)
    {
        List<String> names = new ArrayList<>();
        for (OfflinePlayer player : players)
        {
            names.add(player.getName());
        }
        return StringUtils.join(names, ", ");
    }

    public static String dateToString(Date date)
    {
        return new SimpleDateFormat(DATE_STORAGE_FORMAT, Locale.ENGLISH).format(date);
    }

    public static Date stringToDate(String dateString)
    {
        try
        {
            return new SimpleDateFormat(DATE_STORAGE_FORMAT, Locale.ENGLISH).parse(dateString);
        }
        catch (ParseException pex)
        {
            return new Date(0L);
        }
    }

    public static boolean isFromHostConsole(String senderName)
    {
        return ConfigEntry.HOST_SENDER_NAMES.getList().contains(senderName.toLowerCase());
    }

    public static boolean fuzzyIpMatch(String a, String b, int octets)
    {
        boolean match = true;

        String[] aParts = a.split("\\.");
        String[] bParts = b.split("\\.");

        if (aParts.length != 4 || bParts.length != 4)
        {
            return false;
        }

        if (octets > 4)
        {
            octets = 4;
        }
        else if (octets < 1)
        {
            octets = 1;
        }

        for (int i = 0; i < octets; i++)
        {
            if (aParts[i].equals("*") || bParts[i].equals("*"))
            {
                continue;
            }

            if (!aParts[i].equals(bParts[i]))
            {
                match = false;
                break;
            }
        }

        return match;
    }

    public static String getFuzzyIp(String ip)
    {
        final String[] ipParts = ip.split("\\.");
        if (ipParts.length == 4)
        {
            return String.format("%s.%s.*.*", ipParts[0], ipParts[1]);
        }

        return ip;
    }

    //getField: Borrowed from WorldEdit
    @SuppressWarnings("unchecked")
    public static <T> T getField(Object from, String name)
    {
        Class<?> checkClass = from.getClass();
        do
        {
            try
            {
                Field field = checkClass.getDeclaredField(name);
                field.setAccessible(true);
                return (T) field.get(from);

            }
            catch (NoSuchFieldException | IllegalAccessException ignored)
            {
            }
        }
        while (checkClass.getSuperclass() != Object.class
                && ((checkClass = checkClass.getSuperclass()) != null));

        return null;
    }

    public static ChatColor randomChatColor()
    {
        return CHAT_COLOR_POOL.get(RANDOM.nextInt(CHAT_COLOR_POOL.size()));
    }

    public static String rainbowify(String string)
    {
        Iterator<ChatColor> CHAT_COLOR_ITERATOR = CHAT_COLOR_POOL.iterator();

        StringBuilder newString = new StringBuilder();
        char[] chars = string.toCharArray();

        for (char c : chars)
        {
            if (!CHAT_COLOR_ITERATOR.hasNext())
            {
                CHAT_COLOR_ITERATOR = CHAT_COLOR_POOL.iterator(); //Restart from first colour if there are no more colours in iterator.
            }
            newString.append(CHAT_COLOR_ITERATOR.next()).append(c);
        }

        return newString.toString();
    }

    public static String colorize(String string)
    {
        if (string != null)
        {
            Matcher matcher = Pattern.compile("&#[a-f0-9A-F]{6}").matcher(string);
            while (matcher.find())
            {
                String code = matcher.group().replace("&", "");
                string = string.replace("&" + code, net.md_5.bungee.api.ChatColor.of(code) + "");
            }

            string = ChatColor.translateAlternateColorCodes('&', string);
        }
        return string;
    }

    public static Date getUnixDate(long unix)
    {
        return new Date(unix);
    }

    public static long getUnixTime()
    {
        return Instant.now().toEpochMilli();
    }

    public static long getUnixTime(Date date)
    {
        if (date == null)
        {
            return 0;
        }

        return date.getTime();
    }

    public static String getNMSVersion()
    {
        String packageName = getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    public static int randomInteger(int min, int max)
    {
        int range = max - min + 1;
        return (int) (Math.random() * range) + min;
    }

    public static String randomString(int length)
    {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789-_=+[]{};:,.<>~";
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            int selectedCharacter = randomInteger(1, characters.length()) - 1;

            randomString.append(characters.charAt(selectedCharacter));
        }

        return randomString.toString();

    }

    public static String randomAlphanumericString(int length)
    {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            int selectedCharacter = randomInteger(1, characters.length()) - 1;

            randomString.append(characters.charAt(selectedCharacter));
        }

        return randomString.toString();

    }

    public static boolean isPaper()
    {
        try
        {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        }
        catch (ClassNotFoundException ignored)
        {
            return false;
        }
    }

    public static void fixCommandVoid(Player player)
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            for (Entity passengerEntity : p.getPassengers())
            {
                if (passengerEntity == player)
                {
                    p.removePassenger(passengerEntity);
                }
            }
        }
    }

    public static char getRandomCharacter()
    {
        return CHARACTER_STRING.charAt(new SplittableRandom().nextInt(CHARACTER_STRING.length()));
    }

    public static void give(Player player, Material material, String coloredName, int amount, String... lore)
    {
        ItemStack stack = new ItemStack(material, amount);
        ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        meta.setDisplayName(FUtil.colorize(coloredName));
        List<String> loreList = new ArrayList<>();
        for (String entry : lore)
        {
            loreList.add(FUtil.colorize(entry));
        }
        meta.setLore(loreList);
        stack.setItemMeta(meta);
        player.getInventory().setItem(player.getInventory().firstEmpty(), stack);
    }

    public static Player getRandomPlayer()
    {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        return players.get(randomInteger(0, players.size() - 1));
    }

    // convert the current time
    // Why is this needed??? It's not even used.
    public static int getTimeInTicks(int tz)
    {
        if (timeZoneOutOfBounds(tz))
        {
            return -1;
        }
        Calendar date = Calendar.getInstance(TimeZone.getTimeZone(TIMEZONE_LOOKUP.get(tz)));
        int res = 0;
        for (int i = 0; i < date.get(Calendar.HOUR_OF_DAY) - 6; i++) // oh yeah i don't know why this is 6 hours ahead
        {
            res += 1000;
        }
        int addExtra = 0; // we're adding extra to account for repeating decimals
        for (int i = 0; i < date.get(Calendar.MINUTE); i++)
        {
            res += 16;
            addExtra++;
            if (addExtra == 3)
            {
                res += 1;
                addExtra = 0;
            }
        }
        // this is the best it can be. trust me.
        return res;
    }

    public static boolean timeZoneOutOfBounds(int tz)
    {
        return tz < -12 || tz > 12;
    }

    public static String getIp(Player player)
    {
        return Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress().trim();
    }

    public static String getIp(PlayerLoginEvent event)
    {
        return event.getAddress().getHostAddress().trim();
    }

    private static Color interpolateColor(Color c1, Color c2, double factor)
    {
        long[] c1values = {c1.getRed(), c1.getGreen(), c1.getBlue()};
        long[] c2values = {c2.getRed(), c2.getGreen(), c2.getBlue()};
        for (int i = 0; i < 3; i++)
        {
            c1values[i] = Math.round(c1values[i] + factor * (c2values[i] - c1values[i]));
        }
        return Color.fromRGB((int) c1values[0], (int) c1values[1], (int) c1values[2]);
    }

    public static boolean isValidIPv4(String ip)
    {
        return !ip.matches("^([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))\\.([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))\\.([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))\\.([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))$")
                && !ip.matches("^([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))\\.([0-9]|[1-9][0-9]|1([0-9][0-9])|2([0-4][0-9]|5[0-5]))\\.([*])\\.([*])$");
    }

    public static List<Color> createColorGradient(Color c1, Color c2, int steps)
    {
        double factor = 1.0 / (steps - 1.0);
        List<Color> colors = new ArrayList<>();
        for (int i = 0; i < steps; i++)
        {
            colors.add(interpolateColor(c1, c2, factor * i));
        }
        return colors;
    }

    public static boolean colorClose(Color first, Color second, int tresHold)
    {
        int redDelta = Math.abs(first.getRed() - second.getRed());
        int greenDelta = Math.abs(first.getGreen() - second.getGreen());
        int blueDelta = Math.abs(first.getBlue() - second.getBlue());
        return (redDelta + greenDelta + blueDelta) < tresHold;
    }

    public static Color fromAWT(java.awt.Color color)
    {
        return Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static java.awt.Color toAWT(Color color)
    {
        return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static java.awt.Color getRandomAWTColor()
    {
        return new java.awt.Color(randomInteger(0, 255), randomInteger(0, 255), randomInteger(0, 255));
    }

    public static String getHexStringOfAWTColor(java.awt.Color color)
    {
        String hex = Integer.toHexString(color.getRGB() & 0xFFFFFF);
        if (hex.length() < 6)
        {
            hex = "0" + hex;
        }
        return "#" + hex;
    }

    public static void createExplosionOnDelay(Location location, float power, int delay)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                Objects.requireNonNull(location.getWorld()).createExplosion(location, power);
            }
        }.runTaskLater(TotalFreedomMod.getPlugin(), delay);
    }

    public static int getFakePlayerCount()
    {
        int i = TotalFreedomMod.getPlugin().al.vanished.size();
        for (String name : TotalFreedomMod.getPlugin().al.vanished)
        {
            if (Bukkit.getPlayer(name) == null)
            {
                i--;
            }
        }
        return getServer().getOnlinePlayers().size() - i;
    }

    public static class PaginationList<T> extends ArrayList<T>
    {

        private final int epp;

        public PaginationList(int epp)
        {
            super();
            this.epp = epp;
        }

        @SafeVarargs
        public PaginationList(int epp, T... elements)
        {
            super(Arrays.asList(elements));
            this.epp = epp;
        }

        public int getPageCount()
        {
            return (int) Math.ceil((double) size() / (double) epp);
        }

        public List<T> getPage(int page)
        {
            if (page < 1 || page > getPageCount())
            {
                return null;
            }
            int startIndex = (page - 1) * epp;
            int endIndex = Math.min(startIndex + (epp - 1), this.size() - 1);
            return subList(startIndex, endIndex + 1);
        }
    }
}
