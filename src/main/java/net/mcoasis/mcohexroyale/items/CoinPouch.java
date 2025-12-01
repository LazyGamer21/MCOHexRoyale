package net.mcoasis.mcohexroyale.items;

import me.ericdavis.lazyItems.AbstractCustomItem;
import me.ericdavis.lazyItems.LazyItems;
import net.mcoasis.mcohexroyale.gui.shop.SellPage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class CoinPouch extends AbstractCustomItem {

    private static final NamespacedKey COINS_KEY = new NamespacedKey(LazyItems.getInstance().getPlugin(), "pouch_coins");

    public CoinPouch() {
        super(
                new NamespacedKey(LazyItems.getInstance().getPlugin(), "coin_pouch"),
                Material.SUNFLOWER,
                0,
                ChatColor.GOLD + "Coin Pouch",
                List.of(ChatColor.GRAY + "Right-click to open")
        );
    }

    // Factory method — use this to create all coin pouches
    public static ItemStack of(int coins) {
        ItemStack item = AbstractCustomItem.of(CoinPouch.class); // Uses the registered item
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        // Store coin amount
        meta.getPersistentDataContainer().set(COINS_KEY, PersistentDataType.INTEGER, coins);

        // Update name & lore
        meta.setDisplayName(ChatColor.GOLD + "Coin Pouch " + ChatColor.YELLOW + "(" + coins + " coins)");

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Right-click to open");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Contains: " + ChatColor.GOLD + ChatColor.BOLD + coins + " coins");
        lore.add(ChatColor.LIGHT_PURPLE + "Share coins with teammates!");
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void onRightClick(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        // First: verify it's our custom item
        if (!AbstractCustomItem.isCustomItem(item, CoinPouch.class)) {
            return; // Not our pouch
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        Integer coins = meta.getPersistentDataContainer().get(COINS_KEY, PersistentDataType.INTEGER);
        if (coins == null || coins <= 0) {
            player.sendMessage(ChatColor.RED + "This coin pouch is empty!");
            return;
        }

        // Give coins
        SellPage.coinAmounts.put(player.getUniqueId(),
                SellPage.coinAmounts.getOrDefault(player.getUniqueId(), 0) + coins);

        player.sendMessage(ChatColor.GREEN + "You opened a coin pouch and received " +
                ChatColor.GOLD + coins + " coins" + ChatColor.GREEN + "!");

        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);

        // Consume one pouch
        if (item.getAmount() <= 1) {
            player.getInventory().setItemInMainHand(null);
        } else {
            item.setAmount(item.getAmount() - 1);
        }
    }
}