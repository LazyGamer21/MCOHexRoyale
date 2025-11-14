package net.mcoasis.mcohexroyale.commands.participant;

import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.gui.MainPage;
import net.mcoasis.mcohexroyale.gui.ShopPage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return true;
        }

        if (args.length != 0) {
            p.sendMessage("Incorrect Usage: /shop");
            return true;
        }

        if (!GuiManager.getInstance().openPage(ShopPage.pageId, p)) {
            p.sendMessage(ChatColor.RED + "An Error Occurred: Could not open shop");
            Bukkit.getLogger().warning("Could not open page -- ensure it is created in main plugin class");
        }

        return true;
    }
}
