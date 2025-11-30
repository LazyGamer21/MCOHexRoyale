package net.mcoasis.mcohexroyale.commands;

import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetTeamCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /setteam <player> <team>

        if (!sender.hasPermission("hexroyale.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect Usage");
            return false;
        }

        String arg1 = ChatColor.stripColor(args[0]);
        String arg2 = ChatColor.stripColor(args[1]);

        Player playerToChange = Bukkit.getPlayer(arg1);
        HexTeam team;
        switch (arg2.toUpperCase()) {
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
            case "NONE":
                team = HexManager.getInstance().getPlayerTeam(playerToChange);
                if (team == null) {
                    sender.sendMessage(ChatColor.RED + "Player " + playerToChange.getDisplayName() + " is not on a team");
                    return true;
                }
                team.getMembersAlive().remove(playerToChange.getUniqueId());
                sender.sendMessage(ChatColor.GREEN + "Successfully removed " + playerToChange.getDisplayName() + " from " + team.getTeamColor().getColor() + team.getTeamColor().getName() + ChatColor.GREEN + " team");
                return true;
            default:
                sender.sendMessage(ChatColor.RED + "Invalid team name \"" + arg2 + "\"");
                return true;
        }

        if (playerToChange == null) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return true;
        }

        team.addMember(playerToChange);
        playerToChange.sendMessage(ChatColor.GRAY + "You are now on " + team.getTeamColor().getName() + " Team");

        sender.sendMessage(ChatColor.GREEN + "Successfully added " + playerToChange.getDisplayName() + " to " + team.getTeamColor().getColor() + team.getTeamColor().getName() + ChatColor.GREEN + " team");

        return true;
    }
}
