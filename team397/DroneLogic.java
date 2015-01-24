package team397;

import battlecode.common.*;

public class DroneLogic extends RobotLogic {

	private MapLocation attTarget;
	
    public DroneLogic(RobotController controller) {
        super(controller);
        attTarget = rc.getLocation();
    }
    
			goTo(attTarget, attRad);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
