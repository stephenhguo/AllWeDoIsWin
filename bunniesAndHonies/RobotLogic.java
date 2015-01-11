package bunniesAndHonies;

import java.util.Random;

import battlecode.common.*;

/*
 * This class is overridden with specific unit logic.
 */
public class RobotLogic 
{	
	public final int ATTACKXPORT = 0;
	public final int ATTACKYPORT = 1;
	
	public final int NEXTBUILD = 2;
	
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
	
	public void attack(RobotController myController, int myRange, Team enemyTeam){
		if(!myController.isWeaponReady()){
			return;
		}
		RobotInfo[] enemies = myController.senseNearbyRobots(myRange, enemyTeam);
		if(enemies.length==0){
			return;
		}
		double minHealth = 2000.0;
		RobotInfo enemyToAttack = enemies[0];
		for(RobotInfo inf : enemies){
			if(inf.health<minHealth){
				minHealth = inf.health;
				enemyToAttack = inf;
			}
		}
		try {
			myController.attackLocation(enemyToAttack.location);
			myController.yield();
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	public void roam(RobotController myController, Random rand){
		int dir = rand.nextInt(8);
		myController.setIndicatorString(0, Double.toString(myController.getHealth()));
		Direction movedir;
		switch(dir){
		case 0:
			movedir = Direction.NORTH;
			break;
		case 1:
			movedir = Direction.NORTH_EAST;
			break;
		case 2:
			movedir = Direction.EAST;
			break;
		case 3:
			movedir = Direction.SOUTH_EAST;
			break;
		case 4:
			movedir = Direction.SOUTH;
			break;
		case 5:
			movedir = Direction.SOUTH_WEST;
			break;
		case 6:
			movedir = Direction.WEST;
			break;
		case 7:
			movedir = Direction.NORTH_WEST;
			break;
		default:
			movedir=Direction.NORTH;
		}
		try {
			myController.move(movedir);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
}
