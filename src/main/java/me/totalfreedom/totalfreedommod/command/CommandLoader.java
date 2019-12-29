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
        FLog.info("Loaded " + handler.getCommandAmount() + " commands.");
    }

    @Override
    public void stop()
    {
        handler.clearCommands();
    }

}