package net.rollanddeath.smp.core.scripting.builtin;

import net.rollanddeath.smp.core.teams.TeamManager;
import net.rollanddeath.smp.core.teams.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.RoleType;
import net.rollanddeath.smp.core.scripting.Action;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.Condition;
import net.rollanddeath.smp.core.scripting.Resolvers;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import net.rollanddeath.smp.core.scripting.ScriptPhase;
import net.rollanddeath.smp.core.scripting.library.CallGuard;
import net.rollanddeath.smp.core.scripting.library.ScriptLibrary;
import net.rollanddeath.smp.core.scripting.builtin.actions.ActionRegistrar;
import net.rollanddeath.smp.core.scripting.particles.MathExpression;
import net.rollanddeath.smp.core.scripting.particles.ScriptedParticleSystemService;
import net.rollanddeath.smp.core.scripting.projectiles.ScriptedProjectileService;
import net.rollanddeath.smp.integration.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.GameRule;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Bee;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Item;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Ageable;
import org.bukkit.Effect;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import org.bukkit.craftbukkit.entity.CraftEntity;

import net.kyori.adventure.text.minimessage.MiniMessage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.Objects;
import java.util.HashMap;
import java.util.Collection;
import java.util.Queue;
import java.util.Set;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class BuiltInActions {

    @FunctionalInterface
    public interface ActionFactory extends Function<Map<?, ?>, Action> {
        if (type == null || type.isBlank() || parser == null) return;
        String t = type.trim().toLowerCase(Locale.ROOT);
        REGISTERED.put(t, parser);
        if (aliases != null) {
            for (String a : aliases) {
                if (a == null || a.isBlank()) continue;
                REGISTERED.put(a.trim().toLowerCase(Locale.ROOT), parser);
            }
        }
                yield explodeAt(where, pwr, setFire, breakBlocks);
            }
            case "clear_anvil_result" -> null; // registered override
            case "multiply_food_loss" -> null; // registered override
            case "kill_player" -> null; // registered override
            case "damage_player" -> null; // registered override
            case "damage_player_or_kill" -> null; // registered override
            case "stack_player_attribute_modifier", "stack_attribute_modifier" -> null; // registered override
            case "damage_nearby_entities" -> null; // registered override
            case "broadcast" -> null; // registered override
            case "set_event_damage" -> null; // registered override
            case "multiply_event_damage" -> null; // registered override
            case "add_velocity" -> null; // registered override
            case "set_player_cooldown" -> null; // registered override
            case "set_player_velocity_forward", "dash_forward" -> null; // registered override
            case "set_player_riptiding" -> null; // registered override
            case "projectile_add_random_spread", "bow_projectile_spread" -> null; // registered override
            case "strike_lightning_at_projectile_hit", "lightning_at_projectile_hit" -> null; // registered override
            case "strike_lightning", "lightning" -> null; // registered override
            case "read_item_pdc_to_var", "read_pdc_to_var" -> null; // registered override
            case "set_var_target_block_location", "raycast_block_location" -> null; // registered override
            case "run_repeating", "repeat" -> null; // registered override
            case "gravity_pull_near_location", "gravity_pull" -> null; // registered override
            case "give_daily_roll_reward", "daily_roll_reward" -> null; // registered override
            case "assign_random_role", "random_role" -> null; // registered override
            case "consume_extra_hand_item" -> null; // registered override
            case "play_sound" -> null; // registered override
            case "stop_all_sounds" -> null; // registered override
            case "set_view_distance" -> null; // registered override
            case "set_world_time" -> null; // registered override
            case "set_gamerule_all_worlds" -> null; // registered override
            case "add_velocity_random" -> null; // registered override
            case "spawn_entity_near_player" -> null; // registered override
            case "set_var_now_plus" -> null; // registered override
            case "for_each_online_player" -> null; // registered override
            case "for_each_entity_in_all_worlds", "for_each_entity_all_worlds" -> null; // registered override
            case "set_fire_ticks" -> null; // registered override
            case "set_compass_target_random" -> null; // registered override
            case "set_compass_target_spawn" -> null; // registered override
            case "set_weather_all_worlds" -> null; // registered override
            case "set_nametag_visibility" -> null; // registered override
            case "slip_drop_hand_item" -> null; // registered override
            case "set_block_below_type" -> null; // registered override
            case "spawn_entity_at_player" -> null; // registered override
            case "set_block_break_drop_items" -> null; // registered override
            case "set_portal_destination_random" -> null; // registered override
            case "apply_effect_to_event_entity" -> null; // registered override
            case "apply_effect_to_mob" -> null; // registered override
            case "set_block_type_at" -> null; // registered override
            case "set_creeper_max_fuse_ticks" -> null; // registered override
            case "set_phantom_size" -> null; // registered override
            case "set_slime_size" -> null; // registered override
            case "set_bee_anger" -> null; // registered override
            case "spawn_entity_at_key" -> null; // registered override
            case "spawn_mob_at_key", "spawn_custom_mob_at_key", "spawn_mob_type_at_key" -> null; // registered override
            case "teleport_player_to_key" -> null; // registered override
            case "get_slime_size_to_var", "store_slime_size" -> null; // registered override
            case "set_protection_purge_active" -> null; // registered override
            case "find_top_block_near" -> null; // registered override
            case "location_offset" -> null; // registered override
            case "place_torches_around" -> null; // registered override
            case "fill_chest_loot", "fill_container_loot" -> null; // registered override
            case "broadcast_with_location" -> null; // registered override
            case "discord_announce_with_location" -> null; // registered override
            case "strike_lightning_effect_at" -> null; // registered override
            case "random_bool_to_var", "random_boolean_to_var" -> null; // registered override
            case "drop_item_at_location", "drop_item_at", "drop_item_naturally" -> null; // registered override
            case "spawn_mount_for_mob" -> null; // registered override
            case "spawn_passenger_for_mob" -> null; // registered override
            case "spawn_particle_shape", "spawn_particles" -> {
                Object centerKey = firstNonNull(raw, "center", "center_key");
                Object followKey = firstNonNull(raw, "follow", "follow_entity_key");

                String particle = Optional.ofNullable(Resolvers.string(null, raw, "particle")).orElse(Resolvers.string(null, raw, "particle_type"));
                if (particle == null || particle.isBlank()) yield null;

                Integer count = Resolvers.integer(null, raw, "count");
                Double offsetX = Resolvers.doubleVal(null, raw, "offset_x");
                Double offsetY = Resolvers.doubleVal(null, raw, "offset_y");
                Double offsetZ = Resolvers.doubleVal(null, raw, "offset_z");
                Double extra = Resolvers.doubleVal(null, raw, "extra");

                String dustColor = Optional.ofNullable(Resolvers.string(null, raw, "dust_color")).orElse(Resolvers.string(null, raw, "color"));
                Double dustSize = Resolvers.doubleVal(null, raw, "dust_size");

                String shapeRaw = Optional.ofNullable(Resolvers.string(null, raw, "shape")).orElse("POINT");
                Integer points = Resolvers.integer(null, raw, "points");
                Double radius = Resolvers.doubleVal(null, raw, "radius");
                Double height = Resolvers.doubleVal(null, raw, "height");
                Double turns = Resolvers.doubleVal(null, raw, "turns");
                Double yOffset = Optional.ofNullable(Resolvers.doubleVal(null, raw, "y_offset")).orElse(Resolvers.doubleVal(null, raw, "y"));
                Double angleOffset = Optional.ofNullable(Resolvers.doubleVal(null, raw, "angle_offset")).orElse(Resolvers.doubleVal(null, raw, "angle"));

                String fx = Optional.ofNullable(Resolvers.string(null, raw, "formula_x")).orElse(Resolvers.string(null, raw, "x"));
                String fy = Optional.ofNullable(Resolvers.string(null, raw, "formula_y")).orElse(Resolvers.string(null, raw, "y"));
                String fz = Optional.ofNullable(Resolvers.string(null, raw, "formula_z")).orElse(Resolvers.string(null, raw, "z"));

                Double centerOffsetX = Optional.ofNullable(Resolvers.doubleVal(null, raw, "center_offset_x")).orElse(Resolvers.doubleVal(null, raw, "center_x"));
                Double centerOffsetY = Optional.ofNullable(Resolvers.doubleVal(null, raw, "center_offset_y")).orElse(Resolvers.doubleVal(null, raw, "center_y"));
                Double centerOffsetZ = Optional.ofNullable(Resolvers.doubleVal(null, raw, "center_offset_z")).orElse(Resolvers.doubleVal(null, raw, "center_z"));

                yield spawnParticleShape(
                    centerKey,
                    followKey,
                    particle,
                    count,
                    offsetX,
                    offsetY,
                    offsetZ,
                    extra,
                    dustColor,
                    dustSize,
                    shapeRaw,
                    points,
                    radius,
                    height,
                    turns,
                    yOffset,
                    angleOffset,
                    fx,
                    fy,
                    fz,
                    centerOffsetX,
                    centerOffsetY,
                    centerOffsetZ
                );
            }
            case "start_particle_system" -> {
                String id = Resolvers.string(null, raw, "id");
                String storeIdKey = Optional.ofNullable(Resolvers.string(null, raw, "store_id_key")).orElse(Resolvers.string(null, raw, "id_key"));
                Object centerKey = firstNonNull(raw, "center", "center_key");
                Object followKey = firstNonNull(raw, "follow", "follow_entity_key");

                String particle = Optional.ofNullable(Resolvers.string(null, raw, "particle")).orElse(Resolvers.string(null, raw, "particle_type"));
                if (particle == null || particle.isBlank()) yield null;

                Integer lifetimeTicks = Optional.ofNullable(Resolvers.integer(null, raw, "lifetime_ticks")).orElse(Resolvers.integer(null, raw, "duration_ticks"));
                Integer periodTicks = Resolvers.integer(null, raw, "period_ticks");

                Integer count = Resolvers.integer(null, raw, "count");
                Double offsetX = Resolvers.doubleVal(null, raw, "offset_x");
                Double offsetY = Resolvers.doubleVal(null, raw, "offset_y");
                Double offsetZ = Resolvers.doubleVal(null, raw, "offset_z");
                Double extra = Resolvers.doubleVal(null, raw, "extra");

                String dustColor = Optional.ofNullable(Resolvers.string(null, raw, "dust_color")).orElse(Resolvers.string(null, raw, "color"));
                Double dustSize = Resolvers.doubleVal(null, raw, "dust_size");

                String shapeRaw = Optional.ofNullable(Resolvers.string(null, raw, "shape")).orElse("POINT");
                Integer points = Resolvers.integer(null, raw, "points");
                Double radius = Resolvers.doubleVal(null, raw, "radius");
                Double height = Resolvers.doubleVal(null, raw, "height");
                Double turns = Resolvers.doubleVal(null, raw, "turns");
                Double yOffset = Optional.ofNullable(Resolvers.doubleVal(null, raw, "y_offset")).orElse(Resolvers.doubleVal(null, raw, "y"));
                Double angleOffset = Optional.ofNullable(Resolvers.doubleVal(null, raw, "angle_offset")).orElse(Resolvers.doubleVal(null, raw, "angle"));

                String fx = Optional.ofNullable(Resolvers.string(null, raw, "formula_x")).orElse(Resolvers.string(null, raw, "x"));
                String fy = Optional.ofNullable(Resolvers.string(null, raw, "formula_y")).orElse(Resolvers.string(null, raw, "y"));
                String fz = Optional.ofNullable(Resolvers.string(null, raw, "formula_z")).orElse(Resolvers.string(null, raw, "z"));

                Double centerOffsetX = Optional.ofNullable(Resolvers.doubleVal(null, raw, "center_offset_x")).orElse(Resolvers.doubleVal(null, raw, "center_x"));
                Double centerOffsetY = Optional.ofNullable(Resolvers.doubleVal(null, raw, "center_offset_y")).orElse(Resolvers.doubleVal(null, raw, "center_y"));
                Double centerOffsetZ = Optional.ofNullable(Resolvers.doubleVal(null, raw, "center_offset_z")).orElse(Resolvers.doubleVal(null, raw, "center_z"));

                yield startParticleSystem(
                    id,
                    storeIdKey,
                    centerKey,
                    followKey,
                    particle,
                    lifetimeTicks,
                    periodTicks,
                    count,
                    offsetX,
                    offsetY,
                    offsetZ,
                    extra,
                    dustColor,
                    dustSize,
                    shapeRaw,
                    points,
                    radius,
                    height,
                    turns,
                    yOffset,
                    angleOffset,
                    fx,
                    fy,
                    fz,
                    centerOffsetX,
                    centerOffsetY,
                    centerOffsetZ
                );
            }
            case "stop_particle_system" -> {
                String id = Resolvers.string(null, raw, "id");
                String idKey = Resolvers.string(null, raw, "id_key");
                if ((id == null || id.isBlank()) && (idKey == null || idKey.isBlank())) yield null;
                yield stopParticleSystem(id, idKey);
            }
            case "launch_curved_projectile", "curved_projectile", "scripted_projectile" -> {
                String projectile = Optional.ofNullable(Resolvers.string(null, raw, "projectile")).orElse(Resolvers.string(null, raw, "projectile_type"));
                if (projectile == null || projectile.isBlank()) projectile = "SNOWBALL";

                String shooterKey = Optional.ofNullable(Resolvers.string(null, raw, "shooter")).orElse(Resolvers.string(null, raw, "shooter_key"));
                String startKey = Optional.ofNullable(Resolvers.string(null, raw, "start")).orElse(Resolvers.string(null, raw, "start_key"));
                String targetKey = Optional.ofNullable(Resolvers.string(null, raw, "target")).orElse(Resolvers.string(null, raw, "target_key"));

                Integer durationTicks = Resolvers.integer(null, raw, "duration_ticks");
                String durationTicksKey = Resolvers.string(null, raw, "duration_ticks_key");
                Double speed = Resolvers.doubleVal(null, raw, "speed");
                String speedKey = Resolvers.string(null, raw, "speed_key");

                Double curveHeight = Resolvers.doubleVal(null, raw, "curve_height");
                String curveHeightKey = Resolvers.string(null, raw, "curve_height_key");
                Double curveSide = Resolvers.doubleVal(null, raw, "curve_side");
                String curveSideKey = Resolvers.string(null, raw, "curve_side_key");
                boolean homing = raw.get("homing") instanceof Boolean b ? b : true;
                Double targetYOffset = Resolvers.doubleVal(null, raw, "target_y_offset");
                String targetYOffsetKey = Resolvers.string(null, raw, "target_y_offset_key");

                boolean explodeOnImpact = raw.get("explode_on_impact") instanceof Boolean b ? b : true;
                boolean explodeOnFinish = raw.get("explode_on_finish") instanceof Boolean b ? b : true;
                Double explosionPower = Optional.ofNullable(Resolvers.doubleVal(null, raw, "explosion_power")).orElse(Resolvers.doubleVal(null, raw, "power"));
                String explosionPowerKey = Optional.ofNullable(Resolvers.string(null, raw, "explosion_power_key")).orElse(Resolvers.string(null, raw, "power_key"));
                boolean explosionFire = raw.get("explosion_fire") instanceof Boolean b ? b : false;
                boolean explosionBreakBlocks = raw.get("explosion_break_blocks") instanceof Boolean b ? b : false;

                // Hooks: listas de acciones anidadas
                List<Action> onHit = null;
                Object onHitObj = raw.get("on_hit");
                if (!(onHitObj instanceof List<?>)) onHitObj = raw.get("on_impact");
                onHit = Resolvers.parseActionList(onHitObj);

                List<Action> onFinish = null;
                Object onFinishObj = raw.get("on_finish");
                onFinish = Resolvers.parseActionList(onFinishObj);

                List<Action> onTick = null;
                Object onTickObj = raw.get("on_tick");
                onTick = Resolvers.parseActionList(onTickObj);

                List<Action> onLaunch = null;
                Object onLaunchObj = raw.get("on_launch");
                onLaunch = Resolvers.parseActionList(onLaunchObj);

                List<String> targets = null;
                Object targetsObj = raw.get("targets");
                if (targetsObj instanceof List<?> list && !list.isEmpty()) {
                    targets = list.stream().filter(String.class::isInstance).map(String.class::cast).toList();
                }

                yield launchCurvedProjectile(
                    projectile,
                    shooterKey,
                    startKey,
                    targetKey,
                    durationTicks,
                    durationTicksKey,
                    speed,
                    speedKey,
                    homing,
                    targetYOffset,
                    targetYOffsetKey,
                    curveHeight,
                    curveHeightKey,
                    curveSide,
                    curveSideKey,
                    explodeOnImpact,
                    explodeOnFinish,
                    explosionPower,
                    explosionPowerKey,
                    explosionFire,
                    explosionBreakBlocks,
                    onHit,
                    onFinish,
                    onTick,
                    onLaunch,
                    targets
                );
            }
            case "set_vex_charging" -> {
                boolean charging = raw.get("value") instanceof Boolean b ? b : true;
                yield setVexCharging(charging);
            }
            case "set_shulker_color" -> {
                String color = Resolvers.string(null, raw, "color");
                if (color == null || color.isBlank()) yield null;
                yield setShulkerColor(color);
            }
            case "set_iron_golem_player_created" -> {
                Boolean created = raw.get("value") instanceof Boolean b ? b : null;
                if (created == null) yield null;
                yield setIronGolemPlayerCreated(created);
            }
            case "set_mob_target_nearest_player" -> {
                Integer radius = Resolvers.integer(null, raw, "radius");
                int r = radius != null ? Math.max(1, radius) : 20;
                yield setMobTargetNearestPlayer(r);
            }
            case "set_zombie_baby" -> {
                Boolean baby = raw.get("value") instanceof Boolean b ? b : null;
                if (baby == null) yield null;
                yield setZombieBaby(baby);
            }
            case "set_mob_attribute_base" -> {
                String attribute = Resolvers.string(null, raw, "attribute");
                if (attribute == null || attribute.isBlank()) yield null;
                Double value = Resolvers.doubleVal(null, raw, "value");
                String valueKey = Resolvers.string(null, raw, "value_key");
                if (value == null && (valueKey == null || valueKey.isBlank())) yield null;
                yield setMobAttributeBase(attribute, value, valueKey);
            }
            case "set_mob_max_health" -> {
                Double value = Resolvers.doubleVal(null, raw, "value");
                String valueKey = Resolvers.string(null, raw, "value_key");
                if (value == null && (valueKey == null || valueKey.isBlank())) yield null;
                yield setMobMaxHealth(value, valueKey);
            }
            case "apply_effect_near_location" -> {
                Object key = firstNonNull(raw, "where", "location", "key", "location_key");
                String effect = Resolvers.string(null, raw, "effect");
                if (key == null || effect == null || effect.isBlank()) yield null;
                Integer duration = Resolvers.integer(null, raw, "duration");
                Integer amplifier = Resolvers.integer(null, raw, "amplifier");
                int dur = duration != null ? duration : 40;
                int amp = amplifier != null ? amplifier : 0;
                Integer radius = Resolvers.integer(null, raw, "radius");
                int r = radius != null ? Math.max(0, radius) : 5;
                boolean includePlayers = raw.get("include_players") instanceof Boolean b ? b : true;
                boolean includeMobs = raw.get("include_mobs") instanceof Boolean b ? b : true;
                boolean ambient = raw.get("ambient") instanceof Boolean b ? b : false;
                boolean particles = raw.get("particles") instanceof Boolean b ? b : false;
                yield applyEffectNearLocation(key, effect, dur, amp, r, includePlayers, includeMobs, ambient, particles);
            }
            case "freeze_area" -> {
                Object key = firstNonNull(raw, "where", "location", "key", "location_key");
                if (key == null) yield null;
                Integer radius = Resolvers.integer(null, raw, "radius");
                int r = radius != null ? Math.max(0, radius) : 4;
                Double airChance = Resolvers.doubleVal(null, raw, "air_chance");
                double p = airChance != null ? Math.max(0.0, Math.min(1.0, airChance)) : 0.3;
                String airMaterial = Resolvers.string(null, raw, "air_material");
                String waterMaterial = Resolvers.string(null, raw, "water_material");
                String iceMaterial = Resolvers.string(null, raw, "ice_material");
                yield freezeArea(key, r, p,
                    airMaterial != null ? airMaterial : "POWDER_SNOW",
                    waterMaterial != null ? waterMaterial : "WATER",
                    iceMaterial != null ? iceMaterial : "ICE"
                );
            }
            case "math_set_var" -> {
                String key = Resolvers.string(null, raw, "key");
                String op = Optional.ofNullable(Resolvers.string(null, raw, "operation")).orElse(Resolvers.string(null, raw, "op"));
                if (key == null || key.isBlank() || op == null || op.isBlank()) yield null;

                Double a = Resolvers.doubleVal(null, raw, "a");
                String aKey = Resolvers.string(null, raw, "a_key");
                Double b = Resolvers.doubleVal(null, raw, "b");
                String bKey = Resolvers.string(null, raw, "b_key");
                Double c = Resolvers.doubleVal(null, raw, "c");
                String cKey = Resolvers.string(null, raw, "c_key");
                if (a == null && (aKey == null || aKey.isBlank())) yield null;
                if ((b == null && (bKey == null || bKey.isBlank())) && !isUnaryMathOp(op)) yield null;
                if ((c == null && (cKey == null || cKey.isBlank())) && isTernaryMathOp(op)) yield null;

                yield mathSetVar(key, op, a, aKey, b, bKey, c, cKey);
            }
            case "add_event_death_drop" -> {
                String material = Resolvers.string(null, raw, "material");
                if (material == null || material.isBlank()) yield null;
                Integer amount = Resolvers.integer(null, raw, "amount");
                int a = amount != null ? Math.max(1, amount) : 1;
                yield addEventDeathDrop(material, a);
            }
            case "clear_event_death_drops" -> clearEventDeathDrops();
            case "set_event_entity_silent" -> {
                Boolean value = raw.get("value") instanceof Boolean b ? b : null;
                if (value == null) yield null;
                yield setEventEntitySilent(value);
            }
            case "set_monsters_silent_all_worlds" -> {
                Boolean value = raw.get("value") instanceof Boolean b ? b : null;
                if (value == null) yield null;
                yield setMonstersSilentAllWorlds(value);
            }
            case "multiply_explosion_radius" -> {
                Double mult = Resolvers.doubleVal(null, raw, "multiplier");
                if (mult == null) yield null;
                yield multiplyExplosionRadius(mult);
            }
            case "cancel_event" -> {
                boolean value = raw.get("value") instanceof Boolean b ? b : true;
                yield cancelEvent(value);
            }
            case "set_event_use_interacted_block" -> {
                String value = Resolvers.string(null, raw, "value");
                if (value == null || value.isBlank()) yield null;
                yield setEventUseInteractedBlock(value);
            }
            case "set_event_use_item_in_hand" -> {
                String value = Resolvers.string(null, raw, "value");
                if (value == null || value.isBlank()) yield null;
                yield setEventUseItemInHand(value);
            }
            case "remove_event_item_entity" -> removeEventItemEntity();
            case "give_item" -> giveItem(raw);
            case "damage_item_durability" -> {
                String slotName = Resolvers.string(null, raw, "slot");
                EquipmentSlot slot = Resolvers.resolveEquipmentSlot(slotName);
                if (slot == null) yield null;
                Integer amount = Resolvers.integer(null, raw, "amount");
                int a = amount != null ? Math.max(1, amount) : 1;
                yield damageItemDurability(slot, a);
            }
            case "damage_helmet_durability" -> {
                Integer amount = Resolvers.integer(null, raw, "amount");
                int a = amount != null ? Math.max(1, amount) : 1;
                yield damageItemDurability(EquipmentSlot.HEAD, a);
            }
            case "unequip_item" -> {
                String slotName = Resolvers.string(null, raw, "slot");
                EquipmentSlot slot = Resolvers.resolveEquipmentSlot(slotName);
                if (slot == null) yield null;
                String message = Resolvers.string(null, raw, "message");
                String color = Resolvers.string(null, raw, "color");
                boolean dropIfFull = raw.get("drop_if_full") instanceof Boolean b ? b : true;
                yield unequipItem(slot, dropIfFull, message, color);
            }
            case "aggro_nearby_mobs" -> {
                String entityType = Resolvers.string(null, raw, "entity_type");
                if (entityType == null || entityType.isBlank()) yield null;
                Integer radius = Resolvers.integer(null, raw, "radius");
                int r = radius != null ? Math.max(1, radius) : 32;
                boolean onlyIfNoTarget = raw.get("only_if_no_target") instanceof Boolean b ? b : true;
                yield aggroNearbyMobs(entityType, r, onlyIfNoTarget);
            }
            case "aggro_nearby_creatures" -> {
                Integer radius = Resolvers.integer(null, raw, "radius");
                Integer radiusY = Resolvers.integer(null, raw, "radius_y", "y_radius", "radiusy");
                int r = radius != null ? Math.max(1, radius) : 30;
                int ry = radiusY != null ? Math.max(1, radiusY) : 10;
                boolean onlyIfNoTarget = raw.get("only_if_no_target") instanceof Boolean b ? b : true;
                yield aggroNearbyCreatures(r, ry, onlyIfNoTarget);
            }
            case "explode_nearby_entities" -> {
                String entityType = Resolvers.string(null, raw, "entity_type");
                Object entityTypesObj = raw.get("entity_types");
                List<String> entityTypes = null;
                if (entityTypesObj instanceof List<?> list && !list.isEmpty()) {
                    entityTypes = list.stream().filter(String.class::isInstance).map(String.class::cast).toList();
                }
                if ((entityType == null || entityType.isBlank()) && (entityTypes == null || entityTypes.isEmpty())) yield null;

                Double radius = Resolvers.doubleVal(null, raw, "radius");
                Double power = Resolvers.doubleVal(null, raw, "power");
                boolean setFire = raw.get("set_fire") instanceof Boolean b ? b : false;
                boolean breakBlocks = raw.get("break_blocks") instanceof Boolean b ? b : false;

                yield explodeNearbyEntities(entityTypes != null ? entityTypes : List.of(entityType),
                    radius != null ? Math.max(0.0, radius) : 3.0,
                    power != null ? power.floatValue() : 2.0f,
                    setFire,
                    breakBlocks
                );
            }
            case "rotate_online_player_positions" -> {
                String nextKey = Resolvers.string(null, raw, "next_key", "key");
                if (nextKey == null || nextKey.isBlank()) yield null;
                Long intervalMs = Resolvers.longVal(null, raw, "interval_ms", "interval", "ms");
                long interval = intervalMs != null ? Math.max(0L, intervalMs) : 1_800_000L;
                String message = Resolvers.string(null, raw, "message");
                String color = Resolvers.string(null, raw, "color");
                boolean shuffle = raw.get("shuffle") instanceof Boolean b ? b : true;
                yield rotateOnlinePlayerPositions(nextKey, interval, shuffle, message, color);
            }
            case "ensure_player_has_item" -> {
                String material = Resolvers.string(null, raw, "material");
                if (material == null || material.isBlank()) yield null;
                Integer amount = Resolvers.integer(null, raw, "amount");
                int a = amount != null ? Math.max(1, amount) : 1;
                yield ensurePlayerHasItem(material, a);
            }
            case "set_player_inventory_hide_tooltip" -> {
                Boolean hide = raw.get("hide") instanceof Boolean b ? b : (raw.get("value") instanceof Boolean bb ? bb : null);
                if (hide == null) yield null;
                yield setPlayerInventoryHideTooltip(hide);
            }
            case "set_hide_tooltip_from_event_items" -> {
                Boolean hide = raw.get("hide") instanceof Boolean b ? b : (raw.get("value") instanceof Boolean bb ? bb : null);
                if (hide == null) yield null;
                yield setHideTooltipFromEventItems(hide);
            }
            case "bow_refund_consumable" -> bowRefundConsumable();
            case "add_player_attribute_modifier" -> {
                String attribute = Resolvers.string(null, raw, "attribute");
                String key = Resolvers.string(null, raw, "key");
                String operation = Resolvers.string(null, raw, "operation");
                Double amount = Resolvers.doubleVal(null, raw, "amount");
                if (attribute == null || attribute.isBlank() || key == null || key.isBlank() || operation == null || operation.isBlank() || amount == null) yield null;
                String slotGroup = Resolvers.string(null, raw, "slot_group");
                yield addPlayerAttributeModifier(attribute, key, operation, amount, slotGroup);
            }
            case "remove_player_attribute_modifier" -> {
                String attribute = Resolvers.string(null, raw, "attribute");
                String key = Resolvers.string(null, raw, "key");
                if (attribute == null || attribute.isBlank() || key == null || key.isBlank()) yield null;
                yield removePlayerAttributeModifier(attribute, key);
            }
            case "inflate_villager_prices_all_worlds" -> {
                Integer mult = Resolvers.integer(null, raw, "multiplier");
                if (mult == null || mult < 2) yield null;
                String key = Optional.ofNullable(Resolvers.string(null, raw, "key")).orElse("inflated_prices");
                yield inflateVillagerPricesAllWorlds(mult, key);
            }
            case "deflate_villager_prices_all_worlds" -> {
                Integer div = Resolvers.integer(null, raw, "divisor");
                if (div == null || div < 2) yield null;
                String key = Optional.ofNullable(Resolvers.string(null, raw, "key")).orElse("inflated_prices");
                yield deflateVillagerPricesAllWorlds(div, key);
            }
            case "inflate_event_villager_prices" -> {
                Integer mult = Resolvers.integer(null, raw, "multiplier");
                if (mult == null || mult < 2) yield null;
                String key = Optional.ofNullable(Resolvers.string(null, raw, "key")).orElse("inflated_prices");
                yield inflateEventVillagerPrices(mult, key);
            }
            case "multiply_villager_trade_cost" -> {
                Integer mult = Resolvers.integer(null, raw, "multiplier");
                if (mult == null || mult < 2) yield null;
                yield multiplyVillagerTradeCost(mult);
            }
            case "set_skeletons_bow_interval_all_worlds" -> {
                Integer interval = Resolvers.integer(null, raw, "interval");
                if (interval == null || interval < 1) yield null;
                yield setSkeletonsBowIntervalAllWorlds(interval);
            }
            case "set_event_skeleton_bow_interval" -> {
                Integer interval = Resolvers.integer(null, raw, "interval");
                if (interval == null || interval < 1) yield null;
                yield setEventSkeletonBowInterval(interval);
            }
            case "cursed_earth_spawn_giant_with_loot" -> {
                String key = Optional.ofNullable(Resolvers.string(null, raw, "key")).orElse("giant_loot");
                yield cursedEarthSpawnGiantWithLoot(key);
            }
            case "cursed_earth_restore_loot_on_giant_death" -> {
                String key = Optional.ofNullable(Resolvers.string(null, raw, "key")).orElse("giant_loot");
                yield cursedEarthRestoreLootOnGiantDeath(key);
            }
            default -> null;
        };

        if (base == null) return null;

        // Permite condicionar acciones individuales desde YAML:
        // - { type: command, command: "...", condition: { type: role_is, value: ENGINEER } }
        // - { type: message, message: "...", when: { type: min_day, value: 10 } }
        Object condObj = raw.get("condition");
        if (!(condObj instanceof Map<?, ?>)) {
            condObj = raw.get("when");
        }
        if (condObj instanceof Map<?, ?> condMap) {
            Condition cond = BuiltInConditions.parse(condMap);
            if (cond == null) return null;
            return ctx -> cond.test(ctx) ? base.run(ctx) : ActionResult.ALLOW;
        }

        return base;
    }

    private static Object resolveScriptVars(ScriptContext ctx, Object value) {
        if (value instanceof String s) {
            String trimmed = s.trim();
            if (trimmed.startsWith("${") && trimmed.endsWith("}")) {
                String key = trimmed.substring(2, trimmed.length() - 1).trim();
                return ctx.getValue(key);
            }
        }
        return value;
    }

    private static Integer firstInt(Map<?, ?> raw, String... keys) {
        if (raw == null || keys == null) return null;
        for (String k : keys) {
            if (k == null || k.isBlank()) continue;
            Integer v = Resolvers.integer(null, raw, k);
            if (v != null) return v;
        }
        return null;
    }

    private static Double firstDouble(Map<?, ?> raw, String... keys) {
        if (raw == null || keys == null) return null;
        for (String k : keys) {
            if (k == null || k.isBlank()) continue;
            Object o = raw.get(k);
            if (o instanceof Number n) {
                return n.doubleValue();
            }
            if (o instanceof String s) {
                try {
                    return Double.parseDouble(s.trim());
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    private static String firstString(Map<?, ?> raw, String... keys) {
        if (raw == null || keys == null) return null;
        for (String k : keys) {
            if (k == null || k.isBlank()) continue;
            String v = Resolvers.string(null, raw, k);
            if (v != null) return v;
        }
        return null;
    }

    private static Boolean firstBool(Map<?, ?> raw, String... keys) {
        if (raw == null || keys == null) return null;
        for (String k : keys) {
            if (k == null || k.isBlank()) continue;
            Object o = raw.get(k);
            if (o instanceof Boolean b) return b;
            if (o instanceof Number n) return n.intValue() != 0;
            if (o instanceof String s) {
                String v = s.trim().toLowerCase(java.util.Locale.ROOT);
                if (v.isEmpty()) continue;
                if (v.equals("true") || v.equals("1") || v.equals("yes") || v.equals("y") || v.equals("on")) return true;
                if (v.equals("false") || v.equals("0") || v.equals("no") || v.equals("n") || v.equals("off")) return false;
            }
        }
        return null;
    }

    private static Object firstNonNull(Map<?, ?> raw, String... keys) {
        if (raw == null || keys == null) return null;
        for (String k : keys) {
            if (k == null || k.isBlank()) continue;
            Object v = raw.get(k);
            if (v != null) return v;
        }
        return null;
    }

    public static Action message(Map<?, ?> params) {
        return ctx -> {
            Player player = ctx.player();
            RollAndDeathSMP plugin = ctx.plugin();
            if (player == null || plugin == null) return ActionResult.ALLOW;

            String msg = Resolvers.string(ctx, params, "message");
            String msgKey = Resolvers.string(ctx, params, "message_key", "key");
            
            String finalMsg = msg;
            if (msgKey != null) {
                 Object val = ctx.getValue(msgKey);
                 if (val != null) finalMsg = String.valueOf(val);
            }
            
            if (finalMsg == null || finalMsg.isBlank()) return ActionResult.ALLOW;

            NamedTextColor c = java.util.Optional.ofNullable(Resolvers.color(ctx, params, "color")).orElse(NamedTextColor.YELLOW);
            
            String text = PlaceholderUtil.resolvePlaceholders(plugin, player, finalMsg);
            runSync(plugin, () -> player.sendMessage(Component.text(text, c)));
            return ActionResult.ALLOW;
        };
    }

    public static Action deny(String message, String color) {
        return ctx -> {
            Player player = ctx.player();
            RollAndDeathSMP plugin = ctx.plugin();
            if (message != null && !message.isBlank()) {
                NamedTextColor c = java.util.Optional.ofNullable(Resolvers.color(ctx, color)).orElse(NamedTextColor.RED);
                String text = PlaceholderUtil.resolvePlaceholders(plugin, player, message);
                runSync(plugin, () -> player.sendMessage(Component.text(text, c)));
            }
            return ActionResult.DENY;
        };
    }

    public static Action command(Map<?, ?> params) {
        return ctx -> {
            Player player = ctx.player();
            RollAndDeathSMP plugin = ctx.plugin();

            String cmd = Resolvers.string(ctx, params, "command");
            if (cmd == null || cmd.isBlank()) return ActionResult.ALLOW;
            
            String as = Resolvers.string(ctx, params, "as");
            if (as == null) as = "console";

            String resolvedCmd = PlaceholderUtil.resolvePlaceholders(plugin, player, cmd);
            resolvedCmd = resolvedCmd.replace("%player%", player.getName());
            resolvedCmd = resolvedCmd.startsWith("/") ? resolvedCmd.substring(1) : resolvedCmd;

            final String cmdFinal = resolvedCmd;
            final String modeFinal = as.trim().toLowerCase(Locale.ROOT);
            runSync(plugin, () -> {
                if ("player".equals(modeFinal)) {
                    player.performCommand(cmdFinal);
                } else {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmdFinal);
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action takeLives(int amount, String message, String color) {
        return ctx -> {
            Player player = ctx.player();
            RollAndDeathSMP plugin = ctx.plugin();

            runSync(plugin, () -> {
                for (int i = 0; i < amount; i++) {
                    plugin.getLifeManager().removeLife(player);
                }
            });

            if (message != null && !message.isBlank()) {
                NamedTextColor c = java.util.Optional.ofNullable(Resolvers.color(ctx, color)).orElse(NamedTextColor.RED);
                String text = PlaceholderUtil.resolvePlaceholders(plugin, player, message);
                player.sendMessage(Component.text(text, c));
            }

            return ActionResult.ALLOW;
        };
    }

    public static Action addLives(int amount, String message, String color) {
        return ctx -> {
            Player player = ctx.player();
            RollAndDeathSMP plugin = ctx.plugin();

            runSync(plugin, () -> {
                for (int i = 0; i < amount; i++) {
                    plugin.getLifeManager().addLife(player);
                }
            });

            if (message != null && !message.isBlank()) {
                NamedTextColor c = java.util.Optional.ofNullable(Resolvers.color(ctx, color)).orElse(NamedTextColor.GREEN);
                String text = PlaceholderUtil.resolvePlaceholders(plugin, player, message);
                player.sendMessage(Component.text(text, c));
            }

            return ActionResult.ALLOW;
        };
    }

    public static Action setLives(int value, String message, String color) {
        return ctx -> {
            Player player = ctx.player();
            RollAndDeathSMP plugin = ctx.plugin();

            runSync(plugin, () -> plugin.getLifeManager().setLives(player, value));

            if (message != null && !message.isBlank()) {
                NamedTextColor c = java.util.Optional.ofNullable(Resolvers.color(ctx, color)).orElse(NamedTextColor.YELLOW);
                String text = PlaceholderUtil.resolvePlaceholders(plugin, player, message);
                player.sendMessage(Component.text(text, c));
            }

            return ActionResult.ALLOW;
        };
    }

    public static Action setVar(String key, Object value) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();

            Object v = value;
            if (v instanceof String s) {
                v = PlaceholderUtil.resolvePlaceholders(plugin, player, s);
            }

            ctx.setGenericVarCompat(key, v);
            return ActionResult.ALLOW;
        };
    }

    public static Action copyVar(String fromKey, String toKey, Object defaultValue) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();

            Object v = ctx.getValue(fromKey);
            if (v == null) {
                v = defaultValue;
            }
            if (v instanceof String s) {
                v = PlaceholderUtil.resolvePlaceholders(plugin, player, s);
            }
            ctx.setGenericVarCompat(toKey, v);
            return ActionResult.ALLOW;
        };
    }

    public static Action setPlayerHealth(Object valueObj) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            Double target = null;
            if (valueObj instanceof Number n) {
                target = n.doubleValue();
            } else if (valueObj instanceof String s) {
                String t = PlaceholderUtil.resolvePlaceholders(plugin, player, s);
                if (t != null && t.trim().equalsIgnoreCase("max")) {
                    target = null; // marcador: max
                } else {
                    target = Resolvers.resolveDouble(t);
                }
            }

            final Double targetFinal = target;
            runSync(plugin, () -> {
                try {
                    double max;
                    try {
                        var inst = player.getAttribute(Attribute.MAX_HEALTH);
                        max = inst != null ? inst.getValue() : player.getMaxHealth();
                    } catch (Exception ignored) {
                        max = player.getMaxHealth();
                    }

                    double desired = (targetFinal == null) ? max : targetFinal;
                    if (Double.isNaN(desired) || Double.isInfinite(desired)) return;
                    desired = Math.max(0.0, Math.min(max, desired));
                    player.setHealth(desired);
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action addPlayerFood(double foodDelta, double saturationDelta) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                try {
                    int food = player.getFoodLevel();
                    float sat = player.getSaturation();

                    int newFood = (int) Math.round(food + foodDelta);
                    newFood = Math.max(0, Math.min(20, newFood));
                    float newSat = (float) (sat + saturationDelta);
                    newSat = Math.max(0.0f, Math.min(20.0f, newSat));

                    player.setFoodLevel(newFood);
                    player.setSaturation(newSat);
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action addVar(String key, Object value) {
        return ctx -> {
            Object current = ctx.getValue(key);
            Double base = Resolvers.resolveDouble(current);
            Double add = Resolvers.resolveDouble(value);
            if (base == null) base = 0.0;
            if (add == null) add = 0.0;
            ctx.setGenericVarCompat(key, base + add);
            return ActionResult.ALLOW;
        };
    }

    public static Action applyEffect(String effectName, int duration, int amplifier, boolean ambient, boolean particles) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            PotionEffectType type;
            try {
                type = PotionEffectType.getByName(effectName.trim().toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                type = null;
            }
            if (type == null) return ActionResult.ALLOW;

            PotionEffect eff = new PotionEffect(type, Math.max(1, duration), Math.max(0, amplifier), ambient, particles);
            runSync(plugin, () -> player.addPotionEffect(eff));
            return ActionResult.ALLOW;
        };
    }

    public static Action applyRandomEffect(List<String> effects, int duration, int amplifier, boolean ambient, boolean particles) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            if (effects == null || effects.isEmpty()) return ActionResult.ALLOW;

            List<String> cleaned = effects.stream()
                .filter(s -> s != null && !s.isBlank())
                .toList();
            if (cleaned.isEmpty()) return ActionResult.ALLOW;

            String chosen = cleaned.get(ThreadLocalRandom.current().nextInt(cleaned.size()));
            PotionEffectType type;
            try {
                type = PotionEffectType.getByName(chosen.trim().toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                type = null;
            }
            if (type == null) return ActionResult.ALLOW;

            PotionEffect eff = new PotionEffect(type, Math.max(1, duration), Math.max(0, amplifier), ambient, particles);
            runSync(plugin, () -> player.addPotionEffect(eff));
            return ActionResult.ALLOW;
        };
    }

    public static Action explodeAt(String key, float power, boolean setFire, boolean breakBlocks) {
        return explodeAt((Object) key, power, setFire, breakBlocks);
    }

    public static Action explodeAt(Object locationSpec, float power, boolean setFire, boolean breakBlocks) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            Location loc = Resolvers.location(locationSpec, ctx, ctx.player() != null ? ctx.player().getWorld() : null);
            if (loc == null || loc.getWorld() == null) return ActionResult.ALLOW;

            final Location at = loc.clone();
            runSync(plugin, () -> at.getWorld().createExplosion(at, power, setFire, breakBlocks));
            return ActionResult.ALLOW;
        };
    }

    public static Action playSound(String sound, List<String> sounds, float volume, float pitch) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            String chosen = sound;
            if ((chosen == null || chosen.isBlank()) && sounds != null && !sounds.isEmpty()) {
                chosen = sounds.get(ThreadLocalRandom.current().nextInt(sounds.size()));
            }
            if (chosen == null || chosen.isBlank()) return ActionResult.ALLOW;

            final Sound sFinal = parseSound(chosen);
            if (sFinal == null) return ActionResult.ALLOW;

            final float volFinal = Math.max(0.0f, volume);
            final float pitFinal = Math.max(0.01f, pitch);
            runSync(plugin, () -> player.playSound(player.getLocation(), sFinal, volFinal, pitFinal));
            return ActionResult.ALLOW;
        };
    }

    public static Action stopAllSounds() {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            runSync(plugin, player::stopAllSounds);
            return ActionResult.ALLOW;
        };
    }

    public static Action setViewDistance(int chunks) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            int vd = Math.max(2, chunks);
            runSync(plugin, () -> player.setViewDistance(vd));
            return ActionResult.ALLOW;
        };
    }

    public static Action setWorldTime(long time) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null || player.getWorld() == null) return ActionResult.ALLOW;
            long t = time;
            runSync(plugin, () -> player.getWorld().setTime(t));
            return ActionResult.ALLOW;
        };
    }

    public static Action setGameRuleAllWorlds(String ruleName, Object valueObj) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();

            final GameRule<?> ruleFinal = Resolvers.resolveGameRule(ruleName);
            if (ruleFinal == null) return ActionResult.ALLOW;
            final Object valueFinal = valueObj;

            runSync(plugin, () -> {
                for (World w : Bukkit.getWorlds()) {
                    trySetGameRule(w, ruleFinal, valueFinal);
                }
            });
            return ActionResult.ALLOW;
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void trySetGameRule(World w, GameRule rule, Object valueObj) {
        if (w == null || rule == null || valueObj == null) return;

        Class<?> type = rule.getType();
        try {
            if (Boolean.class.equals(type) || boolean.class.equals(type)) {
                Boolean b = null;
                if (valueObj instanceof Boolean vb) b = vb;
                else if (valueObj instanceof String s) b = Boolean.parseBoolean(s.trim());
                else if (valueObj instanceof Number n) b = n.intValue() != 0;
                if (b != null) w.setGameRule((GameRule<Boolean>) rule, b);
                return;
            }

            if (Integer.class.equals(type) || int.class.equals(type)) {
                Integer i = null;
                if (valueObj instanceof Number n) i = n.intValue();
                else if (valueObj instanceof String s) {
                    try {
                        i = Integer.parseInt(s.trim());
                    } catch (Exception ignored) {
                        i = null;
                    }
                }
                if (i != null) w.setGameRule((GameRule<Integer>) rule, i);
            }
        } catch (Exception ignored) {
        }
    }

    public static Action addVelocityRandom(double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            double x = randomBetween(minX, maxX);
            double y = randomBetween(minY, maxY);
            double z = randomBetween(minZ, maxZ);

            runSync(plugin, () -> player.setVelocity(player.getVelocity().add(new Vector(x, y, z))));
            return ActionResult.ALLOW;
        };
    }

    private static double randomBetween(double a, double b) {
        double min = Math.min(a, b);
        double max = Math.max(a, b);
        if (Double.compare(min, max) == 0) return min;
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static Action setVarNowPlus(String key, int minMs, int maxMs) {
        return ctx -> {
            long now = System.currentTimeMillis();
            int a = Math.min(minMs, maxMs);
            int b = Math.max(minMs, maxMs);
            int add = a == b ? a : ThreadLocalRandom.current().nextInt(a, b + 1);
            long next = now + Math.max(0L, add);
            ctx.setGenericVarCompat(key, next);
            return ActionResult.ALLOW;
        };
    }

    public static Action forEachOnlinePlayer(List<Action> actions) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                for (Player target : Bukkit.getOnlinePlayers()) {
                    ScriptContext child = new ScriptContext(plugin, target, ctx.subjectId(), ctx.phase(), ctx.variables());
                    for (Action a : actions) {
                        try {
                            a.run(child);
                        } catch (Exception ignored) {
                        }
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action forEachEntityInAllWorlds(List<String> entityTypes, String entityVarName, List<Action> actions) {
        final java.util.Set<String> types = (entityTypes == null || entityTypes.isEmpty())
            ? null
            : entityTypes.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(s -> s.trim().toUpperCase(Locale.ROOT))
                .collect(java.util.stream.Collectors.toSet());

        final String varName = (entityVarName == null || entityVarName.isBlank()) ? "caster" : entityVarName.trim();
        final String varNameLower = varName.toLowerCase(Locale.ROOT);

        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                for (World w : Bukkit.getWorlds()) {
                    for (Entity e : w.getEntities()) {
                        if (e == null) continue;
                        if (types != null && !types.contains(e.getType().name())) continue;

                        Map<String, Object> vars = new java.util.HashMap<>();
                        if (ctx.variables() != null) {
                            vars.putAll(ctx.variables());
                        }

                        // 100% scopes: no inyectamos vars legacy (caster/target/projectile/entity_type/entity_location).
                        // Solo pasamos bases internas para construir SUBJECT/TARGET/PROJECTILE.
                        if (varNameLower.equals("subject")) {
                            vars.put("__subject", e);
                        } else if (varNameLower.equals("target")) {
                            vars.put("__target", e);
                        } else if (varNameLower.equals("projectile")) {
                            vars.put("__projectile", e);
                        } else {
                            // Si se usa un nombre custom, lo dejamos como INTERNAL para evitar sistema legacy.
                            // Recomendación: usar SUBJECT/TARGET/PROJECTILE en YAML.
                            vars.put("__subject", e);
                        }

                        ScriptContext child = new ScriptContext(plugin, ctx.player(), ctx.subjectId(), ctx.phase(), vars);
                        for (Action a : actions) {
                            try {
                                a.run(child);
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action setFireTicks(int ticks) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            int t = Math.max(0, ticks);
            runSync(plugin, () -> player.setFireTicks(t));
            return ActionResult.ALLOW;
        };
    }

    public static Action setCompassTargetRandom(int minX, int maxX, int minZ, int maxZ, int y) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null || player.getWorld() == null) return ActionResult.ALLOW;

            int aX = Math.min(minX, maxX);
            int bX = Math.max(minX, maxX);
            int aZ = Math.min(minZ, maxZ);
            int bZ = Math.max(minZ, maxZ);

            int x = aX == bX ? aX : ThreadLocalRandom.current().nextInt(aX, bX + 1);
            int z = aZ == bZ ? aZ : ThreadLocalRandom.current().nextInt(aZ, bZ + 1);
            int yy = y;
            runSync(plugin, () -> player.setCompassTarget(new Location(player.getWorld(), x, yy, z)));
            return ActionResult.ALLOW;
        };
    }

    public static Action setCompassTargetSpawn() {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null || player.getWorld() == null) return ActionResult.ALLOW;
            runSync(plugin, () -> player.setCompassTarget(player.getWorld().getSpawnLocation()));
            return ActionResult.ALLOW;
        };
    }

    public static Action setWeatherAllWorlds(Boolean storm, Boolean thunder, Integer weatherDuration, Integer thunderDuration) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                for (World w : Bukkit.getWorlds()) {
                    if (storm != null) {
                        w.setStorm(storm);
                        if (storm && weatherDuration != null) w.setWeatherDuration(Math.max(1, weatherDuration));
                        if (!storm) w.setWeatherDuration(0);
                    }
                    if (thunder != null) {
                        w.setThundering(thunder);
                        if (thunder && thunderDuration != null) w.setThunderDuration(Math.max(1, thunderDuration));
                        if (!thunder) w.setThunderDuration(0);
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action setNameTagVisibility(String teamName, boolean enabled) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                var mgr = Bukkit.getScoreboardManager();
                if (mgr == null) return;
                var board = mgr.getMainScoreboard();
                org.bukkit.scoreboard.Team team = board.getTeam(teamName);

                if (enabled) {
                    if (team == null) {
                        team = board.registerNewTeam(teamName);
                    }
                    team.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        team.addEntry(p.getName());
                    }
                } else {
                    if (team != null) {
                        team.unregister();
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action slipDropHandItem(String message, String color) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            PlayerInteractEvent event = ctx.nativeEvent(PlayerInteractEvent.class);
            if (event == null || !event.hasItem()) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                ItemStack item = event.getItem();
                if (item == null || item.getType().isAir()) return;

                player.getWorld().dropItemNaturally(player.getLocation(), item);
                if (event.getHand() == org.bukkit.inventory.EquipmentSlot.HAND) {
                    player.getInventory().setItemInMainHand(null);
                } else {
                    player.getInventory().setItemInOffHand(null);
                }
                event.setCancelled(true);

                if (message != null && !message.isBlank()) {
                    NamedTextColor c = java.util.Optional.ofNullable(Resolvers.color(ctx, color)).orElse(NamedTextColor.RED);
                    player.sendMessage(Component.text(message, c));
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action setBlockBelowType(String materialName) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            final org.bukkit.Material matFinal = Resolvers.resolveMaterial(materialName);
            if (matFinal == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                try {
                    var block = player.getLocation().getBlock().getRelative(org.bukkit.block.BlockFace.DOWN);
                    block.setType(matFinal, false);
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action spawnEntityAtPlayer(String entityType, int offsetX, int offsetY, int offsetZ) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null || player.getWorld() == null) return ActionResult.ALLOW;

            final EntityType tFinal = Resolvers.resolveEntityType(entityType);
            if (tFinal == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                Location loc = player.getLocation().clone().add(offsetX, offsetY, offsetZ);
                player.getWorld().spawnEntity(loc, tFinal);
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action setBlockBreakDropItems(boolean value) {
        return ctx -> {
            org.bukkit.event.block.BlockBreakEvent event = ctx.nativeEvent(org.bukkit.event.block.BlockBreakEvent.class);
            if (event == null) return ActionResult.ALLOW;
            event.setDropItems(value);
            return ActionResult.ALLOW;
        };
    }

    public static Action setPortalDestinationRandom(int minX, int maxX, int minZ, int maxZ, Integer y, boolean useHighestBlock) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            org.bukkit.event.player.PlayerPortalEvent event = ctx.nativeEvent(org.bukkit.event.player.PlayerPortalEvent.class);
            if (event == null) return ActionResult.ALLOW;

            int aX = Math.min(minX, maxX);
            int bX = Math.max(minX, maxX);
            int aZ = Math.min(minZ, maxZ);
            int bZ = Math.max(minZ, maxZ);

            int x = aX == bX ? aX : ThreadLocalRandom.current().nextInt(aX, bX + 1);
            int z = aZ == bZ ? aZ : ThreadLocalRandom.current().nextInt(aZ, bZ + 1);

            runSync(plugin, () -> {
                Location to = event.getTo();
                World w = to != null ? to.getWorld() : player.getWorld();
                if (w == null) return;

                int yy;
                if (useHighestBlock) {
                    try {
                        yy = w.getHighestBlockYAt(x, z) + 1;
                    } catch (Exception ignored) {
                        yy = y != null ? y : Math.min(120, w.getMaxHeight() - 1);
                    }
                } else {
                    yy = y != null ? y : (to != null ? (int) to.getY() : 80);
                }

                Location newTo = new Location(w, x + 0.5, yy, z + 0.5, player.getLocation().getYaw(), player.getLocation().getPitch());
                event.setTo(newTo);
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action applyEffectToEventEntity(String effectName, int duration, int amplifier, boolean ambient, boolean particles) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            LivingEntity target = null;
            CreatureSpawnEvent cse = ctx.nativeEvent(CreatureSpawnEvent.class);
            if (cse != null) {
                target = cse.getEntity();
            } else {
                EntitySpawnEvent ese = ctx.nativeEvent(EntitySpawnEvent.class);
                if (ese != null) {
                    target = ese.getEntity() instanceof LivingEntity le ? le : null;
                }
            }
            if (target == null) return ActionResult.ALLOW;

            PotionEffectType type;
            try {
                type = PotionEffectType.getByName(effectName.trim().toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                type = null;
            }
            if (type == null) return ActionResult.ALLOW;

            PotionEffect eff = new PotionEffect(type, Math.max(1, duration), Math.max(0, amplifier), ambient, particles);
            final LivingEntity targetFinal = target;
            runSync(plugin, () -> targetFinal.addPotionEffect(eff));
            return ActionResult.ALLOW;
        };
    }

    public static Action applyEffectToMob(String effectName, int duration, int amplifier, boolean ambient, boolean particles) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            LivingEntity target = ctx.subject(LivingEntity.class);

            if (target == null) {
                org.bukkit.event.entity.EntityEvent ee = ctx.nativeEvent(org.bukkit.event.entity.EntityEvent.class);
                if (ee != null && ee.getEntity() instanceof LivingEntity le) {
                    target = le;
                }
            }

            if (target == null) return ActionResult.ALLOW;

            PotionEffectType type;
            try {
                type = PotionEffectType.getByName(effectName.trim().toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                type = null;
            }
            if (type == null) return ActionResult.ALLOW;

            PotionEffect eff = new PotionEffect(type, Math.max(1, duration), Math.max(0, amplifier), ambient, particles);
            final LivingEntity targetFinal = target;
            runSync(plugin, () -> targetFinal.addPotionEffect(eff));
            return ActionResult.ALLOW;
        };
    }

    public static Action setBlockTypeAt(String locationKey, String materialName, boolean applyPhysics) {
        return setBlockTypeAt((Object) locationKey, materialName, applyPhysics);
    }

    public static Action setBlockTypeAt(Object locationSpec, String materialName, boolean applyPhysics) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            Location loc = Resolvers.location(locationSpec, ctx, ctx.player() != null ? ctx.player().getWorld() : null);
            if (loc == null || loc.getWorld() == null) {
                return ActionResult.ALLOW;
            }

            org.bukkit.Material parsed = null;
            try {
                parsed = org.bukkit.Material.valueOf(materialName.trim().toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
            }
            if (parsed == null) {
                return ActionResult.ALLOW;
            }

            final Location locFinal = loc;
            final org.bukkit.Material matFinal = parsed;

            runSync(plugin, () -> {
                try {
                    locFinal.getBlock().setType(matFinal, applyPhysics);
                } catch (Exception ignored) {
                    try {
                        locFinal.getBlock().setType(matFinal);
                    } catch (Exception ignored2) {
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action setCreeperMaxFuseTicks(int ticks) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            org.bukkit.entity.Creeper creeper = ctx.subjectOrEventEntity(org.bukkit.entity.Creeper.class);

            if (creeper == null) return ActionResult.ALLOW;

            final org.bukkit.entity.Creeper creeperFinal = creeper;
            int safe = Math.max(1, ticks);
            runSync(plugin, () -> {
                try {
                    creeperFinal.setMaxFuseTicks(safe);
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action setPhantomSize(int size) {
        return setPhantomSize(size, null);
    }

    public static Action setPhantomSize(int size, Object entitySpec) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            org.bukkit.entity.Phantom phantom = null;

            Object direct = null;
            if (entitySpec instanceof String key && !key.isBlank()) {
                direct = ctx.getValue(key.trim());
            } else {
                direct = entitySpec;
            }
            if (direct instanceof org.bukkit.entity.Phantom p) {
                phantom = p;
            }

            if (phantom == null) {
                phantom = ctx.subjectOrEventEntity(org.bukkit.entity.Phantom.class);
            }
            if (phantom == null) return ActionResult.ALLOW;

            final org.bukkit.entity.Phantom phantomFinal = phantom;
            int safe = Math.max(0, Math.min(64, size));
            runSync(plugin, () -> {
                try {
                    phantomFinal.setSize(safe);
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action setSlimeSize(int size) {
        return setSlimeSize(Integer.valueOf(size), null);
    }

    public static Action setSlimeSize(Object sizeSpec) {
        // Permite YAML: size: 10  |  size: "EVENT.custom.some_size"  (o "10")
        if (sizeSpec instanceof Number n) {
            return setSlimeSize(n.intValue(), null);
        }
        if (sizeSpec instanceof String s) {
            String t = s.trim();
            if (t.isBlank()) return ctx -> ActionResult.ALLOW;
            try {
                return setSlimeSize(Integer.parseInt(t), null);
            } catch (Exception ignored) {
                return setSlimeSize(null, t);
            }
        }
        return ctx -> ActionResult.ALLOW;
    }

    public static Action setSlimeSize(Integer size, String sizeKey) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            Slime slime = ctx.subjectOrEventEntity(Slime.class);
            if (slime == null) return ActionResult.ALLOW;

            Integer desired = (size != null && size >= 1) ? size : null;
            if (desired == null && sizeKey != null && !sizeKey.isBlank()) {
                Object v = ctx.getValue(sizeKey);
                if (v instanceof Number n) {
                    desired = n.intValue();
                } else if (v instanceof String s) {
                    try {
                        desired = Integer.parseInt(s.trim());
                    } catch (Exception ignored) {
                    }
                }
            }
            if (desired == null) return ActionResult.ALLOW;

            final Slime slimeFinal = slime;
            int safe = Math.max(1, Math.min(127, desired));
            runSync(plugin, () -> {
                try {
                    slimeFinal.setSize(safe);
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action teleportPlayerToKey(String key) {
        return teleportPlayerTo((Object) key);
    }

    public static Action teleportPlayerTo(Object locationSpec) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            Location loc = Resolvers.location(locationSpec, ctx, player.getWorld());
            if (loc == null || loc.getWorld() == null) return ActionResult.ALLOW;

            final Location target = loc.clone();
            runSync(plugin, () -> {
                try {
                    player.teleport(target);
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action strikeLightningEffectAt(Object locationSpec) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            Location loc = Resolvers.location(locationSpec, ctx, ctx.player() != null ? ctx.player().getWorld() : null);
            if (loc == null || loc.getWorld() == null) return ActionResult.ALLOW;

            final Location target = loc.clone();
            runSync(plugin, () -> {
                try {
                    target.getWorld().strikeLightningEffect(target);
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action strikeLightningAt(Object locationSpec, boolean effectOnly) {
        final boolean effectOnlyFinal = effectOnly;
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            Location loc = Resolvers.location(locationSpec, ctx, ctx.player() != null ? ctx.player().getWorld() : null);
            if (loc == null || loc.getWorld() == null) return ActionResult.ALLOW;

            final Location target = loc.clone();
            runSync(plugin, () -> {
                try {
                    if (effectOnlyFinal) {
                        target.getWorld().strikeLightningEffect(target);
                    } else {
                        target.getWorld().strikeLightning(target);
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action readEventItemPdcToVar(String pdcKeyRaw, String dataTypeRaw, String storeKey) {
        final String pdcKeyFinal = pdcKeyRaw;
        final String dtFinal = dataTypeRaw;
        final String storeFinal = storeKey;

        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            ItemStack item = ctx.item();
            if (item == null) {
                try {
                    org.bukkit.event.player.PlayerInteractEvent pie = ctx.nativeEvent(org.bukkit.event.player.PlayerInteractEvent.class);
                    if (pie != null) item = pie.getItem();
                } catch (Exception ignored) {
                }
            }
            if (item == null) {
                try {
                    org.bukkit.event.player.PlayerItemConsumeEvent pce = ctx.nativeEvent(org.bukkit.event.player.PlayerItemConsumeEvent.class);
                    if (pce != null) item = pce.getItem();
                } catch (Exception ignored) {
                }
            }

            if (item == null) return ActionResult.ALLOW;
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return ActionResult.ALLOW;

            NamespacedKey k;
            String rawKey = pdcKeyFinal != null ? pdcKeyFinal.trim() : "";
            if (rawKey.isBlank()) return ActionResult.ALLOW;
            if (rawKey.contains(":")) {
                k = NamespacedKey.fromString(rawKey);
            } else {
                k = new NamespacedKey(plugin, rawKey);
            }
            if (k == null) return ActionResult.ALLOW;

            String dt = dtFinal != null ? dtFinal.trim().toUpperCase(Locale.ROOT) : null;
            Object out = null;

            try {
                var pdc = meta.getPersistentDataContainer();

                if (dt == null || dt.isBlank()) {
                    // Heurística: intenta en orden común.
                    Double d = pdc.get(k, PersistentDataType.DOUBLE);
                    if (d != null) out = d;
                    if (out == null) {
                        Integer i = pdc.get(k, PersistentDataType.INTEGER);
                        if (i != null) out = i.doubleValue();
                    }
                    if (out == null) {
                        Long l = pdc.get(k, PersistentDataType.LONG);
                        if (l != null) out = l.doubleValue();
                    }
                    if (out == null) {
                        String s = pdc.get(k, PersistentDataType.STRING);
                        if (s != null) out = s;
                    }
                    if (out == null) {
                        Byte b = pdc.get(k, PersistentDataType.BYTE);
                        if (b != null) out = b != 0;
                    }
                } else {
                    switch (dt) {
                        case "DOUBLE" -> out = pdc.get(k, PersistentDataType.DOUBLE);
                        case "INT", "INTEGER" -> {
                            Integer i = pdc.get(k, PersistentDataType.INTEGER);
                            out = i != null ? i.doubleValue() : null;
                        }
                        case "LONG" -> {
                            Long l = pdc.get(k, PersistentDataType.LONG);
                            out = l != null ? l.doubleValue() : null;
                        }
                        case "STRING" -> out = pdc.get(k, PersistentDataType.STRING);
                        case "BYTE", "BOOLEAN" -> {
                            Byte b = pdc.get(k, PersistentDataType.BYTE);
                            out = b != null ? (b != 0) : null;
                        }
                        default -> out = pdc.get(k, PersistentDataType.STRING);
                    }
                }
            } catch (Exception ignored) {
                out = null;
            }

            if (out != null) {
                setVarAny(ctx, storeFinal, out);
            }

            return ActionResult.ALLOW;
        };
    }

    public static Action randomBoolToVar(String key, double probabilityTrue) {
        return ctx -> {
            double p = Math.max(0.0, Math.min(1.0, probabilityTrue));
            boolean v = ThreadLocalRandom.current().nextDouble() < p;
            setVarAny(ctx, key, v);
            return ActionResult.ALLOW;
        };
    }

    private static void setVarAny(ScriptContext ctx, String key, Object value) {
        if (ctx == null || key == null || key.isBlank()) return;

        try {
            ctx.setGenericVarCompat(key, value);
        } catch (Exception ignored) {
        }
    }

    private static String replaceLocationTokens(String message, Location loc) {
        if (message == null || loc == null) return message;
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        String world = loc.getWorld() != null ? loc.getWorld().getName() : "";

        return message
            .replace("{x}", String.valueOf(x))
            .replace("{y}", String.valueOf(y))
            .replace("{z}", String.valueOf(z))
            .replace("{world}", world);
    }

    public static Action setBeeAnger(int ticks) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            Bee bee = ctx.subjectOrEventEntity(Bee.class);
            if (bee == null) return ActionResult.ALLOW;

            final Bee beeFinal = bee;
            int safe = Math.max(0, ticks);
            runSync(plugin, () -> {
                try {
                    beeFinal.setAnger(safe);
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action spawnEntityAtKey(String key, String entityType, int count, int radius, int yOffset, String name, boolean nameVisible, Integer beeAngerTicks) {
        return spawnEntityAt((Object) key, entityType, count, radius, yOffset, name, nameVisible, beeAngerTicks, null);
    }

    public static Action spawnEntityAt(Object locationSpec, String entityType, int count, int radius, int yOffset, String name, boolean nameVisible, Integer beeAngerTicks) {
        return spawnEntityAt(locationSpec, entityType, count, radius, yOffset, name, nameVisible, beeAngerTicks, null);
    }

    public static Action spawnEntityAt(Object locationSpec, String entityType, int count, int radius, int yOffset, String name, boolean nameVisible, Integer beeAngerTicks, String storeKey) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            Location base = Resolvers.location(locationSpec, ctx, ctx.player() != null ? ctx.player().getWorld() : null);
            if (base == null) return ActionResult.ALLOW;
            World world = base.getWorld();
            if (world == null) return ActionResult.ALLOW;

            EntityType et;
            try {
                et = EntityType.valueOf(entityType.trim().toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
                return ActionResult.ALLOW;
            }

            java.util.concurrent.atomic.AtomicReference<Entity> firstSpawned = new java.util.concurrent.atomic.AtomicReference<>();

            runSync(plugin, () -> {
                for (int i = 0; i < Math.max(1, count); i++) {
                    Location at = base.clone().add(0, yOffset, 0);
                    if (radius > 0) {
                        double dx = (ThreadLocalRandom.current().nextDouble() * 2 - 1) * radius;
                        double dz = (ThreadLocalRandom.current().nextDouble() * 2 - 1) * radius;
                        at.add(dx, 0, dz);
                    }
                    Entity spawned;
                    try {
                        spawned = world.spawnEntity(at, et);
                    } catch (Exception ignored) {
                        continue;
                    }

                    if (firstSpawned.get() == null) {
                        firstSpawned.set(spawned);
                    }

                    if (spawned instanceof LivingEntity le) {
                        if (name != null && !name.isBlank()) {
                            try {
                                le.customName(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(name));
                            } catch (Exception ignored) {
                                le.customName(Component.text(name));
                            }
                            le.setCustomNameVisible(nameVisible);
                        }
                        if (beeAngerTicks != null && le instanceof Bee bee) {
                            try {
                                bee.setAnger(Math.max(0, beeAngerTicks));
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            });

            if (storeKey != null && !storeKey.isBlank()) {
                Entity spawned = firstSpawned.get();
                if (spawned != null) {
                    String k = storeKey.trim();
                    ctx.setGenericVarCompat(k, spawned);
                }
            }

            return ActionResult.ALLOW;
        };
    }

    public static Action spawnParticleShape(
        String centerKey,
        String followEntityKey,
        String particle,
        Integer count,
        Double offsetX,
        Double offsetY,
        Double offsetZ,
        Double extra,
        String dustColor,
        Double dustSize,
        String shapeRaw,
        Integer points,
        Double radius,
        Double height,
        Double turns,
        Double yOffset,
        Double angleOffset,
        String formulaX,
        String formulaY,
        String formulaZ,
        Double centerOffsetX,
        Double centerOffsetY,
        Double centerOffsetZ
    ) {
        return spawnParticleShape((Object) centerKey,
            (Object) followEntityKey,
            particle,
            count,
            offsetX,
            offsetY,
            offsetZ,
            extra,
            dustColor,
            dustSize,
            shapeRaw,
            points,
            radius,
            height,
            turns,
            yOffset,
            angleOffset,
            formulaX,
            formulaY,
            formulaZ,
            centerOffsetX,
            centerOffsetY,
            centerOffsetZ
        );
    }

    public static Action spawnParticleShape(
        Object centerSpec,
        Object followEntitySpec,
        String particle,
        Integer count,
        Double offsetX,
        Double offsetY,
        Double offsetZ,
        Double extra,
        String dustColor,
        Double dustSize,
        String shapeRaw,
        Integer points,
        Double radius,
        Double height,
        Double turns,
        Double yOffset,
        Double angleOffset,
        String formulaX,
        String formulaY,
        String formulaZ,
        Double centerOffsetX,
        Double centerOffsetY,
        Double centerOffsetZ
    ) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            ScriptedParticleSystemService svc = plugin.getScriptedParticleSystemService();
            if (svc == null) return ActionResult.ALLOW;

            Entity followEnt = Resolvers.entity(followEntitySpec, ctx);
            UUID followId = followEnt != null ? followEnt.getUniqueId() : null;

            Location center = null;
            if (followId == null) {
                World defaultWorld = ctx.player() != null ? ctx.player().getWorld() : null;
                Object cObj = centerSpec != null ? centerSpec : "caster";
                center = Resolvers.location(cObj, ctx, defaultWorld);
                if (center == null && ctx.player() != null) {
                    center = ctx.player().getLocation();
                }
                if (center == null) {
                    center = ctx.location();
                }
                if (center == null) {
                    org.bukkit.event.entity.EntityEvent ee = ctx.nativeEvent(org.bukkit.event.entity.EntityEvent.class);
                    if (ee != null) center = ee.getEntity().getLocation();
                }
            }

            ScriptedParticleSystemService.ParticleSpec spec = ScriptedParticleSystemService.ParticleSpec.from(
                particle,
                count,
                offsetX,
                offsetY,
                offsetZ,
                extra,
                dustColor,
                dustSize
            );
            if (spec == null) return ActionResult.ALLOW;

            ScriptedParticleSystemService.Shape shape = parseParticleShape(shapeRaw);
            int pts = points != null ? Math.max(1, points) : 16;
            double r = radius != null ? radius : 1.0;
            double h = height != null ? height : 2.0;
            double tr = turns != null ? turns : 1.0;
            double yo = yOffset != null ? yOffset : 0.0;
            double ao = angleOffset != null ? angleOffset : 0.0;

            MathExpression.Compiled fx = shape == ScriptedParticleSystemService.Shape.FORMULA ? MathExpression.compile(formulaX) : null;
            MathExpression.Compiled fy = shape == ScriptedParticleSystemService.Shape.FORMULA ? MathExpression.compile(formulaY) : null;
            MathExpression.Compiled fz = shape == ScriptedParticleSystemService.Shape.FORMULA ? MathExpression.compile(formulaZ) : null;

            Vector centerOffset = new Vector(
                centerOffsetX != null ? centerOffsetX : 0.0,
                centerOffsetY != null ? centerOffsetY : 0.0,
                centerOffsetZ != null ? centerOffsetZ : 0.0
            );

            svc.spawnNow(new ScriptedParticleSystemService.SpawnRequest(
                center,
                followId,
                centerOffset,
                spec,
                shape,
                pts,
                r,
                h,
                tr,
                yo,
                ao,
                fx,
                fy,
                fz
            ));

            return ActionResult.ALLOW;
        };
    }

    public static Action startParticleSystem(
        String id,
        String storeIdKey,
        String centerKey,
        String followEntityKey,
        String particle,
        Integer lifetimeTicks,
        Integer periodTicks,
        Integer count,
        Double offsetX,
        Double offsetY,
        Double offsetZ,
        Double extra,
        String dustColor,
        Double dustSize,
        String shapeRaw,
        Integer points,
        Double radius,
        Double height,
        Double turns,
        Double yOffset,
        Double angleOffset,
        String formulaX,
        String formulaY,
        String formulaZ,
        Double centerOffsetX,
        Double centerOffsetY,
        Double centerOffsetZ
    ) {
        return startParticleSystem(
            id,
            storeIdKey,
            (Object) centerKey,
            (Object) followEntityKey,
            particle,
            lifetimeTicks,
            periodTicks,
            count,
            offsetX,
            offsetY,
            offsetZ,
            extra,
            dustColor,
            dustSize,
            shapeRaw,
            points,
            radius,
            height,
            turns,
            yOffset,
            angleOffset,
            formulaX,
            formulaY,
            formulaZ,
            centerOffsetX,
            centerOffsetY,
            centerOffsetZ
        );
    }

    public static Action startParticleSystem(
        String id,
        String storeIdKey,
        Object centerSpec,
        Object followEntitySpec,
        String particle,
        Integer lifetimeTicks,
        Integer periodTicks,
        Integer count,
        Double offsetX,
        Double offsetY,
        Double offsetZ,
        Double extra,
        String dustColor,
        Double dustSize,
        String shapeRaw,
        Integer points,
        Double radius,
        Double height,
        Double turns,
        Double yOffset,
        Double angleOffset,
        String formulaX,
        String formulaY,
        String formulaZ,
        Double centerOffsetX,
        Double centerOffsetY,
        Double centerOffsetZ
    ) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            ScriptedParticleSystemService svc = plugin.getScriptedParticleSystemService();
            if (svc == null) return ActionResult.ALLOW;

            Entity followEnt = Resolvers.entity(followEntitySpec, ctx);
            UUID followId = followEnt != null ? followEnt.getUniqueId() : null;

            Location center = null;
            if (followId == null) {
                World defaultWorld = ctx.player() != null ? ctx.player().getWorld() : null;
                Object cObj = centerSpec != null ? centerSpec : "caster";
                center = Resolvers.location(cObj, ctx, defaultWorld);
                if (center == null && ctx.player() != null) {
                    center = ctx.player().getLocation();
                }
                if (center == null) {
                    center = ctx.location();
                }
                if (center == null) {
                    org.bukkit.event.entity.EntityEvent ee = ctx.nativeEvent(org.bukkit.event.entity.EntityEvent.class);
                    if (ee != null) center = ee.getEntity().getLocation();
                }
            }

            ScriptedParticleSystemService.ParticleSpec spec = ScriptedParticleSystemService.ParticleSpec.from(
                particle,
                count,
                offsetX,
                offsetY,
                offsetZ,
                extra,
                dustColor,
                dustSize
            );
            if (spec == null) return ActionResult.ALLOW;

            ScriptedParticleSystemService.Shape shape = parseParticleShape(shapeRaw);
            int pts = points != null ? Math.max(1, points) : 16;
            double r = radius != null ? radius : 1.0;
            double h = height != null ? height : 2.0;
            double tr = turns != null ? turns : 1.0;
            double yo = yOffset != null ? yOffset : 0.0;
            double ao = angleOffset != null ? angleOffset : 0.0;

            MathExpression.Compiled fx = shape == ScriptedParticleSystemService.Shape.FORMULA ? MathExpression.compile(formulaX) : null;
            MathExpression.Compiled fy = shape == ScriptedParticleSystemService.Shape.FORMULA ? MathExpression.compile(formulaY) : null;
            MathExpression.Compiled fz = shape == ScriptedParticleSystemService.Shape.FORMULA ? MathExpression.compile(formulaZ) : null;

            Vector centerOffset = new Vector(
                centerOffsetX != null ? centerOffsetX : 0.0,
                centerOffsetY != null ? centerOffsetY : 0.0,
                centerOffsetZ != null ? centerOffsetZ : 0.0
            );

            int life = lifetimeTicks != null ? Math.max(1, lifetimeTicks) : 60;
            int per = periodTicks != null ? Math.max(1, periodTicks) : 2;

            String outId = svc.startSystem(new ScriptedParticleSystemService.StartRequest(
                id,
                center,
                followId,
                centerOffset,
                spec,
                shape,
                pts,
                r,
                h,
                tr,
                yo,
                ao,
                fx,
                fy,
                fz,
                life,
                per
            ));

            if (storeIdKey != null && !storeIdKey.isBlank() && outId != null && !outId.isBlank()) {
                ctx.setGenericVarCompat(storeIdKey, outId);
            }

            return ActionResult.ALLOW;
        };
    }

    public static Action stopParticleSystem(String id, String idKey) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            ScriptedParticleSystemService svc = plugin.getScriptedParticleSystemService();
            if (svc == null) return ActionResult.ALLOW;

            String resolved = (id != null && !id.isBlank()) ? id : null;
            if (resolved == null && idKey != null && !idKey.isBlank()) {
                Object v = ctx.getValue(idKey);
                if (v != null) resolved = String.valueOf(v);
            }

            if (resolved != null && !resolved.isBlank()) {
                svc.stopSystem(resolved);
            }

            return ActionResult.ALLOW;
        };
    }

    private static ScriptedParticleSystemService.Shape parseParticleShape(String raw) {
        if (raw == null || raw.isBlank()) return ScriptedParticleSystemService.Shape.POINT;
        String s = raw.trim().toUpperCase(Locale.ROOT);
        try {
            return ScriptedParticleSystemService.Shape.valueOf(s);
        } catch (Exception ignored) {
            return ScriptedParticleSystemService.Shape.POINT;
        }
    }

    public static Action launchCurvedProjectile(
        String projectileType,
        String shooterKey,
        String startKey,
        String targetKey,
        Integer durationTicks,
        String durationTicksKey,
        Double speed,
        String speedKey,
        boolean homing,
        Double targetYOffset,
        String targetYOffsetKey,
        Double curveHeight,
        String curveHeightKey,
        Double curveSide,
        String curveSideKey,
        boolean explodeOnImpact,
        boolean explodeOnFinish,
        Double explosionPower,
        String explosionPowerKey,
        boolean explosionFire,
        boolean explosionBreakBlocks,
        List<Action> onHit,
        List<Action> onFinish,
        List<Action> onTick,
        List<Action> onLaunch,
        List<String> targets
    ) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            ScriptedProjectileService svc = plugin.getScriptedProjectileService();
            if (svc == null) return ActionResult.ALLOW;

            // Shooter
            Object shooterObj = null;
            if (shooterKey != null && !shooterKey.isBlank()) {
                shooterObj = ctx.getValue(shooterKey);
            }
            if (shooterObj == null) {
                shooterObj = ctx.getValue("SUBJECT");
            }
            LivingEntity shooter = shooterObj instanceof LivingEntity le ? le : ctx.player();
            if (shooter == null) return ActionResult.ALLOW;

            // Start
            Location start = null;
            if (startKey != null && !startKey.isBlank()) {
                start = tryResolvers.location(ctx.getValue(startKey));
            }
            if (start == null) {
                try {
                    start = shooter.getEyeLocation();
                } catch (Exception ignored) {
                    start = shooter.getLocation();
                }
            }
            if (start == null || start.getWorld() == null) return ActionResult.ALLOW;

            // Target
            Object targetObj = null;
            if (targetKey != null && !targetKey.isBlank()) {
                targetObj = ctx.getValue(targetKey);
            }
            if (targetObj == null) {
                // Fallback: target genérico del contexto (ej: Mob#getTarget)
                targetObj = ctx.getValue("TARGET");
            }
            
            // Validate target (exclude shooter and team members)
            TeamManager teamManager = plugin.getTeamManager();
            if (targetObj instanceof Entity e && !isValidTarget(e, shooter, teamManager, targets)) {
                targetObj = null;
            }

            if (targetObj == null) {
                // Fallback final: player más cercano válido
                targetObj = findNearestTarget(start, 48.0, shooter, teamManager, targets);
            }

            UUID targetId = null;
            Location end = null;
            if (targetObj instanceof Entity e) {
                targetId = e.getUniqueId();
                end = e.getLocation();
            } else {
                end = tryResolvers.location(targetObj);
            }
            
            // Fallback direction if no target found
            if (end == null) {
                try {
                    end = start.clone().add(shooter.getEyeLocation().getDirection().multiply(50));
                } catch (Exception ignored) {
                }
            }

            double resolvedTargetYOffset = resolveDoubleFromLiteralOrKey(ctx, targetYOffset, targetYOffsetKey, 1.0);
            double resolvedCurveHeight = resolveDoubleFromLiteralOrKey(ctx, curveHeight, curveHeightKey, 3.0);
            double resolvedCurveSide = resolveDoubleFromLiteralOrKey(ctx, curveSide, curveSideKey, 0.0);
            double resolvedExplosionPower = resolveDoubleFromLiteralOrKey(ctx, explosionPower, explosionPowerKey, 2.0);

            if (targetObj instanceof Entity) {
                if (end != null) end = end.clone().add(0, resolvedTargetYOffset, 0);
            }

            if (end == null || end.getWorld() == null) return ActionResult.ALLOW;
            if (end.getWorld() != start.getWorld()) return ActionResult.ALLOW;

            // Duración (ticks)
            Integer resolvedDurationTicks = durationTicks != null ? durationTicks : tryGetInt(ctx.getValue(durationTicksKey));
            Double resolvedSpeed = speed != null ? speed : tryGetDouble(ctx.getValue(speedKey));

            int ticks;
            if (resolvedDurationTicks != null && resolvedDurationTicks > 0) {
                ticks = resolvedDurationTicks;
            } else if (resolvedSpeed != null && resolvedSpeed > 0) {
                double dist = Math.max(0.1, start.distance(end));
                ticks = (int) Math.max(5, Math.min(200, Math.round((dist / resolvedSpeed) * 20.0)));
            } else {
                ticks = 40;
            }

            Class<? extends Projectile> projClass = projectileClassFromName(projectileType);
            if (projClass == null) return ActionResult.ALLOW;

            ScriptedProjectileService.ScriptInvocation inv = new ScriptedProjectileService.ScriptInvocation(
                ctx.subjectId(),
                ctx.phase() != null ? ctx.phase() : ScriptPhase.MOB,
                ctx.player(),
                ctx.variables() != null ? new HashMap<>(ctx.variables()) : null
            );

            ScriptedProjectileService.LaunchRequest req = new ScriptedProjectileService.LaunchRequest(
                projClass,
                shooter,
                start,
                end,
                targetId,
                resolvedTargetYOffset,
                homing,
                ticks,
                resolvedCurveHeight,
                resolvedCurveSide,
                null,
                null,
                explodeOnImpact,
                explodeOnFinish,
                resolvedExplosionPower,
                explosionFire,
                explosionBreakBlocks,
                onHit,
                onFinish,
                onTick,
                onLaunch,
                targets,
                inv
            );

            svc.launchCurvedProjectile(req);
            return ActionResult.ALLOW;
        };
    }

    private static Location tryResolvers.location(Object v) {
        return Resolvers.location(null, v, null);
    }

    private static double resolveDoubleFromLiteralOrKey(ScriptContext ctx, Double literal, String key, double fallback) {
        if (literal != null) return literal;
        if (ctx != null && key != null && !key.isBlank()) {
            Double v = tryGetDouble(ctx.getValue(key));
            if (v != null) return v;
        }
        return fallback;
    }

    private static int resolveIntFromLiteralOrKey(ScriptContext ctx, Integer literal, String key, int fallback) {
        if (literal != null) return literal;
        if (ctx != null && key != null && !key.isBlank()) {
            Integer v = tryGetInt(ctx.getValue(key));
            if (v != null) return v;
        }
        return fallback;
    }

    private static boolean isValidTarget(Entity target, LivingEntity shooter, TeamManager teamManager, List<String> allowedTypes) {
        if (target == null || target.equals(shooter)) return false;
        if (!(target instanceof LivingEntity)) return false;
        if (target.isDead()) return false;

        // Check types
        if (allowedTypes != null && !allowedTypes.isEmpty()) {
            boolean typeMatch = false;
            for (String type : allowedTypes) {
                if (matchesType(target, type)) {
                    typeMatch = true;
                    break;
                }
            }
            if (!typeMatch) return false;
        } else {
            // Default to PLAYERS if no types specified
            if (!(target instanceof Player)) return false;
        }

        // Check Teams/Allies for Players
        if (target instanceof Player targetPlayer && shooter instanceof Player shooterPlayer && teamManager != null) {
            Team shooterTeam = teamManager.getTeam(shooterPlayer.getUniqueId());
            if (shooterTeam != null) {
                if (shooterTeam.getMembers().contains(targetPlayer.getUniqueId())) return false;
                Team targetTeam = teamManager.getTeam(targetPlayer.getUniqueId());
                if (targetTeam != null && shooterTeam.isAlliedWith(targetTeam.getName())) return false;
            }
        }
        
        // Check Pets (exclude shooter's pets and team/ally pets)
        if (target instanceof org.bukkit.entity.Tameable tameable && tameable.isTamed()) {
            var owner = tameable.getOwner();
            if (owner != null) {
                if (owner.getUniqueId().equals(shooter.getUniqueId())) return false;
                
                if (owner instanceof Player ownerPlayer && shooter instanceof Player shooterPlayer && teamManager != null) {
                    Team shooterTeam = teamManager.getTeam(shooterPlayer.getUniqueId());
                    if (shooterTeam != null) {
                        if (shooterTeam.getMembers().contains(ownerPlayer.getUniqueId())) return false;
                        Team ownerTeam = teamManager.getTeam(ownerPlayer.getUniqueId());
                        if (ownerTeam != null && shooterTeam.isAlliedWith(ownerTeam.getName())) return false;
                    }
                }
            }
        }

        return true;
    }

    private static boolean matchesType(Entity entity, String type) {
        if (type == null) return false;
        String t = type.trim().toUpperCase(Locale.ROOT);
        return switch (t) {
            case "PLAYER", "PLAYERS" -> entity instanceof Player;
            case "MOB", "MOBS" -> entity instanceof Mob;
            case "MONSTER", "MONSTERS", "HOSTILE", "HOSTILE_MOBS" -> entity instanceof Monster;
            case "ANIMAL", "ANIMALS", "PACIFIC", "PACIFIC_MOBS", "PASSIVE" -> 
                entity instanceof org.bukkit.entity.Animals || 
                entity instanceof org.bukkit.entity.WaterMob || 
                entity instanceof org.bukkit.entity.Ambient ||
                entity instanceof org.bukkit.entity.Villager ||
                entity instanceof org.bukkit.entity.WanderingTrader;
            case "PET", "PETS" -> entity instanceof org.bukkit.entity.Tameable tameable && tameable.isTamed();
            default -> {
                try {
                    yield entity.getType() == EntityType.valueOf(t);
                } catch (Exception e) {
                    yield false;
                }
            }
        };
    }

    private static LivingEntity findNearestTarget(Location from, double maxDistance, LivingEntity shooter, TeamManager teamManager, List<String> allowedTypes) {
        if (from == null || from.getWorld() == null) return null;
        
        // Si no se especifican targets, no auto-apuntar a nada (disparar recto)
        if (allowedTypes == null || allowedTypes.isEmpty()) return null;
        
        boolean onlyPlayers = (allowedTypes.size() == 1 && (allowedTypes.get(0).equalsIgnoreCase("PLAYER") || allowedTypes.get(0).equalsIgnoreCase("PLAYERS")));

        java.util.Collection<? extends Entity> candidates;
        if (onlyPlayers) {
            candidates = from.getWorld().getPlayers();
        } else {
            candidates = from.getWorld().getNearbyEntities(from, maxDistance, maxDistance, maxDistance);
        }

        LivingEntity best = null;
        double bestSq = maxDistance * maxDistance;

        for (Entity e : candidates) {
            if (!(e instanceof LivingEntity le)) continue;
            if (!isValidTarget(le, shooter, teamManager, allowedTypes)) continue;

            double dSq;
            try {
                dSq = e.getLocation().distanceSquared(from);
            } catch (Exception ignored) {
                continue;
            }
            
            if (dSq < bestSq) {
                bestSq = dSq;
                best = le;
            }
        }
        return best;
    }

    private static Player nearestPlayer(Location from, double maxDistance) {
        if (from == null || from.getWorld() == null) return null;
        double maxSq = Math.max(0.0, maxDistance) * Math.max(0.0, maxDistance);

        Player best = null;
        double bestSq = Double.MAX_VALUE;
        for (Player p : from.getWorld().getPlayers()) {
            if (p == null || !p.isOnline() || p.isDead()) continue;
            double dSq;
            try {
                dSq = p.getLocation().distanceSquared(from);
            } catch (Exception ignored) {
                continue;
            }
            if (dSq > maxSq) continue;
            if (dSq < bestSq) {
                bestSq = dSq;
                best = p;
            }
        }
        return best;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Projectile> projectileClassFromName(String raw) {
        if (raw == null) return null;
        String k = raw.trim().toUpperCase(Locale.ROOT);
        return switch (k) {
            case "SNOWBALL" -> (Class<? extends Projectile>) org.bukkit.entity.Snowball.class;
            case "ARROW" -> (Class<? extends Projectile>) org.bukkit.entity.Arrow.class;
            case "SPECTRAL_ARROW" -> (Class<? extends Projectile>) org.bukkit.entity.SpectralArrow.class;
            case "TRIDENT" -> (Class<? extends Projectile>) org.bukkit.entity.Trident.class;
            case "SMALL_FIREBALL" -> (Class<? extends Projectile>) org.bukkit.entity.SmallFireball.class;
            case "FIREBALL" -> (Class<? extends Projectile>) org.bukkit.entity.Fireball.class;
            case "DRAGON_FIREBALL" -> (Class<? extends Projectile>) org.bukkit.entity.DragonFireball.class;
            case "WITHER_SKULL" -> (Class<? extends Projectile>) org.bukkit.entity.WitherSkull.class;
            case "ENDER_PEARL" -> (Class<? extends Projectile>) org.bukkit.entity.EnderPearl.class;
            default -> (Class<? extends Projectile>) org.bukkit.entity.Snowball.class;
        };
    }

    public static Action setZombieBaby(boolean baby) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            org.bukkit.entity.Zombie zombie = ctx.subjectOrEventEntity(org.bukkit.entity.Zombie.class);
            if (zombie == null) return ActionResult.ALLOW;

            final org.bukkit.entity.Zombie zombieFinal = zombie;
            runSync(plugin, () -> {
                try {
                    zombieFinal.setBaby(baby);
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action setVexCharging(boolean charging) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            org.bukkit.entity.Vex vex = ctx.subjectOrEventEntity(org.bukkit.entity.Vex.class);
            if (vex == null) return ActionResult.ALLOW;

            final org.bukkit.entity.Vex vexFinal = vex;
            runSync(plugin, () -> {
                try {
                    vexFinal.setCharging(charging);
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action setShulkerColor(String color) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            org.bukkit.entity.Shulker shulker = ctx.subjectOrEventEntity(org.bukkit.entity.Shulker.class);
            if (shulker == null) return ActionResult.ALLOW;

            org.bukkit.DyeColor dye;
            try {
                dye = org.bukkit.DyeColor.valueOf(color.trim().toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
                dye = null;
            }
            if (dye == null) return ActionResult.ALLOW;

            final org.bukkit.entity.Shulker shulkerFinal = shulker;
            final org.bukkit.DyeColor dyeFinal = dye;
            runSync(plugin, () -> {
                try {
                    shulkerFinal.setColor(dyeFinal);
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action setIronGolemPlayerCreated(boolean created) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            org.bukkit.entity.IronGolem golem = ctx.subjectOrEventEntity(org.bukkit.entity.IronGolem.class);
            if (golem == null) return ActionResult.ALLOW;

            final org.bukkit.entity.IronGolem golemFinal = golem;
            runSync(plugin, () -> {
                try {
                    golemFinal.setPlayerCreated(created);
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action setMobTargetNearestPlayer(int radius) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            org.bukkit.entity.Mob mob = ctx.subjectOrEventEntity(org.bukkit.entity.Mob.class);
            if (mob == null) return ActionResult.ALLOW;

            final org.bukkit.entity.Mob mobFinal = mob;
            final int r = Math.max(1, radius);
            runSync(plugin, () -> {
                try {
                    var loc = mobFinal.getLocation();
                    if (loc.getWorld() == null) return;

                    org.bukkit.entity.Player nearest = null;
                    double best = Double.MAX_VALUE;
                    double max = (double) r * (double) r;

                    for (org.bukkit.entity.Player p : loc.getWorld().getPlayers()) {
                        double d = p.getLocation().distanceSquared(loc);
                        if (d < best && d <= max) {
                            best = d;
                            nearest = p;
                        }
                    }
                    if (nearest != null) {
                        mobFinal.setTarget(nearest);
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action setMobAttributeBase(String attributeName, Double value, String valueKey) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            LivingEntity target = ctx.subjectOrEventEntity(LivingEntity.class);
            if (target == null) return ActionResult.ALLOW;

            Attribute attr;
            try {
                attr = Attribute.valueOf(attributeName.trim().toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                attr = null;
            }
            if (attr == null) return ActionResult.ALLOW;

            Double v = value;
            if (v == null && valueKey != null && !valueKey.isBlank()) {
                v = tryGetDouble(ctx.getValue(valueKey));
            }
            if (v == null || Double.isNaN(v) || Double.isInfinite(v)) return ActionResult.ALLOW;
            double base = v;

            final LivingEntity targetFinal = target;
            final double baseFinal = base;
            final Attribute attrFinal = attr;
            runSync(plugin, () -> {
                try {
                    AttributeInstance inst = targetFinal.getAttribute(attrFinal);
                    if (inst == null) return;
                    inst.setBaseValue(baseFinal);

                    if (attrFinal == Attribute.MAX_HEALTH) {
                        try {
                            targetFinal.setHealth(Math.max(0.0, Math.min(baseFinal, inst.getValue())));
                        } catch (Exception ignored) {
                        }
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action setMobMaxHealth(Double value, String valueKey) {
        return ctx -> {
            Double v = value;
            if (v == null && valueKey != null && !valueKey.isBlank()) {
                v = tryGetDouble(ctx.getValue(valueKey));
            }
            if (v == null) return ActionResult.ALLOW;
            return setMobAttributeBase("MAX_HEALTH", v, null).run(ctx);
        };
    }

    public static Action applyEffectNearLocation(
        String locationKey,
        String effectName,
        int duration,
        int amplifier,
        int radius,
        boolean includePlayers,
        boolean includeMobs,
        boolean ambient,
        boolean particles
    ) {
        return applyEffectNearLocation((Object) locationKey, effectName, duration, amplifier, radius, includePlayers, includeMobs, ambient, particles);
    }

    public static Action applyEffectNearLocation(
        Object locationSpec,
        String effectName,
        int duration,
        int amplifier,
        int radius,
        boolean includePlayers,
        boolean includeMobs,
        boolean ambient,
        boolean particles
    ) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            Location loc = Resolvers.location(locationSpec, ctx, ctx.player() != null ? ctx.player().getWorld() : null);
            if (loc == null || loc.getWorld() == null) {
                return ActionResult.ALLOW;
            }

            PotionEffectType type;
            try {
                type = PotionEffectType.getByName(effectName.trim().toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                type = null;
            }
            if (type == null) return ActionResult.ALLOW;

            PotionEffect eff = new PotionEffect(type, Math.max(1, duration), Math.max(0, amplifier), ambient, particles);
            int r = Math.max(0, radius);

            runSync(plugin, () -> {
                try {
                    for (Entity e : loc.getNearbyEntities(r, r, r)) {
                        if (!(e instanceof LivingEntity le)) continue;
                        if (!includePlayers && e instanceof Player) continue;
                        if (!includeMobs && !(e instanceof Player)) continue;
                        try {
                            le.addPotionEffect(eff);
                        } catch (Exception ignored) {
                        }
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action freezeArea(
        String locationKey,
        int radius,
        double airChance,
        String airMaterial,
        String waterMaterial,
        String iceMaterial
    ) {
        return freezeArea((Object) locationKey, radius, airChance, airMaterial, waterMaterial, iceMaterial);
    }

    public static Action freezeArea(
        Object locationSpec,
        int radius,
        double airChance,
        String airMaterial,
        String waterMaterial,
        String iceMaterial
    ) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            Location loc = Resolvers.location(locationSpec, ctx, ctx.player() != null ? ctx.player().getWorld() : null);
            if (loc == null || loc.getWorld() == null) {
                return ActionResult.ALLOW;
            }

            org.bukkit.Material airMat;
            org.bukkit.Material waterMat;
            org.bukkit.Material iceMat;
            try {
                airMat = org.bukkit.Material.valueOf(airMaterial.trim().toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                airMat = org.bukkit.Material.POWDER_SNOW;
            }
            try {
                waterMat = org.bukkit.Material.valueOf(waterMaterial.trim().toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                waterMat = org.bukkit.Material.WATER;
            }
            try {
                iceMat = org.bukkit.Material.valueOf(iceMaterial.trim().toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                iceMat = org.bukkit.Material.ICE;
            }

            int r = Math.max(0, radius);
            double p = Math.max(0.0, Math.min(1.0, airChance));

            final org.bukkit.Material airMatFinal = airMat;
            final org.bukkit.Material waterMatFinal = waterMat;
            final org.bukkit.Material iceMatFinal = iceMat;

            runSync(plugin, () -> {
                try {
                    Location origin = loc.clone();
                    for (int x = -r; x <= r; x++) {
                        for (int y = -r; y <= r; y++) {
                            for (int z = -r; z <= r; z++) {
                                Location at = origin.clone().add(x, y, z);
                                if (at.distance(origin) > r) continue;
                                var block = at.getBlock();
                                var t = block.getType();

                                if (t == org.bukkit.Material.AIR) {
                                    if (ThreadLocalRandom.current().nextDouble() < p) {
                                        try {
                                            block.setType(airMatFinal);
                                        } catch (Exception ignored) {
                                        }
                                    }
                                } else if (t == waterMatFinal) {
                                    try {
                                        block.setType(iceMatFinal);
                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action mathSetVar(String targetKey, String op, Double a, String aKey, Double b, String bKey, Double c, String cKey) {
        return ctx -> {
            Double left = a != null ? a : tryGetDouble(ctx.getValue(aKey));
            if (left == null) return ActionResult.ALLOW;

            Double right = null;
            if (!isUnaryMathOp(op)) {
                right = b != null ? b : tryGetDouble(ctx.getValue(bKey));
                if (right == null) return ActionResult.ALLOW;
            }

            Double third = null;
            if (isTernaryMathOp(op)) {
                third = c != null ? c : tryGetDouble(ctx.getValue(cKey));
                if (third == null) return ActionResult.ALLOW;
            }

            String o = op.trim().toLowerCase(Locale.ROOT);
            double out;
            switch (o) {
                case "set", "=", "assign" -> out = left;
                case "add", "+" -> out = left + right;
                case "sub", "-" -> out = left - right;
                case "mul", "*" -> out = left * right;
                case "div", "/" -> out = right == 0.0 ? left : (left / right);
                case "min" -> out = Math.min(left, right);
                case "max" -> out = Math.max(left, right);
                case "abs" -> out = Math.abs(left);
                case "neg" -> out = -left;
                case "floor" -> out = Math.floor(left);
                case "ceil" -> out = Math.ceil(left);
                case "round" -> out = Math.rint(left);
                case "pow" -> out = Math.pow(left, right);
                case "mod", "%" -> out = right == 0.0 ? left : (left % right);
                case "sqrt" -> out = left < 0.0 ? Double.NaN : Math.sqrt(left);
                case "cbrt" -> out = Math.cbrt(left);
                case "ln" -> out = left <= 0.0 ? Double.NaN : Math.log(left);
                case "log10" -> out = left <= 0.0 ? Double.NaN : Math.log10(left);
                case "exp" -> out = Math.exp(left);

                case "sin" -> out = Math.sin(left);
                case "cos" -> out = Math.cos(left);
                case "tan" -> out = Math.tan(left);
                case "asin" -> out = Math.asin(left);
                case "acos" -> out = Math.acos(left);
                case "atan" -> out = Math.atan(left);
                case "atan2" -> out = Math.atan2(left, right);

                case "sin_deg" -> out = Math.sin(Math.toRadians(left));
                case "cos_deg" -> out = Math.cos(Math.toRadians(left));
                case "tan_deg" -> out = Math.tan(Math.toRadians(left));

                case "deg_to_rad" -> out = Math.toRadians(left);
                case "rad_to_deg" -> out = Math.toDegrees(left);

                case "rand" -> {
                    double max = left;
                    if (max == 0.0) {
                        out = 0.0;
                    } else {
                        out = ThreadLocalRandom.current().nextDouble(Math.min(0.0, max), Math.max(0.0, max));
                    }
                }
                case "rand_range" -> out = ThreadLocalRandom.current().nextDouble(Math.min(left, right), Math.max(left, right));

                case "clamp" -> {
                    double min = Math.min(right, third);
                    double max = Math.max(right, third);
                    out = Math.max(min, Math.min(max, left));
                }
                case "lerp" -> out = left + (right - left) * third;
                default -> {
                    return ActionResult.ALLOW;
                }
            }

            if (Double.isNaN(out) || Double.isInfinite(out)) return ActionResult.ALLOW;
            ctx.setGenericVarCompat(targetKey, out);
            return ActionResult.ALLOW;
        };
    }

    private static boolean isUnaryMathOp(String op) {
        if (op == null) return false;
        String o = op.trim().toLowerCase(Locale.ROOT);
        return switch (o) {
            case "abs", "floor", "ceil", "round", "neg", "sqrt", "cbrt", "ln", "log10", "exp",
                 "sin", "cos", "tan", "asin", "acos", "atan",
                 "sin_deg", "cos_deg", "tan_deg",
                 "deg_to_rad", "rad_to_deg",
                 "set", "=", "assign",
                 "rand" -> true;
            default -> false;
        };
    }

    private static boolean isTernaryMathOp(String op) {
        if (op == null) return false;
        String o = op.trim().toLowerCase(Locale.ROOT);
        return "clamp".equals(o) || "lerp".equals(o);
    }

    private static Double tryGetDouble(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.doubleValue();
        return Resolvers.resolveDouble(String.valueOf(v));
    }

    private static Integer tryGetInt(Object v) {
        Double d = tryGetDouble(v);
        if (d == null) return null;
        if (Double.isNaN(d) || Double.isInfinite(d)) return null;
        try {
            return (int) Math.round(d);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Action addEventDeathDrop(String materialName, int amount) {
        return ctx -> {
            EntityDeathEvent event = ctx.nativeEvent(EntityDeathEvent.class);
            if (event == null) return ActionResult.ALLOW;

            org.bukkit.Material mat;
            try {
                mat = org.bukkit.Material.valueOf(materialName.trim().toUpperCase(Locale.ROOT));
            } catch (Exception e) {
                mat = null;
            }
            if (mat == null || mat == org.bukkit.Material.AIR) return ActionResult.ALLOW;

            event.getDrops().add(new ItemStack(mat, Math.max(1, amount)));
            return ActionResult.ALLOW;
        };
    }

    public static Action clearEventDeathDrops() {
        return ctx -> {
            EntityDeathEvent event = ctx.nativeEvent(EntityDeathEvent.class);
            if (event == null) return ActionResult.ALLOW;
            event.getDrops().clear();
            return ActionResult.ALLOW;
        };
    }

    public static Action setEventEntitySilent(boolean value) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            EntitySpawnEvent ev = ctx.nativeEvent(EntitySpawnEvent.class);
            if (plugin == null || ev == null) return ActionResult.ALLOW;

            Entity entity = ev.getEntity();
            if (entity == null) return ActionResult.ALLOW;

            final Entity entityFinal = entity;
            runSync(plugin, () -> entityFinal.setSilent(value));
            return ActionResult.ALLOW;
        };
    }

    public static Action setMonstersSilentAllWorlds(boolean value) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;
            runSync(plugin, () -> {
                for (World w : Bukkit.getWorlds()) {
                    for (Entity e : w.getEntities()) {
                        if (e instanceof Monster m) {
                            try {
                                m.setSilent(value);
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action multiplyExplosionRadius(double multiplier) {
        return ctx -> {
            ExplosionPrimeEvent e = ctx.nativeEvent(ExplosionPrimeEvent.class);
            if (e == null) return ActionResult.ALLOW;
            float r = e.getRadius();
            float next = (float) Math.max(0.0, r * multiplier);
            e.setRadius(next);
            return ActionResult.ALLOW;
        };
    }

    public static Action damageHelmetDurability(int amount) {
        return damageItemDurability(EquipmentSlot.HEAD, amount);
    }

    public static Action damageItemDurability(EquipmentSlot slot, int amount) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            int amt = Math.max(1, amount);
            runSync(plugin, () -> {
                ItemStack item = getItemInSlot(player, slot);
                if (item == null || item.getType().isAir()) return;

                var meta = item.getItemMeta();
                if (!(meta instanceof Damageable damageable)) return;

                int max = item.getType().getMaxDurability();
                if (max <= 0) return;

                int unbreaking = item.getEnchantmentLevel(Enchantment.UNBREAKING);

                int currentDamage = damageable.getDamage();
                int remaining = max - currentDamage;

                if (remaining <= 1) {
                    ItemStack drop = item.clone();
                    drop.editMeta(m -> {
                        if (m instanceof Damageable d) {
                            d.setDamage(Math.max(max - 1, 0));
                        }
                    });
                    setItemInSlot(player, slot, null);
                    player.getWorld().dropItemNaturally(player.getLocation(), drop);
                    return;
                }

                for (int i = 0; i < amt; i++) {
                    boolean takeDamage = true;
                    if (unbreaking > 0) {
                        takeDamage = ThreadLocalRandom.current().nextInt(unbreaking + 1) == 0;
                    }
                    if (!takeDamage) continue;

                    currentDamage = Math.min(max - 1, currentDamage + 1);
                    remaining = max - currentDamage;
                    if (remaining <= 1) {
                        ItemStack drop = item.clone();
                        drop.editMeta(m -> {
                            if (m instanceof Damageable d) {
                                d.setDamage(Math.max(max - 1, 0));
                            }
                        });
                        setItemInSlot(player, slot, null);
                        player.getWorld().dropItemNaturally(player.getLocation(), drop);
                        return;
                    }
                }

                damageable.setDamage(currentDamage);
                item.setItemMeta(meta);
                setItemInSlot(player, slot, item);
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action unequipItem(EquipmentSlot slot, boolean dropIfFull, String message, String color) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                ItemStack item = getItemInSlot(player, slot);
                if (item == null || item.getType().isAir()) return;

                setItemInSlot(player, slot, null);
                Map<Integer, ItemStack> leftovers = player.getInventory().addItem(item);
                if (!leftovers.isEmpty() && dropIfFull) {
                    for (ItemStack left : leftovers.values()) {
                        if (left == null || left.getType().isAir()) continue;
                        player.getWorld().dropItemNaturally(player.getLocation(), left);
                    }
                }

                if (message != null && !message.isBlank()) {
                    NamedTextColor c = java.util.Optional.ofNullable(Resolvers.color(ctx, color)).orElse(NamedTextColor.RED);
                    player.sendMessage(Component.text(message, c));
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action aggroNearbyMobs(String entityType, int radius, boolean onlyIfNoTarget) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null || player.getWorld() == null) return ActionResult.ALLOW;

            EntityType type = Resolvers.resolveEntityType(entityType);
            if (type == null) return ActionResult.ALLOW;
            int r = Math.max(1, radius);

            runSync(plugin, () -> {
                for (Entity e : player.getNearbyEntities(r, r, r)) {
                    if (e == null || e.getType() != type) continue;
                    if (!(e instanceof Mob mob)) continue;
                    if (onlyIfNoTarget && mob.getTarget() != null) continue;
                    try {
                        mob.setTarget(player);
                    } catch (Exception ignored) {
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action cancelEvent(boolean cancelled) {
        return ctx -> {
            Object ev = ctx.nativeEvent();
            if (!(ev instanceof Cancellable c)) return ActionResult.ALLOW;
            c.setCancelled(cancelled);
            return ActionResult.ALLOW;
        };
    }

    public static Action setEventUseInteractedBlock(String resultValue) {
        final String valueFinal = resultValue != null ? resultValue.trim().toUpperCase(java.util.Locale.ROOT) : "DEFAULT";
        return ctx -> {
            PlayerInteractEvent e = ctx.nativeEvent(PlayerInteractEvent.class);
            if (e == null) return ActionResult.ALLOW;
            try {
                org.bukkit.event.Event.Result result = switch (valueFinal) {
                    case "ALLOW" -> org.bukkit.event.Event.Result.ALLOW;
                    case "DENY" -> org.bukkit.event.Event.Result.DENY;
                    default -> org.bukkit.event.Event.Result.DEFAULT;
                };
                e.setUseInteractedBlock(result);
            } catch (Exception ignored) {
            }
            return ActionResult.ALLOW;
        };
    }

    public static Action setEventUseItemInHand(String resultValue) {
        final String valueFinal = resultValue != null ? resultValue.trim().toUpperCase(java.util.Locale.ROOT) : "DEFAULT";
        return ctx -> {
            PlayerInteractEvent e = ctx.nativeEvent(PlayerInteractEvent.class);
            if (e == null) return ActionResult.ALLOW;
            try {
                org.bukkit.event.Event.Result result = switch (valueFinal) {
                    case "ALLOW" -> org.bukkit.event.Event.Result.ALLOW;
                    case "DENY" -> org.bukkit.event.Event.Result.DENY;
                    default -> org.bukkit.event.Event.Result.DEFAULT;
                };
                e.setUseItemInHand(result);
            } catch (Exception ignored) {
            }
            return ActionResult.ALLOW;
        };
    }

    public static Action removeEventItemEntity() {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            EntityPickupItemEvent e = ctx.nativeEvent(EntityPickupItemEvent.class);
            if (plugin == null || e == null) return ActionResult.ALLOW;
            runSync(plugin, () -> {
                try {
                    e.getItem().remove();
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action giveItem(Map<?, ?> params) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            String materialName = Resolvers.string(ctx, params, "material");
            org.bukkit.Material mat = Resolvers.resolveMaterial(materialName);
            if (mat == null || mat.isAir()) return ActionResult.ALLOW;

            Integer amount = Resolvers.integer(ctx, params, "amount", "amount_key");
            int amt = amount != null ? Math.max(1, amount) : 1;

            final ItemStack stack = new ItemStack(mat, amt);
            runSync(plugin, () -> {
                Map<Integer, ItemStack> leftovers = player.getInventory().addItem(stack);
                if (!leftovers.isEmpty()) {
                    for (ItemStack left : leftovers.values()) {
                        if (left == null || left.getType().isAir()) continue;
                        player.getWorld().dropItemNaturally(player.getLocation(), left);
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action addPlayerAttributeModifier(String attributeName, String keyName, String operationName, double amount, String slotGroupName) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            Attribute attribute;
            try {
                attribute = Attribute.valueOf(attributeName.trim().toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
                attribute = null;
            }
            if (attribute == null) return ActionResult.ALLOW;

            final Attribute attributeFinal = attribute;

            AttributeModifier.Operation op;
            try {
                op = AttributeModifier.Operation.valueOf(operationName.trim().toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
                op = null;
            }
            if (op == null) return ActionResult.ALLOW;

            final AttributeModifier.Operation opFinal = op;

            EquipmentSlotGroup group;
            group = parseEquipmentSlotGroup(slotGroupName);

            final NamespacedKey key = new NamespacedKey(plugin, keyName);
            final double amt = amount;
            final EquipmentSlotGroup groupFinal = group;

            runSync(plugin, () -> {
                try {
                    AttributeInstance inst = player.getAttribute(attributeFinal);
                    if (inst == null) return;
                    boolean present = inst.getModifiers().stream().anyMatch(m -> {
                        try {
                            return Objects.equals(m.getKey(), key);
                        } catch (Exception ignored2) {
                            return false;
                        }
                    });
                    if (present) return;
                    inst.addModifier(new AttributeModifier(key, amt, opFinal, groupFinal));
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action removePlayerAttributeModifier(String attributeName, String keyName) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            Attribute attribute;
            try {
                attribute = Attribute.valueOf(attributeName.trim().toUpperCase(Locale.ROOT));
            } catch (Exception ignored) {
                attribute = null;
            }
            if (attribute == null) return ActionResult.ALLOW;

            final Attribute attributeFinal = attribute;

            final NamespacedKey key = new NamespacedKey(plugin, keyName);
            runSync(plugin, () -> {
                try {
                    AttributeInstance inst = player.getAttribute(attributeFinal);
                    if (inst == null) return;
                    for (AttributeModifier m : inst.getModifiers()) {
                        try {
                            if (Objects.equals(m.getKey(), key)) {
                                inst.removeModifier(m);
                                break;
                            }
                        } catch (Exception ignored2) {
                        }
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }



    private static EquipmentSlotGroup parseEquipmentSlotGroup(String name) {
        if (name == null || name.isBlank()) return EquipmentSlotGroup.ANY;
        String n = name.trim().toUpperCase(Locale.ROOT);

        // Alias comunes
        if ("HAND".equals(n) || "MAIN_HAND".equals(n) || "MAIN".equals(n)) n = "MAINHAND";
        if ("HELMET".equals(n)) n = "HEAD";
        if ("CHESTPLATE".equals(n)) n = "CHEST";
        if ("LEGGINGS".equals(n)) n = "LEGS";
        if ("BOOTS".equals(n)) n = "FEET";

        // Manual (sin reflection) usando constantes conocidas en este repo/API.
        return switch (n) {
            case "ANY" -> EquipmentSlotGroup.ANY;
            case "MAINHAND" -> EquipmentSlotGroup.MAINHAND;
            case "HEAD" -> EquipmentSlotGroup.HEAD;
            case "CHEST" -> EquipmentSlotGroup.CHEST;
            case "LEGS" -> EquipmentSlotGroup.LEGS;
            case "FEET" -> EquipmentSlotGroup.FEET;
            default -> EquipmentSlotGroup.ANY;
        };
    }

    private static ItemStack getItemInSlot(Player player, EquipmentSlot slot) {
        if (player == null || slot == null) return null;
        try {
            EntityEquipment eq = player.getEquipment();
            return eq != null ? eq.getItem(slot) : null;
        } catch (Exception ignored) {
            // fallback para versiones raras
            if (slot == EquipmentSlot.HAND) return player.getInventory().getItemInMainHand();
            if (slot == EquipmentSlot.OFF_HAND) return player.getInventory().getItemInOffHand();
            return null;
        }
    }

    private static void setItemInSlot(Player player, EquipmentSlot slot, ItemStack item) {
        if (player == null || slot == null) return;
        try {
            EntityEquipment eq = player.getEquipment();
            if (eq != null) {
                eq.setItem(slot, item);
                return;
            }
        } catch (Exception ignored) {
        }

        try {
            if (slot == EquipmentSlot.HAND) {
                player.getInventory().setItemInMainHand(item);
            } else if (slot == EquipmentSlot.OFF_HAND) {
                player.getInventory().setItemInOffHand(item);
            } else if (slot == EquipmentSlot.HEAD) {
                player.getInventory().setHelmet(item);
            } else if (slot == EquipmentSlot.CHEST) {
                player.getInventory().setChestplate(item);
            } else if (slot == EquipmentSlot.LEGS) {
                player.getInventory().setLeggings(item);
            } else if (slot == EquipmentSlot.FEET) {
                player.getInventory().setBoots(item);
            }
        } catch (Exception ignored) {
        }
    }

    public static Action inflateVillagerPricesAllWorlds(int multiplier, String keyName) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;
            final NamespacedKey key = new NamespacedKey(plugin, keyName);

            runSync(plugin, () -> {
                for (World w : Bukkit.getWorlds()) {
                    for (Entity e : w.getEntities()) {
                        if (e instanceof Villager v) {
                            inflateVillagerPrices(plugin, v, multiplier, key);
                        }
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action deflateVillagerPricesAllWorlds(int divisor, String keyName) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;
            final NamespacedKey key = new NamespacedKey(plugin, keyName);

            runSync(plugin, () -> {
                for (World w : Bukkit.getWorlds()) {
                    for (Entity e : w.getEntities()) {
                        if (e instanceof Villager v) {
                            deflateVillagerPrices(v, divisor, key);
                        }
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action inflateEventVillagerPrices(int multiplier, String keyName) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            EntitySpawnEvent ese = ctx.nativeEvent(EntitySpawnEvent.class);
            if (plugin == null || ese == null) return ActionResult.ALLOW;
            if (!(ese.getEntity() instanceof Villager villager)) return ActionResult.ALLOW;

            final NamespacedKey key = new NamespacedKey(plugin, keyName);
            Bukkit.getScheduler().runTask(plugin, () -> inflateVillagerPrices(plugin, villager, multiplier, key));
            return ActionResult.ALLOW;
        };
    }

    public static Action multiplyVillagerTradeCost(int multiplier) {
        return ctx -> {
            VillagerAcquireTradeEvent event = ctx.nativeEvent(VillagerAcquireTradeEvent.class);
            if (event == null) return ActionResult.ALLOW;

            MerchantRecipe recipe = event.getRecipe();
            if (recipe == null) return ActionResult.ALLOW;
            multiplyRecipeCost(recipe, multiplier);
            event.setRecipe(recipe);

            return ActionResult.ALLOW;
        };
    }

    public static Action setSkeletonsBowIntervalAllWorlds(int interval) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                for (World w : Bukkit.getWorlds()) {
                    for (Entity e : w.getEntities()) {
                        if (e instanceof Skeleton s) {
                            trySetSkeletonBowInterval(s, interval);
                        }
                    }
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action setEventSkeletonBowInterval(int interval) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            EntitySpawnEvent ese = ctx.nativeEvent(EntitySpawnEvent.class);
            if (plugin == null || ese == null) return ActionResult.ALLOW;
            if (!(ese.getEntity() instanceof Skeleton skeleton)) return ActionResult.ALLOW;

            runSync(plugin, () -> trySetSkeletonBowInterval(skeleton, interval));
            return ActionResult.ALLOW;
        };
    }

    public static Action cursedEarthSpawnGiantWithLoot(String keyName) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            PlayerDeathEvent event = ctx.nativeEvent(PlayerDeathEvent.class);
            if (plugin == null || event == null) return ActionResult.ALLOW;

            Player player = event.getEntity();
            if (player == null || player.getWorld() == null) return ActionResult.ALLOW;

            final NamespacedKey lootKey = new NamespacedKey(plugin, keyName);
            runSync(plugin, () -> {
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

                storeLootToPdc(giant, drops, lootKey);
                setupGiantAI(giant);
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action cursedEarthRestoreLootOnGiantDeath(String keyName) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            EntityDeathEvent event = ctx.nativeEvent(EntityDeathEvent.class);
            if (plugin == null || event == null) return ActionResult.ALLOW;
            if (event.getEntityType() != EntityType.GIANT) return ActionResult.ALLOW;

            NamespacedKey lootKey = new NamespacedKey(plugin, keyName);
            if (!event.getEntity().getPersistentDataContainer().has(lootKey, PersistentDataType.STRING)) return ActionResult.ALLOW;

            List<ItemStack> loot = retrieveLootFromPdc(event.getEntity(), lootKey);
            if (!loot.isEmpty()) {
                event.getDrops().addAll(loot);
            }
            return ActionResult.ALLOW;
        };
    }

    private static void inflateVillagerPrices(RollAndDeathSMP plugin, Villager villager, int multiplier, NamespacedKey key) {
        if (villager == null) return;
        try {
            if (villager.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) return;
        } catch (Exception ignored) {
            return;
        }

        try {
            List<MerchantRecipe> recipes = new java.util.ArrayList<>(villager.getRecipes());
            for (MerchantRecipe r : recipes) {
                multiplyRecipeCost(r, multiplier);
            }
            villager.setRecipes(recipes);
            villager.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        } catch (Exception ignored) {
        }
    }

    private static void deflateVillagerPrices(Villager villager, int divisor, NamespacedKey key) {
        if (villager == null) return;
        try {
            if (!villager.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) return;
        } catch (Exception ignored) {
            return;
        }

        try {
            List<MerchantRecipe> recipes = new java.util.ArrayList<>(villager.getRecipes());
            for (MerchantRecipe r : recipes) {
                divideRecipeCost(r, divisor);
            }
            villager.setRecipes(recipes);
            villager.getPersistentDataContainer().remove(key);
        } catch (Exception ignored) {
        }
    }

    private static void multiplyRecipeCost(MerchantRecipe recipe, int multiplier) {
        if (recipe == null) return;
        try {
            List<ItemStack> ingredients = recipe.getIngredients();
            for (ItemStack ing : ingredients) {
                if (ing != null && ing.getType() == org.bukkit.Material.EMERALD) {
                    int newAmount = ing.getAmount() * multiplier;
                    if (newAmount > 64) newAmount = 64;
                    ing.setAmount(newAmount);
                }
            }
            recipe.setIngredients(ingredients);
        } catch (Exception ignored) {
        }
    }

    private static void divideRecipeCost(MerchantRecipe recipe, int divisor) {
        if (recipe == null) return;
        try {
            List<ItemStack> ingredients = recipe.getIngredients();
            for (ItemStack ing : ingredients) {
                if (ing != null && ing.getType() == org.bukkit.Material.EMERALD) {
                    int newAmount = Math.max(1, ing.getAmount() / divisor);
                    ing.setAmount(newAmount);
                }
            }
            recipe.setIngredients(ingredients);
        } catch (Exception ignored) {
        }
    }

    private static void trySetSkeletonBowInterval(Skeleton skeleton, int interval) {
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

    private static void storeLootToPdc(Giant giant, List<ItemStack> items, NamespacedKey lootKey) {
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.set("c", items);
            String encoded = config.saveToString();
            giant.getPersistentDataContainer().set(lootKey, PersistentDataType.STRING, encoded);
        } catch (Exception ignored) {
        }
    }

    private static List<ItemStack> retrieveLootFromPdc(Entity entity, NamespacedKey lootKey) {
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

    private static void setupGiantAI(Giant bukkitGiant) {
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
    public static Action killPlayer() {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            runSync(plugin, () -> player.setHealth(0.0));
            return ActionResult.ALLOW;
        };
    }

    public static Action damagePlayer(double amount) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            double amt = Math.max(0.0, amount);
            runSync(plugin, () -> player.damage(amt));
            return ActionResult.ALLOW;
        };
    }

    public static Action broadcast(String message, String color) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            final NamedTextColor cFinal = java.util.Optional.ofNullable(Resolvers.color(ctx, color)).orElse(NamedTextColor.YELLOW);
            String text = PlaceholderUtil.resolvePlaceholders(plugin, player, message);
            if (player != null) {
                text = text.replace("%player%", player.getName());
            }
            final String textFinal = text;
            runSync(plugin, () -> plugin.getServer().broadcast(Component.text(textFinal, cFinal)));
            return ActionResult.ALLOW;
        };
    }

    public static Action broadcastFromKey(String messageKey, String color) {
        final String keyFinal = messageKey;
        return ctx -> {
            if (ctx == null) return ActionResult.ALLOW;
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            Player player = ctx.player();
            Object v;
            try {
                v = ctx.getValue(keyFinal);
            } catch (Exception ignored) {
                v = null;
            }

            if (v == null) return ActionResult.ALLOW;
            String msg = String.valueOf(v);
            if (msg.isBlank()) return ActionResult.ALLOW;

            final NamedTextColor cFinal = java.util.Optional.ofNullable(Resolvers.color(ctx, color)).orElse(NamedTextColor.YELLOW);
            String text = PlaceholderUtil.resolvePlaceholders(plugin, player, msg);
            if (player != null) {
                text = text.replace("%player%", player.getName());
            }
            final String textFinal = text;
            runSync(plugin, () -> plugin.getServer().broadcast(Component.text(textFinal, cFinal)));
            return ActionResult.ALLOW;
        };
    }

    private static Sound parseSound(String name) {
        if (name == null || name.isBlank()) return null;
        try {
            return Sound.valueOf(name.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
            return null;
        }
    }





    public static Action setEventDamage(double value) {
        return ctx -> {
            Object ev = ctx.nativeEvent();
            if (ev instanceof EntityDamageEvent dmg) {
                dmg.setDamage(Math.max(0.0, value));
                return ActionResult.ALLOW;
            }
            if (ev instanceof PlayerItemDamageEvent itemDmg) {
                itemDmg.setDamage(Math.max(0, (int) Math.round(value)));
                return ActionResult.ALLOW;
            }
            if (ev instanceof EntityDamageByEntityEvent byEntity) {
                byEntity.setDamage(Math.max(0.0, value));
                return ActionResult.ALLOW;
            }
            return ActionResult.ALLOW;
        };
    }

    public static Action multiplyEventDamage(double multiplier) {
        return ctx -> {
            Object ev = ctx.nativeEvent();
            double mult = multiplier;

            if (ev instanceof EntityDamageEvent dmg) {
                dmg.setDamage(Math.max(0.0, dmg.getDamage() * mult));
                return ActionResult.ALLOW;
            }
            if (ev instanceof PlayerItemDamageEvent itemDmg) {
                itemDmg.setDamage(Math.max(0, (int) Math.round(itemDmg.getDamage() * mult)));
                return ActionResult.ALLOW;
            }
            if (ev instanceof EntityDamageByEntityEvent byEntity) {
                byEntity.setDamage(Math.max(0.0, byEntity.getDamage() * mult));
                return ActionResult.ALLOW;
            }
            return ActionResult.ALLOW;
        };
    }

    public static Action addVelocity(double x, double y, double z) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;
            Vector add = new Vector(x, y, z);
            runSync(plugin, () -> player.setVelocity(player.getVelocity().add(add)));
            return ActionResult.ALLOW;
        };
    }

    private static final java.util.Map<RollAndDeathSMP, net.rollanddeath.smp.core.modifiers.scripted.PersistentShadowService> PERSISTENT_SHADOW = new java.util.concurrent.ConcurrentHashMap<>();

    private static PotionEffectType resolvePotionEffectType(String effectName) {
        if (effectName == null || effectName.isBlank()) return null;
        try {
            return PotionEffectType.getByName(effectName.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Action removeEffect(String effectName, Integer onlyIfAmplifier) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            final PotionEffectType type = resolvePotionEffectType(effectName);
            if (type == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                PotionEffect current = player.getPotionEffect(type);
                if (current == null) return;
                if (onlyIfAmplifier != null && current.getAmplifier() != onlyIfAmplifier) return;
                player.removePotionEffect(type);
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action healPlayer(double amount) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            double amt = Math.max(0.0, amount);
            if (amt <= 0.0) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                try {
                    double max = player.getAttribute(Attribute.MAX_HEALTH) != null ? player.getAttribute(Attribute.MAX_HEALTH).getValue() : player.getMaxHealth();
                    double next = Math.min(player.getHealth() + amt, max);
                    player.setHealth(next);
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action healPlayerFromKey(String amountKey) {
        return ctx -> {
            if (amountKey == null || amountKey.isBlank()) return ActionResult.ALLOW;

            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            Double amt = tryGetDouble(ctx.getValue(amountKey));
            if (amt == null) return ActionResult.ALLOW;
            double amount = Math.max(0.0, amt);
            if (amount <= 0.0) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                try {
                    double max = player.getAttribute(Attribute.MAX_HEALTH) != null ? player.getAttribute(Attribute.MAX_HEALTH).getValue() : player.getMaxHealth();
                    double next = Math.min(player.getHealth() + amount, max);
                    player.setHealth(next);
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action consumeEventItem(int amount) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            int a = Math.max(1, amount);
            Object ev = ctx.nativeEvent();

            runSync(plugin, () -> {
                try {
                    if (ev instanceof org.bukkit.event.player.PlayerInteractEvent pie) {
                        var hand = pie.getHand();
                        ItemStack item = pie.getItem();
                        if (item == null) return;

                        int next = item.getAmount() - a;
                        if (next <= 0) {
                            item.setAmount(0);
                            if (hand == org.bukkit.inventory.EquipmentSlot.HAND) {
                                player.getInventory().setItemInMainHand(null);
                            } else if (hand == org.bukkit.inventory.EquipmentSlot.OFF_HAND) {
                                player.getInventory().setItemInOffHand(null);
                            }
                        } else {
                            item.setAmount(next);
                            if (hand == org.bukkit.inventory.EquipmentSlot.HAND) {
                                player.getInventory().setItemInMainHand(item);
                            } else if (hand == org.bukkit.inventory.EquipmentSlot.OFF_HAND) {
                                player.getInventory().setItemInOffHand(item);
                            }
                        }
                        return;
                    }

                    if (ev instanceof org.bukkit.event.player.PlayerItemConsumeEvent pce) {
                        ItemStack item = pce.getItem();
                        if (item == null) return;
                        int next = item.getAmount() - a;
                        item.setAmount(Math.max(0, next));
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action duplicateEntityDeathDrops() {
        return duplicateEntityDeathDrops(2);
    }

    public static Action duplicateEntityDeathDrops(int multiplier) {
        return ctx -> {
            EntityDeathEvent ede = ctx.nativeEvent(EntityDeathEvent.class);
            if (ede == null) return ActionResult.ALLOW;

            List<ItemStack> drops = ede.getDrops();
            if (drops == null || drops.isEmpty()) return ActionResult.ALLOW;

            int mult = Math.max(1, multiplier);
            if (mult <= 1) return ActionResult.ALLOW;

            // Clona la lista actual y la multiplica (por defecto x2, paridad con DoubleLootModifier)
            ItemStack[] snapshot = drops.toArray(new ItemStack[0]);
            for (int i = 1; i < mult; i++) {
                for (ItemStack drop : snapshot) {
                    if (drop == null) continue;
                    drops.add(drop.clone());
                }
            }
            return ActionResult.ALLOW;
        };
    }

    public static Action explosiveMiningBonusDrop(String locationKey) {
        return explosiveMiningBonusDrop(locationKey, 1);
    }

    public static Action explosiveMiningBonusDrop(String locationKey, int multiplier) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            BlockBreakEvent bbe = ctx.nativeEvent(BlockBreakEvent.class);
            if (bbe == null) return ActionResult.ALLOW;

            Player player = bbe.getPlayer();
            if (player == null) return ActionResult.ALLOW;

            Location loc = null;
            Object maybeLoc = ctx.getValue(locationKey);
            if (maybeLoc instanceof Location l) {
                loc = l;
            } else {
                try {
                    loc = bbe.getBlock() != null ? bbe.getBlock().getLocation() : null;
                } catch (Exception ignored) {
                    loc = null;
                }
            }
            if (loc == null || loc.getWorld() == null) return ActionResult.ALLOW;

            final Location finalLoc = loc;
            final int mult = Math.max(1, multiplier);

            final boolean tnt = ThreadLocalRandom.current().nextBoolean();
            final Material mat = tnt ? Material.TNT : Material.DIAMOND;

            runSync(plugin, () -> {
                finalLoc.getWorld().dropItemNaturally(finalLoc, new ItemStack(mat, mult));
                String msg;
                if (tnt) {
                    msg = mult == 1 ? "<red>¡Has encontrado TNT!" : "<red>¡Has encontrado " + mult + " TNT!";
                } else {
                    msg = mult == 1 ? "<aqua>¡Has encontrado un Diamante!" : "<aqua>¡Has encontrado " + mult + " Diamantes!";
                }
                player.sendMessage(MiniMessage.miniMessage().deserialize(msg));
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action silkTouchHands() {
        return silkTouchHands(true, Map.of());
    }

    public static Action silkTouchHands(boolean defaultEnchantments, Map<Enchantment, Integer> extraEnchantments) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            BlockBreakEvent bbe = ctx.nativeEvent(BlockBreakEvent.class);
            if (bbe == null) return ActionResult.ALLOW;
            if (bbe.isCancelled()) return ActionResult.ALLOW;

            Player player = bbe.getPlayer();
            if (player == null) return ActionResult.ALLOW;
            Block block = bbe.getBlock();
            if (block == null) return ActionResult.ALLOW;

            ItemStack hand;
            try {
                hand = player.getInventory().getItemInMainHand();
            } catch (Exception ignored) {
                hand = null;
            }
            if (hand != null && hand.containsEnchantment(Enchantment.SILK_TOUCH)) {
                return ActionResult.ALLOW;
            }

            Material blockType;
            try {
                blockType = block.getType();
            } catch (Exception ignored) {
                blockType = Material.AIR;
            }

            Material dummyType;
            if (hand != null && hand.getType() != null && hand.getType() != Material.AIR) {
                dummyType = hand.getType();
            } else if (blockType != null && blockType != Material.AIR) {
                if (Tag.MINEABLE_AXE.isTagged(blockType)) {
                    dummyType = Material.DIAMOND_AXE;
                } else if (Tag.MINEABLE_SHOVEL.isTagged(blockType)) {
                    dummyType = Material.DIAMOND_SHOVEL;
                } else if (Tag.MINEABLE_HOE.isTagged(blockType)) {
                    dummyType = Material.DIAMOND_HOE;
                } else {
                    dummyType = Material.DIAMOND_PICKAXE;
                }
            } else {
                dummyType = Material.DIAMOND_PICKAXE;
            }

            ItemStack dummyTool = new ItemStack(dummyType);
            Map<Enchantment, Integer> enchants = new HashMap<>();
            if (defaultEnchantments) {
                enchants.put(Enchantment.SILK_TOUCH, 1);
            }
            if (defaultEnchantments && hand != null && hand.getType() != null && hand.getType() != Material.AIR) {
                try {
                    Map<Enchantment, Integer> fromHand = hand.getEnchantments();
                    if (fromHand != null && !fromHand.isEmpty()) {
                        for (Map.Entry<Enchantment, Integer> e : fromHand.entrySet()) {
                            Enchantment ench = e.getKey();
                            Integer lvlObj = e.getValue();
                            if (ench == null || lvlObj == null) continue;

                            int lvl = Math.max(1, lvlObj);
                            Integer prev = enchants.get(ench);
                            if (prev == null || lvl > prev) {
                                enchants.put(ench, lvl);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            if (extraEnchantments != null && !extraEnchantments.isEmpty()) {
                for (Map.Entry<Enchantment, Integer> e : extraEnchantments.entrySet()) {
                    Enchantment ench = e.getKey();
                    Integer lvlObj = e.getValue();
                    if (ench == null || lvlObj == null) continue;
                    int lvl = Math.max(1, lvlObj);
                    Integer prev = enchants.get(ench);
                    if (prev == null || lvl > prev) {
                        enchants.put(ench, lvl);
                    }
                }
            }
            for (Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
                try {
                    dummyTool.addUnsafeEnchantment(e.getKey(), e.getValue());
                } catch (Exception ignored) {
                }
            }

            runSync(plugin, () -> {
                try {
                    bbe.setDropItems(false);
                    for (ItemStack drop : block.getDrops(dummyTool, player)) {
                        if (drop == null || drop.getType() == Material.AIR) continue;
                        block.getWorld().dropItemNaturally(block.getLocation(), drop);
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action defaultItemEnchantments(Map<Enchantment, Integer> extraEnchantments) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Object ev = ctx.nativeEvent();
            if (ev instanceof BlockBreakEvent bbe) {
                if (bbe.isCancelled()) return ActionResult.ALLOW;

                Player player = bbe.getPlayer();
                if (player == null) return ActionResult.ALLOW;
                Block block = bbe.getBlock();
                if (block == null) return ActionResult.ALLOW;

                ItemStack hand;
                try {
                    hand = player.getInventory().getItemInMainHand();
                } catch (Exception ignored) {
                    hand = null;
                }

                boolean wantsSilkTouch = extraEnchantments != null && extraEnchantments.containsKey(Enchantment.SILK_TOUCH);
                if (wantsSilkTouch && hand != null && hand.containsEnchantment(Enchantment.SILK_TOUCH)) {
                    return ActionResult.ALLOW;
                }

                Material blockType;
                try {
                    blockType = block.getType();
                } catch (Exception ignored) {
                    blockType = Material.AIR;
                }

                Material dummyType;
                if (hand != null && hand.getType() != null && hand.getType() != Material.AIR) {
                    dummyType = hand.getType();
                } else if (blockType != null && blockType != Material.AIR) {
                    if (Tag.MINEABLE_AXE.isTagged(blockType)) {
                        dummyType = Material.DIAMOND_AXE;
                    } else if (Tag.MINEABLE_SHOVEL.isTagged(blockType)) {
                        dummyType = Material.DIAMOND_SHOVEL;
                    } else if (Tag.MINEABLE_HOE.isTagged(blockType)) {
                        dummyType = Material.DIAMOND_HOE;
                    } else {
                        dummyType = Material.DIAMOND_PICKAXE;
                    }
                } else {
                    dummyType = Material.DIAMOND_PICKAXE;
                }

                ItemStack dummyTool = new ItemStack(dummyType);
                Map<Enchantment, Integer> enchants = mergeEnchantments(hand, extraEnchantments);

                for (Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
                    try {
                        dummyTool.addUnsafeEnchantment(e.getKey(), e.getValue());
                    } catch (Exception ignored) {
                    }
                }

                runSync(plugin, () -> {
                    try {
                        bbe.setDropItems(false);
                        for (ItemStack drop : block.getDrops(dummyTool, player)) {
                            if (drop == null || drop.getType() == Material.AIR) continue;
                            block.getWorld().dropItemNaturally(block.getLocation(), drop);
                        }
                    } catch (Exception ignored) {
                    }
                });

                return ActionResult.ALLOW;
            }

            if (ev instanceof EntityDamageByEntityEvent edbe) {
                if (edbe.isCancelled()) return ActionResult.ALLOW;

                if (!(edbe.getDamager() instanceof Player damager)) return ActionResult.ALLOW;
                if (!(edbe.getEntity() instanceof LivingEntity target)) return ActionResult.ALLOW;

                ItemStack hand;
                try {
                    hand = damager.getInventory().getItemInMainHand();
                } catch (Exception ignored) {
                    hand = null;
                }

                Map<Enchantment, Integer> enchants = mergeEnchantments(hand, extraEnchantments);

                double bonus = computeAttackDamageBonus(enchants, target);
                if (bonus != 0.0) {
                    double next = Math.max(0.0, edbe.getDamage() + bonus);
                    edbe.setDamage(next);
                }

                Integer fireLvl = enchants.get(Enchantment.FIRE_ASPECT);
                if (fireLvl != null && fireLvl > 0) {
                    int ticks = Math.max(0, 80 * fireLvl);
                    runSync(plugin, () -> {
                        try {
                            target.setFireTicks(Math.max(target.getFireTicks(), ticks));
                        } catch (Exception ignored) {
                        }
                    });
                }

                Integer kbLvl = enchants.get(Enchantment.KNOCKBACK);
                if (kbLvl != null && kbLvl > 0) {
                    int k = Math.max(1, kbLvl);
                    runSync(plugin, () -> {
                        try {
                            Vector dir = target.getLocation().toVector().subtract(damager.getLocation().toVector());
                            if (dir.lengthSquared() > 0.0001) {
                                dir.normalize();
                                double strength = 0.35 * k;
                                target.setVelocity(target.getVelocity().add(dir.multiply(strength)));
                            }
                        } catch (Exception ignored) {
                        }
                    });
                }

                return ActionResult.ALLOW;
            }

            return ActionResult.ALLOW;
        };
    }

    private static Map<Enchantment, Integer> mergeEnchantments(ItemStack fromHand, Map<Enchantment, Integer> extraEnchantments) {
        Map<Enchantment, Integer> enchants = new HashMap<>();
        if (fromHand != null && fromHand.getType() != null && fromHand.getType() != Material.AIR) {
            try {
                Map<Enchantment, Integer> from = fromHand.getEnchantments();
                if (from != null && !from.isEmpty()) {
                    for (Map.Entry<Enchantment, Integer> e : from.entrySet()) {
                        Enchantment ench = e.getKey();
                        Integer lvlObj = e.getValue();
                        if (ench == null || lvlObj == null) continue;
                        int lvl = Math.max(1, lvlObj);
                        Integer prev = enchants.get(ench);
                        if (prev == null || lvl > prev) {
                            enchants.put(ench, lvl);
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
        if (extraEnchantments != null && !extraEnchantments.isEmpty()) {
            for (Map.Entry<Enchantment, Integer> e : extraEnchantments.entrySet()) {
                Enchantment ench = e.getKey();
                Integer lvlObj = e.getValue();
                if (ench == null || lvlObj == null) continue;
                int lvl = Math.max(1, lvlObj);
                Integer prev = enchants.get(ench);
                if (prev == null || lvl > prev) {
                    enchants.put(ench, lvl);
                }
            }
        }
        return enchants.isEmpty() ? Map.of() : enchants;
    }

    private static double computeAttackDamageBonus(Map<Enchantment, Integer> enchants, LivingEntity target) {
        if (enchants == null || enchants.isEmpty() || target == null) return 0.0;

        int sharp = enchants.getOrDefault(Enchantment.SHARPNESS, 0);
        int smite = enchants.getOrDefault(Enchantment.SMITE, 0);
        int bane = enchants.getOrDefault(Enchantment.BANE_OF_ARTHROPODS, 0);

        double best = 0.0;
        if (sharp > 0) {
            // Java 1.9+: 0.5*level + 0.5
            best = Math.max(best, 0.5 * sharp + 0.5);
        }

        EntityType type = null;
        try {
            type = target.getType();
        } catch (Exception ignored) {
            type = null;
        }

        if (smite > 0 && isUndead(type)) {
            best = Math.max(best, 2.5 * smite);
        }
        if (bane > 0 && isArthropod(type)) {
            best = Math.max(best, 2.5 * bane);
        }

        return best;
    }

    private static boolean isUndead(EntityType type) {
        if (type == null) return false;
        return switch (type) {
            case ZOMBIE, DROWNED, HUSK, ZOMBIE_VILLAGER, ZOMBIFIED_PIGLIN, SKELETON, STRAY, WITHER_SKELETON,
                 WITHER, PHANTOM, ZOGLIN, SKELETON_HORSE -> true;
            default -> false;
        };
    }

    private static boolean isArthropod(EntityType type) {
        if (type == null) return false;
        return switch (type) {
            case SPIDER, CAVE_SPIDER, SILVERFISH, ENDERMITE, BEE -> true;
            default -> false;
        };
    }

    public static Action discordWebhook(String url, String content, String username, String avatarUrl) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();

            String resolvedUrl = PlaceholderUtil.resolvePlaceholders(plugin, player, url);
            String resolvedContent = PlaceholderUtil.resolvePlaceholders(plugin, player, content);
            String resolvedUsername = username != null ? PlaceholderUtil.resolvePlaceholders(plugin, player, username) : null;
            String resolvedAvatar = avatarUrl != null ? PlaceholderUtil.resolvePlaceholders(plugin, player, avatarUrl) : null;

            if (resolvedUrl == null || resolvedUrl.isBlank() || resolvedContent == null || resolvedContent.isBlank()) {
                return ActionResult.ALLOW;
            }

            String payload = buildDiscordWebhookJson(resolvedContent, resolvedUsername, resolvedAvatar);

            // fire-and-forget async; no bloquear tick
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(5))
                        .build();

                    HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(resolvedUrl.trim()))
                        .timeout(Duration.ofSeconds(10))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                        .build();

                    client.send(req, HttpResponse.BodyHandlers.discarding());
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    private static String buildDiscordWebhookJson(String content, String username, String avatarUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("\"content\":\"").append(jsonEscape(content)).append('"');
        if (username != null && !username.isBlank()) {
            sb.append(",\"username\":\"").append(jsonEscape(username)).append('"');
        }
        if (avatarUrl != null && !avatarUrl.isBlank()) {
            sb.append(",\"avatar_url\":\"").append(jsonEscape(avatarUrl)).append('"');
        }
        sb.append('}');
        return sb.toString();
    }

    private static String jsonEscape(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> {
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
                }
            }
        }
        return out.toString();
    }

    public static Action growAgeableToMax() {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            PlayerInteractEvent pie = ctx.nativeEvent(PlayerInteractEvent.class);
            if (pie == null) return ActionResult.ALLOW;

            if (pie.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return ActionResult.ALLOW;
            if (pie.getClickedBlock() == null) return ActionResult.ALLOW;

            Block block = pie.getClickedBlock();
            if (block == null) return ActionResult.ALLOW;

            if (!(block.getBlockData() instanceof Ageable age)) return ActionResult.ALLOW;
            if (age.getAge() >= age.getMaximumAge()) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                try {
                    age.setAge(age.getMaximumAge());
                    block.setBlockData(age);
                    block.getWorld().playEffect(block.getLocation(), Effect.VILLAGER_PLANT_GROW, 0);
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action multiplyEventExp(int multiplier) {
        return ctx -> {
            PlayerExpChangeEvent pece = ctx.nativeEvent(PlayerExpChangeEvent.class);
            if (pece == null) return ActionResult.ALLOW;
            int mult = Math.max(1, multiplier);
            int next = Math.max(0, pece.getAmount() * mult);
            pece.setAmount(next);
            return ActionResult.ALLOW;
        };
    }

    public static Action legendaryFisher(int minWaitTicks, int maxWaitTicks, boolean applyLure) {
        return ctx -> {
            PlayerFishEvent pfe = ctx.nativeEvent(PlayerFishEvent.class);
            if (pfe == null) return ActionResult.ALLOW;
            if (pfe.getState() != PlayerFishEvent.State.FISHING) return ActionResult.ALLOW;

            if (!(pfe.getHook() instanceof org.bukkit.entity.FishHook hook)) return ActionResult.ALLOW;
            try {
                hook.setMinWaitTime(Math.max(0, minWaitTicks));
                hook.setMaxWaitTime(Math.max(0, maxWaitTicks));
                hook.setApplyLure(applyLure);
            } catch (Exception ignored) {
            }
            return ActionResult.ALLOW;
        };
    }

    public static Action restoreAnvilOnClose() {
        return ctx -> {
            InventoryCloseEvent ice = ctx.nativeEvent(InventoryCloseEvent.class);
            if (ice == null) return ActionResult.ALLOW;

            if (ice.getInventory() == null || ice.getInventory().getType() != org.bukkit.event.inventory.InventoryType.ANVIL) {
                return ActionResult.ALLOW;
            }
            Location loc = ice.getInventory().getLocation();
            if (loc == null || loc.getWorld() == null) return ActionResult.ALLOW;

            Block block = loc.getBlock();
            if (block == null) return ActionResult.ALLOW;
            Material t = block.getType();
            if (t == Material.CHIPPED_ANVIL || t == Material.DAMAGED_ANVIL) {
                block.setType(Material.ANVIL);
            }
            return ActionResult.ALLOW;
        };
    }

    public static Action reflectDamageIfBlocking(double multiplier) {
        return reflectDamageIfBlocking(multiplier, false);
    }

    public static Action reflectDamageIfBlocking(double multiplier, boolean resetNoDamageTicks) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            EntityDamageByEntityEvent edbe = ctx.nativeEvent(EntityDamageByEntityEvent.class);
            if (edbe == null) return ActionResult.ALLOW;
            if (!(edbe.getEntity() instanceof Player player)) return ActionResult.ALLOW;
            if (!(edbe.getDamager() instanceof LivingEntity attacker)) return ActionResult.ALLOW;
            if (!player.isBlocking()) return ActionResult.ALLOW;

            double dmg = edbe.getDamage();
            double m = Math.max(0.0, multiplier);
            if (dmg <= 0.0 || m <= 0.0) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                try {
                    attacker.damage(dmg * m, player);
                    if (resetNoDamageTicks) {
                        attacker.setNoDamageTicks(0);
                    }
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action runLater(int delayTicks, List<Action> actions) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            int delay = Math.max(0, delayTicks);
            List<Action> safe = actions != null ? actions : List.of();
            if (safe.isEmpty()) return ActionResult.ALLOW;

            Player player = ctx.player();
            String subjectId = ctx.subjectId();
            ScriptPhase phase = ctx.phase();

            Map<String, Object> vars = ctx.variables() != null ? new java.util.HashMap<>(ctx.variables()) : new java.util.HashMap<>();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                try {
                    ScriptContext child = new ScriptContext(plugin, player, subjectId, phase, vars);
                    ScriptEngine.runAllWithResult(child, safe);
                } catch (Exception ignored) {
                }
            }, delay);

            return ActionResult.ALLOW;
        };
    }

    public static Action damagePlayerOrKill(double damage, double killIfHealthAtMost) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            double dmg = Math.max(0.0, damage);
            double th = Math.max(0.0, killIfHealthAtMost);
            if (dmg <= 0.0) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                try {
                    double hp = player.getHealth();
                    if (hp > th) {
                        player.damage(dmg);
                    } else {
                        player.damage(1000.0);
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action fortuneTouchBestDrop(int attempts, int fortuneLevel) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            BlockBreakEvent bbe = ctx.nativeEvent(BlockBreakEvent.class);
            if (bbe == null) return ActionResult.ALLOW;
            if (bbe.isCancelled()) return ActionResult.ALLOW;

            Block block = bbe.getBlock();
            Player player = bbe.getPlayer();
            if (block == null || player == null) return ActionResult.ALLOW;

            String typeName = null;
            try {
                typeName = block.getType() != null ? block.getType().name() : null;
            } catch (Exception ignored) {
                typeName = null;
            }
            if (typeName == null) return ActionResult.ALLOW;
            if (!(typeName.endsWith("_ORE") || typeName.equals("ANCIENT_DEBRIS"))) return ActionResult.ALLOW;

            try {
                ItemStack hand = player.getInventory().getItemInMainHand();
                if (hand != null && hand.containsEnchantment(Enchantment.SILK_TOUCH)) {
                    return ActionResult.ALLOW;
                }
            } catch (Exception ignored) {
            }

            ItemStack tool = new ItemStack(Material.DIAMOND_PICKAXE);
            if (fortuneLevel > 0) {
                try {
                    tool.addEnchantment(Enchantment.FORTUNE, fortuneLevel);
                } catch (Exception ignored) {
                }
            }

            int tries = Math.max(1, attempts);
            Collection<ItemStack> bestDrops = null;
            int bestCount = -1;

            for (int i = 0; i < tries; i++) {
                Collection<ItemStack> drops;
                try {
                    drops = block.getDrops(tool);
                } catch (Exception ignored) {
                    drops = null;
                }
                if (drops == null || drops.isEmpty()) continue;
                int count = 0;
                for (ItemStack it : drops) {
                    if (it == null) continue;
                    count += it.getAmount();
                }
                if (count > bestCount) {
                    bestCount = count;
                    bestDrops = drops;
                }
            }

            if (bestDrops == null || bestDrops.isEmpty()) return ActionResult.ALLOW;

            Collection<ItemStack> bestFinal = bestDrops;
            runSync(plugin, () -> {
                try {
                    bbe.setDropItems(false);
                    for (ItemStack drop : bestFinal) {
                        if (drop == null || drop.getType() == Material.AIR) continue;
                        block.getWorld().dropItemNaturally(block.getLocation(), drop);
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action lumberjackBreakTree(int limit) {
        return ctx -> {
            BlockBreakEvent bbe = ctx.nativeEvent(BlockBreakEvent.class);
            if (bbe == null) return ActionResult.ALLOW;
            if (bbe.isCancelled()) return ActionResult.ALLOW;

            Block start = bbe.getBlock();
            Player player = bbe.getPlayer();
            if (start == null || player == null) return ActionResult.ALLOW;

            if (player.isSneaking()) return ActionResult.ALLOW;
            if (!isEligibleLog(start.getType())) return ActionResult.ALLOW;

            int max = Math.max(1, limit);

            Queue<Block> queue = new LinkedList<>();
            Set<Block> visited = new HashSet<>();
            queue.add(start);
            visited.add(start);

            int count = 0;
            while (!queue.isEmpty() && count < max) {
                Block current = queue.poll();
                if (current == null) continue;

                try {
                    current.breakNaturally();
                } catch (Exception ignored) {
                }
                count++;

                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            if (x == 0 && y == 0 && z == 0) continue;
                            Block rel = current.getRelative(x, y, z);
                            if (rel == null) continue;
                            if (visited.contains(rel)) continue;
                            if (!isEligibleLog(rel.getType())) continue;
                            visited.add(rel);
                            queue.add(rel);
                        }
                    }
                }
            }

            return ActionResult.ALLOW;
        };
    }

    private static boolean isEligibleLog(Material type) {
        if (type == null) return false;
        String name = type.name();
        if (name.startsWith("STRIPPED_")) return false;
        return name.endsWith("_LOG");
    }



    public static Action pullNearbyItems(double radius, double speed, boolean ignorePickupDelay, boolean pullXp, boolean pullItems) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            double r = Math.max(0.0, radius);
            double s = Math.max(0.0, speed);
            if (r <= 0.0 || s <= 0.0) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                for (Entity e : player.getNearbyEntities(r, r, r)) {
                    if (pullItems && e instanceof Item item) {
                        if (!ignorePickupDelay && item.getPickupDelay() > 0) continue;
                        Vector direction = player.getLocation().toVector().subtract(item.getLocation().toVector());
                        if (direction.lengthSquared() < 0.0001) continue;
                        direction.normalize();
                        item.setVelocity(direction.multiply(s));
                    } else if (pullXp && e instanceof org.bukkit.entity.ExperienceOrb orb) {
                        Vector direction = player.getLocation().toVector().subtract(orb.getLocation().toVector());
                        if (direction.lengthSquared() < 0.0001) continue;
                        direction.normalize();
                        orb.setVelocity(direction.multiply(s));
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action fairTradePiglinBarter() {
        return ctx -> {
            PiglinBarterEvent pbe = ctx.nativeEvent(PiglinBarterEvent.class);
            if (pbe == null) return ActionResult.ALLOW;

            List<ItemStack> outcome = pbe.getOutcome();
            if (outcome == null) return ActionResult.ALLOW;

            java.util.List<ItemStack> newOutcome = new java.util.ArrayList<>();
            for (ItemStack item : outcome) {
                if (item == null || item.getType() == Material.AIR) continue;
                if (isBadBarterItem(item.getType())) {
                    newOutcome.add(randomGoodBarterItem());
                } else {
                    newOutcome.add(item);
                }
            }

            if (newOutcome.isEmpty()) {
                newOutcome.add(randomGoodBarterItem());
            }

            outcome.clear();
            outcome.addAll(newOutcome);
            return ActionResult.ALLOW;
        };
    }

    private static boolean isBadBarterItem(Material type) {
        return type == Material.GRAVEL
            || type == Material.BLACKSTONE
            || type == Material.SOUL_SAND
            || type == Material.NETHER_BRICK
            || type == Material.ROTTEN_FLESH;
    }

    private static ItemStack randomGoodBarterItem() {
        int r = ThreadLocalRandom.current().nextInt(100);
        if (r < 20) return new ItemStack(Material.ENDER_PEARL, 2 + ThreadLocalRandom.current().nextInt(3));
        if (r < 40) return new ItemStack(Material.IRON_NUGGET, 10 + ThreadLocalRandom.current().nextInt(10));
        if (r < 60) return new ItemStack(Material.OBSIDIAN, 1);
        if (r < 80) return new ItemStack(Material.FIRE_CHARGE, 1 + ThreadLocalRandom.current().nextInt(3));
        return new ItemStack(Material.LEATHER, 2 + ThreadLocalRandom.current().nextInt(4));
    }

    public static Action mirrorWorldMakeEndermanPassive() {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            Enderman enderman = ctx.subjectOrEventEntity(Enderman.class);
            if (enderman == null) return ActionResult.ALLOW;

            final Enderman endermanFinal = enderman;
            runSync(plugin, () -> mirrorWorldMakeEndermanPassiveInternal(endermanFinal));
            return ActionResult.ALLOW;
        };
    }

    private static void mirrorWorldMakeEndermanPassiveInternal(Enderman enderman) {
        if (enderman == null) return;
        if (enderman.getScoreboardTags().contains("mirror_world_passive")) return;
        try {
            net.minecraft.world.entity.monster.EnderMan nms = (net.minecraft.world.entity.monster.EnderMan) ((CraftEntity) enderman).getHandle();
            nms.targetSelector.getAvailableGoals().clear();
            enderman.addScoreboardTag("mirror_world_passive");
        } catch (Exception ignored) {
        }
    }

    public static Action mirrorWorldMakePigAggressive() {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            Pig pig = ctx.subjectOrEventEntity(Pig.class);
            if (pig == null) return ActionResult.ALLOW;

            final Pig pigFinal = pig;
            runSync(plugin, () -> mirrorWorldMakePigAggressiveInternal(pigFinal));
            return ActionResult.ALLOW;
        };
    }

    private static void mirrorWorldMakePigAggressiveInternal(Pig pig) {
        if (pig == null) return;
        if (pig.getScoreboardTags().contains("mirror_world_aggressive")) return;
        try {
            net.minecraft.world.entity.animal.Pig nmsPig = (net.minecraft.world.entity.animal.Pig) ((CraftEntity) pig).getHandle();
            nmsPig.goalSelector.addGoal(1, new MirrorWorldPigAttackGoal(nmsPig, 1.2D, false));
            nmsPig.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(
                nmsPig,
                net.minecraft.world.entity.player.Player.class,
                true
            ));
            pig.addScoreboardTag("mirror_world_aggressive");
        } catch (Exception ignored) {
        }
    }

    private static final class MirrorWorldPigAttackGoal extends net.minecraft.world.entity.ai.goal.MeleeAttackGoal {
        MirrorWorldPigAttackGoal(net.minecraft.world.entity.PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
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

    public static Action lavaFlowLikeWater() {
        return ctx -> {
            BlockFromToEvent bf = ctx.nativeEvent(BlockFromToEvent.class);
            if (bf == null) return ActionResult.ALLOW;

            Block source = bf.getBlock();
            if (source == null || source.getType() != Material.LAVA) return ActionResult.ALLOW;
            if (!(source.getBlockData() instanceof Levelled sourceData)) return ActionResult.ALLOW;

            int currentLevel = sourceData.getLevel();
            if (currentLevel >= 7) return ActionResult.ALLOW;

            Block toBlock = bf.getToBlock();
            if (toBlock == null) return ActionResult.ALLOW;
            if (!(toBlock.getType() == Material.AIR || !toBlock.getType().isSolid())) return ActionResult.ALLOW;

            bf.setCancelled(true);
            try {
                toBlock.setType(Material.LAVA, false);
                if (toBlock.getBlockData() instanceof Levelled newData) {
                    newData.setLevel(currentLevel + 1);
                    toBlock.setBlockData(newData);
                }
            } catch (Exception ignored) {
            }
            return ActionResult.ALLOW;
        };
    }

    public static Action persistentShadowStart(long intervalMs, int checkPeriodTicks) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                var service = PERSISTENT_SHADOW.computeIfAbsent(plugin, p -> new net.rollanddeath.smp.core.modifiers.scripted.PersistentShadowService(p));
                service.start(intervalMs, checkPeriodTicks);
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action persistentShadowStop() {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                var service = PERSISTENT_SHADOW.get(plugin);
                if (service != null) {
                    service.stop();
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action aggroNearbyCreatures(int radiusXZ, int radiusY, boolean onlyIfNoTarget) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            int rxz = Math.max(0, radiusXZ);
            int ry = Math.max(0, radiusY);

            runSync(plugin, () -> {
                try {
                    for (Entity entity : player.getNearbyEntities(rxz, ry, rxz)) {
                        if (!(entity instanceof org.bukkit.entity.Creature creature)) continue;
                        if (onlyIfNoTarget && creature.getTarget() != null) continue;
                        creature.setTarget(player);
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action explodeNearbyEntities(List<String> entityTypes, double radius, float power, boolean setFire, boolean breakBlocks) {
        final java.util.Set<String> types = (entityTypes == null || entityTypes.isEmpty())
            ? java.util.Set.of()
            : entityTypes.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(s -> s.trim().toUpperCase(Locale.ROOT))
                .collect(java.util.stream.Collectors.toSet());

        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null || player.getWorld() == null) return ActionResult.ALLOW;
            if (types.isEmpty()) return ActionResult.ALLOW;

            double r = Math.max(0.0, radius);
            float pwr = Math.max(0.0f, power);

            runSync(plugin, () -> {
                try {
                    for (Entity entity : player.getNearbyEntities(r, r, r)) {
                        if (entity == null) continue;
                        if (!types.contains(entity.getType().name())) continue;
                        try {
                            entity.getWorld().createExplosion(entity.getLocation(), pwr, setFire, breakBlocks);
                        } catch (Exception ignored) {
                        }
                        try {
                            entity.remove();
                        } catch (Exception ignored) {
                        }
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action rotateOnlinePlayerPositions(String nextRunKey, long intervalMs, boolean shuffle, String message, String color) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null || nextRunKey == null || nextRunKey.isBlank()) return ActionResult.ALLOW;

            long now = System.currentTimeMillis();
            long next = -1L;
            try {
                Object v = ctx.getValue(nextRunKey);
                if (v instanceof Number n) next = n.longValue();
                else if (v instanceof String s) next = Long.parseLong(s.trim());
            } catch (Exception ignored) {
                next = -1L;
            }

            if (next > 0L && now < next) return ActionResult.ALLOW;

            // Importante: guardamos el próximo timestamp ANTES de ejecutar la rotación.
            // Como esta acción se ejecuta secuencialmente, evita rotaciones duplicadas si se llama desde player_tick.
            ctx.setGenericVarCompat(nextRunKey, now + Math.max(0L, intervalMs));

            final NamedTextColor cFinal = java.util.Optional.ofNullable(Resolvers.color(ctx, color)).orElse(NamedTextColor.YELLOW);
            runSync(plugin, () -> {
                List<Player> players = new java.util.ArrayList<>(Bukkit.getOnlinePlayers());
                if (players.size() < 2) return;

                if (shuffle) {
                    java.util.Collections.shuffle(players);
                }

                List<Location> locations = new java.util.ArrayList<>(players.size());
                for (Player p : players) {
                    try {
                        locations.add(p.getLocation());
                    } catch (Exception ignored) {
                        locations.add(null);
                    }
                }

                for (int i = 0; i < players.size(); i++) {
                    Player p = players.get(i);
                    Location loc = locations.get((i + 1) % players.size());
                    if (p == null || loc == null) continue;
                    try {
                        p.teleport(loc);
                    } catch (Exception ignored) {
                    }
                    if (message != null && !message.isBlank()) {
                        try {
                            p.sendMessage(Component.text(message, cFinal));
                        } catch (Exception ignored) {
                        }
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action ensurePlayerHasItem(String materialName, int amount) {
        final org.bukkit.Material matFinal = Resolvers.resolveMaterial(materialName);
        final int amtFinal = Math.max(1, amount);

        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null || matFinal == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                try {
                    if (player.getInventory().contains(matFinal)) return;
                    player.getInventory().addItem(new ItemStack(matFinal, amtFinal));
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action setPlayerInventoryHideTooltip(boolean hide) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                try {
                    for (ItemStack item : player.getInventory().getContents()) {
                        applyHideTooltip(item, hide);
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action setHideTooltipFromEventItems(boolean hide) {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            Object ev = ctx.nativeEvent();
            runSync(plugin, () -> {
                if (ev instanceof InventoryClickEvent ice) {
                    applyHideTooltip(ice.getCurrentItem(), hide);
                    applyHideTooltip(ice.getCursor(), hide);
                }
                if (ev instanceof EntityPickupItemEvent epi) {
                    try {
                        if (epi.getItem() != null) {
                            applyHideTooltip(epi.getItem().getItemStack(), hide);
                        }
                    } catch (Exception ignored) {
                    }
                }
                if (ev instanceof org.bukkit.event.inventory.PrepareItemCraftEvent pic) {
                    try {
                        applyHideTooltip(pic.getInventory().getResult(), hide);
                    } catch (Exception ignored) {
                    }
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action bowRefundConsumable() {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            org.bukkit.event.entity.EntityShootBowEvent bow = ctx.nativeEvent(org.bukkit.event.entity.EntityShootBowEvent.class);
            if (bow == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                try {
                    bow.setConsumeItem(false);
                } catch (Exception ignored) {
                }

                try {
                    ItemStack shot = bow.getConsumable();
                    ItemStack refund;
                    if (shot != null) {
                        refund = shot.clone();
                        refund.setAmount(1);
                    } else {
                        refund = new ItemStack(org.bukkit.Material.ARROW, 1);
                    }
                    player.getInventory().addItem(refund);
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action setPlayerCooldown(String materialName, int ticks) {
        final org.bukkit.Material matFinal = Resolvers.resolveMaterial(materialName);
        final int ticksFinal = Math.max(0, ticks);

        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null || matFinal == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                try {
                    player.setCooldown(matFinal, ticksFinal);
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action setPlayerVelocityForward(double multiplier, Double yOverride) {
        final double multFinal = multiplier;
        final Double yFinal = yOverride;

        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                try {
                    Vector dir = player.getEyeLocation().getDirection();
                    if (dir.lengthSquared() > 1.0e-6) {
                        dir.normalize();
                    }
                    Vector v = dir.multiply(multFinal);
                    if (yFinal != null) {
                        v.setY(v.getY() + yFinal);
                    }
                    player.setVelocity(v);
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action setPlayerRiptiding(boolean riptiding) {
        final boolean value = riptiding;
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            Player player = ctx.player();
            if (plugin == null || player == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                try {
                    player.setRiptiding(value);
                } catch (Exception ignored) {
                }
            });
            return ActionResult.ALLOW;
        };
    }

    public static Action projectileAddRandomSpread(double spread) {
        final double spreadFinal = Math.max(0.0, spread);
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;
            org.bukkit.event.entity.EntityShootBowEvent bow = ctx.nativeEvent(org.bukkit.event.entity.EntityShootBowEvent.class);
            if (bow == null) return ActionResult.ALLOW;
            if (!(bow.getProjectile() instanceof Projectile proj)) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                try {
                    Vector velocity = proj.getVelocity();
                    double dx = (ThreadLocalRandom.current().nextDouble() - 0.5) * spreadFinal;
                    double dy = (ThreadLocalRandom.current().nextDouble() - 0.5) * spreadFinal;
                    double dz = (ThreadLocalRandom.current().nextDouble() - 0.5) * spreadFinal;
                    velocity.add(new Vector(dx, dy, dz));
                    proj.setVelocity(velocity);
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action strikeLightningAtProjectileHit() {
        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;
            org.bukkit.event.entity.ProjectileHitEvent phe = ctx.nativeEvent(org.bukkit.event.entity.ProjectileHitEvent.class);
            if (phe == null) return ActionResult.ALLOW;

            runSync(plugin, () -> {
                try {
                    Location loc = null;
                    if (phe.getHitEntity() != null) {
                        loc = phe.getHitEntity().getLocation();
                    } else if (phe.getHitBlock() != null) {
                        loc = phe.getHitBlock().getLocation();
                    }
                    if (loc == null || loc.getWorld() == null) return;
                    loc.getWorld().strikeLightning(loc);
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    public static Action setVarTargetBlockLocation(String storeKey, int range, double fallbackDistance, double yOffset, boolean centerBlock) {
        final String keyFinal = storeKey;
        final int rangeFinal = Math.max(1, range);
        final double fallbackFinal = Math.max(0.0, fallbackDistance);
        final double yFinal = yOffset;
        final boolean centerFinal = centerBlock;

        return ctx -> {
            Player player = ctx.player();
            if (player == null) return ActionResult.ALLOW;

            Location out;
            try {
                org.bukkit.block.Block target;
                try {
                    target = player.getTargetBlockExact(rangeFinal);
                } catch (NoSuchMethodError ignored) {
                    target = player.getTargetBlock(null, rangeFinal);
                }

                if (target != null && target.getType() != null && target.getType() != org.bukkit.Material.AIR) {
                    out = target.getLocation().clone();
                    if (centerFinal) {
                        out.add(0.5, 0.5, 0.5);
                    }
                } else {
                    out = player.getEyeLocation().clone().add(player.getEyeLocation().getDirection().multiply(fallbackFinal));
                }

                out.add(0, yFinal, 0);
            } catch (Exception ignored) {
                out = player.getEyeLocation();
            }

            ctx.setGenericVarCompat(keyFinal, out);

            return ActionResult.ALLOW;
        };
    }

    public static Action runRepeating(int intervalTicks, int totalTicks, List<Action> actions) {
        final int intervalFinal = Math.max(1, intervalTicks);
        final int totalFinal = Math.max(1, totalTicks);
        final List<Action> safe = actions != null ? actions : List.of();

        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;
            if (safe.isEmpty()) return ActionResult.ALLOW;

            Map<String, Object> base = ctx.variables() != null ? new HashMap<>(ctx.variables()) : new HashMap<>();
            Player player = ctx.player();
            String subjectId = ctx.subjectId();
            ScriptPhase phase = ctx.phase();

            // CRÍTICO: Preservar el genericRoot del scope EVENT original (donde está EVENT.custom.*)
            // Esto captura datos guardados con set_var como EVENT.custom.void_center
            Map<String, Object> eventGenericTemp;
            try {
                var eventScope = ctx.scopes().get(net.rollanddeath.smp.core.scripting.scope.ScopeId.EVENT);
                if (eventScope != null && eventScope.storage() != null) {
                    // Clonar el genericRoot para evitar modificaciones concurrentes
                    eventGenericTemp = new HashMap<>(eventScope.storage().genericRoot());
                } else {
                    eventGenericTemp = new HashMap<>();
                }
            } catch (Exception e) {
                eventGenericTemp = new HashMap<>();
            }
            final Map<String, Object> originalEventGeneric = eventGenericTemp;

            // Preservar el evento original para metadata
            final Object originalEvent = base.get("__event");

            final org.bukkit.scheduler.BukkitTask[] task = new org.bukkit.scheduler.BukkitTask[1];
            task[0] = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                Integer ageObj = base.get("__repeat_age_ticks") instanceof Number n ? n.intValue() : null;
                int age = ageObj != null ? ageObj : 0;
                age += intervalFinal;
                base.put("__repeat_age_ticks", age);

                if (age > totalFinal) {
                    try {
                        task[0].cancel();
                    } catch (Exception ignored) {
                    }
                    return;
                }

                Map<String, Object> vars = new HashMap<>(base);

                // Crear evento sintético con metadata del loop + datos del evento original
                Map<String, Object> ev = new HashMap<>();
                ev.put("type", "repeat_tick");
                ev.put("__native", originalEvent);
                ev.put("original", originalEvent);
                ev.put("repeatAgeTicks", age);
                ev.put("repeatTotalTicks", totalFinal);
                ev.put("repeatProgress", totalFinal > 0 ? (age / (double) totalFinal) : 1.0);
                
                // CRÍTICO: Copiar el genericRoot del evento original dentro de "custom"
                // para que el EventProvider lo reconozca y lo copie al nuevo genericRoot.
                // El genericRoot almacena datos con keyPath sin el prefijo "custom",
                // pero el EventProvider necesita que el Map tenga la clave "custom" para detectarlo.
                if (!originalEventGeneric.isEmpty()) {
                    ev.put("custom", new HashMap<>(originalEventGeneric));
                }
                
                vars.put("__event", ev);

                ScriptContext child = new ScriptContext(plugin, player, subjectId, phase, vars);
                try {
                    ScriptEngine.runAllWithResult(child, safe);
                } catch (Exception ignored) {
                }
            }, 0L, intervalFinal);

            return ActionResult.ALLOW;
        };
    }

    public static Action gravityPullNearLocation(Object centerSpec, Double radius, Double strength, String strengthKey, boolean includePlayers, boolean includeMobs, boolean excludeCaster, boolean excludeSpectators, boolean scaleByDistance, Double maxForce, List<Action> atTargetActions) {
        final double rFinal = Math.max(0.1, radius != null ? radius : 5.0);
        final Double sFinal = strength;
        final String sKeyFinal = strengthKey;
        final boolean incPlayers = includePlayers;
        final boolean incMobs = includeMobs;
        final boolean excCaster = excludeCaster;
        final boolean excSpecs = excludeSpectators;
        final boolean scale = scaleByDistance;
        final Object centerFinal = centerSpec;
        final Double maxFinal = maxForce;
        final List<Action> atTargetFinal = atTargetActions != null ? atTargetActions : List.of();

        return ctx -> {
            RollAndDeathSMP plugin = ctx.plugin();
            if (plugin == null) return ActionResult.ALLOW;

            Location center = Resolvers.Resolvers.location(ctx, centerFinal);
            if (center == null || center.getWorld() == null) return ActionResult.ALLOW;

            Object casterObj = ctx.getValue("SUBJECT");
            Entity caster = casterObj instanceof Entity e ? e : null;
            Player player = ctx.player();
            String subjectId = ctx.subjectId();
            ScriptPhase phase = ctx.phase();

            double baseStrength = 0.0;
            if (sFinal != null) baseStrength = sFinal;
            if (sKeyFinal != null) {
                Double d = Resolvers.resolveDouble(ctx.getValue(sKeyFinal));
                if (d != null) baseStrength = d;
            }
            final double strengthVal = baseStrength;

            runSync(plugin, () -> {
                try {
                    Collection<Entity> nearby = center.getWorld().getNearbyEntities(center, rFinal, rFinal, rFinal);
                    for (Entity e : nearby) {
                        if (e == null || !e.isValid() || e.isDead()) continue;
                        if (excCaster && caster != null && caster.getUniqueId().equals(e.getUniqueId())) continue;

                        if (e instanceof Player p) {
                            if (!incPlayers) continue;
                            if (excSpecs && p.getGameMode() == org.bukkit.GameMode.SPECTATOR) continue;
                        } else {
                            if (!incMobs) continue;
                            if (!(e instanceof LivingEntity)) continue;
                        }

                        Vector dir = center.toVector().subtract(e.getLocation().toVector());
                        double distSq = dir.lengthSquared();
                        if (distSq < 1.0e-6) continue;
                        double dist = Math.sqrt(distSq);
                        dir.multiply(1.0 / dist);

                        double factor = strengthVal;
                        if (scale) {
                            factor *= Math.max(0.0, 1.0 - (dist / rFinal));
                        }
                        if (maxFinal != null && factor > maxFinal) {
                            factor = maxFinal;
                        }
                        if (factor == 0.0) continue;

                        try {
                            e.setVelocity(e.getVelocity().add(dir.multiply(factor)));
                        } catch (Exception ignored) {
                        }

                        // Ejecutar acciones at_target en la entidad atraída
                        if (!atTargetFinal.isEmpty()) {
                            try {
                                Map<String, Object> vars = ctx.variables() != null ? new HashMap<>(ctx.variables()) : new HashMap<>();
                                vars.put("__subject", e);
                                vars.put("__target", e);
                                ScriptContext targetCtx = new ScriptContext(plugin, player, e.getUniqueId().toString(), phase, vars);
                                ScriptEngine.runAllWithResult(targetCtx, atTargetFinal);
                            } catch (Exception ignored) {
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            });

            return ActionResult.ALLOW;
        };
    }

    private static void applyHideTooltip(ItemStack item, boolean hide) {
        if (item == null || item.getType().isAir()) return;
        try {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;
            meta.setHideTooltip(hide);
            item.setItemMeta(meta);
        } catch (NoSuchMethodError ignored) {
            // Paper/Spigot viejo
        } catch (Exception ignored) {
        }
    }

    private static void runSync(RollAndDeathSMP plugin, Runnable action) {
        if (plugin == null || action == null) return;
        if (Bukkit.isPrimaryThread()) {
            action.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, action);
        }
    }

}
