package net.rollanddeath.smp.core.mobs;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MobManager {

    private final RollAndDeathSMP plugin;
    private final Map<String, CustomMob> mobs = new HashMap<>();

    public MobManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    public void registerMob(CustomMob mob) {
        CustomMob previous = mobs.put(mob.getId(), mob);
        if (previous != null) {
            try {
                HandlerList.unregisterAll(previous);
                plugin.getLogger().info("Mob override aplicado: " + mob.getId());
            } catch (Exception ignored) {
            }
        }

        plugin.getServer().getPluginManager().registerEvents(mob, plugin);
    }

    public CustomMob getMob(String id) {
        return mobs.get(id);
    }

    public LivingEntity spawnMob(String id, Location location) {
        CustomMob mob = mobs.get(id);
        if (mob != null) {
            return mob.spawn(location);
        }
        return null;
    }

    public Set<String> getMobIds() {
        return mobs.keySet();
    }
}
