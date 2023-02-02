package io.musician101.musigui.sponge.chest;

import io.musician101.musigui.common.chest.ChestGUI;
import io.musician101.musigui.common.chest.GUIButton;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.menu.ClickType;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.item.inventory.menu.handler.CloseHandler;
import org.spongepowered.api.item.inventory.menu.handler.SlotClickHandler;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.item.inventory.type.ViewableInventory.Builder.BuildingStep;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.math.vector.Vector2i;
import org.spongepowered.plugin.PluginContainer;

/**
 * Sponge implementation of {@link ChestGUI}
 */
public abstract class SpongeChestGUI extends ChestGUI<ClickType<?>, ViewableInventory, PluginContainer, ServerPlayer, ItemStack, Component, Container, Container> implements CloseHandler, SlotClickHandler {

    /**
     * If the GUI is read only.
     */
    private final boolean readOnly;
    /**
     * Sponge specific field for handling extra actions for when a {@link ClickType} is used.
     */
    @Nonnull
    protected SlotClickHandler extraClickHandler;
    /**
     * Sponge specific field for handling extra actions for when the GUI is closed.
     */
    @Nonnull
    protected CloseHandler extraCloseHandler = (cause, container) -> {

    };

    /**
     * Base Sponge implementation constructor
     *
     * @param player     The player using the GUI.
     * @param name       The name of the GUI.
     * @param size       The number of slots in the GUI.
     * @param plugin     The plugin registering the GUI.
     * @param manualOpen When set to false, the GUI is opened automatically.
     * @param readOnly   If the GUI is read only.
     */
    protected SpongeChestGUI(@Nonnull ServerPlayer player, @Nonnull Component name, int size, @Nonnull PluginContainer plugin, boolean manualOpen, boolean readOnly) {
        super(parseInventory(player, size), name, player, plugin, manualOpen);
        this.readOnly = readOnly;
        extraClickHandler = (cause, container, slot, slotIndex, clickType) -> readOnly;
    }

    private static ViewableInventory parseInventory(ServerPlayer player, int size) {
        int rows = size / 9;
        BuildingStep builder = ViewableInventory.builder().type(switch (rows) {
            case 1 -> ContainerTypes.GENERIC_9X1.get();
            case 2 -> ContainerTypes.GENERIC_9X2.get();
            case 3 -> ContainerTypes.GENERIC_9X3.get();
            case 4 -> ContainerTypes.GENERIC_9X4.get();
            case 5 -> ContainerTypes.GENERIC_9X5.get();
            default -> ContainerTypes.GENERIC_9X6.get();
        });
        IntStream.range(0, rows).forEach(i -> {
            Inventory inv = Inventory.builder().slots(9).completeStructure().build();
            builder.grid(inv.slots(), Vector2i.from(9, i), Vector2i.from(0, i * 3));
        });

        return builder.completeStructure().carrier(player).build();
    }

    /**
     * @see ChestGUI#addItem(int, Object)
     */
    @Override
    protected void addItem(int slot, @Nonnull ItemStack itemStack) {
        inventory.set(slot, itemStack);
    }

    @Override
    public boolean handle(Cause cause, Container container, Slot slot, int slotIndex, ClickType<?> clickType) {
        return cause.first(ServerPlayer.class).map(player -> {
            if (isCorrectInventory(container)) {
                Optional<Consumer<ServerPlayer>> action = buttons.stream().filter(button -> button.getSlot() == slotIndex).findFirst().flatMap(button -> button.getAction(clickType));
                if (action.isPresent()) {
                    action.get().accept(player);
                    return true;
                }

                extraClickHandler.handle(cause, container, slot, slotIndex, clickType);
            }

            return readOnly;
        }).orElse(readOnly);
    }

    @Override
    public void handle(Cause cause, Container container) {
        cause.first(ServerPlayer.class).filter(p -> isCorrectInventory(container) && p.uniqueId().equals(container.viewer().uniqueId())).ifPresent(player -> extraCloseHandler.handle(cause, container));
    }

    /**
     * @see ChestGUI#isCorrectInventory(Object)
     */
    @Override
    protected boolean isCorrectInventory(@Nonnull Container container) {
        return container.get(Keys.DISPLAY_NAME).filter(name -> name.equals(this.name) && player.uniqueId().equals(container.viewer().uniqueId())).isPresent();
    }

    /**
     * @see ChestGUI#open()
     */
    @Override
    public void open() {
        Sponge.server().scheduler().submit(Task.builder().execute(() -> {
            inventory.clear();
            buttons.forEach(button -> inventory.set(button.getSlot(), button.getItemStack()));
            InventoryMenu menu = inventory.asMenu();
            menu.setTitle(name);
            menu.setReadOnly(readOnly);
            menu.registerSlotClick(this);
            menu.registerClose(this);
            menu.open(player);
        }).delay(Ticks.of(1L)).plugin(plugin).build());
    }

    @Override
    protected void removeItem(int slot) {
        inventory.set(slot, ItemStack.empty());
    }

    /**
     * Set a button.
     *
     * @param slot      The slot to set.
     * @param itemStack The item to put in the slot.
     * @param clickType The type of click that triggers the button.
     * @param action    What happens when the button is clicked.
     */
    public final <C extends ClickType<?>> void setButton(int slot, @Nonnull ItemStack itemStack, @Nonnull Supplier<C> clickType, @Nonnull Consumer<ServerPlayer> action) {
        setButton(slot, itemStack, Map.of(clickType.get(), action));
    }

    /**
     * Set a button.
     *
     * @param slot      The slot to set.
     * @param itemStack The item to put in the slot.
     * @param actions   A map of click types as the key, and actions as the values.
     */
    public final <C extends ClickType<?>> void setButton(int slot, @Nonnull ItemStack itemStack, @Nonnull Map<Supplier<C>, Consumer<ServerPlayer>> actions) {
        buttons.removeIf(g -> g.getSlot() == slot);
        buttons.add(new GUIButton<>(slot, itemStack, actions.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().get(), Entry::getValue))));
        addItem(slot, itemStack);
    }
}
