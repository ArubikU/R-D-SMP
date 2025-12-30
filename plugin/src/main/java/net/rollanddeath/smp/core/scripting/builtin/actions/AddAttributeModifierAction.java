package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlotGroup;

final class AddAttributeModifierAction {
    private AddAttributeModifierAction() {}

    static void register() {
        ActionRegistrar.register("add_attribute_modifier", AddAttributeModifierAction::parse, "add_player_attribute_modifier");
    }

    private static Action parse(Map<?, ?> raw) {
        String attributeName = Resolvers.string(null, raw, "attribute");
        String key = Resolvers.string(null, raw, "key");
        String operationName = Resolvers.string(null, raw, "operation");
        Double amount = Resolvers.doubleVal(null, raw, "amount");
        if (attributeName == null || attributeName.isBlank() || key == null || key.isBlank() || operationName == null || operationName.isBlank() || amount == null) return null;
        String slotGroupName = Resolvers.string(null, raw, "slot_group");
        Object targetSpec = raw.get("target");

        return ctx -> {
            List<Entity> targets = Resolvers.resolveEntities(ctx, targetSpec);
            if (targets.isEmpty()) return ActionResult.ALLOW;

            Attribute attr;
            try {
                attr = Attribute.valueOf(attributeName.toUpperCase());
            } catch (Exception e) {
                return ActionResult.ALLOW;
            }

            AttributeModifier.Operation op;
            try {
                op = AttributeModifier.Operation.valueOf(operationName.toUpperCase());
            } catch (Exception e) {
                return ActionResult.ALLOW;
            }

            EquipmentSlotGroup slotGroup = EquipmentSlotGroup.ANY;
            if (slotGroupName != null && !slotGroupName.isBlank()) {
                try {
                    slotGroup = EquipmentSlotGroup.getByName(slotGroupName.toUpperCase());
                } catch (Exception ignored) {
                    try {
                        slotGroup = EquipmentSlotGroup.valueOf(slotGroupName.toUpperCase());
                    } catch (Exception ignored2) {}
                }
            }
            final EquipmentSlotGroup finalSlotGroup = slotGroup;

            final AttributeModifier mod = new AttributeModifier(new org.bukkit.NamespacedKey(ctx.plugin(), key), amount, op, finalSlotGroup);

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity e : targets) {
                    if (e instanceof LivingEntity le) {
                        AttributeInstance inst = le.getAttribute(attr);
                        if (inst != null) {
                            // Remove existing with same key if any
                            for (AttributeModifier m : inst.getModifiers()) {
                                if (m.getKey().equals(mod.getKey())) {
                                    inst.removeModifier(m);
                                    break;
                                }
                            }
                            inst.addModifier(mod);
                        }
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
