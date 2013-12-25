package moddingExperiments.util;

import net.minecraftforge.common.ForgeDirection;

public class Vector3 {
    
    private double x, y, z;
    
    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(ForgeDirection d) {
        this(d.offsetX, d.offsetY, d.offsetZ);
    }

    public Vector3 normalize() {
        return this.scalarMult(1.0F / this.length());
    }
    
    public Vector3 add(Vector3 v) {
        return new Vector3(this.x + v.x, this.y + v.y, this.z + v.z);
    }
    
    public Vector3 add(double a) {
        return new Vector3(this.x + a, this.y + a, this.z + a);
    }
    
    public Vector3 sub(Vector3 v) {
        return this.add(v.negate());
    }
    
    public Vector3 sub(double a) {
        return this.add(-a);
    }

    public Vector3 scalarMult(double a) {
        return new Vector3(a * this.x, a * this.y, a * this.z);
    }

    public Vector3 negate() {
        return scalarMult(-1);
    }
    
    public double dot(Vector3 v) {
        return this.x * v.x + this.y * v.y + this.z * v.z;
    }
    
    public double length2() {
        return this.dot(this);
    }
    
    public double length() {
        return Math.sqrt(this.length2());
    }

    public Vector3 cross(Vector3 v) {
        return new Vector3(this.y * v.z - this.z * v.y, this.z * v.x - this.x * v.z, this.x * v.y - this.y * v.x);
    }
   
    public void setNewCoordinates(double x, double y, double z) {
    	this.x = x;
    	this.y = y;
    	this.z = z;
    }
    
    @Override
    public String toString() {
        return "( " + x + ", " + y + ", " + z + " )";
    }

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
}
