package net.mcoasis.mcohexroyale.managers;

import me.ericdavis.lazyScoreboard.LazyScoreboard;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.events.listeners.custom.HexCaptureListener;
import net.mcoasis.mcohexroyale.gui.shop.SellPage;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;

public class RunnablesManager {

    private static RunnablesManager instance;

    public static RunnablesManager getInstance() {
        if (instance == null) {
            instance = new RunnablesManager();
        }
        return instance;
    }

    private RunnablesManager() {}



    private BukkitTask gameLogicUpdater;
    private BukkitTask warningMessageTask;
    private LazyScoreboard scoreboard;

    public void restartRunnables() {
        restartLogicRunnable();
        restartWarningRunnable();
    }

    public void restartLogicRunnable() {
        MCOHexRoyale plugin = MCOHexRoyale.getInstance();

        if (gameLogicUpdater != null && !gameLogicUpdater.isCancelled()) gameLogicUpdater.cancel();

        plugin.reloadConfig();

        gameLogicUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                updateTiles();
                updateScoreboard();
                GuiManager.getInstance().refreshPages();
            }
        }.runTaskTimer(plugin, 0, plugin.getConfig().getInt("capture-update-timer", 20));
    }

    public void restartWarningRunnable() {
        MCOHexRoyale plugin = MCOHexRoyale.getInstance();

        if (warningMessageTask != null && !warningMessageTask.isCancelled()) warningMessageTask.cancel();

        plugin.reloadConfig();

        warningMessageTask = new BukkitRunnable() {
            @Override
            public void run() {
                sendWarningMessages();
            }
        }.runTaskTimer(MCOHexRoyale.getInstance(), 0, plugin.getConfig().getInt("warning-message-timer", 30));
    }

    void sendWarningMessages() {
        for (HexTeam team : HexManager.getInstance().getTeams()) {
            if (!team.getBaseTile().isBaseAndBeingCaptured()) continue;
            if (!team.isTeamAlive()) continue;
            for (UUID memberId : team.getMembersAlive().keySet()) {
                Player member = Bukkit.getPlayer(memberId);
                // send action bar message and sound effect
                member.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.RED + "Your base is being captured!"));
                Location loc = member.getLocation();

                new BukkitRunnable() {
                    int tick = 0;
                    @Override
                    public void run() {
                        if (tick > 4) {
                            cancel();
                            return;
                        }
                        float pitch = 1.0f - ((tick%2) * 0.2f);
                        member.playSound(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, pitch);
                        tick++;
                    }
                }.runTaskTimer(MCOHexRoyale.getInstance(), 0L, 2L);
            }
        }
    }

    void updateTiles() {
        for (HexTile tile : HexManager.getInstance().getHexGrid()) {
            if (tile.getFlagLocation() == null) continue;
            tile.doCaptureCheck();
            tile.spawnParticles(MCOHexRoyale.getInstance().getConfig().getDouble("capture-distance"));
            tile.updateFlagPosition();
        }
    }

    private void updateScoreboard() {
        if (scoreboard == null) return;

        int lineLength = 30;

        for (Player player : Bukkit.getOnlinePlayers()) {
            // only update scoreboard for players in the game world
            if (player.getWorld() != WorldManager.getInstance().getGameWorld()) {
                scoreboard.removeScoreboard(player);
                continue;
            }

            // sets up scoreboard if it doesn't exist for the player
            scoreboard.setupScoreboard(player);

            scoreboard.addBlankLine(player);

            if (!GameManager.getInstance().isSuddenDeathStarted()) {
                scoreboard.setStat(player, "timer", ChatColor.YELLOW + "Sudden Death: "
                        + ChatColor.WHITE + GameManager.getInstance().getFormattedTime(GameManager.getInstance().getGameTimerSeconds()));


                if (GameManager.getInstance().getMiddleTileSeconds() <= 0) {
                    if (HexCaptureListener.middleTileTeam != null) {
                        int timeleft = HexCaptureListener.getWinTimeLeft();
                        scoreboard.setStat(player, "middle", ChatColor.YELLOW + "Middle Tile: "
                                + HexCaptureListener.middleTileTeam.getTeamColor().getColor() + HexCaptureListener.middleTileTeam.getTeamColor().getName()
                                + ChatColor.WHITE + " - " + ChatColor.WHITE + GameManager.getInstance().getFormattedTime(timeleft));
                    } else {
                        scoreboard.setStat(player, "middle", ChatColor.YELLOW + "Middle Tile: "
                                + ChatColor.WHITE + "Capturable");
                    }
                } else {
                    scoreboard.setStat(player, "middle", ChatColor.YELLOW + "Middle Tile: "
                            + ChatColor.WHITE + GameManager.getInstance().getFormattedTime(GameManager.getInstance().getMiddleTileSeconds()));
                }
            } else {
                scoreboard.setStat(player, "timer", ChatColor.RED + "Sudden Death!");
            }

            scoreboard.addBlankLine(player);

            for (HexTeam team : HexManager.getInstance().getTeams()) {

                String line;
                // if the team is alive, show alive count, else show eliminated
                if (team.isTeamAlive()) {
                    int alive = team.getMembersAlive().isEmpty() ? 0 : (int) team.getMembersAlive().entrySet().stream().filter(Map.Entry::getValue).count();
                    int total = team.getMembersAlive().isEmpty() ? 0 : team.getMembersAlive().size();

                    line = team.getTeamColor().getColor() + team.getTeamColor().getName() + " Alive: " + alive + "/" + total;
                } else {
                    line = team.getTeamColor().getColor() + team.getTeamColor().getName() + ": " + ChatColor.DARK_RED + "ELIMINATED";
                }

                scoreboard.setStat(player, team.getTeamColor().getName(), line, lineLength, true);

            }

            scoreboard.addBlankLine(player);

            int coins = SellPage.coinAmounts.getOrDefault(player.getUniqueId(), 0);
            if (coins == 0) {
                scoreboard.setStat(player, "wallet", ChatColor.GRAY + "Wallet: " + ChatColor.GOLD + "You're Broke");
            } else {
                scoreboard.setStat(player, "wallet", ChatColor.GRAY + "Wallet: "
                        + ChatColor.GOLD + SellPage.coinAmounts.get(player.getUniqueId()) + " Coins");
            }

            HexTeam playerTeam = HexManager.getInstance().getPlayerTeam(player);
            if (playerTeam != null) {
                HexTeam.TeamColor teamColor = playerTeam.getTeamColor();
                scoreboard.setStat(player, "playerTeam", ChatColor.GRAY + "Your Team: " + teamColor.getColor() + teamColor.getName());
            }



            scoreboard.addBlankLine(player);

            scoreboard.setStat(player, "endLine", ChatColor.GOLD + "" + ChatColor.BOLD + "-+--------------+-");

            scoreboard.updateStats(player);
        }
    }

    public LazyScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public void setScoreboard(LazyScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

}
