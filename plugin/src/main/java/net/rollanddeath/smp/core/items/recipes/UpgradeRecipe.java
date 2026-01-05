package net.rollanddeath.smp.core.items.recipes;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

public record UpgradeRecipe(
        NamespacedKey key,
        Station station,
        RecipeChoice template,
        RecipeChoice base,
        RecipeChoice addition,
        ItemStack result
) {

    public ItemStack resultCopy() {
        return result == null ? null : result.clone();
    }

    public enum Station {
        SMITHING,
        ANVIL;

        public static Station fromString(String raw) {
            if (raw == null) return SMITHING;
            String r = raw.trim().toLowerCase();
            return switch (r) {
                case "anvil", "yunque" -> ANVIL;
                default -> SMITHING;
            };
        }
    }
}
