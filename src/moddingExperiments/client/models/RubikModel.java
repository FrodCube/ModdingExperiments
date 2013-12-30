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
		public final float posX1;
		public final float posY1;
		public final float posZ1;
		public final float posX2;
		public final float posY2;
		public final float posZ2;
		public String name;

		public PieceBox(ModelRenderer model, int texX, int texY, float x, float y, float z, int xSize, int ySize, int zSize, float scale, int pieceX, int pieceY, int pieceZ) {
			super(model, texX, texY, x, y, z, xSize, ySize, zSize, scale);
			this.posX1 = x;
			this.posY1 = y;
			this.posZ1 = z;
			this.posX2 = x + (float) xSize;
			this.posY2 = y + (float) ySize;
			this.posZ2 = z + (float) zSize;
			this.vertexPositions = new PositionTextureVertex[8];
			this.quadList = new TexturedQuad[6];
			float f4 = x + (float) xSize;
			float f5 = y + (float) ySize;
			float f6 = z + (float) zSize;
			x -= scale;
			y -= scale;
			z -= scale;
			f4 += scale;
			f5 += scale;
			f6 += scale;

			if (model.mirror) {
				float f7 = f4;
				f4 = x;
				x = f7;
			}

			PositionTextureVertex positiontexturevertex = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
			PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f4, y, z, 0.0F, 8.0F);
			PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(f4, f5, z, 8.0F, 8.0F);
			PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(x, f5, z, 8.0F, 0.0F);
			PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(x, y, f6, 0.0F, 0.0F);
			PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f4, y, f6, 0.0F, 8.0F);
			PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(f4, f5, f6, 8.0F, 8.0F);
			PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(x, f5, f6, 8.0F, 0.0F);
			this.vertexPositions[0] = positiontexturevertex;
			this.vertexPositions[1] = positiontexturevertex1;
			this.vertexPositions[2] = positiontexturevertex2;
			this.vertexPositions[3] = positiontexturevertex3;
			this.vertexPositions[4] = positiontexturevertex4;
			this.vertexPositions[5] = positiontexturevertex5;
			this.vertexPositions[6] = positiontexturevertex6;
			this.vertexPositions[7] = positiontexturevertex7;
			this.quadList[0] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex5, positiontexturevertex1, positiontexturevertex2, positiontexturevertex6 }, 0, 8, 0, 8, model.textureWidth, model.textureHeight);
			this.quadList[1] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex, positiontexturevertex4, positiontexturevertex7, positiontexturevertex3 }, 8, 8, 0, 8, model.textureWidth, model.textureHeight);
			this.quadList[2] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex5, positiontexturevertex4, positiontexturevertex, positiontexturevertex1 }, 16, 8, 0, 8, model.textureWidth, model.textureHeight);
			this.quadList[3] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex2, positiontexturevertex3, positiontexturevertex7, positiontexturevertex6 }, 0, 8, 8, 8, model.textureWidth, model.textureHeight);
			this.quadList[4] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex1, positiontexturevertex, positiontexturevertex3, positiontexturevertex2 }, 8, 8, 8, 8, model.textureWidth, model.textureHeight);
			this.quadList[5] = new TexturedQuad(new PositionTextureVertex[] { positiontexturevertex4, positiontexturevertex5, positiontexturevertex6, positiontexturevertex7 }, 16, 8, 8, 8, model.textureWidth, model.textureHeight);

			if (model.mirror) {
				for (int j1 = 0; j1 < this.quadList.length; ++j1) {
					this.quadList[j1].flipFace();
				}
			}
		}

		@SideOnly(Side.CLIENT)
		public void render(Tessellator par1Tessellator, float par2) {
			for (int i = 0; i < this.quadList.length; ++i) {
				this.quadList[i].draw(par1Tessellator, par2);
			}
		}

		public PieceBox func_78244_a(String par1Str) {
			this.name = par1Str;
			return this;
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

		public ModelRenderer addBox(float x, float y, float z, int xSize, int ySize, int zSize, int pieceX, int pieceY, int pieceZ) {
			cubeList.add(new PieceBox(this, this.textureOffsetX, this.textureOffsetY, x, y, z, xSize, ySize, zSize, 0.0F, pieceX, pieceY, pieceZ));
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
					GL11.glRotatef((float) Math.toDegrees(rotation.getAngle()), axis.x, -axis.y, -axis.z);

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
	public static final float OFFSET = 0.0F /*
											 * 0.5F * (1 - PIECES_PER_SIDE *
											 * INT_PIECE_WIDTH)
											 */;

	PieceRenderer[][][] pieces = new PieceRenderer[PIECES_PER_SIDE][PIECES_PER_SIDE][PIECES_PER_SIDE];

	public RubikModel() {
		for (int x = 0; x < pieces.length; x++) {
			for (int y = 0; y < pieces.length; y++) {
				for (int z = 0; z < pieces.length; z++) {

					PieceRenderer piece = new PieceRenderer(this);
					piece.addBox(OFFSET + PIECE_WIDTH * x - CENTER, OFFSET + PIECE_WIDTH * y - CENTER, OFFSET + PIECE_WIDTH * z - CENTER, INT_PIECE_WIDTH, INT_PIECE_WIDTH, INT_PIECE_WIDTH);
					piece.setRotationPoint(CENTER, -CENTER, -CENTER);
					pieces[x][y][z] = piece;
				}
			}
		}
	}

	public void render(RubikTileEntity rubik, float scale) {
		for (int x = 0; x < pieces.length; x++) {
			for (int y = 0; y < pieces.length; y++) {
				for (int z = 0; z < pieces.length; z++) {
//					if (y != 0) {
//						continue;
//					}
					pieces[x][y][z].renderWithRotation(scale, rubik.getRotation(x, y, z), rubik.getTempRotation(x, y, z));
				}
			}
		}
		
		//pieces[0][0][0].renderWithRotation(scale, rubik.getRotation(0, 0, 0), rubik.getTempRotation(0, 0, 0));
		
//		Vector3i r1 = new Vector3i(90, 0, 0);
//		Vector3i r2 = new Vector3i(0, 90, 0);
//		Vector3i r3 = new Vector3i(0, 0, 90);
//		Vector3i r4 = new Vector3i(0, -90, 0);
//		Matrix3i m = new Matrix3i(1, 0, 0, 0, 1, 0, 0, 0, 1);
//		m = m.rotate(r1);
//		m = m.rotate(r2);
//		m = m.rotate(r3);
//		m = m.rotate(r4);
//		Vector3i v = new Vector3i(0, 180, 0);
//		
//		pieces[0][0][0].renderWithRotation(scale, m, v);
//		pieces[1][0][0].renderWithRotation(scale, m, v);
//		pieces[2][0][0].renderWithRotation(scale, m, v);
//		pieces[0][0][0].renderWithRotation(scale, m, v);
//		pieces[0][1][0].renderWithRotation(scale, m, v);
//		pieces[0][2][0].renderWithRotation(scale, m, v);
//		pieces[0][0][0].renderWithRotation(scale, m, v);
//		pieces[0][0][1].renderWithRotation(scale, m, v);
//		pieces[0][0][2].renderWithRotation(scale, m, v);
	}

}
