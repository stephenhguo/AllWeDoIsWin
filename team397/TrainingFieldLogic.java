package team397;

import battlecode.common.Direction;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class TrainingFieldLogic extends RobotLogic {

    public TrainingFieldLogic(RobotController controller) {
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
    	int commNum = radio.readCount(RobotType.COMMANDER);
    	if((buildPhase==2 || buildPhase==4) && commNum<1){
			if(rc.isCoreReady())
			{
				for(Direction direction : Direction.values())
				{
					if(rc.canSpawn(direction, RobotType.COMMANDER))
					{
						rc.spawn(direction, RobotType.COMMANDER);
						return; //Can only spawn once per round
					}
				}
			}
    	}
	}

}
