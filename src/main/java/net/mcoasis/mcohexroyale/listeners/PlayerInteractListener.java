package net.mcoasis.mcohexroyale.listeners;

import net.mcoasis.mcohexroyale.commands.subcommands.FlagCommand;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private final FlagCommand flagCommand;

    public PlayerInteractListener(FlagCommand flagCommand) {
        this.flagCommand = flagCommand;
    }

    @EventHandler
    public void onFlagSet(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block clikedBlock = e.getClickedBlock();

        if (clikedBlock == null) return;
        //if (!flagCommand.getFlagSetters().containsKey(player.getUniqueId())) return;


    }

}
