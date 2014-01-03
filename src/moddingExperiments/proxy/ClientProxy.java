package moddingExperiments.proxy;

import moddingExperiments.client.core.RubikEventHandler;
import moddingExperiments.client.renderer.RubikItemRenderer;
import moddingExperiments.client.renderer.RubikRenderer;
import moddingExperiments.client.sound.SoundHandler;
import moddingExperiments.lib.ItemInfo;
import moddingExperiments.tileEntities.RubikTileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void initRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(RubikTileEntity.class, new RubikRenderer());
		MinecraftForgeClient.registerItemRenderer(ItemInfo.RUBIK_ITEM_ID + 256, new RubikItemRenderer());
	}

	@Override
	public void initSounds() {
		new SoundHandler();
	}

	@Override
	public void registerHandlers() {
		MinecraftForge.EVENT_BUS.register(new RubikEventHandler());
	}

}
