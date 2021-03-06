package moddingExperiments.util;

import org.lwjgl.util.vector.Vector3f;

public class Matrix3i {
	public int aa, ab, ac, ba, bb, bc, ca, cb, cc;

	public Matrix3i() {
		this(0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	public Matrix3i(int aa, int ab, int ac, int ba, int bb, int bc, int ca, int cb, int cc) {
		this.aa = aa;
		this.ab = ab;
		this.ac = ac;
		this.ba = ba;
		this.bb = bb;
		this.bc = bc;
		this.ca = ca;
		this.cb = cb;
		this.cc = cc;
	}

	public Matrix3i setIdentity() {
		ab = ac = ba = bc = ca = cb = 0;
		aa = bb = cc = 1;

		return this;
	}

	public Matrix3i transpose() {
		Matrix3i result = new Matrix3i(aa, ab, ac, ba, bb, bc, ca, cb, cc);
		int temp;

		temp = result.ab;
		result.ab = result.ba;
		result.ba = temp;

		temp = result.ac;
		result.ac = result.ca;
		result.ca = temp;

		temp = result.bc;
		result.bc = result.cb;
		result.cb = temp;

		return result;
	}

	public Matrix3i mult(Matrix3i m) {
		Matrix3i result = new Matrix3i();

		result.aa = aa * m.aa + ab * m.ba + ac * m.ca;
		result.ab = aa * m.ab + ab * m.bb + ac * m.cb;
		result.ac = aa * m.ac + ab * m.bc + ac * m.cc;

		result.ba = ba * m.aa + bb * m.ba + bc * m.ca;
		result.bb = ba * m.ab + bb * m.bb + bc * m.cb;
		result.bc = ba * m.ac + bb * m.bc + bc * m.cc;

		result.ca = ca * m.aa + cb * m.ba + cc * m.ca;
		result.cb = ca * m.ab + cb * m.bb + cc * m.cb;
		result.cc = ca * m.ac + cb * m.bc + cc * m.cc;
		return result;
	}

	public Vector3f mult(Vector3f v) {
		Vector3f result = new Vector3f();

		result.x = Vector3f.dot(v, new Vector3f(aa, ab, ac));
		result.y = Vector3f.dot(v, new Vector3f(ba, bb, bc));
		result.z = Vector3f.dot(v, new Vector3f(ca, cb, cc));

		return result;
	}

	public Matrix3i rotate(Vector3i angles) {
		Matrix3i rotation = new Matrix3i();

		if (angles.getX() != 0) {
			rotation.aa = 1;
			if (angles.getX() > 0) {
				rotation.bc = -1;
				rotation.cb = 1;
			} else {
				rotation.bc = 1;
				rotation.cb = -1;
			}
		} else if (angles.getY() != 0) {
			rotation.bb = 1;
			if (angles.getY() > 0) {
				rotation.ac = 1;
				rotation.ca = -1;
			} else {
				rotation.ac = -1;
				rotation.ca = 1;
			}
		} else if (angles.getZ() != 0) {
			rotation.cc = 1;
			if (angles.getZ() > 0) {
				rotation.ab = -1;
				rotation.ba = 1;
			} else {
				rotation.ab = 1;
				rotation.ba = -1;
			}
		} else {
			rotation.setIdentity();
		}

		return rotation.mult(this);
	}

	public float getAngle() {
		return (float) Math.acos(0.5 * (aa + bb + cc - 1));
	}

	public Vector3f getAxis() {
		float d = (float) (2 * Math.sin(getAngle()));
		float f = 0.70710678118F; // = sqrt(2) / 2
		if ((int) d == 0) {
			if (aa == 1 && bb == 1 && cc == 1) {
				return new Vector3f(1, 0, 0);
			} else if (aa == 1 && bb == -1 && cc == -1) {
				return new Vector3f(1, 0, 0);
			} else if (aa == -1 && bb == 1 && cc == -1) {
				return new Vector3f(0, 1, 0);
			} else if (aa == -1 && bb == -1 && cc == 1) {
				return new Vector3f(0, 0, 1);
			} else if (aa == -1 && bc == -1 && cb == -1) {
				return new Vector3f(0, f, -f);
			} else if (aa == -1 && bc == 1 && cb == 1) {
				return new Vector3f(0, f, f);
			} else if (ac == -1 && bb == -1 && ca == -1) {
				return new Vector3f(f, 0, -f);
			} else if (ac == 1 && bb == -1 && ca == 1) {
				return new Vector3f(f, 0, f);
			} else if (ab == -1 && ba == -1 && cc == -1) {
				return new Vector3f(-f, f, 0);
			} else if (ab == 1 && ba == 1 && cc == -1) {
				return new Vector3f(f, f, 0);
			}
		}

		float x = (cb - bc) / d;
		float y = (ac - ca) / d;
		float z = (ba - ab) / d;

		return new Vector3f(x, y, z);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Matrix3i) {
			Matrix3i m = (Matrix3i) o;
			return aa == m.aa && ab == m.ab && ac == m.ac && ba == m.ba && bb == m.bb && bc == m.bc && ca == m.ca && cb == m.cb && cc == m.cc;
		}
		return false;
	}

	public byte[] toArray() {
		return new byte[] { (byte) aa, (byte) ab, (byte) ac, (byte) ba, (byte) bb, (byte) bc, (byte) ca, (byte) cb, (byte) cc };
	}

	public static Matrix3i fromArray(byte[] m) {
		if (m.length != 9) {
			System.out.println("lenght is not 9! it is " + m.length);
			Matrix3i matrix = new Matrix3i();
			matrix.setIdentity();
			return matrix;
		}
		return new Matrix3i(m[0], m[1], m[2], m[3], m[4], m[5], m[6], m[7], m[8]);
	}

	@Override
	public String toString() {
		return "((" + aa + ", " + ab + ", " + ac + "), (" + ba + ", " + bb + ", " + bc + "), (" + ca + ", " + cb + ", " + cc + ")) Angle: " + Math.toDegrees(getAngle()) + " Axis: " + getAxis().toString();
	}

}
