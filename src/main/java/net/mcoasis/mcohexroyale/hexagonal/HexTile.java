package net.mcoasis.mcohexroyale.hexagonal;

import org.bukkit.Location;

public class HexTile {

    public HexCoordinate getHexCoordinate() {
        return hexCoordinate;
    }

    public Location getFlagLocation() {
        return flagLocation;
    }

    public HexTeam.TeamColors getCurrentTeam() {
        return currentTeam;
    }

    private HexCoordinate hexCoordinate;
    private Location flagLocation;
    private HexTeam.TeamColors currentTeam;

    //! not used for anything right now but will give it a HexCoordinate and use it in setHex
    public HexTile(int q, int r) {
        HexTileManager.getInstance().setHex(q, r, null);
    }

    public HexTile(int q, int r, HexTeam.TeamColors teamColor) {
        HexTileManager.getInstance().setHex(q, r, null);
        this.currentTeam = teamColor;
    }

    public HexTile(int q, int r, HexTeam.TeamColors teamColor, Location flagLocation) {
        HexTileManager.getInstance().setHex(q, r, null);
        this.currentTeam = teamColor;
        this.flagLocation = flagLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HexTile hexTile = (HexTile) o;
        return hexCoordinate.equals(hexTile.hexCoordinate);
    }

    @Override
    public int hashCode() {
        return hexCoordinate.hashCode();
    }

}
