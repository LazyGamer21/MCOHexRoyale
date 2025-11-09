package net.mcoasis.mcohexroyale;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TeamPlaceholder extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "mcohexroyale"; // %mcohexroyale_<placeholder>%
    }

    @Override
    public @NotNull String getAuthor() {
        return "RandomStuff";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";

        if (identifier.equalsIgnoreCase("team")) {
            HexTeam team = HexManager.getInstance().getPlayerTeam(player);
            if (team == null) return "";
            return team.getTeamColor().getColor() + "[" + team.getTeamColor().getName() + "]";
        }

        return null; // placeholder not handled
    }
}

