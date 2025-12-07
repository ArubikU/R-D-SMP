package net.rollanddeath.smp.core.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public abstract class CustomItem implements Listener {

    protected final RollAndDeathSMP plugin;
    protected final CustomItemType type;
    protected final NamespacedKey key;

    public CustomItem(RollAndDeathSMP plugin, CustomItemType type) {
        this.plugin = plugin;
        this.type = type;
        this.key = new NamespacedKey(plugin, "custom_item_id");
    }

    public CustomItemType getType() {
        return type;
    }

    public ItemStack getItemStack() {
        ItemStack item = createBaseItem();
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.displayName(MiniMessage.miniMessage().deserialize("<!i><white>" + type.getDisplayName()));
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, type.name());

            Integer cmd = type.getCustomModelData();
            if (cmd != null) {
                meta.setCustomModelData(cmd);
            }
            
            List<String> lore = getLore();
            if (lore != null && !lore.isEmpty()) {
                meta.lore(lore.stream()
                        .map(line -> MiniMessage.miniMessage().deserialize("<!i><gray>" + line))
                        .toList());
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }

    protected abstract ItemStack createBaseItem();
    
    protected abstract List<String> getLore();

    /** Rol requerido para usar/craftear el ítem. Por defecto ninguno. */
    public RoleType getRequiredRoleType() {
        return null;
    }
    
    protected boolean isItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        String id = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        return type.name().equals(id);
    }
}
