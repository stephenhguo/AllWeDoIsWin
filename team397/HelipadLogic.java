package team397;

import battlecode.common.*;

public class HelipadLogic extends RobotLogic {

	
    public HelipadLogic(RobotController controller) {
        super(controller);
    }
    
    public void run()
	{
		try {
			spawn();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public void spawn() throws Exception
	{
    	int buildPhase = radio.getBuildPhase();
    	int droneNum = radio.readCount(RobotType.DRONE);
    	if(buildPhase>=1 /*&& buildPhase<4*/ && droneNum<25){
			if(rc.isCoreReady())
			{
				for(Direction direction : Direction.values())
				{
					if(rc.canSpawn(direction, RobotType.DRONE))
					{
						rc.spawn(direction, RobotType.DRONE);
						return; //Can only spawn once per round
					}
				}
			}
    	}
    	if(buildPhase==1 && droneNum>=1){
    		radio.advanceBuildPhase(1);
    	}
	}

}
