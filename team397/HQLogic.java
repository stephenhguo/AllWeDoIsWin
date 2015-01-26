package team397;

import battlecode.common.*;

public class HQLogic extends RobotLogic{

	private int myRange;
	private int numBeaver, numDrone, numTank, numMiner, numComm;
	private int numMinerFact, numHeli, numMIT, numTrainF, numBar, numTankF;
	private int commandersSpawned;
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
		
		retreat(RobotType.TANK);
		radio.setHunterSwitch(radio.HUNT_TEAM);
		
		radio.initializeBuildPhase();
		
		numBeaver=numDrone=numTank=numMiner=numComm=numMinerFact=numHeli=numMIT=numTrainF=numBar=numTankF=0;
	}
	
	public void run() throws GameActionException
	{
		attack(myRange);
		basicSupply();
		spawn();
		countBots();
		manageTeams();
		planAttack();
		rc.setIndicatorString(0, Integer.toString(radio.getBuildPhase()));
	}
	
	public void countBots() throws GameActionException{
		RobotInfo[] myRobots = rc.senseNearbyRobots(999999, myTeam);
		int newDroneNum = 0;
	    int newBeavNum = 0;
		int newTankNum = 0;
		int newMinerNum = 0; 
		int newCommNum = 0;
		int newMineFacNum = 0;
		int newHeliNum = 0;
		int newMITNum = 0;
		int newTrainFNum = 0;
		int newBarNum = 0;
		int newTankFNum = 0;
		for(RobotInfo inf : myRobots){
			if(inf.type.equals(RobotType.DRONE)){
				newDroneNum++;
			} else if(inf.type.equals(RobotType.BEAVER)){
				newBeavNum++;
			} else if(inf.type.equals(RobotType.TANK)){
				newTankNum++;
			} else if(inf.type.equals(RobotType.MINER)){
				newMinerNum++;
			} else if(inf.type.equals(RobotType.COMMANDER)){
				newCommNum++;
			} else if(inf.type.equals(RobotType.MINERFACTORY)){
				newMineFacNum++;
			} else if(inf.type.equals(RobotType.HELIPAD)){
				newHeliNum++;
			} else if(inf.type.equals(RobotType.TECHNOLOGYINSTITUTE)){
				newMITNum++;
			} else if(inf.type.equals(RobotType.TRAININGFIELD)){
				newTrainFNum++;
			} else if(inf.type.equals(RobotType.BARRACKS)){
				newBarNum++;
			} else if(inf.type.equals(RobotType.TANKFACTORY)){
				newTankFNum++;
			}
		}
		if(newBeavNum!=numBeaver){
			radio.updateCount(RobotType.BEAVER, newBeavNum);
			numBeaver=newBeavNum;
		}
		if(newDroneNum!=numDrone){
			radio.updateCount(RobotType.DRONE, newDroneNum);
			numDrone=newDroneNum;
		}
		if(newMinerNum!=numMiner){
			radio.updateCount(RobotType.MINER, newMinerNum);
			numMiner=newMinerNum;
		}
		if(newTankNum!=numTank){
			radio.updateCount(RobotType.TANK, newTankNum);
			numTank=newTankNum;
		}
		if(newCommNum!=numComm){
			radio.updateCount(RobotType.COMMANDER, newCommNum);
			numComm=newCommNum;
		}
		if(newMineFacNum!=numMinerFact){
			radio.updateCount(RobotType.MINERFACTORY, newMineFacNum);
			numMinerFact=newMineFacNum;
		}
		if(newHeliNum!=numHeli){
			radio.updateCount(RobotType.HELIPAD, newHeliNum);
			numHeli=newHeliNum;
		}
		if(newMITNum!=numMIT){
			radio.updateCount(RobotType.TECHNOLOGYINSTITUTE, newMITNum);
			numMIT=newMITNum;
		}
		if(newTrainFNum!=numTrainF){
			radio.updateCount(RobotType.TRAININGFIELD, newTrainFNum);
			numTrainF = newTrainFNum;
		}
		if(newBarNum!=numBar){
			radio.updateCount(RobotType.BARRACKS, newBarNum);
			numBar = newBarNum;
		}
		if(newTankFNum!=numTankF){
			radio.updateCount(RobotType.TANKFACTORY, newTankFNum);
			numTankF = newTankFNum;
		}
	}
	
	public void manageTeams() throws GameActionException{
		// droneTeams = radio.ATTACK_TEAM, radio.HUNT_TEAM, radio.SUPPLY_TEAM
		int minAttackSize = 0, minHuntSize = 0, minSupplySize = 0;
		int attackSize, huntSize, supplySize;
		
		attackSize = radio.getAttendance(RobotType.DRONE, radio.ATTACK_TEAM);
		radio.resetAttendance(RobotType.DRONE, radio.ATTACK_TEAM);
		
		huntSize = radio.getAttendance(RobotType.DRONE, radio.HUNT_TEAM);
		radio.resetAttendance(RobotType.DRONE, radio.HUNT_TEAM);
		
		
		supplySize = radio.getAttendance(RobotType.DRONE, radio.SUPPLY_TEAM);
		radio.resetAttendance(RobotType.DRONE, radio.SUPPLY_TEAM);
		
		//if(numDrone - (attackSize + huntSize + supplySize) >= 5)
		//	minHuntSize = 5;
		if(numDrone - (huntSize + supplySize) > 15)
			minAttackSize = 15;
		else
			radio.setHunterSwitch(radio.HUNT_TEAM);
		
		radio.setWanted(RobotType.DRONE, radio.ATTACK_TEAM, Math.max(minAttackSize - attackSize, 0));
		radio.setWanted(RobotType.DRONE, radio.HUNT_TEAM, Math.max(minHuntSize - huntSize, 0));
		radio.setWanted(RobotType.DRONE, radio.SUPPLY_TEAM, Math.max(minSupplySize - supplySize, 0));
		
		numDrone = numDrone - (huntSize + supplySize);
		rc.setIndicatorString(0, "Number of attack drones:" + attackSize);
		rc.setIndicatorString(1, "Number of hunt drones:" + huntSize);
		//System.out.println("" + attackSize + ", " + huntSize);
	}
	
	public void spawn() throws GameActionException
	{
		int buildPhase = radio.getBuildPhase();
		rc.setIndicatorString(0, Integer.toString(buildPhase));
		int CommNum = radio.readCount(RobotType.COMMANDER);
		int beaverNum = radio.readCount(RobotType.BEAVER);
		if(beaverNum<2 && rc.isCoreReady())
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
		//swarmEnemyTower(RobotType.DRONE);
		rc.setIndicatorString(1, Integer.toString(numTank));
		if(numTank>=25){
			swarmEnemyTower(RobotType.TANK);
		} else if(numTank<=7){
			retreat(RobotType.TANK);
		}	
		//swarmEnemyTower(RobotType.TANK);
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
		if (type.equals(RobotType.DRONE))
			radio.setHunterSwitch(radio.ATTACK_TEAM);
		radio.setSwarm(target, type);
		//radio.setSwarm(radio.getEnemyHQLoc(), type);
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
