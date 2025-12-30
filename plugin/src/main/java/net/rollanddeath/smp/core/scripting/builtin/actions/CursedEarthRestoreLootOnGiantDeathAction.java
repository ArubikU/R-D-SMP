package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

final class CursedEarthRestoreLootOnGiantDeathAction {
    private CursedEarthRestoreLootOnGiantDeathAction() {}

    static void register() {
        ActionRegistrar.register("cursed_earth_restore_loot_on_giant_death", CursedEarthRestoreLootOnGiantDeathAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String keyName = Resolvers.string(null, raw, "key");
        if (keyName == null || keyName.isBlank()) return null;

        return ctx -> {
            EntityDeathEvent event = ctx.nativeEvent(EntityDeathEvent.class);
            if (event == null) return ActionResult.ALLOW;
            if (event.getEntityType() != EntityType.GIANT) return ActionResult.ALLOW;

            NamespacedKey lootKey = new NamespacedKey(ctx.plugin(), keyName);
            if (!event.getEntity().getPersistentDataContainer().has(lootKey, PersistentDataType.STRING)) return ActionResult.ALLOW;

            List<ItemStack> loot = ActionUtils.retrieveLootFromPdc(event.getEntity(), lootKey);
            if (!loot.isEmpty()) {
                event.getDrops().addAll(loot);
            }
            return ActionResult.ALLOW;
        };
    }
}
