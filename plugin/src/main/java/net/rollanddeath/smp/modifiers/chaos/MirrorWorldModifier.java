package net.rollanddeath.smp.modifiers.chaos;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MirrorWorldModifier extends Modifier {

    public MirrorWorldModifier(RollAndDeathSMP plugin) {
        super(plugin, "Mundo Espejo", ModifierType.CHAOS, "Enderman pacíficos, Cerdos agresivos.");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Enderman enderman) {
                    modifyEnderman(enderman);
                } else if (entity instanceof Pig pig) {
                    modifyPig(pig);
                }
            }
        }
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Enderman enderman) {
            modifyEnderman(enderman);
        } else if (event.getEntity() instanceof Pig pig) {
            modifyPig(pig);
        }
    }

    private void modifyEnderman(Enderman enderman) {
        net.minecraft.world.entity.monster.EnderMan nmsEnderman = (net.minecraft.world.entity.monster.EnderMan) ((CraftEntity) enderman).getHandle();
        // Clear target selector to make them passive (remove aggression towards players/endermites)
        nmsEnderman.targetSelector.getAvailableGoals().clear();
    }

    private void modifyPig(Pig pig) {
        net.minecraft.world.entity.animal.Pig nmsPig = (net.minecraft.world.entity.animal.Pig) ((CraftEntity) pig).getHandle();
        
        // Add attack goals
        nmsPig.goalSelector.addGoal(1, new PigAttackGoal(nmsPig, 1.2D, false));
        nmsPig.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(nmsPig, net.minecraft.world.entity.player.Player.class, true));
    }

    private static class PigAttackGoal extends MeleeAttackGoal {
        public PigAttackGoal(net.minecraft.world.entity.PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
            super(mob, speedModifier, followingTargetEvenIfNotSeen);
        }

        @Override
        protected void checkAndPerformAttack(net.minecraft.world.entity.LivingEntity enemy) {
            if (this.canPerformAttack(enemy)) {
                this.resetAttackCooldown();
                this.mob.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                float damage = 2.0f;
                enemy.hurt(this.mob.damageSources().mobAttack(this.mob), damage);
            }
        }
    }
}
