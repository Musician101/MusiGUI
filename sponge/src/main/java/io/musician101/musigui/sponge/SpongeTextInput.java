package io.musician101.musigui.sponge;

import io.musician101.musigui.common.TextInput;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.PlayerChatEvent;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.plugin.PluginContainer;

public abstract class SpongeTextInput extends TextInput<ServerPlayer> {

    private static final List<UUID> PLAYERS = new ArrayList<>();

    public SpongeTextInput(@Nonnull PluginContainer plugin, @Nonnull ServerPlayer player) {
        this(plugin, player, null);
    }

    public SpongeTextInput(@Nonnull PluginContainer plugin, @Nonnull ServerPlayer player, @Nullable String text) {
        super(player);
        if (PLAYERS.contains(player.uniqueId())) {
            player.sendMessage(Component.text("An error has occurred. Something is already waiting for your input.", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.text("Please enter the text you would like to submit."));
        if (text != null) {
            player.sendMessage(Component.text("Or click here to edit the text.").clickEvent(ClickEvent.suggestCommand(text)));
        }

        Sponge.eventManager().registerListeners(plugin, this);
    }

    public static boolean isWaitingForInput(ServerPlayer player) {
        return PLAYERS.contains(player.uniqueId());
    }

    @Override
    protected void cancel() {
        Sponge.eventManager().unregisterListeners(this);
        PLAYERS.remove(player.uniqueId());
    }

    @Listener
    public final void onPlayerChat(PlayerChatEvent event, @First ServerPlayer player) {
        if (player.uniqueId().equals(this.player.uniqueId())) {
            event.setCancelled(true);
            process(player, PlainTextComponentSerializer.plainText().serialize(event.originalMessage()));
            cancel();
        }
    }

    @Listener
    public final void onQuit(ServerSideConnectionEvent.Disconnect event, @First ServerPlayer player) {
        if (player.uniqueId().equals(this.player.uniqueId())) {
            cancel();
        }
    }
}
