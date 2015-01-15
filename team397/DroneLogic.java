package team397;

import java.util.Random;

import battlecode.common.*;

public class DroneLogic extends RobotLogic {

	Random rand;
	private MapLocation attTarget;
	private int myRange;
	
    public DroneLogic(RobotController controller) {
        super(controller);
        rand = new Random(rc.getID());
        attTarget = rc.getLocation();
        myRange = rc.getType().attackRadiusSquared;
    }
    
    public void run()
	{
		try {
			basicSupply();
			attack(myRange);
			attTarget = radio.getAttackLoc(RobotType.DRONE);
			move(attTarget);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
