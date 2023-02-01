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

public abstract class SpongeChestGUI extends ChestGUI<ClickType<?>, ViewableInventory, PluginContainer, ServerPlayer, ItemStack, Component, Container, Container> implements CloseHandler, SlotClickHandler {

    private final boolean readOnly;
    @Nonnull
    protected SlotClickHandler extraClickHandler = new SlotClickHandler() {

        @Override
        public boolean handle(Cause cause, Container container, Slot slot, int slotIndex, ClickType<?> clickType) {
            return readOnly;
        }
    };

    protected SpongeChestGUI(@Nonnull ServerPlayer player, @Nonnull Component name, int size, @Nonnull PluginContainer plugin, boolean manualOpen, boolean readOnly) {
        super(parseInventory(player, size), name, player, plugin, manualOpen);
        this.readOnly = readOnly;
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

    public final <C extends ClickType<?>> void setButton(int slot, @Nonnull ItemStack itemStack, @Nonnull Supplier<C> clickType, @Nonnull Consumer<ServerPlayer> action) {
        setButton(slot, itemStack, Map.of(clickType.get(), action));
    }

    public final <C extends ClickType<?>> void setButton(int slot, @Nonnull ItemStack itemStack, @Nonnull Map<Supplier<C>, Consumer<ServerPlayer>> actions) {
        buttons.removeIf(g -> g.getSlot() == slot);
        buttons.add(new GUIButton<>(slot, itemStack, actions.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().get(), Entry::getValue))));
        addItem(slot, itemStack);
    }

    @Override
    protected boolean isCorrectInventory(@Nonnull Container container) {
        return container.get(Keys.DISPLAY_NAME).filter(name -> name.equals(this.name) && player.uniqueId().equals(container.viewer().uniqueId())).isPresent();
    }

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
    public void handle(Cause cause, Container container) {
        cause.first(ServerPlayer.class).filter(p -> isCorrectInventory(container) && p.uniqueId().equals(container.viewer().uniqueId())).ifPresent(player -> extraCloseHandler.accept(container));
    }

    @Override
    protected void removeItem(int slot) {
        inventory.set(slot, ItemStack.empty());
    }
}
