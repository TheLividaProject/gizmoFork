package net.jeqo.gizmo.Commands;

import net.jeqo.gizmo.Gizmo;
import net.jeqo.gizmo.Managers.Commands.SubCommands;
import net.jeqo.gizmo.Utils.ColourUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GizmoHelpCommand implements SubCommands {

    private final Gizmo plugin;

    private final ColourUtils colourUtils = new ColourUtils();

    public GizmoHelpCommand(Gizmo plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("gizmo.help")) {
                player.sendMessage(colourUtils.miniFormat(plugin.getConfigManager().getLang().getString("prefix") + plugin.getConfigManager().getLang().getString("commands.no-permission")));
                return;
            }
        }

        plugin.getConfigManager().getLang().getStringList("commands.help.usage").forEach(string -> {
            sender.sendMessage(colourUtils.miniFormat(string));
        });
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
