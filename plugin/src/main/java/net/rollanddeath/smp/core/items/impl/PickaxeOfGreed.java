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
import org.bukkit.Tag;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class PickaxeOfGreed extends CustomItem {

    public PickaxeOfGreed(RollAndDeathSMP plugin) {
        super(plugin, CustomItemType.GREED_PICKAXE);
    }

    @Override
    protected ItemStack createBaseItem() {
        ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.FORTUNE, 10, true);
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

        // Only punish when mining ores to match the item description.
        Material broken = event.getBlock().getType();
        if (!isOre(broken)) return;

        // Damage player (0.5 hearts)
        if (player.getHealth() > 1) {
            player.damage(1.0);
        } else {
            // Maybe don't kill them, or do? "Greed kills".
            player.damage(100.0); // Kill them
        }
    }
    
    private static final Set<Material> ORE_MATERIALS = buildOreSet();

    private static Set<Material> buildOreSet() {
        EnumSet<Material> ores = EnumSet.noneOf(Material.class);
        ores.add(Material.ANCIENT_DEBRIS);
        ores.addAll(Tag.COAL_ORES.getValues());
        ores.addAll(Tag.COPPER_ORES.getValues());
        ores.addAll(Tag.DIAMOND_ORES.getValues());
        ores.addAll(Tag.EMERALD_ORES.getValues());
        ores.addAll(Tag.GOLD_ORES.getValues());
        ores.addAll(Tag.IRON_ORES.getValues());
        ores.addAll(Tag.LAPIS_ORES.getValues());
        ores.addAll(Tag.REDSTONE_ORES.getValues());
        ores.add(Material.NETHER_GOLD_ORE);
        ores.add(Material.NETHER_QUARTZ_ORE);
        return ores;
    }

    private static boolean isOre(Material type) {
        return ORE_MATERIALS.contains(type);
    }

}