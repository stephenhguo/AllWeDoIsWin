package team397;

import battlecode.common.Direction;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class TankFactoryLogic extends RobotLogic {

	private RobotController myController;
	
    public TankFactoryLogic(RobotController controller) {
        super(controller);
        myController = controller;
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
    	if(buildPhase==3){
			if(myController.isCoreReady())
			{
				for(Direction direction : Direction.values())
				{
					if(myController.canSpawn(direction, RobotType.TANK))
					{
						myController.spawn(direction, RobotType.TANK);
						return; //Can only spawn once per round
					}
				}
			}
    	}
	}
}
