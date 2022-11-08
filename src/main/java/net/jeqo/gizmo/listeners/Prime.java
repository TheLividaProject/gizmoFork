package net.jeqo.gizmo.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import net.jeqo.gizmo.Gizmo;
import net.jeqo.gizmo.data.Utilities;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Prime implements Listener {

    Gizmo plugin = Gizmo.getPlugin(Gizmo.class);
    String shift48 = plugin.getConfig().getString("Unicodes.shift-48");
    String shift1013 = plugin.getConfig().getString("Unicodes.shift-1013");
    String shift1536 = plugin.getConfig().getString("Unicodes.shift-1536");

    public static HashMap<String, ItemStack[]> saveInv = new HashMap<>();

    public static Boolean screening;

    @EventHandler
    public void onPackAccept(PlayerResourcePackStatusEvent e) {
        Player p = e.getPlayer();

        if (e.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {

            screening = true;

            if (Objects.equals(plugin.getConfig().getString("Sound-on-Pack-Load.enable"), "true")) {
                p.playSound(p.getLocation(), Sound.valueOf(plugin.getConfig().getString("Sound-on-Pack-Load.sound")), Float.parseFloat(Objects.requireNonNull(plugin.getConfig().getString("Sound-on-Pack-Load.volume"))), Float.parseFloat(Objects.requireNonNull(plugin.getConfig().getString("Sound-on-Pack-Load.pitch"))));
            }


            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                public void run() {

                    saveInv.put(p.getName(), p.getInventory().getContents());
                    p.getInventory().clear();

                    if (Objects.equals(plugin.getConfig().getString("enable-welcome-screen"), "true")) {
                        e.getPlayer().setGameMode(GameMode.SPECTATOR);
                        if (Objects.equals(plugin.getConfig().getString("enable-background"), "true")) {
                            InventoryView screen = e.getPlayer().openInventory(plugin.getServer().createInventory(null, 54, ChatColor.WHITE + shift1013 + plugin.getConfig().getString("Unicodes.background") + shift1536 + plugin.getConfig().getString("Unicodes.welcome-screen")));

                            if (plugin.getConfig().getString("Items") != null) {
                                for (String key : Objects.requireNonNull(plugin.getConfig().getConfigurationSection("Items")).getKeys(false)) {
                                    ConfigurationSection keySection = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("Items")).getConfigurationSection(key);
                                    assert keySection != null;
                                    int slot = keySection.getInt("slot");
                                    ItemStack item = new ItemStack(Objects.requireNonNull(Material.matchMaterial(Objects.requireNonNull(keySection.getString("material")))));
                                    ItemMeta meta = item.getItemMeta();
                                    assert meta != null;
                                    if (plugin.getConfig().getString("Items." + key + ".lore") != null) {
                                        List<String> lore = keySection.getStringList("lore");
                                        for (int i = 0; i < lore.size(); i++) {
                                            lore.set(i, Utilities.hex(lore.get(i)));
                                        }
                                        meta.setLore(lore);
                                    }
                                    meta.setCustomModelData(keySection.getInt("custom-model-data"));
                                    meta.setDisplayName(Utilities.hex(keySection.getString("name")));
                                    item.setItemMeta(meta);
                                    assert screen != null;
                                    screen.setItem(slot, item);
                                }
                            }


                        } else if (Objects.equals(plugin.getConfig().getString("enable-background"), "false")) {
                            InventoryView screenNoBg = e.getPlayer().openInventory(plugin.getServer().createInventory(null, 54, ChatColor.WHITE + shift48 + plugin.getConfig().getString("Unicodes.welcome-screen")));

                            if (plugin.getConfig().getString("Items") != null) {
                                for (String key : Objects.requireNonNull(plugin.getConfig().getConfigurationSection("Items")).getKeys(false)) {
                                    ConfigurationSection keySection = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("Items")).getConfigurationSection(key);
                                    assert keySection != null;
                                    int slot = keySection.getInt("slot");
                                    ItemStack item = new ItemStack(Objects.requireNonNull(Material.matchMaterial(Objects.requireNonNull(keySection.getString("material")))));
                                    ItemMeta meta = item.getItemMeta();
                                    assert meta != null;
                                    if (plugin.getConfig().getString("Items." + key + ".lore") != null) {
                                        List<String> lore = keySection.getStringList("lore");
                                        for (int i = 0; i < lore.size(); i++) {
                                            lore.set(i, Utilities.hex(lore.get(i)));
                                        }
                                        meta.setLore(lore);
                                    }
                                    meta.setCustomModelData(keySection.getInt("custom-model-data"));
                                    meta.setDisplayName(Utilities.hex(keySection.getString("name")));
                                    item.setItemMeta(meta);
                                    assert screenNoBg != null;
                                    screenNoBg.setItem(slot, item);
                                }
                            }

                        }
                    } else {
                        if (Objects.equals(plugin.getConfig().getString("enable-fade"), "true")) {
                            if (Objects.equals(plugin.getConfig().getString("fade-mode"), "A")) {
                                p.sendTitle((String) plugin.getConfig().get("Unicodes.background"), "", 0, 5, plugin.getConfig().getInt("fade-time"));
                            } else if (Objects.equals(plugin.getConfig().getString("fade-mode"), "B")) {
                                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, plugin.getConfig().getInt("fade-time"), 1, false, false));
                            }
                        }
                    }

                }
            }, 3);

        } else if (e.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD || e.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED) {
            p.setGameMode(Protect.joinGm);
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            if (Objects.equals(plugin.getConfig().getString("messages.no-pack-loaded"), "[]")) {
            } else {
                p.sendMessage(Utilities.hex("#ee0000[Gizmo] " + plugin.getConfig().getString("messages.no-pack-loaded")));
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        if (Objects.equals(plugin.getConfig().getString("hide-join-messages"), String.valueOf(true))) {
            e.setJoinMessage("");
        }

    }

    @EventHandler
    public void restoreInv(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();

        if(e.getView().getTitle().equals(ChatColor.WHITE + shift1013 + plugin.getConfig().getString("Unicodes.background") + shift1536 + plugin.getConfig().getString("Unicodes.welcome-screen")) || e.getView().getTitle().equals(ChatColor.WHITE + shift48 + plugin.getConfig().getString("Unicodes.welcome-screen"))) {
            p.getInventory().setContents((ItemStack[]) saveInv.get(p.getName()));
        }
    }
}