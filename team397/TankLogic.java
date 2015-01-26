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
	private boolean supplying;
	
    public TankLogic(RobotController controller) {
        super(controller);
        supplying = true;
    }

    public void run() throws GameActionException{
    	/*if(supplying){
    		if(rc.getSupplyLevel()>1900){
				supplying=false;
			}
			simpleGoal(rc.senseHQLocation(),3);
    	} else{*/
    	basicSupply();
		attack(myRange);
		attTarget = radio.getSwarmLoc(RobotType.TANK);
		int attRad = radio.getSwarmRadius(RobotType.TANK);
		if (attTarget.equals(rc.senseHQLocation()))
			simpleGoal(attTarget, attRad);
		else
			goAttack(false, attTarget, attRad);
    	//}
    }

//    /*
//    public void run(){
//    	attack(myController, myRange, enemyTeam);
//    	attTarget = getAttTarget();
//        myRange = rc.getType().attackRadiusSquared;
//    }
//=======
//>>>>>>> ebbaca9afb2fa3344737eaabca6d32efdb525265
//    
//    public void run() throws GameActionException{
//    	attack(myRange);
//		attTarget = radio.getSwarmLoc(RobotType.TANK);
//		int attRad = radio.getSwarmRadius(RobotType.TANK);
//		if (attTarget.equals(rc.senseHQLocation()))
//			simpleGoal(attTarget, attRad);
//		else
//			goAttack(false, attTarget, attRad);
//    }
}
