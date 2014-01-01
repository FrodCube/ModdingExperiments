package moddingExperiments.proxy;

import net.minecraftforge.client.MinecraftForgeClient;
import moddingExperiments.client.renderer.RubikItemRenderer;
import moddingExperiments.client.renderer.RubikRenderer;
import moddingExperiments.lib.ItemInfo;
import moddingExperiments.tileEntities.RubikTileEntity;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void initRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(RubikTileEntity.class, new RubikRenderer());
		MinecraftForgeClient.registerItemRenderer(ItemInfo.RUBIK_ITEM_ID + 256, new RubikItemRenderer());
	}
	
	//TODO sounds!

}
