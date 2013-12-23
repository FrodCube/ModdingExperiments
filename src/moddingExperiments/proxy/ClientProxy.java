package moddingExperiments.proxy;

import moddingExperiments.client.renderer.RubikRenderer;
import moddingExperiments.tileEntities.RubikTE;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void initRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(RubikTE.class, new RubikRenderer());
	}

}
