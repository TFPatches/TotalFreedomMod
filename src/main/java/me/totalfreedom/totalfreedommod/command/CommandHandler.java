package me.totalfreedom.totalfreedommod.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommandHandler
{
    private List<FreedomCommand> commands;

    public CommandHandler()
    {
        commands = new ArrayList<>();
    }

    public void add(FreedomCommand cmd)
    {
        commands.add(cmd);
        cmd.register();
    }

    public int getCommandAmount()
    {
        return commands.size();
    }

    public void clearCommands()
    {
        commands.clear();
    }
}