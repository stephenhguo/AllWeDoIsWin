package team397;

import battlecode.common.Direction;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class MinerFactoryLogic extends RobotLogic {

	private Team myTeam;
	
    public MinerFactoryLogic(RobotController controller) {
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
    	RobotInfo[] myRobots = rc.senseNearbyRobots(999999, myTeam);
		int numMine = 0;
		for(RobotInfo inf : myRobots){
			if(inf.type.equals(RobotType.MINER)){
				numMine++;
			}
		}
		if(numMine<31 && rc.isCoreReady())
		{
			for(Direction direction : Direction.values())
			{
				if(rc.canSpawn(direction, RobotType.MINER))
				{
					rc.spawn(direction, RobotType.MINER);
					return; //Can only spawn once per round
				}
			}
		}
	}
}
