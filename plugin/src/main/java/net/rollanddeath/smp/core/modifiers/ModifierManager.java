package net.rollanddeath.smp.core.modifiers;

import org.bukkit.plugin.java.JavaPlugin;
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
    }

    public void registerModifier(Modifier modifier) {
        registeredModifiers.put(modifier.getName(), modifier);
    }

    public void activateModifier(String name) {
        Modifier mod = registeredModifiers.get(name);
        if (mod != null && !activeModifiers.contains(name)) {
            mod.onEnable();
            activeModifiers.add(name);
            plugin.getLogger().info("Modificador activado: " + name);
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

    public void startRandomModifier() {
        List<Modifier> available = new ArrayList<>(registeredModifiers.values());
        if (available.isEmpty()) return;
        
        // Filter out already active ones if we want unique daily events?
        // For now, just pick one.
        Modifier randomMod = available.get(random.nextInt(available.size()));
        activateModifier(randomMod.getName());
    }
}
