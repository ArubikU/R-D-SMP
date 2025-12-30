package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TrueSightHelmet extends CustomItem {

    private static final int RADIUS = 6;
    private static final int MAX_MARKERS = 64;
    private final Map<UUID, List<Entity>> oreMarkers = new HashMap<>();
    private final Map<UUID, Location> lastScan = new HashMap<>();

    public TrueSightHelmet(RollAndDeathSMP plugin) {
        super(plugin, "TRUE_SIGHT_HELMET");
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ItemStack helmet = player.getInventory().getHelmet();
                if (!isItem(helmet)) {
                    clearMarkers(player);
                    continue;
                }

                // Reveal nearby entities
                for (Entity entity : player.getNearbyEntities(20, 20, 20)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 0, false, false, false));
                    }
                }

                Location last = lastScan.get(player.getUniqueId());
                Location now = player.getLocation();
                if (last == null || last.getWorld() != now.getWorld() || last.distanceSquared(now) > 4) {
                    rescanOres(player, now);
                    lastScan.put(player.getUniqueId(), now);
                }
            }
        }, 20L, 20L);
    }

    private void rescanOres(Player player, Location center) {
        clearMarkers(player);

        List<Entity> markers = new ArrayList<>();
        int marked = 0;
        for (int x = -RADIUS; x <= RADIUS && marked < MAX_MARKERS; x++) {
            for (int y = -RADIUS; y <= RADIUS && marked < MAX_MARKERS; y++) {
                for (int z = -RADIUS; z <= RADIUS && marked < MAX_MARKERS; z++) {
                    Material type = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z).getType();
                    if (!isOre(type)) continue;

                    Location spot = center.clone().add(x + 0.5, y + 0.2, z + 0.5);
                    BlockDisplay display = center.getWorld().spawn(spot, BlockDisplay.class, bd -> {
                        bd.setBlock(Material.LIGHT_BLUE_STAINED_GLASS.createBlockData());
                        bd.setGlowing(true);
                        bd.setInvulnerable(true);
                        bd.setGravity(false);
                        bd.setPersistent(true);
                        bd.setTransformation(new Transformation(new Vector3f(-0.25f, -0.25f, -0.25f), new Quaternionf(), new Vector3f(0.5f, 0.5f, 0.5f), new Quaternionf()));
                    });
                    markers.add(display);
                    marked++;
                }
            }
        }

        if (!markers.isEmpty()) {
            oreMarkers.put(player.getUniqueId(), markers);
        }
    }

    private void clearMarkers(Player player) {
        List<Entity> list = oreMarkers.remove(player.getUniqueId());
        if (list != null) {
            list.forEach(Entity::remove);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        clearMarkers(event.getPlayer());
        lastScan.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!oreMarkers.containsKey(event.getPlayer().getUniqueId())) return;
        List<Entity> list = oreMarkers.get(event.getPlayer().getUniqueId());
        if (list == null) return;
        list.removeIf(as -> {
            if (as.getLocation().getBlock().equals(event.getBlock())) {
                as.remove();
                return true;
            }
            return false;
        });
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.GOLDEN_HELMET);
    }

    @Override
    public String getDisplayName() {
        return "Casco de la Visión Verdadera";
    }

    @Override
    protected Integer getCustomModelData() {
        return 710025;
    }

    @Override
    protected List<String> getLore() {
        return List.of("Ves mobs invisibles y ores a través de paredes.");
    }

    private static final Set<Material> ORE_MATERIALS = buildOreSet();

    private static Set<Material> buildOreSet() {
        EnumSet<Material> ores = EnumSet.noneOf(Material.class);
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
