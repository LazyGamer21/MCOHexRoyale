package net.mcoasis.mcohexroyale.gui.shop;

import me.clip.placeholderapi.libs.kyori.adventure.text.Component;
import me.clip.placeholderapi.libs.kyori.adventure.text.format.NamedTextColor;
import me.ericdavis.lazygui.item.GuiItem;
import me.ericdavis.lazygui.test.AbstractGuiPage;
import me.ericdavis.lazygui.test.GuiManager;
import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.gui.ShopPage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class BuyPage extends AbstractGuiPage {

    public static String pageId = "shop.buy";

    public BuyPage(JavaPlugin plugin) {
        super(plugin, true, false, ShopPage.pageId, true);
    }

    @Override
    protected String getDisplayName(UUID uuid) {
        return ChatColor.DARK_AQUA + "Buy Shop";
    }

    @Override
    protected int getRows() {
        return 5;
    }







    @Override
    protected void assignItems(UUID uuid) {
        column1(uuid);
        column2(uuid);
        column3(uuid);
        column4(uuid);
        column5(uuid);

        // wallet viewer
        int coins = SellPage.coinAmounts.getOrDefault(uuid, 0);
        String endWord = coins > 1 || coins == 0 ? " Coins" : " Coin";
        assignItem(uuid, 18, new GuiItem(Material.KELP, e -> {
        }).setName(ChatColor.GOLD + "Wallet: " + coins + endWord));
    }

    private void column1(UUID uuid) {
        int shovelCost = 150;
        assignItem(uuid, 11, new GuiItem(Material.IRON_SHOVEL, e -> {
            Player p = (Player) e.getWhoClicked();
            if (!buyItem(p, shovelCost)) return;
            p.sendMessage(ChatColor.GREEN + "Purchased Iron Shovel for " + shovelCost + " coins!");
            p.getInventory().addItem(new ItemStack(Material.IRON_SHOVEL, 1));
        }).setName(ChatColor.GOLD + "Buy Iron Shovel")
                .setLore("Cost: " + shovelCost));

        int pickaxeCost = 250;
        assignItem(uuid, 20, new GuiItem(Material.IRON_PICKAXE, e -> {
            Player p = (Player) e.getWhoClicked();
            if (!buyItem(p, pickaxeCost)) return;
            p.sendMessage(ChatColor.GREEN + "Purchased Iron Pickaxe for " + pickaxeCost + " coins!");
            p.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE, 1));
        }).setName(ChatColor.GOLD + "Buy Iron Pickaxe")
                .setLore("Cost: " + pickaxeCost));

        int axeCost = 500;
        assignItem(uuid, 29, new GuiItem(Material.IRON_AXE, e -> {
            Player p = (Player) e.getWhoClicked();
            if (!buyItem(p, axeCost)) return;
            p.sendMessage(ChatColor.GREEN + "Purchased Iron Axe for " + axeCost + " coins!");
            p.getInventory().addItem(new ItemStack(Material.IRON_AXE, 1));
        }).setName(ChatColor.GOLD + "Buy Iron Axe")
                .setLore("Cost: " + axeCost));
    }

    private void column2(UUID uuid) {
        int steakCost = 100;
        assignItem(uuid, 12, new GuiItem(new ItemStack(Material.COOKED_BEEF, 8), e -> {
            Player p = (Player) e.getWhoClicked();
            if (!buyItem(p, steakCost)) return;
            p.sendMessage(ChatColor.GREEN + "Purchased 8 Cooked Beef for " + steakCost + " coins!");
            p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 8));
        }).setName(ChatColor.GOLD + "Buy 8 Cooked Beef")
                .setLore("Cost: " + steakCost));

        int mushroomStewCost = 250;
        assignItem(uuid, 21, new GuiItem(Material.MUSHROOM_STEW, e -> {
            Player p = (Player) e.getWhoClicked();
            if (!buyItem(p, mushroomStewCost)) return;
            p.sendMessage(ChatColor.GREEN + "Purchased Mushroom Stew for " + mushroomStewCost + " coins!");
            p.getInventory().addItem(new ItemStack(Material.MUSHROOM_STEW));
        }).setName(ChatColor.GOLD + "Buy Mushroom Stew")
                .setLore("Cost: " + mushroomStewCost));

        int goldenAppleCost = 350;
        assignItem(uuid, 30, new GuiItem(Material.GOLDEN_APPLE, e -> {
            Player p = (Player) e.getWhoClicked();
            if (!buyItem(p, goldenAppleCost)) return;
            p.sendMessage(ChatColor.GREEN + "Purchased Golden Apple for " + goldenAppleCost + " coins!");
            p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
        }).setName(ChatColor.GOLD + "Buy Golden Apple")
                .setLore("Cost: " + goldenAppleCost));
    }

    private void column3(UUID uuid) {
        int pigCost = 250;
        assignItem(uuid, 13, new GuiItem(Material.CARROT_ON_A_STICK, e -> {
            Player p = (Player) e.getWhoClicked();
            if (!buyItem(p, pigCost)) return;
            p.sendMessage(ChatColor.GREEN + "Purchased Pig for " + pigCost + " coins!");
            p.getInventory().addItem(new ItemStack(Material.PIG_SPAWN_EGG));
            p.getInventory().addItem(new ItemStack(Material.SADDLE));
            p.getInventory().addItem(new ItemStack(Material.CARROT_ON_A_STICK));
        }).setName(ChatColor.GOLD + "Buy Pig + Saddle + Carrot on a Stick")
                .setLore("Cost: " + pigCost));

        int horseCost = 500;
        assignItem(uuid, 22, new GuiItem(Material.SADDLE, e -> {
            Player p = (Player) e.getWhoClicked();
            if (!buyItem(p, horseCost)) return;
            p.sendMessage(ChatColor.GREEN + "Purchased Horse for " + horseCost + " coins!");
            p.getInventory().addItem(new ItemStack(Material.HORSE_SPAWN_EGG));
            p.getInventory().addItem(new ItemStack(Material.SADDLE));
        }).setName(ChatColor.GOLD + "Buy Horse + Saddle")
                .setLore("Cost: " + horseCost));
    }

    private void column4(UUID uuid) {
        int bowCost = 750;
        assignItem(uuid, 14, new GuiItem(Material.BOW, e -> {
            Player p = (Player) e.getWhoClicked();
            if (!buyItem(p, bowCost)) return;
            p.sendMessage(ChatColor.GREEN + "Purchased Bow for " + bowCost + " coins!");
            p.getInventory().addItem(new ItemStack(Material.BOW));
        }).setName(ChatColor.GOLD + "Buy Bow")
                .setLore("Cost: " + bowCost));

        int arrowsCost = 250;
        assignItem(uuid, 23, new GuiItem(new ItemStack(Material.ARROW, 8), e -> {
            Player p = (Player) e.getWhoClicked();
            if (!buyItem(p, arrowsCost)) return;
            p.sendMessage(ChatColor.GREEN + "Purchased 8 Arrows for " + arrowsCost + " coins!");
            p.getInventory().addItem(new ItemStack(Material.ARROW, 8));
        }).setName(ChatColor.GOLD + "Buy 8 Arrows")
                .setLore("Cost: " + arrowsCost));

        int shieldCost = 750;
        assignItem(uuid, 32, new GuiItem(Material.SHIELD, e -> {
            Player p = (Player) e.getWhoClicked();
            if (!buyItem(p, shieldCost)) return;
            p.sendMessage(ChatColor.GREEN + "Purchased Shield for " + shieldCost + " coins!");
            p.getInventory().addItem(new ItemStack(Material.SHIELD));
        }).setName(ChatColor.GOLD + "Buy Shield")
                .setLore("Cost: " + shieldCost));
    }

    private void column5(UUID uuid) {
        int ironSwordCost = 500;
        assignItem(uuid, 15, new GuiItem(Material.IRON_SWORD, e -> {
            Player p = (Player) e.getWhoClicked();
            if (!buyItem(p, ironSwordCost)) return;
            p.sendMessage(ChatColor.GREEN + "Purchased Iron Sword for " + ironSwordCost + " coins!");
            p.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
        }).setName(ChatColor.GOLD + "Buy Iron Sword")
                .setLore("Cost: " + ironSwordCost));

        int diamondSwordCost = 1000;
        assignItem(uuid, 24, new GuiItem(Material.DIAMOND_SWORD, e -> {
            Player p = (Player) e.getWhoClicked();
            if (!buyItem(p, diamondSwordCost)) return;
            p.sendMessage(ChatColor.GREEN + "Purchased Diamond Sword for " + diamondSwordCost + " coins!");
            p.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
        }).setName(ChatColor.GOLD + "Buy Diamond Sword")
                .setLore("Cost: " + diamondSwordCost));

        int furnaceCost = 30;
        assignItem(uuid, 33, new GuiItem(Material.FURNACE, e -> {
            Player p = (Player) e.getWhoClicked();
            if (!buyItem(p, furnaceCost)) return;
            p.sendMessage(ChatColor.GREEN + "Purchased Furnace for " + furnaceCost + " coins!");
            p.getInventory().addItem(new ItemStack(Material.FURNACE));
        }).setName(ChatColor.GOLD + "Buy Furnace")
                .setLore("Cost: " + furnaceCost));
    }

    private boolean buyItem(Player p, int cost) {
        int playerCoins = SellPage.coinAmounts.get(p.getUniqueId());
        if (playerCoins < cost) {
            p.sendMessage(ChatColor.RED + "You don't have enough coins to buy that!");
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return false;
        }
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        SellPage.coinAmounts.put(p.getUniqueId(), playerCoins - cost);
        GuiManager.getInstance().refreshPages();
        return true;
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
