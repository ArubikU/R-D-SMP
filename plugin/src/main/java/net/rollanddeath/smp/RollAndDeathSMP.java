package net.rollanddeath.smp;

import net.rollanddeath.smp.core.LifeManager;
import net.rollanddeath.smp.core.modifiers.ModifierManager;
import net.rollanddeath.smp.core.protection.ProtectionListener;
import net.rollanddeath.smp.core.protection.ProtectionManager;
import net.rollanddeath.smp.core.teams.TeamCommand;
import net.rollanddeath.smp.core.teams.TeamListener;
import net.rollanddeath.smp.core.teams.TeamManager;
import net.rollanddeath.smp.core.teams.WarTrackerTask;
import net.rollanddeath.smp.core.teams.TeamBuffListener;
import net.rollanddeath.smp.events.ChatListener;
import net.rollanddeath.smp.events.PlayerDeathListener;
import net.rollanddeath.smp.core.roles.RoleManager;
import net.rollanddeath.smp.core.roles.RoleCommand;
import net.rollanddeath.smp.core.roles.impl.*;
import net.rollanddeath.smp.core.items.ItemManager;
import net.rollanddeath.smp.core.items.ItemCommand;
import net.rollanddeath.smp.core.items.RecipeManager;
import net.rollanddeath.smp.core.items.LootManager;
import net.rollanddeath.smp.core.items.scripted.ScriptedItemManager;
import net.rollanddeath.smp.core.mobs.MobManager;
import net.rollanddeath.smp.core.mobs.DailyMobRotationManager;
import net.rollanddeath.smp.core.mobs.MobCommand;
import net.rollanddeath.smp.core.mobs.scripted.ScriptedMobManager;
import net.rollanddeath.smp.core.items.impl.*;
import net.rollanddeath.smp.core.game.GameManager;
import net.rollanddeath.smp.core.game.KillPointsManager;
import net.rollanddeath.smp.core.game.PlayerHudManager;
import net.rollanddeath.smp.core.game.WebStatusManager;
import net.rollanddeath.smp.core.game.AnnounceManager;
import net.rollanddeath.smp.core.game.StarterKitListener;
import net.rollanddeath.smp.core.game.SoftLockListener;
import net.rollanddeath.smp.core.game.ArmorRestrictionTask;
import net.rollanddeath.smp.core.rules.DayRuleListener;
import net.rollanddeath.smp.core.rules.DayRuleManager;
import net.rollanddeath.smp.core.hunters.BountyManager;
import net.rollanddeath.smp.core.hunters.BountyCommand;
import net.rollanddeath.smp.core.hunters.BountyListener;
import net.rollanddeath.smp.core.shops.ShopManager;
import net.rollanddeath.smp.core.shops.ShopListener;
import net.rollanddeath.smp.core.monetization.MonetizationManager;
import net.rollanddeath.smp.core.monetization.MonetizationListener;
import net.rollanddeath.smp.core.monetization.MonetizationCommand;
import net.rollanddeath.smp.core.monetization.TrailListener;
import net.rollanddeath.smp.core.monetization.TrailCommand;
import net.rollanddeath.smp.core.items.CraftingListener;
import net.rollanddeath.smp.core.items.UpgradeAndAnvilListener;
import net.rollanddeath.smp.core.commands.AdminCommand;
import net.rollanddeath.smp.core.commands.KillStoreCommand;
import net.rollanddeath.smp.core.items.DailyRollManager;
import net.rollanddeath.smp.core.commands.DailyCommand;
import net.rollanddeath.smp.core.commands.MenuCommand;
import net.rollanddeath.smp.core.commands.EventMenuCommand;
import net.rollanddeath.smp.core.combat.CombatLogManager;
import net.rollanddeath.smp.core.combat.ReanimationManager;
import net.rollanddeath.smp.core.scripting.scope.ScopeRegistry;
import net.rollanddeath.smp.core.scripting.projectiles.ScriptedProjectileService;
import net.rollanddeath.smp.core.scripting.particles.ScriptedParticleSystemService;
import net.rollanddeath.smp.core.scripting.library.ScriptLibrary;
import net.rollanddeath.smp.core.scripting.library.ScriptLibraryLoader;
import net.rollanddeath.smp.integration.discord.DiscordWebhookService;
import net.rollanddeath.smp.integration.PlaceholderHook;
import net.rollanddeath.smp.core.roles.RoleType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.GameRule;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

import java.util.List;

public final class RollAndDeathSMP extends JavaPlugin {

    private LifeManager lifeManager;
    private ModifierManager modifierManager;
    private ProtectionManager protectionManager;
    private TeamManager teamManager;
    private RoleManager roleManager;
    private MobManager mobManager;
    private ScriptedMobManager scriptedMobManager;
    private DailyMobRotationManager dailyMobRotationManager;
    private ItemManager itemManager;
    private RecipeManager recipeManager;
    private LootManager lootManager;
    private GameManager gameManager;
    private KillPointsManager killPointsManager;
    private DailyRollManager dailyRollManager;
    private WebStatusManager webStatusManager;
    private AnnounceManager announceManager;
    private ReanimationManager reanimationManager;
    private CombatLogManager combatLogManager;
    private MonetizationManager monetizationManager;
    private DiscordWebhookService discordService;
    private PlayerHudManager playerHudManager;
    private DayRuleManager dayRuleManager;
    private ScopeRegistry scopeRegistry;
    private ScriptedProjectileService scriptedProjectileService;
    private ScriptedParticleSystemService scriptedParticleSystemService;
    private net.rollanddeath.smp.core.modifiers.scripted.PersistentShadowService persistentShadowService;
    private ScriptLibrary scriptLibrary;
    private BountyManager bountyManager;
    private ShopManager shopManager;

    @Override
    public void onEnable() {
        // Load Config
        saveDefaultConfig();
        // Rellena claves nuevas con defaults sin pisar valores existentes (evita configs vacías)
        getConfig().options().copyDefaults(true);
        saveConfig();

        this.scopeRegistry = new ScopeRegistry(this);

        // Script library (macros reutilizables)
        net.rollanddeath.smp.core.scripting.builtin.actions.ActionRegistrar.registerDefaults();
        net.rollanddeath.smp.core.scripting.builtin.conditions.ConditionRegistrar.registerDefaults();
        this.scriptLibrary = ScriptLibraryLoader.load(this);

        // Scripting runtime helpers
        this.scriptedParticleSystemService = new ScriptedParticleSystemService(this);
        this.scriptedParticleSystemService.start();
        this.scriptedProjectileService = new ScriptedProjectileService(this);
        this.scriptedProjectileService.start();
        this.persistentShadowService = new net.rollanddeath.smp.core.modifiers.scripted.PersistentShadowService(this);

        // Ajuste global: solo 30% de jugadores necesarios para saltar la noche
        getServer().getWorlds().forEach(world -> world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 30));

        // Initialize Managers
        ConfigurationSection livesSection = getConfig().getConfigurationSection("lives");
        boolean livesEnabled = livesSection == null || livesSection.getBoolean("enabled", true);
        int defaultLives = livesSection != null ? livesSection.getInt("default", 3) : 3;

        this.lifeManager = new LifeManager(this, livesEnabled, defaultLives);
        this.modifierManager = new ModifierManager(this);
        this.teamManager = new TeamManager(this);
        teamManager.loadFromConfig(getConfig().getConfigurationSection("teams"));
        this.protectionManager = new ProtectionManager(this, teamManager);
        this.reanimationManager = new ReanimationManager(this, lifeManager);
        this.combatLogManager = new CombatLogManager(this, reanimationManager, teamManager);
        this.gameManager = new GameManager(this);

        ConfigurationSection discordSection = getConfig().getConfigurationSection("discord");
        boolean discordEnabled = discordSection != null && discordSection.getBoolean("enabled", false);
        String webhookUrl = discordSection != null ? discordSection.getString("webhook_url") : null;
        this.discordService = new DiscordWebhookService(this, teamManager, webhookUrl, discordEnabled);
        
        // Start War Tracker
        new WarTrackerTask(teamManager).runTaskTimer(this, 100L, 60L);

        this.roleManager = new RoleManager(this);
        this.mobManager = new MobManager(this);
        this.itemManager = new ItemManager(this);
        this.dailyMobRotationManager = new DailyMobRotationManager(this);
        this.recipeManager = new RecipeManager(this);
        this.lootManager = new LootManager(this);
        this.dayRuleManager = new DayRuleManager(this);
        this.killPointsManager = new KillPointsManager(this);
        this.announceManager = new AnnounceManager(this);
        this.monetizationManager = new MonetizationManager(this);
        this.bountyManager = new BountyManager(this);
        this.shopManager = new ShopManager(this);

        // Reactiva modifiers persistidos una vez que los managers necesarios ya existen.
        // Ej: "purge" necesita ProtectionManager.
        try {
            this.modifierManager.restoreActiveModifiers();
        } catch (Exception ignored) {
        }

        // Initialize UI/Web
        this.webStatusManager = new WebStatusManager(this);
        this.playerHudManager = new PlayerHudManager(reanimationManager, 5);
        try {
            getServer().getPluginManager().registerEvents(playerHudManager, this);
            playerHudManager.runTaskTimer(this, 1L, 5L);
        } catch (Exception ignored) {
        }
        // (migrados a modifiers.yml)
        ArmorRestrictionTask armorRestrictionTask = new ArmorRestrictionTask(this);
        armorRestrictionTask.runTaskTimer(this, 60L, 60L);

        // Register Modifiers
        // (ported a YAML) Sol Tóxico
        // (ported a YAML) Gravedad Pesada
        // (ported a YAML) Escasez de Hierro
        // (ported a YAML) Atmósfera Densa
        // (ported a YAML) Tierra Maldita
        // (ported a YAML) Arachnophobia
        // (ported a YAML) Suelo Frágil
        // (ported a YAML) Hambre Voraz / Sin Regeneración
        // (ported a YAML) Creeper Nuclear
        // (ported a YAML) Pesadillas
        // (ported a YAML) Agua Ácida
        // (ported a YAML) Ceguera Profunda
        // (ported a YAML) Inventario Pesado
        // (ported a YAML) Tormenta Eterna
        // (ported a YAML) Manos Resbaladizas
        // (ported a YAML) Madera Podrida
        // (ported a YAML) Silencio Mortal
        // (ported a YAML) Plaga de Ratas
        // (ported a YAML) Sol Abrasador
        // (ported a YAML) Huesos de Cristal
        // (ported a YAML) Sin Escudos
        // (ported a YAML) BleedingModifier
        // (ported a YAML) Sin Encantamientos / Sin Pociones / Sin Leche / Sin Camas
        // (ported a YAML) Inflación
        // (ported a YAML) Esqueletos Francotiradores
        // (ported a YAML) Gravedad Lunar Inversa
        // (ported a YAML) Nether Inestable
        // (ported a YAML) Toque de Midas Maligno
        // (ported a YAML) Enderman Furiosos
        // (ported a YAML) Maldición de Binding
        // (ported a YAML) Sombra Persistente
        // (ported a YAML) Pies de Plomo
        // (ported a YAML) Vientos Huracanados
        // (ported a YAML) Sed de Sangre
        // (ported a YAML) DeafnessModifier
        // (ported a YAML) ParanoiaModifier
        // (ported a YAML) Desorientación
        // (ported a YAML) Maldición de Ícaro
        // (ported a YAML) Fuego Fatuo
        // (ported a YAML) Cristal Frágil
        
        // Register Blessings
        // (ported a YAML) Corazón de Titán
        // (ported a YAML) Fuerza Descomunal
        // (ported a YAML) Piel de Acero
        // (ported a YAML) Vuelo de Ícaro
        // (ported a YAML) Branquias
        // (ported a YAML) Aura de Luz
        // (ported a YAML) Supersalto
        // (ported a YAML) Minería Explosiva
        // (ported a YAML) Sangre de Vampiro
        // (ported a YAML) Manos de Seda
        // (ported a YAML) Adrenalina
        // (ported a YAML) Bendición de Deméter
        // (ported a YAML) Doble Loot
        // (ported a YAML) XP Líquida
        // (ported a YAML) Puño de Hierro
        // (ported a YAML) Fotosíntesis
        // (ported a YAML) Amistad Animal
        // (ported a YAML) Toque de Fortuna
        // (ported a YAML) Velocidad de Luz
        // (ported a YAML) Pescador Legendario
        // (ported a YAML) Herrero Divino
        // (ported a YAML) Inmunidad al Veneno
        // (ported a YAML) Salto de Fe
        // (ported a YAML) Comercio Justo
        // (ported a YAML) Escudo Reflectante
        // (ported a YAML) Maestro Leñador
        // (ported a YAML) Imán de Ítems
        // (ported a YAML) Arquero Mágico
        // (ported a YAML) Poción Permanente
        // (ported a YAML) Sin Hambre
        // (ported a YAML) Cama Regenerativa
        // (ported a YAML) Invisibilidad Táctica
        // (ported a YAML) Regalo del Cielo
        // (ported a YAML) Ojos de Ender
        // (ported a YAML) Vida Extra

        // Register Chaos
        // (ported a YAML) Mundo Espejo
        // (ported a YAML) SlimeRainModifier
        // (ported a YAML) Intercambio Equivalente
        // (ported a YAML) EternalNightModifier
        // (ported a YAML) Botín Explosivo
        // (ported a YAML) ZeroGravityModifier
        // (ported a YAML) Suelo de Hielo
        // (ported a YAML) Chat Maldito
        // (ported a YAML) Juego de la Silla
        // (ported a YAML) Mundo Gigante
        // (ported a YAML) NoCoordinatesModifier
        // (ported a YAML) Invasión Zombie
        // (ported a YAML) Invasión Esqueleto
        // (ported a YAML) Día de Pesca
        // (ported a YAML) La Purga
        // (ported a YAML) EarthquakeModifier
        // (ported a YAML) WhispersModifier
        // (ported a YAML) Gallinas Explosivas
        // (ported a YAML) Sin Armadura
        // (ported a YAML) Guerra de Nieve
        // (ported a YAML) Pisos de Lava
        // (ported a YAML) Atracción Fatal
        // (ported a YAML) Sin Nombres
        // (ported a YAML) Ruleta Rusa
        // (ported a YAML) Sin Chat
        // (ported a YAML) Inventario Ciego
        // (ported a YAML) MyopiaModifier

        // Register Roles
        roleManager.registerRole(new PacifistRole(this));
        roleManager.registerRole(new VampireRole(this));
        roleManager.registerRole(new GlassCannonRole(this));
        roleManager.registerRole(new MidasRole(this));
        roleManager.registerRole(new NomadRole(this));
        roleManager.registerRole(new TankRole(this));
        roleManager.registerRole(new AssassinRole(this));
        roleManager.registerRole(new EngineerRole(this));
        roleManager.registerRole(new PyroRole(this));
        roleManager.registerRole(new CursedRole(this));
        roleManager.registerRole(new DruidRole(this));
        roleManager.registerRole(new BerserkerRole(this));
        roleManager.registerRole(new SniperRole(this));
        roleManager.registerRole(new MerchantRole(this));
        roleManager.registerRole(new GhostRole(this));
        roleManager.registerRole(new AquaticRole(this));
        roleManager.registerRole(new MinerRole(this));
        roleManager.registerRole(new TamerRole(this));
        roleManager.registerRole(new AlchemistRole(this));
        roleManager.registerRole(new KnightRole(this));
        roleManager.registerRole(new ThiefRole(this));
        roleManager.registerRole(new GiantRole(this));
        roleManager.registerRole(new DwarfRole(this));
        roleManager.registerRole(new IllusionistRole(this));
        roleManager.registerRole(new BarbarianRole(this));
        roleManager.registerRole(new SageRole(this));
        roleManager.registerRole(new ChaoticRole(this));
        roleManager.registerRole(new GuardianRole(this));
        roleManager.registerRole(new ExplorerRole(this));
        roleManager.registerRole(new CookRole(this));

        // Register Mobs
        // (ported a mobs.yml) CaveRat, LesserPhantom, MagmaSlime, JumpingSpider, MinerZombie,
        // (ported a mobs.yml) WanderingSkeleton, WetCreeper, ArmoredSkeleton, SpeedZombie, IceCreeper
        // (ported a mobs.yml) TheHive

        // (ported a mobs.yml) VengefulSpirit, CorruptedGolem, GiantPhantom, SwampWitch, TheStalker,
        // (ported a mobs.yml) Shadow, BlueBlaze, MimicShulker, MadEvoker, Banshee

        // Java-only mobs (pendientes de migración)
        // (ported a mobs.yml) BoneTurret, EliteSpiderJockey

        // Batch 3 (bosses y especiales)
        // (ported a mobs.yml) ApocalypseKnight, Leviathan, RatKing, AwakenedWarden, AlphaDragon, TheReaper, SlimeKing, VoidWalker

        // ScriptedMobs (mobs.yml) - se cargan al final para permitir override de los mobs Java
        this.scriptedMobManager = new ScriptedMobManager(this);
        this.scriptedMobManager.loadAndRegister(this.mobManager);

        // Register Items
        itemManager.registerItem(new SharpeningStone(this));
        itemManager.registerItem(new EternalTorch(this));
        itemManager.registerItem(new OblivionPotion(this));
        
        // Batch 2

        itemManager.registerItem(new SmallBackpack(this));
        itemManager.registerItem(new GrapplingHook(this));
        // (ported a items.yml) BoneBow, PoseidonTrident
        
        
        // Batch 3
        itemManager.registerItem(new InvisibilityCloak(this));
        itemManager.registerItem(new ResurrectionOrb(this));
        itemManager.registerItem(new WorldDestroyerPickaxe(this));
        itemManager.registerItem(new TrueSightHelmet(this));
        itemManager.registerItem(new SoulContract(this));
        // (ported a items.yml) VoidCall
        

        // Ítems ligados a roles (migrados a items.yml)


        // Gear progresivo sin encantamientos (armadura con atributos)
        

    // ScriptedItems (items.yml) - cargar al final para permitir override de los items Java
    new ScriptedItemManager(this).loadAndRegister(this.itemManager);

    // Daily rolls dependen de CustomItem registrados (incluyendo scripted items)
    this.dailyRollManager = new DailyRollManager(this, itemManager);
    // (ported a items.yml) LokiDice, LokiRoleDice
        this.dailyMobRotationManager.refreshForDay(this.gameManager.getCurrentDay());

        // Register Recipes & Loot
        recipeManager.registerRecipes();
        getServer().getPluginManager().registerEvents(lootManager, this);
        getServer().getPluginManager().registerEvents(new CraftingListener(this), this);
        getServer().getPluginManager().registerEvents(new UpgradeAndAnvilListener(this), this);
        getServer().getPluginManager().registerEvents(reanimationManager, this);
        if (combatLogManager.isEnabled()) {
            getServer().getPluginManager().registerEvents(combatLogManager, this);
        }

        // Register Listeners
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this, lifeManager, discordService, killPointsManager, modifierManager), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(protectionManager), this);
        getServer().getPluginManager().registerEvents(new TeamListener(teamManager), this);
        getServer().getPluginManager().registerEvents(new TeamBuffListener(teamManager, lifeManager), this);
        getServer().getPluginManager().registerEvents(new ChatListener(teamManager, roleManager, discordService), this);
        getServer().getPluginManager().registerEvents(roleManager, this);
        getServer().getPluginManager().registerEvents(new net.rollanddeath.smp.core.mobs.MobSpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new net.rollanddeath.smp.core.mobs.BossDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new DayRuleListener(this), this);
        getServer().getPluginManager().registerEvents(new MonetizationListener(monetizationManager), this);
        getServer().getPluginManager().registerEvents(new TrailListener(monetizationManager), this);
        getServer().getPluginManager().registerEvents(new BountyListener(bountyManager), this);
        getServer().getPluginManager().registerEvents(new ShopListener(this, shopManager), this);

        // Register Commands
        TeamCommand teamCommand = new TeamCommand(this, teamManager);
        PluginCommand teamCmd = getCommand("team");
        if (teamCmd != null) {
            teamCmd.setExecutor(teamCommand);
            teamCmd.setTabCompleter(teamCommand);
        }

        RoleCommand roleCommand = new RoleCommand(this);
        PluginCommand roleCmd = getCommand("role");
        if (roleCmd != null) {
            roleCmd.setExecutor(roleCommand);
            roleCmd.setTabCompleter(roleCommand);
        }

        MobCommand mobCommand = new MobCommand(this);
        PluginCommand mobCmd = getCommand("mob");
        if (mobCmd != null) {
            mobCmd.setExecutor(mobCommand);
            mobCmd.setTabCompleter(mobCommand);
        }

        ItemCommand itemCommand = new ItemCommand(this);
        PluginCommand itemCmd = getCommand("item");
        if (itemCmd != null) {
            itemCmd.setExecutor(itemCommand);
            itemCmd.setTabCompleter(itemCommand);
        }

        AdminCommand adminCommand = new AdminCommand(this);
        PluginCommand rdCmd = getCommand("rd");
        if (rdCmd != null) {
            rdCmd.setExecutor(adminCommand);
            rdCmd.setTabCompleter(adminCommand);
        }

        DailyCommand dailyCommand = new DailyCommand(dailyRollManager);
        PluginCommand dailyCmd = getCommand("daily");
        if (dailyCmd != null) {
            dailyCmd.setExecutor(dailyCommand);
            dailyCmd.setTabCompleter(dailyCommand);
        }

        KillStoreCommand killStoreCommand = new KillStoreCommand(this);
        PluginCommand killStoreCmd = getCommand("killstore");
        if (killStoreCmd != null) {
            killStoreCmd.setExecutor(killStoreCommand);
            killStoreCmd.setTabCompleter(killStoreCommand);
        }

        MenuCommand menuCmd = new MenuCommand(this);
        PluginCommand menuCommand = getCommand("menu");
        if (menuCommand != null) {
            menuCommand.setExecutor(menuCmd);
            menuCommand.setTabCompleter(menuCmd);
        }
        getServer().getPluginManager().registerEvents(menuCmd, this);

        EventMenuCommand eventMenu = new EventMenuCommand(this);
        PluginCommand eventosCmd = getCommand("eventos");
        if (eventosCmd != null) {
            eventosCmd.setExecutor(eventMenu);
            eventosCmd.setTabCompleter(eventMenu);
        }
        getServer().getPluginManager().registerEvents(eventMenu, this);

        net.rollanddeath.smp.core.commands.MobsCommand mobsCmd = new net.rollanddeath.smp.core.commands.MobsCommand(this);
        getCommand("mobs").setExecutor(mobsCmd);
        getServer().getPluginManager().registerEvents(mobsCmd, this);

        net.rollanddeath.smp.core.commands.RulesCommand rulesCmd = new net.rollanddeath.smp.core.commands.RulesCommand(this);
        getCommand("reglas").setExecutor(rulesCmd);
        getServer().getPluginManager().registerEvents(rulesCmd, this);

        MonetizationCommand monetCmd = new MonetizationCommand(monetizationManager);
        PluginCommand ecCmd = getCommand("ec");
        if (ecCmd != null) {
            ecCmd.setExecutor(monetCmd);
            ecCmd.setTabCompleter(monetCmd);
        }
        PluginCommand craftCmd = getCommand("craft");
        if (craftCmd != null) {
            craftCmd.setExecutor(monetCmd);
            craftCmd.setTabCompleter(monetCmd);
        }
        PluginCommand anvilCmd = getCommand("anvil");
        if (anvilCmd != null) {
            anvilCmd.setExecutor(monetCmd);
            anvilCmd.setTabCompleter(monetCmd);
        }
        PluginCommand enchantCmd = getCommand("enchant");
        if (enchantCmd != null) {
            enchantCmd.setExecutor(monetCmd);
            enchantCmd.setTabCompleter(monetCmd);
        }
        PluginCommand stoneCmd = getCommand("stonecutter");
        if (stoneCmd != null) {
            stoneCmd.setExecutor(monetCmd);
            stoneCmd.setTabCompleter(monetCmd);
        }
        PluginCommand smithCmd = getCommand("smith");
        if (smithCmd != null) {
            smithCmd.setExecutor(monetCmd);
            smithCmd.setTabCompleter(monetCmd);
        }
        PluginCommand loomCmd = getCommand("loom");
        if (loomCmd != null) {
            loomCmd.setExecutor(monetCmd);
            loomCmd.setTabCompleter(monetCmd);
        }
        PluginCommand grindCmd = getCommand("grindstone");
        if (grindCmd != null) {
            grindCmd.setExecutor(monetCmd);
            grindCmd.setTabCompleter(monetCmd);
        }
        PluginCommand cartCmd = getCommand("cartography");
        if (cartCmd != null) {
            cartCmd.setExecutor(monetCmd);
            cartCmd.setTabCompleter(monetCmd);
        }
        TrailCommand trailCmd = new TrailCommand(monetizationManager);
        PluginCommand trail = getCommand("trail");
        if (trail != null) {
            trail.setExecutor(trailCmd);
            trail.setTabCompleter(trailCmd);
        }
        PluginCommand backpackCmd = getCommand("backpack");
        if (backpackCmd != null) {
            backpackCmd.setExecutor(monetCmd);
            backpackCmd.setTabCompleter(monetCmd);
        }
        PluginCommand trashCmd = getCommand("trash");
        if (trashCmd != null) {
            trashCmd.setExecutor(monetCmd);
            trashCmd.setTabCompleter(monetCmd);
        }
        PluginCommand furnaceCmd = getCommand("furnace");
        if (furnaceCmd != null) {
            furnaceCmd.setExecutor(monetCmd);
            furnaceCmd.setTabCompleter(monetCmd);
        }
        PluginCommand blastCmd = getCommand("blast");
        if (blastCmd != null) {
            blastCmd.setExecutor(monetCmd);
            blastCmd.setTabCompleter(monetCmd);
        }
        PluginCommand smokerCmd = getCommand("smoker");
        if (smokerCmd != null) {
            smokerCmd.setExecutor(monetCmd);
            smokerCmd.setTabCompleter(monetCmd);
        }

        BountyCommand bountyCommand = new BountyCommand(bountyManager);
        PluginCommand cazadoresCmd = getCommand("cazadores");
        if (cazadoresCmd != null) {
            cazadoresCmd.setExecutor(bountyCommand);
            cazadoresCmd.setTabCompleter(bountyCommand);
        }

        if (discordService != null && discordService.isEnabled()) {
            discordService.sendServerStatus(true);
        }

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderHook(this).register();
            getLogger().info("PlaceholderAPI hook registrado.");
        } else {
            getLogger().info("PlaceholderAPI no encontrado; los placeholders se desactivan.");
        }

        getLogger().info("RollAndDeath SMP Plugin has been enabled!");
        getLogger().info("Protocolo Diciembre iniciado...");
    }

    public ScriptLibrary getScriptLibrary() {
        return scriptLibrary;
    }

    public void setScriptLibrary(ScriptLibrary scriptLibrary) {
        this.scriptLibrary = scriptLibrary;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (scriptedProjectileService != null) {
            scriptedProjectileService.stop();
        }
        if (scriptedParticleSystemService != null) {
            scriptedParticleSystemService.stop();
        }
        if (persistentShadowService != null) {
            persistentShadowService.stop();
        }
        if (webStatusManager != null) {
            webStatusManager.stop();
        }
        if (announceManager != null) {
            announceManager.stop();
        }
        if (killPointsManager != null) {
            killPointsManager.shutdown();
        }
        if (playerHudManager != null) {
            try {
                playerHudManager.cancel();
            } catch (Exception ignored) {
            }
        }
        if (reanimationManager != null) {
            reanimationManager.shutdown();
        }
        if (teamManager != null) {
            getConfig().set("teams", null);
            teamManager.saveToConfig(getConfig().createSection("teams"));
            saveConfig();
        }
        if (discordService != null && discordService.isEnabled()) {
            discordService.sendServerStatus(false);
        }
        getLogger().info("RollAndDeath SMP Plugin has been disabled!");
    }

    public LifeManager getLifeManager() {
        return lifeManager;
    }

    public ModifierManager getModifierManager() {
        return modifierManager;
    }

    public ProtectionManager getProtectionManager() {
        return protectionManager;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public RoleManager getRoleManager() {
        return roleManager;
    }

    public MobManager getMobManager() {
        return mobManager;
    }

    public ScriptedMobManager getScriptedMobManager() {
        return scriptedMobManager;
    }

    public ReanimationManager getReanimationManager() {
        return reanimationManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public LootManager getLootManager() {
        return lootManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public AnnounceManager getAnnounceManager() {
        return announceManager;
    }

    public KillPointsManager getKillPointsManager() {
        return killPointsManager;
    }

    public DailyRollManager getDailyRollManager() {
        return dailyRollManager;
    }

    public DailyMobRotationManager getDailyMobRotationManager() {
        return dailyMobRotationManager;
    }

    public WebStatusManager getWebStatusManager() {
        return webStatusManager;
    }

    public DayRuleManager getDayRuleManager() {
        return dayRuleManager;
    }

    public ScopeRegistry getScopeRegistry() {
        return scopeRegistry;
    }

    public ScriptedProjectileService getScriptedProjectileService() {
        return scriptedProjectileService;
    }

    public ScriptedParticleSystemService getScriptedParticleSystemService() {
        return scriptedParticleSystemService;
    }

    public net.rollanddeath.smp.core.modifiers.scripted.PersistentShadowService getPersistentShadowService() {
        return persistentShadowService;
    }

    public DiscordWebhookService getDiscordService() {
        return discordService;
    }

    public CombatLogManager getCombatLogManager() {
        return combatLogManager;
    }
}
