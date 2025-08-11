package net.mcoasis.mcohexroyale.lazygui.guiitems;

import me.ericdavis.lazyGui.guiItem.AbstractGuiItem;
import me.ericdavis.lazyGui.guiPage.AbstractGuiPage;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class FlagItem extends AbstractGuiItem {

    private final HexTile tile;

    public FlagItem(String name, HexTile tile) {
        super(Material.WHITE_BANNER, 0, "flag", name);

        this.tile = tile;
    }

    @Override
    public void onClicked(InventoryClickEvent inventoryClickEvent, AbstractGuiPage abstractGuiPage) {
        if (tile == null) {
            inventoryClickEvent.getWhoClicked().sendMessage(ChatColor.RED + "An Error Occurred: The HexTile for this flag does not exist!");
            return;
        }

        Bukkit.broadcastMessage(ChatColor.GRAY + "Flag clicked with axial coordinates (" + ChatColor.AQUA + tile.getQ() + ", " + tile.getR() + ChatColor.GRAY + ")");
    }
}
