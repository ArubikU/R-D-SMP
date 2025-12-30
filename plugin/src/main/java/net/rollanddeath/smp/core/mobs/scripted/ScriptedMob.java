package net.rollanddeath.smp.core.mobs.scripted;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.mobs.CustomMob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public final class ScriptedMob extends CustomMob {

    private final ScriptedMobDefinition def;

    public ScriptedMob(RollAndDeathSMP plugin, ScriptedMobDefinition def) {
        super(plugin, def.id(), def.displayName(), def.entityType());
        this.def = def;
    }

    public ScriptedMobDefinition definition() {
        return def;
    }

    @Override
    public LivingEntity spawn(Location location) {
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, getEntityType());

        String display = def.displayName();
        if (display != null && !display.isBlank()) {
            entity.customName(MiniMessage.miniMessage().deserialize(display));
            entity.setCustomNameVisible(true);
        }

        entity.addScoreboardTag("custom_mob");
        entity.addScoreboardTag(id);

        applyAttributes(entity);
        applyEquipment(entity);

        // `mob_spawn` desde mobs.yml no se dispara vía CreatureSpawnEvent para spawns del plugin,
        // porque las tags se añaden después del evento. Lo ejecutamos manualmente aquí.
        try {
            var scripted = plugin.getScriptedMobManager();
            if (scripted != null && scripted.runtime() != null) {
                scripted.runtime().onScriptedMobSpawn(entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.CUSTOM);
            }
        } catch (Exception ignored) {
        }

        return entity;
    }

    @Override
    protected void applyAttributes(LivingEntity entity) {
        if (def.attributes() != null) {
            for (var e : def.attributes().entrySet()) {
                String aName = e.getKey();
                Double v = e.getValue();
                if (aName == null || aName.isBlank() || v == null) continue;

                Attribute a;
                try {
                    a = Attribute.valueOf(aName.trim().toUpperCase(Locale.ROOT));
                } catch (Exception ex) {
                    a = null;
                }
                if (a == null) continue;

                var inst = entity.getAttribute(a);
                if (inst == null) continue;

                inst.setBaseValue(v);

                if (a == Attribute.MAX_HEALTH) {
                    try {
                        entity.setHealth(v);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    @Override
    protected void applyEquipment(LivingEntity entity) {
        ScriptedMobDefinition.Equipment eq = def.equipment();
        if (eq == null) return;

        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        setItem(equipment::setHelmet, eq.helmet());
        setItem(equipment::setChestplate, eq.chestplate());
        setItem(equipment::setLeggings, eq.leggings());
        setItem(equipment::setBoots, eq.boots());
        setItem(equipment::setItemInMainHand, eq.mainHand());
        setItem(equipment::setItemInOffHand, eq.offHand());
    }

    private void setItem(java.util.function.Consumer<ItemStack> setter, String materialRaw) {
        if (materialRaw == null || materialRaw.isBlank()) return;
        Material mat;
        try {
            mat = Material.valueOf(materialRaw.trim().toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            mat = null;
        }
        if (mat == null || mat == Material.AIR) return;
        setter.accept(new ItemStack(mat));
    }
}
