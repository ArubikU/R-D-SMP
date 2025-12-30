package net.rollanddeath.smp.core.items.impl;

import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GrapplingHook extends CustomItem {

    private static final long COOLDOWN_MS = 3000L;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public GrapplingHook(RollAndDeathSMP plugin) {
        super(plugin, "GRAPPLING_HOOK");
    }

    @Override
    protected ItemStack createBaseItem() {
        return new ItemStack(Material.FISHING_ROD);
    }

    @Override
    public String getDisplayName() {
        return "Gancho de Agarre";
    }

    @Override
    protected Integer getCustomModelData() {
        return 710014;
    }

    @Override
    protected List<String> getLore() {
        return List.of("Te permite escalar paredes verticales.");
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isItem(item)) {
            item = player.getInventory().getItemInOffHand();
            if (!isItem(item)) return;
        }

        if (event.getState() == PlayerFishEvent.State.REEL_IN || event.getState() == PlayerFishEvent.State.IN_GROUND || event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY) {
            long now = System.currentTimeMillis();
            long nextAllowed = cooldowns.getOrDefault(player.getUniqueId(), 0L);
            if (now < nextAllowed) {
                return;
            }

            cooldowns.put(player.getUniqueId(), now + COOLDOWN_MS);
            Location hookLoc = event.getHook().getLocation();
            Location playerLoc = player.getLocation();
            
            Vector direction = hookLoc.toVector().subtract(playerLoc.toVector());
            
            player.setVelocity(direction.normalize().multiply(1.5));
        }
    }
}
