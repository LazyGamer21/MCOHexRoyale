package net.mcoasis.mcohexroyale.hexagonal;

import org.bukkit.Location;

import java.util.Objects;

public class HexTile {

    private final HexManager hexManager;

    /**
     * The axial coordinates for this {@link HexTile}
     */
    private final int q, r;
    private Location flagLocation;
    private HexTeam currentTeam;

    public HexTile(int q, int r) {
        this.hexManager = HexManager.getInstance();
        this.q = q;
        this.r = r;
        setHex(this);
    }

    public HexTile(int q, int r, HexTeam team) {
        this.hexManager = HexManager.getInstance();
        this.q = q;
        this.r = r;
        this.currentTeam = team;
        setHex(this);
    }

    public HexTile(int q, int r, HexTeam team, Location flagLocation) {
        this.hexManager = HexManager.getInstance();
        this.q = q;
        this.r = r;
        this.currentTeam = team;
        this.flagLocation = flagLocation;
        setHex(this);
    }

    private void setHex(HexTile tile) {
        HexTile oldTile = hexManager.getHexTile(tile.getQ(), tile.getR());
        if (oldTile != null) hexManager.getHexGrid().remove(oldTile);
        hexManager.getHexGrid().add(tile);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HexTile that = (HexTile) o;
        return q == that.q && r == that.r;
    }

    @Override
    public int hashCode() {
        return Objects.hash(q, r);
    }

    // -- == Getters + Setters == --

    public int getQ() {
        return q;
    }

    public int getR() {
        return r;
    }

    public Location getFlagLocation() {
        return flagLocation;
    }

    public HexTeam getCurrentTeam() {
        return currentTeam;
    }
}
