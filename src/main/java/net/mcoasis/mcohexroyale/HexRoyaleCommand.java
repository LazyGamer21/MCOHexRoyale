package net.mcoasis.mcohexroyale;

import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.gui.MainPage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HexRoyaleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length != 0) {
            player.sendMessage("Usage: /mcohexroyale");
            return true;
        }

        if (!GuiManager.getInstance().openPage(MainPage.pageId, player)) {
            player.sendMessage(ChatColor.RED + "An Error Occurred: Could not open GUI");
            Bukkit.getLogger().warning("Could not open page -- ensure it is created in main plugin class");
        }

        return true;
    }

}
