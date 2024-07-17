package net.jeqo.gizmo;

import net.jeqo.gizmo.Managers.Commands.GizmoCommandManager;
import net.jeqo.gizmo.Managers.GUI.GUIManager;
import net.jeqo.gizmo.data.UpdateChecker;
import net.jeqo.gizmo.Listeners.GUI.GUIClickListener;
import net.jeqo.gizmo.Listeners.PlayerScreeningListener;
import net.jeqo.gizmo.Listeners.ScreenHandlersListener;
import net.jeqo.gizmo.Managers.ConfigManager;
import net.jeqo.gizmo.Managers.ScreeningManager;
import net.jeqo.gizmo.data.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Gizmo extends JavaPlugin {

    private final int pluginId = 16873;

    private ConfigManager configManager;
    private ScreeningManager screeningManager;
    private GUIManager guiManager;

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
        guiManager = new GUIManager();
    }

    private void loadCommands() {
        new GizmoCommandManager(this);
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerScreeningListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ScreenHandlersListener(this), this);

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

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ScreeningManager getScreeningManager() {
        return screeningManager;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }
}
