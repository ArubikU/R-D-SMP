package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Locale;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

/** Spawns a mount for the current subject mob and seats the mob on it. */
public final class SpawnMountForMobAction {
    private SpawnMountForMobAction() {
    }

    static void register() {
        ActionRegistrar.register("spawn_mount_for_mob", SpawnMountForMobAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String entityType = Resolvers.string(null, raw, "entity_type");
        if (entityType == null || entityType.isBlank()) return null;
        Double maxHealth = Resolvers.doubleVal(null, raw, "max_health");
        String name = Resolvers.string(null, raw, "name");
        return ctx -> execute(ctx, entityType, maxHealth, name);
    }

    private static ActionResult execute(ScriptContext ctx, String entityType, Double maxHealth, String name) {
        RollAndDeathSMP plugin = ctx.plugin();
        if (plugin == null) return ActionResult.ALLOW;

        Object mobObj = ctx.getValue("SUBJECT");
        if (!(mobObj instanceof LivingEntity mob)) return ActionResult.ALLOW;
        Location loc = mob.getLocation();
        if (loc == null || loc.getWorld() == null) return ActionResult.ALLOW;

        EntityType et;
        try {
            et = EntityType.valueOf(entityType.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
            return ActionResult.ALLOW;
        }

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            Entity mount;
            try {
                mount = loc.getWorld().spawnEntity(loc, et);
            } catch (Exception ignored) {
                return;
            }
            if (mount instanceof LivingEntity le) {
                if (name != null && !name.isBlank()) {
                    try {
                        le.customName(MiniMessage.miniMessage().deserialize(name));
                    } catch (Exception ignored) {
                        le.customName(Component.text(name));
                    }
                    le.setCustomNameVisible(true);
                }
                if (maxHealth != null && maxHealth > 0) {
                    try {
                        var inst = le.getAttribute(Attribute.MAX_HEALTH);
                        if (inst != null) {
                            inst.setBaseValue(maxHealth);
                            le.setHealth(Math.min(maxHealth, inst.getValue()));
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
            try {
                mount.addPassenger(mob);
            } catch (Exception ignored) {
            }
        });

        return ActionResult.ALLOW;
    }
}
