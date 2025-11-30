package net.mcoasis.mcohexroyale.events.listeners;

import net.mcoasis.mcohexroyale.MCOHexRoyale;
import net.mcoasis.mcohexroyale.hexagonal.HexManager;
import net.mcoasis.mcohexroyale.hexagonal.HexTeam;
import net.mcoasis.mcohexroyale.hexagonal.HexTile;
import net.mcoasis.mcohexroyale.managers.GameManager;
import net.mcoasis.mcohexroyale.managers.WorldManager;
import net.mcoasis.mcohexroyale.util.GameWorldMapRenderer;
import org.bukkit.*;
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
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RespawnListener implements Listener {

    public static Set<UUID> playerRespawning = new java.util.HashSet<>();

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

        if (team == null || GameManager.getInstance().getGameState() == GameManager.GameState.LOBBY) {
            if (GameManager.getInstance().getGameState() == GameManager.GameState.LOBBY) {
                p.teleport(WorldManager.getInstance().getLobbyWorld().getSpawnLocation());
                return;
            }
            return;
        }

        p.getInventory().clear();
        Bukkit.getScheduler().runTaskLater(MCOHexRoyale.getInstance(), () -> p.setGameMode(GameMode.SPECTATOR), 1L);
        Bukkit.getScheduler().runTaskLater(MCOHexRoyale.getInstance(), () -> team.getBaseTile().teleportToBase(p, true), 1L);
        team.getMembersAlive().put(p.getUniqueId(), false);

        if (team.hasBaseOwnership()) {
            int respawnTimer = MCOHexRoyale.getInstance().getConfig().getInt("respawn-timer", 3);

            playerRespawning.add(p.getUniqueId());

            p.sendMessage(ChatColor.GRAY + "Respawning in " + respawnTimer + " seconds!");
            Bukkit.getScheduler().runTaskLater(MCOHexRoyale.getInstance(), () -> {
                if (!playerRespawning.contains(p.getUniqueId())) return;
                if (!team.isTeamAlive()) return;
                p.setGameMode(GameMode.SURVIVAL);
                team.getBaseTile().teleportToBase(p, true);
                setKit(p);
                team.getMembersAlive().put(p.getUniqueId(), true);
                playerRespawning.remove(p.getUniqueId());
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
        dyeArmor(p);

        GameWorldMapRenderer.giveWorldMap(p);
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

        clearArmorAndTools(p);

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

        clearArmorAndTools(p);

        // set armor
        inv.setHelmet(new ItemStack(Material.LEATHER_HELMET));
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

        clearArmorAndTools(p);

        // set armor
        inv.setHelmet(new ItemStack(Material.LEATHER_HELMET));
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
        inv.setHelmet(new ItemStack(Material.LEATHER_HELMET));
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

    public static void clearArmorAndTools(Player p) {
        PlayerInventory inv = p.getInventory();

        // clear armor pieces if they are kit items
        if (inv.getHelmet() != null && KIT_ITEMS.contains(inv.getHelmet().getType()))
            inv.setHelmet(null);
        if (inv.getChestplate() != null && KIT_ITEMS.contains(inv.getChestplate().getType()))
            inv.setChestplate(null);
        if (inv.getLeggings() != null && KIT_ITEMS.contains(inv.getLeggings().getType()))
            inv.setLeggings(null);
        if (inv.getBoots() != null && KIT_ITEMS.contains(inv.getBoots().getType()))
            inv.setBoots(null);

        // clear inventory items that match kit items
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && KIT_ITEMS.contains(item.getType())) {
                inv.setItem(i, null);
            }
        }
    }

    private static final Set<Material> KIT_ITEMS = Set.of(
            // armor
            Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
            Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
            Material.IRON_LEGGINGS, Material.IRON_BOOTS,

            // tools / weapons
            Material.WOODEN_AXE, Material.WOODEN_SWORD, Material.STONE_PICKAXE,
            Material.STONE_AXE, Material.STONE_SWORD,

            // food
            Material.BREAD
    );

    private static void dyeArmor(Player p) {
        // dye armor based on team color
        HexTeam playerTeam = HexManager.getInstance().getPlayerTeam(p);
        if (playerTeam == null) return;

        // TeamColor - RED, BLUE, GREEN, YELLOW
        Color dyeColor = playerTeam.getTeamColor().getBukkitColor();

        // dye all leather armor based on the teamColor
        // Armor slots
        ItemStack[] armor = p.getInventory().getArmorContents();

        for (int i = 0; i < armor.length; i++) {
            ItemStack piece = armor[i];

            // Only dye leather armor
            if (piece != null && piece.getItemMeta() instanceof LeatherArmorMeta meta) {
                meta.setColor(dyeColor);
                piece.setItemMeta(meta);
                armor[i] = piece;
            }
        }

        // Apply updated armor back onto player
        p.getInventory().setArmorContents(armor);
    }


}