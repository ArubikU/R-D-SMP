package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class InflationModifier extends Modifier {

    private final NamespacedKey inflatedKey;

    public InflationModifier(JavaPlugin plugin) {
        super(plugin, "Inflación", ModifierType.CURSE, "Aldeanos cobran x3 esmeraldas por todo.");
        this.inflatedKey = new NamespacedKey(plugin, "inflated_prices");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Villager villager) {
                    inflatePrices(villager);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Villager villager) {
                    deflatePrices(villager);
                }
            }
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Villager villager) {
            // Wait a tick for trades to generate if it's a new spawn?
            // Usually trades are generated when interacted or on spawn if it has profession.
            Bukkit.getScheduler().runTask(plugin, () -> inflatePrices(villager));
        }
    }

    @EventHandler
    public void onTradeAcquire(VillagerAcquireTradeEvent event) {
        if (event.getEntity() instanceof Villager villager) {
            // The event provides the recipe being acquired.
            MerchantRecipe recipe = event.getRecipe();
            multiplyRecipeCost(recipe);
            event.setRecipe(recipe);
        }
    }

    private void inflatePrices(Villager villager) {
        if (villager.getPersistentDataContainer().has(inflatedKey, PersistentDataType.BYTE)) return;

        List<MerchantRecipe> recipes = new ArrayList<>(villager.getRecipes());
        for (MerchantRecipe recipe : recipes) {
            multiplyRecipeCost(recipe);
        }
        villager.setRecipes(recipes);
        villager.getPersistentDataContainer().set(inflatedKey, PersistentDataType.BYTE, (byte) 1);
    }

    private void deflatePrices(Villager villager) {
        if (!villager.getPersistentDataContainer().has(inflatedKey, PersistentDataType.BYTE)) return;

        List<MerchantRecipe> recipes = new ArrayList<>(villager.getRecipes());
        for (MerchantRecipe recipe : recipes) {
            divideRecipeCost(recipe);
        }
        villager.setRecipes(recipes);
        villager.getPersistentDataContainer().remove(inflatedKey);
    }

    private void multiplyRecipeCost(MerchantRecipe recipe) {
        List<ItemStack> ingredients = recipe.getIngredients();
        for (ItemStack ingredient : ingredients) {
            int newAmount = ingredient.getAmount() * 3;
            if (newAmount > 64) newAmount = 64; // Cap at 64
            ingredient.setAmount(newAmount);
        }
        recipe.setIngredients(ingredients);
    }

    private void divideRecipeCost(MerchantRecipe recipe) {
        List<ItemStack> ingredients = recipe.getIngredients();
        for (ItemStack ingredient : ingredients) {
            int newAmount = Math.max(1, ingredient.getAmount() / 3);
            ingredient.setAmount(newAmount);
        }
        recipe.setIngredients(ingredients);
    }
}
