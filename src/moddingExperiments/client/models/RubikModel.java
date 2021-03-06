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

	// Custom ModelRenderer for better rendering
	private class PieceRenderer extends ModelRenderer {

		private int textureOffsetX;
		private int textureOffsetY;
		private boolean compiled;
		private int displayList;
		private ModelBase baseModel;

		public PieceRenderer(ModelBase model, int texX, int texY) {
			super(model, texX, texY);
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

		@SideOnly(Side.CLIENT)
		public void renderWithRotation(float scale, Matrix3i rotation, Vector3i tempRotation, float solveProgress) {
			if (!this.isHidden) {
				if (this.showModel) {
					if (!this.compiled) {
						this.compileDisplayList(scale);
					}

					GL11.glPushMatrix();
					GL11.glTranslatef(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

					float angle = rotation.getAngle();
					if (angle != 0) {
						angle = (float) Math.toDegrees(angle);
						angle *= solveProgress;
						Vector3f axis = rotation.getAxis();
						GL11.glRotatef(angle, axis.getX(), axis.getY(), axis.getZ());
					}

					int tempAngle = Math.abs(tempRotation.dot(new Vector3i(1, 1, 1)));
					if (tempAngle != 0) {
						Vector3f tempAxis = rotation.transpose().mult(tempRotation.normalize());
						GL11.glRotatef(tempAngle, tempAxis.x, tempAxis.y, tempAxis.z);
					}

					GL11.glCallList(this.displayList);
					GL11.glPopMatrix();
				}
			}
		}
	}

	public final int PIECES_PER_SIDE;
	public final int PIECES_PER_FACE;
	public final int PIECES_PER_CUBE;
	public final float BLOCK_SIZE = 16.0F;
	public final float CENTER = BLOCK_SIZE / 2.0F;
	public final float PIECE_WIDTH;
	public final int INT_PIECE_WIDTH;
	public final float OFFSET;

	PieceRenderer[][][] pieces;

	int texY;

	public RubikModel(int pps) {
		this.textureWidth = 64;
		this.textureHeight = 64;

		PIECES_PER_SIDE = pps;
		PIECES_PER_FACE = pps * pps;
		PIECES_PER_CUBE = pps * pps * pps;
		PIECE_WIDTH = BLOCK_SIZE / PIECES_PER_SIDE;
		INT_PIECE_WIDTH = (int) PIECE_WIDTH;

		OFFSET = (PIECE_WIDTH - INT_PIECE_WIDTH) / 2.0F;

		pieces = new PieceRenderer[PIECES_PER_SIDE][PIECES_PER_SIDE][PIECES_PER_SIDE];

		switch (PIECES_PER_SIDE) {
			case 2:
				texY = 0;
				break;
			case 3:
				texY = 16;
				break;
			case 4:
				texY = 26;
				break;
			case 5:
				texY = 34;
				break;
			case 6:
			case 7:
			case 8:
				texY = 40;
				break;
			default:
				texY = 44;
		}

		for (int x = 0; x < pieces.length; x++) {
			for (int y = 0; y < pieces.length; y++) {
				for (int z = 0; z < pieces.length; z++) {
					if (x != 0 && y != 0 && z != 0 && x != PIECES_PER_SIDE - 1 && y != PIECES_PER_SIDE - 1 && z != PIECES_PER_SIDE - 1) {
						continue;
					}
					PieceRenderer piece = new PieceRenderer(this, 0, texY);
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
					if (pieces[x][y][z] != null) {
						pieces[x][y][z].renderWithRotation(scale, rubik.getRotation(x, y, z), rubik.getTempRotation(x, y, z), rubik.getSolveProgress());
					}
				}
			}
		}
	}

	public void render(float scale) {
		for (int x = 0; x < pieces.length; x++) {
			for (int y = 0; y < pieces.length; y++) {
				for (int z = 0; z < pieces.length; z++) {
					if (pieces[x][y][z] != null) {
						pieces[x][y][z].renderWithRotation(scale, new Matrix3i().setIdentity(), new Vector3i(), 0);
					}
				}
			}
		}
	}

}
