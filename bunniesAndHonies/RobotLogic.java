package bunniesAndHonies;

import battlecode.common.Direction;
import battlecode.common.RobotType;

/*
 * This class is overridden with specific unit logic.
 */
public class RobotLogic 
{	
	
	/*
	 * Used for easy access to directions.
	 */
	public Direction[] directions = {Direction.NORTH, 
									 Direction.NORTH_EAST, 
									 Direction.EAST, 
									 Direction.SOUTH_EAST, 
									 Direction.SOUTH, 
									 Direction.SOUTH_WEST, 
									 Direction.WEST, 
									 Direction.NORTH_WEST};
	
	/*
	 * This is the attack preference for all units.
	 */
	public RobotType[] attackPreference = {RobotType.HQ, 
			  RobotType.TOWER, 
			  RobotType.MISSILE,
			  RobotType.TANK,
			  RobotType.COMMANDER,
			  RobotType.DRONE,
			  RobotType.LAUNCHER,
			  RobotType.BASHER,
			  RobotType.SOLDIER,
			  RobotType.BEAVER,
			  RobotType.MINER,
			  RobotType.AEROSPACELAB,
			  RobotType.TANKFACTORY,
			  RobotType.TRAININGFIELD,
			  RobotType.HELIPAD,
			  RobotType.BARRACKS,
			  RobotType.MINERFACTORY,
			  RobotType.SUPPLYDEPOT,
			  RobotType.TECHNOLOGYINSTITUTE,
			  RobotType.HANDWASHSTATION,
			  RobotType.COMPUTER};
	/*
	 * This method is meant to be overridden for each specific unit logic.
	 */
	public void run(){}
}
