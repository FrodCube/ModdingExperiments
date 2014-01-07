package moddingExperiments.tileEntities;

import java.util.Random;

import moddingExperiments.achievements.Achievements;
import moddingExperiments.client.models.RubikModel;
import moddingExperiments.client.particles.Particles;
import moddingExperiments.client.sounds.Sounds;
import moddingExperiments.config.ConfigurationHandler;
import moddingExperiments.util.Matrix3i;
import moddingExperiments.util.Vector3i;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
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
	public static final int SOLVE_DURATION = 200;
	public static final int SOLVE_SPEED = 4;

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
	private boolean solving;
	private int solveProgress;
	private int scrambleCounter;
	private String playerName;

	@SideOnly(Side.CLIENT)
	private RubikModel model;

	public RubikTileEntity() {
		move = NO_MOVE;
		prevMove = NO_MOVE;
		scrambleCounter = 0;
		solveProgress = 0;
		scrambling = false;
		scrambled = false;
		solving = false;
		piecesPerSide = 0;
		playerName = "";
	}

	public RubikTileEntity(int piecesPerSide) {
		this();
		this.piecesPerSide = piecesPerSide;
		this.newCube(this.piecesPerSide);
	}

	private void newCube(int piecesPerSide) {
		pieces = new Piece[piecesPerSide][piecesPerSide][piecesPerSide];
		cube = new Vector3i[piecesPerSide][piecesPerSide][piecesPerSide];
		face = new Vector3i[piecesPerSide][piecesPerSide];

		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			model = new RubikModel(piecesPerSide);
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
		if (solving) {
			if (solveProgress >= SOLVE_DURATION) {
				solving = false;
				solveProgress = 0;
				if (!worldObj.isRemote) {
					newCube(piecesPerSide);
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			} else {
				solveProgress += SOLVE_SPEED;
			}
		}

		if (move == NO_MOVE) {
			if (!worldObj.isRemote) {
				if (scrambling) {
					if (scrambleCounter == ConfigurationHandler.SCRAMBLE_LENGTH) {
						scrambling = false;
						prevMove = NO_MOVE;
						scrambleCounter = 0;
						worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
		if (face == null) {
			return false;
		}
		int axis = move / piecesPerSide;
		tempAngle += ANGLE;
		int ang = (tempAngle + ANGLE) >= TOTAL_ANGLE ? TOTAL_ANGLE : tempAngle;
		if (!clockwise)
			ang *= -1;
		for (int a = 0; a < piecesPerSide; a++) {
			for (int b = 0; b < piecesPerSide; b++) {
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
		int i = 0;
		int slice = move % piecesPerSide;
		int axis = move / piecesPerSide;
		for (int x = 0; x < piecesPerSide; x++) {
			for (int y = 0; y < piecesPerSide; y++) {
				for (int z = 0; z < piecesPerSide; z++) {
					if (axis == Y_AXIS && y != slice)
						continue;
					if (axis == Z_AXIS && z != slice)
						continue;
					if (axis == X_AXIS && x != slice)
						continue;
					Vector3i piece = cube[x][y][z];
					face[i % piecesPerSide][i / piecesPerSide] = cube[x][y][z];
					
					if (face[i % piecesPerSide][i / piecesPerSide] == null) {
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

		int f = (int) Math.floor(piecesPerSide / 2.0);
		int c = (int) Math.ceil(piecesPerSide / 2.0);
		int i = 0;
		int slice = move % piecesPerSide;
		int axis = move / piecesPerSide;

		if ((clockwise && axis == Y_AXIS) || (!clockwise && (axis == X_AXIS || axis == Z_AXIS))) {
			for (int x = 0; x < f; x++) {
				for (int y = 0; y < c; y++) {
					Vector3i temp = face[x][y];
					face[x][y] = face[y][piecesPerSide - 1 - x];
					face[y][piecesPerSide - 1 - x] = face[piecesPerSide - 1 - x][piecesPerSide - 1 - y];
					face[piecesPerSide - 1 - x][piecesPerSide - 1 - y] = face[piecesPerSide - 1 - y][x];
					face[piecesPerSide - 1 - y][x] = temp;
				}
			}
		} else {
			for (int x = 0; x < f; x++) {
				for (int y = 0; y < c; y++) {
					Vector3i temp = face[piecesPerSide - 1 - y][x];
					face[piecesPerSide - 1 - y][x] = face[piecesPerSide - 1 - x][piecesPerSide - 1 - y];
					face[piecesPerSide - 1 - x][piecesPerSide - 1 - y] = face[y][piecesPerSide - 1 - x];
					face[y][piecesPerSide - 1 - x] = face[x][y];
					face[x][y] = temp;
				}
			}
		}

		for (int x = 0; x < piecesPerSide; x++) {
			for (int y = 0; y < piecesPerSide; y++) {
				for (int z = 0; z < piecesPerSide; z++) {
					if (axis == Y_AXIS && y != slice)
						continue;
					if (axis == Z_AXIS && z != slice)
						continue;
					if (axis == X_AXIS && x != slice)
						continue;
					cube[x][y][z] = face[i % piecesPerSide][i / piecesPerSide];
					i++;
				}
			}
		}

		for (int x = 0; x < piecesPerSide; x++) {
			for (int y = 0; y < piecesPerSide; y++) {
				for (int z = 0; z < piecesPerSide; z++) {
					pieces[x][y][z].performMove();
				}
			}
		}

		if (!scrambling && scrambled) {
			if (!playerName.equalsIgnoreCase("") && isSolved()) {
				spawnParticles();

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
		// TODO fix solved check to include not oriented centers
		// TODO particles?
		for (int x = 0; x < piecesPerSide; x++) {
			for (int y = 0; y < piecesPerSide; y++) {
				for (int z = 0; z < piecesPerSide; z++) {
					if (!pieces[x][y][z].rotation.equals(pieces[0][0][0].rotation)) {
						Minecraft.getMinecraft().thePlayer.addChatMessage("Not solved. Fail at " + new Vector3i(x, y, z));
						return false;
					}
				}
			}
		}

		// TODO fix weird achievement rendering
		Minecraft.getMinecraft().thePlayer.addChatMessage("cube solved " + piecesPerSide);

		return true;
	}

	private void spawnParticles() {
		Random rand = new Random();
		for (int i = 0; i < 180; i += 20) {
			for (int j = 0; j < 360; j += 15) {
				double v = 0.08 + 0.02 * rand.nextDouble();
				double ci = Math.cos(Math.toRadians(i));
				double si = Math.sin(Math.toRadians(i));
				double cj = Math.cos(Math.toRadians(j));
				double sj = Math.sin(Math.toRadians(j));
				Particles.SOLVED.spawnParticle(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, v * sj * ci, v * cj, v * sj * si);
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
		tag.setByte("move", (byte) move);
		tag.setByte("prevMove", (byte) prevMove);
		tag.setBoolean("clockwise", clockwise);
		tag.setInteger("tempAngle", tempAngle);
		tag.setByte("piecesPerSide", (byte) piecesPerSide);
		tag.setBoolean("solving", solving);
		tag.setByte("solveProgress", (byte) solveProgress);
		tag.setBoolean("scrambling", scrambling);
		tag.setBoolean("scrambled", scrambled);
		tag.setByte("scrambleCounter", (byte) scrambleCounter);		
		tag.setString("playerName", playerName);

		for (int x = 0; x < piecesPerSide; x++) {
			for (int y = 0; y < piecesPerSide; y++) {
				for (int z = 0; z < piecesPerSide; z++) {
					Piece piece = pieces[x][y][z];
					Vector3i vCube = cube[x][y][z];
					NBTTagCompound pieceTag = new NBTTagCompound();

					// TODO better networking, I'm sending too much stuff
					pieceTag.setByteArray("rotation", piece.rotation.toArray());
					pieceTag.setByteArray("tempRotation", piece.tempRotation.toArray());
					pieceTag.setByteArray("cube", vCube.toArray());
					if (z == 0 && face[x][y] != null) {
						pieceTag.setByteArray("face", face[x][y].toArray());
					}

					tag.setCompoundTag(x + " " + y + " " + z, pieceTag);

				}
			}
		}
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
		getFace();
		Minecraft.getMinecraft().thePlayer.addChatMessage("Packet Received Move: " + move + (clockwise ? " clockwise" : " anticlockwise"));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		piecesPerSide = tag.getByte("piecesPerSide");
		cube = new Vector3i[piecesPerSide][piecesPerSide][piecesPerSide];
		pieces = new Piece[piecesPerSide][piecesPerSide][piecesPerSide];
		face = new Vector3i[piecesPerSide][piecesPerSide];
		for (int x = 0; x < piecesPerSide; x++) {
			for (int y = 0; y < piecesPerSide; y++) {
				for (int z = 0; z < piecesPerSide; z++) {
					NBTTagCompound pieceTag = tag.getCompoundTag(x + " " + y + " " + z);
					pieces[x][y][z] = new Piece();
					pieces[x][y][z].rotation = Matrix3i.fromArray(pieceTag.getByteArray("rotation"));
					pieces[x][y][z].tempRotation = Vector3i.fromArray(pieceTag.getByteArray("tempRotation"));
					cube[x][y][z] = Vector3i.fromArray(pieceTag.getByteArray("cube"));
					if (z == 0) {
						face[x][y] = Vector3i.fromArray(pieceTag.getByteArray("face"));
					}
				}
			}
		}
		
		move = tag.getByte("move");
		prevMove = tag.getByte("prevMove");
		clockwise = tag.getBoolean("clockwise");
		tempAngle = tag.getInteger("tempAngle");
		solving = tag.getBoolean("solving");
		solveProgress = tag.getByte("solveProgress");
		scrambling = tag.getBoolean("scrambling");
		scrambled = tag.getBoolean("scrambled");
		scrambleCounter = tag.getByte("scrambleCounter");
		playerName = tag.getString("playerName");
	}

	public boolean setMove(int i, boolean clock, String player) {
		if (move != NO_MOVE || solving || (scrambling && !player.equalsIgnoreCase("")))
			return false;
		this.move = i;
		this.clockwise = clock;
		this.playerName = player;
		getFace();
		Sounds.CUBE_0.playMultiSound(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 1, 1);
		return true;
	}

	public void scramble() {
		if (move == NO_MOVE && !scrambling && !solving) {
			System.out.println("started scrambling");
			scrambling = true;
			scrambled = true;
		}
	}

	public void solve() {
		if (move == NO_MOVE && !solving && !scrambling && !isSolved()) {
			scrambled = false;
			solving = true;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
		spawnParticles();
		Minecraft.getMinecraft().guiAchievement.queueTakenAchievement(Achievements.pocketCube);
		for (int x = 0; x < piecesPerSide; x++) {
			for (int y = 0; y < piecesPerSide; y++) {
				for (int z = 0; z < piecesPerSide; z++) {
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

	public float getSolveProgress() {
		return 1 - ((float) solveProgress / (float) SOLVE_DURATION);
	}

	public boolean isMoving() {
		return move != NO_MOVE;
	}

	public float getTempAngleProgress() {
		return (float) Math.sin(Math.toRadians(2 * tempAngle));
	}

	public float getScrambleProgress() {
		return (float) scrambleCounter / (float) ConfigurationHandler.SCRAMBLE_LENGTH;
	}

	public boolean isScrambling() {
		return scrambling;
	}

}
