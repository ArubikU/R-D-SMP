package net.rollanddeath.smp.core.mobs;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DailyMobRotationManager {

    private final RollAndDeathSMP plugin;
    private final List<MobType> rotationOrder = new ArrayList<>();
    private final List<MobType> activeMobs = new ArrayList<>();
    private final Random random = new Random();
    private int lastDay = -1;

    public DailyMobRotationManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        loadState();
    }

    private void loadState() {
        // Load rotation order
        List<String> savedOrder = plugin.getConfig().getStringList("game.mob_rotation_order");
        if (savedOrder == null || savedOrder.isEmpty()) {
            // Initialize random order if not exists
            List<MobType> allMobs = new ArrayList<>(Arrays.asList(MobType.values()));
            Collections.shuffle(allMobs, new Random(plugin.getConfig().getLong("game.seed", System.currentTimeMillis())));
            rotationOrder.addAll(allMobs);
            
            // Save the order
            plugin.getConfig().set("game.mob_rotation_order", rotationOrder.stream().map(Enum::name).collect(Collectors.toList()));
            plugin.saveConfig();
        } else {
            for (String name : savedOrder) {
                try {
                    rotationOrder.add(MobType.valueOf(name));
                } catch (IllegalArgumentException ignored) {}
            }
            
            // If new mobs were added to enum but not in config, append them
            for (MobType type : MobType.values()) {
                if (!rotationOrder.contains(type)) {
                    rotationOrder.add(type);
                }
            }
        }

        // Load active mobs (though we can reconstruct this from day)
        lastDay = plugin.getConfig().getInt("game.last_mob_day", 0);
        refreshForDay(plugin.getGameManager().getCurrentDay());
    }

    public void refreshForDay(int day) {
        if (rotationOrder.isEmpty()) return;

        // Ensure day is within bounds (1 to 31+)
        int count = Math.min(day, rotationOrder.size());
        
        activeMobs.clear();
        for (int i = 0; i < count; i++) {
            activeMobs.add(rotationOrder.get(i));
        }
        
        if (day != lastDay) {
            lastDay = day;
            plugin.getConfig().set("game.last_mob_day", lastDay);
            plugin.saveConfig();
            
            // Announce new mob
            if (day <= rotationOrder.size()) {
                MobType newMob = rotationOrder.get(day - 1);
                plugin.getServer().broadcast(net.kyori.adventure.text.Component.text("¡Nueva amenaza detectada! " + newMob.getDisplayName() + " ha entrado al mundo.", net.kyori.adventure.text.format.NamedTextColor.RED));
            }
        }
    }

    public List<MobType> getActiveMobs() {
        return new ArrayList<>(activeMobs);
    }

    public MobType getReplacement(EntityType vanillaType) {
        List<MobType> candidates = new ArrayList<>();
        
        for (MobType type : activeMobs) {
            CustomMob mob = plugin.getMobManager().getMob(type);
            if (mob != null && mob.getEntityType() == vanillaType) {
                candidates.add(type);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        // If multiple candidates, pick one randomly
        return candidates.get(random.nextInt(candidates.size()));
    }
    
    public double getSpawnChance(MobType type) {
        // Define spawn chances based on rarity/power
        // This could be moved to MobType or CustomMob if needed
        switch (type) {
            // Common - High chance
            case CAVE_RAT:
            case LESSER_PHANTOM:
            case MAGMA_SLIME:
            case JUMPING_SPIDER:
            case MINER_ZOMBIE:
            case WANDERING_SKELETON:
            case WET_CREEPER:
                return 1.0; // Always replace

            // Rare - Medium chance
            case ARMORED_SKELETON:
            case SPEED_ZOMBIE:
            case THE_HIVE:
            case VENGEFUL_SPIRIT:
            case CORRUPTED_GOLEM:
            case ICE_CREEPER:
            case GIANT_PHANTOM:
            case SWAMP_WITCH:
                return 0.25; // 25% chance

            // Epic - Low chance
            case THE_STALKER:
            case BONE_TURRET:
            case SHADOW:
            case BLUE_BLAZE:
            case MIMIC_SHULKER:
            case ELITE_SPIDER_JOCKEY:
            case MAD_EVOKER:
                return 0.10; // 10% chance

            // Legendary - Very low chance (Bosses)
            case APOCALYPSE_KNIGHT:
            case LEVIATHAN:
            case RAT_KING:
            case AWAKENED_WARDEN:
            case ALPHA_DRAGON:
            case THE_REAPER:
            case SLIME_KING:
            case BANSHEE:
            case VOID_WALKER:
                return 0.02; // 2% chance

            default:
                return 0.1;
        }
    }
}
