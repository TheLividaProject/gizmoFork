package net.jeqo.gizmo.listeners.GUI;

import net.jeqo.gizmo.Gizmo;
import net.jeqo.gizmo.Managers.GUI.GUIManager;
import net.jeqo.gizmo.Managers.GUI.MenuInventoryHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

public class GUIClickListener implements Listener {

    private final Gizmo plugin;

    public GUIClickListener(Gizmo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();

        MenuInventoryHolder menuInventoryHolder = GUIManager.getOpenGUI(player);

        if (menuInventoryHolder == null) return;

        if (menuInventoryHolder.cancelEvent()) {
            event.setCancelled(true);
        }

        MenuInventoryHolder newMenuHolder = menuInventoryHolder.handleClick(player, event.getCurrentItem(), event);

        if (newMenuHolder == null) return;

        event.getView().close();

        GUIManager.setGUI(player, newMenuHolder);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.PLAYER) return;
        if (event.getInventory().getType() == InventoryType.CRAFTING) return;

        Player player = (Player) event.getPlayer();

        MenuInventoryHolder menuInventoryHolder = GUIManager.getOpenGUI(player);

        if (menuInventoryHolder == null) return;

        GUIManager.getPlayerGUICache().remove(player);
    }
}
