package net.jeqo.gizmo;

import net.jeqo.gizmo.Managers.Commands.GizmoCommandManager;
import net.jeqo.gizmo.data.UpdateChecker;
import net.jeqo.gizmo.listeners.ClickableItemsListener;
import net.jeqo.gizmo.listeners.GUI.GUIClickListener;
import net.jeqo.gizmo.listeners.PlayerScreeningListener;
import net.jeqo.gizmo.listeners.ScreenAdvanceListener;
import net.jeqo.gizmo.listeners.ScreenHandlersListener;
import net.jeqo.gizmo.Managers.ConfigManager;
import net.jeqo.gizmo.Managers.ScreeningManager;
import net.jeqo.gizmo.data.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Gizmo extends JavaPlugin {

    private final int pluginId = 16873;

    public ConfigManager configManager;
    public ScreeningManager screeningManager;

    @Override
    public void onEnable() {
        this.getLogger().log(Level.INFO, "|---[ GIZMO ]--------------------------------------------------------|");
        this.getLogger().log(Level.INFO, "|                           Plugin loaded.                           |");
        this.getLogger().log(Level.INFO, "|-------------------------------------------------[ MADE BY JEQO ]---|");

        loadManagers();
        loadListeners();

        loadCommands();
        Metrics metrics = new Metrics(this, pluginId);

        updateChecker();
    }

    @Override
    public void onDisable() {
        this.getLogger().log(Level.INFO, "|---[ GIZMO ]--------------------------------------------------------|");
        this.getLogger().log(Level.INFO, "|                          Shutting down...                          |");
        this.getLogger().log(Level.INFO, "|-------------------------------------------------[ MADE BY JEQO ]---|");
    }

    private void loadManagers() {
        configManager = new ConfigManager(this);
        screeningManager = new ScreeningManager(this);
    }

    private void loadCommands() {
        new GizmoCommandManager(this);
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerScreeningListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ScreenHandlersListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ScreenAdvanceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ClickableItemsListener(this), this);

        Bukkit.getPluginManager().registerEvents(new GUIClickListener(this), this);
    }

    private void updateChecker() {
        new UpdateChecker(this, 106024).getVersion(version -> {
            if (this.getPluginMeta().getVersion().equals(version)) return;

            this.getLogger().warning("|---[ GIZMO ]--------------------------------------------------------|");
            this.getLogger().warning("|                  There is a new update available!                  |");
            this.getLogger().warning("|                       https://jeqo.net/gizmo                       |");
            this.getLogger().warning("|-------------------------------------------------[ MADE BY JEQO ]---|");
        });
    }
}
