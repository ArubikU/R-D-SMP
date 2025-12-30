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
            case "blockx" -> loc.getBlockX();
            case "blocky" -> loc.getBlockY();
            case "blockz" -> loc.getBlockZ();
            case "yaw" -> loc.getYaw();
            case "pitch" -> loc.getPitch();
            case "world" -> loc.getWorld() != null ? loc.getWorld().getName() : null;
            case "worldobj", "world_object" -> loc.getWorld();
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
            case "type" -> block.getType() != null ? block.getType().name() : null;
            case "material" -> block.getType() != null ? block.getType().name() : null;
            case "x" -> block.getX();
            case "y" -> block.getY();
            case "z" -> block.getZ();
            case "world" -> block.getWorld() != null ? block.getWorld().getName() : null;
            case "worldobj", "world_object" -> block.getWorld();
            case "location" -> block.getLocation();
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
            case "type", "material" -> is.getType() != null ? is.getType().name() : null;
            case "amount" -> {
                try {
                    yield is.getAmount();
                } catch (Exception ignored) {
                    yield null;
                }
            }
            case "isair", "is_air" -> {
                try {
                    yield is.getType() == null || is.getType().isAir();
                } catch (Exception ignored) {
                    yield false;
                }
            }
            case "isedible", "is_edible", "isfood", "is_food" -> {
                try {
                    yield is.getType() != null && is.getType().isEdible();
                } catch (Exception ignored) {
                    yield false;
                }
            }
            case "hasmeta", "has_meta" -> {
                try {
                    yield is.hasItemMeta();
                } catch (Exception ignored) {
                    yield false;
                }
            }
            case "displayname", "display_name" -> {
                try {
                    if (!is.hasItemMeta() || is.getItemMeta() == null) yield null;
                    var meta = is.getItemMeta();
                    yield meta.hasDisplayName() ? meta.getDisplayName() : null;
                } catch (Exception ignored) {
                    yield null;
                }
            }
            case "custommodeldata", "custom_model_data" -> {
                try {
                    if (!is.hasItemMeta() || is.getItemMeta() == null) yield null;
                    var meta = is.getItemMeta();
                    yield meta.hasCustomModelData() ? meta.getCustomModelData() : null;
                } catch (Exception ignored) {
                    yield null;
                }
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
            case "time" -> w.getTime();
            case "fulltime", "full_time" -> w.getFullTime();
            default -> null;
        };
    }

    private static Object getEntityChild(Entity e, String key) {
        return switch (key.toLowerCase(Locale.ROOT)) {
            case "uuid" -> String.valueOf(e.getUniqueId());
            case "type" -> e.getType() != null ? e.getType().name() : null;
            case "ismonster", "is_monster" -> e instanceof Monster;
            case "name" -> {
                try {
                    yield e.getName();
                } catch (Exception ex) {
                    yield null;
                }
            }
            case "customname", "custom_name" -> {
                try {
                    var cn = e.customName();
                    yield cn != null ? cn.toString() : null;
                } catch (Exception ex) {
                    yield null;
                }
            }
            case "location" -> e.getLocation();
            case "world" -> e.getWorld() != null ? e.getWorld().getName() : null;
            case "worldobj", "world_object" -> e.getWorld();
            case "velocity" -> {
                try {
                    yield e.getVelocity();
                } catch (Exception ex) {
                    yield null;
                }
            }
            case "isdead", "is_dead" -> {
                try {
                    yield e.isDead();
                } catch (Exception ex) {
                    yield null;
                }
            }
            default -> null;
        };
    }

    private static Object getLivingEntityChild(LivingEntity le, String key) {
        String k = key.toLowerCase(Locale.ROOT);
        if (k.equals("health")) {
            try {
                return le.getHealth();
            } catch (Exception ignored) {
                return null;
            }
        }
        if (k.equals("maxhealth") || k.equals("max_health")) {
            try {
                var inst = le.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH);
                return inst != null ? inst.getValue() : null;
            } catch (Exception ignored) {
                return null;
            }
        }
        return getEntityChild(le, key);
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
