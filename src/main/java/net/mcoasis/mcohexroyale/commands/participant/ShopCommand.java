package net.mcoasis.mcohexroyale.commands.participant;

import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.events.listeners.RespawnListener;
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

        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "Players cannot use this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Incorrect Usage: /shop {player}");
            return true;
        }

        Player p = Bukkit.getPlayer(ChatColor.stripColor(args[0]));

        if (!GuiManager.getInstance().openPage(ShopPage.pageId, p)) {
            sender.sendMessage(ChatColor.RED + "An Error Occurred: Could not open shop");
            Bukkit.getLogger().warning("Could not open page -- ensure it is created in main plugin class");
        }

        RespawnListener.setKit(p, true);

        return true;
    }
}
