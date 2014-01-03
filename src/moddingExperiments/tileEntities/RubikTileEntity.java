package moddingExperiments.tileEntities;

import java.util.Random;

import moddingExperiments.achievements.Achievements;
import moddingExperiments.client.models.RubikModel;
import moddingExperiments.config.ConfigurationHandler;
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
			rotation = rotation.rotate(tempRotation);
			tempRotation = new Vector3i();
		}

		@Override
		public String toString() {
			return "ROTATION: " + rotation.toString() + " TEMP_ROTATION: " + tempRotation.toString();
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
	private int prevMove;
	private boolean clockwise;
	private int tempAngle;
	private boolean scrambled; // TODO save these to NBT
	private boolean scrambling;
	private int scrambleCounter;
	private String playerName;

	@SideOnly(Side.CLIENT)
	private RubikModel model;

	public RubikTileEntity(int pps) {
		move = NO_MOVE;
		prevMove = NO_MOVE;
		scrambleCounter = 0;
		scrambling = false;
		piecesPerSide = pps;
		newCube(pps);
	}

	private void newCube(int pps) {
		pieces = new Piece[piecesPerSide][piecesPerSide][piecesPerSide];
		cube = new Vector3i[piecesPerSide][piecesPerSide][piecesPerSide];
		face = new Vector3i[piecesPerSide][piecesPerSide];

		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			model = new RubikModel(pps);
		}

		for (int x = 0; x < piecesPerSide; x++) {
			for (int y = 0; y < piecesPerSide; y++) {
				for (int z = 0; z < piecesPerSide; z++) {
					pieces[x][y][z] = new Piece();
					cube[x][y][z] = new Vector3i(x, y, z);
				}
			}
		}
	}

	@Override
	public void updateEntity() {
		if (move == NO_MOVE) {
			if (!worldObj.isRemote && scrambling) {
				if (scrambleCounter == ConfigurationHandler.SCRAMBLE_LENGTH) {
					scrambling = false;
					prevMove = NO_MOVE;
					scrambleCounter = 0;
				} else {
					Random random = new Random();
					scrambleCounter++;
					int randomMove;
					do {
						randomMove = random.nextInt(3 * piecesPerSide);
					} while (randomMove == prevMove);

					prevMove = randomMove;
					setMove(randomMove, random.nextBoolean(), "");
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			}
		} else {
			if (updateMoveProgress()) {
				if (!worldObj.isRemote) {
					performMove();
					move = NO_MOVE;
					clockwise = false;
					tempAngle = 0;
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
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
		// System.out.println("** GETTING FACE **");
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

					// System.out.println((worldObj.isRemote ? "Client" :
					// "Server") + " Face: " + (new Vector3i(x, y,
					// z)).toString() + " = " + piece.toString());
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
		// System.out.println("** PERFORMING MOVE **");
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

		if ((clockwise && axis == Y_AXIS) || (!clockwise && (axis == X_AXIS || axis == Z_AXIS))) {
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
					// System.out.println("Piece in " + (new Vector3i(x, y,
					// z).toString()) + " is now " + face[i % pps][i /
					// pps].toString());
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

		if (!scrambling && scrambled) {
			if (!playerName.equalsIgnoreCase("") && isSolved()) {
				switch (piecesPerSide) {
					case 2:
						FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(playerName).addStat(Achievements.pocketCube, 1);
						break;
					case 3:
						FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(playerName).addStat(Achievements.rubikCube, 1);
						break;
					case 4:
						FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(playerName).addStat(Achievements.revengeCube, 1);
						break;
					case 5:
						FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(playerName).addStat(Achievements.professorCube, 1);
						break;

				}
				
				scrambled = false;
			}
		}

	}

	private boolean isSolved() {
		Matrix3i id = new Matrix3i().setIdentity();
		for (int x = 0; x < piecesPerSide; x++) {
			for (int y = 0; y < piecesPerSide; y++) {
				for (int z = 0; z < piecesPerSide; z++) {
					if (!pieces[x][y][z].rotation.equals(id)) {
						return false;
					}
				}
			}
		}
		
		//TODO fix weird achievement rendering
		Minecraft.getMinecraft().thePlayer.addChatMessage("cube solved " + piecesPerSide);

		return true;
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
		tag.setByte("piecesPerSide", (byte) piecesPerSide);
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

		int pps = piecesPerSide = tag.getByte("piecesPerSide");
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

	public boolean setMove(int i, boolean clock, String player) {
		if (move != NO_MOVE || (scrambling && !player.equalsIgnoreCase("")))
			return false;
		this.move = i;
		this.clockwise = clock;
		this.playerName = player;
		getFace();
		Minecraft.getMinecraft().thePlayer.addChatMessage("MOVE: " + move + (clockwise ? " clockwise" : " anticlockwise"));
		return true;
	}

	public void scramble() {
		if (!scrambling) {
			System.out.println("started scrambling");
			scrambling = true;
			scrambled = true;
		}
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
