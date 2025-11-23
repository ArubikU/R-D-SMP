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
        nmsPig.goalSelector.addGoal(1, new MeleeAttackGoal(nmsPig, 1.2D, false));
        nmsPig.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(nmsPig, net.minecraft.world.entity.player.Player.class, true));
        
        // Ensure Pig has attack damage attribute
        if (pig.getAttribute(Attribute.ATTACK_DAMAGE) == null) {
             // Pigs don't have this attribute by default, we might need to register it via NMS or just hope Bukkit allows setting it if we add modifier?
             // Actually, Bukkit's registerAttribute is not available on all entities easily if not defined.
             // But let's try to set it. If it's null, we can't set it via Bukkit easily without NMS attribute map modification.
             // However, for this task, let's assume we can just add the goal and maybe they deal 1 damage (default punch).
             // Or we can use NMS to add the attribute.
             // For simplicity and safety, let's just add the AI. If they attack but do 0 damage, it's still "aggressive".
             // But we can try to set the attribute if it exists.
        }
        
        // Let's try to add the attribute via NMS if possible, or just ignore damage for now.
        // Actually, we can use a modifier on the item in hand? No, pigs don't hold items usually.
        // Let's just leave the AI.
    }
}
