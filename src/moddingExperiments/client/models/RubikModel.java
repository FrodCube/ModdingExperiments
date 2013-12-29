package moddingExperiments.client.models;

import moddingExperiments.tileEntities.RubikTileEntity;
import moddingExperiments.util.Matrix3i;
import moddingExperiments.util.Vector3i;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RubikModel extends ModelBase {

	// Custom ModelBox for easier texturing
	private class PieceBox extends ModelBox {

		private final PositionTextureVertex[] red = new PositionTextureVertex[] {};

		private PositionTextureVertex[] vertexPositions;
		private TexturedQuad[] quadList;

		public PieceBox(ModelRenderer model, int texX, int texY, float x, float y, float z, int xSize, int ySize, int zSize, float scale) {
			super(model, texX, texY, x, y, z, xSize, ySize, zSize, scale);
			this.quadList[0] = new TexturedQuad(this.quadList[0].vertexPositions, 0, 8, 0, 8, model.textureWidth, model.textureHeight);
			if (model.mirror) {
				for (int j1 = 0; j1 < this.quadList.length; ++j1) {
					this.quadList[j1].flipFace();
				}
			}
		}

	}

	// Custom ModelRenderer for better rendering
	private class PieceRenderer extends ModelRenderer {

		private int textureOffsetX;
		private int textureOffsetY;
		private boolean compiled;
		private int displayList;
		private ModelBase baseModel;

		public PieceRenderer(ModelBase model) {
			super(model);
		}

		@SideOnly(Side.CLIENT)
		private void compileDisplayList(float scale) {
			this.displayList = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(this.displayList, GL11.GL_COMPILE);
			Tessellator tessellator = Tessellator.instance;

			for (int i = 0; i < this.cubeList.size(); ++i) {
				((ModelBox) this.cubeList.get(i)).render(tessellator, scale);
			}

			GL11.glEndList();
			this.compiled = true;
		}

		@Override
		public ModelRenderer addBox(float x, float y, float z, int xSize, int ySize, int zSize) {
			cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, x, y, z, xSize, ySize, zSize, 0.0F));
			return this;
		}

		@SideOnly(Side.CLIENT)
		public void renderWithRotation(float scale, Matrix3i rotation, Vector3i tempRotation) {
			if (!this.isHidden) {
				if (this.showModel) {
					if (!this.compiled) {
						this.compileDisplayList(scale);
					}

					GL11.glPushMatrix();
					GL11.glTranslatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

					Vector3f axis = rotation.getAxis();
					GL11.glRotatef((float) Math.toDegrees(rotation.getAngle()), axis.x, axis.y, axis.z);

					GL11.glRotatef(tempRotation.getX(), 1.0F, 0.0F, 0.0F);
					GL11.glRotatef(tempRotation.getY(), 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(tempRotation.getZ(), 0.0F, 0.0F, 1.0F);

					GL11.glCallList(this.displayList);
					GL11.glPopMatrix();
				}
			}
		}
	}

	public static final int PIECES_PER_SIDE = 3;
	public static final int PIECES_PER_FACE = PIECES_PER_SIDE * PIECES_PER_SIDE;
	public static final int PIECES_PER_CUBE = PIECES_PER_SIDE * PIECES_PER_SIDE * PIECES_PER_SIDE;
	public static final float BLOCK_SIZE = 16.0F;
	public static final float CENTER = BLOCK_SIZE / 2.0F;
	public static final float PIECE_WIDTH = BLOCK_SIZE / PIECES_PER_SIDE;
	public static final int INT_PIECE_WIDTH = (int) PIECE_WIDTH;
	public static final float OFFSET = 0.0F /*  0.5F * (1 - PIECES_PER_SIDE * INT_PIECE_WIDTH) */;

	PieceRenderer[] pieces = new PieceRenderer[PIECES_PER_CUBE];

	public RubikModel() {
		for (int i = 0; i < pieces.length; i++) {
			PieceRenderer piece = new PieceRenderer(this);
			piece.addBox(OFFSET + PIECE_WIDTH * (i % PIECES_PER_SIDE) - CENTER, OFFSET + PIECE_WIDTH * (i / PIECES_PER_FACE) - CENTER, OFFSET + PIECE_WIDTH * ((i % PIECES_PER_FACE) / PIECES_PER_SIDE) - CENTER, INT_PIECE_WIDTH, INT_PIECE_WIDTH, INT_PIECE_WIDTH);
			piece.setRotationPoint(CENTER, -CENTER, -CENTER);
			pieces[i] = piece;
		}
	}

	public void render(RubikTileEntity rubik, float scale) {
		for (int i = 0; i < pieces.length; i++) {
			pieces[i].renderWithRotation(scale, rubik.getRotation(i), rubik.getTempRotation(i));
		}
	}

}
