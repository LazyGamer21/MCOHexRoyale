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

public class WeaponsPage extends AbstractBuyPage {

    public static final String pageId = "shop.buy.weapons";

    public WeaponsPage(JavaPlugin plugin) {
        super(plugin, true, true, BuyPage.pageId, true);
    }

    @Override
    protected String getDisplayName(UUID uuid) {
        return ChatColor.DARK_AQUA + "Weapons Shop";
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
                new ShopItem(new ItemStack(Material.IRON_SHOVEL),
                        shopConfig.getInt("buy.weapons-tools.iron-shovel", 150000), 11),

                new ShopItem(new ItemStack(Material.IRON_PICKAXE),
                        shopConfig.getInt("buy.weapons-tools.iron-pickaxe", 350000), 20),

                new ShopItem(new ItemStack(Material.IRON_AXE),
                        shopConfig.getInt("buy.weapons-tools.iron-axe", 500000), 29),



                new ShopItem(new ItemStack(Material.DIAMOND_SHOVEL),
                        shopConfig.getInt("buy.weapons-tools.diamond-shovel", 500000), 12),

                new ShopItem(new ItemStack(Material.DIAMOND_PICKAXE),
                        shopConfig.getInt("buy.weapons-tools.diamond-pickaxe", 750000), 21),

                new ShopItem(new ItemStack(Material.DIAMOND_AXE),
                        shopConfig.getInt("buy.weapons-tools.diamond-axe", 1500000), 30),




                new ShopItem(new ItemStack(Material.IRON_SWORD),
                        shopConfig.getInt("buy.weapons-tools.iron-sword", 500000), 14),

                new ShopItem(new ItemStack(Material.DIAMOND_SWORD),
                        shopConfig.getInt("buy.weapons-tools.diamond-sword", 1500000), 23),

                new ShopItem(new ItemStack(Material.MACE),
                        shopConfig.getInt("buy.weapons-tools.mace", 1500000), 32),

                new ShopItem(new ItemStack(Material.BOW),
                        shopConfig.getInt("buy.weapons-tools.bow", 500000), 15),

                new ShopItem(new ItemStack(Material.ARROW, 8),
                        shopConfig.getInt("buy.weapons-tools.arrows", 150000), 24),

                new ShopItem(new ItemStack(Material.SHIELD),
                        shopConfig.getInt("buy.weapons-tools.shield", 750000), 33)
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
