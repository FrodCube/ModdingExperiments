package moddingExperiments;

import moddingExperiments.achievements.Achievements;
import moddingExperiments.blocks.Blocks;
import moddingExperiments.client.core.RubikEventHandler;
import moddingExperiments.config.ConfigurationHandler;
import moddingExperiments.creativeTabs.RubikCreativeTab;
import moddingExperiments.creativeTabs.Tabs;
import moddingExperiments.entities.Entities;
import moddingExperiments.items.Items;
import moddingExperiments.lib.AchievementInfo;
import moddingExperiments.lib.ModInfo;
import moddingExperiments.network.PacketHandler;
import moddingExperiments.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = ModInfo.NAME, version = ModInfo.VERSION, name = ModInfo.NAME)
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { ModInfo.CHANNEL }, packetHandler = PacketHandler.class)
public class RubiksCubes {

	public static AchievementPage achievementPage;

	@Instance(ModInfo.MODID)
	public static RubiksCubes instance;

	@SidedProxy(clientSide = "moddingExperiments.proxy.ClientProxy", serverSide = "moddingExperiments.proxy.CommonProxy")
	public static CommonProxy proxy;

	// TODO achievements, scrambler, solver

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		ConfigurationHandler.init(e.getSuggestedConfigurationFile());
		proxy.initRenderers();
		proxy.initSounds();
		proxy.registerHandlers();
	}

	@EventHandler
	public void load(FMLInitializationEvent e) {
		Tabs.init();
		
		Blocks.init();
		Items.init();
		Entities.init();

		Items.registerNames();
		Items.registerRecipies();

		Blocks.registerNames();
		Blocks.registerRecipies();
		Blocks.registerTileEntities();		
		
		Achievements.init();
		Achievements.registerNames();
		
		achievementPage = new AchievementPage(AchievementInfo.PAGE_NAME, Achievements.pocketCube, Achievements.rubikCube, Achievements.revengeCube, Achievements.professorCube);
		AchievementPage.registerAchievementPage(achievementPage);
	}

}
