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

public class PerksPage extends AbstractBuyPage {

    public static final String pageId = "shop.buy.perks";

    public PerksPage(JavaPlugin plugin) {
        super(plugin, true, true, BuyPage.pageId, true);
    }

    @Override
    protected String getDisplayName(UUID uuid) {
        return ChatColor.DARK_AQUA + "Perks Shop";
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
                        shopConfig.getInt("buy.IRON_SHOVEL", 150), 11),

                new ShopItem(new ItemStack(Material.IRON_PICKAXE),
                        shopConfig.getInt("buy.IRON_PICKAXE", 250), 20),

                new ShopItem(new ItemStack(Material.IRON_AXE),
                        shopConfig.getInt("buy.IRON_AXE", 500), 29),

                new ShopItem(new ItemStack(Material.COOKED_BEEF, 8),
                        shopConfig.getInt("buy.COOKED_BEEF", 100), 12),

                new ShopItem(new ItemStack(Material.MUSHROOM_STEW),
                        shopConfig.getInt("buy.MUSHROOM_STEW", 250), 21),

                new ShopItem(new ItemStack(Material.GOLDEN_APPLE),
                        shopConfig.getInt("buy.GOLDEN_APPLE", 350), 30),

                // --- PIG BUNDLE ---
                new ShopItem(new ItemStack(Material.CARROT_ON_A_STICK),
                        shopConfig.getInt("buy.CARROT_ON_A_STICK", 250), 13) {{
                    addExtraItems(
                            new ItemStack(Material.SADDLE),
                            new ItemStack(Material.PIG_SPAWN_EGG)
                    );
                }},

                // --- HORSE BUNDLE ---
                new ShopItem(new ItemStack(Material.SADDLE),
                        shopConfig.getInt("buy.SADDLE", 350), 22) {{
                    addExtraItems(
                            new ItemStack(Material.HORSE_SPAWN_EGG)
                    );
                }},

                new ShopItem(new ItemStack(Material.BOW),
                        shopConfig.getInt("buy.BOW", 500), 14),

                new ShopItem(new ItemStack(Material.ARROW, 8),
                        shopConfig.getInt("buy.ARROW", 250), 23),

                new ShopItem(new ItemStack(Material.SHIELD),
                        shopConfig.getInt("buy.SHIELD", 750), 32),

                new ShopItem(new ItemStack(Material.IRON_SWORD),
                        shopConfig.getInt("buy.IRON_SWORD", 250), 15),

                new ShopItem(new ItemStack(Material.DIAMOND_SWORD),
                        shopConfig.getInt("buy.DIAMOND_SWORD", 1000), 24),

                new ShopItem(new ItemStack(Material.BLAST_FURNACE),
                        shopConfig.getInt("buy.BLAST_FURNACE", 15), 33)
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
