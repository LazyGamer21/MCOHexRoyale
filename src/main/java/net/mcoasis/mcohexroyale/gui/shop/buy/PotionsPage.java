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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PotionsPage extends AbstractBuyPage {

    public static final String pageId = "shop.buy.potions";

    public PotionsPage(JavaPlugin plugin) {
        super(plugin, true, true, BuyPage.pageId, true);
    }

    @Override
    protected String getDisplayName(UUID uuid) {
        return ChatColor.DARK_AQUA + "Potions Shop";
    }

    @Override
    protected int getRows() {
        return 5;
    }

    protected List<ShopItem> getShopItems() {
        ConfigUtil shopConfigUtil = MCOHexRoyale.getInstance().getShopConfigUtil();
        shopConfigUtil.reload();
        FileConfiguration shopConfig = shopConfigUtil.getConfig();

        List<ShopItem> items = new ArrayList<>();

        // Strength I-III
        for (int level = 1; level <= 3; level++) {
            ItemStack potion = createPotion(PotionEffectType.STRENGTH, level);
            items.add(new ShopItem(potion,
                    shopConfig.getInt("buy.potions.strength." + level, 100000 * level),
                    11 + ((level-1) * 9)));
        }

        // Speed I-III
        for (int level = 1; level <= 3; level++) {
            ItemStack potion = createPotion(PotionEffectType.SPEED, level);
            items.add(new ShopItem(potion,
                    shopConfig.getInt("buy.potions.speed." + level, 75000 * level),
                    12 + ((level-1) * 9)));
        }

        // Regeneration I-II
        for (int level = 1; level <= 2; level++) {
            ItemStack potion = createPotion(PotionEffectType.REGENERATION, level);
            items.add(new ShopItem(potion,
                    shopConfig.getInt("buy.potions.regeneration." + level, 200000 * level),
                    14 + ((level-1) * 9)));
        }

        // Jump Boost I-II
        for (int level = 1; level <= 2; level++) {
            ItemStack potion = createPotion(PotionEffectType.JUMP_BOOST, level);
            items.add(new ShopItem(potion,
                    shopConfig.getInt("buy.potions.jump." + level, 50000 * level),
                    15 + ((level-1) * 9)));
        }

        return items;
    }

    private ItemStack createPotion(PotionEffectType type, int level) {
        ItemStack potion = new ItemStack(Material.SPLASH_POTION, 1); // splash by default
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + type.getName() + " " + level);
        meta.addCustomEffect(new PotionEffect(type, 20 * 60 * 3, level - 1), true); // 3 minutes
        potion.setItemMeta(meta);
        return potion;
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
