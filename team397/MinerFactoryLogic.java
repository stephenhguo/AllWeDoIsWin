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
    	int buildPhase = radio.getBuildPhase();
    	int minerNum = radio.readCount(RobotType.MINER);
    	if(buildPhase>=1 && minerNum<30){
			if(rc.isCoreReady())
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
}
