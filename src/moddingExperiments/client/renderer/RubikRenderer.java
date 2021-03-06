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
	
	public final static ResourceLocation texture = new ResourceLocation(ModInfo.TEXTURE_FOLDER, "textures/models/rubik.png");
	public RubikModel model;

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float partialTickTime) {
		renderRubik((RubikTileEntity) tileentity, x, y, z, partialTickTime);
	}

	public void renderRubik(RubikTileEntity rubik, double x, double y, double z, float partialTickTime) {
		this.model = rubik.getModel();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
		GL11.glScalef(-1.0F, -1.0F, 1.0F);
		
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		model.render(rubik, 0.0625F);

		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

}
