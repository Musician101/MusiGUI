package io.musician101.musigui.spigot;

import io.musician101.musigui.common.TextInput;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Spigot implementation of {@link TextInput}
 */
public abstract class SpigotTextInput extends TextInput<Player> implements Listener {

    /**
     * Start a new Text Input process.
     *
     * @param plugin The plugin to registering the listeners.
     * @param player The player that we're accepting input from.
     */
    public SpigotTextInput(@Nonnull JavaPlugin plugin, @Nonnull Player player) {
        super(player);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Check if the provider player is already in the process of a Text Input processor.
     *
     * @param player The player we're checking.
     * @return True if we're waiting on the player to finish a previous Text Input processor.
     */
    public static boolean isWaitingForInput(Player player) {
        return PLAYERS.contains(player.getUniqueId());
    }

    /**
     * @see TextInput#cancel()
     */
    public void cancel() {
        HandlerList.unregisterAll(this);
        PLAYERS.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId().equals(this.player.getUniqueId())) {
            event.setCancelled(true);
            process(player, event.getMessage());
            cancel();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId().equals(this.player.getUniqueId())) {
            cancel();
        }
    }
}
