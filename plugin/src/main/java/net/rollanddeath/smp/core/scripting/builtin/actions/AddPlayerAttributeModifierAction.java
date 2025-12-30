package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;

final class AddPlayerAttributeModifierAction {
    private AddPlayerAttributeModifierAction() {}

    static void register() {
        ActionRegistrar.register("add_player_attribute_modifier", AddPlayerAttributeModifierAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String attributeName = Resolvers.string(null, raw, "attribute");
        String key = Resolvers.string(null, raw, "key");
        String operationName = Resolvers.string(null, raw, "operation");
        Double amount = Resolvers.doubleVal(null, raw, "amount");
        if (attributeName == null || attributeName.isBlank() || key == null || key.isBlank() || operationName == null || operationName.isBlank() || amount == null) return null;
        String slotGroupName = Resolvers.string(null, raw, "slot_group");

        return ctx -> {
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

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

            final AttributeModifier mod = new AttributeModifier(new org.bukkit.NamespacedKey(ctx.plugin(), key), amount, op, slotGroup);

            ActionUtils.runSync(ctx.plugin(), () -> {
                AttributeInstance inst = player.getAttribute(attr);
                if (inst != null) {
                    for (AttributeModifier m : inst.getModifiers()) {
                        if (m.getKey().equals(mod.getKey())) {
                            inst.removeModifier(m);
                            break;
                        }
                    }
                    inst.addModifier(mod);
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
