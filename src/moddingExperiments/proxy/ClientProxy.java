package moddingExperiments.proxy;

import moddingExperiments.client.renderer.RubikRenderer;
import moddingExperiments.tileEntities.RubikTileEntity;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void initRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(RubikTileEntity.class, new RubikRenderer());
	}

}
