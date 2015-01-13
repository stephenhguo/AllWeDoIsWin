package bunniesAndHonies;

import battlecode.common.*;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class BarrackLogic extends RobotLogic {

	private RobotController myController;
	private Team myTeam;
	
    public BarrackLogic(RobotController controller) {
        super();
        myController = controller;
        myTeam = myController.getTeam();
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
    	RobotInfo[] myRobots = myController.senseNearbyRobots(999999, myTeam);
		int numSold = 0;
		for(RobotInfo inf : myRobots){
			if(inf.type.equals(RobotType.SOLDIER)){
				numSold++;
			}
		}
		if(numSold<26 && myController.isCoreReady())
		{
			for(Direction direction : Direction.values())
			{
				if(myController.canSpawn(direction, RobotType.SOLDIER))
				{
					myController.spawn(direction, RobotType.SOLDIER);
					return; //Can only spawn once per round
				}
			}
		}
	}
}
