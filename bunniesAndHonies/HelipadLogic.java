package bunniesAndHonies;

import battlecode.common.*;

public class HelipadLogic extends RobotLogic {

	private RobotController myController;
	
    public HelipadLogic(RobotController controller) {
        super();
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
		if(myController.isCoreReady())
		{
			for(Direction direction : Direction.values())
			{
				if(myController.canSpawn(direction, RobotType.DRONE))
				{
					myController.spawn(direction, RobotType.DRONE);
					return; //Can only spawn once per round
				}
			}
		}
	}

}
