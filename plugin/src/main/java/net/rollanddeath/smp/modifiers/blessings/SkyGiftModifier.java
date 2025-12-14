package net.rollanddeath.smp.modifiers.blessings;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import net.rollanddeath.smp.integration.discord.DiscordWebhookService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Random;

public class SkyGiftModifier extends Modifier {

    private BukkitRunnable task;
    private final Random random = new Random();
    private final DiscordWebhookService discordService;

    public SkyGiftModifier(JavaPlugin plugin) {
        super(plugin, "Regalo del Cielo", ModifierType.BLESSING, "Cofre con suministros cae en Spawn cada hora.");
        this.discordService = plugin instanceof RollAndDeathSMP smp ? smp.getDiscordService() : null;
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
        // Pick random spot within 150 blocks of spawn (uniform in circle)
        double angle = random.nextDouble() * Math.PI * 2;
        double radius = 150 * Math.sqrt(random.nextDouble());
        int dx = (int) Math.round(Math.cos(angle) * radius);
        int dz = (int) Math.round(Math.sin(angle) * radius);

        Location dropBase = spawn.clone().add(dx, 0, dz);
        int y = world.getHighestBlockYAt(dropBase);
        Location chestLoc = new Location(world, dropBase.getX(), y + 1, dropBase.getZ());
        
        chestLoc.getBlock().setType(Material.CHEST);
        if (chestLoc.getBlock().getState() instanceof Chest chest) {
            // Fill with random goodies
            chest.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 2));
            chest.getInventory().addItem(new ItemStack(Material.DIAMOND, random.nextInt(3) + 1));
            chest.getInventory().addItem(new ItemStack(Material.IRON_INGOT, random.nextInt(10) + 5));
            chest.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));
            chest.getInventory().addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 10));
            
            placeTorches(world, chestLoc);

            int bx = chestLoc.getBlockX();
            int bz = chestLoc.getBlockZ();
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize(
                    "<green>¡Un regalo del cielo ha caído cerca del spawn! Coords: X " + bx + " Z " + bz));
            world.strikeLightningEffect(chestLoc);

            // Notifica al webhook si está activo
            if (discordService != null && discordService.isEnabled()) {
                discordService.sendEventAnnouncement(
                        "Regalo del Cielo",
                        "Cofre en spawn: X " + bx + " Z " + bz,
                        NamedTextColor.GREEN
                );
            }
        }
    }

    private void placeTorches(World world, Location chestLoc) {
        int[][] offsets = new int[][]{ {1, 0}, {-1, 0}, {0, 1}, {0, -1} };
        for (int[] off : offsets) {
            Location torchLoc = chestLoc.clone().add(off[0], 0, off[1]);
            Location below = torchLoc.clone().add(0, -1, 0);
            if (!below.getBlock().getType().isSolid()) {
                continue;
            }
            if (torchLoc.getBlock().getType().isAir()) {
                torchLoc.getBlock().setType(Material.TORCH);
            }
        }
    }
}
