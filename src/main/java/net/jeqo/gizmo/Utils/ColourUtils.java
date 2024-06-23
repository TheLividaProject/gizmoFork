package net.jeqo.gizmo.Utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ColourUtils {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public @NotNull Component miniFormat(String message) {
        return miniMessage.deserialize(message).decoration(TextDecoration.ITALIC, false);
    }

    public @NotNull Component miniFormat(String message, TagResolver tagResolver) {
        return miniMessage.deserialize(message, tagResolver).decoration(TextDecoration.ITALIC, false);
    }

    public @NotNull Component placeHolderMiniFormat(Player player, String message) {
        return miniMessage.deserialize(message, papiTag(player)).decoration(TextDecoration.ITALIC, false);
    }

    public @NotNull Component placeHolderMiniFormat(Player player, String message, TagResolver tagResolver) {
        return miniMessage.deserialize(message, papiTag(player), tagResolver).decoration(TextDecoration.ITALIC, false);
    }

    public String stripColour(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    private @NotNull TagResolver papiTag(final @NotNull Player player) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();
            String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%');
            Component componentPlaceholder = miniMessage.deserialize(parsedPlaceholder);
            return Tag.selfClosingInserting(componentPlaceholder);
        });
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }
}
