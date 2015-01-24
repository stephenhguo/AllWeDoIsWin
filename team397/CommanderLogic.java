package team397;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class CommanderLogic extends RobotLogic {

    public CommanderLogic(RobotController controller) {
    	super(controller);
    }

    public void run() throws GameActionException{
    	attack(myRange);
		MapLocation attTarget = radio.getSwarmLoc(RobotType.TANK);
		int attRad = radio.getSwarmRadius(RobotType.TANK);
		if (attTarget.equals(rc.senseHQLocation()))
			simpleGoal(attTarget, attRad);
		else
			goAttack(false, attTarget, attRad);
    }
}
