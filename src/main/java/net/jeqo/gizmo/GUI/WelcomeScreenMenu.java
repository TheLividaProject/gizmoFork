package net.jeqo.gizmo.GUI;

import net.jeqo.gizmo.Events.PlayerProcessedEvent;
import net.jeqo.gizmo.Gizmo;
import net.jeqo.gizmo.Managers.Data.ItemData;
import net.jeqo.gizmo.Managers.Data.PDC.ItemDataPDC;
import net.jeqo.gizmo.Managers.GUI.MenuInventoryHolder;
import net.jeqo.gizmo.Utils.ColourUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class WelcomeScreenMenu implements MenuInventoryHolder {

    private final Gizmo plugin;

    private final Inventory inventory;

    private final NamespacedKey itemDataKey;

    private final ColourUtils colourUtils = new ColourUtils();

    public WelcomeScreenMenu(Gizmo plugin, Player player, String configOption) {
        this.plugin = plugin;
        this.itemDataKey = new NamespacedKey(plugin, "ItemData");

        this.inventory = Bukkit.createInventory(this, getSize(), getName());

        if (plugin.configManager.getScreens().getConfigurationSection(configOption) != null) {
            for (String key : plugin.configManager.getScreens().getConfigurationSection(configOption).getKeys(false)) {

                int slot = plugin.configManager.getScreens().getInt(configOption +  "." + key + ".slot");
                ItemStack item = new ItemStack(Material.valueOf(plugin.configManager.getScreens().getString(configOption +  "." + key + ".material")));

                item.editMeta(meta -> {
                    if (plugin.configManager.getScreens().get(configOption +  "." + key + ".lore") != null) {
                        List<Component> loreSetter = new ArrayList<>();

                        for (String string : plugin.configManager.getScreens().getStringList(configOption +  "." + key + ".lore")) {
                            loreSetter.add(colourUtils.placeHolderMiniFormat(player, string));
                        }

                        meta.lore(loreSetter);
                    }

                    if (plugin.configManager.getScreens().getBoolean(configOption +  "." + key + ".hide-flags")) {
                        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
                        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    }

                    meta.setCustomModelData(plugin.configManager.getScreens().getInt(configOption +  "." + key + ".custom-model-data"));
                    meta.displayName(colourUtils.miniFormat(plugin.configManager.getScreens().getString(configOption +  "." + key + ".name")));

                    meta.getPersistentDataContainer().set(itemDataKey, new ItemDataPDC(), new ItemData(slot, plugin.configManager.getScreens().getString("Items." + key)));
                });

                inventory.setItem(slot, item);
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public String getName() {
        return plugin.configManager.screenTitle();
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public MenuInventoryHolder handleClick(Player player, ItemStack item, InventoryClickEvent event) {
        ItemMeta meta = item.getItemMeta();

        ItemData itemData = meta.getPersistentDataContainer().get(itemDataKey, new ItemDataPDC());

        if (itemData == null) return null;

        String itemName = itemData.getItemOption();

        if (plugin.configManager.getScreens().getBoolean("Items." + itemName + ".close-on-click")) player.closeInventory();
        if (plugin.configManager.getScreens().getString("Items." + itemName + ".commands") == null) return null;

        for (String command : plugin.configManager.getScreens().getStringList("Items." + itemName + ".commands")) {
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
                plugin.getLogger().warning("\"" + itemName + "\"" + " (screens.yml) has a command with an invalid format.");
            }
        }

        return null;
    }

    @Override
    public MenuInventoryHolder handClose(Player player, InventoryCloseEvent event) {
        if (!plugin.screeningManager.playersScreenActive.contains(player.getUniqueId())) return null;
        if (plugin.screeningManager.processingPlayers.contains(player.getUniqueId())) return null;

        if (plugin.configManager.getConfig().getBoolean("enable-fade")) {
            player.showTitle(Title.title(colourUtils.miniFormat(plugin.configManager.getScreens().getString("Unicodes.background")), colourUtils.miniFormat(""), Title.Times.times(Duration.parse("0"), Duration.parse("0"), Duration.parse(String.valueOf(plugin.getConfig().getInt("fade-time"))))));
        }

        plugin.screeningManager.processingPlayers.add(player.getUniqueId());

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
            plugin.screeningManager.processingPlayers.remove(player.getUniqueId());
            plugin.screeningManager.playersScreenActive.remove(player.getUniqueId());
        }

        if (!player.hasPlayedBefore()) {
            if (!plugin.configManager.getScreens().getBoolean("first-join-welcome-screen")) return null;
            welcomeMessage(player, "first-join-welcome-message");
        } else {
            welcomeMessage(player, "welcome-message");
        }

        return null;
    }

    @Override
    public boolean cancelEvent() {
        return true;
    }

    private void welcomeMessage(Player player, String message) {
        String welcomeMessage = (plugin.configManager.getLang().getString(message));

        if (welcomeMessage.equals("[]")) return;

        welcomeMessage = welcomeMessage.replace(", ", "\n").replace("[", "").replace("]", "");
        player.sendMessage(colourUtils.miniFormat(welcomeMessage));
    }
}
