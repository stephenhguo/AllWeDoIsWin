package team397;

import battlecode.common.MapLocation;

public class AttractObject extends PFObject {

	private final double W_GOAL = 30.0;
	public double w_out;
	
	public AttractObject(MapLocation location, double w){
		super(location);
		w_out = w;
	}
	
	public AttractObject(MapLocation location){
		super(location);
		w_out = W_GOAL;
	}
	
	public double calculate(MapLocation other){
		return PFObject.decay(d_sq_to(other), w_out);
	}
}
