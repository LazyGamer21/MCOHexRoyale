package net.mcoasis.mcohexroyale.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class HelpCommand implements SubCommand {

    // /hexroyale help

    //* examples of using the tabcompleter
    // private final List<String> arg1Topics = Arrays.asList("trash", "nick");
    // private final List<String> arg2Topics = Arrays.asList("idk");

    @Override
    public void execute(CommandSender sender, String[] args) {

        sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "HexRoyale Commands:");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + " >> " + ChatColor.RESET + ChatColor.GRAY + "/hexroyale start - Start the game");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + " >> " + ChatColor.RESET + ChatColor.GRAY + "/hexroyale flag - Modify flags");
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + " >> " + ChatColor.RESET + ChatColor.GRAY + "/hexroyale end - End the game");
        sender.sendMessage(ChatColor.YELLOW + "For more information on a specific command use that command's help function");

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        //* example of tabcompelter usage
        /*String input;
        switch (args.length) {
            case 1:
                input = args[args.length-1].toLowerCase();
                return arg1Topics.stream()
                        .filter(topic -> topic.startsWith(input))
                        .collect(Collectors.toList());
            case 2:
                input = args[args.length-1].toLowerCase();
                return arg2Topics.stream()
                        .filter(topic -> topic.startsWith(input))
                        .collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }*/
        return Collections.emptyList();
    }

}