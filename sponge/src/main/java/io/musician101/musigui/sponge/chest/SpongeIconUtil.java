package io.musician101.musigui.sponge.chest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Ticks;

/**
 * For help creating in-line assignment when the built-in {@link ItemStack.Builder} isn't enough
 */
public class SpongeIconUtil {

    private SpongeIconUtil() {

    }

    public static ItemStack addEnchantment(@Nonnull ItemStack itemStack, @Nonnull Enchantment Enchantment) {
        return addEnchantments(itemStack, List.of(Enchantment));
    }

    public static ItemStack addEnchantmentType(@Nonnull ItemStack itemStack, @Nonnull Supplier<? extends EnchantmentType> EnchantmentType) {
        return addEnchantmentType(itemStack, EnchantmentType.get());
    }

    public static ItemStack addEnchantmentType(@Nonnull ItemStack itemStack, @Nonnull EnchantmentType EnchantmentType) {
        return addEnchantmentTypes(itemStack, List.of(EnchantmentType));
    }

    public static ItemStack addEnchantmentTypes(@Nonnull ItemStack itemStack, @Nonnull List<EnchantmentType> EnchantmentTypes) {
        return addEnchantments(itemStack, EnchantmentTypes.stream().map(e -> Enchantment.of(e, 1)).toList());
    }

    public static ItemStack addEnchantments(@Nonnull ItemStack itemStack, @Nonnull List<Enchantment> Enchantment) {
        List<Enchantment> list = new ArrayList<>(itemStack.get(Keys.APPLIED_ENCHANTMENTS).orElse(List.of()));
        list.addAll(Enchantment);
        return offer(itemStack, Keys.APPLIED_ENCHANTMENTS, list);
    }

    public static ItemStack addLore(@Nonnull ItemStack itemStack, @Nonnull Component lore) {
        return addLore(itemStack, List.of(lore));
    }

    public static ItemStack addLore(@Nonnull ItemStack itemStack, @Nonnull List<Component> lore) {
        List<Component> list = new ArrayList<>(itemStack.get(Keys.LORE).orElse(List.of()));
        list.addAll(lore);
        return offer(itemStack, Keys.LORE, list);
    }

    public static ItemStack addPotionEffect(@Nonnull ItemStack itemStack, @Nonnull PotionEffect potionEffect) {
        return addPotionEffects(itemStack, List.of(potionEffect));
    }

    public static ItemStack addPotionEffectType(@Nonnull ItemStack itemStack, @Nonnull Supplier<? extends PotionEffectType> potionEffectType) {
        return addPotionEffectType(itemStack, potionEffectType.get());
    }

    public static ItemStack addPotionEffectType(@Nonnull ItemStack itemStack, @Nonnull PotionEffectType potionEffectType) {
        return addPotionEffectTypes(itemStack, List.of(potionEffectType));
    }

    public static ItemStack addPotionEffectTypes(@Nonnull ItemStack itemStack, @Nonnull List<PotionEffectType> potionEffectTypes) {
        return addPotionEffects(itemStack, potionEffectTypes.stream().map(p -> PotionEffect.of(p, 1, Ticks.of(1))).toList());
    }

    public static ItemStack addPotionEffects(@Nonnull ItemStack itemStack, @Nonnull List<PotionEffect> potionEffect) {
        List<PotionEffect> list = new ArrayList<>(itemStack.get(Keys.POTION_EFFECTS).orElse(List.of()));
        list.addAll(potionEffect);
        return offer(itemStack, Keys.POTION_EFFECTS, list);
    }

    public static ItemStack customName(@Nonnull ItemStack itemStack, @Nonnull Component name) {
        return offer(itemStack, Keys.CUSTOM_NAME, name);
    }

    public static ItemStack durability(@Nonnull ItemStack itemStack, int durability) {
        return offer(itemStack, Keys.ITEM_DURABILITY, durability);
    }

    public static <E> ItemStack offer(@Nonnull ItemStack itemStack, @Nonnull Supplier<Key<? extends Value<E>>> key, E value) {
        itemStack.offer(key, value);
        return itemStack;
    }

    public static <E> ItemStack offer(@Nonnull ItemStack itemStack, @Nonnull Key<? extends Value<E>> key, E value) {
        itemStack.offer(key, value);
        return itemStack;
    }

    public static ItemStack quantity(@Nonnull ItemStack itemStack, int quantity) {
        itemStack.setQuantity(Math.min(quantity, itemStack.maxStackQuantity()));
        return itemStack;
    }

    /**
     * Warning that this will set the level to 1, and all previous enchantments will be removed.
     */
    public static ItemStack setEnchantmentType(@Nonnull ItemStack itemStack, @Nonnull Supplier<? extends EnchantmentType> EnchantmentType) {
        return setEnchantmentType(itemStack, EnchantmentType.get());
    }

    /**
     * Warning that this will set the level to 1, and all previous enchantments will be removed.
     */
    public static ItemStack setEnchantmentType(@Nonnull ItemStack itemStack, @Nonnull EnchantmentType EnchantmentType) {
        return setEnchantmentTypes(itemStack, List.of(EnchantmentType));
    }

    /**
     * Warning that this will set the level to 1, and all previous enchantments will be removed.
     */
    public static ItemStack setEnchantmentTypes(@Nonnull ItemStack itemStack, @Nonnull List<EnchantmentType> EnchantmentTypes) {
        return setEnchantments(itemStack, EnchantmentTypes.stream().map(e -> Enchantment.of(e, 1)).toList());
    }

    /**
     * Warning that all previous enchantments will be removed.
     */
    public static ItemStack setEnchantments(@Nonnull ItemStack itemStack, @Nonnull Enchantment Enchantment) {
        return setEnchantments(itemStack, List.of(Enchantment));
    }

    /**
     * Warning that all previous enchantments will be removed.
     */
    public static ItemStack setEnchantments(@Nonnull ItemStack itemStack, @Nonnull List<Enchantment> Enchantment) {
        return offer(itemStack, Keys.APPLIED_ENCHANTMENTS, Enchantment);
    }

    /**
     * Warning that all previous lore will be removed.
     */
    public static ItemStack setLore(@Nonnull ItemStack itemStack, @Nonnull Component lore) {
        return setLore(itemStack, List.of(lore));
    }

    /**
     * Warning that all previous lore will be removed.
     */
    public static ItemStack setLore(@Nonnull ItemStack itemStack, @Nonnull List<Component> lore) {
        return offer(itemStack, Keys.LORE, lore);
    }

    /**
     * Warning that all previous potion effects will be removed.
     */
    public static ItemStack setPotionEffectType(@Nonnull ItemStack itemStack, @Nonnull Supplier<? extends PotionEffectType> potionEffectType) {
        return setPotionEffectType(itemStack, potionEffectType.get());
    }

    /**
     * Warning that all previous potion effects will be removed.
     */
    public static ItemStack setPotionEffectType(@Nonnull ItemStack itemStack, @Nonnull PotionEffectType potionEffectType) {
        return setPotionEffectTypes(itemStack, List.of(potionEffectType));
    }

    /**
     * Warning that all previous potion effects will be removed.
     */
    public static ItemStack setPotionEffectTypes(@Nonnull ItemStack itemStack, @Nonnull List<PotionEffectType> potionEffectTypes) {
        return setPotionEffects(itemStack, potionEffectTypes.stream().map(p -> PotionEffect.of(p, 1, Ticks.of(1))).toList());
    }

    /**
     * Warning that all previous potion effects will be removed.
     */
    public static ItemStack setPotionEffects(@Nonnull ItemStack itemStack, @Nonnull PotionEffect potionEffect) {
        return setPotionEffects(itemStack, List.of(potionEffect));
    }

    /**
     * Warning that all previous potion effects will be removed.
     */
    public static ItemStack setPotionEffects(@Nonnull ItemStack itemStack, @Nonnull List<PotionEffect> potionEffect) {
        return offer(itemStack, Keys.POTION_EFFECTS, potionEffect);
    }
}
