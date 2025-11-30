package net.mcoasis.mcohexroyale.gui.shop.buy;

import me.ericdavis.lazygui.item.GuiItem;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.gui.shop.BuyPage;
import net.mcoasis.mcohexroyale.util.ConfigUtil;
import net.mcoasis.mcohexroyale.util.ShopItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class FoodPage extends AbstractBuyPage {

    public static final String pageId = "shop.buy.food";

    public FoodPage(JavaPlugin plugin) {
        super(plugin, true, true, BuyPage.pageId, true);
    }

    @Override
    protected String getDisplayName(UUID uuid) {
        return ChatColor.DARK_AQUA + "Food Shop";
    }

    @Override
    protected int getRows() {
        return 5;
    }

    protected List<ShopItem> getShopItems() {

        ConfigUtil shopConfigUtil = MCOHexRoyale.getInstance().getShopConfigUtil();
        shopConfigUtil.reload();

        FileConfiguration shopConfig = shopConfigUtil.getConfig();

        return List.of(

                new ShopItem(new ItemStack(Material.COOKED_BEEF, 8),
                        shopConfig.getInt("buy.food.cooked-beef", 100000), 21),

                new ShopItem(new ItemStack(Material.MUSHROOM_STEW),
                        shopConfig.getInt("buy.food.mushroom-stew", 250000), 22),

                new ShopItem(new ItemStack(Material.GOLDEN_APPLE),
                        shopConfig.getInt("buy.food.golden-apple", 500000), 23)
        );

    }

    @Override
    public String getPageIdentifier() {
        return pageId;
    }

    @Override
    protected List<GuiItem> getListedButtons(UUID uuid) {
        return List.of();
    }
}
