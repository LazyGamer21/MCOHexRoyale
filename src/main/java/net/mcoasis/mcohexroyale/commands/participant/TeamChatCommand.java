package net.mcoasis.mcohexroyale.commands.participant;

import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TeamChatCommand implements CommandExecutor {

    private Set<UUID> teamChatPlayers = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player p)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        HexTeam team = HexManager.getInstance().getPlayerTeam(p);

        if (team == null) {
            p.sendMessage(ChatColor.RED + "You cannot use teamchat - You are not on a team");
            return true;
        }

        if (args.length > 0) {
            String message = String.join(" ", args);

            sendTeamMessage(p, team, message);

            return true;
        }

        if (teamChatPlayers.contains(p.getUniqueId())) {
            teamChatPlayers.remove(p.getUniqueId());
            p.sendMessage(ChatColor.GREEN + "Moved to global chat");
            return true;
        }

        teamChatPlayers.add(p.getUniqueId());
        p.sendMessage(team.getTeamColor().getColor() + "Moved to team chat");

        return true;
    }

    public void sendTeamMessage(Player sender, HexTeam team, String message) {
        Set<Player> members = team.getMembersAlive().keySet();

        for (Player teammate : members) {
            if (teammate != null && teammate.isOnline()) {
                teammate.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[Team] " + ChatColor.RESET + team.getTeamColor().getColor() + sender.getName() + ": " + ChatColor.GRAY + message);
            }
        }
    }

    public Set<UUID> getTeamChatPlayers() { return this.teamChatPlayers; }
}
