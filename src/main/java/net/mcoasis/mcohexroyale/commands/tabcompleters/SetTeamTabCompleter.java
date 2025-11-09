package net.mcoasis.mcohexroyale.commands.tabcompleters;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SetTeamTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> completions = new ArrayList<>();

        // /setteam <player> <team>
        if (args.length == 1) {
            // Suggest online player names
            completions.addAll(
                    Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                            .collect(Collectors.toList())
            );
        } else if (args.length == 2) {
            // Suggest available team names
            List<String> teams = List.of("RED", "BLUE", "GREEN", "YELLOW", "NONE");
            completions.addAll(
                    teams.stream()
                            .filter(t -> t.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList())
            );
        }

        return completions;
    }
}

