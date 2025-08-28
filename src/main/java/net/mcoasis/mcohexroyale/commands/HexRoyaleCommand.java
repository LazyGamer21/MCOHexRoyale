package net.mcoasis.mcohexroyale.commands;

import net.mcoasis.mcohexroyale.commands.subcommands.*;

import net.mcoasis.mcohexroyale.commands.subcommands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class HexRoyaleCommand implements CommandExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public HexRoyaleCommand() {
        // Register subcommands
        subCommands.put("help", new HelpCommand());
        subCommands.put("gui", new GuiCommand());
    }

    public Map<String, SubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /hexroyale __subcommands__

        if (args.length == 0) {
            sender.sendMessage("Usage: /hexroyale <subcommand>");
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(subCommandName);

        if (subCommand == null) {
            sender.sendMessage("Unknown subcommand: " + subCommandName);
            return true;
        }

        // Pass the remaining arguments to the subcommand
        String[] subArgs = new String[args.length - 1];
        System.arraycopy(args, 1, subArgs, 0, args.length - 1);
        subCommand.execute(sender, subArgs);
        return true;
    }

}
