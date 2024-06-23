package net.jeqo.gizmo.Managers.GUI;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class GUIManager {

    private static final HashMap<Player, MenuInventoryHolder> playerGUIMap = new HashMap<>();

    public static void setGUI(Player player, MenuInventoryHolder gui) {
        player.closeInventory();
        playerGUIMap.put(player, gui);
        player.openInventory(gui.getInventory());
    }

    public static MenuInventoryHolder getOpenGUI(Player player) {
        return playerGUIMap.get(player);
    }

    public static HashMap<Player, MenuInventoryHolder> getPlayerGUICache() {
        return playerGUIMap;
    }
}
