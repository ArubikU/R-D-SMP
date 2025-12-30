package net.rollanddeath.smp.core.mobs;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DailyMobRotationManager {

    private final RollAndDeathSMP plugin;
    private final List<String> rotationOrder = new ArrayList<>();
    private final List<String> activeMobs = new ArrayList<>();
    private final Random random = new Random();
    private int lastDay = -1;

    public DailyMobRotationManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        loadState();
    }

    private void loadState() {
        // Load rotation order
        List<String> savedOrder = plugin.getConfig().getStringList("game.mob_rotation_order");
        if (savedOrder == null || savedOrder.isEmpty()) {
            // Initialize random order if not exists
            List<String> allMobs = new ArrayList<>();
            if (plugin.getMobManager() != null) {
                allMobs.addAll(plugin.getMobManager().getMobIds());
            }
            Collections.shuffle(allMobs, new Random(plugin.getConfig().getLong("game.seed", System.currentTimeMillis())));
            rotationOrder.addAll(allMobs);
            
            // Save the order
            plugin.getConfig().set("game.mob_rotation_order", rotationOrder);
            plugin.saveConfig();
        } else {
            rotationOrder.addAll(savedOrder);
            
            // If new mobs were added but not in config, append them
            if (plugin.getMobManager() != null) {
                for (String id : plugin.getMobManager().getMobIds()) {
                    if (!rotationOrder.contains(id)) {
                        rotationOrder.add(id);
                    }
                }
            }
        }

        // Load active mobs (though we can reconstruct this from day)
        lastDay = plugin.getConfig().getInt("game.last_mob_day", 0);
        refreshForDay(plugin.getGameManager().getCurrentDay());
    }

    public void refreshForDay(int day) {
        if (rotationOrder.isEmpty()) return;

        // Activos = todos los mobs cuyo spawn_day efectivo <= día
        activeMobs.clear();
        for (int i = 0; i < rotationOrder.size(); i++) {
            String id = rotationOrder.get(i);
            int unlockDay = getEffectiveSpawnDay(id, i);
            if (unlockDay <= day) {
                activeMobs.add(id);
            }
        }
        
        if (day != lastDay) {
            lastDay = day;
            plugin.getConfig().set("game.last_mob_day", lastDay);
            plugin.saveConfig();

            // Anunciar tema del día (si está definido en mobs.yml)
            try {
                var scripted = plugin.getScriptedMobManager();
                String theme = scripted != null ? scripted.getDailyThemeForDay(day) : null;
                if (theme != null && !theme.isBlank()) {
                    Component c;
                    try {
                        c = MiniMessage.miniMessage().deserialize(theme);
                    } catch (Exception ignored) {
                        c = Component.text(theme, NamedTextColor.GOLD);
                    }
                    plugin.getServer().broadcast(Component.text("Tema del día: ", NamedTextColor.GOLD).append(c));
                }
            } catch (Exception ignored) {
            }
            
            // Announce mobs desbloqueados hoy (pueden ser varios si spawn_day fue configurado)
            for (int i = 0; i < rotationOrder.size(); i++) {
                String id = rotationOrder.get(i);
                int unlockDay = getEffectiveSpawnDay(id, i);
                if (unlockDay == day) {
                    CustomMob mob = plugin.getMobManager().getMob(id);
                    String name = mob != null ? mob.getDisplayName() : id;
                    plugin.getServer().broadcast(net.kyori.adventure.text.Component.text(
                        "¡Nueva amenaza detectada! " + name + " ha entrado al mundo.",
                        net.kyori.adventure.text.format.NamedTextColor.RED
                    ));
                }
            }
        }
    }

    private int getEffectiveSpawnDay(String mobId, int rotationIndex) {
        int fallback = Math.max(1, rotationIndex + 1);
        try {
            var scripted = plugin.getScriptedMobManager();
            if (scripted == null || scripted.runtime() == null) return fallback;
            var def = scripted.runtime().getDefinition(mobId);
            if (def == null || def.spawnDay() == null) return fallback;
            return Math.max(1, def.spawnDay());
        } catch (Exception ignored) {
            return fallback;
        }
    }

    public List<String> getActiveMobs() {
        return new ArrayList<>(activeMobs);
    }

    public int getLastDay() {
        return lastDay;
    }

    public String getReplacement(EntityType vanillaType) {
        List<String> candidates = new ArrayList<>();
        
        for (String id : activeMobs) {
            CustomMob mob = plugin.getMobManager().getMob(id);
            if (mob != null && mob.getEntityType() == vanillaType) {
                candidates.add(id);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        // If multiple candidates, pick one randomly
        return candidates.get(random.nextInt(candidates.size()));
    }
    
    public double getSpawnChance(String mobId) {
        // Override desde mobs.yml si existe
        try {
            var scripted = plugin.getScriptedMobManager();
            if (scripted != null && scripted.runtime() != null) {
                var def = scripted.runtime().getDefinition(mobId);
                if (def != null && def.spawnRate() != null) {
                    return Math.max(0.0, Math.min(1.0, def.spawnRate()));
                }
            }
        } catch (Exception ignored) {
        }

        return 0.1;
    }
}
