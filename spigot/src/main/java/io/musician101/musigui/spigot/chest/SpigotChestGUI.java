package io.musician101.musigui.spigot.chest;

import io.musician101.musigui.common.chest.ChestGUI;
import io.musician101.musigui.common.chest.GUIButton;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Spigot implementation of {@link ChestGUI}
 *
 * @param <J> The plugin registering the GUI.
 */
public abstract class SpigotChestGUI<J extends JavaPlugin> extends ChestGUI<ClickType, Inventory, J, Player, ItemStack, String, InventoryView, InventoryCloseEvent> implements Listener {

    /**
     * Spigot specific method to handle any extra actions for when a GUI is closed.
     *
     * @param event The {@link InventoryCloseEvent}
     */
    protected void handleExtraClose(InventoryCloseEvent event) {

    }

    /**
     * Spigot specific method to handle any extra actions for when an item is dragged in the GUI.
     *
     * @param event The {@link InventoryDragEvent}
     */
    protected void handleExtraDrag(InventoryDragEvent event) {

    }

    /**
     * Spigot specific method to handle any extra actions for when an item is dragged in the GUI.
     *
     * @param event The {@link InventoryClickEvent}
     */
    protected void handleExtraClick(InventoryClickEvent event) {

    }

    /**
     * Base Spigot implementation constructor
     *
     * @param player The player using the GUI.
     * @param name The name of the GUI.
     * @param size The number of slots in the GUI.
     * @param plugin The plugin registering the GUI.
     * @param manualOpen When set to false, the GUI is opened automatically.
     */
    protected SpigotChestGUI(@Nonnull Player player, @Nonnull String name, int size, @Nonnull J plugin, boolean manualOpen) {
        super(Bukkit.createInventory(player, size, name), name, player, plugin, manualOpen);
    }

    /**
     * @see ChestGUI#addItem(int, Object)
     */
    @Override
    protected void addItem(int slot, @Nonnull ItemStack itemStack) {
        inventory.setItem(slot, itemStack);
    }

    /**
     * @see ChestGUI#isCorrectInventory(Object)
     */
    @Override
    protected boolean isCorrectInventory(@Nonnull InventoryView inventoryView) {
        return inventoryView.getTitle().equals(name) && inventoryView.getPlayer().getUniqueId().equals(player.getUniqueId());
    }

    @EventHandler
    public final void onInventoryClick(InventoryClickEvent event) {
        InventoryView inventoryView = event.getView();
        if (isCorrectInventory(inventoryView)) {
            if (buttons.stream().map(GUIButton::getSlot).anyMatch(i -> i == event.getRawSlot())) {
                event.setCancelled(true);
                buttons.stream().filter(button -> button.getSlot() == event.getRawSlot()).findFirst().flatMap(button -> button.getAction(event.getClick())).ifPresent(consumer -> consumer.accept(player));
            }

            handleExtraClick(event);
        }
    }

    @EventHandler
    public final void onInventoryClose(InventoryCloseEvent event) {
        InventoryView inventory = event.getView();
        if (inventory.getTitle().equals(name) && player.getUniqueId().equals(inventory.getPlayer().getUniqueId())) {
            handleExtraClose(event);
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public final void onInventoryItemDrag(InventoryDragEvent event) {
        if (isCorrectInventory(event.getView())) {
            if (buttons.stream().map(GUIButton::getSlot).anyMatch(i -> event.getRawSlots().contains(i))) {
                event.setCancelled(true);
            }

            handleExtraDrag(event);
        }
    }

    /**
     * @see ChestGUI#open()
     */
    @Override
    public void open() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            inventory.clear();
            buttons.forEach(button -> inventory.setItem(button.getSlot(), button.getItemStack()));
            player.openInventory(inventory);
            Bukkit.getPluginManager().registerEvents(this, plugin);
        });
    }

    /**
     * @see ChestGUI#removeItem(int)
     */
    @Override
    protected void removeItem(int slot) {
        inventory.setItem(slot, null);
    }
}
