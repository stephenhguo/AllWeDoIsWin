package team397;

import battlecode.common.*;

public class DroneLogic extends RobotLogic {

	private MapLocation attTarget;
	private int myRange;
	
    public DroneLogic(RobotController controller) {
        super(controller);
        attTarget = rc.getLocation();
        myRange = rc.getType().attackRadiusSquared;
    }
    
    public void run()
	{
		try {
			basicSupply();
			attack(myRange);
			attTarget = radio.getSwarmLoc(RobotType.DRONE);
			int attRad = radio.getSwarmRadius(RobotType.DRONE);
			goTo(attTarget, attRad);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
