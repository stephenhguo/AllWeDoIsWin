package team397;

import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class TankLogic extends RobotLogic {

	private MapLocation attTarget;
	
    public TankLogic(RobotController controller) {
        super(controller);
    }
    
    public void run() throws GameActionException{
    	attack(myRange);
		attTarget = radio.getSwarmLoc(RobotType.TANK);
		int attRad = radio.getSwarmRadius(RobotType.TANK);
		if (attTarget.equals(rc.senseHQLocation()))
			simpleGoal(attTarget, attRad);
		else
			goAttack(false, attTarget, attRad);
    }
}
