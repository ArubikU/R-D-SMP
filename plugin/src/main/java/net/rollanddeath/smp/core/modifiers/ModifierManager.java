package net.rollanddeath.smp.core.modifiers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ModifierManager {

    private final JavaPlugin plugin;
    private final Map<String, Modifier> registeredModifiers = new HashMap<>();
    private final Set<String> activeModifiers = new HashSet<>();
    private final Random random = new Random();

    public ModifierManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadActiveModifiers();
    }

    private void loadActiveModifiers() {
        List<String> saved = plugin.getConfig().getStringList("game.active_modifiers");
        if (saved != null) {
            activeModifiers.addAll(saved);
        }
    }

    private void saveActiveModifiers() {
        plugin.getConfig().set("game.active_modifiers", new ArrayList<>(activeModifiers));
        plugin.saveConfig();
    }

    public void registerModifier(Modifier modifier) {
        registeredModifiers.put(modifier.getName(), modifier);
        if (activeModifiers.contains(modifier.getName())) {
            modifier.onEnable();
            plugin.getLogger().info("Restaurando modificador activo: " + modifier.getName());
        }
    }

    public void activateModifier(String name) {
        Modifier mod = registeredModifiers.get(name);
        if (mod != null && !activeModifiers.contains(name)) {
            mod.onEnable();
            activeModifiers.add(name);
            saveActiveModifiers();
            plugin.getLogger().info("Modificador activado: " + name);
            
            // Announce to players
            Title title = Title.title(
                Component.text(mod.getName(), NamedTextColor.RED),
                Component.text(mod.getDescription(), NamedTextColor.YELLOW),
                Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(5000), Duration.ofMillis(1000))
            );
            Bukkit.getServer().showTitle(title);
            Bukkit.broadcast(Component.text("¡Nuevo Evento: " + mod.getName() + "!", NamedTextColor.GOLD));
            Bukkit.broadcast(Component.text(mod.getDescription(), NamedTextColor.YELLOW));
        }
    }

    public void deactivateModifier(String name) {
        Modifier mod = registeredModifiers.get(name);
        
        if (mod != null) {
            mod.onDisable();
        }

        if (activeModifiers.contains(name)) {
            activeModifiers.remove(name);
            saveActiveModifiers();
            
            if (mod != null) {
                plugin.getLogger().info("Modificador desactivado: " + name);
                Bukkit.broadcast(Component.text("Evento finalizado: " + mod.getName(), NamedTextColor.GREEN));
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

    public Modifier getModifier(String name) {
        return registeredModifiers.get(name);
    }

    public Set<String> getRegisteredModifierNames() {
        return registeredModifiers.keySet();
    }

    public void startRandomModifier() {
        List<Modifier> available = new ArrayList<>();
        for (Modifier mod : registeredModifiers.values()) {
            if (!activeModifiers.contains(mod.getName())) {
                available.add(mod);
            }
        }
        
        if (available.isEmpty()) {
            Bukkit.broadcast(Component.text("¡Todos los eventos posibles ya están activos!", NamedTextColor.RED));
            return;
        }
        
        Modifier randomMod = available.get(random.nextInt(available.size()));
        activateModifier(randomMod.getName());
    }

    public void spinRoulette() {
        List<Modifier> available = new ArrayList<>(registeredModifiers.values());
        if (available.isEmpty()) return;

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
                
                // Final selection
                Bukkit.getScheduler().runTask(plugin, this::startRandomModifier);
                
                Bukkit.getScheduler().runTask(plugin, () -> {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
