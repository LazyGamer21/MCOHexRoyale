package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class RespawnListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        stuff(e.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        stuff(e.getPlayer());
    }

    public static void stuff(Player p) {
        HexTeam team = HexManager.getInstance().getPlayerTeam(p);

        if (team == null) return;

        p.getInventory().clear();
        p.setGameMode(GameMode.SPECTATOR);
        team.getMembersAlive().put(p, false);

        if (team.hasBaseOwnership()) {
            int respawnTimer = MCOHexRoyale.getInstance().getConfig().getInt("respawn-timer", 3);

            p.sendMessage(ChatColor.GRAY + "Respawning in " + respawnTimer + " seconds!");
            Bukkit.getScheduler().runTaskLater(MCOHexRoyale.getInstance(), () -> {
                if (!team.isTeamAlive()) return;

                team.getBaseTile().teleportToBase(p);
                p.setGameMode(GameMode.SURVIVAL);
                setKit(p);
                team.getMembersAlive().put(p, true);
            }, 20L * respawnTimer);

            return;
        }

        p.sendMessage(ChatColor.RED + "Your team does not have their flag! Wait to respawn until it is recaptured!");
    }

    public static void setKit(Player p) {
        HexTeam team = HexManager.getInstance().getPlayerTeam(p);
        if (team == null) {
            Bukkit.getLogger().warning("[HexRoyale] Failed to retrieve " + p.getDisplayName() + "'s team - setting kit");
            return;
        }

        // get how many tiles the player's team owns
        int tilesOwned = HexManager.getInstance().getOwnedTiles(team);

        FileConfiguration config = MCOHexRoyale.getInstance().getConfig();
        int kit2Threshold = config.getInt("kit2-threshold");
        int kit3Threshold = config.getInt("kit3-threshold");
        int kit4Threshold = config.getInt("kit4-threshold");

        if (tilesOwned >= kit4Threshold) {
            giveKit4(p);
        } else if (tilesOwned >= kit3Threshold) {
            giveKit3(p);
        } else if (tilesOwned >= kit2Threshold) {
            giveKit2(p);
        } else {
            giveKit1(p);
        }

        // add curse of binding to all the player's armor
        bindPlayerArmor(p);
        makeInventoryUnbreakable(p);
    }

    private static void makeInventoryUnbreakable(Player player) {
        if (player == null) return;

        PlayerInventory inv = player.getInventory();
        ItemStack[] contents = inv.getContents();

        for (ItemStack item : contents) {
            if (item == null || item.getType() == Material.AIR) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }

        inv.setContents(contents);
    }

    private static void giveKit1(Player p) {
        PlayerInventory inv = p.getInventory();

        inv.clear();

        // set armor
        inv.setHelmet(new ItemStack(Material.LEATHER_HELMET));
        inv.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        inv.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        inv.setBoots(new ItemStack(Material.LEATHER_BOOTS));

        // weapons
        inv.setItem(0, new ItemStack(Material.WOODEN_AXE));
        inv.setItem(1, new ItemStack(Material.WOODEN_SWORD));
        inv.setItem(2, new ItemStack(Material.STONE_PICKAXE));

        // food
        inv.setItem(8, new ItemStack(Material.BREAD, 8));
    }

    private static void giveKit2(Player p) {
        PlayerInventory inv = p.getInventory();

        inv.clear();

        // set armor
        inv.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        inv.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        inv.setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        inv.setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));

        // weapons
        inv.setItem(0, new ItemStack(Material.WOODEN_AXE));
        inv.setItem(1, new ItemStack(Material.WOODEN_SWORD));
        inv.setItem(2, new ItemStack(Material.STONE_PICKAXE));

        // food
        inv.setItem(8, new ItemStack(Material.BREAD, 8));
    }

    private static void giveKit3(Player p) {
        PlayerInventory inv = p.getInventory();

        inv.clear();

        // set armor
        inv.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        inv.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        inv.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
        inv.setBoots(new ItemStack(Material.IRON_BOOTS));

        // weapons
        inv.setItem(0, new ItemStack(Material.STONE_AXE));
        inv.setItem(1, new ItemStack(Material.STONE_SWORD));
        inv.setItem(2, new ItemStack(Material.STONE_PICKAXE));

        // food
        inv.setItem(8, new ItemStack(Material.BREAD, 8));
    }

    private static void giveKit4(Player p) {
        PlayerInventory inv = p.getInventory();

        // prot 2 leggings
        ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
        ArmorMeta leggingsMeta = (ArmorMeta) leggings.getItemMeta();
        leggingsMeta.addEnchant(Enchantment.PROTECTION, 2, false);
        leggings.setItemMeta(leggingsMeta);

        // prot 2 boots
        ItemStack boots = new ItemStack(Material.IRON_BOOTS);
        ArmorMeta bootsMeta = (ArmorMeta) boots.getItemMeta();
        bootsMeta.addEnchant(Enchantment.PROTECTION, 2, false);
        boots.setItemMeta(bootsMeta);

        inv.clear();

        // set armor
        inv.setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
        inv.setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        inv.setLeggings(leggings);
        inv.setBoots(boots);

        // weapons
        inv.setItem(0, new ItemStack(Material.STONE_AXE));
        inv.setItem(1, new ItemStack(Material.STONE_SWORD));
        inv.setItem(2, new ItemStack(Material.STONE_PICKAXE));

        // food
        inv.setItem(8, new ItemStack(Material.BREAD, 8));
    }

    private static void bindPlayerArmor(Player player) {
        if (player == null) return;

        PlayerInventory inv = player.getInventory();
        ItemStack[] armor = inv.getArmorContents();

        for (int i = 0; i < armor.length; i++) {
            ItemStack item = armor[i];
            if (item == null || item.getType() == Material.AIR) continue;

            item.addEnchantment(Enchantment.BINDING_CURSE, 1);
            armor[i] = item;
        }

        inv.setArmorContents(armor);
    }

}