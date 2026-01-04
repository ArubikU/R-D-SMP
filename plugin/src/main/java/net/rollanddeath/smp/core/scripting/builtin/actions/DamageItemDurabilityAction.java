package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

final class DamageItemDurabilityAction {
    private DamageItemDurabilityAction() {}

    static void register() {
        ActionRegistrar.register("damage_item_durability", DamageItemDurabilityAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        // slot estático (MAIN_HAND, OFF_HAND, etc.)
        String slotName = Resolvers.string(null, raw, "slot");
        EquipmentSlot staticSlot = Resolvers.resolveEquipmentSlot(slotName);
        
        // slot_key dinámico (EVENT.hand, variable, etc.)
        String slotKey = Resolvers.string(null, raw, "slot_key");
        
        // Debe haber al menos uno
        if (staticSlot == null && slotKey == null) return null;
        
        Integer amount = Resolvers.integer(null, raw, "amount");
        int a = amount != null ? Math.max(1, amount) : 1;

        return ctx -> {
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            
            // Resolver slot dinámicamente si hay slot_key
            EquipmentSlot slot = staticSlot;
            if (slotKey != null) {
                String resolved = Resolvers.string(ctx, slotKey);
                if (resolved != null) {
                    slot = Resolvers.resolveEquipmentSlot(resolved);
                }
            }
            if (slot == null) return ActionResult.ALLOW;
            
            final EquipmentSlot finalSlot = slot;
            ActionUtils.runSync(ctx.plugin(), () -> {
                player.damageItemStack(finalSlot, a);
            });
            return ActionResult.ALLOW;
        };
    }
}
