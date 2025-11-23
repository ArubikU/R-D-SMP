package net.rollanddeath.smp.modifiers.blessings;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class SkyGiftModifier extends Modifier {

    private BukkitRunnable task;
    private final Random random = new Random();

    public SkyGiftModifier(JavaPlugin plugin) {
        super(plugin, "Regalo del Cielo", ModifierType.BLESSING, "Cofre con suministros cae en Spawn cada hora.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                spawnGift();
            }
        };
        // 1 hour = 72000 ticks. Start delay 10s (200 ticks).
        task.runTaskTimer(plugin, 200L, 72000L);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        if (task != null) task.cancel();
    }

    private void spawnGift() {
        World world = Bukkit.getWorld("world"); // Default world
        if (world == null) return;

        Location spawn = world.getSpawnLocation();
        // Find highest block at spawn
        int y = world.getHighestBlockYAt(spawn);
        Location chestLoc = new Location(world, spawn.getX(), y + 1, spawn.getZ());
        
        chestLoc.getBlock().setType(Material.CHEST);
        if (chestLoc.getBlock().getState() instanceof Chest chest) {
            // Fill with random goodies
            chest.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 2));
            chest.getInventory().addItem(new ItemStack(Material.DIAMOND, random.nextInt(3) + 1));
            chest.getInventory().addItem(new ItemStack(Material.IRON_INGOT, random.nextInt(10) + 5));
            chest.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));
            chest.getInventory().addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 10));
            
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<green>¡Un regalo del cielo ha caído en el Spawn!"));
            world.strikeLightningEffect(chestLoc);
        }
    }
}
