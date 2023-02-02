package io.musician101.musigui.sponge;

import io.musician101.musigui.common.TextInput;
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

/**
 * Sponge implementation of {@link TextInput}
 */
public abstract class SpongeTextInput extends TextInput<ServerPlayer> {

    /**
     * Start a new Text Input process.
     *
     * @param plugin The plugin to registering the listeners.
     * @param player The player that we're accepting input from.
     */
    public SpongeTextInput(@Nonnull PluginContainer plugin, @Nonnull ServerPlayer player) {
        this(plugin, player, null);
    }

    /**
     * Start a new Text Input process.
     *
     * @param plugin The plugin to registering the listeners.
     * @param player The player that we're accepting input from.
     * @param text   Old/default text that can be selected and changed by the player.
     */
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

    /**
     * Check if the provider player is already in the process of a Text Input processor.
     *
     * @param player The player we're checking.
     * @return True if we're waiting on the player to finish a previous Text Input processor.
     */
    public static boolean isWaitingForInput(ServerPlayer player) {
        return PLAYERS.contains(player.uniqueId());
    }

    /**
     * @see TextInput#cancel()
     */
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
