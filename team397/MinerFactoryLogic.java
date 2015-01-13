package team397;

import battlecode.common.Direction;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class MinerFactoryLogic extends RobotLogic {

	private RobotController myController;
	private Team myTeam;
	
    public MinerFactoryLogic(RobotController controller) {
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
    	RobotInfo[] myRobots = myController.senseNearbyRobots(999999, myTeam);
		int numMine = 0;
		for(RobotInfo inf : myRobots){
			if(inf.type.equals(RobotType.MINER)){
				numMine++;
			}
		}
		if(numMine<31 && myController.isCoreReady())
		{
			for(Direction direction : Direction.values())
			{
				if(myController.canSpawn(direction, RobotType.MINER))
				{
					myController.spawn(direction, RobotType.MINER);
					return; //Can only spawn once per round
				}
			}
		}
	}
}
