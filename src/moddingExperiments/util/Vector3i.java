package moddingExperiments.util;

import net.minecraftforge.common.ForgeDirection;

public class Vector3i {

	private int x, y, z;

	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3i() {
		this(0, 0, 0);
	}

	public Vector3i add(Vector3i v) {
		return new Vector3i(this.x + v.x, this.y + v.y, this.z + v.z);
	}

	public Vector3i add(int a) {
		return new Vector3i(this.x + a, this.y + a, this.z + a);
	}

	public Vector3i sub(Vector3i v) {
		return this.add(v.negate());
	}

	public Vector3i sub(int a) {
		return this.add(-a);
	}

	public Vector3i scalarMult(int a) {
		return new Vector3i(a * this.x, a * this.y, a * this.z);
	}

	public Vector3i negate() {
		return scalarMult(-1);
	}

	public double dot(Vector3i v) {
		return this.x * v.x + this.y * v.y + this.z * v.z;
	}

	public double length2() {
		return this.dot(this);
	}

	public double length() {
		return Math.sqrt(this.length2());
	}

	public Vector3i cross(Vector3i v) {
		return new Vector3i(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
	}

	public void setNewCoordinates(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3i rotateX() {
		return new Vector3i(0, this.z, -this.y);
	}

	public Vector3i rotateY() {
		return new Vector3i(this.z, 0, -this.x);
	}

	public Vector3i rotateZ() {
		return new Vector3i(this.y, this.x, 0);
	}

	@Override
	public String toString() {
		return "( " + x + ", " + y + ", " + z + " )";
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vector3i)) {
			return false;
		}
		Vector3i v = (Vector3i) obj;
		return this.x == v.getX() && this.y == v.getY() && this.z == v.getZ();
	}

	public byte[] toArray() {
		return new byte[] { (byte) x, (byte) y, (byte) z };
	}

	public static Vector3i fromArray(byte[] v) {
		if (v.length != 3) {
			return new Vector3i();
		}
		return new Vector3i(v[0], v[1], v[2]);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
}
