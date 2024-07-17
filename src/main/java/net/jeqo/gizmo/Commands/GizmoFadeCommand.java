package net.jeqo.gizmo.Commands;

import net.jeqo.gizmo.Gizmo;
import net.jeqo.gizmo.Managers.Commands.SubCommands;
import net.jeqo.gizmo.Utils.ColourUtils;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;

public class GizmoFadeCommand implements SubCommands {

    private final Gizmo plugin;

    private final ColourUtils colourUtils = new ColourUtils();

    public GizmoFadeCommand(Gizmo plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("gizmo.fade")) {
                player.sendMessage(colourUtils.miniFormat(plugin.getConfigManager().getLang().getString("prefix") + plugin.getConfigManager().getLang().getString("commands.no-permission")));
                return;
            }
        }

        if (args.length == 0) {
            sender.sendMessage(colourUtils.miniFormat(plugin.getConfigManager().getLang().getString("prefix") + plugin.getConfigManager().getLang().getString("commands.fade.usage")));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(colourUtils.miniFormat(plugin.getConfigManager().getLang().getString("prefix") + plugin.getConfigManager().getLang().getString("commands.fade.invalid-player")));
            return;
        }

        if (args.length < 4) {
            sender.sendMessage(colourUtils.miniFormat(plugin.getConfigManager().getLang().getString("prefix") + plugin.getConfigManager().getLang().getString("commands.fade.invalid-numbers-amount")));
            return;
        }

        if (!args[1].matches("[0-9]+") && !args[2].matches("[0-9]+") && !args[3].matches("[0-9]+")) {
            sender.sendMessage(colourUtils.miniFormat(plugin.getConfigManager().getLang().getString("prefix") + plugin.getConfigManager().getLang().getString("commands.fade.invalid-numbers")));
            return;
        }

        target.showTitle(Title.title(colourUtils.miniFormat(plugin.getConfigManager().getScreens().getString("Unicodes.background")), colourUtils.miniFormat(""), Title.Times.times(Duration.ofMillis(Long.parseLong(args[1])), Duration.ofMillis(Long.parseLong(args[2])), Duration.ofMillis(Long.parseLong(args[3])))));
    }

    @Override
    public String name() {
        return "fade";
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
