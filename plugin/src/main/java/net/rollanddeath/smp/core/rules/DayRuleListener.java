package net.rollanddeath.smp.core.rules;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;
import java.util.Set;

import net.rollanddeath.smp.core.scripting.ScriptVars;

public class DayRuleListener implements Listener {

    private final RollAndDeathSMP plugin;
    private final Random random = new Random();

    public DayRuleListener(RollAndDeathSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        var vars = ScriptVars.create()
                .event(event)
                .subject((Player) event.getDamager())
                .target((Player) event.getEntity())
                .build();
        if (plugin.getDayRuleManager().runScripts("entity_damage_by_entity", vars)) {
            event.setCancelled(true);
            return;
        }

        int currentDay = plugin.getGameManager().getCurrentDay();
        if (plugin.getDayRuleManager().isRuleActive(currentDay, RuleType.NO_PVP)) {
            event.setCancelled(true);
            event.getDamager().sendMessage(Component.text("El PvP está desactivado hoy.", NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onMobDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof org.bukkit.entity.Monster)) {
            return;
        }

        var vars = ScriptVars.create()
                .event(event)
                .subject(event.getDamager())
                .target(event.getEntity())
                .build();
        if (plugin.getDayRuleManager().runScripts("entity_damage_by_entity", vars)) {
            event.setCancelled(true);
            return;
        }

        int currentDay = plugin.getGameManager().getCurrentDay();
        double boost = plugin.getDayRuleManager().getCumulativeValue(currentDay, RuleType.MOB_DAMAGE_BOOST);
        
        if (boost > 0) {
            double original = event.getDamage();
            event.setDamage(original * (1.0 + boost));
        }
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        var vars = ScriptVars.create()
                .event(event)
                .subject(event.getPlayer())
                .build();
        if (plugin.getDayRuleManager().runScripts("player_bed_enter", vars)) {
            event.setCancelled(true);
            return;
        }

        int currentDay = plugin.getGameManager().getCurrentDay();
        if (plugin.getDayRuleManager().isRuleActive(currentDay, RuleType.NO_SLEEP)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Component.text("El insomnio te impide dormir hoy.", NamedTextColor.RED));
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        int currentDay = plugin.getGameManager().getCurrentDay();
        LivingEntity entity = event.getEntity();

        var vars = ScriptVars.create()
                .event(event)
                .subject(entity)
                .target(event.getEntity().getKiller())
                .build();
        plugin.getDayRuleManager().runScripts("entity_death", vars);

        // Loot Restriction
        Set<EntityType> restricted = plugin.getDayRuleManager().getRestrictedMobs(currentDay);
        if (restricted.contains(entity.getType())) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }

        // Ravager Buff (Totem Drop)
        if (entity instanceof Ravager) {
            double chance = plugin.getDayRuleManager().getLatestValue(currentDay, RuleType.RAVAGER_BUFF);
            if (chance > 0 && random.nextDouble() < chance) {
                event.getDrops().add(new ItemStack(Material.TOTEM_OF_UNDYING));
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return; // Avoid infinite loops if we spawn mobs

        int currentDay = plugin.getGameManager().getCurrentDay();
        LivingEntity entity = event.getEntity();

        var vars = ScriptVars.create()
                .event(event)
                .subject(entity)
                .build();
        if (plugin.getDayRuleManager().runScripts("creature_spawn", vars)) {
            event.setCancelled(true);
            return;
        }

        // Spider Buff
        if (entity.getType() == EntityType.SPIDER || entity.getType() == EntityType.CAVE_SPIDER) {
            double buffLevel = plugin.getDayRuleManager().getLatestValue(currentDay, RuleType.SPIDER_BUFF);
            if (buffLevel > 0) {
                applySpiderBuffs(entity, (int) buffLevel);
            }
        }

        // Double/Triple Mob Spawn
        if (entity instanceof org.bukkit.entity.Monster) {
            if (plugin.getDayRuleManager().isRuleActive(currentDay, RuleType.TRIPLE_MOB_SPAWN)) {
                spawnExtraMobs(entity, 2);
            } else if (plugin.getDayRuleManager().isRuleActive(currentDay, RuleType.DOUBLE_MOB_SPAWN)) {
                spawnExtraMobs(entity, 1);
            }
        }
    }

    private void applySpiderBuffs(LivingEntity spider, int level) {
        PotionEffectType[] possibleEffects = {
            PotionEffectType.SPEED,
            PotionEffectType.STRENGTH,
            PotionEffectType.REGENERATION,
            PotionEffectType.INVISIBILITY,
            PotionEffectType.RESISTANCE
        };

        int count;
        if (level <= 1) {
            count = 1 + random.nextInt(2); // 1-2 efectos
        } else if (level == 2) {
            count = 2 + random.nextInt(2); // 2-3 efectos
        } else if (level == 3) {
            count = 3; // 3 efectos garantizados
        } else if (level == 4) {
            count = 4; // 4 efectos garantizados
        } else {
            count = possibleEffects.length; // Nivel 5+: todos los efectos disponibles
        }

        for (int i = 0; i < count; i++) {
            PotionEffectType type = possibleEffects[random.nextInt(possibleEffects.length)];
            spider.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, 0));
        }
    }

    private void spawnExtraMobs(LivingEntity original, int count) {
        for (int i = 0; i < count; i++) {
            original.getWorld().spawnEntity(original.getLocation(), original.getType(), CreatureSpawnEvent.SpawnReason.CUSTOM);
        }
    }
}
