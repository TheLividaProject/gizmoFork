package net.jeqo.gizmo.Listeners;

import net.jeqo.gizmo.GUI.WelcomeScreenMenu;
import net.jeqo.gizmo.Gizmo;
import net.jeqo.gizmo.Managers.GUI.GUIManager;
import net.jeqo.gizmo.Utils.ColourUtils;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
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
                if (!plugin.getConfigManager().getConfig().getBoolean("delay-background")) return;
                player.showTitle(Title.title(colourUtils.miniFormat(plugin.getConfigManager().getConfig().getString("background-color") + plugin.getConfigManager().getScreens().getString("Unicodes.background")), colourUtils.miniFormat(""), Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(999999), Duration.ofMillis(0))));
            case SUCCESSFULLY_LOADED:
                // Play a configured sound when the pack is loaded
                try {
                    if (plugin.getConfigManager().getConfig().getBoolean("sound-on-pack-load.enable")) {
                        String soundID = plugin.getConfigManager().getConfig().getString("sound-on-pack-load.sound");
                        float soundVolume = Float.parseFloat(plugin.getConfigManager().getConfig().getString("sound-on-pack-load.volume"));
                        float soundPitch = Float.parseFloat(plugin.getConfigManager().getConfig().getString("sound-on-pack-load.pitch"));

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
                    if (plugin.getConfigManager().getScreens().getBoolean("enable-first-join-welcome-screen")) {
                        plugin.getScreeningManager().displayScreen(player, "First-Join-Items", "enable-first-join-welcome-screen");
                        return;
                    }
                }

                // Display the screen once per restart
                if (plugin.getConfigManager().getScreens().getBoolean("once-per-restart")) {
                    // Check if the player has already seen the screen this server session
                    if (playerTracker.get(player.getUniqueId()) == null) {
                        playerTracker.put(player.getUniqueId(), String.valueOf(1));
                        plugin.getScreeningManager().displayScreen(player, "Items", "enable-welcome-screen");
                    }
                } else if (!plugin.getConfigManager().getScreens().getBoolean("once-per-restart")) {
                    plugin.getScreeningManager().displayScreen(player, "Items", "enable-welcome-screen");
                }
            case DECLINED:
            case FAILED_DOWNLOAD:
                // Debug mode check; if enabled it will still send the player the welcome screen
                if (plugin.getConfigManager().getConfig().getBoolean("debug-mode")) {
                    player.sendMessage(colourUtils.miniFormat(plugin.getConfigManager().getLang().getString("prefix") + "#acb5bfNo server resource pack detected and/or debug mode is enabled."));
                    player.sendMessage(colourUtils.miniFormat(plugin.getConfigManager().getLang().getString("prefix") + "#acb5bfSending welcome screen..."));
                    plugin.getScreeningManager().displayScreen(player, "Items", "enable-welcome-screen");
                } else {
                    player.removePotionEffect(PotionEffectType.BLINDNESS);

                    if (!plugin.getConfigManager().getLang().getString("resource-pack.no-pack-loaded").equals("[]")) {
                        for (String msg : plugin.getConfigManager().getLang().getStringList("resource-pack.no-pack-loaded")) {
                            player.sendMessage(colourUtils.miniFormat(msg));
                        }
                    }
                }
        }
    }
}
