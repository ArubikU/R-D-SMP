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

final class RemoveAttributeModifierAction {
    private RemoveAttributeModifierAction() {}

    static void register() {
        ActionRegistrar.register("remove_attribute_modifier", RemoveAttributeModifierAction::parse, "remove_player_attribute_modifier");
    }

    private static Action parse(Map<?, ?> raw) {
        String attributeName = Resolvers.string(null, raw, "attribute");
        String key = Resolvers.string(null, raw, "key");
        if (attributeName == null || attributeName.isBlank() || key == null || key.isBlank()) return null;
        Object targetSpec = raw.get("target");

        return ctx -> {
            List<Entity> targets = Resolvers.entities(ctx, targetSpec);
            if (targets.isEmpty()) return ActionResult.ALLOW;

            Attribute attr = Resolvers.attribute(ctx, attributeName);
            if (attr == null) return ActionResult.ALLOW;

            org.bukkit.NamespacedKey nsKey = new org.bukkit.NamespacedKey(ctx.plugin(), key);

            ActionUtils.runSync(ctx.plugin(), () -> {
                for (Entity e : targets) {
                    if (e instanceof LivingEntity le) {
                        AttributeInstance inst = le.getAttribute(attr);
                        if (inst != null) {
                            for (AttributeModifier m : inst.getModifiers()) {
                                if (m.getKey().equals(nsKey)) {
                                    inst.removeModifier(m);
                                    // Don't break, remove all with same key? Usually key is unique per modifier but maybe not?
                                    // Keys are unique per modifier instance, but we are matching by NamespacedKey.
                                    // Usually only one.
                                }
                            }
                        }
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
