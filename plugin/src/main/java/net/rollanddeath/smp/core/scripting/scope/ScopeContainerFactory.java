package net.rollanddeath.smp.core.scripting.scope;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.teams.Team;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class ScopeContainerFactory {

    private final RollAndDeathSMP plugin;
    private final List<ScopeContainerProvider> providers = new ArrayList<>();

    ScopeContainerFactory(RollAndDeathSMP plugin) {
        this.plugin = plugin;

        providers.add(new EntityProvider());
        providers.add(new ItemProvider());
        providers.add(new PlayerProvider());
        providers.add(new LocationProvider());
        providers.add(new WorldProvider());
        providers.add(new ChunkProvider());
        providers.add(new TeamProvider());
        providers.add(new GlobalProvider());
        providers.add(new EventProvider());
    }

    ScopeContainer create(ScopeId id, Object base, ScopeStorage storage) {
        for (ScopeContainerProvider p : providers) {
            if (!p.supports(id, base)) continue;
            return p.create(id, base, storage, plugin);
        }
        // Sin fallback reflectivo: si no hay provider, el scope queda vacío.
        return new DefaultScopeContainer(id, base, storage, null, null);
    }

    private static final class PlayerProvider implements ScopeContainerProvider {
        @Override
        public boolean supports(ScopeId id, Object base) {
            return id == ScopeId.PLAYER && base instanceof Player;
        }

        @Override
        public ScopeContainer create(ScopeId id, Object base, ScopeStorage storage, RollAndDeathSMP plugin) {
            Player p = (Player) base;

            Map<String, Object> nativeSnap = new HashMap<>();
            nativeSnap.put("name", p.getName());
            nativeSnap.put("uuid", String.valueOf(p.getUniqueId()));
            nativeSnap.put("health", p.getHealth());
            nativeSnap.put("maxHealth", p.getMaxHealth());
            nativeSnap.put("hunger", p.getFoodLevel());
            nativeSnap.put("saturation", p.getSaturation());

            // Location (flat + nested)
            Location loc = p.getLocation();
            if (loc != null) {
                nativeSnap.put("x", loc.getX());
                nativeSnap.put("y", loc.getY());
                nativeSnap.put("z", loc.getZ());
                nativeSnap.put("world", loc.getWorld() != null ? loc.getWorld().getName() : null);

                Map<String, Object> locSnap = new HashMap<>();
                locSnap.put("x", loc.getX());
                locSnap.put("y", loc.getY());
                locSnap.put("z", loc.getZ());
                locSnap.put("blockX", loc.getBlockX());
                locSnap.put("blockY", loc.getBlockY());
                locSnap.put("blockZ", loc.getBlockZ());
                locSnap.put("yaw", loc.getYaw());
                locSnap.put("pitch", loc.getPitch());
                locSnap.put("world", loc.getWorld() != null ? loc.getWorld().getName() : null);
                nativeSnap.put("location", locSnap);
            }

            // Inventory (nested) - útil para scripts sin depender de vars legacy
            try {
                Map<String, Object> invSnap = new HashMap<>();
                var inv = p.getInventory();
                invSnap.put("mainHand", inv.getItemInMainHand() != null ? inv.getItemInMainHand().getType().name() : null);
                invSnap.put("offHand", inv.getItemInOffHand() != null ? inv.getItemInOffHand().getType().name() : null);
                invSnap.put("helmet", inv.getHelmet() != null ? inv.getHelmet().getType().name() : null);
                invSnap.put("chestplate", inv.getChestplate() != null ? inv.getChestplate().getType().name() : null);
                invSnap.put("leggings", inv.getLeggings() != null ? inv.getLeggings().getType().name() : null);
                invSnap.put("boots", inv.getBoots() != null ? inv.getBoots().getType().name() : null);
                nativeSnap.put("inventory", invSnap);
            } catch (Exception ignored) {
            }

            Map<String, Object> stateSnap = new HashMap<>();
            stateSnap.put("isOnline", p.isOnline());
            stateSnap.put("isDead", p.isDead());
            stateSnap.put("isSneaking", p.isSneaking());
            stateSnap.put("isSprinting", p.isSprinting());
            stateSnap.put("isFlying", p.isFlying());
            stateSnap.put("isGliding", p.isGliding());
            stateSnap.put("isInWater", p.isInWater());
            stateSnap.put("isInvulnerable", p.isInvulnerable());

            // Nested state agrupado (sin romper claves existentes)
            Map<String, Object> movementSnap = new HashMap<>();
            movementSnap.put("isSneaking", p.isSneaking());
            movementSnap.put("isSprinting", p.isSprinting());
            movementSnap.put("isFlying", p.isFlying());
            movementSnap.put("isGliding", p.isGliding());
            movementSnap.put("isInWater", p.isInWater());
            stateSnap.put("movement", movementSnap);

            return new DefaultScopeContainer(id, base, storage, new MapValueAccessor(nativeSnap), new MapValueAccessor(stateSnap));
        }
    }

    private static final class EntityProvider implements ScopeContainerProvider {
        @Override
        public boolean supports(ScopeId id, Object base) {
            return (id == ScopeId.SUBJECT || id == ScopeId.TARGET || id == ScopeId.PROJECTILE) && base instanceof Entity;
        }

        @Override
        public ScopeContainer create(ScopeId id, Object base, ScopeStorage storage, RollAndDeathSMP plugin) {
            Entity e = (Entity) base;

            Map<String, Object> nativeSnap = new HashMap<>();
            nativeSnap.put("uuid", String.valueOf(e.getUniqueId()));
            nativeSnap.put("type", e.getType() != null ? e.getType().name() : null);
            nativeSnap.put("name", e.getName());
            try {
                nativeSnap.put("customName", e.getCustomName());
            } catch (Exception ignored) {
                nativeSnap.put("customName", null);
            }

            Location loc = e.getLocation();
            if (loc != null) {
                nativeSnap.put("x", loc.getX());
                nativeSnap.put("y", loc.getY());
                nativeSnap.put("z", loc.getZ());
                nativeSnap.put("world", loc.getWorld() != null ? loc.getWorld().getName() : null);

                Map<String, Object> locSnap = new HashMap<>();
                locSnap.put("x", loc.getX());
                locSnap.put("y", loc.getY());
                locSnap.put("z", loc.getZ());
                locSnap.put("blockX", loc.getBlockX());
                locSnap.put("blockY", loc.getBlockY());
                locSnap.put("blockZ", loc.getBlockZ());
                locSnap.put("yaw", loc.getYaw());
                locSnap.put("pitch", loc.getPitch());
                locSnap.put("world", loc.getWorld() != null ? loc.getWorld().getName() : null);
                nativeSnap.put("location", locSnap);
            }

            Map<String, Object> stateSnap = new HashMap<>();
            stateSnap.put("isValid", e.isValid());
            stateSnap.put("isDead", (e instanceof LivingEntity le) && le.isDead());

            if (e instanceof LivingEntity le) {
                try {
                    stateSnap.put("health", le.getHealth());
                    stateSnap.put("maxHealth", le.getMaxHealth());
                } catch (Exception ignored) {
                }
            }

            ValueAccessor nativeAccessor = path -> {
                Object v = new MapValueAccessor(nativeSnap).get(path);
                if (v != null) return v;
                return new ReflectiveValueAccessor(base).get(path);
            };

            ValueAccessor stateAccessor = path -> {
                Object v = new MapValueAccessor(stateSnap).get(path);
                if (v != null) return v;
                return null;
            };

            return new DefaultScopeContainer(id, base, storage, nativeAccessor, stateAccessor);
        }
    }

    private static final class ItemProvider implements ScopeContainerProvider {
        @Override
        public boolean supports(ScopeId id, Object base) {
            return id == ScopeId.ITEM && base instanceof ItemStack;
        }

        @Override
        public ScopeContainer create(ScopeId id, Object base, ScopeStorage storage, RollAndDeathSMP plugin) {
            ItemStack item = (ItemStack) base;

            Map<String, Object> nativeSnap = new HashMap<>();
            try {
                nativeSnap.put("type", item.getType() != null ? item.getType().name() : null);
            } catch (Exception ignored) {
                nativeSnap.put("type", null);
            }
            try {
                nativeSnap.put("amount", item.getAmount());
            } catch (Exception ignored) {
                nativeSnap.put("amount", null);
            }

            try {
                boolean hasMeta = item.hasItemMeta();
                nativeSnap.put("hasMeta", hasMeta);
                if (hasMeta && item.getItemMeta() != null) {
                    var meta = item.getItemMeta();
                    try {
                        nativeSnap.put("displayName", meta.hasDisplayName() ? meta.getDisplayName() : null);
                    } catch (Exception ignored) {
                        nativeSnap.put("displayName", null);
                    }
                    try {
                        nativeSnap.put("lore", meta.hasLore() ? meta.getLore() : null);
                    } catch (Exception ignored) {
                        nativeSnap.put("lore", null);
                    }
                    try {
                        nativeSnap.put("customModelData", meta.hasCustomModelData() ? meta.getCustomModelData() : null);
                    } catch (Exception ignored) {
                        nativeSnap.put("customModelData", null);
                    }
                }
            } catch (Exception ignored) {
            }

            Map<String, Object> stateSnap = new HashMap<>();
            try {
                stateSnap.put("isAir", item.getType() == null || item.getType().isAir());
            } catch (Exception ignored) {
                stateSnap.put("isAir", false);
            }
            try {
                stateSnap.put("isEdible", item.getType() != null && item.getType().isEdible());
            } catch (Exception ignored) {
                stateSnap.put("isEdible", false);
            }

            ValueAccessor nativeAccessor = path -> {
                Object v = new MapValueAccessor(nativeSnap).get(path);
                if (v != null) return v;
                return new ReflectiveValueAccessor(base).get(path);
            };

            return new DefaultScopeContainer(id, base, storage, nativeAccessor, new MapValueAccessor(stateSnap));
        }
    }

    private static final class WorldProvider implements ScopeContainerProvider {
        @Override
        public boolean supports(ScopeId id, Object base) {
            return id == ScopeId.WORLD && base instanceof World;
        }

        @Override
        public ScopeContainer create(ScopeId id, Object base, ScopeStorage storage, RollAndDeathSMP plugin) {
            World w = (World) base;

            Map<String, Object> nativeSnap = new HashMap<>();
            nativeSnap.put("name", w.getName());
            nativeSnap.put("time", w.getTime());
            nativeSnap.put("fullTime", w.getFullTime());

            Map<String, Object> timeSnap = new HashMap<>();
            timeSnap.put("time", w.getTime());
            timeSnap.put("fullTime", w.getFullTime());
            nativeSnap.put("clock", timeSnap);

            Map<String, Object> stateSnap = new HashMap<>();
            stateSnap.put("isRaining", w.hasStorm());
            stateSnap.put("isThundering", w.isThundering());
            long t = w.getTime() % 24000L;
            stateSnap.put("isDay", t >= 0 && t < 12300);
            stateSnap.put("isNight", t >= 12300 && t < 24000);
            stateSnap.put("isLoaded", true);

            Map<String, Object> weatherSnap = new HashMap<>();
            weatherSnap.put("isRaining", w.hasStorm());
            weatherSnap.put("isThundering", w.isThundering());
            stateSnap.put("weather", weatherSnap);

            return new DefaultScopeContainer(id, base, storage, new MapValueAccessor(nativeSnap), new MapValueAccessor(stateSnap));
        }
    }

    private static final class LocationProvider implements ScopeContainerProvider {
        @Override
        public boolean supports(ScopeId id, Object base) {
            return id == ScopeId.LOCATION && base instanceof Location;
        }

        @Override
        public ScopeContainer create(ScopeId id, Object base, ScopeStorage storage, RollAndDeathSMP plugin) {
            Location loc = (Location) base;

            Map<String, Object> nativeSnap = new HashMap<>();
            nativeSnap.put("x", loc.getX());
            nativeSnap.put("y", loc.getY());
            nativeSnap.put("z", loc.getZ());
            nativeSnap.put("blockX", loc.getBlockX());
            nativeSnap.put("blockY", loc.getBlockY());
            nativeSnap.put("blockZ", loc.getBlockZ());
            nativeSnap.put("yaw", loc.getYaw());
            nativeSnap.put("pitch", loc.getPitch());
            nativeSnap.put("world", loc.getWorld() != null ? loc.getWorld().getName() : null);
            nativeSnap.put("worldObj", loc.getWorld());
            try {
                nativeSnap.put("block", loc.getBlock());
            } catch (Exception ignored) {
                nativeSnap.put("block", null);
            }

            ValueAccessor nativeAccessor = path -> {
                Object v = new MapValueAccessor(nativeSnap).get(path);
                if (v != null) return v;
                return new ReflectiveValueAccessor(base).get(path);
            };

            return new DefaultScopeContainer(id, base, storage, nativeAccessor, new MapValueAccessor(new HashMap<>()));
        }
    }

    private static final class ChunkProvider implements ScopeContainerProvider {
        @Override
        public boolean supports(ScopeId id, Object base) {
            return id == ScopeId.CHUNK && base instanceof Chunk;
        }

        @Override
        public ScopeContainer create(ScopeId id, Object base, ScopeStorage storage, RollAndDeathSMP plugin) {
            Chunk c = (Chunk) base;

            Map<String, Object> nativeSnap = new HashMap<>();
            nativeSnap.put("x", c.getX());
            nativeSnap.put("z", c.getZ());
            nativeSnap.put("world", c.getWorld() != null ? c.getWorld().getName() : null);

            Map<String, Object> posSnap = new HashMap<>();
            posSnap.put("x", c.getX());
            posSnap.put("z", c.getZ());
            nativeSnap.put("pos", posSnap);

            Map<String, Object> stateSnap = new HashMap<>();
            stateSnap.put("isLoaded", c.isLoaded());
            // isGenerated era Paper-only; sin reflection no lo exponemos.

            return new DefaultScopeContainer(id, base, storage, new MapValueAccessor(nativeSnap), new MapValueAccessor(stateSnap));
        }
    }

    private static final class TeamProvider implements ScopeContainerProvider {
        @Override
        public boolean supports(ScopeId id, Object base) {
            return id == ScopeId.TEAM && base instanceof Team;
        }

        @Override
        public ScopeContainer create(ScopeId id, Object base, ScopeStorage storage, RollAndDeathSMP plugin) {
            Team t = (Team) base;

            Map<String, Object> nativeSnap = new HashMap<>();
            nativeSnap.put("name", t.getName());
            nativeSnap.put("owner", t.getOwner() != null ? String.valueOf(t.getOwner()) : null);
            try {
                nativeSnap.put("memberCount", t.getMembers() != null ? t.getMembers().size() : 0);
            } catch (Exception ignored) {
                nativeSnap.put("memberCount", 0);
            }

            Map<String, Object> stateSnap = new HashMap<>();
            stateSnap.put("isFriendlyFireEnabled", t.isFriendlyFire());
            // isEliminated se espera como engine-only via TEAM.state.isEliminated

            return new DefaultScopeContainer(id, base, storage, new MapValueAccessor(nativeSnap), new MapValueAccessor(stateSnap));
        }
    }

    private static final class GlobalProvider implements ScopeContainerProvider {
        @Override
        public boolean supports(ScopeId id, Object base) {
            return id == ScopeId.GLOBAL;
        }

        @Override
        public ScopeContainer create(ScopeId id, Object base, ScopeStorage storage, RollAndDeathSMP plugin) {
            // GLOBAL: algunos valores nativos útiles del server/plugin.
            Map<String, Object> nativeSnap = new HashMap<>();
            try {
                nativeSnap.put("currentDay", plugin != null && plugin.getGameManager() != null ? plugin.getGameManager().getCurrentDay() : null);
            } catch (Exception ignored) {
                nativeSnap.put("currentDay", null);
            }

            Map<String, Object> stateSnap = new HashMap<>();
            stateSnap.put("isMaintenanceMode", false);
            stateSnap.put("isDebugEnabled", false);
            return new DefaultScopeContainer(id, base, storage, new MapValueAccessor(nativeSnap), new MapValueAccessor(stateSnap));
        }
    }

    private static final class EventProvider implements ScopeContainerProvider {
        @Override
        public boolean supports(ScopeId id, Object base) {
            return id == ScopeId.EVENT;
        }

        @Override
        public ScopeContainer create(ScopeId id, Object base, ScopeStorage storage, RollAndDeathSMP plugin) {
            Map<String, Object> nativeSnap = new HashMap<>();
            if (base != null) {
                Class<?> c = base.getClass();
                nativeSnap.put("class", c.getName());
                nativeSnap.put("simpleClass", c.getSimpleName());
            }

            Map<String, Object> stateSnap = new HashMap<>();

            boolean isAsync = base instanceof org.bukkit.event.Event e && e.isAsynchronous();
            stateSnap.put("isAsync", isAsync);

            boolean isCancelled = false;
            if (base instanceof org.bukkit.event.Cancellable c) {
                isCancelled = c.isCancelled();
            }
            stateSnap.put("isCancelled", isCancelled);

            stateSnap.put("isHandled", false);

            // CRÍTICO: Si el base es un Map (ej: evento normalizado o sintético de runRepeating),
            // copiar su contenido al storage.genericRoot() para que EVENT.custom.* funcione,
            // Y usar MapValueAccessor en el nativeAccessor para que EVENT.action etc funcionen.
            if (base instanceof Map<?, ?> m) {
                try {
                    for (var e : m.entrySet()) {
                        if (e.getKey() == null) continue;
                        String key = String.valueOf(e.getKey());
                        // Ignorar claves internas/metadatos ya manejadas
                        if (key.equals("native") || key.equals("__native") ||
                            key.equals("class") || key.equals("simpleClass") ||
                            key.equals("type")) {
                            continue;
                        }
                        // Si la clave es exactamente "custom" o "generic" y el valor es un Map,
                        // copiar su CONTENIDO directamente al genericRoot (sin el wrapper)
                        if ((key.equals("custom") || key.equals("generic")) && e.getValue() instanceof Map<?, ?> innerMap) {
                            try {
                                for (var innerEntry : innerMap.entrySet()) {
                                    if (innerEntry.getKey() == null) continue;
                                    String innerKey = String.valueOf(innerEntry.getKey());
                                    net.rollanddeath.smp.core.scripting.scope.NestedMaps.set(
                                        storage.genericRoot(), 
                                        new String[]{innerKey}, 
                                        innerEntry.getValue()
                                    );
                                }
                            } catch (Exception ignored2) {
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }

            ValueAccessor nativeAccessor = path -> {
                Object v = new MapValueAccessor(nativeSnap).get(path);
                if (v != null) return v;
                // Si base es un Map, usar MapValueAccessor para acceder a claves como action, block, etc.
                if (base instanceof Map<?, ?>) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) base;
                    return new MapValueAccessor(map).get(path);
                }
                return new ReflectiveValueAccessor(base).get(path);
            };

            return new DefaultScopeContainer(id, base, storage, nativeAccessor, new MapValueAccessor(stateSnap));
        }
    }
}
