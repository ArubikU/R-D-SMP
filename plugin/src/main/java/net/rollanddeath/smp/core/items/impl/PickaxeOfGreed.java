package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.items.CustomItemType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PickaxeOfGreed extends CustomItem {

    public PickaxeOfGreed(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.GREED_PICKAXE);
    }

    @Override
    protected ItemStack createBaseItem() {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 10, true);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected List<String> getLore() {
        return List.of("Fortuna X, pero te quita vida al picar.");
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!isItem(item)) return;

        // Damage player (0.5 hearts)
        if (player.getHealth() > 1) {
            player.damage(1.0);
        } else {
            // Maybe don't kill them, or do? "Greed kills".
            player.damage(100.0); // Kill them
        }
    }
}
