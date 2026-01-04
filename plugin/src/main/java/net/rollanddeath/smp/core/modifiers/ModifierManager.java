package net.rollanddeath.smp.core.modifiers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.modifiers.scripted.ScriptedModifier;
import net.rollanddeath.smp.core.modifiers.scripted.ScriptedModifierDefinition;
import net.rollanddeath.smp.core.modifiers.scripted.ScriptedModifierParser;
import net.rollanddeath.smp.core.scripting.lint.ScriptLinter;
import net.rollanddeath.smp.core.scripting.scope.modifiers.ScopedModifierParser;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.io.File;

public class ModifierManager {

    private final RollAndDeathSMP plugin;
    private final Map<String, Modifier> registeredModifiers = new HashMap<>();
    private final Set<String> activeModifiers = new HashSet<>();
    private final List<String> eventHistory = new ArrayList<>();
    private final Random random = new Random();
    private int lastRouletteDay = -1;
    private boolean spinning = false;
    private boolean restoredAtStartup = false;

    public ModifierManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        loadModifierState();
        loadScriptedModifiers();
    }

    private void loadScriptedModifiers() {
        try {
            if (!plugin.getDataFolder().exists()) {
                //noinspection ResultOfMethodCallIgnored
                plugin.getDataFolder().mkdirs();
            }

            File file = new File(plugin.getDataFolder(), "modifiers.yml");
            if (!file.exists()) {
                plugin.saveResource("modifiers.yml", false);
            }

            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

            // Lint temprano para detectar referencias inválidas en scripts
            ScriptLinter.logIssues(plugin.getLogger(), ScriptLinter.lintConfiguration("modifiers.yml", cfg));

            Map<String, ScriptedModifierDefinition> defs = ScriptedModifierParser.parseAll(cfg);
            for (ScriptedModifierDefinition def : defs.values()) {
                ScriptedModifier mod = new ScriptedModifier(plugin, def.name(), def.type(), def.description(), def.events());
                registerModifier(mod);
            }

            // Scoped modifiers (PLAYER/WORLD/CHUNK/GLOBAL/TEAM/EVENT)
            try {
                var scoped = ScopedModifierParser.parseAll(cfg);
                plugin.getScopeRegistry().setScopedModifiers(scoped);
                int total = scoped.values().stream().mapToInt(java.util.List::size).sum();
                if (total > 0) {
                    plugin.getLogger().info("Cargados scoped_modifiers desde modifiers.yml: " + total);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("No se pudo cargar scoped_modifiers: " + e.getMessage());
            }

            if (!defs.isEmpty()) {
                plugin.getLogger().info("Cargados modifiers scripted desde modifiers.yml: " + defs.size());
            }
        } catch (Exception e) {
            plugin.getLogger().warning("No se pudo cargar modifiers.yml: " + e.getMessage());
        }
    }

    /**
     * Recarga los modifiers desde modifiers.yml sin afectar los modifiers actualmente activos.
     * Los nuevos modifiers estarán disponibles para la próxima ruleta.
     */
    public void loadAndRegister() {
        // Limpiar solo los modifiers scripted registrados (mantener los activos)
        registeredModifiers.entrySet().removeIf(entry -> entry.getValue() instanceof ScriptedModifier);
        
        // Recargar desde archivo
        loadScriptedModifiers();
    }

    private void loadModifierState() {
        List<String> savedActive = plugin.getConfig().getStringList("game.active_modifiers");
        if (savedActive != null) {
            activeModifiers.addAll(savedActive);
        }

        // Cleanup removed modifiers
        activeModifiers.remove("Bloques Random");

        List<String> savedHistory = plugin.getConfig().getStringList("game.event_history");
        if (savedHistory != null) {
            eventHistory.addAll(savedHistory);
        }

        lastRouletteDay = plugin.getConfig().getInt("game.last_modifier_day", -1);
    }

    private void persistModifierState() {
        plugin.getConfig().set("game.active_modifiers", new ArrayList<>(activeModifiers));
        plugin.getConfig().set("game.event_history", new ArrayList<>(eventHistory));
        plugin.getConfig().set("game.last_modifier_day", lastRouletteDay);
        plugin.saveConfig();
    }

    public void registerModifier(Modifier modifier) {
        if (registeredModifiers.containsKey(modifier.getName())) {
            plugin.getLogger().warning("Modifier duplicado ignorado: " + modifier.getName());
            return;
        }

        registeredModifiers.put(modifier.getName(), modifier);
    }

    /**
     * Restaura (reactiva) los modificadores persistidos como activos.
     *
     * Importante: debe llamarse cuando el plugin ya terminó de inicializar managers
     * que los scripts puedan necesitar (ej: ProtectionManager para "purge").
     */
    public void restoreActiveModifiers() {
        if (restoredAtStartup) return;
        restoredAtStartup = true;

        for (String name : new HashSet<>(activeModifiers)) {
            Modifier mod = registeredModifiers.get(name);
            if (mod == null) {
                plugin.getLogger().warning("Modificador activo persistido no encontrado: " + name);
                continue;
            }
            try {
                mod.onEnable();
                plugin.getLogger().info("Restaurando modificador activo: " + mod.getName());
            } catch (Exception e) {
                plugin.getLogger().warning("No se pudo restaurar modificador activo: " + mod.getName() + " -> " + e.getMessage());
            }
        }
    }

    public void activateModifier(String name) {
        Modifier mod = registeredModifiers.get(name);
        if (mod != null && !activeModifiers.contains(name)) {
            mod.onEnable();
            activeModifiers.add(name);
            eventHistory.add(mod.getName());
            persistModifierState();
            plugin.getLogger().info("Modificador activado: " + name);

            announceModifier(mod);
        }
    }

    public void deactivateModifier(String name) {
        Modifier mod = registeredModifiers.get(name);
        
        if (mod != null) {
            mod.onDisable();
        }

        if (activeModifiers.contains(name)) {
            activeModifiers.remove(name);
            persistModifierState();
            
            if (mod != null) {
                plugin.getLogger().info("Modificador desactivado: " + name);
                Bukkit.broadcast(Component.text("Evento finalizado: " + mod.getName(), NamedTextColor.GREEN));
                var service = plugin.getDiscordService();
                if (service != null && service.isEnabled()) {
                    service.sendEventAnnouncement("Evento finalizado", "El evento " + mod.getName() + " ha concluido.", NamedTextColor.GREEN);
                }
            } else {
                plugin.getLogger().info("Modificador desconocido eliminado de la lista de activos: " + name);
            }
        }
    }

    public void clearAllModifiers() {
        for (String name : new HashSet<>(activeModifiers)) {
            deactivateModifier(name);
        }
    }

    public boolean isActive(String name) {
        return activeModifiers.contains(name);
    }

    public Set<String> getActiveModifiers() {
        return new HashSet<>(activeModifiers);
    }

    public List<String> getEventHistory() {
        return new ArrayList<>(eventHistory);
    }

    public Modifier getModifier(String name) {
        return registeredModifiers.get(name);
    }

    public Set<String> getRegisteredModifierNames() {
        return registeredModifiers.keySet();
    }

    public int getLastRouletteDay() {
        return lastRouletteDay;
    }

    public Modifier startRandomModifier() {
        List<Modifier> available = new ArrayList<>();
        for (Modifier mod : registeredModifiers.values()) {
            if (!activeModifiers.contains(mod.getName())) {
                available.add(mod);
            }
        }
        
        if (available.isEmpty()) {
            Bukkit.broadcast(Component.text("¡Todos los eventos posibles ya están activos!", NamedTextColor.RED));
            return null;
        }
        
        Modifier randomMod = available.get(random.nextInt(available.size()));
        activateModifier(randomMod.getName());
        return randomMod;
    }

    public void spinRoulette() {
        int today = plugin.getGameManager().getCurrentDay();
        if (today <= lastRouletteDay) {
            Bukkit.broadcast(Component.text("La ruleta ya se giró hoy.", NamedTextColor.YELLOW));
            return;
        }

        if (spinning) {
            Bukkit.broadcast(Component.text("La ruleta sigue girando...", NamedTextColor.GRAY));
            return;
        }

        List<Modifier> available = new ArrayList<>(registeredModifiers.values());
        if (available.isEmpty()) return;

        spinning = true;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Animation loop
                for (int i = 0; i < 10; i++) {
                    Modifier temp = available.get(random.nextInt(available.size()));
                    Title title = Title.title(
                        Component.text(temp.getName(), NamedTextColor.GRAY),
                        Component.text("Girando...", NamedTextColor.YELLOW),
                        Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(200), Duration.ofMillis(0))
                    );
                    
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.getServer().showTitle(title);
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1f, 1f);
                        }
                    });
                    
                    Thread.sleep(200 + (i * 50)); // Slow down
                }
                
                // Final selection and feedback
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Modifier selected = startRandomModifier();
                    if (selected != null) {
                        lastRouletteDay = today;
                        persistModifierState();
                        plugin.getGameManager().markEventExecuted();
                    }

                    spinning = false;

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    }
                });

            } catch (InterruptedException e) {
                spinning = false;
                e.printStackTrace();
            }
        });
    }

    private void announceModifier(Modifier mod) {
        String type = mod.getType();
        NamedTextColor typeColor = getTypeColor(type);
        Component titleMain = Component.text("[Evento]", typeColor);
        Component titleSub = Component.text(getTypeDisplayName(type) + " - " + mod.getName(), NamedTextColor.WHITE);
        Title title = Title.title(
            titleMain,
            titleSub,
            Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(5000), Duration.ofMillis(1000))
        );

        Bukkit.getServer().showTitle(title);

        Component chatMessage = Component.text("[Evento] ", NamedTextColor.LIGHT_PURPLE)
            .append(Component.text(getTypeDisplayName(type), typeColor))
            .append(Component.text(" - " + mod.getName(), NamedTextColor.WHITE));

        Bukkit.broadcast(chatMessage);
        Bukkit.broadcast(Component.text(mod.getDescription(), NamedTextColor.GRAY));

        Sound sound = getTypeSound(type);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), sound, 1f, 1f);
        }

        var service = plugin.getDiscordService();
        if (service != null && service.isEnabled()) {
            service.sendEventAnnouncement(getTypeDisplayName(type) + " - " + mod.getName(), mod.getDescription(), typeColor);
        }
    }

    private NamedTextColor getTypeColor(String type) {
        return switch (type) {
            case "CURSE" -> NamedTextColor.DARK_PURPLE;
            case "CHAOS" -> NamedTextColor.GOLD;
            case "BLESSING" -> NamedTextColor.AQUA;
            default -> NamedTextColor.GRAY;
        };
    }

    private String getTypeDisplayName(String type) {
        return switch (type) {
            case "CURSE" -> "Maldición";
            case "CHAOS" -> "Caos";
            case "BLESSING" -> "Bendición";
            default -> type;
        };
    }

    private Sound getTypeSound(String type) {
        return switch (type) {
            case "CURSE" -> Sound.ENTITY_ENDER_DRAGON_GROWL;
            case "CHAOS" -> Sound.ENTITY_WITHER_SPAWN;
            case "BLESSING" -> Sound.UI_TOAST_CHALLENGE_COMPLETE;
            default -> Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        };
    }
}
