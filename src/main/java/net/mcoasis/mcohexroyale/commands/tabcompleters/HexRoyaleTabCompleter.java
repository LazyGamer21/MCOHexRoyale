package net.mcoasis.mcohexroyale.commands.tabcompleters;

import net.mcoasis.mcohexroyale.commands.HexRoyaleCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import net.mcoasis.mcohexroyale.commands.subcommands.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HexRoyaleTabCompleter implements TabCompleter {
    private final HexRoyaleCommand commandExecutor;

    public HexRoyaleTabCompleter(HexRoyaleCommand commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Map<String, SubCommand> subCommands = commandExecutor.getSubCommands();

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            return subCommands.keySet().stream()
                    .filter(subCommand -> subCommand.startsWith(input))
                    .collect(Collectors.toList());
        }

        if (args.length > 1) {
            String subCommandName = args[0].toLowerCase();
            SubCommand subCommand = subCommands.get(subCommandName);
            if (subCommand != null) {
                String[] subArgs = new String[args.length - 1];
                System.arraycopy(args, 1, subArgs, 0, args.length - 1);
                return subCommand.onTabComplete(sender, subArgs);
            }
        }

        return new ArrayList<>();
    }
}
