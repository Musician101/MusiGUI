package io.musician101.musigui.common.chest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

/**
 * A class for creating inventory based GUIs.
 *
 * @param <ClickType>       The click type class used by the implementation.
 * @param <Inventory>       The inventory class used by the implementation.
 * @param <PluginContainer> The plugin object that's registering this GUI.
 * @param <Player>          The player class used by the implementation
 * @param <ItemStack>       The item stack class used by the implementation.
 * @param <Name>            The text type used by the implementation (i.e. String for Spigot, Component for SpongeAPI, etc.)
 * @param <InventoryView>   The viewable inventory class used by the inventory.
 * @param <InventoryClose>  The event class that's fired when the inventory is closed.
 */
public abstract class ChestGUI<ClickType, Inventory, PluginContainer, Player, ItemStack, Name, InventoryView, InventoryClose> {

    /**
     * The player interacting with this GUI.
     */
    @Nonnull
    protected final Player player;
    /**
     * The buttons that are in this GUI.
     */
    @Nonnull
    protected List<GUIButton<ClickType, Player, ItemStack>> buttons = new ArrayList<>();
    /**
     * The inventory we're using as a GUI.
     */
    @Nonnull
    protected Inventory inventory;
    /**
     * The name of this GUI.
     */
    @Nonnull
    protected Name name;
    /**
     * The plugin object for registering/unregistering listeners.
     */
    @Nonnull
    protected PluginContainer plugin;

    /**
     * @param inventory  The inventory
     * @param name       The name of the GUI
     * @param player     The player using the GUI
     * @param plugin     The plugin that's registering the GUI
     * @param manualOpen When set to false, the GUI is opened automatically.
     */
    protected ChestGUI(@Nonnull Inventory inventory, @Nonnull Name name, @Nonnull Player player, @Nonnull PluginContainer plugin, boolean manualOpen) {
        this.inventory = inventory;
        this.name = name;
        this.player = player;
        this.plugin = plugin;
        if (!manualOpen) {
            open();
        }
    }

    /**
     * Add an item.
     *
     * @param slot      The slot to put the item in.
     * @param itemStack The item to be added.
     */
    protected abstract void addItem(int slot, @Nonnull ItemStack itemStack);

    /**
     * Check if the inventory is our GUI.
     *
     * @param inventoryView The inventory to be tested.
     * @return True if the inventory is our GUI.
     */
    protected abstract boolean isCorrectInventory(@Nonnull InventoryView inventoryView);

    /**
     * Open the GUI.
     */
    public abstract void open();

    /**
     * Remove a button.
     *
     * @param slot The slot to be removed.
     */
    public void removeButton(int slot) {
        buttons.removeIf(g -> g.getSlot() == slot);
        removeItem(slot);
    }

    /**
     * Remove an item.
     *
     * @param slot The slot to be removed.
     */
    protected abstract void removeItem(int slot);

    /**
     * Set a button.
     *
     * @param slot      The slot to set.
     * @param itemStack The item to put in the slot.
     */
    public void setButton(int slot, @Nonnull ItemStack itemStack) {
        setButton(slot, itemStack, Map.of());
    }

    /**
     * Set a button.
     *
     * @param slot      The slot to set.
     * @param itemStack The item to put in the slot.
     * @param clickType The type of click that triggers the button.
     * @param action    What happens when the button is clicked.
     */
    public void setButton(int slot, @Nonnull ItemStack itemStack, @Nonnull ClickType clickType, @Nonnull Consumer<Player> action) {
        setButton(slot, itemStack, Map.of(clickType, action));
    }

    /**
     * Set a button.
     *
     * @param slot      The slot to set.
     * @param itemStack The item to put in the slot.
     * @param actions   A map of click types as the key, and actions as the values.
     */
    public void setButton(int slot, @Nonnull ItemStack itemStack, @Nonnull Map<ClickType, Consumer<Player>> actions) {
        buttons.removeIf(g -> g.getSlot() == slot);
        buttons.add(new GUIButton<>(slot, itemStack, actions));
        addItem(slot, itemStack);
    }

    /**
     * Set a button.
     *
     * @param button The button to set.
     */
    public void setButton(@Nonnull GUIButton<ClickType, Player, ItemStack> button) {
        setButtons(List.of(button));
    }

    /**
     * Set a button.
     *
     * @param buttons The buttons to set.
     */
    public void setButtons(@Nonnull List<GUIButton<ClickType, Player, ItemStack>> buttons) {
        buttons.forEach(button -> {
            this.buttons.removeIf(b -> b.getSlot() == button.getSlot());
            this.buttons.add(button);
            addItem(button.getSlot(), button.getItemStack());
        });

    }

}
