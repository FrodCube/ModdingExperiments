package moddingExperiments.client.core;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import moddingExperiments.blocks.Blocks;
import moddingExperiments.config.ConfigurationHandler;
import moddingExperiments.tileEntities.RubikTileEntity.Piece;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RubikEventHandler {

	@ForgeSubscribe
	public void onDrawBlockHighlightEvent(DrawBlockHighlightEvent event) {
		Minecraft minecraft = FMLClientHandler.instance().getClient();
		if (Minecraft.isGuiEnabled() && minecraft.inGameHasFocus) {
			if (event.target.typeOfHit == EnumMovingObjectType.TILE) {
				if (event.player.worldObj.getBlockId(event.target.blockX, event.target.blockY, event.target.blockZ) == Blocks.rubik.blockID) {
					drawFaceSelector(event);
				}
			}

		}
	}

	private void drawFaceSelector(DrawBlockHighlightEvent event) {
		double playerX = event.player.prevPosX + (event.player.posX - event.player.prevPosX) * event.partialTicks;
		double playerY = event.player.prevPosY + (event.player.posY - event.player.prevPosY) * event.partialTicks;
		double playerZ = event.player.prevPosZ + (event.player.posZ - event.player.prevPosZ) * event.partialTicks;

		int x = event.target.blockX;
		int y = event.target.blockY;
		int z = event.target.blockZ;

		int pps = Math.min(event.player.worldObj.getBlockMetadata(x, y, z) + 2, ConfigurationHandler.MAX_SIZE);
		double pieceWidth = 1.0 / pps;

		int face = event.target.sideHit;

		double hitX = pieceWidth * (int) ((event.target.hitVec.xCoord - x) / pieceWidth);
		double hitY = pieceWidth * (int) ((event.target.hitVec.yCoord - y) / pieceWidth);
		double hitZ = pieceWidth * (int) ((event.target.hitVec.zCoord - z) / pieceWidth);

		// TODO rotation f(front)

		int front = (MathHelper.floor_double((double) (event.player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) + 1;
		front = front == 1 ? 2 : (front == 2 ? 5 : front);

		GL11.glPushMatrix();
		GL11.glScaled(0.99, 0.99, 0.99);
		GL11.glTranslated(x - playerX + 0.5, y - playerY + 0.5, z - playerZ + 0.5);

		if (face == 0 || face == 1) {
			int angle = -90 * (int) (((event.player.rotationYaw - 45) / 90) % 4);
			GL11.glRotatef(angle, 0, 1, 0);

			double transZ = 0;
			switch (front) {
				case 2:
					transZ = hitX;
					break;
				case 3:
					transZ = 1 - (hitX + pieceWidth);
					break;
				case 4:
					transZ = 1 - (hitZ + pieceWidth);
					break;
				case 5:
					transZ = hitZ;
					break;
			}

			GL11.glTranslated(0, 0, transZ);
		} else if (face == front) {
			GL11.glRotatef(90, 1, 0, 0);
			GL11.glTranslated(0, 0, 1 - (hitY + pieceWidth));
		} else {
			int angle = -90 * (int) ((((event.player.rotationYaw - 45) / 90) + 1) % 4);
			GL11.glRotatef(angle, 0, 1, 0);

			double transZ = 0;
			switch (front) {
				case 2:
					transZ = hitZ;
					break;
				case 3:
					transZ = 1 - (hitZ + pieceWidth);
					break;
				case 4:
					transZ = hitX;
					break;
				case 5:

					transZ = 1 - (hitX + pieceWidth);
					break;
			}

			GL11.glTranslated(0, 0, transZ);
		}

		drawBox(pieceWidth);

		GL11.glPopMatrix();
	}

	private void drawBox(double width) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glColor4f(1, 1, 1, 0.25F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		tessellator.startDrawingQuads();
		tessellator.addVertex(-0.5, -0.5, -0.5);
		tessellator.addVertex(-0.5, 0.5, -0.5);
		tessellator.addVertex(0.5, 0.5, -0.5);
		tessellator.addVertex(0.5, -0.5, -0.5);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.addVertex(-0.5, -0.5, width - 0.5);
		tessellator.addVertex(0.5, -0.5, width - 0.5);
		tessellator.addVertex(0.5, 0.5, width - 0.5);
		tessellator.addVertex(-0.5, 0.5, width - 0.5);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.addVertex(-0.5, 0.5, -0.5);
		tessellator.addVertex(-0.5, 0.5, width - 0.5);
		tessellator.addVertex(0.5, 0.5, width - 0.5);
		tessellator.addVertex(0.5, 0.5, -0.5);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.addVertex(-0.5, -0.5, -0.5);
		tessellator.addVertex(0.5, -0.5, -0.5);
		tessellator.addVertex(0.5, -0.5, width - 0.5);
		tessellator.addVertex(-0.5, -0.5, width - 0.5);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.addVertex(-0.5, -0.5, -0.5);
		tessellator.addVertex(-0.5, -0.5, width - 0.5);
		tessellator.addVertex(-0.5, 0.5, width - 0.5);
		tessellator.addVertex(-0.5, 0.5, -0.5);
		tessellator.draw();

		tessellator.startDrawingQuads();
		tessellator.addVertex(0.5, -0.5, -0.5);
		tessellator.addVertex(0.5, 0.5, -0.5);
		tessellator.addVertex(0.5, 0.5, width - 0.5);
		tessellator.addVertex(0.5, -0.5, width - 0.5);
		tessellator.draw();

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

}
