package moddingExperiments.client.renderer;

import moddingExperiments.client.models.RubikModel;
import moddingExperiments.lib.ModInfo;
import moddingExperiments.tileEntities.RubikTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class RubikRenderer extends TileEntitySpecialRenderer {

	public RubikModel model;
	public ResourceLocation texture = new ResourceLocation(ModInfo.MODID, "textures/models/rubik.png");

	public RubikRenderer() {
		this.model = new RubikModel();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float partialTickTime) {
		renderRubik((RubikTileEntity) tileentity, x, y, z, partialTickTime);
	}

	public void renderRubik(RubikTileEntity rubik, double x, double y, double z, float partialTickTime) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		
		//Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		model.render(rubik, 0.0625F);

		GL11.glPopMatrix();
	}

}
