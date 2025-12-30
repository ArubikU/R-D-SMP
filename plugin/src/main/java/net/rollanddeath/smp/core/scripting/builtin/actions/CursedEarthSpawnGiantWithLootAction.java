package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import java.util.Map;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Resolvers;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

final class CursedEarthSpawnGiantWithLootAction {
    private CursedEarthSpawnGiantWithLootAction() {}

    static void register() {
        ActionRegistrar.register("cursed_earth_spawn_giant_with_loot", CursedEarthSpawnGiantWithLootAction::parse);
    }

    private static Action parse(Map<?, ?> raw) {
        String keyName = Resolvers.string(null, raw, "key");
        if (keyName == null || keyName.isBlank()) return null;

        return ctx -> {
            PlayerDeathEvent event = ctx.nativeEvent(PlayerDeathEvent.class);
            if (event == null) return ActionResult.ALLOW;

            Player player = event.getEntity();
            if (player == null || player.getWorld() == null) return ActionResult.ALLOW;

            final NamespacedKey lootKey = new NamespacedKey(ctx.plugin(), keyName);
            ActionUtils.runSync(ctx.plugin(), () -> {
                List<ItemStack> drops = List.copyOf(event.getDrops());
                event.getDrops().clear();

                Entity spawned = player.getWorld().spawnEntity(player.getLocation(), EntityType.GIANT);
                if (!(spawned instanceof Giant giant)) return;

                if (giant.getAttribute(Attribute.MAX_HEALTH) != null) giant.getAttribute(Attribute.MAX_HEALTH).setBaseValue(100.0);
                giant.setHealth(100.0);
                if (giant.getAttribute(Attribute.MOVEMENT_SPEED) != null) giant.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.3);
                if (giant.getAttribute(Attribute.ATTACK_DAMAGE) != null) giant.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(15.0);
                if (giant.getAttribute(Attribute.FOLLOW_RANGE) != null) giant.getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(32.0);

                giant.setCanPickupItems(false);
                giant.setRemoveWhenFarAway(false);

                EntityEquipment equipment = giant.getEquipment();
                if (equipment != null) {
                    equipment.setArmorContents(player.getInventory().getArmorContents());
                    equipment.setItemInMainHand(player.getInventory().getItemInMainHand());
                    equipment.setItemInOffHand(player.getInventory().getItemInOffHand());
                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        try {
                            equipment.setDropChance(slot, 0f);
                        } catch (Exception ignored) {
                        }
                    }
                }

                ActionUtils.storeLootToPdc(giant, drops, lootKey);
                ActionUtils.setupGiantAI(giant);
            });

            return ActionResult.ALLOW;
        };
    }
}
