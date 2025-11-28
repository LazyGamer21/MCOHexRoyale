package net.mcoasis.mcohexroyale.commands;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetMiddleSpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (!p.hasPermission("hexroyale.admin")) {
            p.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length != 0) {
            p.sendMessage(ChatColor.RED + "Usage: /setmiddlespawn");
            return true;
        }

        p.sendMessage(ChatColor.GREEN + "Middle spawn set to your current location.");
        saveLocation(MCOHexRoyale.getInstance().getConfig(), "middle-tile-spawn", p.getLocation());
        MCOHexRoyale.getInstance().saveConfig();

        return true;
    }

    public static void saveLocation(FileConfiguration config, String path, Location loc) {
        if (loc == null) return;

        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
    }
}

