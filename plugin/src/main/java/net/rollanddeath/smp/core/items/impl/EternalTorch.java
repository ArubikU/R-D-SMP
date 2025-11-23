package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EternalTorch extends CustomItem {

    public EternalTorch(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.ETERNAL_TORCH);
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.TORCH);
    }

    @Override
    protected List<String> getLore() {
        return List.of("Nunca se gasta al colocarse");
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (isItem(event.getItemInHand())) {
            // Cancel item consumption
            // BlockPlaceEvent doesn't directly support not consuming item, 
            // but we can set the item in hand back to amount + 1 (or same amount if it was 1)
            // Actually, if we just want infinite torch, we can just give it back.
            ItemStack hand = event.getItemInHand();
            hand.setAmount(1); // Just keep it full or handle logic to not decrease.
            // Better way:
            event.getPlayer().getInventory().setItem(event.getHand(), hand);
        }
    }
}
