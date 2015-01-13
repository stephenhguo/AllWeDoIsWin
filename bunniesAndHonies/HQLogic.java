package bunniesAndHonies;

import battlecode.common.*;

public class HQLogic extends RobotLogic{

	private RobotController myController;
	private int myRange;
	private Team myTeam;
	private Team enemyTeam;
	private int beaverNum;
	//private final int MINEPORT = 1;
	
	public HQLogic(RobotController controller)
	{
		super();
		myController = controller;
		myRange = myController.getType().attackRadiusSquared;
		myTeam = myController.getTeam();
		enemyTeam = myController.getTeam().opponent();
		beaverNum=0;
	}
	
	public void run()
	{
		try {
			attack(myController, myRange, enemyTeam);
			basicSupply();
			spawn();
			planAttack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void spawn() throws Exception
	{
		if(beaverNum>5){
			return;
		}
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
		int numBeaves = 0;
		int numSold = 0;
		for(RobotInfo inf : myRobots){
			if(inf.type.equals(RobotType.DRONE)){
				numDrones++;
			}
			if(inf.type.equals(RobotType.BEAVER)){
				numBeaves++;
			}
			if(inf.type.equals(RobotType.SOLDIER)){
				numSold++;
			}
		}
		beaverNum = numBeaves;
		if(numDrones>=20){
			broadcastOut(ATTACKXPORT, ATTACKYPORT);
		} else if(numDrones<=3){
			broadcastIn(ATTACKXPORT, ATTACKYPORT);
		}
		if(numSold>=15){
			broadcastOut(SOLDPORTX, SOLDPORTY);
		} else if(numSold<=3){
			broadcastIn(SOLDPORTX, SOLDPORTY);
		}
		broadcastOut(TANKPORTX, TANKPORTY);
	}
	
	public void broadcastOut(int XPORT, int YPORT){
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
		broadcastLocation(XPORT, YPORT, target);
	}
	
	public void broadcastIn(int XPORT, int YPORT){
		MapLocation target = myController.getLocation();
		broadcastLocation(XPORT, YPORT, target);
	}
	
	public void basicSupply(){
		RobotInfo[] myRobots = myController.senseNearbyRobots(GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, myTeam);
		double minSupply = 2000.0;
		if(myRobots.length==0){
			return;
		}
		RobotInfo rob = myRobots[0];
		for(RobotInfo inf : myRobots){
			if(inf.supplyLevel<minSupply){
				minSupply = inf.supplyLevel;
				rob = inf;
			}
		}
		try {
			myController.transferSupplies((int)myController.getSupplyLevel(), rob.location);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
