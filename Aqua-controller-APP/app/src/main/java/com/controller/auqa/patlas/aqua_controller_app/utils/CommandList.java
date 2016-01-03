package com.controller.auqa.patlas.aqua_controller_app.utils;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by PatLas on 2016-01-03.
 */
public class CommandList
{
    private Hashtable<String, Command> commands = new Hashtable<>();

    //Singleton
    private static CommandList commandList = new CommandList();
    private CommandList(){}

    public static CommandList getInstance()
    {
        return commandList;
    }

    public boolean addCommand(String name, Command cmd)
    {
        if(commands.put(name, cmd) != null)
            return false;
        return true;
    }

    public void executeCommand(String cmd, ArrayList<String> args)
    {
        Command command = commands.get(cmd);
        if(command != null)
        {
            commands.get(cmd).execute(args);
        }
    }

}
