package net.mcoasis.mcohexroyale.datacontainers;

import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import net.mcoasis.mcohexroyale.listeners.PlayerInteractListener;
import org.bukkit.Location;

public class PlayerFlagData {

    Location flagTemp1 = null;
    Location flagTemp2 = null;

    public boolean isSettingPole() {
        return settingPole;
    }

    boolean settingPole = false;

    private PlayerInteractListener.CurrentBlock currentBlock;
    private HexTile hexTile;

    public PlayerFlagData(HexTile hexTile, boolean settingPole) {
        this.currentBlock = PlayerInteractListener.CurrentBlock.FIRST;
        this.hexTile = hexTile;
        this.settingPole = settingPole;
    }

    public PlayerInteractListener.CurrentBlock getCurrentBlock() { return currentBlock; }
    public HexTile getHexTile() { return hexTile; }

    public void setCurrentBlock(PlayerInteractListener.CurrentBlock currentBlock) {
        this.currentBlock = currentBlock;
    }

    public void setHexTile(HexTile hexTile) {
        this.hexTile = hexTile;
    }

    public Location getFlagTemp2() {
        return flagTemp2;
    }

    public void setFlagTemp2(Location flagTemp2) {
        this.flagTemp2 = flagTemp2;
    }

    public Location getFlagTemp1() {
        return flagTemp1;
    }

    public void setFlagTemp1(Location flagTemp1) {
        this.flagTemp1 = flagTemp1;
    }
}
