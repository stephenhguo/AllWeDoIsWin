package team397;

import battlecode.common.*;

public class HQLogic extends RobotLogic{

	private int myRange;
	private int numBeavers, numDrones, numSoldiers, numTanks, numMiners, numMinerFact;
	private boolean beaversSearching, minersSearching;
	//private final int MINEPORT = 1;
	
	public HQLogic(RobotController controller) throws GameActionException
	{
		super(controller);
		myRange = rc.getType().attackRadiusSquared;
		
		MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
		MapLocation target = rc.senseEnemyHQLocation(), myLoc = rc.getLocation();
		
		radio.setEnemyTowerLocs(enemyTowers);
		radio.setEnemyHQLoc(target);
		int midx = (target.x + myLoc.x) / 2, midy = (target.y + myLoc.y) / 2;

		if(midx == myLoc.x)
			radio.reportSymmetry(1,midx,midy);
		else if(midy == myLoc.y)
			radio.reportSymmetry(-1,midx,midy);
		else
			radio.reportSymmetry(0,midx,midy);
		
		radio.searchOrders(RobotType.BEAVER, true);
		radio.searchOrders(RobotType.MINER, true);
		beaversSearching = true;
		minersSearching = true;
	}
	
	public void run()
	{
		try {
			attack(myRange);
			basicSupply();
			spawn();
			countBots();
			if (numBeavers > 5 && beaversSearching)
				radio.searchOrders(RobotType.BEAVER, false);
			planAttack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void countBots(){
		RobotInfo[] myRobots = rc.senseNearbyRobots(999999, myTeam);
		numDrones = 0;
	    numBeavers = 0;
		numSoldiers = 0;
		numTanks = 0;
		numMiners = 0; 
		numMinerFact = 0;
		for(RobotInfo inf : myRobots){
			if(inf.type.equals(RobotType.DRONE)){
				numDrones++;
			}
			if(inf.type.equals(RobotType.BEAVER)){
				numBeavers++;
			}
			if(inf.type.equals(RobotType.SOLDIER)){
				numSoldiers++;
			}
			if(inf.type.equals(RobotType.TANK)){
				numTanks++;
			}
			if(inf.type.equals(RobotType.MINER)){
				numMiners++;
			}
			if(inf.type.equals(RobotType.MINERFACTORY)){
				numMinerFact++;
			}
		}
	}
	
	public void spawn() throws Exception
	{
		if(numBeavers>8){
			return;
		}
		if(rc.isCoreReady())
		{
			for(Direction direction : Direction.values())
			{
				if(rc.canSpawn(direction, RobotType.BEAVER))
				{
					rc.spawn(direction, RobotType.BEAVER);
					return;
				}
			}
		}
	}
	
	
	public void planAttack() throws GameActionException{
		if(numDrones>=20){
			swarmEnemyTower(RobotType.DRONE);
		} else if(numDrones<=7){
			retreat(RobotType.DRONE);
		}
		if(numSoldiers>=15){
			swarmEnemyTower(RobotType.SOLDIER);
		} else if(numSoldiers<=3){
			retreat(RobotType.SOLDIER);
		}
		
		swarmEnemyTower(RobotType.TANK);
	}
	
	public void swarmEnemyTower(RobotType type) throws GameActionException{
		//broadcast attack target
		MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
		MapLocation target;
		if(enemyTowers.length==0){
			target = rc.senseEnemyHQLocation();
		} else{
			MapLocation ownloc = rc.senseHQLocation();
			int mindist = ownloc.distanceSquaredTo(enemyTowers[0]);
			target = enemyTowers[0];
			for(int i=1; i<enemyTowers.length; i++){
				if(ownloc.distanceSquaredTo(enemyTowers[i])<mindist){
					mindist = ownloc.distanceSquaredTo(enemyTowers[i]);
					target = enemyTowers[i];
				}
			}
		}
		radio.setSwarm(target, type);
		radio.setEnemyTowerLocs(enemyTowers);
	}
	
	public void retreat(RobotType type) throws GameActionException{
		MapLocation target = rc.getLocation();
		radio.setSwarm(target, type);
	}
	
	public void basicSupply(){
		RobotInfo[] myRobots = rc.senseNearbyRobots(GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, myTeam);
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
			rc.transferSupplies((int)rc.getSupplyLevel(), rob.location);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
