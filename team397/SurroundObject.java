package team397;

import battlecode.common.MapLocation;

public class SurroundObject extends PFObject {

	public final double W_KEEPOUT = -30.;
	public final double W_GOAL = 30.;
	public double w_in, w_out;
	public int r_sq;
	
	public SurroundObject(MapLocation location, double w_outside, double w_inside, int rad_sq){
		super(location);
		r_sq = rad_sq;
		w_in = w_inside;
		w_out = w_outside;
		//System.out.println(""+ w_out + ", " + w_outside);
	}
	
	public SurroundObject(MapLocation location, double w_outside, int rad_sq){
		super(location);
		r_sq = rad_sq;
		w_in = W_KEEPOUT;
		w_out = w_outside;
	}
	
	public SurroundObject(MapLocation location, int rad_sq){
		super(location);
		r_sq = rad_sq;
		w_in = W_KEEPOUT;
		w_out = W_GOAL;
	}
	
	public double test(){
		return w_out;
	}
	
	public double calculate(MapLocation other){
		//System.out.println("" + w_out + ", " + w_in + ", " + r_sq);
		return PFObject.decayUpTo(d_sq_to(other), w_out, w_in, r_sq);
	}
}
