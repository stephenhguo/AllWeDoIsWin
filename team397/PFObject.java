package team397;

import battlecode.common.MapLocation;

public abstract class PFObject {
	
	protected MapLocation loc;
	
	public PFObject(MapLocation location){
		loc = location;
	}
	
	public double test(){
		return .1;
	}
	
	public abstract double calculate(MapLocation location);
	
	public int d_sq_to(MapLocation other){
		return loc.distanceSquaredTo(other);
	}
	
	public static double linear(int d_sq, double weight, int r_sq){
		if (d_sq < r_sq)
			return (double) weight / r_sq * (r_sq - d_sq);
		else
			return 0.;
	}
	
	public MapLocation getLocation(){
		return loc;
	}
	
	public static double step(int d_sq, double weight, int r_sq){
		if (d_sq <= r_sq)
			return weight;
		else
			return 0;
	}
	
	public static double decay(int d_sq, double weight, int r_sq){
		if (d_sq >= r_sq)
			return weight / (1.+ (d_sq-r_sq));
		else
			return weight*(d_sq/r_sq)*(d_sq/r_sq);
			
	}
	
	public static double decay(int d_sq, double weight){
		return decay(d_sq, weight, 0);
	}
	
	public static double decayUpTo(int d_sq, double w_out, double w_in, int r_sq){
		if (d_sq > r_sq)
			return w_out / (1.+ (d_sq-r_sq));
		else
			return w_in;
	}
	
	public static double linearUpTo(int d_sq, double w_out, double w_in, int boundary_sq, int r_sq){
		if (d_sq >= r_sq)
			return w_out / r_sq * (boundary_sq + r_sq - d_sq);
		else
			return w_in;
	}
	
	public static double repulsiveCorner(int d_x, int d_y, double weight, int quad, int r_sq){
		if(quad == 1 && d_x > 0 && d_y > 0)
			return weight*(r_sq - d_x - d_y);
		else if(quad == 2 && d_x < 0 && d_y > 0)
			return weight*(r_sq + d_x - d_y);
		else if(quad == 3 && d_x < 0 && d_y < 0)
			return weight*(r_sq + d_x + d_y);
		else if(quad == 4 && d_x > 0 && d_y < 0)
			return weight*(r_sq - d_x + d_y);
		else
			return 0.;
	}
}
