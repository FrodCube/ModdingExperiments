package moddingExperiments.tileEntities;

import java.util.Random;

import moddingExperiments.client.models.RubikModel;
import moddingExperiments.util.Matrix3i;
import moddingExperiments.util.Vector3i;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLCommonHandler;

public class RubikTileEntity extends TileEntity {

	public class Piece {
		public Matrix3i rotation;
		public Vector3i tempRotation;

		public Piece() {
			rotation = new Matrix3i();
			tempRotation = new Vector3i();
			rotation.setIdentity();
		}

		public Piece(Matrix3i rotation, Vector3i tempRotation) {
			this.rotation = rotation;
			this.tempRotation = tempRotation;
		}

		public void performMove() {
			//debug?
			rotation = rotation.rotate(tempRotation);
			tempRotation = new Vector3i();
		}

		@Override
		public String toString() {
			return "Piece: ROTATION: " + rotation.toString() + " TEMP_ROTATION: " + tempRotation.toString();
		}
	}

	public static final int NO_MOVE = -1;
	public final float SPEED = 11F;
	public final int TOTAL_ANGLE = 90;
	public final int ANGLE = (int) (TOTAL_ANGLE / SPEED);

	private int progress;
	private Vector3i[][][] cube;
	private Piece[][][] pieces;
	private Piece[] face;
	private int move;
	private boolean clockwise;
	private int tempAngle;

	public RubikTileEntity() {
		move = -1;
		pieces = new Piece[RubikModel.PIECES_PER_SIDE][RubikModel.PIECES_PER_SIDE][RubikModel.PIECES_PER_SIDE];
		cube = new Vector3i[RubikModel.PIECES_PER_SIDE][RubikModel.PIECES_PER_SIDE][RubikModel.PIECES_PER_SIDE];
		face = new Piece[RubikModel.PIECES_PER_FACE];
		newCube();
	}

	private void newCube() {
		System.out.println(FMLCommonHandler.instance().getEffectiveSide().toString());
		for (int x = 0; x < RubikModel.PIECES_PER_SIDE; x++) {
			for (int y = 0; y < RubikModel.PIECES_PER_SIDE; y++) {
				for (int z = 0; z < RubikModel.PIECES_PER_SIDE; z++) {
					pieces[x][y][z] = new Piece();
					cube[x][y][z] = new Vector3i(x, y, z);
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
		// System.out.println(worldObj.isRemote ? "Client" : "Server");
		int axis = move / 3;
		int ang = (tempAngle + ANGLE) >= TOTAL_ANGLE ? TOTAL_ANGLE - tempAngle : ANGLE;
		tempAngle += ANGLE;
		if (clockwise)
			ang *= -1;
		System.out.println("TEMPANGLE: " + tempAngle + " ANGLE: " + ang + " FACE NUMBER " + face.length);
		for (Piece piece : face) {
			if (piece == null) {
				System.out.println("FOUND NULL PIECE WHILE MOVING D:");
				continue;
			}
			//TODO CHANGE CHANGE CHANGE CHANGE (+ partial tick time?)

			if (axis == 0)
				piece.tempRotation.setY(tempAngle);
			else if (axis == 1)
				piece.tempRotation.setZ(tempAngle);
			else if (axis == 2)
				piece.tempRotation.setX(tempAngle);
		}

		if (face[0] != null) {
			System.out.println(face[0].toString());
		}

		return tempAngle >= TOTAL_ANGLE;
	}

	private void getFace() {
		if (move == NO_MOVE)
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
					Vector3i piece = cube[x][y][z];
					face[i] = pieces[piece.getX()][piece.getY()][piece.getZ()];

					//System.out.println((new Vector3i(x, y, z)).toString() + " = " + piece.toString());
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
		// TODO DEBUGGE DEBUGGE DEBBUDEBBUGIBUGGE
		if (move == NO_MOVE)
			return;
		int pps = RubikModel.PIECES_PER_SIDE;
		int i = 0;
		int slice = move % 3;
		int axis = move / 3;
		Vector3i[] tempFace = new Vector3i[RubikModel.PIECES_PER_FACE];
		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					if (axis == 0 && y != slice)
						continue;
					if (axis == 1 && z != slice)
						continue;
					if (axis == 2 && x != slice)
						continue;
					int j = !clockwise ? pps * (i % pps) + Math.abs((i / pps) - (pps - 1)) : pps * (i / pps) + Math.abs((i % pps) - (pps - 1));
					tempFace[j] = cube[x][y][z];

					//System.out.println(i + " ---> " + j);

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
					cube[x][y][z] = tempFace[i];
					i++;
				}
			}
		}

		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					pieces[x][y][z].performMove();
				}
			}
		}

	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		int pps = RubikModel.PIECES_PER_SIDE;
		tag.setByte("move", (byte) move);
		tag.setBoolean("clockwise", clockwise);
		tag.setInteger("tempAngle", tempAngle);
		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					Piece piece = pieces[x][y][z];
					Vector3i vCube = cube[x][y][z];
					NBTTagCompound pieceTag = new NBTTagCompound();

					//TODO better networking, I'm sending too much stuff (also getting wrong stuff from the server :\)
					pieceTag.setByteArray("rotation", piece.rotation.toArray());
					pieceTag.setByteArray("tempRotation", piece.tempRotation.toArray());
					pieceTag.setByteArray("cube", vCube.toArray());

//					System.out.println(piece.rotation.toString() + " = " + Matrix3i.fromArray(pieceTag.getByteArray("rotation")).toString());
//					System.out.println("Wrote: rotation: " + pieceTag.getByteArray("rotation").length + " tempRotation: " + pieceTag.getByteArray("tempRotation").length + " cube: " + pieceTag.getByteArray("cube").length);

					tag.setCompoundTag(x + " " + y + " " + z, pieceTag);

				}
			}
		}
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
		getFace();
		System.out.println("Packet Received Move: " + move + (clockwise ? " clockwise" : " anticlockwise"));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		int pps = RubikModel.PIECES_PER_SIDE;
		// TODO read and save pps from nbt
		pieces = new Piece[pps][pps][pps];
		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					NBTTagCompound pieceTag = tag.getCompoundTag(x + " " + y + " " + z);
					pieces[x][y][z] = new Piece();
					pieces[x][y][z].rotation = Matrix3i.fromArray(pieceTag.getByteArray("rotation"));
					pieces[x][y][z].tempRotation = Vector3i.fromArray(pieceTag.getByteArray("tempRotation"));
					cube[x][y][z] = Vector3i.fromArray(pieceTag.getByteArray("cube"));

//					System.out.println("Read: rotation: " + pieceTag.getByteArray("rotation").length + " tempRotation: " + pieceTag.getByteArray("tempRotation").length + " cube: " + pieceTag.getByteArray("cube").length);
				}
			}
		}
		move = tag.getByte("move");
		clockwise = tag.getBoolean("clockwise");
		tempAngle = tag.getInteger("tempAngle");

		/*
		 * for (int x = 0; x < pps; x++) { for (int y = 0; y < pps; y++) { for
		 * (int z = 0; z < pps; z++) {
		 * System.out.println(FMLCommonHandler.instance
		 * ().getEffectiveSide().toString() + " " + pieces[x][y][z].toString());
		 * } } }
		 */
	}

	public boolean setMove(int i, boolean clock) {
		// TODO
		if (move != NO_MOVE)
			return false;
		System.out.println("__________________________________________________________________________");
		System.out.println(" |o/ " + pieces[0][0][0].toString() + " " + cube[0][0][0].toString());
		Random random = new Random();
		this.move = random.nextInt(9);
		this.clockwise = random.nextBoolean();
		getFace();
		System.out.println("MOVE: " + move + (clockwise ? " CLOCKWISE" : " ANTICLOCKWISE"));
		return true;
	}

	public Matrix3i getRotation(int i) {
		Matrix3i rotation = pieces[i % RubikModel.PIECES_PER_SIDE][i / RubikModel.PIECES_PER_FACE][(i % RubikModel.PIECES_PER_FACE) / RubikModel.PIECES_PER_SIDE].rotation;
		if (rotation == null) {
			rotation = new Matrix3i();
			rotation.setIdentity();
		}
		return rotation;
	}

	public Vector3i getTempRotation(int i) {
		Vector3i tempRotation = pieces[i % RubikModel.PIECES_PER_SIDE][i / RubikModel.PIECES_PER_FACE][(i % RubikModel.PIECES_PER_FACE) / RubikModel.PIECES_PER_SIDE].tempRotation;
		if (tempRotation == null) {
			tempRotation = new Vector3i();
		}
		return tempRotation;
	}
}
