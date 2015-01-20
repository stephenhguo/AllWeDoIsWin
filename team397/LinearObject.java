package team397;

import battlecode.common.MapLocation;

public class LinearObject extends PFObject {

	private int r_sq;
	private double weight; 
	
	public LinearObject(MapLocation location, double w, int rad_sq){
		super(location);
		weight = w;
		r_sq = rad_sq;
	}
	
	public double calculate(MapLocation other) {
		return PFObject.linear(d_sq_to(other), weight, r_sq);
	}

}
