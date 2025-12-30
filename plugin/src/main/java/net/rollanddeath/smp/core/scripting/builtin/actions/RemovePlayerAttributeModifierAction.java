package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

final class RemovePlayerAttributeModifierAction {
    private RemovePlayerAttributeModifierAction() {}

    static void register() {
        ActionRegistrar.register("remove_player_attribute_modifier", RemovePlayerAttributeModifierAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String attributeName = Resolvers.string(null, raw, "attribute");
        String key = Resolvers.string(null, raw, "key");
        if (attributeName == null || attributeName.isBlank() || key == null || key.isBlank()) return null;

        return ctx -> {
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            Attribute attr;
            try {
                attr = Attribute.valueOf(attributeName.toUpperCase());
            } catch (Exception e) {
                return ActionResult.ALLOW;
            }

            NamespacedKey nsKey = new NamespacedKey(ctx.plugin(), key);

            ActionUtils.runSync(ctx.plugin(), () -> {
                AttributeInstance inst = player.getAttribute(attr);
                if (inst != null) {
                    for (AttributeModifier m : inst.getModifiers()) {
                        if (m.getKey().equals(nsKey)) {
                            inst.removeModifier(m);
                            break;
                        }
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }
}
