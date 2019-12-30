package me.totalfreedom.totalfreedommod;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import me.totalfreedom.totalfreedommod.admin.ActivityLog;
import me.totalfreedom.totalfreedommod.admin.AdminList;
import me.totalfreedom.totalfreedommod.amp.AMP;
import me.totalfreedom.totalfreedommod.banning.BanManager;
import me.totalfreedom.totalfreedommod.banning.PermbanList;
import me.totalfreedom.totalfreedommod.blocking.BlockBlocker;
import me.totalfreedom.totalfreedommod.blocking.EditBlocker;
import me.totalfreedom.totalfreedommod.blocking.EventBlocker;
import me.totalfreedom.totalfreedommod.blocking.InteractBlocker;
import me.totalfreedom.totalfreedommod.blocking.MobBlocker;
import me.totalfreedom.totalfreedommod.blocking.PVPBlocker;
import me.totalfreedom.totalfreedommod.blocking.PotionBlocker;
import me.totalfreedom.totalfreedommod.blocking.SignBlocker;
import me.totalfreedom.totalfreedommod.blocking.command.CommandBlocker;
import me.totalfreedom.totalfreedommod.bridge.BukkitTelnetBridge;
import me.totalfreedom.totalfreedommod.bridge.CoreProtectBridge;
import me.totalfreedom.totalfreedommod.bridge.EssentialsBridge;
import me.totalfreedom.totalfreedommod.bridge.LibsDisguisesBridge;
import me.totalfreedom.totalfreedommod.bridge.WorldEditBridge;
import me.totalfreedom.totalfreedommod.bridge.WorldEditListener;
import me.totalfreedom.totalfreedommod.bridge.WorldGuardBridge;
import me.totalfreedom.totalfreedommod.caging.Cager;
import me.totalfreedom.totalfreedommod.command.CommandLoader;
import me.totalfreedom.totalfreedommod.config.MainConfig;
import me.totalfreedom.totalfreedommod.discord.Discord;
import me.totalfreedom.totalfreedommod.freeze.Freezer;
import me.totalfreedom.totalfreedommod.fun.CurseListener;
import me.totalfreedom.totalfreedommod.fun.ItemFun;
import me.totalfreedom.totalfreedommod.fun.Jumppads;
import me.totalfreedom.totalfreedommod.fun.Landminer;
import me.totalfreedom.totalfreedommod.fun.MP44;
import me.totalfreedom.totalfreedommod.fun.MobStacker;
import me.totalfreedom.totalfreedommod.fun.Trailer;
import me.totalfreedom.totalfreedommod.httpd.HTTPDaemon;
import me.totalfreedom.totalfreedommod.masterbuilder.MasterBuilder;
import me.totalfreedom.totalfreedommod.masterbuilder.MasterBuilderList;
import me.totalfreedom.totalfreedommod.masterbuilder.MasterBuilderWorldRestrictions;
import me.totalfreedom.totalfreedommod.player.PlayerList;
import me.totalfreedom.totalfreedommod.playerverification.PlayerVerification;
import me.totalfreedom.totalfreedommod.punishments.PunishmentList;
import me.totalfreedom.totalfreedommod.rank.RankManager;
import me.totalfreedom.totalfreedommod.rollback.RollbackManager;
import me.totalfreedom.totalfreedommod.shop.Shop;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.util.MethodTimer;
import me.totalfreedom.totalfreedommod.world.CleanroomChunkGenerator;
import me.totalfreedom.totalfreedommod.world.WorldManager;
import org.bstats.Metrics;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.SpigotConfig;

public class TotalFreedomMod extends JavaPlugin
{
    private static TotalFreedomMod plugin;
    public static TotalFreedomMod getPlugin()
    {
        return plugin;
    }

    public static final String CONFIG_FILENAME = "config.yml";
    //
    public static final BuildProperties build = new BuildProperties();
    //
    public static String pluginName;
    public static String pluginVersion;
    //
    public MainConfig config;
    //
    // Services
    public ServerInterface si;
    public SavedFlags sf;
    public WorldManager wm;
    public LogViewer lv;
    public AdminList al;
    public ActivityLog acl;
    public RankManager rm;
    public CommandLoader cl;
    public CommandBlocker cb;
    public EventBlocker eb;
    public BlockBlocker bb;
    public MobBlocker mb;
    public InteractBlocker ib;
    public PotionBlocker pb;
    public LoginProcess lp;
    public AntiNuke nu;
    public AntiSpam as;
    public PlayerList pl;
    public Shop sh;
    public Announcer an;
    public ChatManager cm;
    public Discord dc;
    public PunishmentList pul;
    public BanManager bm;
    public PermbanList pm;
    public ProtectArea pa;
    public GameRuleHandler gr;
    public RollbackManager rb;
    public CommandSpy cs;
    public Cager ca;
    public Freezer fm;
    public EditBlocker ebl;
    public PVPBlocker pbl;
    public Orbiter or;
    public Muter mu;
    public Fuckoff fo;
    public AutoKick ak;
    public AutoEject ae;
    public Monitors mo;
    public MovementValidator mv;
    public ServerPing sp;
    public CurseListener cul;
    public ItemFun it;
    public Landminer lm;
    public MobStacker ms;
    public MP44 mp;
    public Jumppads jp;
    public Trailer tr;
    public HTTPDaemon hd;
    public MasterBuilderList mbl;
    public MasterBuilderWorldRestrictions mbwr;
    public SignBlocker snp;
    public PlayerVerification pv;
    //public HubWorldRestrictions hwr;
    //
    // Bridges
    public BukkitTelnetBridge btb;
    public EssentialsBridge esb;
    public LibsDisguisesBridge ldb;
    public CoreProtectBridge cpb;
    public WorldEditBridge web;
    public WorldEditListener wel;
    public WorldGuardBridge wgb;
    public AMP amp;

    @Override
    public void onEnable()
    {
        plugin = this;

        TotalFreedomMod.pluginName = getDescription().getName();
        TotalFreedomMod.pluginVersion = getDescription().getVersion();

        FLog.setPluginLogger(getLogger());
        FLog.setServerLogger(getServer().getLogger());

        build.load(this);

        FLog.info("Created by Madgeek1450 and Prozza");
        FLog.info("Version " + build.version);
        FLog.info("Compiled " + build.date + " by " + build.author);

        final MethodTimer timer = new MethodTimer();
        timer.start();

        // Warn if we're running on a wrong version
        ServerInterface.warnVersion();

        // Delete unused files
        FUtil.deleteCoreDumps();
        FUtil.deleteFolder(new File("./_deleteme"));

        BackupManager backups = new BackupManager();
        backups.createBackups(TotalFreedomMod.CONFIG_FILENAME, true);
        backups.createBackups(AdminList.CONFIG_FILENAME);
        backups.createBackups(PermbanList.CONFIG_FILENAME);
        backups.createBackups(MasterBuilder.CONFIG_FILENAME);
        backups.createBackups(PunishmentList.CONFIG_FILENAME);

        config = new MainConfig();
        config.load();

        // Start services
        start();

        timer.update();
        FLog.info("Version " + pluginVersion + " for " + ServerInterface.COMPILE_NMS_VERSION + " enabled in " + timer.getTotal() + "ms");

        // Metrics @ https://bstats.org/plugin/bukkit/TotalFreedomMod
        new Metrics(this);

        // Add spawnpoints later - https://github.com/TotalFreedom/TotalFreedomMod/issues/438
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                plugin.pa.autoAddSpawnpoints();
            }
        }.runTaskLater(plugin, 60L);
        // little workaround to stop spigot from autorestarting - causing AMP to detach from process.
        SpigotConfig.config.set("settings.restart-on-crash", false);
    }

    @Override
    public void onDisable()
    {
        // Stop services
        stop();

        getServer().getScheduler().cancelTasks(plugin);
        
        plugin = null;

        FLog.info("Plugin disabled");
    }

    public static class BuildProperties
    {

        public String author;
        public String codename;
        public String version;
        public String number;
        public String date;
        public String head;

        public void load(TotalFreedomMod plugin)
        {
            try
            {
                final Properties props;

                try (InputStream in = plugin.getResource("build.properties"))
                {
                    props = new Properties();
                    props.load(in);
                }

                author = props.getProperty("buildAuthor", "unknown");
                codename = props.getProperty("buildCodeName", "unknown");
                version = props.getProperty("buildVersion", pluginVersion);
                number = props.getProperty("buildNumber", "1");
                date = props.getProperty("buildDate", "unknown");
                // Need to do this or it will display ${git.commit.id.abbrev}
                head = props.getProperty("buildHead", "unknown").replace("${git.commit.id.abbrev}", "unknown");
            }
            catch (Exception ex)
            {
                FLog.severe("Could not load build properties! Did you compile with NetBeans/Maven?");
                FLog.severe(ex);
            }
        }

        public String formattedVersion()
        {
            return pluginVersion + "." + number + " (" + head + ")";
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
    {
        return new CleanroomChunkGenerator(id);
    }

    public void start()
    {
        // Start services
        si = new ServerInterface();
        sf = new SavedFlags();
        wm = new WorldManager();
        lv = new LogViewer();
        al = new AdminList();
        acl = new ActivityLog();
        rm = new RankManager();
        cl = new CommandLoader();
        cb = new CommandBlocker();
        eb = new EventBlocker();
        bb = new BlockBlocker();
        mb = new MobBlocker();
        ib = new InteractBlocker();
        pb = new PotionBlocker();
        lp = new LoginProcess();
        nu = new AntiNuke();
        as = new AntiSpam();
        mbl = new MasterBuilderList();
        mbwr = new MasterBuilderWorldRestrictions();
        //hwr = new HubWorldRestrictions();
        pl = new PlayerList();
        sh = new Shop();
        an = new Announcer();
        cm = new ChatManager();
        dc = new Discord();
        pul = new PunishmentList();
        bm = new BanManager();
        pm = new PermbanList();
        pa = new ProtectArea();
        gr = new GameRuleHandler();
        snp = new SignBlocker();

        // Single admin utils
        rb = new RollbackManager();
        cs = new CommandSpy();
        ca = new Cager();
        fm = new Freezer();
        or = new Orbiter();
        mu = new Muter();
        ebl = new EditBlocker();
        pbl = new PVPBlocker();
        fo = new Fuckoff();
        ak = new AutoKick();
        ae = new AutoEject();
        mo = new Monitors();


        mv = new MovementValidator();
        sp = new ServerPing();
        pv = new PlayerVerification();

        // Fun
        cul = new CurseListener();
        it = new ItemFun();
        lm = new Landminer();
        ms = new MobStacker();
        mp = new MP44();
        jp = new Jumppads();
        tr = new Trailer();

        // HTTPD
        hd = new HTTPDaemon();

        // Start bridges
        btb = new BukkitTelnetBridge();
        cpb = new CoreProtectBridge();
        esb = new EssentialsBridge();
        ldb = new LibsDisguisesBridge();
        web = new WorldEditBridge();
        wel = new WorldEditListener();
        wgb = new WorldGuardBridge();
        amp = new AMP();

        // Start services
        si.start();
        sf.start();
        wm.start();
        lv.start();
        al.start();
        acl.start();
        rm.start();
        cl.start();
        cb.start();
        eb.start();
        bb.start();
        mb.start();
        ib.start();
        pb.start();
        lp.start();
        nu.start();
        as.start();
        mbl.start();
        mbwr.start();
        //hwr.start();
        pl.start();
        sh.start();
        an.start();
        cm.start();
        dc.start();
        pul.start();
        bm.start();
        pm.start();
        pa.start();
        gr.start();
        snp.start();

        rb.start();
        cs.start();
        ca.start();
        fm.start();
        or.start();
        mu.start();
        ebl.start();
        pbl.start();
        fo.start();
        ak.start();
        ae.start();
        mo.start();


        mv.start();
        sp.start();
        pv.start();

        cul.start();
        it.start();
        lm.start();
        ms.start();
        mp.start();
        jp.start();
        tr.start();

        hd.start();

        // Start bridges
        btb.start();
        cpb.start();
        esb.start();
        ldb.start();
        web.start();
        wel.start();
        wgb.start();
    }

    public void stop()
    {
        // Stop services
        si.stop();
        sf.stop();
        wm.stop();
        lv.stop();
        al.stop();
        acl.stop();
        rm.stop();
        cl.stop();
        cb.stop();
        eb.stop();
        bb.stop();
        mb.stop();
        ib.stop();
        pb.stop();
        lp.stop();
        nu.stop();
        as.stop();
        mbl.stop();
        mbwr.stop();
        //hwr.stop();
        pl.stop();
        sh.stop();
        an.stop();
        cm.stop();
        dc.stop();
        pul.stop();
        bm.stop();
        pm.stop();
        pa.stop();
        gr.stop();
        snp.stop();

        rb.stop();
        cs.stop();
        ca.stop();
        fm.stop();
        or.stop();
        mu.stop();
        ebl.stop();
        pbl.stop();
        fo.stop();
        ak.stop();
        ae.stop();
        mo.stop();


        mv.stop();
        sp.stop();
        pv.stop();

        cul.stop();
        it.stop();
        lm.stop();
        ms.stop();
        mp.stop();
        jp.stop();
        tr.stop();

        hd.stop();

        // Stop bridges
        btb.stop();
        cpb.stop();
        esb.stop();
        ldb.stop();
        web.stop();
        wel.stop();
        wgb.stop();
    }
}
