package net.rollanddeath.smp.core.scripting.scope;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Construye un payload estable para el scope EVENT.
 *
 * Objetivo:
 * - EVENT.<campo> accesible con nombres consistentes (location/from/to/damage/etc) cuando exista.
 * - EVENT.native.* mantiene acceso reflectivo completo al objeto original (Bukkit Event u objeto custom).
 *
 * Nota: este payload es READ-ONLY desde scripts (es un Map, pero el engine no permite escribir en EVENT.*).
 */
public final class EventPayloads {

    private EventPayloads() {
    }

    public static Object wrap(Object raw) {
        if (raw == null) return null;

        if (raw instanceof Map<?, ?> m) {
            // Si ya viene como Map, lo respetamos, pero si incluye un evento nativo
            // (native/__native) añadimos normalizaciones comunes SIN pisar claves ya existentes.
            Object nativeObj = null;
            if (m.containsKey("native")) nativeObj = m.get("native");
            else if (m.containsKey("__native")) nativeObj = m.get("__native");

            if (nativeObj == null) {
                return raw;
            }

            Map<String, Object> out = new HashMap<>();
            for (var e : m.entrySet()) {
                if (e.getKey() != null) out.put(String.valueOf(e.getKey()), e.getValue());
            }
            out.putIfAbsent("native", nativeObj);

            // Metadatos de clase (si no estaban ya)
            try {
                Class<?> c = nativeObj.getClass();
                out.putIfAbsent("class", c.getName());
                out.putIfAbsent("simpleClass", c.getSimpleName());
            } catch (Exception ignored) {
            }

            // Normalizaciones comunes desde el evento nativo
            try {
                ReflectiveValueAccessor rva = new ReflectiveValueAccessor(nativeObj);

                putIfAbsentNotNull(out, "location", rva.get(new String[]{"location"}));
                putIfAbsentNotNull(out, "from", rva.get(new String[]{"from"}));
                putIfAbsentNotNull(out, "to", rva.get(new String[]{"to"}));

                putIfAbsentNotNull(out, "damage", rva.get(new String[]{"damage"}));
                putIfAbsentNotNull(out, "finalDamage", rva.get(new String[]{"final_damage"}));
                putIfAbsentNotNull(out, "cause", rva.get(new String[]{"cause"}));

                putIfAbsentNotNull(out, "entity", rva.get(new String[]{"entity"}));
                putIfAbsentNotNull(out, "player", rva.get(new String[]{"player"}));
                putIfAbsentNotNull(out, "damager", rva.get(new String[]{"damager"}));

                putIfAbsentNotNull(out, "block", rva.get(new String[]{"block"}));
                putIfAbsentNotNull(out, "item", rva.get(new String[]{"item"}));

                // ProjectileHitEvent y similares
                putIfAbsentNotNull(out, "hitBlock", rva.get(new String[]{"hit_block"}));
                putIfAbsentNotNull(out, "hitEntity", rva.get(new String[]{"hit_entity"}));
            } catch (Exception ignored) {
            }

            // hitLocation derivada (útil para scripts de proyectiles)
            if (!out.containsKey("hitLocation")) {
                Location hitLoc = null;
                try {
                    Object he = out.get("hitEntity");
                    if (he instanceof Entity e) {
                        hitLoc = e.getLocation();
                    }
                } catch (Exception ignored) {
                }
                if (hitLoc == null) {
                    try {
                        Object hb = out.get("hitBlock");
                        if (hb instanceof Block b) {
                            hitLoc = b.getLocation().add(0.5, 0.5, 0.5);
                        }
                    } catch (Exception ignored) {
                    }
                }
                if (hitLoc != null) {
                    out.put("hitLocation", hitLoc);
                }
            }

            // Si no hay location, intentamos derivarla desde otras claves típicas.
            if (!out.containsKey("location")) {
                Location derived = null;

                Object to = out.get("to");
                if (to instanceof Location l) derived = l;

                if (derived == null) {
                    Object from = out.get("from");
                    if (from instanceof Location l) derived = l;
                }

                if (derived == null) {
                    Object block = out.get("block");
                    if (block instanceof Block b) {
                        try {
                            derived = b.getLocation();
                        } catch (Exception ignored) {
                        }
                    }
                }

                if (derived == null) {
                    Object ent = out.get("entity");
                    if (ent instanceof Entity e) {
                        try {
                            derived = e.getLocation();
                        } catch (Exception ignored) {
                        }
                    }
                }

                if (derived != null) {
                    out.put("location", derived);
                }
            }

            // Delta (útil para PlayerMoveEvent y similares): EVENT.delta.{x,y,z}
            try {
                Object from = out.get("from");
                Object to = out.get("to");
                if (from instanceof Location lf && to instanceof Location lt && !out.containsKey("delta")) {
                    Map<String, Object> delta = new HashMap<>();
                    delta.put("x", lt.getX() - lf.getX());
                    delta.put("y", lt.getY() - lf.getY());
                    delta.put("z", lt.getZ() - lf.getZ());
                    out.put("delta", delta);
                }
            } catch (Exception ignored) {
            }

            return out;
        }

        Map<String, Object> out = new HashMap<>();
        out.put("native", raw);

        Class<?> c = raw.getClass();
        out.put("class", c.getName());
        out.put("simpleClass", c.getSimpleName());

        // Instanceof checks específicos para eventos comunes de Bukkit
        if (raw instanceof org.bukkit.event.player.PlayerInteractEvent pie) {
            try {
                putIfNotNull(out, "action", pie.getAction() != null ? pie.getAction().name() : null);
                putIfNotNull(out, "block", pie.getClickedBlock());
                putIfNotNull(out, "item", pie.getItem());
                putIfNotNull(out, "hand", pie.getHand() != null ? pie.getHand().name() : null);
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.block.BlockBreakEvent bbe) {
            try {
                putIfNotNull(out, "block", bbe.getBlock());
                putIfNotNull(out, "player", bbe.getPlayer());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.entity.EntityDamageEvent ede) {
            try {
                putIfNotNull(out, "entity", ede.getEntity());
                putIfNotNull(out, "cause", ede.getCause() != null ? ede.getCause().name() : null);
                putIfNotNull(out, "damage", ede.getDamage());
                putIfNotNull(out, "finalDamage", ede.getFinalDamage());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.entity.EntityDamageByEntityEvent edee) {
            try {
                putIfNotNull(out, "damager", edee.getDamager());
                putIfNotNull(out, "entity", edee.getEntity());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.player.PlayerMoveEvent pme) {
            try {
                putIfNotNull(out, "from", pme.getFrom());
                putIfNotNull(out, "to", pme.getTo());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.entity.ProjectileHitEvent phe) {
            try {
                putIfNotNull(out, "hitBlock", phe.getHitBlock());
                putIfNotNull(out, "hitEntity", phe.getHitEntity());
                putIfNotNull(out, "entity", phe.getEntity());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.entity.EntityShootBowEvent esbe) {
            try {
                putIfNotNull(out, "entity", esbe.getEntity());
                putIfNotNull(out, "bow", esbe.getBow());
                putIfNotNull(out, "consumable", esbe.getConsumable());
                putIfNotNull(out, "projectile", esbe.getProjectile());
                putIfNotNull(out, "force", esbe.getForce());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.player.PlayerItemConsumeEvent pice) {
            try {
                putIfNotNull(out, "player", pice.getPlayer());
                putIfNotNull(out, "item", pice.getItem());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.inventory.InventoryClickEvent ice) {
            try {
                putIfNotNull(out, "slot", ice.getSlot());
                putIfNotNull(out, "rawSlot", ice.getRawSlot());
                putIfNotNull(out, "slotType", ice.getSlotType() != null ? ice.getSlotType().name() : null);
                putIfNotNull(out, "click", ice.getClick() != null ? ice.getClick().name() : null);
                putIfNotNull(out, "currentItem", ice.getCurrentItem());
                putIfNotNull(out, "cursor", ice.getCursor());
                putIfNotNull(out, "isShiftClick", ice.isShiftClick());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.entity.EntityRegainHealthEvent erhe) {
            try {
                putIfNotNull(out, "entity", erhe.getEntity());
                putIfNotNull(out, "regainReason", erhe.getRegainReason() != null ? erhe.getRegainReason().name() : null);
                putIfNotNull(out, "amount", erhe.getAmount());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.entity.FoodLevelChangeEvent flce) {
            try {
                putIfNotNull(out, "entity", flce.getEntity());
                putIfNotNull(out, "foodLevel", flce.getFoodLevel());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.player.PlayerFishEvent pfe) {
            try {
                putIfNotNull(out, "player", pfe.getPlayer());
                putIfNotNull(out, "state", pfe.getState() != null ? pfe.getState().name() : null);
                putIfNotNull(out, "caught", pfe.getCaught());
                putIfNotNull(out, "hook", pfe.getHook());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.inventory.InventoryCloseEvent ic) {
            try {
                putIfNotNull(out, "player", ic.getPlayer());
                putIfNotNull(out, "inventory", ic.getInventory());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.player.PlayerPortalEvent ppe) {
            try {
                putIfNotNull(out, "player", ppe.getPlayer());
                putIfNotNull(out, "from", ppe.getFrom());
                putIfNotNull(out, "to", ppe.getTo());
                putIfNotNull(out, "cause", ppe.getCause() != null ? ppe.getCause().name() : null);
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.player.PlayerBedEnterEvent pbee) {
            try {
                putIfNotNull(out, "player", pbee.getPlayer());
                putIfNotNull(out, "bed", pbee.getBed());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.entity.EntityDeathEvent ede) {
            try {
                putIfNotNull(out, "entity", ede.getEntity());
                putIfNotNull(out, "drops", ede.getDrops());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.entity.EntitySpawnEvent ese) {
            try {
                putIfNotNull(out, "entity", ese.getEntity());
                putIfNotNull(out, "location", ese.getLocation());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.entity.CreatureSpawnEvent cse) {
            try {
                putIfNotNull(out, "entity", cse.getEntity());
                putIfNotNull(out, "spawnReason", cse.getSpawnReason() != null ? cse.getSpawnReason().name() : null);
                putIfNotNull(out, "location", cse.getLocation());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.entity.ExplosionPrimeEvent epe) {
            try {
                putIfNotNull(out, "entity", epe.getEntity());
                putIfNotNull(out, "radius", epe.getRadius());
                putIfNotNull(out, "fire", epe.getFire());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.player.PlayerItemDamageEvent pide) {
            try {
                putIfNotNull(out, "player", pide.getPlayer());
                putIfNotNull(out, "item", pide.getItem());
                putIfNotNull(out, "damage", pide.getDamage());
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.entity.EntityPotionEffectEvent epee) {
            try {
                putIfNotNull(out, "entity", epee.getEntity());
                putIfNotNull(out, "action", epee.getAction() != null ? epee.getAction().name() : null);
                putIfNotNull(out, "newEffect", epee.getNewEffect());
                putIfNotNull(out, "oldEffect", epee.getOldEffect());
                putIfNotNull(out, "cause", epee.getCause() != null ? epee.getCause().name() : null);
            } catch (Exception ignored) {
            }
        }

        if (raw instanceof org.bukkit.event.block.BlockFromToEvent bfte) {
            try {
                putIfNotNull(out, "block", bfte.getBlock());
                putIfNotNull(out, "toBlock", bfte.getToBlock());
            } catch (Exception ignored) {
            }
        }

        // Fallback: reflexión para casos no cubiertos
        ReflectiveValueAccessor rva = new ReflectiveValueAccessor(raw);

        // Normalizaciones comunes (si el evento lo expone y no fue seteado arriba).
        putIfAbsentNotNull(out, "location", rva.get(new String[]{"location"}));
        putIfAbsentNotNull(out, "from", rva.get(new String[]{"from"}));
        putIfAbsentNotNull(out, "to", rva.get(new String[]{"to"}));

        // Damage
        putIfAbsentNotNull(out, "damage", rva.get(new String[]{"damage"}));
        putIfAbsentNotNull(out, "finalDamage", rva.get(new String[]{"final_damage"}));
        putIfAbsentNotNull(out, "cause", rva.get(new String[]{"cause"}));

        // Entity-ish
        putIfAbsentNotNull(out, "entity", rva.get(new String[]{"entity"}));
        putIfAbsentNotNull(out, "player", rva.get(new String[]{"player"}));
        putIfAbsentNotNull(out, "damager", rva.get(new String[]{"damager"}));

        // Block/item
        putIfAbsentNotNull(out, "block", rva.get(new String[]{"block"}));
        putIfAbsentNotNull(out, "item", rva.get(new String[]{"item"}));

        // ProjectileHitEvent y similares
        putIfAbsentNotNull(out, "hitBlock", rva.get(new String[]{"hit_block"}));
        putIfAbsentNotNull(out, "hitEntity", rva.get(new String[]{"hit_entity"}));

        // hitLocation derivada
        try {
            if (!out.containsKey("hitLocation")) {
                Location hitLoc = null;
                Object he = out.get("hitEntity");
                if (he instanceof Entity e) {
                    hitLoc = e.getLocation();
                }
                if (hitLoc == null) {
                    Object hb = out.get("hitBlock");
                    if (hb instanceof Block b) {
                        hitLoc = b.getLocation().add(0.5, 0.5, 0.5);
                    }
                }
                if (hitLoc != null) {
                    out.put("hitLocation", hitLoc);
                }
            }
        } catch (Exception ignored) {
        }

        // Si no hay location, intentamos derivarla desde otras claves típicas.
        if (!out.containsKey("location")) {
            Location derived = null;

            Object to = out.get("to");
            if (to instanceof Location l) derived = l;

            if (derived == null) {
                Object from = out.get("from");
                if (from instanceof Location l) derived = l;
            }

            if (derived == null) {
                Object block = out.get("block");
                if (block instanceof Block b) {
                    try {
                        derived = b.getLocation();
                    } catch (Exception ignored) {
                    }
                }
            }

            if (derived == null) {
                Object ent = out.get("entity");
                if (ent instanceof Entity e) {
                    try {
                        derived = e.getLocation();
                    } catch (Exception ignored) {
                    }
                }
            }

            if (derived != null) {
                out.put("location", derived);
            }
        }

        // Delta (útil para PlayerMoveEvent y similares): EVENT.delta.{x,y,z}
        try {
            Object from = out.get("from");
            Object to = out.get("to");
            if (from instanceof Location lf && to instanceof Location lt) {
                Map<String, Object> delta = new HashMap<>();
                delta.put("x", lt.getX() - lf.getX());
                delta.put("y", lt.getY() - lf.getY());
                delta.put("z", lt.getZ() - lf.getZ());
                out.put("delta", delta);
            }
        } catch (Exception ignored) {
        }

        return out;
    }

    private static void putIfNotNull(Map<String, Object> out, String key, Object value) {
        if (out == null || key == null) return;
        if (value != null) out.put(key, value);
    }

    private static void putIfAbsentNotNull(Map<String, Object> out, String key, Object value) {
        if (out == null || key == null) return;
        if (value != null && !out.containsKey(key)) out.put(key, value);
    }
}
