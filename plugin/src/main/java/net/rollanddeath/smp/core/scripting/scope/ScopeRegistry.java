package net.rollanddeath.smp.core.scripting.scope;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.teams.Team;
import net.rollanddeath.smp.core.scripting.scope.modifiers.ScopedModifier;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ScopeRegistry {

    private final RollAndDeathSMP plugin;

    private final ScopeContainerFactory containerFactory;

    private final ScopeStorage globalStorage = new ScopeStorage();

    private final Map<UUID, ScopeStorage> players = new ConcurrentHashMap<>();
    private final Map<UUID, ScopeStorage> worlds = new ConcurrentHashMap<>();
    private final Map<ChunkKey, ScopeStorage> chunks = new ConcurrentHashMap<>();
    private final Map<String, ScopeStorage> teams = new ConcurrentHashMap<>();

    private final Map<ScopeId, List<ScopedModifier>> scopedModifiers = new EnumMap<>(ScopeId.class);

    public ScopeRegistry(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.containerFactory = new ScopeContainerFactory(plugin);
    }

    public void setScopedModifiers(Map<ScopeId, List<ScopedModifier>> modifiersByScope) {
        scopedModifiers.clear();
        if (modifiersByScope != null) {
            scopedModifiers.putAll(modifiersByScope);
        }
    }

    public Map<ScopeId, List<ScopedModifier>> scopedModifiers() {
        return scopedModifiers;
    }

    public ScopeContainer global() {
        return containerFactory.create(ScopeId.GLOBAL, plugin, globalStorage);
    }

    public ScopeContainer player(Player player) {
        ScopeStorage storage = players.computeIfAbsent(player.getUniqueId(), ignored -> new ScopeStorage());
        return containerFactory.create(ScopeId.PLAYER, player, storage);
    }

    public ScopeContainer world(World world) {
        ScopeStorage storage = worlds.computeIfAbsent(world.getUID(), ignored -> new ScopeStorage());
        return containerFactory.create(ScopeId.WORLD, world, storage);
    }

    public ScopeContainer location(Location location) {
        // LOCATION es efímero: base por contexto
        return containerFactory.create(ScopeId.LOCATION, location, new ScopeStorage());
    }

    public ScopeContainer chunk(Chunk chunk) {
        ChunkKey key = new ChunkKey(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ());
        ScopeStorage storage = chunks.computeIfAbsent(key, ignored -> new ScopeStorage());
        return containerFactory.create(ScopeId.CHUNK, chunk, storage);
    }

    public ScopeContainer team(Team team) {
        ScopeStorage storage = teams.computeIfAbsent(team.getName().toLowerCase(), ignored -> new ScopeStorage());
        return containerFactory.create(ScopeId.TEAM, team, storage);
    }

    public ScopeContainer event(Object eventBase) {
        // EVENT es efímero: storage por contexto
        return containerFactory.create(ScopeId.EVENT, eventBase, new ScopeStorage());
    }

    public ScopeContainer item(ItemStack item) {
        // ITEM es efímero: base por contexto
        return containerFactory.create(ScopeId.ITEM, item, new ScopeStorage());
    }

    public ScopeContainer subject(Object base) {
        // SUBJECT es efímero: base por contexto
        return containerFactory.create(ScopeId.SUBJECT, base, new ScopeStorage());
    }

    public ScopeContainer target(Object base) {
        // TARGET es efímero: base por contexto
        return containerFactory.create(ScopeId.TARGET, base, new ScopeStorage());
    }

    public ScopeContainer projectile(Object base) {
        // PROJECTILE es efímero: base por contexto
        return containerFactory.create(ScopeId.PROJECTILE, base, new ScopeStorage());
    }

    private record ChunkKey(UUID worldId, int x, int z) {
    }
}
