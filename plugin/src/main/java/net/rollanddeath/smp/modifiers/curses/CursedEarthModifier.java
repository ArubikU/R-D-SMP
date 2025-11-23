package net.rollanddeath.smp.modifiers.curses;

import net.rollanddeath.smp.core.modifiers.Modifier;
import net.rollanddeath.smp.core.modifiers.ModifierType;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CursedEarthModifier extends Modifier {

    private final NamespacedKey lootKey;

    public CursedEarthModifier(JavaPlugin plugin) {
        super(plugin, "Tierra Maldita", ModifierType.CURSE, "Al morir, un Gigante aparece en tu lugar con tu loot.");
        this.lootKey = new NamespacedKey(plugin, "giant_loot");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        List<ItemStack> drops = new ArrayList<>(event.getDrops());
        
        // Clear drops so they don't fall on the ground
        event.getDrops().clear();

        Giant giant = (Giant) player.getWorld().spawnEntity(player.getLocation(), EntityType.GIANT);
        
        // Setup Giant Stats
        if (giant.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null)
            giant.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100.0);
        giant.setHealth(100.0);
        if (giant.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null)
            giant.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);
        if (giant.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null)
            giant.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(15.0);
        if (giant.getAttribute(Attribute.GENERIC_FOLLOW_RANGE) != null)
            giant.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(32.0);

        giant.setCanPickupItems(false);
        giant.setRemoveWhenFarAway(false);

        // Equip Giant (Visuals only, drop chance 0 to avoid dupes with stored loot)
        EntityEquipment equipment = giant.getEquipment();
        if (equipment != null) {
            equipment.setArmorContents(player.getInventory().getArmorContents());
            equipment.setItemInMainHand(player.getInventory().getItemInMainHand());
            equipment.setItemInOffHand(player.getInventory().getItemInOffHand());
            
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                equipment.setDropChance(slot, 0f);
            }
        }

        // Save Loot to PDC
        storeLoot(giant, drops);

        // Setup NMS AI
        setupGiantAI(giant);
    }

    private void setupGiantAI(Giant bukkitGiant) {
        net.minecraft.world.entity.monster.Giant nmsGiant = ((org.bukkit.craftbukkit.entity.CraftGiant) bukkitGiant).getHandle();
        
        // Clear existing goals just in case (Giants usually have none)
        nmsGiant.goalSelector.removeAllGoals(goal -> true);
        nmsGiant.targetSelector.removeAllGoals(goal -> true);

        // Add AI Goals
        nmsGiant.goalSelector.addGoal(0, new net.minecraft.world.entity.ai.goal.FloatGoal(nmsGiant));
        nmsGiant.goalSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.MeleeAttackGoal(nmsGiant, 1.0D, true));
        nmsGiant.goalSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal(nmsGiant, 1.0D));
        nmsGiant.goalSelector.addGoal(7, new net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal(nmsGiant, 1.0D));
        nmsGiant.goalSelector.addGoal(8, new net.minecraft.world.entity.ai.goal.LookAtPlayerGoal(nmsGiant, net.minecraft.world.entity.player.Player.class, 8.0F));
        nmsGiant.goalSelector.addGoal(8, new net.minecraft.world.entity.ai.goal.RandomLookAroundGoal(nmsGiant));

        // Add Target Goals
        nmsGiant.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal(nmsGiant));
        nmsGiant.targetSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(nmsGiant, net.minecraft.world.entity.player.Player.class, true));
    }

    @EventHandler
    public void onGiantDeath(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.GIANT) return;
        
        PersistentDataContainer pdc = event.getEntity().getPersistentDataContainer();
        if (pdc.has(lootKey, PersistentDataType.STRING)) {
            List<ItemStack> loot = retrieveLoot(event.getEntity());
            event.getDrops().addAll(loot);
        }
    }

    private void storeLoot(Giant giant, List<ItemStack> items) {
        try {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
            os.writeInt(items.size());
            for (ItemStack item : items) {
                os.writeObject(item);
            }
            os.flush();
            String encoded = Base64.getEncoder().encodeToString(io.toByteArray());
            giant.getPersistentDataContainer().set(lootKey, PersistentDataType.STRING, encoded);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<ItemStack> retrieveLoot(org.bukkit.entity.Entity entity) {
        List<ItemStack> items = new ArrayList<>();
        try {
            String encoded = entity.getPersistentDataContainer().get(lootKey, PersistentDataType.STRING);
            if (encoded == null) return items;
            
            ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(encoded));
            BukkitObjectInputStream is = new BukkitObjectInputStream(in);
            int size = is.readInt();
            for (int i = 0; i < size; i++) {
                items.add((ItemStack) is.readObject());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}
