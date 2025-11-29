package net.mcoasis.mcohexroyale.util;

import net.mcoasis.mcohexroyale.hexagonal.HexTile;

import java.util.List;
import java.util.Set;

public class MapUtils {

    public static class Bounds {
        public final int minX, minZ, maxX, maxZ;

        public Bounds(int minX, int minZ, int maxX, int maxZ) {
            this.minX = minX;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxZ = maxZ;
        }
    }

    public static Bounds calculateBounds(Set<HexTile> tiles) {
        if (tiles.isEmpty()) return new Bounds(0, 0, 1000, 1000);

        int minX = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (HexTile tile : tiles) {
            int x = tile.getFlagLocation().getBlockX();
            int z = tile.getFlagLocation().getBlockZ();

            if (x < minX) minX = x;
            if (z < minZ) minZ = z;
            if (x > maxX) maxX = x;
            if (z > maxZ) maxZ = z;
        }

        return new Bounds(minX, minZ, maxX, maxZ);
    }
}

