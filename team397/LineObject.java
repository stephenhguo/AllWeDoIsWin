package team397;

import battlecode.common.MapLocation;

public class LineObject extends PFObject {

	public final double W_STAYOUT = -30.;
	public int width;
	public MapLocation root, vector;
	public double magnitude, weight;
	
	
	public LineObject(MapLocation root_pt, MapLocation vect, int wid, double wait){
		super(root_pt);
		root = root_pt;
		vector = vect;
		width = wid;
		magnitude = vector.x * vector.x + vector.y * vector.y;
		weight = wait;
	}
	
	public double calculate(MapLocation other) {
		double projLen = projectedLength(other);
		if(projLen >= 0){
			double distToLine = projectedDistance(other, projLen);
			if(distToLine <= width)
				return weight;
			else
				return 0.;
		}
		else
			return 0.;
	}
	
	private double projectedDistance(MapLocation other, double projLen){ //distance from line
		int v_x = other.x - root.x, v_y = other.y - root.y;
		double u_x = vector.x * projLen / magnitude, u_y = vector.y * projLen / magnitude;
		double delt_x = u_x - v_x, delt_y = u_y - v_y;
		return delt_x * delt_x + delt_y * delt_y;
		
	}
	
	private double projectedLength(MapLocation other){ //distance along 'vector'
		int v_x = other.x - root.x, v_y = other.y - root.y;
		return (v_x * vector.x + v_y * vector.y) / (magnitude);
	}

}
