package net.rollanddeath.smp.modifiers.blessings;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FairTradeModifier extends Modifier {

    private final Random random = new Random();

    public FairTradeModifier(JavaPlugin plugin) {
        super(plugin, "Comercio Justo", ModifierType.BLESSING, "Piglin Bartering siempre da cosas buenas.");
    }

    @EventHandler
    public void onBarter(PiglinBarterEvent event) {
        List<ItemStack> outcome = event.getOutcome();
        List<ItemStack> newOutcome = new ArrayList<>();

        for (ItemStack item : outcome) {
            if (isBadItem(item.getType())) {
                newOutcome.add(getGoodItem());
            } else {
                newOutcome.add(item);
            }
        }
        
        // Ensure at least one item
        if (newOutcome.isEmpty()) {
            newOutcome.add(getGoodItem());
        }

        // Replace the outcome list (clear and addAll because getOutcome returns a mutable list usually, but setOutcome is safer if available? No setOutcome in older versions, but getOutcome is mutable)
        // Paper/Spigot: getOutcome() returns the list that will be dropped.
        outcome.clear();
        outcome.addAll(newOutcome);
    }

    private boolean isBadItem(Material type) {
        return type == Material.GRAVEL || 
               type == Material.BLACKSTONE || 
               type == Material.SOUL_SAND || 
               type == Material.NETHER_BRICK ||
               type == Material.ROTTEN_FLESH;
    }

    private ItemStack getGoodItem() {
        int r = random.nextInt(100);
        if (r < 20) return new ItemStack(Material.ENDER_PEARL, 2 + random.nextInt(3));
        if (r < 40) return new ItemStack(Material.IRON_NUGGET, 10 + random.nextInt(10));
        if (r < 60) return new ItemStack(Material.OBSIDIAN, 1);
        if (r < 80) return new ItemStack(Material.FIRE_CHARGE, 1 + random.nextInt(3));
        return new ItemStack(Material.LEATHER, 2 + random.nextInt(4));
    }
}
