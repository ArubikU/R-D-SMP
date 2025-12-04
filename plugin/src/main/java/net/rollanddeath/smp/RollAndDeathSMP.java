package net.rollanddeath.smp;

import net.rollanddeath.smp.core.LifeManager;
import net.rollanddeath.smp.core.modifiers.ModifierManager;
import net.rollanddeath.smp.core.protection.ProtectionListener;
import net.rollanddeath.smp.core.protection.ProtectionManager;
import net.rollanddeath.smp.core.teams.TeamCommand;
import net.rollanddeath.smp.core.teams.TeamListener;
import net.rollanddeath.smp.core.teams.TeamManager;
import net.rollanddeath.smp.core.teams.WarTrackerTask;
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
import net.rollanddeath.smp.core.mobs.MobCommand;
import net.rollanddeath.smp.core.items.impl.*;
import net.rollanddeath.smp.core.mobs.impl.*;
import net.rollanddeath.smp.core.game.GameManager;
import net.rollanddeath.smp.core.game.ScoreboardManager;
import net.rollanddeath.smp.core.game.WebStatusManager;
import net.rollanddeath.smp.core.game.AnnounceManager;
import net.rollanddeath.smp.core.items.CraftingListener;
import net.rollanddeath.smp.core.commands.AdminCommand;
import net.rollanddeath.smp.core.items.DailyRollManager;
import net.rollanddeath.smp.core.commands.DailyCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class RollAndDeathSMP extends JavaPlugin {

    private LifeManager lifeManager;
    private ModifierManager modifierManager;
    private ProtectionManager protectionManager;
    private TeamManager teamManager;
    private RoleManager roleManager;
    private MobManager mobManager;
    private ItemManager itemManager;
    private RecipeManager recipeManager;
    private LootManager lootManager;
    private GameManager gameManager;
    private DailyRollManager dailyRollManager;
    private WebStatusManager webStatusManager;
    private AnnounceManager announceManager;

    @Override
    public void onEnable() {
        // Load Config
        saveDefaultConfig();

        // Initialize Managers
        this.lifeManager = new LifeManager(this);
        this.modifierManager = new ModifierManager(this);
        this.teamManager = new TeamManager(this);
        this.protectionManager = new ProtectionManager(this, teamManager);
        
        // Start War Tracker
        new WarTrackerTask(teamManager).runTaskTimer(this, 100L, 60L);

        this.roleManager = new RoleManager(this);
        this.mobManager = new MobManager(this);
        this.itemManager = new ItemManager(this);
        this.recipeManager = new RecipeManager(this);
        this.lootManager = new LootManager(this);
        this.gameManager = new GameManager(this);
        this.announceManager = new AnnounceManager(this);

        // Initialize UI/Web
        this.webStatusManager = new WebStatusManager(this);
        getServer().getPluginManager().registerEvents(new ScoreboardManager(this), this);

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
        modifierManager.registerModifier(new RandomBlocksModifier(this));
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

        this.dailyRollManager = new DailyRollManager(this, itemManager);

        // Register Recipes & Loot
        recipeManager.registerRecipes();
        getServer().getPluginManager().registerEvents(lootManager, this);
        getServer().getPluginManager().registerEvents(new CraftingListener(this), this);

        // Register Listeners
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this, lifeManager), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(protectionManager), this);
        getServer().getPluginManager().registerEvents(new TeamListener(teamManager), this);
        getServer().getPluginManager().registerEvents(new ChatListener(teamManager, roleManager), this);

        // Register Commands
        getCommand("team").setExecutor(new TeamCommand(teamManager));
        getCommand("role").setExecutor(new RoleCommand(this));
        getCommand("mob").setExecutor(new MobCommand(this));
        getCommand("item").setExecutor(new ItemCommand(this));
        getCommand("rd").setExecutor(new AdminCommand(this));
        getCommand("daily").setExecutor(new DailyCommand(dailyRollManager));
        
        net.rollanddeath.smp.core.commands.MenuCommand menuCmd = new net.rollanddeath.smp.core.commands.MenuCommand(this);
        getCommand("menu").setExecutor(menuCmd);
        getServer().getPluginManager().registerEvents(menuCmd, this);

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

    public DailyRollManager getDailyRollManager() {
        return dailyRollManager;
    }

    public WebStatusManager getWebStatusManager() {
        return webStatusManager;
    }
}
