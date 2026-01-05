package net.rollanddeath.smp.core.hunters;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Gestiona las recompensas publicadas en /cazadores.
 */
public class BountyManager {

    private final RollAndDeathSMP plugin;
    private final Map<UUID, List<BountyReward>> bounties = new HashMap<>();
    private final File dataFile;

    public BountyManager(RollAndDeathSMP plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "bounties.yml");
        plugin.getDataFolder().mkdirs();
        load();
    }

    public synchronized void addBounty(Player contributor, OfflinePlayer target, ItemStack reward) {
        ItemStack stored = reward.clone();
        List<BountyReward> list = bounties.computeIfAbsent(target.getUniqueId(), k -> new ArrayList<>());
        list.add(new BountyReward(contributor.getUniqueId(), contributor.getName(), stored, System.currentTimeMillis()));
        save();
    }

    /**
     * Elimina y devuelve las recompensas que el jugador puso para un objetivo concreto.
     * @return número de entradas devueltas (no suma de ítems).
     */
    public synchronized int cancelBounties(Player contributor, OfflinePlayer target) {
        List<BountyReward> list = bounties.get(target.getUniqueId());
        if (list == null || list.isEmpty()) return 0;

        int removed = 0;
        List<BountyReward> remaining = new ArrayList<>();
        for (BountyReward reward : list) {
            if (reward.contributor().equals(contributor.getUniqueId())) {
                giveOrDrop(contributor, reward.item());
                removed++;
            } else {
                remaining.add(reward);
            }
        }

        if (remaining.isEmpty()) {
            bounties.remove(target.getUniqueId());
        } else {
            bounties.put(target.getUniqueId(), remaining);
        }

        if (removed > 0) {
            save();
        }
        return removed;
    }

    public synchronized Map<UUID, List<BountyReward>> snapshot() {
        Map<UUID, List<BountyReward>> copy = new HashMap<>();
        bounties.forEach((key, value) -> copy.put(key, new ArrayList<>(value)));
        return copy;
    }

    public synchronized List<BountyReward> getBountiesFor(UUID target) {
        return bounties.getOrDefault(target, List.of());
    }

    /**
     * Entrega las recompensas al killer (o las deja caer) y limpia la entrada.
     */
    public synchronized void payout(Player killer, Player victim) {
        List<BountyReward> rewards = bounties.remove(victim.getUniqueId());
        if (rewards == null || rewards.isEmpty()) return;

        for (BountyReward reward : rewards) {
            ItemStack copy = reward.item().clone();
            if (killer != null) {
                giveOrDrop(killer, copy);
            } else {
                victim.getWorld().dropItemNaturally(victim.getLocation(), copy);
            }
        }
        save();

        String killerName = killer != null ? killer.getName() : "(desconocido)";
        String victimName = victim.getName();
        String summary = summarize(rewards);
        plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(
                "<gold>⚔ <yellow>" + killerName + " cobró la recompensa por <red>" + victimName + "</red>: <green>" + summary));
    }

    private void giveOrDrop(Player player, ItemStack item) {
        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(item);
        if (!leftovers.isEmpty()) {
            leftovers.values().forEach(stack -> player.getWorld().dropItemNaturally(player.getLocation(), stack));
            player.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>Inventario lleno: se tiraron recompensas al suelo."));
        }
    }

    private String summarize(List<BountyReward> rewards) {
        Map<String, Integer> totals = new HashMap<>();
        for (BountyReward reward : rewards) {
            String key = reward.item().getType().name().toLowerCase(Locale.ROOT);
            totals.merge(key, reward.item().getAmount(), Integer::sum);
        }
        return totals.entrySet().stream()
                .map(e -> e.getValue() + "x " + e.getKey())
                .limit(5)
                .reduce((a, b) -> a + ", " + b)
                .orElse("sin detalles");
    }

    private void load() {
        if (!dataFile.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection targets = cfg.getConfigurationSection("targets");
        if (targets == null) return;

        for (String targetKey : targets.getKeys(false)) {
            UUID targetId;
            try {
                targetId = UUID.fromString(targetKey);
            } catch (IllegalArgumentException e) {
                continue;
            }

            ConfigurationSection targetSec = targets.getConfigurationSection(targetKey);
            if (targetSec == null) continue;
            ConfigurationSection rewardsSec = targetSec.getConfigurationSection("rewards");
            if (rewardsSec == null) continue;

            List<BountyReward> list = new ArrayList<>();
            for (String rewardKey : rewardsSec.getKeys(false)) {
                ConfigurationSection rewardSec = rewardsSec.getConfigurationSection(rewardKey);
                if (rewardSec == null) continue;
                String contributorStr = rewardSec.getString("contributor");
                if (contributorStr == null) continue;
                UUID contributor;
                try {
                    contributor = UUID.fromString(contributorStr);
                } catch (IllegalArgumentException ex) {
                    continue;
                }
                String contributorName = rewardSec.getString("contributorName", "?");
                ItemStack item = rewardSec.getItemStack("item");
                if (item == null || item.getType().isAir()) continue;
                long createdAt = rewardSec.getLong("createdAt", System.currentTimeMillis());
                list.add(new BountyReward(contributor, contributorName, item, createdAt));
            }

            if (!list.isEmpty()) {
                bounties.put(targetId, list);
            }
        }
    }

    private void save() {
        YamlConfiguration cfg = new YamlConfiguration();
        ConfigurationSection targets = cfg.createSection("targets");

        for (Map.Entry<UUID, List<BountyReward>> entry : bounties.entrySet()) {
            ConfigurationSection targetSec = targets.createSection(entry.getKey().toString());
            ConfigurationSection rewardsSec = targetSec.createSection("rewards");
            int idx = 0;
            for (BountyReward reward : entry.getValue()) {
                ConfigurationSection rewardSec = rewardsSec.createSection(String.valueOf(idx++));
                rewardSec.set("contributor", reward.contributor().toString());
                rewardSec.set("contributorName", reward.contributorName());
                rewardSec.set("createdAt", reward.createdAt());
                rewardSec.set("item", reward.item());
            }
        }

        try {
            cfg.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("No se pudo guardar bounties.yml: " + e.getMessage());
        }
    }

    public record BountyReward(UUID contributor, String contributorName, ItemStack item, long createdAt) {
        public BountyReward {
            Objects.requireNonNull(contributor, "contributor");
            Objects.requireNonNull(item, "item");
            Objects.requireNonNull(contributorName, "contributorName");
        }
    }
}
