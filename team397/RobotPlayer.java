package team397;

import battlecode.common.*;

public class RobotPlayer {
	/*
	 * Run at the start of each unit's existence.
	 */
	public static void run(RobotController controller) throws GameActionException
	{
		
		/*
		 * Determine unit type.
		 */
		RobotType myType = controller.getType();
		
		/*
		 * Initialize logic to suppress error.
		 */
		RobotLogic myLogic = new RobotLogic(controller);
		
		/*
		 * Initialize logic based on unit type.
		 */
		switch(myType)
		{
			case AEROSPACELAB:
			    myLogic=new AerospaceLogic(controller);
				break;
			case BARRACKS:
				myLogic =new BarrackLogic(controller);
				break;
			case BASHER:
			    myLogic=new BasherLogic(controller);
				break;
			case BEAVER:
				myLogic = new BeaverLogic(controller);
				break;
			case COMMANDER:
			    myLogic=new CommanderLogic(controller);
				break;
			case COMPUTER:
			    myLogic=new ComputerLogic(controller);
				break;
			case DRONE:
			    myLogic = new DroneLogic(controller);
				break;
			case HANDWASHSTATION:
				break;
			case HELIPAD:
			    myLogic=new HelipadLogic(controller);
				break;
			case HQ:
				myLogic = new HQLogic(controller);
				break;
			case LAUNCHER:
			    myLogic= new LauncherLogic(controller);
				break;
			case MINER:
			    myLogic= new MinerLogic(controller);
				break;
			case MINERFACTORY:
			    myLogic=new MinerFactoryLogic(controller);
				break;
			case MISSILE:
			    myLogic=new MissileLogic(controller);
				break;
			case SOLDIER:
			    myLogic=new SoldierLogic(controller);
				break;
			case SUPPLYDEPOT:
			    myLogic=new SupplyLogic(controller);
				break;
			case TANK:
			    myLogic=new TankLogic(controller);
				break;
			case TANKFACTORY:
			    myLogic=new TankFactoryLogic(controller);
				break;
			case TECHNOLOGYINSTITUTE:
			    myLogic=new MITLogic(controller);
				break;
			case TOWER:
				myLogic = new TowerLogic(controller);
				break;
			case TRAININGFIELD:
			    myLogic=new TrainingFieldLogic(controller);
				break;
		}
		
		/*
		 * Run unit logic for the duration of the game.
		 */
		
		//myLogic.buildMiniMap(); Add back in if we want minimap functionality
		
		while(true)
		{
			myLogic.run();
			controller.yield();
		}
	}
}
