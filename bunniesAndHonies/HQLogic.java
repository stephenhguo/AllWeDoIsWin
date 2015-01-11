package bunniesAndHonies;

import battlecode.common.*;

public class HQLogic extends RobotLogic{

	private RobotController myController;
	private final int ATTACKPORT = 0;
	//private final int MINEPORT = 1;

	public HQLogic(RobotController controller)
	{
		super();
		myController = controller;
	}
	
	public void run()
	{
		try {
			//attack();
			spawn();
			broadcastOut();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void spawn() throws Exception
	{
		if(myController.isCoreReady())
		{
			for(Direction direction : directions)
			{
				if(myController.canSpawn(direction, RobotType.BEAVER))
				{
					myController.spawn(direction, RobotType.BEAVER);
					return; //Can only spawn once per round
				}
			}
		}
	}
	
	/**
	 * changes the map location into an integer and then broadcasts
	 * @param loc MapLocation to be broadcast
	 */
	public void broadcastLocation(int port, MapLocation loc){
		int maploc = loc.x*1000+loc.y;
		try {
			myController.broadcast(port, maploc);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
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
		broadcastLocation(ATTACKPORT, target);
	}
}
