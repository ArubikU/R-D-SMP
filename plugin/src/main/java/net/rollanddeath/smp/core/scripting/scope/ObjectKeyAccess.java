package net.rollanddeath.smp.core.scripting.scope;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Acceso de lectura a "sub-propiedades" de objetos almacenados en scopes.
 *
 * Esto permite paths tipo EVENT.custom.location.x aunque el valor sea un LivingEntity.
 *
 * Nota: esto es intencionalmente READ-ONLY.
 */
public final class ObjectKeyAccess {

    private ObjectKeyAccess() {
    }

    public static Object getChild(Object obj, String key) {
        if (obj == null || key == null) return null;

        // Normalizamos key para algunos accesos
        String k = key.trim();
        if (k.isEmpty()) return null;

        // Mapa normal (+ size)
        if (obj instanceof Map<?, ?> m) {
            String lk = k.toLowerCase(Locale.ROOT);
            if (lk.equals("size") || lk.equals("length")) {
                return m.size();
            }
            return m.get(k);
        }

        // Colecciones (+ size)
        if (obj instanceof Collection<?> c) {
            String lk = k.toLowerCase(Locale.ROOT);
            if (lk.equals("size") || lk.equals("length")) {
                return c.size();
            }
        }

        // Arrays (+ length)
        try {
            if (obj.getClass().isArray()) {
                String lk = k.toLowerCase(Locale.ROOT);
                if (lk.equals("length") || lk.equals("size")) {
                    return Array.getLength(obj);
                }
            }
        } catch (Exception ignored) {
        }

        // Strings (+ length)
        if (obj instanceof String s) {
            String lk = k.toLowerCase(Locale.ROOT);
            if (lk.equals("length") || lk.equals("size")) {
                return s.length();
            }
        }

        if (obj instanceof Location loc) {
            return getLocationChild(loc, k);
        }

        if (obj instanceof Block block) {
            return getBlockChild(block, k);
        }

        if (obj instanceof Vector v) {
            return getVectorChild(v, k);
        }

        if (obj instanceof ItemStack is) {
            return getItemStackChild(is, k);
        }

        // Importante: todos los Tameable son LivingEntity.
        // Si resolvemos LivingEntity primero, nunca se ejecutan las keys especiales de Tameable.
        if (obj instanceof Tameable t) {
            Object v = getTameableChild(t, k);
            if (v != null) return v;
        }

        if (obj instanceof LivingEntity le) {
            return getLivingEntityChild(le, k);
        }

        if (obj instanceof Projectile pr) {
            Object v = getEntityChild(pr, k);
            if (v != null) return v;
            return getProjectileChild(pr, k);
        }

        if (obj instanceof Entity e) {
            return getEntityChild(e, k);
        }

        if (obj instanceof World w) {
            return getWorldChild(w, k);
        }

        return null;
    }

    private static Object getLocationChild(Location loc, String key) {
        return switch (key.toLowerCase(Locale.ROOT)) {
            case "x" -> loc.getX();
            case "y" -> loc.getY();
            case "z" -> loc.getZ();
            case "blockx", "block_x" -> loc.getBlockX();
            case "blocky", "block_y" -> loc.getBlockY();
            case "blockz", "block_z" -> loc.getBlockZ();
            case "yaw" -> loc.getYaw();
            case "pitch" -> loc.getPitch();
            case "world" -> loc.getWorld() != null ? loc.getWorld().getName() : null;
            case "worldobj", "world_object" -> loc.getWorld();
            case "chunk" -> loc.getChunk();
            case "block" -> loc.getBlock();
            case "blockbelow", "block_below", "belowblock", "below_block" -> {
                try {
                    var b = loc.getBlock();
                    yield b != null ? b.getRelative(0, -1, 0) : null;
                } catch (Exception ignored) {
                    yield null;
                }
            }
            default -> null;
        };
    }

    private static Object getBlockChild(Block block, String key) {
        return switch (key.toLowerCase(Locale.ROOT)) {
            case "type" -> block.getType().name();
            case "material" -> block.getType().name();
            case "data" -> block.getBlockData().getAsString();
            case "location" -> block.getLocation();
            case "world" -> block.getWorld();
            case "worldobj", "world_object" -> block.getWorld();
            case "x" -> block.getX();
            case "y" -> block.getY();
            case "z" -> block.getZ();
            case "light" -> block.getLightLevel();
            case "block_power" -> block.getBlockPower();
            case "temperature" -> block.getTemperature();
            case "humidity" -> block.getHumidity();
            case "biome" -> block.getBiome().name();
            case "is_passable" -> block.isPassable();
            case "is_liquid" -> block.isLiquid();
            case "is_empty" -> block.isEmpty();
            default -> null;
        };
    }

    private static Object getVectorChild(Vector v, String key) {
        return switch (key.toLowerCase(Locale.ROOT)) {
            case "x" -> v.getX();
            case "y" -> v.getY();
            case "z" -> v.getZ();
            case "length" -> v.length();
            case "length2", "length_squared" -> v.lengthSquared();
            default -> null;
        };
    }

    private static Object getItemStackChild(ItemStack is, String key) {
        String k = key.toLowerCase(Locale.ROOT);
        return switch (k) {
            case "type", "material" -> is.getType().name();
            case "amount" -> is.getAmount();
            case "max_stack_size" -> is.getMaxStackSize();
            case "isair", "is_air" -> is.getType().isAir();
            case "isedible", "is_edible", "isfood", "is_food" -> is.getType().isEdible();
            case "hasmeta", "has_meta", "has_item_meta" -> is.hasItemMeta();
            case "displayname", "display_name" -> {
                if (!is.hasItemMeta() || is.getItemMeta() == null) yield null;
                var meta = is.getItemMeta();
                yield meta.hasDisplayName() ? meta.getDisplayName() : null;
            }
            case "custommodeldata", "custom_model_data" -> {
                if (!is.hasItemMeta() || is.getItemMeta() == null) yield null;
                var meta = is.getItemMeta();
                yield meta.hasCustomModelData() ? meta.getCustomModelData() : null;
            }
            case "lore" -> {
                try {
                    if (!is.hasItemMeta() || is.getItemMeta() == null) yield null;
                    var meta = is.getItemMeta();
                    yield meta.hasLore() ? meta.getLore() : null;
                } catch (Exception ignored) {
                    yield null;
                }
            }
            case "pdc" -> {
                try {
                    if (!is.hasItemMeta() || is.getItemMeta() == null) yield null;
                    PersistentDataContainer pdc = is.getItemMeta().getPersistentDataContainer();
                    if (pdc == null) yield null;

                    Map<String, Object> out = new HashMap<>();
                    for (NamespacedKey nk : pdc.getKeys()) {
                        if (nk == null) continue;
                        String full = nk.getNamespace() + ":" + nk.getKey();

                        Object v = null;
                        try {
                            v = pdc.get(nk, PersistentDataType.STRING);
                        } catch (Exception ignored) {
                        }
                        if (v == null) {
                            try {
                                v = pdc.get(nk, PersistentDataType.INTEGER);
                            } catch (Exception ignored) {
                            }
                        }
                        if (v == null) {
                            try {
                                v = pdc.get(nk, PersistentDataType.LONG);
                            } catch (Exception ignored) {
                            }
                        }
                        if (v == null) {
                            try {
                                v = pdc.get(nk, PersistentDataType.DOUBLE);
                            } catch (Exception ignored) {
                            }
                        }
                        if (v == null) {
                            try {
                                v = pdc.get(nk, PersistentDataType.FLOAT);
                            } catch (Exception ignored) {
                            }
                        }
                        if (v == null) {
                            try {
                                v = pdc.get(nk, PersistentDataType.BYTE);
                            } catch (Exception ignored) {
                            }
                        }

                        if (v != null) out.put(full, v);
                    }
                    yield out.isEmpty() ? null : out;
                } catch (Exception ignored) {
                    yield null;
                }
            }
            default -> null;
        };
    }

    private static Object getWorldChild(World w, String key) {
        return switch (key.toLowerCase(Locale.ROOT)) {
            case "name" -> w.getName();
            case "uuid" -> w.getUID().toString();
            case "time" -> w.getTime();
            case "fulltime", "full_time" -> w.getFullTime();
            case "weather" -> w.isThundering() ? "thunder" : (w.hasStorm() ? "rain" : "clear");
            case "storm" -> w.hasStorm();
            case "thundering" -> w.isThundering();
            case "seed" -> w.getSeed();
            case "environment" -> w.getEnvironment().name();
            case "difficulty" -> w.getDifficulty().name();
            case "max_height" -> w.getMaxHeight();
            case "min_height" -> w.getMinHeight();
            case "sea_level" -> w.getSeaLevel();
            case "player_count" -> w.getPlayerCount();
            default -> null;
        };
    }

    private static Object getEntityChild(Entity e, String key) {
        return switch (key.toLowerCase(Locale.ROOT)) {
            case "uuid" -> String.valueOf(e.getUniqueId());
            case "type" -> e.getType().name();
            case "name" -> e.getName();
            case "custom_name" -> e.getCustomName();
            case "location" -> e.getLocation();
            case "world" -> e.getWorld();
            case "worldobj", "world_object" -> e.getWorld();
            case "velocity" -> e.getVelocity();
            case "fall_distance" -> e.getFallDistance();
            case "fire_ticks" -> e.getFireTicks();
            case "freeze_ticks" -> e.getFreezeTicks();
            case "height" -> e.getHeight();
            case "width" -> e.getWidth();
            case "is_dead", "dead" -> e.isDead();
            case "is_valid", "valid" -> e.isValid();
            case "is_on_ground", "on_ground" -> e.isOnGround();
            case "is_glowing", "glowing" -> e.isGlowing();
            case "is_invulnerable", "invulnerable" -> e.isInvulnerable();
            case "is_silent", "silent" -> e.isSilent();
            case "is_custom_name_visible" -> e.isCustomNameVisible();
            case "passengers" -> e.getPassengers();
            case "vehicle" -> e.getVehicle();
            case "facing" -> e.getFacing().name();
            case "x" -> e.getLocation().getX();
            case "y" -> e.getLocation().getY();
            case "z" -> e.getLocation().getZ();
            case "yaw" -> e.getLocation().getYaw();
            case "pitch" -> e.getLocation().getPitch();
            case "ismonster", "is_monster" -> e instanceof Monster;
            default -> null;
        };
    }

    private static Object getLivingEntityChild(LivingEntity le, String key) {
        String k = key.toLowerCase(Locale.ROOT);
        switch (k) {
            case "health": return le.getHealth();
            case "max_health", "maxhealth": {
                var attr = le.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
                return attr != null ? attr.getValue() : null;
            }
            case "absorption": return le.getAbsorptionAmount();
            case "air": return le.getRemainingAir();
            case "max_air": return le.getMaximumAir();
            case "eye_location": return le.getEyeLocation();
            case "eye_height": return le.getEyeHeight();
            case "is_gliding": return le.isGliding();
            case "is_swimming": return le.isSwimming();
            case "is_climbing": return le.isClimbing();
            case "is_invisible": return le.isInvisible();
            case "is_leashed": return le.isLeashed();
        }
        
        if (le instanceof org.bukkit.entity.Player p) {
            Object v = getPlayerChild(p, k);
            if (v != null) return v;
        }
        
        return getEntityChild(le, key);
    }

    private static Object getPlayerChild(org.bukkit.entity.Player p, String key) {
        return switch (key) {
            case "display_name", "displayname" -> p.getDisplayName();
            case "gamemode" -> p.getGameMode().name();
            case "ping" -> p.getPing();
            case "locale" -> p.getLocale();
            case "address" -> p.getAddress() != null ? p.getAddress().toString() : null;
            case "exp" -> p.getExp();
            case "level" -> p.getLevel();
            case "total_exp" -> p.getTotalExperience();
            case "fly_speed" -> p.getFlySpeed();
            case "walk_speed" -> p.getWalkSpeed();
            case "is_flying" -> p.isFlying();
            case "is_sneaking" -> p.isSneaking();
            case "is_sprinting" -> p.isSprinting();
            case "is_blocking" -> p.isBlocking();
            case "is_sleeping" -> p.isSleeping();
            case "is_op" -> p.isOp();
            case "food" -> p.getFoodLevel();
            case "saturation" -> p.getSaturation();
            default -> null;
        };
    }

    private static Object getProjectileChild(Projectile pr, String key) {
        return switch (key.toLowerCase(Locale.ROOT)) {
            case "shooter" -> {
                try {
                    yield pr.getShooter();
                } catch (Exception ignored) {
                    yield null;
                }
            }
            case "shooter_entity" -> {
                try {
                    var s = pr.getShooter();
                    yield (s instanceof Entity se) ? se : null;
                } catch (Exception ignored) {
                    yield null;
                }
            }
            default -> null;
        };
    }

    private static Object getTameableChild(Tameable t, String key) {
        return switch (key.toLowerCase(Locale.ROOT)) {
            case "istamed", "is_tamed", "tamed" -> {
                try {
                    yield t.isTamed();
                } catch (Exception ignored) {
                    yield null;
                }
            }
            case "owner" -> {
                try {
                    yield t.getOwner();
                } catch (Exception ignored) {
                    yield null;
                }
            }
            case "owner_uuid", "owneruuid" -> {
                try {
                    var o = t.getOwner();
                    if (o instanceof Entity e) {
                        yield String.valueOf(e.getUniqueId());
                    }
                    yield null;
                } catch (Exception ignored) {
                    yield null;
                }
            }
            default -> null;
        };
    }
}
