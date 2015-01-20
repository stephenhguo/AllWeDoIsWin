package team397;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class CommanderLogic extends RobotLogic {

	MapLocation attTarget;
    public CommanderLogic(RobotController controller) {
    	super(controller);
    	attTarget = null;
    	
    }
    
    public void run() throws GameActionException{
    	radio.setSwarm(rc.getLocation(), RobotType.TANK);
		attack(myRange);
		attTarget = radio.getSwarmLoc(RobotType.COMMANDER);
		//int attRad = radio.getSwarmRadius(RobotType.COMMANDER);
		PFObject[] objects;
		if (attTarget.equals(rc.senseHQLocation())){
			objects = avoidEnemyTowersAndHQ();
			PFObject[] base = {new SurroundObject(attTarget, 0), new LineObject(attTarget, new MapLocation(-1,-1), 2, -80)};
			objects = combine(objects, base);
		}
		else
			objects = avoidEnemyTowersAndHQ(attTarget);
    	goTo(radio.getEnemyHQLoc());
    }

}
