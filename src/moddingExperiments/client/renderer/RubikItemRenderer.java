package moddingExperiments.client.renderer;

import moddingExperiments.client.models.RubikModel;
import moddingExperiments.config.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

public class RubikItemRenderer implements IItemRenderer {

	private RubikModel[] models;
	
	public RubikItemRenderer() {
		models = new RubikModel[ConfigurationHandler.MAX_SIZE - 1];
		for (int i = 0; i < models.length; i++) {
			models[i] = new RubikModel(i + 2);
		}
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
		int dmg = Math.min(stack.getItemDamage(), ConfigurationHandler.MAX_SIZE - 2);

		GL11.glPushMatrix();
		GL11.glScalef(-1F, -1F, 1F);
		
		//TODO fix positions
		switch (type) {
			case INVENTORY:
				GL11.glTranslatef(0.0F, 0.9F, 0.0F);
				break;
			case EQUIPPED:
				GL11.glTranslatef(0.0F, 0.0F, 0.0F);
				break;
			case EQUIPPED_FIRST_PERSON:
				GL11.glTranslatef(-0.9F, -0.1F, 1.0F);
				break;
			case ENTITY:
				break;
			default:
				break;
		}

		Minecraft.getMinecraft().renderEngine.bindTexture(RubikRenderer.texture);
		models[dmg].render(0.0625F);

		GL11.glPopMatrix();
	}
}
