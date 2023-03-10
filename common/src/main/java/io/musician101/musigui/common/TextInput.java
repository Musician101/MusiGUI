package io.musician101.musigui.common;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;

/**
 * A class used for processing input from a specific player from chat.
 *
 * @param <P> The player type we're accepting for the processor.
 */
public abstract class TextInput<P> {

    /**
     * A list of Players' UUIDs so we can keep track of who we're waiting for.
     */
    protected static final List<UUID> PLAYERS = new ArrayList<>();
    /**
     * The player this specific processor is waiting for.
     */
    @Nonnull
    protected final P player;

    /**
     * @param player The player this specific processor is waiting for.
     */
    protected TextInput(@Nonnull P player) {
        this.player = player;
    }

    /**
     * An implementation specific method to cancel the processor.
     */
    protected abstract void cancel();

    /**
     * A method provided to ensure that devs can use any input for their needs.
     *
     * @param player  The player entering text.
     * @param message The text the player sent.
     */
    protected abstract void process(P player, String message);
}
