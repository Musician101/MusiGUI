package io.musician101.musigui.spigot.chest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * For help creating in-line assignment
 */
public final class SpigotIconUtil {

    private SpigotIconUtil() {

    }

    public static ItemStack addEnchantment(@NotNull ItemStack itemStack, @NotNull Enchantment enchantment, int level) {
        return addEnchantments(itemStack, Map.of(enchantment, level));
    }

    /**
     * Warning that this will set the level to 1.
     */
    public static ItemStack addEnchantmentType(@NotNull ItemStack itemStack, @NotNull Enchantment enchantmentType) {
        return addEnchantmentTypes(itemStack, List.of(enchantmentType));
    }

    /**
     * Warning that this will set the levels to 1.
     */
    public static ItemStack addEnchantmentTypes(@NotNull ItemStack itemStack, @NotNull List<Enchantment> enchantmentTypes) {
        return addEnchantments(itemStack, enchantmentTypes.stream().collect(Collectors.toMap(e -> e, e -> 1)));
    }

    public static ItemStack addEnchantments(@NotNull ItemStack itemStack, @NotNull Map<Enchantment, Integer> enchantments) {
        getMeta(itemStack, ItemMeta.class).ifPresent(m -> {
            enchantments.forEach((enchantment, level) -> {
                if (m instanceof EnchantmentStorageMeta esm) {
                    esm.addStoredEnchant(enchantment, 1, true);
                }
                else {
                    m.addEnchant(enchantment, level, true);
                }
            });

            itemStack.setItemMeta(m);
        });
        return itemStack;
    }

    public static ItemStack addLore(@NotNull ItemStack itemStack, @NotNull String lore) {
        return addLore(itemStack, List.of(lore));
    }

    public static ItemStack addLore(@NotNull ItemStack itemStack, @NotNull List<String> lore) {
        getMeta(itemStack, ItemMeta.class).ifPresent(m -> {
            List<String> oldLore = m.getLore();
            List<String> list = oldLore == null ? new ArrayList<>() : new ArrayList<>(oldLore);
            list.addAll(lore);
            m.setLore(list);
            itemStack.setItemMeta(m);
        });

        return itemStack;
    }

    public static ItemStack addPotionEffect(@NotNull ItemStack itemStack, @NotNull PotionEffect potionEffect) {
        return addPotionEffects(itemStack, List.of(potionEffect));
    }

    public static ItemStack addPotionEffectType(@NotNull ItemStack itemStack, @NotNull PotionEffectType potionEffectType) {
        return addPotionEffectTypes(itemStack, List.of(potionEffectType));
    }

    public static ItemStack addPotionEffectTypes(@NotNull ItemStack itemStack, @NotNull List<PotionEffectType> potionEffectTypes) {
        return addPotionEffects(itemStack, potionEffectTypes.stream().map(p -> new PotionEffect(p, 1, 1)).toList());
    }

    public static ItemStack addPotionEffects(@NotNull ItemStack itemStack, @NotNull List<PotionEffect> potionEffects) {
        getMeta(itemStack, PotionMeta.class).ifPresent(m -> {
            potionEffects.forEach(p -> m.addCustomEffect(p, true));
            itemStack.setItemMeta(m);
        });
        return itemStack;
    }

    public static ItemStack customName(@NotNull ItemStack itemStack, @NotNull String name) {
        getMeta(itemStack, ItemMeta.class).ifPresent(m -> {
            m.setDisplayName(name);
            itemStack.setItemMeta(m);
        });

        return itemStack;
    }

    public static ItemStack durability(@NotNull ItemStack itemStack, int durability) {
        getMeta(itemStack, Damageable.class).ifPresent(m -> {
            m.setDamage(durability);
            itemStack.setItemMeta(m);
        });
        return itemStack;
    }

    private static <M extends ItemMeta> Optional<M> getMeta(ItemStack itemStack, Class<M> metaClass) {
        return Optional.ofNullable(itemStack.getItemMeta()).filter(metaClass::isInstance).map(metaClass::cast);
    }

    public static ItemStack quantity(@NotNull ItemStack itemStack, int quantity) {
        itemStack.setAmount(Math.min(quantity, itemStack.getMaxStackSize()));
        return itemStack;
    }

    /**
     * Warning that this will set the level to 1, and all previous enchantments will be removed.
     */
    public static ItemStack setEnchantmentType(@NotNull ItemStack itemStack, @NotNull Enchantment enchantmentType) {
        return setEnchantmentTypes(itemStack, List.of(enchantmentType));
    }

    /**
     * Warning that this will set the levels to 1, and all previous enchantments will be removed.
     */
    public static ItemStack setEnchantmentTypes(@NotNull ItemStack itemStack, @NotNull List<Enchantment> enchantmentTypes) {
        return setEnchantments(itemStack, enchantmentTypes.stream().collect(Collectors.toMap(e -> e, e -> 1)));
    }

    /**
     * Warning that all previous enchantments will be removed.
     */
    public static ItemStack setEnchantments(@NotNull ItemStack itemStack, @NotNull Enchantment enchantment, int level) {
        return setEnchantments(itemStack, Map.of(enchantment, level));
    }

    /**
     * Warning that all previous enchantments will be removed.
     */
    public static ItemStack setEnchantments(@NotNull ItemStack itemStack, @NotNull Map<Enchantment, Integer> enchantments) {
        getMeta(itemStack, ItemMeta.class).ifPresent(m -> {

            if (m instanceof EnchantmentStorageMeta esm) {
                esm.getStoredEnchants().clear();
                enchantments.forEach((enchantment, integer) -> esm.addStoredEnchant(enchantment, integer, true));
            }
            else {
                m.getEnchants().clear();
                enchantments.forEach((enchantment, integer) -> m.addEnchant(enchantment, integer, true));
            }

            itemStack.setItemMeta(m);
        });

        return addEnchantments(itemStack, enchantments);
    }

    /**
     * Warning that all previous lore will be removed.
     */
    public static ItemStack setLore(@NotNull ItemStack itemStack, @NotNull String lore) {
        return setLore(itemStack, List.of(lore));
    }

    /**
     * Warning that all previous lore will be removed.
     */
    public static ItemStack setLore(@NotNull ItemStack itemStack, @NotNull List<String> lore) {
        getMeta(itemStack, ItemMeta.class).ifPresent(m -> {
            m.setLore(lore);
            itemStack.setItemMeta(m);
        });

        return itemStack;
    }

    /**
     * Warning that all previous potion effects will be removed.
     */
    public static ItemStack setPotionEffectType(@NotNull ItemStack itemStack, @NotNull PotionEffectType potionEffectType) {
        return setPotionEffectTypes(itemStack, List.of(potionEffectType));
    }

    /**
     * Warning that all previous potion effects will be removed.
     */
    public static ItemStack setPotionEffectTypes(@NotNull ItemStack itemStack, @NotNull List<PotionEffectType> potionEffectTypes) {
        return setPotionEffects(itemStack, potionEffectTypes.stream().map(p -> new PotionEffect(p, 1, 1)).toList());
    }

    /**
     * Warning that all previous potion effects will be removed.
     */
    public static ItemStack setPotionEffects(@NotNull ItemStack itemStack, @NotNull PotionEffect potionEffect) {
        return setPotionEffects(itemStack, List.of(potionEffect));
    }

    /**
     * Warning that all previous potion effects will be removed.
     */
    public static ItemStack setPotionEffects(@NotNull ItemStack itemStack, @NotNull List<PotionEffect> potionEffects) {
        getMeta(itemStack, PotionMeta.class).ifPresent(m -> {
            m.clearCustomEffects();
            itemStack.setItemMeta(m);
        });
        return addPotionEffects(itemStack, potionEffects);
    }
}
