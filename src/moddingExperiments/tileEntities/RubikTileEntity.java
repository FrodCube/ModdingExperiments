package moddingExperiments.tileEntities;

import java.util.Random;

import moddingExperiments.client.models.RubikModel;
import moddingExperiments.util.Matrix3i;
import moddingExperiments.util.Vector3i;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RubikTileEntity extends TileEntity {

	public class Piece {
		public Matrix3i rotation;
		public Vector3i tempRotation;
		public String name;

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
			String s = this.toString();
			rotation = rotation.rotate(tempRotation);
			tempRotation = new Vector3i();

			s += "----->" + rotation.toString();
			// System.out.println(s);
		}

		@Override
		public String toString() {
			return "Piece name: " + name + " ROTATION: " + rotation.toString() + " TEMP_ROTATION: " + tempRotation.toString();
		}
	}
	
	public static final int X_AXIS = 2;
	public static final int Y_AXIS = 0;
	public static final int Z_AXIS = 1;

	public static final int NO_MOVE = -1;
	public static final float SPEED = 11F;
	public static final int TOTAL_ANGLE = 90;
	public static final int ANGLE = (int) (TOTAL_ANGLE / SPEED);
	
	public int piecesPerSide;
	private Vector3i[][][] cube;
	private Piece[][][] pieces;
	private Vector3i[][] face;
	private int move;
	private boolean clockwise;
	private int tempAngle;
	
	@SideOnly(Side.CLIENT)
	private RubikModel model;
	
	public RubikTileEntity() {
		this(2);
		System.out.println("CREATING TILE ENTITY WITH NO ARG!");
	}

	public RubikTileEntity(int pps) {
		piecesPerSide = pps;
		move = NO_MOVE;
		pieces = new Piece[piecesPerSide][piecesPerSide][piecesPerSide];
		cube = new Vector3i[piecesPerSide][piecesPerSide][piecesPerSide];
		face = new Vector3i[piecesPerSide][piecesPerSide];
		newCube();
		
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			model = new RubikModel(pps);
		}
	}

	private void newCube() {
		System.out.println(FMLCommonHandler.instance().getEffectiveSide().toString());
		for (int x = 0; x < piecesPerSide; x++) {
			for (int y = 0; y < piecesPerSide; y++) {
				for (int z = 0; z < piecesPerSide; z++) {
					pieces[x][y][z] = new Piece();
					pieces[x][y][z].name = (new Vector3i(x, y, z)).toString();
					cube[x][y][z] = new Vector3i(x, y, z);
				}
			}
		}
	}

	@Override
	public void updateEntity() {
		if (move != NO_MOVE && updateMoveProgress()) {
			if (!worldObj.isRemote) {
				performMove();
				move = NO_MOVE;
				clockwise = false;
				tempAngle = 0;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}

	private boolean updateMoveProgress() {
		// TODO Support for all sizes up to 16x16 (metadata = size)
		// System.out.println(worldObj.isRemote ? "Client" : "Server");
		int pps = piecesPerSide;
		int axis = move / pps;
		tempAngle += ANGLE;
		int ang = (tempAngle + ANGLE) >= TOTAL_ANGLE ? TOTAL_ANGLE : tempAngle;
		if (!clockwise)
			ang *= -1;
		// System.out.println("TEMPANGLE: " + tempAngle + " ANGLE: " + ang +
		// " FACE NUMBER " + face.length);
		for (int a = 0; a < pps; a++) {
			for (int b = 0; b < pps; b++) {
				Vector3i coords = face[a][b];
				if (coords == null) {
					continue;
				}
				
				Piece piece = pieces[coords.getX()][coords.getY()][coords.getZ()];
				if (piece == null) {
					System.out.println("FOUND NULL PIECE WHILE MOVING D:");
					continue;
				}
				// TODO CHANGE CHANGE CHANGE CHANGE (+ partial tick time?)

				if (axis == Y_AXIS)
					piece.tempRotation.setY(ang);
				else if (axis == Z_AXIS)
					piece.tempRotation.setZ(ang);
				else if (axis == X_AXIS)
					piece.tempRotation.setX(ang);
			}
		}

		return tempAngle >= TOTAL_ANGLE;
	}

	private void getFace() {
		if (move == NO_MOVE)
			return;
		System.out.println("** GETTING FACE **");
		int pps = piecesPerSide;
		int i = 0;
		int slice = move % pps;
		int axis = move / pps;
		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					if (axis == Y_AXIS && y != slice)
						continue;
					if (axis == Z_AXIS && z != slice)
						continue;
					if (axis == X_AXIS && x != slice)
						continue;
					Vector3i piece = cube[x][y][z];
					face[i % pps][i / pps] = cube[x][y][z];
					// face[i] =
					// pieces[piece.getX()][piece.getY()][piece.getZ()];

					System.out.println((worldObj.isRemote ? "Client" : "Server") + " Face: " + (new Vector3i(x, y, z)).toString() + " = " + piece.toString());
					if (face[i % pps][i / pps] == null) {
						System.out.println("FOUND NULL PIECE WHILE CREATING THE FACE D:");
						continue;
					}

					i++;
				}
			}
		}
	}

	private void performMove() {
		if (move == NO_MOVE)
			return;
		System.out.println("** PERFORMING MOVE **");
		int pps = piecesPerSide;

		Vector3i[][][] previousCube = new Vector3i[piecesPerSide][piecesPerSide][piecesPerSide];
		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					previousCube[x][y][z] = new Vector3i(cube[x][y][z].getX(), cube[x][y][z].getY(), cube[x][y][z].getZ());
				}
			}
		}

		int f = (int) Math.floor(pps / 2.0);
		int c = (int) Math.ceil(pps / 2.0);
		int i = 0;
		int slice = move % pps;
		int axis = move / pps;

		if ((clockwise && axis == Y_AXIS) || (!clockwise && (axis == X_AXIS || axis == Z_AXIS))){
			for (int x = 0; x < f; x++) {
				for (int y = 0; y < c; y++) {
					Vector3i temp = face[x][y];
					face[x][y] = face[y][pps - 1 - x];
					face[y][pps - 1 - x] = face[pps - 1 - x][pps - 1 - y];
					face[pps - 1 - x][pps - 1 - y] = face[pps - 1 - y][x];
					face[pps - 1 - y][x] = temp;
				}
			}
		} else {
			for (int x = 0; x < f; x++) {
				for (int y = 0; y < c; y++) {
					Vector3i temp = face[pps - 1 - y][x];
					face[pps - 1 - y][x] = face[pps - 1 - x][pps - 1 - y];
					face[pps - 1 - x][pps - 1 - y] = face[y][pps - 1 - x];
					face[y][pps - 1 - x] = face[x][y];
					face[x][y] = temp;
				}
			}
		}

		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					if (axis == Y_AXIS && y != slice)
						continue;
					if (axis == Z_AXIS && z != slice)
						continue;
					if (axis == X_AXIS && x != slice)
						continue;
					System.out.println("Piece in " + (new Vector3i(x, y, z).toString()) + " is now " + face[i % pps][i / pps].toString());
					cube[x][y][z] = face[i % pps][i / pps];
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

		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					System.out.println(previousCube[x][y][z].toString() + (previousCube[x][y][z].equals(cube[x][y][z]) ? " didn't move" : " changed") + " and became " + cube[x][y][z].toString());
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
		int pps = piecesPerSide;
		tag.setByte("move", (byte) move);
		tag.setBoolean("clockwise", clockwise);
		tag.setInteger("tempAngle", tempAngle);
		tag.setByte("pps", (byte) piecesPerSide);
		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					Piece piece = pieces[x][y][z];
					Vector3i vCube = cube[x][y][z];
					NBTTagCompound pieceTag = new NBTTagCompound();

					// TODO better networking, I'm sending too much stuff
					pieceTag.setByteArray("rotation", piece.rotation.toArray());
					pieceTag.setByteArray("tempRotation", piece.tempRotation.toArray());
					pieceTag.setByteArray("cube", vCube.toArray());

					pieceTag.setString("name", piece.name);

					// System.out.println(piece.rotation.toString() + " = " +
					// Matrix3i.fromArray(pieceTag.getByteArray("rotation")).toString());
					// System.out.println("Wrote: rotation: " +
					// pieceTag.getByteArray("rotation").length +
					// " tempRotation: " +
					// pieceTag.getByteArray("tempRotation").length + " cube: "
					// + pieceTag.getByteArray("cube").length);

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

		int pps = piecesPerSide = tag.getByte("pps");
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

					pieces[x][y][z].name = pieceTag.getString("name");

					// System.out.println("Read: rotation: " +
					// pieceTag.getByteArray("rotation").length +
					// " tempRotation: " +
					// pieceTag.getByteArray("tempRotation").length + " cube: "
					// + pieceTag.getByteArray("cube").length);
				}
			}
		}
		move = tag.getByte("move");
		clockwise = tag.getBoolean("clockwise");
		tempAngle = tag.getInteger("tempAngle");
	}

	int tempMove = -1;

	public boolean setMove(int i, boolean clock) {
		if (move != NO_MOVE)
			return false;
		
		System.out.println("__________________________________________________________________________");
		System.out.println(" piece[0][0][0]: " + pieces[0][0][0].toString() + " Position: " + cube[0][0][0].toString());
		Random random = new Random();
		this.move = random.nextInt(3 * piecesPerSide);
		this.clockwise = random.nextBoolean();
//		move = 2;
		// move = (tempMove + 1) % 9;
		tempMove = move;
		//clockwise = true;
		getFace();
		System.out.println("MOVE: " + move + (clockwise ? " CLOCKWISE" : " ANTICLOCKWISE"));
		Minecraft.getMinecraft().thePlayer.addChatMessage("MOVE: " + move + (clockwise ? " CLOCKWISE" : " ANTICLOCKWISE"));
		return true;
	}

	public Matrix3i getRotation(int x, int y, int z) {
		Matrix3i rotation = pieces[x][y][z].rotation;
		if (rotation == null) {
			rotation = new Matrix3i();
			rotation.setIdentity();
		}
		return rotation;
	}

	public Vector3i getTempRotation(int x, int y, int z) {
		Vector3i tempRotation = pieces[x][y][z].tempRotation;
		if (tempRotation == null) {
			tempRotation = new Vector3i();
		}
		return tempRotation;
	}

	public void printCube() {
		System.out.println("******** PRINTING THE CUBE ********");
		int pps = piecesPerSide;
		for (int x = 0; x < pps; x++) {
			for (int y = 0; y < pps; y++) {
				for (int z = 0; z < pps; z++) {
					System.out.println((new Vector3i(x, y, z)).toString() + ": " + pieces[cube[x][y][z].getX()][cube[x][y][z].getY()][cube[x][y][z].getZ()].toString() + " POSITION: " + cube[x][y][z].toString());
				}
			}
		}
		System.out.println("******** END PRINTING ********");
	}
	
	@SideOnly(Side.CLIENT)
	public RubikModel getModel() {
		return model;
	}
}
