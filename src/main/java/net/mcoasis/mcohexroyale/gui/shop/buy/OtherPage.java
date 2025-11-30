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

public class OtherPage extends AbstractBuyPage{

    public static final String pageId = "shop.buy.other";

    public OtherPage(JavaPlugin plugin) {
        super(plugin, true, true, BuyPage.pageId, true);
    }

    @Override
    protected String getDisplayName(UUID uuid) {
        return ChatColor.DARK_AQUA + "Other Shop";
    }

    @Override
    protected int getRows() {
        return 5;
    }

    @Override
    List<ShopItem> getShopItems() {
        ConfigUtil shopConfigUtil = MCOHexRoyale.getInstance().getShopConfigUtil();
        shopConfigUtil.reload();

        FileConfiguration shopConfig = shopConfigUtil.getConfig();

        return List.of(

                // --- PIG BUNDLE ---
                new ShopItem(new ItemStack(Material.CARROT_ON_A_STICK),
                        shopConfig.getInt("buy.other.pig", 250000), 21)
                {{
                    setDisplayName("Pig");
                    addExtraItems(
                            new ItemStack(Material.SADDLE),
                            new ItemStack(Material.PIG_SPAWN_EGG)
                    );
                }},

                new ShopItem(new ItemStack(Material.FURNACE),
                        shopConfig.getInt("buy.other.furnace", 5000), 10),

                new ShopItem(new ItemStack(Material.CAULDRON),
                        shopConfig.getInt("buy.other.porta-potty", 500000), 22),

                new ShopItem(new ItemStack(Material.BLAST_FURNACE),
                        shopConfig.getInt("buy.other.blast-furnace", 15000), 19),

                new ShopItem(new ItemStack(Material.SMOKER),
                        shopConfig.getInt("buy.other.smoker", 5000), 28),

                // --- HORSE BUNDLE ---
                new ShopItem(new ItemStack(Material.SADDLE),
                        shopConfig.getInt("buy.other.horse", 350000), 23) {{
                    setDisplayName(ChatColor.GOLD + "Horse");
                    addExtraItems(
                            new ItemStack(Material.HORSE_SPAWN_EGG)
                    );
                }}

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
