package team397;

import battlecode.common.*;

public class HQLogic extends RobotLogic{

	private int myRange;
	private int beaverNum;
	//private final int MINEPORT = 1;
	
	public HQLogic(RobotController controller)
	{
		super(controller);
		myRange = rc.getType().attackRadiusSquared;
		myTeam = rc.getTeam();
		enemyTeam = rc.getTeam().opponent();
		beaverNum=0;
	}
	
	public void run()
	{
		try {
			attack(myRange);
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
		RobotInfo[] myRobots = rc.senseNearbyRobots(999999, myTeam);
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
			attackEnemyTower(RobotType.DRONE);
		} else if(numDrones<=3){
			retreat(RobotType.DRONE);
		}
		if(numSold>=15){
			attackEnemyTower(RobotType.SOLDIER);
		} else if(numSold<=3){
			retreat(RobotType.SOLDIER);
		}
		
		attackEnemyTower(RobotType.TANK);
	}
	
	public void attackEnemyTower(RobotType type) throws GameActionException{
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
		radio.setAttackLoc(target, type);
	}
	
	public void retreat(RobotType type) throws GameActionException{
		MapLocation target = rc.getLocation();
		radio.setAttackLoc(target, type);
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
