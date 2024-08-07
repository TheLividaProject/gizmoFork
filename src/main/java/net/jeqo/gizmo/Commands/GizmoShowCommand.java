package net.jeqo.gizmo.Commands;

import net.jeqo.gizmo.GUI.WelcomeScreenMenu;
import net.jeqo.gizmo.Gizmo;
import net.jeqo.gizmo.Managers.Commands.SubCommands;
import net.jeqo.gizmo.Managers.GUI.GUIManager;
import net.jeqo.gizmo.Utils.ColourUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GizmoShowCommand implements SubCommands {

    private final Gizmo plugin;

    private final ColourUtils colourUtils = new ColourUtils();

    public GizmoShowCommand(Gizmo plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("gizmo.show")) {
                player.sendMessage(colourUtils.miniFormat(plugin.getConfigManager().getLang().getString("prefix") + plugin.getConfigManager().getLang().getString("commands.no-permission")));
                return;
            }
        }

        if (args.length == 0) {
            sender.sendMessage(colourUtils.miniFormat(plugin.getConfigManager().getLang().getString("prefix") + plugin.getConfigManager().getLang().getString("commands.show.usage")));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(colourUtils.miniFormat(plugin.getConfigManager().getLang().getString("prefix") + plugin.getConfigManager().getLang().getString("commands.show.invalid-player")));
            return;
        }

        plugin.getScreeningManager().displayScreen(target, "Items", "enable-welcome-screen");
        sender.sendMessage(colourUtils.placeHolderMiniFormat(target, plugin.getConfigManager().getLang().getString("prefix") + plugin.getConfigManager().getLang().getString("commands.show.showing")));
    }

    @Override
    public String name() {
        return "show";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
