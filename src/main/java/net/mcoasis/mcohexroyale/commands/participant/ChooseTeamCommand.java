package net.mcoasis.mcohexroyale.commands.participant;

import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChooseTeamCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // /chooseteam <team>

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Incorrect Usage: /chooseteam <team>");
            return true;
        }

        if (GameManager.getInstance().getGameState() == GameManager.GameState.STARTING
                || GameManager.getInstance().getGameState() == GameManager.GameState.INGAME) {
            player.sendMessage(ChatColor.RED + "You can't change your team in the middle of the game. Nice try, buster");
            return true;
        }

        String arg1 = ChatColor.stripColor(args[0]);

        HexTeam team;
        switch (arg1.toUpperCase()) {
            case "RED":
                team = HexManager.getInstance().getTeam(HexTeam.TeamColor.RED);
                break;
            case "BLUE":
                team = HexManager.getInstance().getTeam(HexTeam.TeamColor.BLUE);
                break;
            case "GREEN":
                team = HexManager.getInstance().getTeam(HexTeam.TeamColor.GREEN);
                break;
            case "YELLOW":
                team = HexManager.getInstance().getTeam(HexTeam.TeamColor.YELLOW);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Invalid team name \"" + arg1 + "\"");
                return true;
        }

        team.addMember(player);
        player.sendMessage(ChatColor.GRAY + "You are now on " + team.getTeamColor().getName() + " Team");



        return true;
    }
}
