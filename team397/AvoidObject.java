package team397;

import battlecode.common.MapLocation;

public class AvoidObject extends SurroundObject{

	public final double W_AVOID = -30.;
	
	public AvoidObject(MapLocation loc, double weight, int r_sq){
		super(loc, 0., weight, r_sq);
	}
	
	public AvoidObject(MapLocation loc, int r_sq){
		super(loc, 0., r_sq);
	}
}
