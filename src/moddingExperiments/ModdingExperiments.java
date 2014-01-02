package moddingExperiments;

import net.minecraftforge.common.MinecraftForge;
import moddingExperiments.blocks.Blocks;
import moddingExperiments.config.ConfigurationHandler;
import moddingExperiments.entities.Entities;
import moddingExperiments.items.Items;
import moddingExperiments.lib.ModInfo;
import moddingExperiments.network.PacketHandler;
import moddingExperiments.proxy.ClientProxy;
import moddingExperiments.proxy.CommonProxy;
import moddingExperiments.util.LogHelper;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = ModInfo.NAME, version = ModInfo.VERSION, name = ModInfo.NAME)
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = {ModInfo.CHANNEL}, packetHandler = PacketHandler.class)
public class ModdingExperiments {
	
	@Instance(ModInfo.MODID)
	public static ModdingExperiments instance;
	
	@SidedProxy(clientSide = "moddingExperiments.proxy.ClientProxy", serverSide = "moddingExperiments.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	//TODO achievements, scrambler, solver
	
	@EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        ConfigurationHandler.init(e.getSuggestedConfigurationFile());
    	proxy.initRenderers();
    	proxy.initSounds();
    }
    
    @EventHandler
    public void load(FMLInitializationEvent e) {
    	Blocks.init();
    	Items.init();
    	Entities.init();
    	
    	Items.registerNames();
		Items.registerRecipies();
		
		Blocks.registerNames();
		Blocks.registerRecipies();
		Blocks.registerTileEntities();
    }

}
