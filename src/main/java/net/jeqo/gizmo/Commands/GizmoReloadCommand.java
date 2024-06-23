package net.jeqo.gizmo.Commands;

import net.jeqo.gizmo.Gizmo;
import net.jeqo.gizmo.Managers.Commands.SubCommands;
import net.jeqo.gizmo.Utils.ColourUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GizmoReloadCommand implements SubCommands {

    private final Gizmo plugin;

    private final ColourUtils colourUtils = new ColourUtils();

    public GizmoReloadCommand(Gizmo plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("gizmo.reload")) {
                player.sendMessage(colourUtils.oldFormat(plugin.configManager.getLang().getString("prefix") + plugin.configManager.getLang().getString("commands.no-permission")));
                return;
            }
        }

        //TODO load this async
        plugin.configManager.load();

        sender.sendMessage(colourUtils.oldFormat(plugin.configManager.getLang().getString("prefix") + plugin.configManager.getLang().getString("commands.reload.config-reloaded")));
    }


    @Override
    public String name() {
        return "reload";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
