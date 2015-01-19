package team397;

import battlecode.common.*;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class BarrackLogic extends RobotLogic {


	
    public BarrackLogic(RobotController controller) {
        super(controller);
    }
    
    public void run()
	{
		try {
			//spawn();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    /*
    public void spawn() throws Exception
	{
    	RobotInfo[] myRobots = rc.senseNearbyRobots(999999, myTeam);
		int numSold = 0;
		for(RobotInfo inf : myRobots){
			if(inf.type.equals(RobotType.SOLDIER)){
				numSold++;
			}
		}
		if(numSold<26 && rc.isCoreReady())
		{
			for(Direction direction : Direction.values())
			{
				if(rc.canSpawn(direction, RobotType.SOLDIER))
				{
					rc.spawn(direction, RobotType.SOLDIER);
					return; //Can only spawn once per round
				}
			}
		}
	}*/
}
