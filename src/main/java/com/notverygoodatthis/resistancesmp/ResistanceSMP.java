package com.notverygoodatthis.resistancesmp;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ResistanceSMP extends JavaPlugin implements Listener {
    public static HashMap<String, Integer> playerResistance = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        registerCommands();
        registerRecipes();
        getLogger().info(resistanceText(String.format("Enabling Resistance SMP: Version %s", getDescription().getVersion())));
        List<String> players = new ArrayList<>(playerResistance.keySet());
        List<Integer> resistance = new ArrayList<>(playerResistance.values());
        if(!getConfig().isSet("players")) {
            getConfig().set("players", players);
            getConfig().set("resistance", resistance);
        } else {
            List<String> configPlayers = (List<String>) getConfig().getList("players");
            List<Integer> configResistance = (List<Integer>) getConfig().getList("resistance");
            for (String s : configPlayers) {
                playerResistance.put(s, configResistance.get(configPlayers.indexOf(s)));
            }
        }
    }

    @Override
    public void onDisable() {
        saveToConfig();
        Bukkit.removeRecipe(new NamespacedKey(this, "resistance_book"));
    }

    public static String resistanceText(String text) {
        return String.format("§7%s", text);
    }

    public static int getResistanceForPlayer(String name) {
        return playerResistance.get(name);
    }

    public void updateTablist() {
        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
            String name = String.format("[%d] %s", getResistanceForPlayer(p.getName()), p.getName());
            p.setPlayerListName(name);
        }
        saveToConfig();
    }

    public void saveToConfig() {
        List<String> players = new ArrayList<>(ResistanceSMP.playerResistance.keySet());
        List<Integer> resistance = new ArrayList<>(ResistanceSMP.playerResistance.values());
        getConfig().set("players", players);
        getConfig().set("resistance", resistance);
        saveConfig();
        Bukkit.getLogger().info("Successfully saved the resistances!");
    }

    void registerCommands() {
        getCommand("setresistance").setExecutor(new SetResistanceCommand());
        getCommand("withdraw").setExecutor(new WithdrawCommand());
    }

    void registerRecipes() {
        Bukkit.addRecipe(craftableResBook());
    }

    public static ItemStack getResistanceBook(int amount) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        book.setAmount(amount);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(resistanceText("Resistance book"));
        book.setItemMeta(meta);
        return book;
    }

    public static ItemStack getCraftableResBook(int amount) {
        ItemStack res = new ItemStack(Material.BOOK);
        res.setAmount(amount);
        ItemMeta meta = res.getItemMeta();
        meta.setDisplayName(resistanceText("Craftable Resistance Book"));
        res.setItemMeta(meta);
        return res;
    }

    ShapedRecipe craftableResBook() {
        NamespacedKey key = new NamespacedKey(this, "resistance_book");
        ItemStack book = getCraftableResBook(1);
        ShapedRecipe rec = new ShapedRecipe(key, book);
        rec.shape("DSD", "STS", "DSD");
        rec.setIngredient('D', Material.DIAMOND_BLOCK);
        rec.setIngredient('S', Material.SHIELD);
        rec.setIngredient('T', Material.TOTEM_OF_UNDYING);
        return rec;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(!playerResistance.containsKey(e.getPlayer().getName())) {
            playerResistance.put(e.getPlayer().getName(), 0);
            e.getPlayer().sendMessage(resistanceText("Welcome to the Resistance SMP! You can gain resistance by killing other players, " +
                    "while you will lose resistance upon death. Have fun!"));
            e.getPlayer().discoverRecipe(new NamespacedKey(this, "resistance_book"));
            saveToConfig();
        }
        updateTablist();
    }


    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            ResistancePlayer p = new ResistancePlayer((Player) e.getEntity());
            e.setDamage(e.getDamage() - p.getResistance());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(e.getEntity().getKiller() instanceof Player) {
            ResistancePlayer player = new ResistancePlayer(e.getEntity());
            ResistancePlayer killer = new ResistancePlayer(player.getPlayer().getKiller());
            if(player.getResistance() != -5) {
                player.decrementResistance();
                killer.incrementResistance();
            }
            updateTablist();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getPlayer().getInventory().getItemInMainHand().hasItemMeta() && e.getHand() == EquipmentSlot.HAND) {
            if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                //lord forgive me for this if statement
                if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(getResistanceBook(1).getItemMeta().getDisplayName())) {
                    ResistancePlayer p = new ResistancePlayer(e.getPlayer());
                    if(p.getResistance() != 5) {
                        p.incrementResistance();
                        p.getPlayer().getInventory().getItemInMainHand().setAmount(p.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
                        updateTablist();
                    }
                } else if(e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(getCraftableResBook(1).getItemMeta().getDisplayName())) {
                    ResistancePlayer p = new ResistancePlayer(e.getPlayer());
                    if(p.getResistance() < 0) {
                        p.incrementResistance();
                        updateTablist();
                        p.getPlayer().getInventory().getItemInMainHand().setAmount(p.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);
                    } else {
                        p.getPlayer().sendMessage(resistanceText("You can only use this item to get back to 0 resistance!"));
                    }
                }
            }
        }
    }
}
