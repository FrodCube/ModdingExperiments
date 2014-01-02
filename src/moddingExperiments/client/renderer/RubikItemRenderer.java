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

		// TODO fix positions (shadow size!)
		switch (type) {
			case INVENTORY:
				GL11.glTranslatef(0.5F, 0.0F, 0.0F);
				GL11.glRotatef(90, 0, 1, 0);
				GL11.glTranslatef(-0.5F, 0.5F, 0.0F);
				break;
			case EQUIPPED:
				GL11.glRotatef(180, 0, 1, 0);
				GL11.glTranslatef(0.0F, 0.0F, 0.0F);
				break;
			case EQUIPPED_FIRST_PERSON:
				GL11.glRotatef(180, 0, 1, 0);
				GL11.glTranslatef(0.0F, -0.05F, 0.0F);
				break;
			case ENTITY:
				GL11.glScalef(0.8F,0.8F, 0.8F);
				GL11.glTranslatef(-0.5F, 0.00F, 0.5F);
				break;
			default:
				break;
		}

		Minecraft.getMinecraft().renderEngine.bindTexture(RubikRenderer.texture);
		models[dmg].render(0.0625F);

		GL11.glPopMatrix();
	}
}
