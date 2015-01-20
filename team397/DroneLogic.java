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
			PFObject[] objects;
			/*if (attTarget.equals(rc.senseHQLocation())){
				objects = avoidEnemyTowersAndHQ();
				PFObject[] base = {new SurroundObject(attTarget, 0), new LineObject(attTarget, new MapLocation(-1,-1), 2, -80)};
				objects = combine(objects, base);
			}
			else{
				objects = avoidEnemyTowersAndHQ(attTarget);
			}*/
			objects = new PFObject[0];
			makeNextMove(objects);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
