package net.jeqo.gizmo.Managers;

import net.jeqo.gizmo.GUI.WelcomeScreenMenu;
import net.jeqo.gizmo.Gizmo;
import net.jeqo.gizmo.Managers.GUI.GUIManager;
import net.jeqo.gizmo.Utils.ItemUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class ScreeningManager {

    private final Gizmo plugin;

    public HashSet<UUID> playersScreenActive = new HashSet<>();
    public HashMap<UUID, String> playersStoredInventory = new HashMap<>();

    public HashSet<UUID> processingPlayers = new HashSet<>();

    private final ItemUtils itemUtils = new ItemUtils();

    public ScreeningManager(Gizmo plugin) {
        this.plugin = plugin;
    }

    public void displayScreen(Player player, String configOption, String configBoolean) {
        if (!plugin.getConfigManager().getScreens().getBoolean(configBoolean)) return;

        playersScreenActive.add(player.getUniqueId());
        playersStoredInventory.put(player.getUniqueId(), itemUtils.itemStackArrayToBase64(player.getInventory().getContents()));

        player.getInventory().clear();
        plugin.getGuiManager().setGUI(player, new WelcomeScreenMenu(plugin, player, configOption));
    }
}
