package net.mcoasis.mcohexroyale.commands.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChooseTeamTabCompleter implements TabCompleter {

    private static final List<String> TEAMS = Arrays.asList("red", "blue", "green", "yellow");

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {

        // /chooseteam <team>
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            List<String> matches = new ArrayList<>();

            for (String team : TEAMS) {
                if (team.startsWith(input)) {
                    matches.add(team);
                }
            }

            return matches;
        }

        return List.of();
    }
}
