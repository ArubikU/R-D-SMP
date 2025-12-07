package net.rollanddeath.smp.core.mobs;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ScheduledMobManager {

    private record Entry(String id, MobType type, int unlockDay, double healthMultiplier, double damageMultiplier) {}

    private final RollAndDeathSMP plugin;
    private final List<Entry> entries;
    private final Random random = new Random();
    private BukkitTask task;

    public ScheduledMobManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.entries = buildEntries();
        start();
    }

    private List<Entry> buildEntries() {
        List<Entry> list = new ArrayList<>();
        // 31 planned unlocks, increasing difficulty
        list.add(new Entry("sentinela_cueva", MobType.CAVE_RAT, 1, 1.2, 1.0));
        list.add(new Entry("fantasma_hambriento", MobType.LESSER_PHANTOM, 1, 1.0, 1.0));
        list.add(new Entry("saltarin_venenoso", MobType.JUMPING_SPIDER, 2, 1.3, 1.1));
        list.add(new Entry("zombi_minero_elite", MobType.MINER_ZOMBIE, 2, 1.5, 1.2));
        list.add(new Entry("esqueleto_vagabundo_elite", MobType.WANDERING_SKELETON, 3, 1.5, 1.1));
        list.add(new Entry("creeper_mojado_cargado", MobType.WET_CREEPER, 3, 1.4, 1.4));
        list.add(new Entry("golem_roto", MobType.CORRUPTED_GOLEM, 4, 1.6, 1.2));
        list.add(new Entry("bruja_pantano_alpha", MobType.SWAMP_WITCH, 4, 1.2, 1.5));
        list.add(new Entry("torreta_hueso_plus", MobType.BONE_TURRET, 5, 1.4, 1.6));
        list.add(new Entry("acechador_nocturno", MobType.THE_STALKER, 5, 1.8, 1.6));
        list.add(new Entry("mimic_fuerte", MobType.MIMIC_SHULKER, 6, 1.6, 1.4));
        list.add(new Entry("phantom_gigante_alpha", MobType.GIANT_PHANTOM, 6, 1.8, 1.5));
        list.add(new Entry("blaze_azul_furia", MobType.BLUE_BLAZE, 7, 1.7, 1.7));
        list.add(new Entry("evoker_mad", MobType.MAD_EVOCER, 7, 1.5, 1.8));
        list.add(new Entry("caballero_apocalipsis", MobType.APOCALYPSE_KNIGHT, 8, 2.0, 1.8));
        list.add(new Entry("leviatan", MobType.LEVIATHAN, 8, 2.2, 1.8));
        list.add(new Entry("reaper", MobType.THE_REAPER, 9, 2.0, 2.0));
        list.add(new Entry("reyk", MobType.RAT_KING, 9, 2.0, 1.9));
        list.add(new Entry("warden_despierto", MobType.AWAKENED_WARDEN, 10, 2.4, 2.0));
        list.add(new Entry("dragon_alpha", MobType.ALPHA_DRAGON, 10, 2.2, 2.0));
        list.add(new Entry("reina_slime", MobType.SLIME_KING, 11, 2.1, 1.9));
        list.add(new Entry("banshee_programada", MobType.BANSHEE, 11, 1.9, 1.9));
        list.add(new Entry("caminante_vacio", MobType.VOID_WALKER, 12, 2.3, 2.2));
        list.add(new Entry("guardian_sombras", MobType.SHADOW, 12, 1.9, 2.0));
        list.add(new Entry("turret_end", MobType.BONE_TURRET, 13, 1.8, 2.0));
        list.add(new Entry("spider_jockey_elite", MobType.ELITE_SPIDER_JOCKEY, 13, 2.0, 2.0));
        list.add(new Entry("evoker_loco_prime", MobType.MAD_EVOCER, 14, 2.1, 2.1));
        list.add(new Entry("magma_slime_elite", MobType.MAGMA_SLIME, 14, 2.0, 1.6));
        list.add(new Entry("ice_creeper_furia", MobType.ICE_CREEPER, 15, 2.2, 2.0));
        list.add(new Entry("esqueleto_blindado", MobType.ARMORED_SKELETON, 15, 2.0, 2.0));
        list.add(new Entry("hive_overlord", MobType.THE_HIVE, 16, 2.5, 2.2));
        list.add(new Entry("esqueleto_veloz_supremo", MobType.SPEED_ZOMBIE, 17, 2.4, 2.3));
        return list.stream().sorted(Comparator.comparingInt(Entry::unlockDay)).collect(Collectors.toList());
    }

    private void start() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 200L, 20L * 240); // every 4 minutes
    }

    public void stop() {
        if (task != null) task.cancel();
    }

    private void tick() {
        GameManager gm = plugin.getGameManager();
        MobManager mm = plugin.getMobManager();
        if (gm == null || mm == null) return;

        int day = Math.max(1, gm.getCurrentDay());
        List<Entry> unlocked = entries.stream().filter(e -> e.unlockDay <= day).toList();
        if (unlocked.isEmpty()) return;

        List<Player> players = Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.getWorld().getEnvironment() == World.Environment.NORMAL)
                .toList();
        if (players.isEmpty()) return;

        for (Entry entry : unlocked) {
            Player target = players.get(random.nextInt(players.size()));
            Location loc = target.getLocation().clone().add(random.nextInt(21) - 10, 0, random.nextInt(21) - 10);
            World world = loc.getWorld();
            if (world == null) continue;
            int y = world.getHighestBlockYAt(loc);
            loc.setY(y + 1);
            var entity = mm.spawnMob(entry.type, loc);
            if (entity instanceof LivingEntity living) {
                buffEntity(living, entry);
            }
        }
    }

    private void buffEntity(LivingEntity living, Entry entry) {
        AttributeInstance health = living.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (health != null) {
            health.setBaseValue(health.getBaseValue() * entry.healthMultiplier);
            living.setHealth(health.getBaseValue());
        }
        AttributeInstance attack = living.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (attack != null) {
            attack.setBaseValue(attack.getBaseValue() * entry.damageMultiplier);
        }
        living.setCustomName("[D" + entry.unlockDay + "] " + living.getName());
        living.setCustomNameVisible(true);
    }
}
