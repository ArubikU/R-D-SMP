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
    protected final String id;
    protected final NamespacedKey key;

    public CustomItem(RollAndDeathSMP plugin, String id) {
        this.plugin = plugin;
        this.id = id;
        this.key = new NamespacedKey(plugin, "custom_item_id");
    }

    public String getId() {
        return id;
    }

    public ItemStack getItemStack() {
        return getItemStack(null);
    }

    public ItemStack getItemStack(java.util.Map<String, Object> extraPdc) {
        ItemStack item = createBaseItem();
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.displayName(MiniMessage.miniMessage().deserialize("<!i><white>" + getDisplayName()));
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id);

            Integer cmd = getCustomModelData();
            if (cmd != null) {
                meta.setCustomModelData(cmd);
            }
            
            applyExtraPdc(meta, extraPdc);

            List<String> lore = getLore();
            if (lore != null && !lore.isEmpty()) {
                meta.lore(lore.stream()
                        .map(line -> MiniMessage.miniMessage().deserialize("<!i><gray>" + replacePlaceholders(line, meta)))
                        .toList());
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }

    protected void applyExtraPdc(ItemMeta meta, java.util.Map<String, Object> extraPdc) {
        // Default implementation does nothing, override in ScriptedItem
    }

    protected String replacePlaceholders(String line, ItemMeta meta) {
        return line; // Default implementation does nothing
    }

    protected abstract ItemStack createBaseItem();
    
    protected abstract List<String> getLore();
    
    public abstract String getDisplayName();
    
    protected abstract Integer getCustomModelData();

    /** Rol requerido para usar/craftear el ítem. Por defecto ninguno. */
    public RoleType getRequiredRoleType() {
        return null;
    }
    
    protected boolean isItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        String itemId = item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        return id.equals(itemId);
    }
}
