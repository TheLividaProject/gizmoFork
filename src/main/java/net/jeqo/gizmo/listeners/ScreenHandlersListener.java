package net.jeqo.gizmo.listeners;

import net.jeqo.gizmo.Events.PlayerProcessedEvent;
import net.jeqo.gizmo.Gizmo;
import net.jeqo.gizmo.Utils.ColourUtils;
import net.jeqo.gizmo.Utils.ItemUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;

public class ScreenHandlersListener implements Listener {

    private final Gizmo plugin;

    private final ItemUtils itemUtils = new ItemUtils();
    private final ColourUtils colourUtils = new ColourUtils();

    public ScreenHandlersListener(Gizmo plugin) {
        this.plugin = plugin;
    }

    // Player join handlers
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Check and give blindness effect
        if (!plugin.configManager.getConfig().getBoolean("blindness-during-prompt")) return;
        event.getPlayer().addPotionEffect(PotionEffectType.BLINDNESS.createEffect(999999, 1));
    }

    // Resource pack status handler
    @EventHandler
    public void onPackLoad(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();

        if (plugin.configManager.getConfig().getBoolean("resource-pack.kick-on-decline")) {
            if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
                disableEffects(player);
            } else if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED || event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
                disableEffects(player);
                player.kick(colourUtils.miniFormat(plugin.configManager.getConfig().getString("resource-pack.kick-on-decline")));
            }
        } else if (!plugin.configManager.getConfig().getBoolean("resource-pack.kick-on-decline")) {
            if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
                disableEffects(player);
            } else if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED || event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
                disableEffects(player);
                for (String msg : plugin.configManager.getConfig().getStringList("resource-pack.no-pack-loaded")) {
                    player.sendMessage(colourUtils.miniFormat(msg));
                }
            }
        }
    }

    @EventHandler
    public void onProcess(PlayerProcessedEvent event) {
        Player player = event.getPlayer();
        String storedInventory = plugin.screeningManager.playersStoredInventory.get(player.getUniqueId());
        if (storedInventory == null) return;

        try {
            player.getInventory().setContents(itemUtils.itemStackArrayFromBase64(storedInventory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String storedInventory = plugin.screeningManager.playersStoredInventory.get(player.getUniqueId());
        if (storedInventory == null) return;

        try {
            player.getInventory().setContents(itemUtils.itemStackArrayFromBase64(storedInventory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Disable all potion effects
    private void disableEffects(Player player) {
        for (PotionEffectType effect : PotionEffectType.values()) {
            if (!player.hasPotionEffect(effect)) continue;
            player.removePotionEffect(effect);
        }
    }

    // Disabled events while screen is active
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (plugin.screeningManager.playersScreenActive.get(player.getUniqueId()) == null) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player player)) return;
        if (plugin.screeningManager.playersScreenActive.get(player.getUniqueId()) == null) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onSlotClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (plugin.screeningManager.playersScreenActive.get(player.getUniqueId()) == null) return;

        event.setCancelled(true);
    }

    // Toggleable damage events
    @EventHandler
    public void onPlayerDamage(EntityDamageByBlockEvent event) {
        Entity entity = event.getEntity();

        if (!plugin.configManager.getConfig().getBoolean("player-invulnerable-during-load")) return;

        if (!(entity instanceof Player player)) return;
        if (plugin.screeningManager.playersScreenActive.get(player.getUniqueId()) == null) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        if (!plugin.configManager.getConfig().getBoolean("player-invulnerable-during-load")) return;

        if (!(entity instanceof Player player)) return;
        if (plugin.screeningManager.playersScreenActive.get(player.getUniqueId()) == null) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerItemDamage(PlayerItemDamageEvent event) {
        if (!plugin.configManager.getConfig().getBoolean("player-invulnerable-during-load")) return;

        Player player = event.getPlayer();
        if (plugin.screeningManager.playersScreenActive.get(player.getUniqueId()) == null) return;

        event.setCancelled(true);
    }
}
