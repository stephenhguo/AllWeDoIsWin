package team397;

import battlecode.common.MapLocation;

public class RepelObject extends AttractObject {
	private final static double W_REPEL = -30;
	
	public RepelObject(MapLocation location, double w){
		super(location, w);

	}
	
	public RepelObject(MapLocation location){
		super(location, W_REPEL);
	}
}
