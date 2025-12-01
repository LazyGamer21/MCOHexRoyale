package net.mcoasis.mcohexroyale.gui.shop.buy;

import me.ericdavis.lazyItems.AbstractCustomItem;
import me.ericdavis.lazygui.item.GuiItem;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.gui.shop.BuyPage;
import net.mcoasis.mcohexroyale.gui.shop.SellPage;
import net.mcoasis.mcohexroyale.items.CoinPouch;
import net.mcoasis.mcohexroyale.items.TrackingCompass;
import net.mcoasis.mcohexroyale.util.ConfigUtil;
import net.mcoasis.mcohexroyale.util.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
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
    List<ShopItem> getShopItems(UUID uuid) {
        ConfigUtil shopConfigUtil = MCOHexRoyale.getInstance().getShopConfigUtil();
        shopConfigUtil.reload();

        FileConfiguration shopConfig = shopConfigUtil.getConfig();

        int coinPouch1 = 25;
        int coinPouch2 = 100;
        int coinPouch3 = SellPage.coinAmounts.getOrDefault(uuid, 0);

        List<ShopItem> shopItems = new ArrayList<>(Arrays.asList(
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

                new ShopItem(new ItemStack(Material.BLAST_FURNACE),
                        shopConfig.getInt("buy.other.blast-furnace", 15000), 19),

                // --- HORSE BUNDLE ---
                new ShopItem(new ItemStack(Material.SADDLE),
                        shopConfig.getInt("buy.other.horse", 350000), 23) {{
                    setDisplayName(ChatColor.GOLD + "Horse");
                    addExtraItems(
                            new ItemStack(Material.HORSE_SPAWN_EGG)
                    );
                }},

                new ShopItem(AbstractCustomItem.of(TrackingCompass.class),
                        shopConfig.getInt("buy.other.tracking-compass", 1000000), 22)
                        .setDisplayName("Player Tracker"),

                new ShopItem(new ItemStack(Material.CAULDRON),
                        shopConfig.getInt("buy.other.porta-potty", 500000), 24)
                        .setDisplayName("Porta Potty"),

                new ShopItem(new ItemStack(Material.SMOKER),
                        shopConfig.getInt("buy.other.smoker", 5000), 28),

                // coin pouches
                new ShopItem(CoinPouch.of(coinPouch1), coinPouch1, 16)
                        .setDisplayName("Coin Pouch (" + coinPouch1 + ")"),

                new ShopItem(CoinPouch.of(coinPouch2), coinPouch2, 16 + 9)
                        .setDisplayName("Coin Pouch (" + coinPouch2 + ")")

        ));

        if (coinPouch3 > 0) {
            // only show the last pouch (all coins) if they have at least 1 coin
            shopItems.add(new ShopItem(CoinPouch.of(coinPouch3), coinPouch3, 16 + 18)
                    .setDisplayName("Coin Pouch (" + coinPouch3 + ")"));
        }

        return shopItems;
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
