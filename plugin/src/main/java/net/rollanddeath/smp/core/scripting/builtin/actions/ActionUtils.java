package net.rollanddeath.smp.core.scripting.builtin.actions;

import java.util.List;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

final class ActionUtils {
    private ActionUtils() {}

    static void runSync(RollAndDeathSMP plugin, Runnable action) {
        if (plugin == null || action == null) return;
        if (Bukkit.isPrimaryThread()) {
            action.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, action);
        }
    }

    static void trySetSkeletonBowInterval(Skeleton skeleton, int interval) {
        if (skeleton == null) return;
        try {
            net.minecraft.world.entity.monster.AbstractSkeleton nms = (net.minecraft.world.entity.monster.AbstractSkeleton) ((CraftEntity) skeleton).getHandle();
            nms.goalSelector.getAvailableGoals().forEach(wrappedGoal -> {
                if (wrappedGoal.getGoal() instanceof net.minecraft.world.entity.ai.goal.RangedBowAttackGoal<?> bowGoal) {
                    try {
                        bowGoal.setMinAttackInterval(interval);
                    } catch (Exception ignored) {
                    }
                }
            });
        } catch (Exception ignored) {
        }
    }

    static void storeLootToPdc(Giant giant, List<ItemStack> items, NamespacedKey lootKey) {
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.set("c", items);
            String encoded = config.saveToString();
            giant.getPersistentDataContainer().set(lootKey, PersistentDataType.STRING, encoded);
        } catch (Exception ignored) {
        }
    }

    static List<ItemStack> retrieveLootFromPdc(Entity entity, NamespacedKey lootKey) {
        List<ItemStack> items = new java.util.ArrayList<>();
        try {
            String encoded = entity.getPersistentDataContainer().get(lootKey, PersistentDataType.STRING);
            if (encoded == null) return items;

            YamlConfiguration config = new YamlConfiguration();
            config.loadFromString(encoded);
            List<?> list = config.getList("c");
            if (list != null) {
                for (Object obj : list) {
                    if (obj instanceof ItemStack it) items.add(it);
                }
            }
        } catch (Exception ignored) {
        }
        return items;
    }

    static void setupGiantAI(Giant bukkitGiant) {
        try {
            net.minecraft.world.entity.monster.Giant nmsGiant = ((org.bukkit.craftbukkit.entity.CraftGiant) bukkitGiant).getHandle();
            nmsGiant.goalSelector.removeAllGoals(goal -> true);
            nmsGiant.targetSelector.removeAllGoals(goal -> true);

            nmsGiant.goalSelector.addGoal(0, new net.minecraft.world.entity.ai.goal.FloatGoal(nmsGiant));
            nmsGiant.goalSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.MeleeAttackGoal(nmsGiant, 1.0D, true));
            nmsGiant.goalSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal(nmsGiant, 1.0D));
            nmsGiant.goalSelector.addGoal(7, new net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal(nmsGiant, 1.0D));
            nmsGiant.goalSelector.addGoal(8, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(nmsGiant, net.minecraft.world.entity.player.Player.class, 8.0F));
            nmsGiant.goalSelector.addGoal(8, new net.minecraft.world.entity.ai.goal.RandomLookAroundGoal(nmsGiant));

            nmsGiant.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal(nmsGiant));
            nmsGiant.targetSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(nmsGiant, net.minecraft.world.entity.player.Player.class, true));
        } catch (Exception ignored) {
        }
    }
}
