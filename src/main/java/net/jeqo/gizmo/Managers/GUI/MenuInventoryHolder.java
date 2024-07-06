package net.jeqo.gizmo.Managers.GUI;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface MenuInventoryHolder extends InventoryHolder {
    String getName();
    int getSize();
    MenuInventoryHolder handleClick(Player player, ItemStack item, InventoryClickEvent event);
    MenuInventoryHolder handClose(Player player, InventoryCloseEvent event);
    boolean cancelEvent();
}
