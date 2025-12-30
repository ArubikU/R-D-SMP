package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Locale;
import java.util.Map;

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
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

/** Spawns a passenger for the subject mob, applies optional equipment, and seats it. */
public final class SpawnPassengerForMobAction {
    private SpawnPassengerForMobAction() {
    }

    static void register() {
        ActionRegistrar.register("spawn_passenger_for_mob", SpawnPassengerForMobAction::parse);
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String entityType = Resolvers.string(null, raw, "entity_type");
        if (entityType == null || entityType.isBlank()) return null;
        Double maxHealth = Resolvers.doubleVal(null, raw, "max_health");
        String name = Resolvers.string(null, raw, "name");
        Object equipmentObj = raw.get("equipment");
        @SuppressWarnings("unchecked")
        Map<?, ?> equipment = equipmentObj instanceof Map<?, ?> m ? m : null;
        return ctx -> execute(ctx, entityType, maxHealth, name, equipment);
    }

    private static ActionResult execute(ScriptContext ctx, String entityType, Double maxHealth, String name, Map<?, ?> equipmentRaw) {
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
            Entity passenger;
            try {
                passenger = loc.getWorld().spawnEntity(loc, et);
            } catch (Exception ignored) {
                return;
            }
            if (passenger instanceof LivingEntity le) {
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
                try {
                    applySimpleEquipment(le, equipmentRaw);
                } catch (Exception ignored) {
                }
            }
            try {
                mob.addPassenger(passenger);
            } catch (Exception ignored) {
            }
        });

        return ActionResult.ALLOW;
    }

    private static void applySimpleEquipment(LivingEntity entity, Map<?, ?> equipmentRaw) {
        if (entity == null || equipmentRaw == null || equipmentRaw.isEmpty()) return;
        EntityEquipment eq = entity.getEquipment();
        if (eq == null) return;

        setEqItem(eq::setHelmet, equipmentRaw.get("helmet"));
        setEqItem(eq::setChestplate, equipmentRaw.get("chestplate"));
        setEqItem(eq::setLeggings, equipmentRaw.get("leggings"));
        setEqItem(eq::setBoots, equipmentRaw.get("boots"));
        setEqItem(eq::setItemInMainHand, equipmentRaw.get("main_hand"));
        setEqItem(eq::setItemInOffHand, equipmentRaw.get("off_hand"));
    }

    private static void setEqItem(java.util.function.Consumer<ItemStack> setter, Object raw) {
        if (setter == null || raw == null) return;
        if (!(raw instanceof String s) || s.isBlank()) return;
        org.bukkit.Material mat;
        try {
            mat = org.bukkit.Material.valueOf(s.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
            return;
        }
        ItemStack item = new ItemStack(mat);
        try {
            setter.accept(item);
        } catch (Exception ignored) {
        }
    }
}
