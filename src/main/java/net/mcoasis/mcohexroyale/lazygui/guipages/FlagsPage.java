package net.mcoasis.mcohexroyale.lazygui.guipages;

import me.ericdavis.lazyGui.guiPage.AbstractGuiPage;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.lazygui.guiitems.FlagItem;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class FlagsPage extends AbstractGuiPage {

    public static String pageId = "flags";

    public FlagsPage() {
        super(MCOHexRoyale.getInstance(), pageId);

        assignItem(22, new FlagItem("This a flag bro", HexManager.getInstance().getHexTile(0, 0)));

        createInventory();
    }

    @Override
    public String getDisplayName() {
        return ChatColor.BLUE + "Flags";
    }

    @Override
    protected int getRows() {
        return 6;
    }

}
