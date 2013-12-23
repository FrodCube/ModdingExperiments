package moddingExperiments.client.renderer;

import moddingExperiments.client.models.RubikModel;
import moddingExperiments.tileEntities.RubikTE;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

public class RubikRenderer extends TileEntitySpecialRenderer {
	
	public RubikModel model;
	
	public RubikRenderer() {
		this.model = new RubikModel();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,	double z, float partialTickTime) {
		renderRubik((RubikTE)tileentity, x, y, z, partialTickTime);
	}
	
	public void renderRubik(RubikTE rubik, double x, double y, double z, float partialTickTime) {
		GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);     
       
        model.render(rubik, 0.0625F);
        
        GL11.glPopMatrix();
	}


}
