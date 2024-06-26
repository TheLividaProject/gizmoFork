package net.jeqo.gizmo.Managers;

import net.jeqo.gizmo.GUI.WelcomeScreenMenu;
import net.jeqo.gizmo.Gizmo;
import net.jeqo.gizmo.Managers.GUI.GUIManager;
import net.jeqo.gizmo.Utils.ItemUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ScreeningManager {

    private final Gizmo plugin;

    public HashMap<UUID, Boolean> playersScreenActive = new HashMap<>();
    public HashMap<UUID, String> playersStoredInventory = new HashMap<>();

    private final ItemUtils itemUtils = new ItemUtils();

    public ScreeningManager(Gizmo plugin) {
        this.plugin = plugin;
    }

    public void displayScreen(Player player, String configOption, String configBoolean) {
        if (!plugin.configManager.getScreens().getBoolean(configBoolean)) return;
        
        playersScreenActive.put(player.getUniqueId(), true);
        playersStoredInventory.put(player.getUniqueId(),itemUtils.itemStackArrayToBase64(player.getInventory().getContents()));

        player.getInventory().clear();
        GUIManager.setGUI(player, new WelcomeScreenMenu(plugin, player, configOption));
    }
}
