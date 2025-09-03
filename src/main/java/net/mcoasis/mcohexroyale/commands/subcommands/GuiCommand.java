package net.mcoasis.mcohexroyale.commands.subcommands;

import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.gui.MainPage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.*;

public class GuiCommand implements SubCommand{

    // /hexroyale flag [set/remove/help]

    //private final List<String> arg1Topics = Arrays.asList("gui", "help");

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return;
        }

        if (args.length != 0) {
            sendIncorrectUsage(player);
            return;
        }

        if (!GuiManager.getInstance().openPage(MainPage.pageId, player)) {
            Bukkit.broadcastMessage(ChatColor.RED + "An Error Occurred: Could not open GUI");
            Bukkit.getLogger().warning("Could not open page -- ensure it is created in main plugin class");
        }

        /*String arg1 = ChatColor.stripColor(args[0].toLowerCase());

        switch (arg1) {
            case "gui":
                player.sendMessage(ChatColor.GREEN + "Opened game gui!");
                if (!me.ericdavis.lazygui.gui.GuiManager.getInstance().openPage(GameControlsPage.pageId, player)) {
                    Bukkit.broadcastMessage(ChatColor.RED + "An Error Occurred: Could not open GUI");
                }
                break;
            case "help":
                player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Flag Commands:");
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + " >> " + ChatColor.RESET + ChatColor.GRAY + "/hexroyale flag gui - Unimplemented!");
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + " >> " + ChatColor.RESET + ChatColor.GRAY + "/hexroyale flag help - See more information");
                break;
            default:
                sendIncorrectUsage(player);
                break;
        }*/

    }

    private void sendIncorrectUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Incorrect Usage -- Use the help argument if confused");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        String input;
        switch (args.length) {
            default:
                return Collections.emptyList();
        }
    }

}
