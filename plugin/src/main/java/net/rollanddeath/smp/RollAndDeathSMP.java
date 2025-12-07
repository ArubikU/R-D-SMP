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
import net.rollanddeath.smp.modifiers.curses.*;
import net.rollanddeath.smp.modifiers.blessings.*;
import net.rollanddeath.smp.modifiers.chaos.*;
import net.rollanddeath.smp.core.roles.RoleManager;
import net.rollanddeath.smp.core.roles.RoleCommand;
import net.rollanddeath.smp.core.roles.impl.*;
import net.rollanddeath.smp.core.items.ItemManager;
import net.rollanddeath.smp.core.items.ItemCommand;
import net.rollanddeath.smp.core.items.RecipeManager;
import net.rollanddeath.smp.core.items.LootManager;
import net.rollanddeath.smp.core.mobs.MobManager;
import net.rollanddeath.smp.core.mobs.DailyMobRotationManager;
import net.rollanddeath.smp.core.mobs.MobCommand;
import net.rollanddeath.smp.core.items.impl.*;
import net.rollanddeath.smp.core.items.impl.AttributeArmorItem;
import net.rollanddeath.smp.core.items.impl.AttributeWeaponItem;
import net.rollanddeath.smp.core.mobs.impl.*;
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
import net.rollanddeath.smp.core.items.CraftingListener;
import net.rollanddeath.smp.core.commands.AdminCommand;
import net.rollanddeath.smp.core.commands.KillStoreCommand;
import net.rollanddeath.smp.core.items.DailyRollManager;
import net.rollanddeath.smp.core.commands.DailyCommand;
import net.rollanddeath.smp.core.commands.MenuCommand;
import net.rollanddeath.smp.core.commands.EventMenuCommand;
import net.rollanddeath.smp.core.combat.CombatLogManager;
import net.rollanddeath.smp.core.combat.ReanimationManager;
import net.rollanddeath.smp.integration.discord.DiscordWebhookService;
import net.rollanddeath.smp.integration.PlaceholderHook;
import net.rollanddeath.smp.core.items.CustomItemType;
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
    private DiscordWebhookService discordService;
    private PlayerHudManager playerHudManager;
    private DayRuleManager dayRuleManager;

    @Override
    public void onEnable() {
        // Load Config
        saveDefaultConfig();

        // Ajuste global: solo 30% de jugadores necesarios para saltar la noche
        getServer().getWorlds().forEach(world -> world.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 30));

        // Initialize Managers
        this.lifeManager = new LifeManager(this);
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

        // Initialize UI/Web
        this.webStatusManager = new WebStatusManager(this);
        this.playerHudManager = new PlayerHudManager(reanimationManager, 5);
        getServer().getPluginManager().registerEvents(playerHudManager, this);
        getServer().getPluginManager().registerEvents(new StarterKitListener(this), this);
        getServer().getPluginManager().registerEvents(new SoftLockListener(this), this);
        getServer().getPluginManager().registerEvents(new DayRuleListener(this), this);
        getServer().getPluginManager().registerEvents(new net.rollanddeath.smp.core.mobs.MobSpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new net.rollanddeath.smp.core.mobs.BossDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new net.rollanddeath.smp.core.game.EndBattleListener(this), this);
        getServer().getPluginManager().registerEvents(new net.rollanddeath.smp.core.game.EndCrystalListener(this), this);
        playerHudManager.runTaskTimer(this, 40L, 5L);

        ArmorRestrictionTask armorRestrictionTask = new ArmorRestrictionTask(this);
        armorRestrictionTask.runTaskTimer(this, 60L, 60L);

        // Register Modifiers
        modifierManager.registerModifier(new ToxicSunModifier(this));
        modifierManager.registerModifier(new HeavyGravityModifier(this));
        modifierManager.registerModifier(new IronShortageModifier(this));
        modifierManager.registerModifier(new DenseAtmosphereModifier(this));
        modifierManager.registerModifier(new CursedEarthModifier(this));
        modifierManager.registerModifier(new ArachnophobiaModifier(this));
        modifierManager.registerModifier(new FragileGroundModifier(this));
        modifierManager.registerModifier(new VoraciousHungerModifier(this));
        modifierManager.registerModifier(new NoRegenModifier(this));
        modifierManager.registerModifier(new NuclearCreeperModifier(this));
        modifierManager.registerModifier(new NightmaresModifier(this));
        modifierManager.registerModifier(new AcidWaterModifier(this));
        modifierManager.registerModifier(new DeepBlindnessModifier(this));
        modifierManager.registerModifier(new HeavyInventoryModifier(this));
        modifierManager.registerModifier(new EternalStormModifier(this));
        modifierManager.registerModifier(new SlipperyHandsModifier(this));
        modifierManager.registerModifier(new RottenWoodModifier(this));
        modifierManager.registerModifier(new DeadlySilenceModifier(this));
        modifierManager.registerModifier(new RatPlagueModifier(this));
        modifierManager.registerModifier(new ScorchingSunModifier(this));
        modifierManager.registerModifier(new GlassBonesModifier(this));
        modifierManager.registerModifier(new NoShieldsModifier(this));
        modifierManager.registerModifier(new BleedingModifier(this));
        modifierManager.registerModifier(new NoEnchantsModifier(this));
        modifierManager.registerModifier(new NoPotionsModifier(this));
        modifierManager.registerModifier(new NoMilkModifier(this));
        modifierManager.registerModifier(new NoBedsModifier(this));
        modifierManager.registerModifier(new InflationModifier(this));
        modifierManager.registerModifier(new SniperSkeletonsModifier(this));
        modifierManager.registerModifier(new ReverseLunarGravityModifier(this));
        modifierManager.registerModifier(new UnstableNetherModifier(this));
        modifierManager.registerModifier(new EvilMidasTouchModifier(this));
        modifierManager.registerModifier(new FuriousEndermanModifier(this));
        modifierManager.registerModifier(new BindingCurseModifier(this));
        modifierManager.registerModifier(new PersistentShadowModifier(this));
        modifierManager.registerModifier(new LeadFeetModifier(this));
        modifierManager.registerModifier(new HurricaneWindsModifier(this));
        modifierManager.registerModifier(new BloodThirstModifier(this));
        modifierManager.registerModifier(new DeafnessModifier(this));
        modifierManager.registerModifier(new ParanoiaModifier(this));
        modifierManager.registerModifier(new DisorientationModifier(this));
        modifierManager.registerModifier(new IcarusCurseModifier(this));
        modifierManager.registerModifier(new WillOWispModifier(this));
        modifierManager.registerModifier(new FragileGlassModifier(this));
        
        // Register Blessings
        modifierManager.registerModifier(new TitanHeartModifier(this));
        modifierManager.registerModifier(new SuperStrengthModifier(this));
        modifierManager.registerModifier(new SteelSkinModifier(this));
        modifierManager.registerModifier(new IcarusFlightModifier(this));
        modifierManager.registerModifier(new GillsModifier(this));
        modifierManager.registerModifier(new LightAuraModifier(this));
        modifierManager.registerModifier(new SuperJumpModifier(this));
        modifierManager.registerModifier(new ExplosiveMiningModifier(this));
        modifierManager.registerModifier(new VampireBloodModifier(this));
        modifierManager.registerModifier(new SilkTouchHandsModifier(this));
        modifierManager.registerModifier(new AdrenalineModifier(this));
        modifierManager.registerModifier(new DemeterBlessingModifier(this));
        modifierManager.registerModifier(new DoubleLootModifier(this));
        modifierManager.registerModifier(new LiquidXPModifier(this));
        modifierManager.registerModifier(new IronFistModifier(this));
        modifierManager.registerModifier(new PhotosynthesisModifier(this));
        modifierManager.registerModifier(new AnimalFriendshipModifier(this));
        modifierManager.registerModifier(new FortuneTouchModifier(this));
        modifierManager.registerModifier(new LightSpeedModifier(this));
        modifierManager.registerModifier(new LegendaryFisherModifier(this));
        modifierManager.registerModifier(new DivineBlacksmithModifier(this));
        modifierManager.registerModifier(new PoisonImmunityModifier(this));
        modifierManager.registerModifier(new LeapOfFaithModifier(this));
        modifierManager.registerModifier(new FairTradeModifier(this));
        modifierManager.registerModifier(new ReflectiveShieldModifier(this));
        modifierManager.registerModifier(new MasterLumberjackModifier(this));
        modifierManager.registerModifier(new ItemMagnetModifier(this));
        modifierManager.registerModifier(new SkyGiftModifier(this));
        modifierManager.registerModifier(new MagicArcherModifier(this));
        modifierManager.registerModifier(new PermanentPotionModifier(this));
        modifierManager.registerModifier(new NoHungerModifier(this));
        modifierManager.registerModifier(new EnderEyesModifier(this));
        modifierManager.registerModifier(new RegenerativeBedModifier(this));
        modifierManager.registerModifier(new TacticalInvisibilityModifier(this));
        modifierManager.registerModifier(new ExtraLifeModifier(this));

        // Register Chaos
        modifierManager.registerModifier(new MirrorWorldModifier(this));
        modifierManager.registerModifier(new SlimeRainModifier(this));
        modifierManager.registerModifier(new EquivalentExchangeModifier(this));
        modifierManager.registerModifier(new EternalNightModifier(this));
        modifierManager.registerModifier(new ExplosiveLootModifier(this));
        modifierManager.registerModifier(new ZeroGravityModifier(this));
        modifierManager.registerModifier(new IceFloorModifier(this));
        modifierManager.registerModifier(new CursedChatModifier(this));
        modifierManager.registerModifier(new MusicalChairsModifier(this));
        modifierManager.registerModifier(new GiantWorldModifier(this));
        modifierManager.registerModifier(new NoCoordinatesModifier(this));
        modifierManager.registerModifier(new ZombieInvasionModifier(this));
        modifierManager.registerModifier(new SkeletonInvasionModifier(this));
        modifierManager.registerModifier(new FishingDayModifier(this));
        modifierManager.registerModifier(new ThePurgeModifier(this));
        modifierManager.registerModifier(new EarthquakeModifier(this));
        modifierManager.registerModifier(new WhispersModifier(this));
        modifierManager.registerModifier(new ExplosiveChickensModifier(this));
        modifierManager.registerModifier(new NoArmorModifier(this));
        modifierManager.registerModifier(new SnowWarModifier(this));
        modifierManager.registerModifier(new LavaFloorsModifier(this));
        modifierManager.registerModifier(new FatalAttractionModifier(this));
        modifierManager.registerModifier(new NoNamesModifier(this));
        modifierManager.registerModifier(new RussianRouletteModifier(this));
        modifierManager.registerModifier(new NoChatModifier(this));
        modifierManager.registerModifier(new BlindInventoryModifier(this));
        modifierManager.registerModifier(new MyopiaModifier(this));

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
        mobManager.registerMob(new CaveRat(this));
        mobManager.registerMob(new LesserPhantom(this));
        mobManager.registerMob(new MagmaSlime(this));
        mobManager.registerMob(new JumpingSpider(this));
        mobManager.registerMob(new MinerZombie(this));
        mobManager.registerMob(new WanderingSkeleton(this));
        mobManager.registerMob(new WetCreeper(this));
        mobManager.registerMob(new ArmoredSkeleton(this));
        mobManager.registerMob(new SpeedZombie(this));
        mobManager.registerMob(new TheHive(this));
        
        // Batch 2
        mobManager.registerMob(new VengefulSpirit(this));
        mobManager.registerMob(new CorruptedGolem(this));
        mobManager.registerMob(new IceCreeper(this));
        mobManager.registerMob(new GiantPhantom(this));
        mobManager.registerMob(new SwampWitch(this));
        mobManager.registerMob(new TheStalker(this));
        mobManager.registerMob(new BoneTurret(this));
        mobManager.registerMob(new Shadow(this));
        mobManager.registerMob(new BlueBlaze(this));
        mobManager.registerMob(new MimicShulker(this));
        
        // Batch 3
        mobManager.registerMob(new EliteSpiderJockey(this));
        mobManager.registerMob(new MadEvoker(this));
        mobManager.registerMob(new ApocalypseKnight(this));
        mobManager.registerMob(new Leviathan(this));
        mobManager.registerMob(new RatKing(this));
        mobManager.registerMob(new AwakenedWarden(this));
        mobManager.registerMob(new AlphaDragon(this));
        mobManager.registerMob(new TheReaper(this));
        mobManager.registerMob(new SlimeKing(this));
        mobManager.registerMob(new Banshee(this));
        mobManager.registerMob(new VoidWalker(this));

        // Register Items
        itemManager.registerItem(new HealingBandage(this));
        itemManager.registerItem(new SharpeningStone(this));
        itemManager.registerItem(new MoldyBread(this));
        itemManager.registerItem(new EternalTorch(this));
        itemManager.registerItem(new SharpStick(this));
        itemManager.registerItem(new ReinforcedLeatherBoots(this));
        itemManager.registerItem(new MysteriousSoup(this));
        itemManager.registerItem(new HermesBoots(this));
        itemManager.registerItem(new OblivionPotion(this));
        itemManager.registerItem(new ThornShield(this));
        
        // Batch 2
        itemManager.registerItem(new BoneBow(this));
        itemManager.registerItem(new GlassPickaxe(this));
        itemManager.registerItem(new XPMagnet(this));
        itemManager.registerItem(new SmallBackpack(this));
        itemManager.registerItem(new GrapplingHook(this));
        itemManager.registerItem(new PickaxeOfGreed(this));
        itemManager.registerItem(new AppleOfDiscord(this));
        itemManager.registerItem(new PoseidonTrident(this));
        itemManager.registerItem(new ArmoredWings(this));
        
        // Batch 3
        itemManager.registerItem(new InvisibilityCloak(this));
        itemManager.registerItem(new WarHammer(this));
        itemManager.registerItem(new RegenerationTotem(this));
        itemManager.registerItem(new ChanceTotem(this));
        itemManager.registerItem(new ResurrectionOrb(this));
        itemManager.registerItem(new VampireSword(this));
        itemManager.registerItem(new WorldDestroyerPickaxe(this));
        itemManager.registerItem(new TrueSightHelmet(this));
        itemManager.registerItem(new NotchHeart(this));
        itemManager.registerItem(new SoulContract(this));
        itemManager.registerItem(new VoidCall(this));
        itemManager.registerItem(new RealDragonEgg(this));
        itemManager.registerItem(new HandOfGod(this));
        itemManager.registerItem(new LifeGapple(this));

        // Ítems ligados a roles (consumibles con requisitos de rol)
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.PACIFIST_BALM, RoleType.PACIFIST, Material.HONEY_BOTTLE,
            List.of(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 1), new PotionEffect(PotionEffectType.ABSORPTION, 20 * 30, 0)),
            Collections.emptyList(), List.of("Click derecho: Regeneración II + Absorción I"), Sound.ITEM_HONEY_BOTTLE_DRINK, 1.1f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.VAMPIRE_VIAL, RoleType.VAMPIRE, Material.GLASS_BOTTLE,
            List.of(new PotionEffect(PotionEffectType.STRENGTH, 20 * 30, 0), new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 45, 0)),
            Collections.emptyList(), List.of("Click derecho: Fuerza I + Visión nocturna"), Sound.ITEM_BOTTLE_FILL_DRAGONBREATH, 0.9f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.GLASS_CANNON_CHARGE, RoleType.GLASS_CANNON, Material.AMETHYST_SHARD,
            List.of(new PotionEffect(PotionEffectType.STRENGTH, 20 * 15, 1), new PotionEffect(PotionEffectType.SLOWNESS, 20 * 15, 0)),
            Collections.emptyList(), List.of("Fuerza II corta, pero te inmoviliza un poco"), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.3f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.MIDAS_TALISMAN, RoleType.MIDAS, Material.GOLD_INGOT,
            List.of(new PotionEffect(PotionEffectType.LUCK, 20 * 40, 0), new PotionEffect(PotionEffectType.ABSORPTION, 20 * 30, 0)),
            Collections.emptyList(), List.of("Atrae la fortuna por unos segundos"), Sound.BLOCK_AMETHYST_BLOCK_PLACE, 1.0f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.NOMAD_COMPASS, RoleType.NOMAD, Material.COMPASS,
            List.of(new PotionEffect(PotionEffectType.SPEED, 20 * 30, 1), new PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 30, 0)),
            Collections.emptyList(), List.of("Movimiento ligero para seguir viajando"), Sound.ITEM_LODESTONE_COMPASS_LOCK, 1.2f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.TANK_PLATING, RoleType.TANK, Material.IRON_BLOCK,
            List.of(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 20, 1), new PotionEffect(PotionEffectType.SLOWNESS, 20 * 20, 0)),
            Collections.emptyList(), List.of("Armadura extra, te hace más pesado"), Sound.BLOCK_ANVIL_USE, 0.8f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.ASSASSIN_POISON_KIT, RoleType.ASSASSIN, Material.FERMENTED_SPIDER_EYE,
            List.of(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 20, 0), new PotionEffect(PotionEffectType.STRENGTH, 20 * 10, 0)),
            Collections.emptyList(), List.of("Sigilo breve y golpe más fuerte"), Sound.ENTITY_SPIDER_AMBIENT, 1.1f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.ENGINEER_REPAIR_KIT, RoleType.ENGINEER, Material.ANVIL,
            List.of(new PotionEffect(PotionEffectType.HASTE, 20 * 30, 1), new PotionEffect(PotionEffectType.RESISTANCE, 20 * 10, 0)),
            Collections.emptyList(), List.of("Haste II corto para reparar o colocar"), Sound.BLOCK_ANVIL_PLACE, 1.0f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.PYRO_TORCH, RoleType.PYRO, Material.FLINT_AND_STEEL,
            List.of(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 45, 0), new PotionEffect(PotionEffectType.STRENGTH, 20 * 15, 0)),
            Collections.emptyList(), List.of("Inmune al fuego y golpes ardientes"), Sound.ITEM_FLINTANDSTEEL_USE, 1.0f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.CURSED_IDOL, RoleType.CURSED, Material.WITHER_ROSE,
            List.of(new PotionEffect(PotionEffectType.LUCK, 20 * 60, 0), new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 0)),
            List.of(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 20, 0), new PotionEffect(PotionEffectType.POISON, 20 * 10, 0)),
            List.of("50/50 suerte o desgracia"), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.6f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.DRUID_SEED, RoleType.DRUID, Material.MOSS_BLOCK,
            List.of(new PotionEffect(PotionEffectType.REGENERATION, 20 * 15, 0), new PotionEffect(PotionEffectType.SATURATION, 20 * 5, 0)),
            Collections.emptyList(), List.of("Brotan fuerzas de la tierra"), Sound.BLOCK_GRASS_PLACE, 1.0f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.BERSERKER_MEAD, RoleType.BERSERKER, Material.HONEY_BOTTLE,
            List.of(new PotionEffect(PotionEffectType.STRENGTH, 20 * 25, 1), new PotionEffect(PotionEffectType.HUNGER, 20 * 20, 1)),
            Collections.emptyList(), List.of("Fuerza brutal a cambio de más hambre"), Sound.ITEM_HONEY_BOTTLE_DRINK, 1.0f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.SNIPER_SCOPE, RoleType.SNIPER, Material.SPYGLASS,
            List.of(new PotionEffect(PotionEffectType.STRENGTH, 20 * 20, 0), new PotionEffect(PotionEffectType.SLOWNESS, 20 * 10, 0)),
            Collections.emptyList(), List.of("Daño de tiro mejorado, te estabiliza"), Sound.ITEM_SPYGLASS_USE, 1.0f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.MERCHANT_LEDGER, RoleType.MERCHANT, Material.BOOK,
            List.of(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 20 * 120, 0)),
            Collections.emptyList(), List.of("Tratos mejores por un rato"), Sound.ITEM_BOOK_PAGE_TURN, 1.0f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.GHOST_VEIL, RoleType.GHOST, Material.WHITE_STAINED_GLASS_PANE,
            List.of(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 25, 0), new PotionEffect(PotionEffectType.WEAKNESS, 20 * 25, 0)),
            Collections.emptyList(), List.of("Te vuelves etéreo pero más débil"), Sound.ITEM_TRIDENT_RIPTIDE_1, 1.2f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.AQUATIC_GILLS, RoleType.AQUATIC, Material.PRISMARINE_SHARD,
            List.of(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 60, 0), new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 20 * 20, 0)),
            Collections.emptyList(), List.of("Respira y avanza como pez"), Sound.ENTITY_DOLPHIN_AMBIENT_WATER, 1.0f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.MINER_CHARGE, RoleType.MINER, Material.TNT,
            List.of(new PotionEffect(PotionEffectType.HASTE, 20 * 40, 1), new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 40, 0)),
            Collections.emptyList(), List.of("Haste II y visión para minar"), Sound.ENTITY_TNT_PRIMED, 1.2f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.TAMER_WHISTLE, RoleType.TAMER, Material.BONE,
            List.of(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 20, 0), new PotionEffect(PotionEffectType.STRENGTH, 20 * 20, 0)),
            Collections.emptyList(), List.of("Llamado alfa: te y tus mascotas se envalentonan"), Sound.ENTITY_WOLF_AMBIENT, 1.0f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.ALCHEMIST_CATALYST, RoleType.ALCHEMIST, Material.NETHER_WART,
            List.of(new PotionEffect(PotionEffectType.LUCK, 20 * 45, 0), new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 0)),
            Collections.emptyList(), List.of("Cataliza tu siguiente jugada"), Sound.BLOCK_BREWING_STAND_BREW, 1.1f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.KNIGHT_BANNER, RoleType.KNIGHT, Material.WHITE_BANNER,
            List.of(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 40, 1), new PotionEffect(PotionEffectType.RESISTANCE, 20 * 20, 0)),
            Collections.emptyList(), List.of("Estandarte de batalla: absorción + resistencia"), Sound.ITEM_SHIELD_BLOCK, 0.9f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.THIEF_LOCKPICKS, RoleType.THIEF, Material.TRIPWIRE_HOOK,
            List.of(new PotionEffect(PotionEffectType.SPEED, 20 * 20, 1), new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 15, 0)),
            Collections.emptyList(), List.of("Movilidad y sigilo corto"), Sound.BLOCK_TRIPWIRE_CLICK_OFF, 1.3f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.GIANT_BRACER, RoleType.GIANT, Material.IRON_TRAPDOOR,
            List.of(new PotionEffect(PotionEffectType.HEALTH_BOOST, 20 * 45, 1), new PotionEffect(PotionEffectType.SLOWNESS, 20 * 30, 0)),
            Collections.emptyList(), List.of("Más vida pero te hace pesado"), Sound.ENTITY_IRON_GOLEM_REPAIR, 0.8f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.DWARF_FORGE_HAMMER, RoleType.DWARF, Material.IRON_PICKAXE,
            List.of(new PotionEffect(PotionEffectType.HASTE, 20 * 30, 2), new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 30, 0)),
            Collections.emptyList(), List.of("Haste III y resistencia al fuego"), Sound.BLOCK_ANVIL_USE, 1.0f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.ILLUSIONIST_MIRROR, RoleType.ILLUSIONIST, Material.GLASS_PANE,
            List.of(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 20, 0), new PotionEffect(PotionEffectType.SPEED, 20 * 20, 0)),
            Collections.emptyList(), List.of("Te ocultas entre reflejos"), Sound.BLOCK_GLASS_HIT, 1.5f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.BARBARIAN_MEAD, RoleType.BARBARIAN, Material.COOKED_PORKCHOP,
            List.of(new PotionEffect(PotionEffectType.STRENGTH, 20 * 25, 1), new PotionEffect(PotionEffectType.MINING_FATIGUE, 20 * 20, 0)),
            Collections.emptyList(), List.of("Fuerza bruta, poca fineza"), Sound.ENTITY_PLAYER_BURP, 1.0f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.SAGE_SCROLL, RoleType.SAGE, Material.PAPER,
            List.of(new PotionEffect(PotionEffectType.LUCK, 20 * 60, 1), new PotionEffect(PotionEffectType.ABSORPTION, 20 * 20, 0)),
            Collections.emptyList(), List.of("Un impulso de sabiduría y fortuna"), Sound.ITEM_BOOK_PAGE_TURN, 1.2f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.CHAOTIC_SEAL, RoleType.CHAOTIC, Material.ECHO_SHARD,
            List.of(new PotionEffect(PotionEffectType.STRENGTH, 20 * 20, 0), new PotionEffect(PotionEffectType.SPEED, 20 * 20, 0)),
            List.of(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 20, 0), new PotionEffect(PotionEffectType.SLOWNESS, 20 * 20, 0)),
            List.of("Efecto caótico: puede ser bueno o malo"), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 0.7f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.GUARDIAN_SIGIL, RoleType.GUARDIAN, Material.SHIELD,
            List.of(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 15, 1), new PotionEffect(PotionEffectType.ABSORPTION, 20 * 30, 0)),
            Collections.emptyList(), List.of("Protección concentrada"), Sound.ITEM_SHIELD_BLOCK, 0.9f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.EXPLORER_COMPASS, RoleType.EXPLORER, Material.MAP,
            List.of(new PotionEffect(PotionEffectType.SPEED, 20 * 40, 1), new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 40, 0)),
            Collections.emptyList(), List.of("Corre y ve en la oscuridad"), Sound.ITEM_LODESTONE_COMPASS_LOCK, 1.1f));
        itemManager.registerItem(new RoleCurioItem(this, CustomItemType.COOK_SPICE_BLEND, RoleType.COOK, Material.BOWL,
            List.of(new PotionEffect(PotionEffectType.SATURATION, 20 * 10, 0), new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 0)),
            Collections.emptyList(), List.of("Receta secreta: regenera y sacia"), Sound.ITEM_BOTTLE_FILL, 1.2f));

        // Gear progresivo sin encantamientos (armadura con atributos)
        itemManager.registerItem(new AttributeArmorItem(this, CustomItemType.STEEL_HELMET, Material.IRON_HELMET, EquipmentSlotGroup.HEAD, 1.0, 2.0, 0.0, List.of("Casco de acero templado", "Nivel netherite en defensa", "+1 armadura, +2 dureza")));
        itemManager.registerItem(new AttributeArmorItem(this, CustomItemType.STEEL_CHESTPLATE, Material.IRON_CHESTPLATE, EquipmentSlotGroup.CHEST, 2.0, 3.0, 0.0, List.of("Pechera de placas reforzadas", "Nivel netherite en defensa", "+2 armadura, +3 dureza")));
        itemManager.registerItem(new AttributeArmorItem(this, CustomItemType.STEEL_LEGGINGS, Material.IRON_LEGGINGS, EquipmentSlotGroup.LEGS, 2.0, 3.0, 0.0, List.of("Grebas de acero con charnelas", "Nivel netherite en defensa", "+2 armadura, +3 dureza")));
        itemManager.registerItem(new AttributeArmorItem(this, CustomItemType.STEEL_BOOTS, Material.IRON_BOOTS, EquipmentSlotGroup.FEET, 1.0, 2.0, 0.0, List.of("Botas de acero acolchonadas", "Nivel netherite en defensa", "+1 armadura, +2 dureza")));

        itemManager.registerItem(new AttributeArmorItem(this, CustomItemType.OBSIDIAN_HELMET, Material.DIAMOND_HELMET, EquipmentSlotGroup.HEAD, 2.0, 3.0, 0.02, List.of("Casco con placas de obsidiana", "Superior a netherite", "+2 armadura, +3 dureza, +0.02 KB")));
        itemManager.registerItem(new AttributeArmorItem(this, CustomItemType.OBSIDIAN_CHESTPLATE, Material.DIAMOND_CHESTPLATE, EquipmentSlotGroup.CHEST, 3.0, 4.0, 0.02, List.of("Pechera de obsidiana laminada", "Superior a netherite", "+3 armadura, +4 dureza, +0.02 KB")));
        itemManager.registerItem(new AttributeArmorItem(this, CustomItemType.OBSIDIAN_LEGGINGS, Material.DIAMOND_LEGGINGS, EquipmentSlotGroup.LEGS, 3.0, 4.0, 0.02, List.of("Grebas diamantadas blindadas", "Superior a netherite", "+3 armadura, +4 dureza, +0.02 KB")));
        itemManager.registerItem(new AttributeArmorItem(this, CustomItemType.OBSIDIAN_BOOTS, Material.DIAMOND_BOOTS, EquipmentSlotGroup.FEET, 2.0, 2.5, 0.02, List.of("Botas de obsidiana pulida", "Superior a netherite", "+2 armadura, +2.5 dureza, +0.02 KB")));

        itemManager.registerItem(new AttributeArmorItem(this, CustomItemType.VOID_HELMET, Material.NETHERITE_HELMET, EquipmentSlotGroup.HEAD, 3.0, 3.0, 0.05, List.of("Casco del vacío", "Tope de línea", "+3 armadura, +3 dureza, +0.05 KB")));
        itemManager.registerItem(new AttributeArmorItem(this, CustomItemType.VOID_CHESTPLATE, Material.NETHERITE_CHESTPLATE, EquipmentSlotGroup.CHEST, 4.0, 4.0, 0.05, List.of("Pechera del vacío", "Tope de línea", "+4 armadura, +4 dureza, +0.05 KB")));
        itemManager.registerItem(new AttributeArmorItem(this, CustomItemType.VOID_LEGGINGS, Material.NETHERITE_LEGGINGS, EquipmentSlotGroup.LEGS, 3.5, 3.0, 0.05, List.of("Grebas del vacío", "Tope de línea", "+3.5 armadura, +3 dureza, +0.05 KB")));
        itemManager.registerItem(new AttributeArmorItem(this, CustomItemType.VOID_BOOTS, Material.NETHERITE_BOOTS, EquipmentSlotGroup.FEET, 2.0, 2.0, 0.05, List.of("Botas del vacío", "Tope de línea", "+2 armadura, +2 dureza, +0.05 KB")));

        itemManager.registerItem(new AttributeWeaponItem(this, CustomItemType.STEEL_SWORD, Material.IRON_SWORD, 3.0, 0.1, 0.0, List.of("Espada de acero templada", "Nivel netherite en daño", "+3 daño, +0.1 velocidad")));
        itemManager.registerItem(new AttributeWeaponItem(this, CustomItemType.OBSIDIAN_SWORD, Material.DIAMOND_SWORD, 4.0, 0.1, 0.0, List.of("Filo de obsidiana", "Superior a netherite", "+4 daño, +0.1 velocidad")));
        itemManager.registerItem(new AttributeWeaponItem(this, CustomItemType.VOID_SWORD, Material.NETHERITE_SWORD, 5.0, 0.05, 0.1, List.of("Hoja del vacío", "Tope de línea", "+5 daño, +0.05 velocidad, +0.1 KB res")));

        this.dailyRollManager = new DailyRollManager(this, itemManager);
        itemManager.registerItem(new LokiDice(this, dailyRollManager));
        itemManager.registerItem(new LokiRoleDice(this, roleManager));
        this.dailyMobRotationManager.refreshForDay(this.gameManager.getCurrentDay());

        // Register Recipes & Loot
        recipeManager.registerRecipes();
        getServer().getPluginManager().registerEvents(lootManager, this);
        getServer().getPluginManager().registerEvents(new CraftingListener(this), this);
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

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
            playerHudManager.cancel();
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

    public DiscordWebhookService getDiscordService() {
        return discordService;
    }

    public CombatLogManager getCombatLogManager() {
        return combatLogManager;
    }
}
