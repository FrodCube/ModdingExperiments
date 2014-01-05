package moddingExperiments.client.core;

import moddingExperiments.blocks.Blocks;
import moddingExperiments.config.ConfigurationHandler;
import moddingExperiments.items.Items;
import moddingExperiments.tileEntities.RubikTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RubikEventHandler {

	public static final float GLOW_TIME = 350;

	private boolean increasing = true;
	private int glow;

	@ForgeSubscribe
	public void onDrawBlockHighlightEvent(DrawBlockHighlightEvent event) {
		Minecraft minecraft = FMLClientHandler.instance().getClient();
		if (Minecraft.isGuiEnabled() /* && minecraft.inGameHasFocus */) {
			if (event.target.typeOfHit == EnumMovingObjectType.TILE) {
				if (event.player.worldObj.getBlockId(event.target.blockX, event.target.blockY, event.target.blockZ) == Blocks.rubik.blockID) {
					TileEntity te = event.player.worldObj.getBlockTileEntity(event.target.blockX, event.target.blockY, event.target.blockZ);
					if (te instanceof RubikTileEntity) {
						drawFaceSelector((RubikTileEntity) te, event);
					}

				}
			}

		}
	}

	private void drawFaceSelector(RubikTileEntity rubik, DrawBlockHighlightEvent event) {
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

		int front = ((MathHelper.floor_double((Math.toDegrees(Math.atan2(z + 0.5 - playerZ, x + 0.5 - playerX)) - 45) * 4.0 / 360.0)) & 3) + 1;
		front = front == 1 ? 2 : (front == 2 ? 5 : front);

		// TODO config option
		glow += increasing ? 6 : -6;
		if (glow >= GLOW_TIME) {
			glow = (int) GLOW_TIME;
			increasing = false;
		} else if (glow < 0) {
			glow = 0;
			increasing = true;
		}
		float progress = glow / GLOW_TIME;

		GL11.glPushMatrix();
		GL11.glScaled(0.99, 0.99, 0.99);
		GL11.glTranslated(x - playerX + 0.5, y - playerY + 0.5, z - playerZ + 0.5);
		GL11.glColor4f(1, 1, 1, 0.1F + progress * 0.1F);

		if (rubik.isMoving()) {
			float prog = 1.0F + 0.42F * rubik.getTempAngleProgress();
			GL11.glColor4f(1, 1, 1, 0.1F + rubik.getTempAngleProgress() * 0.7F);
			GL11.glScaled(prog, prog, prog);
			drawBox(1);
		} else if (event.player.getCurrentEquippedItem() == null || event.player.getCurrentEquippedItem().itemID != Items.scramblerItem.itemID) {
			if (face == 0 || face == 1) {
				if (front < 4) {
					GL11.glRotatef(90, 0, 1, 0);
					GL11.glTranslated(0, 0, hitX);
				} else {
					GL11.glTranslated(0, 0, hitZ);
				}
			} else if (face == front) {
				GL11.glRotatef(90, 1, 0, 0);
				GL11.glTranslated(0, 0, 1 - (hitY + pieceWidth));
			} else {
				if (front < 4) {
					GL11.glTranslated(0, 0, hitZ);
				} else {
					GL11.glRotatef(90, 0, 1, 0);
					GL11.glTranslated(0, 0, hitX);
				}
			}

			drawBox(pieceWidth);
		} else {
			drawBox(1);
		}
		
		GL11.glPopMatrix();
		
		if (rubik.isScrambling()) {
			GL11.glPushMatrix();
			GL11.glTranslated(x - playerX + 0.5, y - playerY + 1.3, z - playerZ + 0.5);
			double angle = event.player.prevRotationYaw + (event.player.rotationYaw - event.player.prevRotationYaw) * event.partialTicks;
			GL11.glRotated(-angle, 0, 1, 0);
			drawProgressBar(rubik.getScrambleProgress());
			GL11.glPopMatrix();
		}

		
	}

	private void drawBox(double width) {
		Tessellator tessellator = Tessellator.instance;

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
	
	private void drawProgressBar(float progress) {
		Tessellator tessellator = Tessellator.instance;
		
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		double w = -0.7 + 1.4 * progress;
		
		GL11.glColor4f(1, 1, 1, 0.3F);

		tessellator.startDrawingQuads();
		tessellator.addVertex(w, 0.0, 0.0);
		tessellator.addVertex(w, 0.15, 0.0);
		tessellator.addVertex(0.7, 0.15, 0.0);
		tessellator.addVertex(0.7, 0.0, 0.0);
		tessellator.draw();
		
		GL11.glColor4f(0, 1, 0, 0.3F);

		tessellator.startDrawingQuads();
		tessellator.addVertex(-0.7, 0.0, 0.0);
		tessellator.addVertex(-0.7, 0.15, 0.0);
		tessellator.addVertex(w, 0.15, 0.0);
		tessellator.addVertex(w, 0.0, 0.0);
		tessellator.draw();
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

}
