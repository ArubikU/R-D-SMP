package net.rollanddeath.smp.core.mobs.scripted;

import net.rollanddeath.smp.core.modifiers.scripted.ModifierRule;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Map;

public record ScriptedMobDefinition(
    String id,
    EntityType entityType,
    String displayName,
    Integer spawnDay,
    Double spawnRate,
    boolean isBoss,
    Map<String, Double> attributes,
    Equipment equipment,
    Map<String, ModifierRule> events,
    List<LootEntry> loot
) {

    public record Equipment(
        String helmet,
        String chestplate,
        String leggings,
        String boots,
        String mainHand,
        String offHand
    ) {
    }

    public record LootEntry(
        String material,
        String customItem,
        int amount,
        double chance
    ) {
    }
}
