package net.jeqo.gizmo.listeners;

import net.jeqo.gizmo.Events.PlayerProcessedEvent;
import net.jeqo.gizmo.Gizmo;
import net.jeqo.gizmo.Utils.ColourUtils;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ScreenAdvanceListener implements Listener {

    private final Gizmo plugin;

    private final Set<UUID> processingPlayers = new HashSet<>();

    private final ColourUtils colourUtils = new ColourUtils();

    public ScreenAdvanceListener(Gizmo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if (!event.getView().getTitle().equals(plugin.configManager.screenTitle())) return;
        if (!plugin.screeningManager.playersScreenActive.get(player.getUniqueId())) return;

        if (processingPlayers.contains(player.getUniqueId())) return;

        if (plugin.configManager.getConfig().getBoolean("enable-fade")) {
            player.showTitle(Title.title(colourUtils.miniFormat(plugin.configManager.getScreens().getString("Unicodes.background")), colourUtils.miniFormat(""), Title.Times.times(Duration.parse("0"), Duration.parse("0"), Duration.parse(String.valueOf(plugin.getConfig().getInt("fade-time"))))));
        }

        processingPlayers.add(player.getUniqueId());

        try {
            if (plugin.configManager.getConfig().getBoolean("sound-on-advance.enable")) {
                String soundID = plugin.configManager.getConfig().getString("sound-on-advance.sound");
                float soundVolume = Float.parseFloat(plugin.configManager.getConfig().getString("sound-on-advance.volume"));
                float soundPitch = Float.parseFloat(plugin.configManager.getConfig().getString("sound-on-advance.pitch"));

                try {
                    Sound sound = Sound.valueOf(soundID.toUpperCase());
                    player.playSound(player.getLocation(), sound, soundVolume, soundPitch);
                } catch (IllegalArgumentException err) {
                    player.playSound(player.getLocation(), soundID, soundVolume, soundPitch);
                }
            }
        } catch (NullPointerException ex) {
            plugin.getLogger().warning("sound-on-advance is not configured correctly.");
        }

        try {
            plugin.configManager.getConfig().getStringList("commands-on-advance").forEach(command -> {
                if (command.contains("[console]")) {
                    command = command.replace("[console] ", "");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                } else if (command.contains("[message]")) {
                    command = command.replace("[message] ", "");
                    player.sendMessage(colourUtils.placeHolderMiniFormat(player, command.replace("%player%", player.getName())));
                } else if (command.contains("[player]")) {
                    command = command.replace("[player] ", "");
                    player.performCommand(command);
                } else {
                    player.sendMessage(plugin.configManager.getLang().getString("prefix") + "An error occurred. Please review the console for more information.");
                    plugin.getLogger().warning("Commands-on-advance (config.yml) has a command with an invalid format.");
                }
            });
        } finally {
            Bukkit.getServer().getPluginManager().callEvent(new PlayerProcessedEvent(player));
            processingPlayers.remove(player.getUniqueId());
            plugin.screeningManager.playersScreenActive.remove(player.getUniqueId());
        }

        if (!player.hasPlayedBefore()) {
            if (!plugin.configManager.getScreens().getBoolean("first-join-welcome-screen")) return;
            welcomeMessage(player, "first-join-welcome-message");
        } else {
            welcomeMessage(player, "welcome-message");
        }
    }

    private void welcomeMessage(Player player, String message) {
        String welcomeMessage = (plugin.configManager.getLang().getString(message));

        if (welcomeMessage.equals("[]")) return;

        welcomeMessage = welcomeMessage.replace(", ", "\n").replace("[", "").replace("]", "");
        player.sendMessage(colourUtils.miniFormat(welcomeMessage));
    }
}