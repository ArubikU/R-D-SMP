package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Locale;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.scheduler.BukkitScheduler;

/** Adds or updates a keyed attribute modifier on the player by stacking delta amount. */
public final class StackPlayerAttributeModifierAction {
    private StackPlayerAttributeModifierAction() {
    }

    static void register() {
        ActionRegistrar.register("stack_player_attribute_modifier", StackPlayerAttributeModifierAction::parse, "stack_attribute_modifier");
    }

    private static Action parse(java.util.Map<?, ?> raw) {
        String attribute = Resolvers.string(null, raw, "attribute", "attr");
        String key = Resolvers.string(null, raw, "key", "modifier_key", "id");
        Double amount = Resolvers.doubleVal(null, raw, "amount");
        if (attribute == null || attribute.isBlank()) return null;
        if (key == null || key.isBlank()) return null;
        if (amount == null || amount == 0.0) return null;

        String opRaw = Resolvers.string(null, raw, "operation", "op");
        return ctx -> execute(ctx, attribute, key, amount, opRaw);
    }

    private static ActionResult execute(ScriptContext ctx, String attributeRaw, String keyRaw, double deltaAmount, String opRaw) {
        RollAndDeathSMP plugin = ctx.plugin();
        Player player = ctx.player();
        if (plugin == null || player == null) return ActionResult.ALLOW;

        Attribute attribute;
        try {
            attribute = Attribute.valueOf(attributeRaw.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
            return ActionResult.ALLOW;
        }

        AttributeModifier.Operation operation;
        try {
            operation = (opRaw != null && !opRaw.isBlank())
                ? AttributeModifier.Operation.valueOf(opRaw.trim().toUpperCase(Locale.ROOT))
                : AttributeModifier.Operation.ADD_NUMBER;
        } catch (Exception ignored) {
            operation = AttributeModifier.Operation.ADD_NUMBER;
        }

        String keyId = keyRaw.trim();
        if (keyId.isBlank()) return ActionResult.ALLOW;
        NamespacedKey key = new NamespacedKey(plugin, keyId);

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTask(plugin, () -> {
            try {
                AttributeInstance instance = player.getAttribute(attribute);
                if (instance == null) return;

                double current = 0.0;
                AttributeModifier existing = null;
                for (AttributeModifier modifier : instance.getModifiers()) {
                    if (modifier == null) continue;
                    if (key.equals(modifier.getKey())) {
                        existing = modifier;
                        current = modifier.getAmount();
                        break;
                    }
                }
                if (existing != null) {
                    instance.removeModifier(existing);
                }

                double next = current + deltaAmount;
                AttributeModifier newer = new AttributeModifier(key, next, operation, EquipmentSlotGroup.ANY);
                instance.addModifier(newer);
            } catch (Exception ignored) {
            }
        });

        return ActionResult.ALLOW;
    }
}
