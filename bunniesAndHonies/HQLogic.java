package bunniesAndHonies;

import battlecode.common.*;

public class HQLogic extends RobotLogic{

	private RobotController myController;
	private int myRange;
	private Team myTeam;
	private Team enemyTeam;
	//private final int MINEPORT = 1;
	
	public HQLogic(RobotController controller)
	{
		super();
		myController = controller;
		myRange = myController.getType().attackRadiusSquared;
		myTeam = myController.getTeam();
		enemyTeam = myController.getTeam().opponent();
	}
	
	public void run()
	{
		try {
			attack(myController, myRange, enemyTeam);
			spawn();
			planAttack();
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
				if(myController.canSpawn(direction, RobotType.BEAVER))
				{
					myController.spawn(direction, RobotType.BEAVER);
					return;
				}
			}
		}
	}
	
	/**
	 * changes the map location into an integer and then broadcasts
	 * @param loc MapLocation to be broadcast
	 */
	public void broadcastLocation(int port1, int port2, MapLocation loc){
		try {
			myController.setIndicatorString(0, Integer.toString(GameConstants.BROADCAST_MAX_CHANNELS));
			myController.broadcast(port1, loc.x);
			myController.broadcast(port2, loc.y);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
	public void planAttack(){
		RobotInfo[] myRobots = myController.senseNearbyRobots(999999, myTeam);
		int numDrones = 0;
		for(RobotInfo inf : myRobots){
			if(inf.type == RobotType.DRONE){
				numDrones++;
			}
		}
		if(numDrones>=15){
			broadcastOut();
		} else if(numDrones<=3){
			broadcastIn();
		}
	}
	
	public void broadcastOut(){
		//broadcast attack target
		MapLocation[] enemyTowers = myController.senseEnemyTowerLocations();
		MapLocation target;
		if(enemyTowers.length==0){
			target = myController.senseEnemyHQLocation();
		} else{
			MapLocation ownloc = myController.senseHQLocation();
			int mindist = ownloc.distanceSquaredTo(enemyTowers[0]);
			target = enemyTowers[0];
			for(int i=1; i<enemyTowers.length; i++){
				if(ownloc.distanceSquaredTo(enemyTowers[i])<mindist){
					mindist = ownloc.distanceSquaredTo(enemyTowers[i]);
					target = enemyTowers[i];
				}
			}
		}
		broadcastLocation(ATTACKXPORT, ATTACKYPORT, target);
	}
	
	public void broadcastIn(){
		MapLocation target = myController.getLocation();
		broadcastLocation(ATTACKXPORT, ATTACKYPORT, target);
	}
}
