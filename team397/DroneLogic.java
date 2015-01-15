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
			attTarget = radio.getAttackLoc(RobotType.DRONE);
			goTo(attTarget);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
