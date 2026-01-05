package net.rollanddeath.smp.core.shops;

import net.rollanddeath.smp.RollAndDeathSMP;
import org.bukkit.Location;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ShopManager {

    private final ShopStorage storage;
    private final Map<Location, Shop> shops;

    public ShopManager(RollAndDeathSMP plugin) {
        this.storage = new ShopStorage(plugin);
        this.shops = new HashMap<>(storage.load());
    }

    public Map<Location, Shop> getShopsView() {
        return Collections.unmodifiableMap(shops);
    }

    public Shop get(Location location) {
        return shops.get(location);
    }

    public Optional<Shop> getByContainer(Location container) {
        return shops.values().stream()
                .filter(s -> s.getContainerLocation().equals(container))
                .findFirst();
    }

    public void create(Shop shop) {
        shops.put(shop.getSignLocation(), shop);
        save();
    }

    public void remove(Shop shop) {
        shops.remove(shop.getSignLocation());
        save();
    }

    public void save() {
        storage.save(shops);
    }
}
