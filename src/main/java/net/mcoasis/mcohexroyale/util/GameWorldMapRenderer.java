package net.mcoasis.mcohexroyale.util;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GameWorldMapRenderer extends MapRenderer {

    private final BufferedImage image;

    private static final int MIN_X = -500;
    private static final int MAX_X = 500;
    private static final int MIN_Z = -500;
    private static final int MAX_Z = 500;

    public GameWorldMapRenderer(BufferedImage image) {
        super(true); // we draw the entire map manually
        this.image = image;
    }

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {

        // Draw the static map image
        canvas.drawImage(0, 0, image);

        // -------------------------------------------------------
        // DRAW FLAGS (just the circle, no debug squares)
        // -------------------------------------------------------
        for (HexTile tile : HexManager.getInstance().getHexGrid()) {
            int px = worldToPixelX(tile.getFlagLocation().getBlockX());
            int pz = worldToPixelZ(tile.getFlagLocation().getBlockZ());

            Color color = tile.isCurrentTeamOwns() ? tile.getCurrentTeam().getTeamColor().getJavaColor() : Color.LIGHT_GRAY;

            drawCircle(canvas, px, pz, 2, color);
        }

        // -------------------------------------------------------
        // DRAW PLAYER CURSOR (default Minecraft arrow)
        // -------------------------------------------------------
        MapCursorCollection cursors = canvas.getCursors();

        // remove old player cursor (if any)
        for (int i = cursors.size() - 1; i >= 0; i--) {
            MapCursor c = cursors.getCursor(i);
            if (c != null && c.getType() == MapCursor.Type.PLAYER) {
                cursors.removeCursor(c);
            }
        }

        // convert world → pixel
        int playerPx = worldToPixelX(player.getLocation().getBlockX());
        int playerPz = worldToPixelZ(player.getLocation().getBlockZ());

        // convert pixel → cursor coords
        byte cursorX = (byte) ((playerPx - 64) * 2);
        byte cursorZ = (byte) ((playerPz - 64) * 2);

        // rotation
        float yaw = player.getLocation().getYaw();
        if (yaw < 0) yaw += 360;
        byte rotation = (byte) (yaw / 22.5);

        // add cursor
        cursors.addCursor(new MapCursor(
                cursorX,
                cursorZ,
                rotation,
                MapCursor.Type.PLAYER,
                true
        ));
    }

    // ---------------------------------------------
    // Coordinate Conversions
    // ---------------------------------------------

    private static int worldToPixelX(int x) {
        double rel = (x - MIN_X) / 1000.0; // -500 to 500 → 0 to 1
        return (int) (rel * 127);
    }

    private static int worldToPixelZ(int z) {
        double rel = (z - MIN_Z) / 1000.0;
        return (int) (rel * 127);
    }

    // ---------------------------------------------
    // Drawing helpers
    // ---------------------------------------------

    private static void drawCircle(MapCanvas canvas, int centerX, int centerZ, int radius, Color color) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    int px = centerX + x;
                    int pz = centerZ + z;
                    if (px >= 0 && px < 128 && pz >= 0 && pz < 128) {
                        canvas.setPixelColor(px, pz, color);
                    }
                }
            }
        }
    }

    // ---------------------------------------------
    // Give Map to Player
    // ---------------------------------------------

    public static void giveWorldMap(Player player) {
        MapView view = Bukkit.createMap(player.getWorld());
        view.setUnlimitedTracking(true);

        view.setCenterX(0);
        view.setCenterZ(0);
        view.setScale(MapView.Scale.FAR);

        BufferedImage mapImg = MCOHexRoyale.getInstance().getMapImage();
        view.addRenderer(new GameWorldMapRenderer(mapImg));

        ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) mapItem.getItemMeta();
        meta.setMapView(view);
        meta.setDisplayName("§aWorld Map");
        mapItem.setItemMeta(meta);

        player.getInventory().addItem(mapItem);
    }
}
