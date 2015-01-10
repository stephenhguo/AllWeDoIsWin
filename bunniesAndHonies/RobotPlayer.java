package bunniesAndHonies;

import battlecode.common.*;

public class RobotPlayer {
	/*
	 * Run at the start of each unit's existence.
	 */
	public static void run(RobotController controller)
	{
		
		/*
		 * Determine unit type.
		 */
		RobotType myType = controller.getType();
		
		/*
		 * Initialize logic to suppress error.
		 */
		RobotLogic myLogic = new RobotLogic();
		
		/*
		 * Initialize logic based on unit type.
		 */
		switch(myType)
		{
			case AEROSPACELAB:
				break;
			case BARRACKS:
				break;
			case BASHER:
				break;
			case BEAVER:
				myLogic = new BeaverLogic(controller);
				break;
			case COMMANDER:
				break;
			case COMPUTER:
				break;
			case DRONE:
				break;
			case HANDWASHSTATION:
				break;
			case HELIPAD:
				break;
			case HQ:
				myLogic = new HQLogic(controller);
				break;
			case LAUNCHER:
				break;
			case MINER:
				break;
			case MINERFACTORY:
				break;
			case MISSILE:
				break;
			case SOLDIER:
				break;
			case SUPPLYDEPOT:
				break;
			case TANK:
				break;
			case TANKFACTORY:
				break;
			case TECHNOLOGYINSTITUTE:
				break;
			case TOWER:
				myLogic = new TowerLogic(controller);
				break;
			case TRAININGFIELD:
				break;
		}
		
		/*
		 * Run unit logic for the duration of the game.
		 */
		while(true)
		{
			myLogic.run();
			controller.yield();
		}
	}
}
