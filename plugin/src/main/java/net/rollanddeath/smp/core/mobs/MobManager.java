package net.rollanddeath.smp.core.mobs;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class MobManager {

    private final RollAndDeathSMP plugin;
    private final Map<MobType, CustomMob> mobs = new HashMap<>();

    public MobManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    public void registerMob(CustomMob mob) {
        mobs.put(mob.getType(), mob);
        plugin.getServer().getPluginManager().registerEvents(mob, plugin);
    }

    public CustomMob getMob(MobType type) {
        return mobs.get(type);
    }

    public LivingEntity spawnMob(MobType type, Location location) {
        CustomMob mob = mobs.get(type);
        if (mob != null) {
            return mob.spawn(location);
        }
        return null;
    }
}
