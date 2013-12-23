package moddingExperiments.tileEntities;

import moddingExperiments.client.models.RubikModel;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;

public class RubikTE extends TileEntity {

	private class Piece {
		public float angleX;
		public float angleY;
		public float angleZ;

		public Piece(float angleX, float angleY, float angleZ) {
			this.angleX = angleX;
			this.angleY = angleY;
			this.angleZ = angleZ;
		}
		
		public void updateAngles() {
			float ang = (float)(2 * Math.PI);
			if (angleX < 0) angleX += ang;
			if (angleX >= ang) angleX -= ang;
			if (angleY < 0) angleY += ang;
			if (angleY >= ang) angleY -= ang;
			if (angleZ < 0) angleZ += ang;
			if (angleZ >= ang) angleZ -= ang;
		}
	}

	public final float TOTAL_PROGRESS = 30.0F;

	private int progress;
	private Piece[][][] pieces;
	private boolean resync;
	private int move;

	public RubikTE() {
		pieces = new Piece[RubikModel.PIECES_PER_SIDE][RubikModel.PIECES_PER_SIDE][RubikModel.PIECES_PER_SIDE];
		createPieces();
	}

	private void createPieces() {
		for (int x = 0; x < RubikModel.PIECES_PER_SIDE; x++) {
			for (int y = 0; y < RubikModel.PIECES_PER_SIDE; y++) {
				for (int z = 0; z < RubikModel.PIECES_PER_SIDE; z++) {
					pieces[x][y][z] = new Piece(0.0F, 0.0F, 0.0F);
				}
			}
		}
	}

	@Override
	public void updateEntity() {
		move = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		if (move > 0) {
			progress++;
			updateMove();
			if (progress == TOTAL_PROGRESS && move > 0) {
				progress = 0;
				if (!worldObj.isRemote) {
					worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3);
				} 
			}
		}
		
		if (worldObj.isRemote) {
			System.out.println(getAngleX(0));
		}
	}

	private void updateMove() {
		//TODO Support for all sizes up to 16x16 (metadata = size)
		int pps = RubikModel.PIECES_PER_SIDE;
		Piece[] face = new Piece[RubikModel.PIECES_PER_FACE];
		int i = 0;
		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					if (move == 1 && y != pps - 1) continue;
					if (move == 2 && y != 0) continue;
					if (move == 3 && z != pps - 1) continue;
					if (move == 4 && z != 0) continue;
					if (move == 5 && x != 0) continue;
					if (move == 6 && x != pps - 1) continue;
					face[i] = pieces[x][y][z];
					i++;
				}
			}
		}
		
		float ang = (float) (Math.PI / 2) / TOTAL_PROGRESS; 
		for (Piece piece : face) {
			if (move == 1) piece.angleY -= ang;
			if (move == 2) piece.angleY += ang;;
			if (move == 3) piece.angleZ += ang;;
			if (move == 4) piece.angleZ -= ang;;
			if (move == 5) piece.angleX -= ang;;
			if (move == 6) piece.angleX += ang;;
			
			piece.updateAngles();
		}
	}
	
	public float getAngleX(int i) {
		return pieces[i % RubikModel.PIECES_PER_SIDE][i / RubikModel.PIECES_PER_FACE][(i % RubikModel.PIECES_PER_FACE) / RubikModel.PIECES_PER_SIDE].angleX;
	}
	
	public float getAngleY(int i) {
		return pieces[i % RubikModel.PIECES_PER_SIDE][i / RubikModel.PIECES_PER_FACE][(i % RubikModel.PIECES_PER_FACE) / RubikModel.PIECES_PER_SIDE].angleY;
	}
	
	public float getAngleZ(int i) {
		return pieces[i % RubikModel.PIECES_PER_SIDE][i / RubikModel.PIECES_PER_FACE][(i % RubikModel.PIECES_PER_FACE) / RubikModel.PIECES_PER_SIDE].angleZ;
	}

	@Override
	public void readFromNBT(NBTTagCompound comp) {
		super.readFromNBT(comp);

	}

	@Override
	public void writeToNBT(NBTTagCompound comp) {
		super.writeToNBT(comp);

	}

}
