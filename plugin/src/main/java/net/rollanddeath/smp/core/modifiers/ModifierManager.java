package net.rollanddeath.smp.core.modifiers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.rollanddeath.smp.RollAndDeathSMP;
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

public class ModifierManager {

    private final RollAndDeathSMP plugin;
    private final Map<String, Modifier> registeredModifiers = new HashMap<>();
    private final Set<String> activeModifiers = new HashSet<>();
    private final List<String> eventHistory = new ArrayList<>();
    private final Random random = new Random();

    public ModifierManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        loadModifierState();
    }

    private void loadModifierState() {
        List<String> savedActive = plugin.getConfig().getStringList("game.active_modifiers");
        if (savedActive != null) {
            activeModifiers.addAll(savedActive);
        }

        List<String> savedHistory = plugin.getConfig().getStringList("game.event_history");
        if (savedHistory != null) {
            eventHistory.addAll(savedHistory);
        }
    }

    private void persistModifierState() {
        plugin.getConfig().set("game.active_modifiers", new ArrayList<>(activeModifiers));
        plugin.getConfig().set("game.event_history", new ArrayList<>(eventHistory));
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
                
                // Final selection and feedback
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Modifier selected = startRandomModifier();
                    if (selected != null) {
                        plugin.getGameManager().markEventExecuted();
                    }

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private void announceModifier(Modifier mod) {
        ModifierType type = mod.getType();
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

    private NamedTextColor getTypeColor(ModifierType type) {
        return switch (type) {
            case CURSE -> NamedTextColor.DARK_PURPLE;
            case CHAOS -> NamedTextColor.GOLD;
            case BLESSING -> NamedTextColor.AQUA;
        };
    }

    private String getTypeDisplayName(ModifierType type) {
        return switch (type) {
            case CURSE -> "Maldición";
            case CHAOS -> "Caos";
            case BLESSING -> "Bendición";
        };
    }

    private Sound getTypeSound(ModifierType type) {
        return switch (type) {
            case CURSE -> Sound.ENTITY_ENDER_DRAGON_GROWL;
            case CHAOS -> Sound.ENTITY_WITHER_SPAWN;
            case BLESSING -> Sound.UI_TOAST_CHALLENGE_COMPLETE;
        };
    }
}
