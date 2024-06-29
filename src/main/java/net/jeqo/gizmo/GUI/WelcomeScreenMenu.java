package net.jeqo.gizmo.GUI;

import net.jeqo.gizmo.Gizmo;
import net.jeqo.gizmo.Managers.GUI.MenuInventoryHolder;
import net.jeqo.gizmo.Utils.ColourUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WelcomeScreenMenu implements MenuInventoryHolder {

    private final Gizmo plugin;

    private final Inventory inventory;

    private final ColourUtils colourUtils = new ColourUtils();

    public WelcomeScreenMenu(Gizmo plugin, Player player, String configOption) {
        this.plugin = plugin;

        this.inventory = Bukkit.createInventory(this, getSize(), getName());

        if (plugin.configManager.getScreens().getConfigurationSection(configOption) != null) {
            for (String key : plugin.configManager.getScreens().getConfigurationSection(configOption).getKeys(false)) {

                int slot = plugin.configManager.getScreens().getInt(configOption +  "." + key + ".slot");
                ItemStack item = new ItemStack(Material.matchMaterial(plugin.configManager.getScreens().getString(configOption +  "." + key + ".material")));

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
        //TODO setup item click function
        return null;
    }

    @Override
    public boolean cancelEvent() {
        return true;
    }
}
