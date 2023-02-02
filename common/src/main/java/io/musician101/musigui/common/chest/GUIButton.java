package io.musician101.musigui.common.chest;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

/**
 * Button used for GUIs.
 *
 * @param <C> The click type used by the implementation.
 * @param <P> The player used by the implementation.
 * @param <S> The item stack used by the implementation.
 */
public final class GUIButton<C, P, S> {

    /**
     * A map of click types and their associated actions.
     */
    @Nonnull
    private final Map<C, Consumer<P>> actions;
    /**
     * The item stack representing the button.
     */
    @Nonnull
    private final S itemStack;
    /**
     * The slot the button sits in.
     */
    private final int slot;

    /**
     * @param slot      The slot the button sits in.
     * @param itemStack The item stack to represent the button.
     */
    public GUIButton(int slot, @Nonnull S itemStack) {
        this(slot, itemStack, Map.of());
    }

    /**
     * @param slot      The slot the button sits in.
     * @param itemStack The item stack to represent the button.
     * @param actions   A map of click types and their associated actions.
     */
    public GUIButton(int slot, @Nonnull S itemStack, @Nonnull Map<C, Consumer<P>> actions) {
        this.slot = slot;
        this.itemStack = itemStack;
        this.actions = actions;
    }

    /**
     * Get the action of the click type.
     *
     * @param clickType The click type to check.
     * @return An {@link Optional} containing the action if it exists.
     */
    @Nonnull
    public Optional<Consumer<P>> getAction(C clickType) {
        return Optional.ofNullable(actions.get(clickType));
    }

    /**
     * @return The item stack representing the button.
     */
    @Nonnull
    public S getItemStack() {
        return itemStack;
    }

    /**
     * @return The slot that the button sits in.
     */
    public int getSlot() {
        return slot;
    }
}
