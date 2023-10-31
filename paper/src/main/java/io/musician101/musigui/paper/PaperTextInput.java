package io.musician101.musigui.paper;

import io.musician101.musigui.common.TextInput;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Paper implementation of {@link TextInput}
 */
public abstract class PaperTextInput extends TextInput<Player> implements Listener {

    /**
     * Start a new Text Input process.
     *
     * @param plugin The plugin to registering the listeners.
     * @param player The player that we're accepting input from.
     */
    public PaperTextInput(@NotNull JavaPlugin plugin, @NotNull Player player) {
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
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId().equals(this.player.getUniqueId())) {
            event.setCancelled(true);
            process(player, event.message());
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
