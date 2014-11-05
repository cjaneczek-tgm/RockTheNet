package application;

/**
 * 
 * @author Osman Oezsoy
 * @version 2014-10-30
 */
public class Koordinaten {
	private int x;
	private double y;

	public Koordinaten(int x, double y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double d) {
		this.y = d;
	}
}
