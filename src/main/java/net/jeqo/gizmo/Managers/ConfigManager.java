package net.jeqo.gizmo.Managers;

import net.jeqo.gizmo.Gizmo;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigManager {

    private final Gizmo plugin;

    private FileConfiguration config, screens, lang;

    public ConfigManager(Gizmo plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
            plugin.saveResource("config.yml", false);
            plugin.saveResource("screens.yml", false);
            plugin.saveResource("Lang/messages.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        screens = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "screens.yml"));
        lang = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "Lang/messages.yml"));
    }

    public String shift48() {
        return getScreens().getString("Unicodes.shift-48");
    }

    public String shift1013() {
        return getScreens().getString("Unicodes.shift-1013");
    }

    public String shift1536() {
        return getScreens().getString("Unicodes.shift-1536");
    }

    public String screenTitle() {
        return plugin.getConfigManager().getConfig().getString("background-color") + shift1013() + getScreens().getString("Unicodes.background") + shift1536() + "<#FFFFFF>" + getScreens().getString("Unicodes.welcome-screen");
    }

    public String screenTitleFirstJoin() {
        return getConfig().getString("background-color") + shift1013() + getScreens().getString("Unicodes.first-join-background") + shift1536() + "<#FFFFFF>" + getScreens().getString("Unicodes.first-join-welcome-screen");
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getScreens() {
        return screens;
    }

    public FileConfiguration getLang() {
        return lang;
    }
}
