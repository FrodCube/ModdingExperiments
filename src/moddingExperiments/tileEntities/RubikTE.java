package moddingExperiments.tileEntities;

import java.util.Random;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import moddingExperiments.client.models.RubikModel;
import moddingExperiments.util.Vector3;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class RubikTE extends TileEntity {

	private class Piece {
		public int angleX;
		public int angleY;
		public int angleZ;

		public Piece(int angleX, int angleY, int angleZ) {
			this.angleX = angleX;
			this.angleY = angleY;
			this.angleZ = angleZ;
		}

		public void updateAngles() {
			float ang = 360;
			if (angleX < 0)
				angleX += ang;
			if (angleX >= ang)
				angleX -= ang;
			if (angleY < 0)
				angleY += ang;
			if (angleY >= ang)
				angleY -= ang;
			if (angleZ < 0)
				angleZ += ang;
			if (angleZ >= ang)
				angleZ -= ang;
		}

		@Override
		public String toString() {
			return "Piece: X: " + Integer.toString(angleX) + " Y: " + Integer.toString(angleY) + " Z: " + Integer.toString(angleZ);
		}
	}

	public final float SPEED = 11F;
	public final int TOTAL_ANGLE = 90;

	private int progress;
	private Vector3[][][] cube;
	private Piece[][][] pieces;
	private Piece[] face;
	private boolean resync;
	private int move;
	private boolean clockwise;
	private int tempAngle;

	public RubikTE() {
		move = -1;
		pieces = new Piece[RubikModel.PIECES_PER_SIDE][RubikModel.PIECES_PER_SIDE][RubikModel.PIECES_PER_SIDE];
		cube = new Vector3[RubikModel.PIECES_PER_SIDE][RubikModel.PIECES_PER_SIDE][RubikModel.PIECES_PER_SIDE];
		face = new Piece[RubikModel.PIECES_PER_FACE];
		createPieces();
	}

	private void createPieces() {
		System.out.println(FMLCommonHandler.instance().getEffectiveSide().toString());
		for (int x = 0; x < RubikModel.PIECES_PER_SIDE; x++) {
			for (int y = 0; y < RubikModel.PIECES_PER_SIDE; y++) {
				for (int z = 0; z < RubikModel.PIECES_PER_SIDE; z++) {
					pieces[x][y][z] = new Piece(0, 0, 0);
					cube[x][y][z] = new Vector3(x, y, z);
				}
			}
		}
	}

	@Override
	public void updateEntity() {
		if (move > -1 && updateMoveProgress()) {
			if (!worldObj.isRemote) {
				performMove();
				move = -1;
				clockwise = false;
				tempAngle = 0;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}

	private boolean updateMoveProgress() {
		// TODO Support for all sizes up to 16x16 (metadata = size)
		System.out.println(worldObj.isRemote ? "Client" : "Server");
		int axis = move / 3;
		int angle = (int) (TOTAL_ANGLE / SPEED);
		int ang = (tempAngle + angle) >= TOTAL_ANGLE ? TOTAL_ANGLE - tempAngle : angle;
		tempAngle += angle;
		if (clockwise)
			ang *= -1;
		System.out.println("TEMPANGLE: " + tempAngle + " ANGLE: " + ang + " FACE NUMBER " + face.length);
		for (Piece piece : face) {
			if (piece == null) {
				System.out.println("FOUND NULL PIECE D:");
				continue;
			}
			if (axis == 0)
				piece.angleY += ang;
			else if (axis == 1)
				piece.angleZ += ang;
			else if (axis == 2)
				piece.angleX += ang;

			piece.updateAngles();
		}

		return tempAngle >= TOTAL_ANGLE;
	}

	private void getFace() {
		if (move < 0)
			return;
		int pps = RubikModel.PIECES_PER_SIDE;
		int i = 0;
		int slice = move % 3;
		int axis = move / 3;
		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					if (axis == 0 && y != slice)
						continue;
					if (axis == 1 && z != slice)
						continue;
					if (axis == 2 && x != slice)
						continue;
					Vector3 piece = cube[x][y][z];
					face[i] = pieces[(int) piece.getX()][(int) piece.getY()][(int) piece.getZ()];
					System.out.println("adding piece to the face");
					if (face[i] == null) {
						System.out.println("FOUND NULL PIECE WHILE CREATING THE FACE D:");
						continue;
					}
					i++;
				}
			}
		}
	}

	private void performMove() {
		//TODO DEBUGGE DEBUGGE DEBBUDEBBUGIBUGGE
		if (move < 0)
			return;
		int pps = RubikModel.PIECES_PER_SIDE;
		int i = 0;
		int slice = move % 3;
		int axis = move / 3;
		Vector3[] tempFace = new Vector3[RubikModel.PIECES_PER_FACE];
		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					if (axis == 0 && y != slice)
						continue;
					if (axis == 1 && z != slice)
						continue;
					if (axis == 2 && x != slice)
						continue;
					tempFace[i] = cube[x][y][z];
					i++;
				}
			}
		}

		i = 0;
		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					if (axis == 0 && y != slice)
						continue;
					if (axis == 1 && z != slice)
						continue;
					if (axis == 2 && x != slice)
						continue;
					int j = pps * (i % pps) + Math.abs((i / pps) - (pps - 1));
					System.out.println(i + " ---> " + j);
					cube[x][y][z] = tempFace[j];
					i++;
				}
			}
		}

	}

	@Override
	public Packet getDescriptionPacket() {
		// TODO if move != 0 send face coords else send angles (need to send the
		// whole cube?)
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, tag);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
		tempAngle = 0;
		getFace();
		System.out.println("Packet Received Move: " + move + (clockwise? " clockwise" : " anticlockwise"));
	}

	@Override
	public void readFromNBT(NBTTagCompound comp) {
		super.readFromNBT(comp);
		int pps = RubikModel.PIECES_PER_SIDE;
		//TODO read and save pps from nbt
		pieces = new Piece[pps][pps][pps];
		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					NBTTagCompound piece = comp.getCompoundTag(Integer.toString(x) + " " + Integer.toString(y) + " " + Integer.toString(z));
					pieces[x][y][z] = new Piece(piece.getInteger("angleX"), piece.getInteger("angleY"), piece.getInteger("angleZ"));
				}
			}
		}
		move = comp.getByte("move");
		clockwise = comp.getBoolean("clockwise");

		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					System.out.println(FMLCommonHandler.instance().getEffectiveSide().toString() + " " + pieces[x][y][z].toString());
				}
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound comp) {
		super.writeToNBT(comp);
		int pps = RubikModel.PIECES_PER_SIDE;
		comp.setByte("move", (byte) move);
		comp.setBoolean("clockwise", clockwise);
		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					NBTTagCompound piece = new NBTTagCompound();
					piece.setInteger("angleX", pieces[x][y][z].angleX);
					piece.setInteger("angleY", pieces[x][y][z].angleY);
					piece.setInteger("angleZ", pieces[x][y][z].angleZ);
					comp.setCompoundTag(Integer.toString(x) + " " + Integer.toString(y) + " " + Integer.toString(z), piece);
				}
			}
		}
	}

	public void setMove(int i, boolean clock) {
		// TODO
		if (move > -1)
			return;
		System.out.println("__________________________________________________________________________");
		Random ran = new Random();
		this.move = ran.nextInt(9);
		clockwise = ran.nextBoolean();
		System.out.println("MOVE: " + move + (clockwise? " CLOCKWISE" : " ANTICLOCKWISE"));
		/*
		 * this.move = i; this.clockwise = clock;
		 */
		getFace();
	}

	public int getAngleX(int i) {
		return pieces[i % RubikModel.PIECES_PER_SIDE][i / RubikModel.PIECES_PER_FACE][(i % RubikModel.PIECES_PER_FACE) / RubikModel.PIECES_PER_SIDE].angleX;
	}

	public int getAngleY(int i) {
		return pieces[i % RubikModel.PIECES_PER_SIDE][i / RubikModel.PIECES_PER_FACE][(i % RubikModel.PIECES_PER_FACE) / RubikModel.PIECES_PER_SIDE].angleY;
	}

	public int getAngleZ(int i) {
		return pieces[i % RubikModel.PIECES_PER_SIDE][i / RubikModel.PIECES_PER_FACE][(i % RubikModel.PIECES_PER_FACE) / RubikModel.PIECES_PER_SIDE].angleZ;
	}

}
