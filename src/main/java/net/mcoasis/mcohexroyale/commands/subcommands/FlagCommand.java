package net.mcoasis.mcohexroyale.commands.subcommands;

import me.ericdavis.lazyGui.guiOther.GuiManager;
import me.ericdavis.lazyGui.guiPage.AbstractGuiPage;
import net.mcoasis.mcohexroyale.lazygui.guipages.FlagsPage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class FlagCommand implements SubCommand{

    // /hexroyale flag [set/remove/help]

    private final List<String> arg1Topics = Arrays.asList("gui", "help");

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return;
        }

        if (args.length != 1) {
            sendIncorrectUsage(player);
            return;
        }

        String arg1 = ChatColor.stripColor(args[0].toLowerCase());

        switch (arg1) {
            case "gui":
                player.sendMessage(ChatColor.GREEN + "Opened flag gui!");
                AbstractGuiPage page = GuiManager.guiPages.get(FlagsPage.pageId);
                if (page != null) page.open(player);
                else Bukkit.broadcastMessage(ChatColor.RED + "An Error Occurred: Could not open GUI");
                break;
            case "help":
                player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Flag Commands:");
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + " >> " + ChatColor.RESET + ChatColor.GRAY + "/hexroyale flag gui - Unimplemented!");
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + " >> " + ChatColor.RESET + ChatColor.GRAY + "/hexroyale flag help - See more information");
                break;
            default:
                sendIncorrectUsage(player);
                break;
        }

    }

    private void sendIncorrectUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Incorrect Usage -- Use the help argument if confused");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        String input;
        switch (args.length) {
            case 1:
                input = args[args.length-1].toLowerCase();
                return arg1Topics.stream()
                        .filter(topic -> topic.startsWith(input))
                        .collect(Collectors.toList());
            default:
                return Collections.emptyList();
        }
    }

}
