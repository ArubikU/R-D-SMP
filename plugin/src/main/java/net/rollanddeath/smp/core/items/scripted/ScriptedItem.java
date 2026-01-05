package net.rollanddeath.smp.core.items.scripted;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.rollanddeath.smp.RollAndDeathSMP;
import net.rollanddeath.smp.core.items.CustomItem;
import net.rollanddeath.smp.core.modifiers.scripted.ModifierRule;
import net.rollanddeath.smp.core.scripting.ActionResult;
import net.rollanddeath.smp.core.scripting.ScriptContext;
import net.rollanddeath.smp.core.scripting.ScriptEngine;
import net.rollanddeath.smp.core.scripting.ScriptPhase;
import net.rollanddeath.smp.core.scripting.ScriptVars;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;

import org.bukkit.entity.Projectile;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ScriptedItem extends CustomItem {

    private final ScriptedItemDefinition def;

    private static final String PROJECTILE_ITEM_TAG_PREFIX = "rd_item:";

    public ScriptedItem(RollAndDeathSMP plugin, ScriptedItemDefinition def) {
        super(plugin, def.id());
        this.def = def;
    }

    @Override
    public ItemStack getItemStack(Map<String, Object> extraPdc) {
        ItemStack item = createBaseItem();
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            String name = def.displayName() != null && !def.displayName().isBlank() ? def.displayName() : def.id();
            meta.displayName(MiniMessage.miniMessage().deserialize("<!i><white>" + name));
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, def.id());

            Integer cmd = def.customModelData();
            if (cmd != null) {
                meta.setCustomModelData(cmd);
            }

            // Max stack size (Paper). Ignora silenciosamente en Spigot viejo.
            if (def.maxStackSize() != null && def.maxStackSize() > 0) {
                try {
                    meta.setMaxStackSize(def.maxStackSize());
                } catch (NoSuchMethodError ignored) {
                } catch (Exception ignored) {
                }
            }

            // Max damage / durabilidad custom (Paper 1.20.5+). Ignora en versiones antiguas.
            if (def.maxDamage() != null && def.maxDamage() > 0 && meta instanceof org.bukkit.inventory.meta.Damageable damageable) {
                try {
                    damageable.setMaxDamage(def.maxDamage());
                } catch (NoSuchMethodError ignored) {
                } catch (Exception ignored) {
                }
            }

            // Leather armor dye
            if (def.leatherColor() != null && meta instanceof LeatherArmorMeta lam) {
                Color c = parseHexColor(def.leatherColor());
                if (c != null) {
                    lam.setColor(c);
                }
            }

            // Enchantments
            if (def.enchantments() != null && !def.enchantments().isEmpty()) {
                for (ScriptedItemDefinition.EnchantmentSpec e : def.enchantments()) {
                    if (e == null || e.enchantment() == null) continue;
                    int lvl = Math.max(1, e.level());
                    try {
                        meta.addEnchant(e.enchantment(), lvl, true);
                    } catch (Exception ignored) {
                    }
                }
            }

            // Extra PDC entries (Default)
            if (def.pdc() != null && !def.pdc().isEmpty()) {
                for (ScriptedItemDefinition.PdcSpec spec : def.pdc()) {
                    if (spec == null || spec.key() == null || spec.key().isBlank() || spec.value() == null) continue;
                    applyPdcValue(meta, spec.key(), spec.value(), spec.dataType());
                }
            }

            // Extra PDC entries (Runtime)
            if (extraPdc != null && !extraPdc.isEmpty()) {
                for (Map.Entry<String, Object> entry : extraPdc.entrySet()) {
                    applyPdcValue(meta, entry.getKey(), entry.getValue(), null);
                }
            }

            List<String> lore = getLore();
            if (lore != null && !lore.isEmpty()) {
                meta.lore(lore.stream()
                    .map(line -> MiniMessage.miniMessage().deserialize("<!i><gray>" + replacePdcPlaceholders(line, meta)))
                    .toList());
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    private void applyPdcValue(ItemMeta meta, String keyRaw, Object value, String dataType) {
        try {
            NamespacedKey k;
            String raw = keyRaw.trim();
            if (raw.contains(":")) {
                k = NamespacedKey.fromString(raw);
            } else {
                k = new NamespacedKey(plugin, raw);
            }
            if (k == null) return;

            String dt = dataType != null ? dataType.trim().toUpperCase(Locale.ROOT) : null;

            if (dt == null || dt.isBlank()) {
                // Inferencia simple
                if (value instanceof Number) dt = "DOUBLE";
                else if (value instanceof Boolean) dt = "BYTE";
                else dt = "STRING";
            }

            switch (dt) {
                case "STRING" -> meta.getPersistentDataContainer().set(k, PersistentDataType.STRING, String.valueOf(value));
                case "INT", "INTEGER" -> {
                    Integer n = value instanceof Number nn ? nn.intValue() : Integer.parseInt(String.valueOf(value).trim());
                    meta.getPersistentDataContainer().set(k, PersistentDataType.INTEGER, n);
                }
                case "LONG" -> {
                    Long n = value instanceof Number nn ? nn.longValue() : Long.parseLong(String.valueOf(value).trim());
                    meta.getPersistentDataContainer().set(k, PersistentDataType.LONG, n);
                }
                case "DOUBLE" -> {
                    Double n = value instanceof Number nn ? nn.doubleValue() : Double.parseDouble(String.valueOf(value).trim());
                    meta.getPersistentDataContainer().set(k, PersistentDataType.DOUBLE, n);
                }
                case "BYTE", "BOOLEAN" -> {
                    byte b = (value instanceof Boolean bb && bb) ? (byte) 1 : (byte) 0;
                    if (value instanceof Number nn) b = (byte) (nn.intValue() != 0 ? 1 : 0);
                    meta.getPersistentDataContainer().set(k, PersistentDataType.BYTE, b);
                }
                default -> meta.getPersistentDataContainer().set(k, PersistentDataType.STRING, String.valueOf(value));
            }
        } catch (Exception ignored) {
        }
    }

    private String replacePdcPlaceholders(String line, ItemMeta meta) {
        if (line == null || !line.contains("%")) return line;
        String out = line;
        
        // Soporte para %pdc_double:key%, %pdc_percent:key%, %pdc:key% y legacy %pdc_key%
        // Pattern para capturar: %pdc_tipo:key% o %pdc:key% o %pdc_key%
        java.util.regex.Pattern typePattern = java.util.regex.Pattern.compile("%pdc_(double|percent|int|string):([^%]+)%");
        java.util.regex.Matcher tm = typePattern.matcher(out);
        StringBuilder sb1 = new StringBuilder();
        while (tm.find()) {
            String type = tm.group(1);
            String keyName = tm.group(2).trim();
            Object val = getPdcValue(meta, keyName);
            String replacement = formatPdcValue(val, type);
            tm.appendReplacement(sb1, java.util.regex.Matcher.quoteReplacement(replacement));
        }
        tm.appendTail(sb1);
        out = sb1.toString();
        
        // Pattern para %pdc:key% (sin tipo)
        java.util.regex.Pattern simplePattern = java.util.regex.Pattern.compile("%pdc:([^%]+)%");
        java.util.regex.Matcher sm = simplePattern.matcher(out);
        StringBuilder sb2 = new StringBuilder();
        while (sm.find()) {
            String keyName = sm.group(1).trim();
            Object val = getPdcValue(meta, keyName);
            sm.appendReplacement(sb2, val != null ? java.util.regex.Matcher.quoteReplacement(String.valueOf(val)) : "");
        }
        sm.appendTail(sb2);
        out = sb2.toString();
        
        // Legacy pattern para %pdc_key% (retrocompatibilidad)
        java.util.regex.Pattern legacyPattern = java.util.regex.Pattern.compile("%pdc_([a-zA-Z0-9_]+)%");
        java.util.regex.Matcher lm = legacyPattern.matcher(out);
        StringBuilder sb3 = new StringBuilder();
        while (lm.find()) {
            String keyName = lm.group(1);
            Object val = getPdcValue(meta, keyName);
            lm.appendReplacement(sb3, val != null ? java.util.regex.Matcher.quoteReplacement(String.valueOf(val)) : "");
        }
        lm.appendTail(sb3);
        return sb3.toString();
    }
    
    private String formatPdcValue(Object val, String type) {
        if (val == null) return "";
        
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "double" -> {
                if (val instanceof Number n) {
                    yield trimDouble(n.doubleValue());
                }
                yield String.valueOf(val);
            }
            case "percent" -> {
                double d = 0.0;
                if (val instanceof Number n) {
                    d = n.doubleValue();
                } else {
                    try {
                        d = Double.parseDouble(String.valueOf(val).trim());
                    } catch (Exception ignored) {}
                }
                double pct = d * 100.0;
                String sign = pct > 0.0000001 ? "+" : "";
                yield sign + trimDouble(pct) + "%";
            }
            case "int" -> {
                if (val instanceof Number n) {
                    yield String.valueOf(n.intValue());
                }
                yield String.valueOf(val);
            }
            default -> String.valueOf(val);
        };
    }
    
    private static String trimDouble(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return "0";
        String s = String.format(Locale.ROOT, "%.2f", v);
        while (s.contains(".") && (s.endsWith("0") || s.endsWith("."))) {
            s = s.substring(0, s.length() - 1);
        }
        if (s.isBlank()) return "0";
        return s;
    }

    private Object getPdcValue(ItemMeta meta, String keyRaw) {
        try {
            NamespacedKey k;
            if (keyRaw.contains(":")) {
                k = NamespacedKey.fromString(keyRaw);
            } else {
                k = new NamespacedKey(plugin, keyRaw);
            }
            if (k == null) return null;
            
            var pdc = meta.getPersistentDataContainer();
            if (pdc.has(k, PersistentDataType.STRING)) return pdc.get(k, PersistentDataType.STRING);
            if (pdc.has(k, PersistentDataType.DOUBLE)) return pdc.get(k, PersistentDataType.DOUBLE);
            if (pdc.has(k, PersistentDataType.INTEGER)) return pdc.get(k, PersistentDataType.INTEGER);
            if (pdc.has(k, PersistentDataType.LONG)) return pdc.get(k, PersistentDataType.LONG);
            if (pdc.has(k, PersistentDataType.BYTE)) return pdc.get(k, PersistentDataType.BYTE);
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected ItemStack createBaseItem() {
        ItemStack item = new ItemStack(def.baseMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null && def.attributes() != null && !def.attributes().isEmpty()) {
            for (ScriptedItemDefinition.AttributeSpec spec : def.attributes()) {
                if (spec == null || spec.attribute() == null) continue;

                String id = spec.key() != null && !spec.key().isBlank()
                    ? spec.key()
                    : (def.id().toLowerCase(Locale.ROOT) + "_" + spec.attribute().name().toLowerCase(Locale.ROOT));

                AttributeModifier mod = new AttributeModifier(
                    new NamespacedKey(plugin, id),
                    spec.amount(),
                    spec.operation() != null ? spec.operation() : AttributeModifier.Operation.ADD_NUMBER,
                    spec.slot() != null ? spec.slot() : org.bukkit.inventory.EquipmentSlotGroup.MAINHAND
                );
                meta.addAttributeModifier(spec.attribute(), mod);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private static Color parseHexColor(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isBlank()) return null;
        if (s.startsWith("#")) s = s.substring(1);
        if (s.length() != 6) return null;
        try {
            int rgb = Integer.parseInt(s, 16);
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            return Color.fromRGB(r, g, b);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public String getDisplayName() {
        return def.displayName();
    }

    public ScriptedItemDefinition getDefinition() {
        return def;
    }

    @Override
    protected Integer getCustomModelData() {
        return def.customModelData();
    }

    @Override
    protected List<String> getLore() {
        return def.lore();
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        LivingEntity caster = null;
        Projectile projectile = null;
        if (damager instanceof LivingEntity le) {
            caster = le;
        } else if (damager instanceof org.bukkit.entity.Projectile proj) {
            projectile = proj;
            ProjectileSource src = proj.getShooter();
            if (src instanceof LivingEntity le) {
                caster = le;
            }
        }

        if (caster == null) return;

        // Evita que el WAR_HAMMER reprocesse los golpes secundarios (AoE)
        // provocados por damage_nearby_entities, para no duplicar VFX/SFX.
        try {
            if ("WAR_HAMMER".equals(def.id()) && caster.getScoreboardTags().contains("rd_internal:aoe_damage")) {
                return;
            }
        } catch (Exception ignored) {
        }

        // Para daño melee: requiere que el caster tenga el ítem en mainhand.
        // Para proyectiles: permite que el proyectil esté marcado con rd_item:<TYPE>.
        boolean allowByProjectileTag = false;
        if (projectile != null) {
            try {
                allowByProjectileTag = projectile.getScoreboardTags().contains(PROJECTILE_ITEM_TAG_PREFIX + def.id());
            } catch (Exception ignored) {
                allowByProjectileTag = false;
            }
        }

        if (!allowByProjectileTag) {
            EntityEquipment eq = caster.getEquipment();
            ItemStack item = eq != null ? eq.getItemInMainHand() : null;
            if (!isItem(item)) return;
        }

        ItemStack injectedItem = null;
        try {
            EntityEquipment eq = caster.getEquipment();
            ItemStack hand = eq != null ? eq.getItemInMainHand() : null;
            if (isItem(hand)) injectedItem = hand;
        } catch (Exception ignored) {
        }
        if (injectedItem == null) {
            try {
                if (projectile instanceof org.bukkit.entity.Trident t) {
                    ItemStack tri = t.getItemStack();
                    if (isItem(tri)) injectedItem = tri;
                }
            } catch (Exception ignored) {
            }
        }

        ModifierRule projectileHitEntityRule = null;
        if (allowByProjectileTag) {
            projectileHitEntityRule = def.events() != null ? def.events().get("projectile_hit_entity") : null;
        }

        ModifierRule rule = def.events() != null ? def.events().get("entity_damage_by_entity") : null;
        if (projectileHitEntityRule == null && rule == null) return;

        Player player = caster instanceof Player p ? p : null;

        Map<String, Object> vars = ScriptVars.create()
            .event(event)
            .subject(caster)
            .target(event.getEntity())
            .projectile(projectile)
            .item(injectedItem)
            .build();

        // Nuevo evento: dedicado a impactos de proyectil contra entidad.
        // Si existe, lo preferimos para no duplicar lógica con entity_damage_by_entity.
        if (projectileHitEntityRule != null) {
            boolean deny = applyRule(projectileHitEntityRule, player, "projectile_hit_entity", vars);
            if (deny) {
                event.setCancelled(true);
            }
            return;
        }

        boolean deny = applyRule(rule, player, "entity_damage_by_entity", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageTakenByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity victim)) return;

        EntityEquipment eq = victim.getEquipment();
        if (eq == null) return;

        ItemStack main = eq.getItemInMainHand();
        ItemStack off = eq.getItemInOffHand();
        if (!(isItem(main) || isItem(off))) return;

        ItemStack injectedItem = isItem(main) ? main : off;

        ModifierRule rule = def.events() != null ? def.events().get("entity_damage_taken_by_entity") : null;
        if (rule == null) return;

        LivingEntity attacker = null;
        Entity damager = event.getDamager();
        if (damager instanceof LivingEntity le) {
            attacker = le;
        } else if (damager instanceof org.bukkit.entity.Projectile proj) {
            ProjectileSource src = proj.getShooter();
            if (src instanceof LivingEntity le) {
                attacker = le;
            }
        }

        Player player = victim instanceof Player p ? p : null;

        Map<String, Object> vars = ScriptVars.create()
            .event(event)
            .subject(victim)
            .target(attacker)
            .item(injectedItem)
            .build();

        boolean deny = applyRule(rule, player, "entity_damage_taken_by_entity", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        ItemStack item = event.getItem();
        if (!isItem(item)) return;

        ModifierRule rule = def.events() != null ? def.events().get("player_interact") : null;
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
            .event(event)
            .subject(player)
            .item(item)
            .build();

        boolean deny = applyRule(rule, player, "player_interact", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack bow = event.getBow();
        if (!isItem(bow)) return;

        // Tag automático para que projectile_hit / projectile_hit_entity puedan rutearse sin acciones especiales.
        try {
            if (event.getProjectile() instanceof Projectile p) {
                p.addScoreboardTag(PROJECTILE_ITEM_TAG_PREFIX + def.id());
            }
        } catch (Exception ignored) {
        }

        ModifierRule rule = def.events() != null ? def.events().get("entity_shoot_bow") : null;
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
            .event(event)
            .subject(player)
            .projectile(event.getProjectile() instanceof Projectile p ? p : null)
            .item(bow)
            .build();

        boolean deny = applyRule(rule, player, "entity_shoot_bow", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;

        // Matches PoseidonTrident behavior: checks main hand at launch.
        ItemStack hand;
        try {
            hand = player.getInventory().getItemInMainHand();
        } catch (Exception ignored) {
            hand = null;
        }
        if (!isItem(hand)) return;

        // Tag automático para que projectile_hit pueda rutearse incluso si el proyectil no conserva el ItemStack.
        try {
            Projectile p = event.getEntity();
            if (p != null) {
                p.addScoreboardTag(PROJECTILE_ITEM_TAG_PREFIX + def.id());
            }
        } catch (Exception ignored) {
        }

        ModifierRule rule = def.events() != null ? def.events().get("projectile_launch") : null;
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
            .event(event)
            .subject(player)
            .projectile(event.getEntity())
            .item(hand)
            .build();

        boolean deny = applyRule(rule, player, "projectile_launch", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile proj = event.getEntity();
        if (!(proj.getShooter() instanceof Player player)) return;

        // Se activa si el proyectil está marcado con rd_item:<TYPE>.
        boolean matches = false;
        try {
            matches = proj.getScoreboardTags().contains(PROJECTILE_ITEM_TAG_PREFIX + def.id());
        } catch (Exception ignored) {
            matches = false;
        }

        if (!matches) {
            // Fallback: Trident conserva su ItemStack. Permite scripts sin tags.
            try {
                if (proj instanceof org.bukkit.entity.Trident t) {
                    matches = isItem(t.getItemStack());
                }
            } catch (Exception ignored) {
                matches = false;
            }
        }

        if (!matches) return;

        ModifierRule rule = def.events() != null ? def.events().get("projectile_hit") : null;
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
            .event(event)
            .subject(player)
            .target(event.getHitEntity())
            .projectile(proj)
            .item((proj instanceof org.bukkit.entity.Trident t && isItem(t.getItemStack())) ? t.getItemStack() : null)
            .build();

        boolean deny = applyRule(rule, player, "projectile_hit", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        if (event.getTo() == null) return;

        // Evita spam por mover la cámara (yaw/pitch). Solo cuando cambia de bloque.
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
            && event.getFrom().getBlockY() == event.getTo().getBlockY()
            && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isItem(item)) return;

        ModifierRule rule = def.events() != null ? def.events().get("player_move") : null;
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
            .event(event)
            .subject(player)
            .item(item)
            .build();

        boolean deny = applyRule(rule, player, "player_move", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        ItemStack oldItem = player.getInventory().getItem(event.getPreviousSlot());
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());

        boolean oldIs = isItem(oldItem);
        boolean newIs = isItem(newItem);
        if (!(oldIs || newIs)) return;

        boolean deny = false;

        if (newIs) {
            ModifierRule heldRule = def.events() != null ? def.events().get("player_item_held") : null;
            if (heldRule != null) {
                Map<String, Object> heldVars = ScriptVars.create()
                    .event(event)
                    .subject(player)
                    .item(newItem)
                    .build();
                deny = applyRule(heldRule, player, "player_item_held", heldVars) || deny;
            }
        }

        if (oldIs && !newIs) {
            ModifierRule unheldRule = def.events() != null ? def.events().get("player_item_unheld") : null;
            if (unheldRule != null) {
                Map<String, Object> unheldVars = ScriptVars.create()
                    .event(event)
                    .subject(player)
                    .item(oldItem)
                    .build();
                deny = applyRule(unheldRule, player, "player_item_unheld", unheldVars) || deny;
            }
        }

        if (deny) {
            try {
                event.setCancelled(true);
            } catch (Exception ignored) {
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        ItemStack item = event.getItem();
        if (!isItem(item)) return;

        ModifierRule rule = def.events() != null ? def.events().get("player_item_consume") : null;
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
            .event(event)
            .subject(player)
            .item(item)
            .build();

        boolean deny = applyRule(rule, player, "player_item_consume", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (!isItem(item)) return;

        ModifierRule rule = def.events() != null ? def.events().get("block_break") : null;
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
            .event(event)
            .subject(player)
            .item(item)
            .build();

        boolean deny = applyRule(rule, player, "block_break", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityResurrect(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        if (!(isItem(main) || isItem(off))) return;

        ItemStack injectedItem = isItem(main) ? main : off;

        ModifierRule rule = def.events() != null ? def.events().get("entity_resurrect") : null;
        if (rule == null) return;

        Map<String, Object> vars = ScriptVars.create()
            .event(event)
            .subject(player)
            .target(player)
            .item(injectedItem)
            .build();

        boolean deny = applyRule(rule, player, "entity_resurrect", vars);
        if (deny) {
            event.setCancelled(true);
        }
    }

    private boolean applyRule(ModifierRule rule, Player player, String subject, Map<String, Object> vars) {
        String subjectId = "item:" + def.id() + ":" + subject.toLowerCase(Locale.ROOT);
        ScriptContext ctx = new ScriptContext(plugin, player, subjectId, ScriptPhase.ITEM, vars);

        boolean pass = ScriptEngine.allConditionsPass(ctx, rule.requireAll());
        if (pass) {
            ActionResult r = ScriptEngine.runAllWithResult(ctx, rule.onPass());
            return r != null && r.deny();
        }

        ActionResult r = ScriptEngine.runAllWithResult(ctx, rule.onFail());
        return rule.denyOnFail() || (r != null && r.deny());
    }
}
