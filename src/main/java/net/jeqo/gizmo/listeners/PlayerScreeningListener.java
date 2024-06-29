package net.jeqo.gizmo.listeners;

import net.jeqo.gizmo.GUI.WelcomeScreenMenu;
import net.jeqo.gizmo.Gizmo;
import net.jeqo.gizmo.Managers.GUI.GUIManager;
import net.jeqo.gizmo.Utils.ColourUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class PlayerScreeningListener implements Listener {

    private final Gizmo plugin;

    private final HashMap<UUID, String> playerTracker = new HashMap<>();

    private final ColourUtils colourUtils = new ColourUtils();

    public PlayerScreeningListener(Gizmo plugin) {
        this.plugin = plugin;
    }

    // Resource pack status event
    @EventHandler
    public void onPackAccept(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();

        switch (event.getStatus()) {
            case ACCEPTED:
                // Display the background unicode during the delay
                if (!plugin.configManager.getConfig().getBoolean("delay-background")) return;
                player.sendTitle(plugin.configManager.getConfig().getString("background-color") + plugin.configManager.getScreens().getString("Unicodes.background"), "", 0, 999999, 0);
                break;
            case SUCCESSFULLY_LOADED:
                // Play a configured sound when the pack is loaded
                try {
                    if (plugin.configManager.getConfig().getBoolean("sound-on-pack-load.enable")) {
                        String soundID = plugin.configManager.getConfig().getString("sound-on-pack-load.sound");
                        float soundVolume = Float.parseFloat(plugin.configManager.getConfig().getString("sound-on-pack-load.volume"));
                        float soundPitch = Float.parseFloat(plugin.configManager.getConfig().getString("sound-on-pack-load.pitch"));

                        try {
                            Sound sound = Sound.valueOf(soundID.toUpperCase());
                            player.playSound(player.getLocation(), sound, soundVolume, soundPitch);
                        } catch (IllegalArgumentException err) {
                            player.playSound(player.getLocation(), soundID, soundVolume, soundPitch);
                        }
                    }
                } catch (NullPointerException ex) {
                    plugin.getLogger().warning("sound-on-pack is not configured correctly.");
                }

                // Display first time welcome screen
                if (!player.hasPlayedBefore()) {
                    if (plugin.configManager.getScreens().getBoolean("enable-first-join-welcome-screen")) {
                        plugin.screeningManager.displayScreen(player, "First-Join-Items", "enable-first-join-welcome-screen");
                        return;
                    }
                }

                // Display the screen once per restart
                if (plugin.configManager.getScreens().getBoolean("once-per-restart")) {
                    // Check if the player has already seen the screen this server session
                    if (playerTracker.get(player.getUniqueId()) == null) {
                        playerTracker.put(player.getUniqueId(), String.valueOf(1));
                        GUIManager.setGUI(player, new WelcomeScreenMenu(plugin, player, "Items"));
                    }
                } else if (!plugin.configManager.getScreens().getBoolean("once-per-restart")) {
                    GUIManager.setGUI(player, new WelcomeScreenMenu(plugin, player, "Items"));
                }

                break;
            case DECLINED:
            case FAILED_DOWNLOAD:
                // Debug mode check; if enabled it will still send the player the welcome screen
                if (plugin.configManager.getConfig().getBoolean("debug-mode")) {
                    player.sendMessage(colourUtils.miniFormat(plugin.configManager.getLang().getString("prefix") + "#acb5bfNo server resource pack detected and/or debug mode is enabled."));
                    player.sendMessage(colourUtils.miniFormat(plugin.configManager.getLang().getString("prefix") + "#acb5bfSending welcome screen..."));
                    GUIManager.setGUI(player, new WelcomeScreenMenu(plugin, player, "Items"));
                } else {
                    player.removePotionEffect(PotionEffectType.BLINDNESS);

                    if (!plugin.configManager.getLang().getString("resource-pack.no-pack-loaded").equals("[]")) {
                        for (String msg : plugin.configManager.getLang().getStringList("resource-pack.no-pack-loaded")) {
                            player.sendMessage(colourUtils.miniFormat(msg));
                        }
                    }
                }
                break;
        }
    }
}
