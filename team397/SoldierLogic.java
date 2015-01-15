package team397;

import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class SoldierLogic extends RobotLogic {

	private int myRange;
	private Random rand;
	private MapLocation attTarget;
	
    public SoldierLogic(RobotController controller) {
        super(controller);
        myRange = rc.getType().attackRadiusSquared;
		rand = new Random(rc.getID());
    }
    
    public void run(){
    	attack(myRange);
    	attTarget = getAttTarget();
		move(attTarget);
		roam(rand);
    }
    
    public MapLocation getAttTarget(){
		int msgx;
		int msgy;
		try {
			msgx = rc.readBroadcast(SOLDPORTX);
			msgy = rc.readBroadcast(SOLDPORTY);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			msgx = 0;
			msgy = 0;
		}

		return new MapLocation(msgx,msgy);		
	}
    
	public void move(MapLocation target){
		Direction movedir = rc.getLocation().directionTo(target);
		if(rc.canMove(movedir)){
			try {
				rc.move(movedir);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		} else {
			roam(rand);
		}
	}

}
