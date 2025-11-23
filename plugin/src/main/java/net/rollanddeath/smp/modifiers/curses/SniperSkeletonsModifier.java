package net.rollanddeath.smp.modifiers.curses;

import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class SniperSkeletonsModifier extends Modifier {

    public SniperSkeletonsModifier(JavaPlugin plugin) {
        super(plugin, "Esqueletos Francotiradores", ModifierType.CURSE, "Los esqueletos disparan un 50% más rápido.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Skeleton skeleton) {
                    modifySkeleton(skeleton);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        // Hard to revert without storing original state or reloading chunks.
        // We can try to reset to default (20 ticks = 1s, usually it's 20-40).
        // Default is 20.
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Skeleton skeleton) {
                    resetSkeleton(skeleton);
                }
            }
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Skeleton skeleton) {
            modifySkeleton(skeleton);
        }
    }

    private void modifySkeleton(Skeleton skeleton) {
        AbstractSkeleton nmsSkeleton = (AbstractSkeleton) ((CraftEntity) skeleton).getHandle();
        
        nmsSkeleton.goalSelector.getAvailableGoals().forEach(wrappedGoal -> {
            if (wrappedGoal.getGoal() instanceof RangedBowAttackGoal<?> bowGoal) {
                try {
                    bowGoal.setMinAttackInterval(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void resetSkeleton(Skeleton skeleton) {
        AbstractSkeleton nmsSkeleton = (AbstractSkeleton) ((CraftEntity) skeleton).getHandle();
        
        nmsSkeleton.goalSelector.getAvailableGoals().forEach(wrappedGoal -> {
            if (wrappedGoal.getGoal() instanceof RangedBowAttackGoal<?> bowGoal) {
                try {
                    bowGoal.setMinAttackInterval(20);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
