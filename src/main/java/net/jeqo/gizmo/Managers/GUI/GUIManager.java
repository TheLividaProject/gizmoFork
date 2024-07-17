package net.jeqo.gizmo.Managers.GUI;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class GUIManager {

    private final HashMap<Player, MenuInventoryHolder> playerGUIMap = new HashMap<>();

    public void setGUI(Player player, MenuInventoryHolder gui) {
        player.closeInventory();
        playerGUIMap.put(player, gui);
        player.openInventory(gui.getInventory());
    }

    public MenuInventoryHolder getOpenGUI(Player player) {
        return playerGUIMap.get(player);
    }

    public HashMap<Player, MenuInventoryHolder> getPlayerGUICache() {
        return playerGUIMap;
    }
}
