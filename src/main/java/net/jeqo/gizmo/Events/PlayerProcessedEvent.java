package net.jeqo.gizmo.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerProcessedEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public PlayerProcessedEvent(Player player) {
        super(player);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
