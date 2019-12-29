package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.util.FLog;

public class CommandLoader extends FreedomService
{
    private CommandHandler handler;

    public CommandLoader()
    {
        super();
    }

    @Override
    public void start()
    {
        handler = new CommandHandler();
        handler.add(new Command_adminchat());
        handler.add(new Command_admininfo());
        handler.add(new Command_adminmode());
        handler.add(new Command_adminworld());
        handler.add(new Command_adventure());
        handler.add(new Command_aeclear());
        handler.add(new Command_announce());
        handler.add(new Command_attributelist());
        handler.add(new Command_banlist());
        handler.add(new Command_bird());
        handler.add(new Command_blockcmd());
        handler.add(new Command_blockedit());
        handler.add(new Command_blockpvp());
        handler.add(new Command_blockredstone());
        handler.add(new Command_cage());
        handler.add(new Command_cake());
        handler.add(new Command_campfire());
        handler.add(new Command_cartsit());
        handler.add(new Command_cbtool());
        handler.add(new Command_clearchat());
        handler.add(new Command_clearinventory());
        handler.add(new Command_cmdspy());
        handler.add(new Command_coins());
        handler.add(new Command_colorme());
        handler.add(new Command_commandlist());
        handler.add(new Command_consolesay());
        handler.add(new Command_cookie());
        handler.add(new Command_creative());
        handler.add(new Command_cuck());
        handler.add(new Command_curse());
        handler.add(new Command_deafen());
        handler.add(new Command_debug());
        handler.add(new Command_debugstick());
        handler.add(new Command_denick());
        handler.add(new Command_deop());
        handler.add(new Command_deopall());
        handler.add(new Command_disguisetoggle());
        handler.add(new Command_dispfill());
        handler.add(new Command_doom());
        handler.add(new Command_eject());
        handler.add(new Command_enchant());
        handler.add(new Command_enchantments());
        handler.add(new Command_ender());
        handler.add(new Command_entitywipe());
        handler.add(new Command_expel());
        handler.add(new Command_explosivearrows());
        handler.add(new Command_findip());
        handler.add(new Command_flatlands());
        handler.add(new Command_forcekill());
        handler.add(new Command_freeze());
        handler.add(new Command_fuckoff());
        handler.add(new Command_gadmin());
        handler.add(new Command_gchat());
        handler.add(new Command_gcmd());
        handler.add(new Command_glist());
        handler.add(new Command_gravity());
        handler.add(new Command_gtfo());
        handler.add(new Command_health());
        handler.add(new Command_hubworld());
        handler.add(new Command_invis());
        handler.add(new Command_invsee());
        handler.add(new Command_jumppads());
        handler.add(new Command_kick());
        handler.add(new Command_kicknoob());
        handler.add(new Command_landmine());
        handler.add(new Command_lastcmd());
        handler.add(new Command_linkdiscord());
        handler.add(new Command_links());
        handler.add(new Command_list());
        handler.add(new Command_localspawn());
        handler.add(new Command_lockup());
        handler.add(new Command_logs());
        handler.add(new Command_manageshop());
        handler.add(new Command_masterbuilderinfo());
        handler.add(new Command_masterbuilderworld());
        handler.add(new Command_mbconfig());
        handler.add(new Command_moblimiter());
        handler.add(new Command_mobpurge());
        handler.add(new Command_modifyitem());
        handler.add(new Command_mp44());
        handler.add(new Command_myadmin());
        handler.add(new Command_namehistory());
        handler.add(new Command_nether());
        handler.add(new Command_nickclean());
        handler.add(new Command_nickfilter());
        handler.add(new Command_nicknyan());
        handler.add(new Command_onlinemode());
        handler.add(new Command_op());
        handler.add(new Command_opall());
        handler.add(new Command_opme());
        handler.add(new Command_ops());
        handler.add(new Command_orbit());
        handler.add(new Command_permban());
        handler.add(new Command_playerverify());
        handler.add(new Command_plotworld());
        handler.add(new Command_plugincontrol());
        handler.add(new Command_potion());
        handler.add(new Command_potionspy());
        handler.add(new Command_premium());
        handler.add(new Command_protectarea());
        handler.add(new Command_purgeall());
        handler.add(new Command_qdeop());
        handler.add(new Command_qop());
        handler.add(new Command_rainbownick());
        handler.add(new Command_rank());
        handler.add(new Command_rawsay());
        handler.add(new Command_releaseparrots());
        handler.add(new Command_report());
        handler.add(new Command_restart());
        handler.add(new Command_ride());
        handler.add(new Command_ro());
        handler.add(new Command_rollback());
        handler.add(new Command_saconfig());
        handler.add(new Command_say());
        handler.add(new Command_scare());
        handler.add(new Command_setcompass());
        handler.add(new Command_setlevel());
        handler.add(new Command_setlever());
        handler.add(new Command_setlimit());
        handler.add(new Command_setspawnworld());
        handler.add(new Command_smite());
        handler.add(new Command_spawnmob());
        handler.add(new Command_spectate());
        handler.add(new Command_spectator());
        handler.add(new Command_status());
        handler.add(new Command_stfu());
        handler.add(new Command_stop());
        handler.add(new Command_survival());
        handler.add(new Command_tag());
        handler.add(new Command_tagnyan());
        handler.add(new Command_tagrainbow());
        handler.add(new Command_tban());
        handler.add(new Command_tempban());
        handler.add(new Command_toggle());
        handler.add(new Command_togglepickup());
        handler.add(new Command_tossmob());
        handler.add(new Command_totalfreedommod());
        handler.add(new Command_tprandom());
        handler.add(new Command_trail());
        handler.add(new Command_unban());
        handler.add(new Command_undisguiseall());
        handler.add(new Command_unlinkdiscord());
        handler.add(new Command_unloadchunks());
        handler.add(new Command_vanish());
        handler.add(new Command_verify());
        handler.add(new Command_verifynoadmin());
        handler.add(new Command_vote());
        handler.add(new Command_warn());
        handler.add(new Command_whitelist());
        handler.add(new Command_whohas());
        handler.add(new Command_wildcard());
        handler.add(new Command_wipecoreprotectdata());
        handler.add(new Command_wipeflatlands());
        handler.add(new Command_wipepunishments());
        handler.add(new Command_wiperegions());
        handler.add(new Command_wipeuserdata());
        handler.add(new Command_wipewarps());
        FLog.info("Loaded " + handler.getCommandAmount() + " commands.");
    }

    @Override
    public void stop()
    {
        handler.clearCommands();
    }

}