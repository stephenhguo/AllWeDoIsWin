package team397;

import battlecode.common.*;

public class DroneLogic extends RobotLogic {

	private MapLocation attTarget;
	
    public DroneLogic(RobotController controller) {
        super(controller);
        attTarget = rc.getLocation();
    }
    
    public void run()
	{
		try {
			basicSupply();
			attack(myRange);
			attTarget = radio.getSwarmLoc(RobotType.DRONE);
			int attRad = radio.getSwarmRadius(RobotType.DRONE);
			if (attTarget.equals(rc.senseHQLocation()))
				simpleGoal(attTarget, attRad);
			else
				goAttack(false, attTarget, attRad);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
