package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.commands.participant.TeamChatCommand;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    private final TeamChatCommand teamChatCommand;

    public PlayerChatListener(TeamChatCommand teamChatCommand) {
        this.teamChatCommand = teamChatCommand;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        if (!teamChatCommand.getTeamChatPlayers().contains(p.getUniqueId())) return;

        HexTeam team = HexManager.getInstance().getPlayerTeam(p);

        if (team == null) {
            p.sendMessage(ChatColor.GRAY + "You are not on a team - moved to global chat");
            return;
        }

        e.setCancelled(true);
        teamChatCommand.sendTeamMessage(p, team, e.getMessage());
    }

}
