//package moddingExperiments.tileEntities;
//
//import org.lwjgl.opengl.GL11;
//import org.lwjgl.util.vector.Matrix3f;
//import org.lwjgl.util.vector.Matrix4f;
//import org.lwjgl.util.vector.Vector3f;
//
//import moddingExperiments.client.models.RubikModel;
//import moddingExperiments.util.Vector3i;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.network.INetworkManager;
//import net.minecraft.network.packet.Packet;
//import net.minecraft.network.packet.Packet132TileEntityData;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.Vec3;
//import cpw.mods.fml.common.FMLCommonHandler;
//
//public class RubikTE extends TileEntity {
//
//	public class Piece {
//		//		public int angleX;
//		//		public int angleY;
//		//		public int angleZ;
//
//		public Vector3i rotation1;
//		public Vector3i rotation2;
//		public Vector3i tempRotation;
//
//		public Piece(Vector3i rotation1, Vector3i rotation2) {
//			//			this.angleX = angleX;
//			//			this.angleY = angleY;
//			//			this.angleZ = angleZ;
//			this.rotation1 = rotation1;
//			this.rotation2 = rotation2;
//			this.tempRotation = new Vector3i(0, 0, 0);
//		}
//
//		//		public Vector3i getRotation() {
//		//			Matrix4f rot = new Matrix4f();
//		//			Matrix4f.setIdentity(rot);
//		//			rot = rot.rotate((float) Math.toRadians(rotation.getX()), new Vector3f(1, 0, 0));
//		//			rot = rot.rotate((float) Math.toRadians(rotation.getY()), new Vector3f(0, 1, 0));
//		//			rot = rot.rotate((float) Math.toRadians(rotation.getZ()), new Vector3f(0, 0, 1));
//		//			
//		//			rot = rot.rotate((float) Math.toRadians(tempRotation.getX()), new Vector3f(1, 0, 0));
//		//			rot = rot.rotate((float) Math.toRadians(tempRotation.getY()), new Vector3f(0, 1, 0));
//		//			rot = rot.rotate((float) Math.toRadians(tempRotation.getZ()), new Vector3f(0, 0, 1));
//		//			return new Vector3i(xAngle, yAngle, zAngle);
//		//		}
//		//
//		//		public void performMove() {
//		//			this.rotation = getRotation();
//		//			this.tempRotation = new Vector3i(0, 0, 0);
//		//		}
//		//
//		//		public void updateAngles() {
//		//			float ang = 360;
//		//			if (angleX < 0)
//		//				angleX += ang;
//		//			if (angleX >= ang)
//		//				angleX -= ang;
//		//			if (angleY < 0)
//		//				angleY += ang;
//		//			if (angleY >= ang)
//		//				angleY -= ang;
//		//			if (angleZ < 0)
//		//				angleZ += ang;
//		//			if (angleZ >= ang)
//		//				angleZ -= ang;
//		//		}
//		//
//		//		@Override
//		//		public String toString() {
//		//			return "Piece: X: " + Integer.toString(angleX) + " Y: " + Integer.toString(angleY) + " Z: " + Integer.toString(angleZ);
//		//		}
//	}
//
//	public final float SPEED = 11F;
//	public final int TOTAL_ANGLE = 90;
//	public final int ANGLE = (int) (TOTAL_ANGLE / SPEED);
//
//	private int progress;
//	private Vector3i[][][] cube;
//	private Piece[][][] pieces;
//	private Piece[] face;
//	private boolean resync;
//	private int move;
//	private boolean clockwise;
//	private int tempAngle;
//
//	public RubikTE() {
//		move = -1;
//		pieces = new Piece[RubikModel.PIECES_PER_SIDE][RubikModel.PIECES_PER_SIDE][RubikModel.PIECES_PER_SIDE];
//		cube = new Vector3i[RubikModel.PIECES_PER_SIDE][RubikModel.PIECES_PER_SIDE][RubikModel.PIECES_PER_SIDE];
//		face = new Piece[RubikModel.PIECES_PER_FACE];
//		createPieces();
//	}
//
//	private void createPieces() {
//		System.out.println(FMLCommonHandler.instance().getEffectiveSide().toString());
//		for (int x = 0; x < RubikModel.PIECES_PER_SIDE; x++) {
//			for (int y = 0; y < RubikModel.PIECES_PER_SIDE; y++) {
//				for (int z = 0; z < RubikModel.PIECES_PER_SIDE; z++) {
////					pieces[x][y][z] = new Piece(new Vector3i(0, 0, 0));
//					cube[x][y][z] = new Vector3i(x, y, z);
//				}
//			}
//		}
//	}
//
//	@Override
//	public void updateEntity() {
//		if (move > -1 && updateMoveProgress()) {
//			if (!worldObj.isRemote) {
//				performMove();
//				move = -1;
//				clockwise = false;
//				tempAngle = 0;
//				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
//			}
//		}
//	}
//
//	private boolean updateMoveProgress() {
//		// TODO Support for all sizes up to 16x16 (metadata = size)
//		// System.out.println(worldObj.isRemote ? "Client" : "Server");
//		int axis = move / 3;
//		int ang = (tempAngle + ANGLE) >= TOTAL_ANGLE ? TOTAL_ANGLE - tempAngle : ANGLE;
//		tempAngle += ANGLE;
//		if (clockwise)
//			ang *= -1;
//		// System.out.println("TEMPANGLE: " + tempAngle + " ANGLE: " + ang +
//		// " FACE NUMBER " + face.length);
//		for (Piece piece : face) {
//			if (piece == null) {
//				System.out.println("FOUND NULL PIECE WHILE MOVING D:");
//				continue;
//			}
//			//TODO CHANGE CHANGE CHANGE CHANGE (+ partial tick time?)
//
//			if (axis == 0)
//				piece.tempRotation = piece.tempRotation.add(new Vector3i(0, ang, 0));
//			else if (axis == 1)
//				piece.tempRotation = piece.tempRotation.add(new Vector3i(0, 0, ang));
//			else if (axis == 2)
//				piece.tempRotation = piece.tempRotation.add(new Vector3i(ang, 0, 0));
//
//			/*
//			if (axis == 0)
//				piece.angleY += ang;
//			else if (axis == 1)
//				piece.angleZ += ang;
//			else if (axis == 2)
//				piece.angleX += ang;
//			*/
//
//			//piece.updateAngles();
//			//System.out.println(piece.toString());
//		}
//
//		return tempAngle >= TOTAL_ANGLE;
//	}
//
//	private void getFace() {
//		if (move < 0)
//			return;
//		int pps = RubikModel.PIECES_PER_SIDE;
//		int i = 0;
//		int slice = move % 3;
//		int axis = move / 3;
//		for (int x = 0; x < pps; x++) {
//			for (int y = 0; y < pps; y++) {
//				for (int z = 0; z < pps; z++) {
//					if (axis == 0 && y != slice)
//						continue;
//					if (axis == 1 && z != slice)
//						continue;
//					if (axis == 2 && x != slice)
//						continue;
//					Vector3i piece = cube[x][y][z];
//					face[i] = pieces[piece.getX()][piece.getY()][piece.getZ()];
//
//					System.out.println((new Vector3i(x, y, z)).toString() + " = " + piece.toString());
//					if (face[i] == null) {
//						System.out.println("FOUND NULL PIECE WHILE CREATING THE FACE D:");
//						continue;
//					}
//
//					i++;
//				}
//			}
//		}
//	}
//
//	private void performMove() {
//		// TODO DEBUGGE DEBUGGE DEBBUDEBBUGIBUGGE
//		if (move < 0)
//			return;
//		int pps = RubikModel.PIECES_PER_SIDE;
//		int i = 0;
//		int slice = move % 3;
//		int axis = move / 3;
//		Vector3i[] tempFace = new Vector3i[RubikModel.PIECES_PER_FACE];
//		for (int x = 0; x < pps; x++) {
//			for (int y = 0; y < pps; y++) {
//				for (int z = 0; z < pps; z++) {
//					if (axis == 0 && y != slice)
//						continue;
//					if (axis == 1 && z != slice)
//						continue;
//					if (axis == 2 && x != slice)
//						continue;
//					int j = !clockwise ? pps * (i % pps) + Math.abs((i / pps) - (pps - 1)) : pps * (i / pps) + Math.abs((i % pps) - (pps - 1));
//					tempFace[j] = cube[x][y][z];
//
//					System.out.println(i + " ---> " + j);
//
//					i++;
//				}
//			}
//		}
//
//		i = 0;
//		for (int x = 0; x < pps; x++) {
//			for (int y = 0; y < pps; y++) {
//				for (int z = 0; z < pps; z++) {
//					if (axis == 0 && y != slice)
//						continue;
//					if (axis == 1 && z != slice)
//						continue;
//					if (axis == 2 && x != slice)
//						continue;
//					cube[x][y][z] = tempFace[i];
//					i++;
//				}
//			}
//		}
//
//		for (int x = 0; x < pps; x++) {
//			for (int y = 0; y < pps; y++) {
//				for (int z = 0; z < pps; z++) {
////					pieces[x][y][z].performMove();
//				}
//			}
//		}
//
//	}
//
//	@Override
//	public Packet getDescriptionPacket() {
//		// TODO if move != 0 send face coords else send angles (need to send the
//		// whole cube?)
//		NBTTagCompound tag = new NBTTagCompound();
//		writeToNBT(tag);
//		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, tag);
//	}
//
//	@Override
//	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
//		//performMove(); shouldn't need this in client, right?
//		readFromNBT(pkt.data);
//		getFace();
//		System.out.println("Packet Received Move: " + move + (clockwise ? " clockwise" : " anticlockwise"));
//	}
//
//	@Override
//	public void readFromNBT(NBTTagCompound comp) {
//		//TODO better networking, I'm sending too much stuff
//		super.readFromNBT(comp);
//		int pps = RubikModel.PIECES_PER_SIDE;
//		// TODO read and save pps from nbt
//		pieces = new Piece[pps][pps][pps];
//		for (int x = 0; x < pps; x++) {
//			for (int y = 0; y < pps; y++) {
//				for (int z = 0; z < pps; z++) {
//					NBTTagCompound tag = comp.getCompoundTag(Integer.toString(x) + " " + Integer.toString(y) + " " + Integer.toString(z));
//					//					pieces[x][y][z] = new Piece(piece.getInteger("angleX"), piece.getInteger("angleY"), piece.getInteger("angleZ"));
////					pieces[x][y][z] = new Piece(Vector3i.unpack(tag.getInteger("rotation")));
//					pieces[x][y][z].tempRotation = Vector3i.unpack(tag.getInteger("tempRotation"));
//					cube[x][y][z] = Vector3i.unpack(tag.getInteger("cube"));
//				}
//			}
//		}
//		move = comp.getByte("move");
//		clockwise = comp.getBoolean("clockwise");
//		tempAngle = comp.getInteger("tempAngle");
//
//		/*
//		 * for (int x = 0; x < pps; x++) { for (int y = 0; y < pps; y++) { for
//		 * (int z = 0; z < pps; z++) {
//		 * System.out.println(FMLCommonHandler.instance
//		 * ().getEffectiveSide().toString() + " " + pieces[x][y][z].toString());
//		 * } } }
//		 */
//	}
//
//	@Override
//	public void writeToNBT(NBTTagCompound comp) {
//		super.writeToNBT(comp);
//		int pps = RubikModel.PIECES_PER_SIDE;
//		comp.setByte("move", (byte) move);
//		comp.setBoolean("clockwise", clockwise);
//		comp.setInteger("tempAngle", tempAngle);
//		for (int x = 0; x < pps; x++) {
//			for (int y = 0; y < pps; y++) {
//				for (int z = 0; z < pps; z++) {
//					Piece piece = pieces[x][y][z];
//					Vector3i vCube = cube[x][y][z];
//					NBTTagCompound tag = new NBTTagCompound();
//
////					tag.setInteger("rotation", piece.rotation.pack());
////					System.out.println(piece.rotation.toString() + " " + tag.getInteger("rotation"));
//					//					tag.setInteger("rotation2", piece.rotation2.pack());
//					tag.setInteger("tempRotation", piece.tempRotation.pack());
//					tag.setInteger("cube", vCube.pack());
//					//					piece.setInteger("angleX", pieces[x][y][z].angleX);
//					//					piece.setInteger("angleY", pieces[x][y][z].angleY);
//					//					piece.setInteger("angleZ", pieces[x][y][z].angleZ);
//					comp.setCompoundTag(Integer.toString(x) + " " + Integer.toString(y) + " " + Integer.toString(z), tag);
//				}
//			}
//		}
//	}
//
//	public boolean setMove(int i, boolean clock) {
//		// TODO
//		if (move > -1)
//			return false;
//		System.out.println("__________________________________________________________________________");
//		//		Random ran = new Random();
//		//		this.move = ran.nextInt(9);
//		//		this.clockwise = random.nextBoolean();
//		this.move = i;
//		this.clockwise = clock;
//		getFace();
//		System.out.println("MOVE: " + move + (clockwise ? " CLOCKWISE" : " ANTICLOCKWISE"));
//		return true;
//	}
//
//	public Piece getPiece(int i) {
//		return pieces[i % RubikModel.PIECES_PER_SIDE][i / RubikModel.PIECES_PER_FACE][(i % RubikModel.PIECES_PER_FACE) / RubikModel.PIECES_PER_SIDE];
//	}
//
//	//	public int getAngleX(int i) {
//	//		return pieces[i % RubikModel.PIECES_PER_SIDE][i / RubikModel.PIECES_PER_FACE][(i % RubikModel.PIECES_PER_FACE) / RubikModel.PIECES_PER_SIDE].angleX;
//	//	}
//	//
//	//	public int getAngleY(int i) {
//	//		return pieces[i % RubikModel.PIECES_PER_SIDE][i / RubikModel.PIECES_PER_FACE][(i % RubikModel.PIECES_PER_FACE) / RubikModel.PIECES_PER_SIDE].angleY;
//	//	}
//	//
//	//	public int getAngleZ(int i) {
//	//		return pieces[i % RubikModel.PIECES_PER_SIDE][i / RubikModel.PIECES_PER_FACE][(i % RubikModel.PIECES_PER_FACE) / RubikModel.PIECES_PER_SIDE].angleZ;
//	//	}
//	//
//	//	public boolean isInFace(int i) {
//	//		Piece piece = pieces[i % RubikModel.PIECES_PER_SIDE][i / RubikModel.PIECES_PER_FACE][(i % RubikModel.PIECES_PER_FACE) / RubikModel.PIECES_PER_SIDE];
//	//		for (Piece p : face) {
//	//			if (p == piece) {
//	//				return true;
//	//			}
//	//		}
//	//		return false;
//	//	}
//
//}
